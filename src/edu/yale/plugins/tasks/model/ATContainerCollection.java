package edu.yale.plugins.tasks.model;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: nathan
 * Date: 10/4/12
 * Time: 5:12 AM
 *
 * Class to store information that used when exporting voyager information. It used as a way to speed up
 * exporting of voyager information
 */
public class ATContainerCollection {
    private Collection<ATContainer> containers;
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
