package edu.yale.plugins.tasks.table;

import ca.odell.glazedlists.TextFilterator;
import edu.yale.plugins.tasks.model.BoxLookupReturnRecords;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nathan
 * Date: 9/25/12
 * Time: 7:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class BoxReturnRecordsFilterator implements TextFilterator {
    /**
     * Method to return the list of string
     *
     * @param list
     * @param record
     */
    public void getFilterStrings(List list, Object record) {
        BoxLookupReturnRecords boxLookupReturnRecord = (BoxLookupReturnRecords) record;

        list.add(boxLookupReturnRecord.getUniqueId());
        list.add(boxLookupReturnRecord.getTitle());
        list.add(boxLookupReturnRecord.getLocation());
        list.add(boxLookupReturnRecord.getBoxLabel());
        list.add(boxLookupReturnRecord.getContainerType());
        list.add(boxLookupReturnRecord.getBarcode());
    }
}
