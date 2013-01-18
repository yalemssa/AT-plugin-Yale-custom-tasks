/**
 * Archivists' Toolkit(TM) Copyright � 2005-2007 Regents of the University of California, New York University, & Five Colleges, Inc.  
 * All rights reserved. 
 *
 * This software is free. You can redistribute it and / or modify it under the terms of the Educational Community License (ECL) 
 * version 1.0 (http://www.opensource.org/licenses/ecl1.php) 
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the ECL license for more details about permissions and limitations. 
 *
 *
 * Archivists' Toolkit(TM) 
 * http://www.archiviststoolkit.org 
 * info@archiviststoolkit.org 
 *
 * @author Lee Mandell
 * Date: Sep 15, 2009
 * Time: 10:44:54 AM
 */

package edu.yale.plugins.tasks.utils;

import edu.yale.plugins.tasks.model.ATContainer;
import edu.yale.plugins.tasks.model.ATContainerCollection;
import org.archiviststoolkit.model.*;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.mydomain.DomainAccessObject;
import org.archiviststoolkit.mydomain.DomainAccessObjectFactory;
import org.archiviststoolkit.mydomain.PersistenceException;
import org.archiviststoolkit.mydomain.LookupException;
import org.archiviststoolkit.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;

public class ContainerGatherer {
    // this is used to force update of all record even if they are the same
    public boolean alwaysSaveCache = false;

	private Resources resource;
    private boolean useCache;
	private String voyagerHoldingsKey = null;
	private HashMap<String, Date> accessionDates;
	private DomainAccessObject accessionDAO;
    private DomainAccessObject instanceDAO;

