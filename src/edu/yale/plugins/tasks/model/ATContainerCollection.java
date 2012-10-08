package edu.yale.plugins.tasks.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: nathan
 * Date: 10/4/12
 * Time: 5:12 AM
 *
 * Class to store information that used when exporting voyager information. It used as a
 * way to speed up exporting of voyager information by caching this information to the backend database
 */
public class ATContainerCollection {
    private ArrayList<ATContainer> containers;
    private String voyagerHoldingsKey = null;
    private HashMap<String, Date> accessionDates;
    private Long resourceId = -1L;
    private Long resourceVersion = -1L;
    private String note = "";

    /**
     * Default constructor
     */
    public ATContainerCollection() {}

    /**
     * The main constructor
     *
     * @param containers
     * @param voyagerHoldingsKey
     * @param accessionDates
     * @param resourceId
     * @param resourceVersion
     */
    public ATContainerCollection(ArrayList<ATContainer> containers, String voyagerHoldingsKey,
                                 HashMap<String, Date> accessionDates, Long resourceId, Long resourceVersion) {
        this.containers = containers;
        this.voyagerHoldingsKey = voyagerHoldingsKey;
        this.accessionDates = accessionDates;
        this.resourceId = resourceId;
        this.resourceVersion = resourceVersion;
    }

    public ArrayList<ATContainer> getContainers() {
        return containers;
    }

    public void setContainers(ArrayList<ATContainer> containers) {
        this.containers = containers;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(Long resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public HashMap<String, Date> getAccessionDates() {
        return accessionDates;
    }

    public void setAccessionDates(HashMap<String, Date> accessionDates) {
        this.accessionDates = accessionDates;
    }

    public String getVoyagerHoldingsKey() {
        return voyagerHoldingsKey;
    }

    public void setVoyagerHoldingsKey(String voyagerHoldingsKey) {
        this.voyagerHoldingsKey = voyagerHoldingsKey;
    }

    /**
     * Method to return the accession date if one was found
     *
     * @param accessionNumber
     * @return
     */
    public String lookupAccessionDate(String accessionNumber) {
        Date accessionDate = accessionDates.get(accessionNumber);
        if (accessionDate == null) {
            return "no accession date";
        } else {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
            return dateFormatter.format(accessionDate);
        }
    }

}
