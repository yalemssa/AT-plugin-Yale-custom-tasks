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
 * Date: Oct 16, 2009
 * Time: 1:35:14 PM
 */

package edu.yale.plugins.tasks.utils;

import edu.yale.plugins.tasks.model.BoxLookupReturnRecords;
import edu.yale.plugins.tasks.model.BoxLookupReturnRecordsCollection;
import edu.yale.plugins.tasks.search.BoxLookupReturnScreen;
import org.archiviststoolkit.dialog.ErrorDialog;
import org.archiviststoolkit.model.*;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.util.MyTimer;
import org.archiviststoolkit.util.StringHelper;
import org.archiviststoolkit.hibernate.SessionFactory;
import org.archiviststoolkit.mydomain.DomainAccessObjectFactory;
import org.archiviststoolkit.mydomain.DomainAccessObject;
import org.archiviststoolkit.mydomain.PersistenceException;
import org.archiviststoolkit.mydomain.LookupException;
import org.hibernate.TransientObjectException;

import java.sql.*;
import java.util.*;
import java.text.NumberFormat;

public class BoxLookupAndUpdate {
    private TreeMap<String, SeriesInfo> seriesInfo = new TreeMap<String, SeriesInfo>();

    private HashMap<Long, String> componentTitleLookup = new HashMap<Long, String>();

    // used to cached return screen records
    private HashMap<Long, BoxLookupReturnRecordsCollection> boxCollection = new HashMap<Long, BoxLookupReturnRecordsCollection>();

    private String logMessage = "";

    private Collection<BoxLookupReturnRecords> results = new ArrayList<BoxLookupReturnRecords>();

    private DomainAccessObject locationDAO;

    private DomainAccessObject instanceDAO;

    private Connection con;

    // prepared statements used when searching
    private PreparedStatement resourceIdLookup;
    private PreparedStatement componentLookupByResource;
    private PreparedStatement componentLookupByComponent;
    private PreparedStatement instanceLookupByComponent;

    // keep tract of the number of instances processed
    private int instanceCount;