	public ContainerGatherer(Resources resource, boolean useCache, boolean alwaysSaveCache) {
		this.resource = resource;
        this.useCache = useCache;
        this.alwaysSaveCache = alwaysSaveCache;

        // initiate the domain access objects
        try {
            initDAO();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
	}

    /**
     * init the domain access objects for getting instances
     */
    public void initDAO() throws PersistenceException {
        instanceDAO = DomainAccessObjectFactory.getInstance().getDomainAccessObject(ArchDescriptionAnalogInstances.class);
        instanceDAO.getLongSession();
    }

	public ATContainerCollection gatherContainers(InfiniteProgressPanel monitor) throws PersistenceException, LookupException {
        // if there is a cache set, use that
        Long resourceId = resource.getIdentifier();
        Long resourceVersion = resource.getVersion();

        // try loading the container record from database
        ATContainerCollection containerCollection = loadATContainerRecordFromDatabase(resourceId);

        // see if to just return the cache result
        if (useCache && containerCollection != null) {
            // now check to see if the version matches
            if(resourceVersion.equals(containerCollection.getResourceVersion())) {
                return containerCollection;
            } else {
                System.out.println("Resource version different, regenerating box lookup collection");
            }
        }

		HashMap<String, ATContainer> containers = new HashMap<String, ATContainer>();
		accessionDates = new HashMap<String, Date>();
		String accessionNumber;
		accessionDAO = DomainAccessObjectFactory.getInstance().getDomainAccessObject(Accessions.class);

		for (ResourcesComponents component: resource.getResourcesComponents()) {
			accessionNumber = component.getComponentUniqueIdentifier().replace("Accession ", "");
			if (!accessionNumber.isEmpty() && !accessionDates.containsKey(accessionNumber)) {
                Date date = lookupAccessionDate(accessionNumber);
				if(date != null) {
                    accessionDates.put(accessionNumber, date);
                }
			}
			gatherContainers(monitor, containers, component, accessionNumber, 2);
		}

        // Array list that hold container
        ArrayList<ATContainer> containerList = new ArrayList<ATContainer>(containers.values());

        // crate and return the AT Container collection
        containerCollection = new ATContainerCollection(containerList,
                voyagerHoldingsKey,
                accessionDates,
                resource.getIdentifier(), resource.getVersion());

        // if we using cache then store this to the database
        if(useCache || alwaysSaveCache) {
            try {
                containerCollection.setResourceVersion(resourceVersion);
                PluginDataUtils.saveATContainerRecord(containerCollection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		return containerCollection;
	}

    /**
     * Find all instances that belong to this container by making recurive calls
     *
     * @param monitor
     * @param containers
     * @param component
     * @param accessionNumber
     * @param depth
     */
	private void gatherContainers(InfiniteProgressPanel monitor,
								  HashMap<String, ATContainer> containers,
								  ResourcesComponents component,
								  String accessionNumber,
								  int depth) {

		String key;
		ArchDescriptionAnalogInstances instance;
		String instanceLabel;

		if(monitor != null) monitor.setTextLine(component.getTitle(), depth);

        System.out.println("Component: " + component.getTitle() + " has child: " + component.isHasChild());
 
		for (Object o : component.getInstances(ArchDescriptionAnalogInstances.class)) {
			instance = (ArchDescriptionAnalogInstances) o;
			instanceLabel = instance.getTopLevelLabel();
			key = accessionNumber + instanceLabel;
			System.out.println("Key: " + key);

            ATContainer atContainer = null;

            if (!containers.containsKey(key)) {
                atContainer = new ATContainer(instanceLabel, accessionNumber, instance.getBarcode());
				containers.put(key, atContainer);

				if (voyagerHoldingsKey == null && instance.getUserDefinedString1() != null) {
					String[] parts = instance.getUserDefinedString1().split("_");
                    if(parts.length == 2) {
					    voyagerHoldingsKey = parts[1];
                    }
				}
			} else {
                atContainer = containers.get(key);
            }

            // store the instance id
            atContainer.addInstanceId(instance.getIdentifier());
		}

		if (component.isHasChild()) {
			for (ResourcesComponents childComponent: component.getResourcesComponents()) {
				gatherContainers(monitor, containers, childComponent, accessionNumber, depth++);
			}
		}
	}

	private Date lookupAccessionDate(String accessionNumber) {

		String fixedAccessionNumber = accessionNumber.replace("-", ".");
        System.out.println("Fix Access Number:" + fixedAccessionNumber);



		String[] accessionParts = fixedAccessionNumber.split("\\.");

        // check length before we print this out to prevent index out of bounds exception
        if(accessionParts.length >= 3) {
            System.out.println(accessionParts[0] + " " + accessionParts[1] + " " + accessionParts[2]);
        }

        String accessionNumber1, accessionNumber2, accessionNumber3, accessionNumber4;

        if (accessionParts.length == 4) {
			System.out.println("4 parts");
			accessionNumber1 = accessionParts[0];
			accessionNumber2 = accessionParts[1];
			accessionNumber3 = accessionParts[2];
			accessionNumber4 = accessionParts[3];
		} else if (accessionParts.length == 3) {
			System.out.println("3 parts");
			accessionNumber1 = accessionParts[0];
			accessionNumber2 = accessionParts[1];
			accessionNumber3 = accessionParts[2];
			accessionNumber4 = "";
		} else if (accessionParts.length == 2) {
			System.out.println("2 parts");
			accessionNumber1 = accessionParts[0];
			accessionNumber2 = accessionParts[1];
			accessionNumber3 = "";
			accessionNumber4 = "";
		} else if (accessionParts.length == 1) {
			System.out.println("1 parts");
			accessionNumber1 = accessionParts[0];
			accessionNumber2 = "";
			accessionNumber3 = "";
			accessionNumber4 = "";
		} else  {
			System.out.println("0 parts");
			accessionNumber1 = "";
			accessionNumber2 = "";
			accessionNumber3 = "";
			accessionNumber4 = "";
		}

		Session session = SessionFactory.getInstance().openSession();
		Criteria criteria = session.createCriteria(Accessions.class);
		System.out.println(criteria.toString());

        if (accessionNumber1.length() > 0) {
			criteria.add(Expression.eq(Accessions.PROPERTYNAME_ACCESSION_NUMBER_1, accessionNumber1));
		}
		if (accessionNumber2.length() > 0) {
			criteria.add(Expression.eq(Accessions.PROPERTYNAME_ACCESSION_NUMBER_2, accessionNumber2));
		}
		if (accessionNumber3.length() > 0) {
			criteria.add(Expression.eq(Accessions.PROPERTYNAME_ACCESSION_NUMBER_3, accessionNumber3));
		}
		if (accessionNumber4.length() > 0) {
			criteria.add(Expression.eq(Accessions.PROPERTYNAME_ACCESSION_NUMBER_4, accessionNumber4));
		}
		Accessions accession = (Accessions) criteria.uniqueResult();
		session.close();

        if(accession != null) {
		    return accession.getAccessionDate();
        } else {
            return null;
        }
	}

    private ATContainerCollection loadATContainerRecordFromDatabase(Long resourceId) {
        if(!alwaysSaveCache) {
            return PluginDataUtils.getATContainerRecord(resourceId);
        } else {
            return null;
        }
    }

    /**
     * Method to update the code for the list of instances
     *
     * @param container whose instances need to be updated
     * @param exported Whether the container was exported
     */
    public void updateExportedToVoyager(ATContainer container, boolean exported) throws Exception {
        String[] ids = container.getInstanceIds().split(",\\s*");

        // for each id get the analog instance object from the database and set the export
        // to voyager to be true
        for (String id : ids) {
            Long lid = new Long(id);

            ArchDescriptionAnalogInstances instance = (ArchDescriptionAnalogInstances) instanceDAO.findByPrimaryKeyLongSession(lid);

            instance.setUserDefinedBoolean2(exported);

            instanceDAO.updateLongSession(instance, false);
        }
    }

    /**
     * Check to see if this has been exported by just checking the first container, since the
     * rest of the instances in the container should be the same
     *
     * @param container
     * @return
     */
    public boolean isExportedToVoyager(ATContainer container) {
        try {
            String[] ids = container.getInstanceIds().split(",\\s*");
            Long lid = new Long(ids[0]);

            ArchDescriptionAnalogInstances instance = (ArchDescriptionAnalogInstances) instanceDAO.findByPrimaryKeyLongSession(lid);
            Boolean export = instance.getUserDefinedBoolean2();
            if(export != null) {
                return export;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
