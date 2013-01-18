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
 * Date: Nov 3, 2009
 * Time: 3:46:12 PM
 */

package edu.yale.plugins.tasks.model;

public class BoxLookupReturnRecords implements Comparable{

	private String collectionId;
	private String uniqueId;
	private String title;
	private String location;
	private String barcode;
	private Boolean restriction;
	private String boxLabel;
	private String containerType;
    private String instanceIds;
    private String voyagerInfo;
    private Boolean exportedToVoyager;

    private String topLevelContainerName; // used for sorting

    private Long locationId;

    public BoxLookupReturnRecords() {}

    public BoxLookupReturnRecords(String collectionId,
								  String uniqueId,
								  String title,
								  String location,
								  String barcode,
								  Boolean restriction,
								  String boxLabel,
								  String containerType) {
		this.collectionId = collectionId;
		this.uniqueId = uniqueId;
		this.title = title;
		this.location = location;
		this.barcode = barcode;
		this.restriction = restriction;
		this.boxLabel = boxLabel;
		this.containerType = containerType;

        // set the top level container name
        topLevelContainerName = boxLabel + " (" + barcode +") ";
	}

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Boolean isRestriction() {
		return restriction;
	}

	public void setRestriction(Boolean restriction) {
		this.restriction = restriction;
	}

	public int compareTo(Object o) {
		return this.toString().compareTo(((BoxLookupReturnRecords)o).toString());
	}

	public String getBoxLabel() {
		return boxLabel;
	}

	public void setBoxLabel(String boxLabel) {
		this.boxLabel = boxLabel;
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

    public String getVoyagerInfo() {
        return voyagerInfo;
    }

    public void setVoyagerInfo(String voyagerInfo) {
        this.voyagerInfo = voyagerInfo;
    }

    public Boolean isExportedToVoyager() {
        return exportedToVoyager;
    }

    public void setExportedToVoyager(Boolean exportedToVoyager) {
        this.exportedToVoyager = exportedToVoyager;
    }

    public String getInstanceIds() {
        return instanceIds;
    }

    public void addInstanceId(Long instanceId) {
        if (this.instanceIds == null) {
            this.instanceIds = instanceId.toString();
        } else {
            this.instanceIds += ", " + instanceId;
        }
    }

    /**
     * Get the top level container name
     *
     * @return
     */
    public String getTopLevelContainerName() {
        return topLevelContainerName;
    }

    /**
     * Set the top level container name used for sorting
     * @param topLevelContainerName
     */
    public void setTopLevelContainerName(String topLevelContainerName) {
        this.topLevelContainerName = topLevelContainerName;
    }

    /**
     * Custom to string method
     *
     * @return
     */
    public String toString() {
        return topLevelContainerName;
    }
}