    public BoxLookupAndUpdate() throws SQLException, ClassNotFoundException {
        Class.forName(SessionFactory.getDriverClass());
        con = DriverManager.getConnection(SessionFactory.getDatabaseUrl(),
                SessionFactory.getUserName(),
                SessionFactory.getPassword());

        // initiate the domain access objects
        try {
            initDAO();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to initialize the set of prepared statement needed
     *
     * @throws Exception
     */
    private void initPreparedStatements() throws SQLException {
        String sqlString = "SELECT Resources.resourceId\n" +
                "FROM Resources\n" +
                "WHERE resourceIdentifier1 = ? and resourceIdentifier2 = ?";
        resourceIdLookup = con.prepareStatement(sqlString);

        sqlString = "SELECT ResourcesComponents.resourceComponentId, \n" +
                "\tResourcesComponents.resourceLevel, \n" +
                "\tResourcesComponents.title, \n" +
                "\tResourcesComponents.hasChild, \n" +
                "\tResourcesComponents.subdivisionIdentifier\n" +
                "FROM ResourcesComponents\n" +
                "WHERE ResourcesComponents.resourceId = ?";
        componentLookupByResource = con.prepareStatement(sqlString);

        sqlString = "SELECT ResourcesComponents.resourceComponentId, \n" +
                "\tResourcesComponents.resourceLevel, \n" +
                "\tResourcesComponents.title, \n" +
                "\tResourcesComponents.hasChild\n" +
                "FROM ResourcesComponents\n" +
                "WHERE ResourcesComponents.parentResourceComponentId = ?";
        componentLookupByComponent = con.prepareStatement(sqlString);


        sqlString = "SELECT *" +
                "FROM ArchDescriptionInstances\n" +
                "WHERE resourceComponentId in (?)";
        instanceLookupByComponent = con.prepareStatement(sqlString);
    }

    /**
     * init the domain access objects for getting instances
     */
    public void initDAO() throws PersistenceException {
        locationDAO = DomainAccessObjectFactory.getInstance().getDomainAccessObject(Locations.class);
        locationDAO.getLongSession();

        instanceDAO = DomainAccessObjectFactory.getInstance().getDomainAccessObject(ArchDescriptionAnalogInstances.class);
        instanceDAO.getLongSession();
    }

    public void doSearch(String msNumber, String ruNumber, String seriesTitle, String accessionNumber, String boxNumber, BoxLookupReturnScreen returnSrceen) {
        seriesInfo = new TreeMap<String, SeriesInfo>();

        componentTitleLookup = new HashMap<Long, String>();

        logMessage = "";

        Collection<BoxLookupReturnRecords> results = new ArrayList<BoxLookupReturnRecords>();

        try {
            // initialize the prepared statements
            initPreparedStatements();
            String sqlString = "";

            System.out.println("doing search");

            String resourceType = null;
            String resourceId2 = null;
            if (msNumber.length() != 0) {
                resourceType = "MS";
                resourceId2 = msNumber;
            } else if (ruNumber.length() != 0) {
                resourceType = "RU";
                resourceId2 = ruNumber;
            }
            resourceIdLookup.setString(1, resourceType);
            resourceIdLookup.setString(2, resourceId2);

            String uniqueId;
            String hashKey;

            String targetUniqueId = "";
            if (seriesTitle.length() != 0) {
                targetUniqueId = seriesTitle;
            } else if (accessionNumber.length() != 0) {
                targetUniqueId = accessionNumber;
            }

            MyTimer timer = new MyTimer();
            timer.reset();
            ResultSet rs = resourceIdLookup.executeQuery();
            if (rs.next()) {
                Long resourceId = rs.getLong(1);
                System.out.println("Resource ID: " + resourceId);
                componentLookupByResource.setLong(1, resourceId);
                ResultSet components = componentLookupByResource.executeQuery();

                while (components.next()) {
                    uniqueId = determineComponentUniqueIdentifier(resourceType, components.getString("subdivisionIdentifier"), components.getString("title"));

                    hashKey = uniqueId + seriesTitle;
                    if (!seriesInfo.containsKey(hashKey)) {
                        seriesInfo.put(hashKey, new SeriesInfo(uniqueId, seriesTitle));
                    }

                    if (targetUniqueId.equals("") || uniqueId.equalsIgnoreCase(targetUniqueId)) {
                        recurseThroughComponents(components.getLong("resourceComponentId"),
                                hashKey,
                                components.getBoolean("hasChild"),
                                componentLookupByComponent,
                                instanceLookupByComponent);
                    }
                }

                ResultSet instances;
                TreeMap<String, ContainerInfo> containers;
                String containerLabel;
                Long componentId;
                String componentTitle;
                Double container1NumIndicator;
                String container1AlphaIndicator;

                for (SeriesInfo series : seriesInfo.values()) {
                    sqlString = "SELECT * " +
                            "FROM ArchDescriptionInstances\n" +
                            "WHERE resourceComponentId in (" + series.getComponentIds() + ")";
                    System.out.println(sqlString);
                    Statement sqlStatement = con.createStatement();
                    instances = sqlStatement.executeQuery(sqlString);
                    containers = new TreeMap<String, ContainerInfo>();
                    while (instances.next()) {

                        container1NumIndicator = instances.getDouble("container1NumericIndicator");
                        container1AlphaIndicator = instances.getString("container1AlphaNumIndicator");
                        if (boxNumber.equals("") || boxNumber.equalsIgnoreCase(determineBoxNumber(container1NumIndicator, container1AlphaIndicator))) {
                            containerLabel = createContainerLabel(instances.getString("container1Type"),
                                    container1NumIndicator,
                                    container1AlphaIndicator,
                                    instances.getString("instanceType"));
                            componentId = instances.getLong("resourceComponentId");
                            componentTitle = componentTitleLookup.get(componentId);
                            if (!containers.containsKey(containerLabel)) {
                                containers.put(containerLabel, new ContainerInfo(containerLabel,
                                        instances.getString("barcode"),
                                        instances.getBoolean("userDefinedBoolean1"),
                                        getLocationString(instances.getLong("locationId")),
                                        componentTitle,
                                        instances.getString("userDefinedString2")));
                            }
                        }
                    }
                    logMessage += "\n\n";
                    for (ContainerInfo container : containers.values()) {
                        results.add(new BoxLookupReturnRecords(createPaddedResourceIdentifier(resourceType, resourceId2),
                                series.getUniqueId(),
                                container.getComponentTitle(),
                                container.getLocation(),
                                container.getBarcode(),
                                container.isRestriction(),
                                container.getLabel(),
                                container.getContainerType()));
                        logMessage += "\nAccession Number: " + series.getUniqueId() +
                                " Sereis Title: " + series.getSeriesTitle() +
                                " Container: " + container.getLabel() +
                                " Barcode: " + container.getBarcode() +
                                " Restrictions: " + container.isRestriction();

                    }
                    returnSrceen.updateResultList(results);
                    returnSrceen.setElapsedTimeText(MyTimer.toString(timer.elapsedTimeMillis()));
                }
            }
        } catch (SQLException e) {
            new ErrorDialog("", e).showDialog();
        } catch (LookupException e) {
            new ErrorDialog("", e).showDialog();
        }
    }

    /**
     * Main entry point into doing a recursion
     *
     * @param record
     * @param monitor
     * @param useCache
     * @return
     */
    public BoxLookupReturnRecordsCollection gatherContainers(Resources record, InfiniteProgressPanel monitor, boolean useCache) {
        TreeMap<String, BoxLookupReturnRecords> containers =
                new TreeMap<String, BoxLookupReturnRecords>();

        // if there is a cache set, use that
        Long resourceId = record.getIdentifier();
        Long resourceVersion = record.getVersion();

        if (useCache && this.boxCollection.containsKey(resourceId)) {
            // now check to see if the version matches
            BoxLookupReturnRecordsCollection boxCollection = this.boxCollection.get(resourceId);
            if(resourceVersion == boxCollection.getResourceVersion()) {
                return this.boxCollection.get(record.getIdentifier());
            } else {
                System.out.println("Resource version different, regenerating box lookup record");
            }
        }

        monitor.setTextLine("Resource: " + record.getTitle(), 2);

        // reset the instance count and begin the timer
        instanceCount = 0;
        MyTimer timer = new MyTimer();
        timer.reset();

        try {
            for (ResourcesComponents component : record.getResourcesComponents()) {
                gatherContainers(monitor, record.getResourceIdentifier(), component, containers, 3);
            }
        } catch (TransientObjectException e) {
            monitor.close();
            new ErrorDialog("Resource must be saved", e).showDialog();
        }

        ArrayList<BoxLookupReturnRecords> boxLookupReturnRecords = new ArrayList<BoxLookupReturnRecords>(containers.values());
        BoxLookupReturnRecordsCollection boxC = new BoxLookupReturnRecordsCollection(boxLookupReturnRecords,resourceId, resourceVersion,
                instanceCount);

        //store a copy of this for future access
        if (useCache) {
            this.boxCollection.put(record.getIdentifier(), boxC);
        }

        // print out the time it took for the search process
        System.out.println("Total Instances: " + instanceCount);
        System.out.println("Total Time: " + MyTimer.toString(timer.elapsedTimeMillis()));

        return boxC;
    }

    /**
     * Method to do recursion through all resource components and create box lookup return
     * records for their instances
     *
     * @param monitor
     * @param resourceIdentifier
     * @param component
     * @param containers
     * @param depth
     * @return The number of instances found
     */
    private void gatherContainers(InfiniteProgressPanel monitor,
                                  String resourceIdentifier, ResourcesComponents component,
                                  TreeMap<String, BoxLookupReturnRecords> containers,
                                  int depth) {

        String containerName;
        ArchDescriptionAnalogInstances newInstance;
        BoxLookupReturnRecords group;
        monitor.setTextLine(component.getTitle(), depth);

        for (Object o : component.getInstances(ArchDescriptionAnalogInstances.class)) {
            newInstance = (ArchDescriptionAnalogInstances) o;
            containerName = newInstance.getTopLevelLabel();

            if (StringHelper.isNotEmpty(newInstance.getBarcode())) {
                containerName = newInstance.getTopLevelLabel() + " (" + newInstance.getBarcode() + ")";
            }

            String uniqueId = determineComponentUniqueIdentifier("", component.getComponentUniqueIdentifier(), component.getTitle());
            group = containers.get(containerName);

            if (group == null) {
                String locationName = "";
                Long locationId;

                if (newInstance.getLocation() != null) {
                    locationName = newInstance.getLocation().toString();
                    locationId = newInstance.getLocation().getLocationId();
                }

                // create and store the BoxLookupReturn Record
                group = new BoxLookupReturnRecords(resourceIdentifier,
                        uniqueId,
                        component.getTitle(),
                        locationName,
                        newInstance.getBarcode(),
                        newInstance.getUserDefinedBoolean1(),
                        newInstance.getTopLevelLabel(),
                        newInstance.getUserDefinedString2());

                group.addInstanceId(newInstance.getIdentifier());
                // need to add location id

                containers.put(containerName, group);
            } else {
                group.addInstanceId(newInstance.getIdentifier());
            }

            instanceCount++;
        }

        if (component.isHasChild()) {
            for (ResourcesComponents childComponent : component.getResourcesComponents()) {
                gatherContainers(monitor, resourceIdentifier, childComponent, containers, depth + 1);
            }
        }
    }

    /**
     * Method to get the indicator for container 1
     *
     * @return Either the Numeric or AlphaNumeric Indicator
     */
    public String determineBoxNumber(Double container1NumericIndicator, String container1AlphaNumericIndicator) {
        if (container1NumericIndicator != null && container1NumericIndicator != 0) {
            return StringHelper.handleDecimal(container1NumericIndicator.toString());
        } else if (container1AlphaNumericIndicator != null && StringHelper.isNotEmpty(container1AlphaNumericIndicator)) {
            return container1AlphaNumericIndicator;
        } else {
            return "";
        }
    }

    private String createPaddedResourceIdentifier(String id1, String id2) {
        String paddedId2;
        if (id2.length() == 1) {
            paddedId2 = "000" + id2;
        } else if (id2.length() == 2) {
            paddedId2 = "00" + id2;
        } else if (id2.length() == 3) {
            paddedId2 = "0" + id2;
        } else {
            paddedId2 = id2;
        }
        return id1 + paddedId2;
    }


    private String determineComponentUniqueIdentifier(String resourceType, String componenetUniqueId, String seriesTitle) {
        if (componenetUniqueId != null && componenetUniqueId.length() != 0) {
            return componenetUniqueId.replace("Accession ", "");
        } else if (resourceType.equalsIgnoreCase("ms")) {
            return seriesTitle.replace("Accession ", "");
        } else {
            return "";
        }
    }

    private String getLocationString(Long locationId) throws LookupException {
        Locations location = (Locations) locationDAO.findByPrimaryKey(locationId);
        if (location != null) {
            return location.toString();
        } else {
            return "";
        }
    }

    private void recurseThroughComponents(Long componentID,
                                          String hashKey,
                                          Boolean hasChild,
                                          PreparedStatement componentLookup,
                                          PreparedStatement instanceLookup) throws SQLException {
//		System.out.println("Component ID: " + componentID + " Level: " + level + " Title: " + title + " Has child: " + hasChild);
        if (hasChild) {
            componentLookup.setLong(1, componentID);
            ResultSet components = componentLookup.executeQuery();
            ArrayList<ComponentInfo> componentList = new ArrayList<ComponentInfo>();
            while (components.next()) {
                componentList.add(new ComponentInfo(components.getLong("resourceComponentId"),
                        components.getString("resourceLevel"),
                        components.getString("title"),
                        components.getBoolean("hasChild")));
                componentTitleLookup.put(components.getLong("resourceComponentId"), components.getString("title"));
            }

            if (componentList.size() > 0) {
                for (ComponentInfo component : componentList) {
                    recurseThroughComponents(component.getComponentId(), hashKey, component.isHasChild(), componentLookup, instanceLookup);
                }
            } else {
                //this is a hack because the has child flag for components may be set wrong
                SeriesInfo series = seriesInfo.get(hashKey);
                series.addComponentId(componentID);
            }
        } else {

            SeriesInfo series = seriesInfo.get(hashKey);
            series.addComponentId(componentID);

        }
    }

    private String createContainerLabel(String type, Double numericIndicator, String alphaIndicator, String instanceType) {

        Boolean hasNumbericIndicator;
        if (instanceType.equalsIgnoreCase("Digital Object")) {
            return "Digital Object(s)";
        } else {
            if (numericIndicator != null && numericIndicator != 0.0) {
                hasNumbericIndicator = true;
            } else {
                hasNumbericIndicator = false;
            }
            if (type != null && type.length() > 0 && (hasNumbericIndicator || alphaIndicator.length() > 0)) {
                try {
                    if (hasNumbericIndicator) {
                        return type + " " + NumberFormat.getInstance().format(numericIndicator);
                    } else {
                        return type + " " + alphaIndicator;
                    }
                } catch (Exception e) {
                    new ErrorDialog("Error creating container label", e).showDialog();
                    return "no container";
                }
            } else {
                return "no container";
            }
        }
    }

    /**
     * Method to update instances of a box record
     *
     * @param instanceIds
     * @param barcode
     * @param container3Type
     * @param userDefinedString2
     * @param changeRestriction
     * @param restriction
     * @param changeExportedToVoyager
     * @param exportedToVoyager
     * @return String containing the top level container name
     * @throws Exception
     */
    public String updateInstanceInformation(String instanceIds, String barcode, String container3Type,
                                          String userDefinedString2, Boolean changeRestriction, Boolean restriction,
                                          Boolean changeExportedToVoyager, Boolean exportedToVoyager) throws Exception {

        // for each id get the analog instance object from the database
        String[] ids = instanceIds.split(",\\s*");
        String topLevelContainerName = null;

        for (String id : ids) {
            Long lid = new Long(id);

            ArchDescriptionAnalogInstances instance = (ArchDescriptionAnalogInstances) instanceDAO.findByPrimaryKeyLongSession(lid);

            // set the top level container name if we have not done so already
            if(topLevelContainerName == null) {
                topLevelContainerName = instance.getTopLevelLabel();
            }

            if (barcode.length() != 0) {
                instance.setBarcode(barcode);
                topLevelContainerName += " (" + barcode + ")";
            }

            if (container3Type.length() != 0) {
                instance.setContainer3Type(container3Type);
            }

            if (userDefinedString2.length() != 0) {
                instance.setUserDefinedString2(userDefinedString2);
            }

            if (changeRestriction) {
                instance.setUserDefinedBoolean1(restriction);
            }

            if (changeExportedToVoyager) {
                instance.setUserDefinedBoolean2(exportedToVoyager);
            }

            // update the record in the database now
            instanceDAO.updateLongSession(instance, false);
        }

        return topLevelContainerName;
    }

    /**
     * Method to update the code for the list of instances
     *
     * @param instanceIds
     * @param barcode
     */
    public void updateBarcode(String instanceIds, String barcode) throws Exception {
        String[] ids = instanceIds.split(",\\s*");

        // for each id get the analog instance object from the database
        for (String id : ids) {
            Long lid = new Long(id);

            ArchDescriptionAnalogInstances instance = (ArchDescriptionAnalogInstances) instanceDAO.findByPrimaryKeyLongSession(lid);

            instance.setBarcode(barcode);

            instanceDAO.updateLongSession(instance, false);
        }
    }

    /**
     * Method to update the voyager information for container records/instances
     *
     * @param instanceIds
     * @param bibHolding
     * @return the number of instances returned
     * @throws Exception
     */
    public int updateVoyagerInformation(String instanceIds, String bibHolding) throws Exception {
        String[] ids = instanceIds.split(",\\s*");

        // for each id get the analog instance object from the database
        int count = 0;

        for (String id : ids) {
            Long lid = new Long(id);

            ArchDescriptionAnalogInstances instance = (ArchDescriptionAnalogInstances) instanceDAO.findByPrimaryKeyLongSession(lid);

            instance.setUserDefinedString1(bibHolding);

            instanceDAO.updateLongSession(instance, false);

            count++;
        }

        return count;
    }


    /**
     * Method add a location to a set of analog instance
     *
     * @param instanceIds
     * @param selectedLocation
     * @return
     * @throws PersistenceException
     */
    public int updateInstanceLocation(String instanceIds, Locations selectedLocation) throws Exception {
        String[] ids = instanceIds.split(",\\s*");

        // for each id get the analog instance object from the database
        int count = 0;

        for (String id : ids) {
            Long lid = new Long(id);

            ArchDescriptionAnalogInstances instance = (ArchDescriptionAnalogInstances) instanceDAO.findByPrimaryKeyLongSession(lid);

            instance.setLocation(selectedLocation);

            instanceDAO.updateLongSession(instance, false);

            count++;
        }

        return count;
    }

    private class ContainerInfo {

        private String label;
        private String barcode;
        private Boolean restriction;
        private String location;
        private String componentTitle;
        private String containerType;

        private ContainerInfo(String label, String barcode, Boolean restriction, String location, String componentTitle, String containerType) {
            this.label = label;
            if (barcode == null || barcode.equals("0.0")) {
                this.barcode = "";
            } else {
                this.barcode = barcode;
            }

            this.restriction = restriction;
            this.location = location;
            this.componentTitle = componentTitle;
            this.containerType = containerType;
        }

        public String getLabel() {
            return label;
        }

        public String getBarcode() {
            return barcode;
        }

        public Boolean isRestriction() {
            return restriction;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getComponentTitle() {
            return componentTitle;
        }

        public void setComponentTitle(String componentTitle) {
            this.componentTitle = componentTitle;
        }

        public String getContainerType() {
            return containerType;
        }

        public void setContainerType(String containerType) {
            this.containerType = containerType;
        }
    }

    private class SeriesInfo {

        private String uniqueId;
        private String seriesTitle;
        private String componentIds = null;

        private SeriesInfo(String uniqueId, String seriesTitle) {
            this.uniqueId = uniqueId;
            this.seriesTitle = seriesTitle;
        }


        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public String getSeriesTitle() {
            return seriesTitle;
        }

        public void setSeriesTitle(String seriesTitle) {
            this.seriesTitle = seriesTitle;
        }

        public String getComponentIds() {
            return componentIds;
        }

        public void addComponentId(Long componentId) {
            if (this.componentIds == null) {
                this.componentIds = componentId.toString();
            } else {
                this.componentIds += ", " + componentId;
            }
        }
    }

    private class ComponentInfo {

        private Long componentId;
        private String resourceLevel;
        private String title;
        private Boolean hasChild;

        private ComponentInfo(Long componentId, String resourceLevel, String title, Boolean hasChild) {
            this.componentId = componentId;
            this.resourceLevel = resourceLevel;
            this.title = title;
            this.hasChild = hasChild;
        }

        public Long getComponentId() {
            return componentId;
        }

        public void setComponentId(Long componentId) {
            this.componentId = componentId;
        }

        public String getResourceLevel() {
            return resourceLevel;
        }

        public void setResourceLevel(String resourceLevel) {
            this.resourceLevel = resourceLevel;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Boolean isHasChild() {
            return hasChild;
        }

        public void setHasChild(Boolean hasChild) {
            this.hasChild = hasChild;
        }
    }

}
