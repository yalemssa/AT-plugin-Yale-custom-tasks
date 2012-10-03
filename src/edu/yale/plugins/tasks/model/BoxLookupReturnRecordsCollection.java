package edu.yale.plugins.tasks.model;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: nathan
 * Date: 9/29/12
 * Time: 12:58 PM
 *
 * Simple class that holds the collections of box lookup return records, as well as additional
 * information such as total containers and resource version, and id.
 *
 */
public class BoxLookupReturnRecordsCollection {
    private Collection<BoxLookupReturnRecords> containers;
    private Long resourceId = -1L;
    private Long resourceVersion = -1L;
    private int totalInstances = -1;
    private String note = "";

    /**
     * Default constructor
     */
    public BoxLookupReturnRecordsCollection() { }

    /**
     * Default constructor
     *
     * @param containers
     * @param resourceId
     * @param resourceVersion
     * @param totalInstances
     */
    public BoxLookupReturnRecordsCollection(Collection<BoxLookupReturnRecords> containers, Long resourceId, Long resourceVersion, int totalInstances) {
        this.containers = containers;
        this.resourceId = resourceId;
        this.resourceVersion = resourceVersion;
        this.totalInstances = totalInstances;
    }

    public Collection<BoxLookupReturnRecords> getContainers() {
        return containers;
    }

    public void setContainers(Collection<BoxLookupReturnRecords> containers) {
        this.containers = containers;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(Long resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public int getTotalInstances() {
        return totalInstances;
    }

    public void setTotalInstances(int totalInstances) {
        this.totalInstances = totalInstances;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
