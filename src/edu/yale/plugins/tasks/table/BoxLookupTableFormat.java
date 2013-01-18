package edu.yale.plugins.tasks.table;

import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.GlazedLists;
import edu.yale.plugins.tasks.model.BoxLookupReturnRecords;

import java.util.Comparator;

/**
 * Archivists' Toolkit(TM) Copyright � 2005-2007 Regents of the University of California, New York University, & Five Colleges, Inc.
 * All rights reserved.
 * <p/>
 * This software is free. You can redistribute it and / or modify it under the terms of the Educational Community License (ECL)
 * version 1.0 (http://www.opensource.org/licenses/ecl1.php)
 * <p/>
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the ECL license for more details about permissions and limitations.
 * <p/>
 * <p/>
 * Archivists' Toolkit(TM)
 * http://www.archiviststoolkit.org
 * info@archiviststoolkit.org
 *
 * @author Lee Mandell, Nathan Stevens
 *         Date: Jun 8, 2006
 *         Time: 9:15:11 PM
 */
public class BoxLookupTableFormat implements WritableTableFormat<BoxLookupReturnRecords>, AdvancedTableFormat<BoxLookupReturnRecords> {
    // The default amount of columns
    private int columnCount = 10;
    private boolean editable = true;

    /**
     * Default constructor does nothing
     */
    public BoxLookupTableFormat() {
    }

    /**
     * Constructor used when using in the main window
     *
     * @param editable, does nothing currently
     */
    public BoxLookupTableFormat(boolean editable) {
        this.editable = false;
        this.columnCount = 9;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public String getColumnName(int column) {
        if (column == 0) return "Collection ID";
        else if (column == 1) return "ACCN/Series";
        else if (column == 2) return "Title";
        else if (column == 3) return "Location";
        else if (column == 4) return "Box";
        else if (column == 5) return "Container Type";
        else if (column == 6) return "Barcode";
        else if (column == 7) return "Voyager Info";
        else if (column == 8) return "Exported";
        else if (column == 9) return "Restriction";

        throw new IllegalStateException();
    }

    public Object getColumnValue(BoxLookupReturnRecords boxLookupReturnRecords, int column) {
        if (column == 0) return boxLookupReturnRecords.getCollectionId();
        else if (column == 1) return boxLookupReturnRecords.getUniqueId();
        else if (column == 2) return boxLookupReturnRecords.getTitle();
        else if (column == 3) return boxLookupReturnRecords.getLocation();
        else if (column == 4) return boxLookupReturnRecords.getBoxLabel();
        else if (column == 5) return boxLookupReturnRecords.getContainerType();
        else if (column == 6) return boxLookupReturnRecords.getBarcode();
        else if (column == 7) return boxLookupReturnRecords.getVoyagerInfo();
        else if (column == 8) return boxLookupReturnRecords.isExportedToVoyager();
        else if (column == 9) return boxLookupReturnRecords.isRestriction();

        throw new IllegalStateException();
    }

    public boolean isEditable(BoxLookupReturnRecords boxLookupReturnRecords, int i) {
        if (!editable) {
            return false;
        } else if (i == 6) {
            return true;
        } else {
            return false;
        }
    }

    public BoxLookupReturnRecords setColumnValue(BoxLookupReturnRecords boxLookupReturnRecords, Object o, int i) {
        return null;
    }

    public Class getColumnClass(int column) {
        if (column == 0) return String.class;
        else if (column == 1) return String.class;
        else if (column == 2) return String.class;
        else if (column == 3) return String.class;
        else if (column == 4) return String.class;
        else if (column == 5) return String.class;
        else if (column == 6) return String.class;
        else if (column == 7) return String.class;
        else if (column == 8) return Boolean.class;
        else if (column == 9) return Boolean.class;

        throw new IllegalStateException();
    }

    public Comparator getColumnComparator(int i) {
        return GlazedLists.comparableComparator();
    }
}