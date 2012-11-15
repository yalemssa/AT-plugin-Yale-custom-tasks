package edu.yale.plugins.tasks;

import edu.yale.plugins.tasks.model.ATContainer;
import edu.yale.plugins.tasks.model.ATContainerCollection;
import edu.yale.plugins.tasks.model.BoxLookupReturnRecordsCollection;
import edu.yale.plugins.tasks.search.BoxLookupReturnScreen;
import edu.yale.plugins.tasks.utils.BoxLookupAndUpdate;
import edu.yale.plugins.tasks.utils.ContainerGatherer;
import org.archiviststoolkit.ApplicationFrame;
import org.archiviststoolkit.dialog.ATFileChooser;
import org.archiviststoolkit.dialog.ErrorDialog;
import org.archiviststoolkit.editor.ArchDescriptionFields;
import org.archiviststoolkit.exceptions.UnsupportedDatabaseType;
import org.archiviststoolkit.hibernate.SessionFactory;
import org.archiviststoolkit.model.ArchDescriptionAnalogInstances;
import org.archiviststoolkit.model.Resources;
import org.archiviststoolkit.model.ResourcesCommon;
import org.archiviststoolkit.mydomain.*;
import org.archiviststoolkit.plugin.ATPlugin;
import org.archiviststoolkit.structure.ATFieldInfo;
import org.archiviststoolkit.structure.DefaultValues;
import org.archiviststoolkit.swing.ATProgressUtil;
import org.archiviststoolkit.swing.InfiniteProgressPanel;
import org.archiviststoolkit.util.*;
import org.java.plugin.Plugin;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
 * <p/>
 * A simple plugin to allow more efficient inputting of yale data
 * <p/>
 * Created by IntelliJ IDEA.
 *
 * @author: Lee Mandell and Nathan Stevens
 * Date: Feb 10, 2009
 * Time: 1:07:45 PM
 */

public class YalePluginTasks extends Plugin implements ATPlugin {
    public static final String APPLY_CONTAINER_INFORMATION_TASK = "Assign Container Information";
    public static final String EXPORT_VOYAGER_INFORMATION = "Export Voyager Information";
    public static final String PARTIAL_EAD_IMPORT = "Partial EAD Import";
    public static final String BOX_LOOKUP = "Box Lookup";
    public static final String INDEX_RECORDS = "Index Records";

    public static final String PLUGIN_NAME = "Yale Tasks";

    protected ApplicationFrame mainFrame;
    protected ArchDescriptionFields parentEditorFields;
    private ResourcesCommon resourceOrComponent;

    protected YaleAnalogInstancesFields editorFields;
    private DomainEditor analogInstanceEditor;
    private JTable callingTable;
    private int selectedRow;
    protected ArchDescriptionAnalogInstances analogInstance;

    // class finding and storing container information
    BoxLookupAndUpdate boxLookupAndUpdate = null;

    public static final String BOX_RECORD_DATA_NAME = "box_record";
    public static final String AT_CONTAINER_DATA_NAME = "container_record";
    public static final String CONFIG_DATA_NAME = "config_record";

    // boolean used to specify that caching should be used
    private boolean useCache = true;

    // the config dialog use for configuring the application
    private YalePluginTasksConfigDialog configDialog;

    // the default constructor
    public YalePluginTasks() {
    }

    // get the category this plugin belongs to
    public String getCategory() {
        return ATPlugin.DEFAULT_CATEGORY;
    }

    // get the name of this plugin
    public String getName() {
        return PLUGIN_NAME;
    }

    // Method to set the main frame
    public void setApplicationFrame(ApplicationFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // Method that display the window
    public void showPlugin() {
    }

    // method to display a plugin that needs a parent frame
    public void showPlugin(Frame owner) {
    }

    // method to display a plugin that needs a parent dialog
    public void showPlugin(Dialog owner) {
    }

    // Method to return the jpanels for plugins that are in an AT editor
    public HashMap getEmbeddedPanels() {
        return null;
    }

    public HashMap getRapidDataEntryPlugins() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setEditorField(DomainEditorFields domainEditorFields) {
    }

    // Method to set the editor field component
    public void setEditorField(ArchDescriptionFields editorField) {
    }

    /**
     * Method to set the domain object for this plugin
     */
    public void setModel(DomainObject domainObject, InfiniteProgressPanel monitor) {
    }

    /**
     * Method to get the table from which the record was selected
     *
     * @param callingTable The table containing the record
     */
    public void setCallingTable(JTable callingTable) {
    }

    /**
     * Method to set the selected row of the calling table
     *
     * @param selectedRow
     */
    public void setSelectedRow(int selectedRow) {
    }

    /**
     * Method to set the current record number along with the total number of records
     *
     * @param recordNumber The current record number
     * @param totalRecords The total number of records
     */
    public void setRecordPositionText(int recordNumber, int totalRecords) {
    }

    // Method to do a specific task in the plugin
    public void doTask(String task) {
        DomainTableWorkSurface workSurface = mainFrame.getWorkSurfaceContainer().getCurrentWorkSurface();

        final DomainSortableTable worksurfaceTable = (DomainSortableTable) workSurface.getTable();

        final ResourcesDAO access = new ResourcesDAO();

        if (task.equals(APPLY_CONTAINER_INFORMATION_TASK)) {
            if (workSurface.getClazz() != Resources.class) {
                JOptionPane.showMessageDialog(mainFrame, "This function only works for the resources module");
            } else if (worksurfaceTable.getSelectedRowCount() != 1) {
                JOptionPane.showMessageDialog(mainFrame, "You must select one resource record");
            } else {
                Thread performer = new Thread(new Runnable() {
                    public void run() {
                        // make sure we have the class that looks up records
                        if (boxLookupAndUpdate == null) {
                            try {
                                boxLookupAndUpdate = new BoxLookupAndUpdate();
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(mainFrame, "Unable to connect to database");
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            System.out.println("BoxLookupAndUpdate Already Created ...");
                        }

                        InfiniteProgressPanel monitor = ATProgressUtil.createModalProgressMonitor(mainFrame, 1000, true);
                        monitor.start("Gathering Containers...");

                        Resources resource = (Resources) worksurfaceTable.getFilteredList().get(worksurfaceTable.getSelectedRow());

                        try {
                            resource = (Resources) access.findByPrimaryKeyLongSession(resource.getIdentifier());
                        } catch (LookupException e) {
                            JOptionPane.showMessageDialog(mainFrame, "Unable to load resource record");
                            e.printStackTrace();
                            return;
                        }

                        final BoxLookupReturnRecordsCollection boxes = boxLookupAndUpdate.gatherContainersBySeries(resource, monitor, useCache);

                        // close the monitor
                        monitor.close();

                        // display the dialog in the EDT thread
                        Runnable doWorkRunnable = new Runnable() {
                            public void run() {
                                YaleLocationAssignmentResources locationAssignmentDialog = new YaleLocationAssignmentResources(mainFrame);
                                locationAssignmentDialog.setSize(900, 700);
                                locationAssignmentDialog.assignContainerListValues(boxes);
                                locationAssignmentDialog.setBoxLookupAndUpdate(boxLookupAndUpdate);
                                locationAssignmentDialog.setVisible(true);
                            }
                        };
                        SwingUtilities.invokeLater(doWorkRunnable);
                    }
                }, "Gather containers");
                performer.start();
            }

        } else if (task.equals(EXPORT_VOYAGER_INFORMATION)) {
            if (workSurface.getClazz() != Resources.class) {
                JOptionPane.showMessageDialog(mainFrame, "This function only works for the resources module");
            } else if (worksurfaceTable.getSelectedRowCount() == 0) {
                JOptionPane.showMessageDialog(mainFrame, "You must select at least one resource record");
            } else {
                ATFileChooser filechooser = new ATFileChooser();

                if (filechooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                    final File outputFile = filechooser.getSelectedFile();

                    Thread performer = new Thread(new Runnable() {
                        public void run() {
                            InfiniteProgressPanel monitor = ATProgressUtil.createModalProgressMonitor(ApplicationFrame.getInstance(), 1000);
                            monitor.start("Exporting Voyager Information...");

                            long resourceId;
                            Resources selectedResource, resource;

                            PrintWriter writer = null;

                            ContainerGatherer gatherer;

                            String accessionNumber;

                            // start the timer object
                            MyTimer timer = new MyTimer();
                            timer.reset();

                            try {
                                writer = new PrintWriter(outputFile);
                                int totalRecords = worksurfaceTable.getSelectedObjects().size();

                                for (int i : worksurfaceTable.getSelectedRows()) {
                                    selectedResource = (Resources) worksurfaceTable.getFilteredList().get(i);
                                    resourceId = selectedResource.getResourceId();
                                    resource = (Resources) access.findByPrimaryKeyLongSession(resourceId);

                                    monitor.setTextLine("Exporting resource " + i + " of " + totalRecords + " - " + resource.getTitle(), 1);

                                    gatherer = new ContainerGatherer(resource, useCache);
                                    ATContainerCollection containerCollection = gatherer.gatherContainers(monitor);

                                    for (ATContainer container : containerCollection.getContainers()) {
                                        accessionNumber = container.getAccessionNumber();
                                        writer.println(resource.getResourceIdentifier2() + "," +
                                                containerCollection.getVoyagerHoldingsKey() + "," +
                                                accessionNumber + "," +
                                                containerCollection.lookupAccessionDate(accessionNumber) + "," +
                                                container.getContainerLabel() + "," +
                                                "," + //just a dummy for box number extension
                                                container.getBarcode() + ",");
                                    }

                                    // close the long session, otherwise memory would quickly run out
                                    access.closeLongSession();
                                    access.getLongSession();
                                }

                                writer.flush();

                                System.out.println("Total Time to export: " + MyTimer.toString(timer.elapsedTimeMillis()));
                            } catch (LookupException e) {
                                monitor.close();
                                new ErrorDialog("Error loading resource", e).showDialog();
                            } catch (FileNotFoundException e) {
                                monitor.close();
                                new ErrorDialog("Error creating file writer", e).showDialog();
                            } catch (PersistenceException e) {
                                new ErrorDialog("Error looking up accession date", e).showDialog();
                            } catch (SQLException e) {
                                new ErrorDialog("Error resetting the long session", e).showDialog();
                            } finally {
                                writer.close();
                                monitor.close();
                            }
                        }
                    }, "Exporting Voyager Information");
                    performer.start();
                }
            }
        } else if (task.equals(BOX_LOOKUP)) {
            try {
                BoxLookupReturnScreen returnScreen = new BoxLookupReturnScreen(mainFrame);
                returnScreen.showDialog();
            } catch (ClassNotFoundException e) {
                new ErrorDialog("", e).showDialog();
            } catch (SQLException e) {
                new ErrorDialog("", e).showDialog();
            }
        } else if (task.equals(INDEX_RECORDS)) {
            if (mainFrame.getCurrentUserAccessClass() == 5) {
                indexRecords(mainFrame, true);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "This function only works for level 5 users");
            }
        }
    }

    public boolean doTask(String s, String[] strings) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    // Method to get the list of specific task the plugin can perform
    public String[] getTaskList() {
        String[] tasks = new String[]{APPLY_CONTAINER_INFORMATION_TASK,
                EXPORT_VOYAGER_INFORMATION,
                BOX_LOOKUP, INDEX_RECORDS};

        return tasks;
    }

    // Method to return the editor type for this plugin
    public String getEditorType() {
        return null;
    }

    /**
     * Method to index records i.e cache box and container information in the database
     */
    public void indexRecords(final JFrame parent, final boolean gui) {
        final ResourcesDAO access = new ResourcesDAO();

        Thread performer = new Thread(new Runnable() {
            public void run() {
                InfiniteProgressPanel monitor = null;

                if (gui) {
                    monitor = ATProgressUtil.createModalProgressMonitor(parent, 1000, true);
                    monitor.start("Generating index...");
                }

                long resourceId;
                Resources selectedResource, resource;

                BoxLookupAndUpdate boxLookupAndUpdate;
                ContainerGatherer gatherer;

                // start the timer object
                MyTimer timer = new MyTimer();
                timer.reset();

                try {
                    ArrayList records = (ArrayList) access.findAll();

                    int totalRecords = records.size();
                    int i = 1;
                    for (Object object : records) {
                        if (monitor != null && monitor.isProcessCancelled()) {
                            System.out.println("Indexing cancelled ...");
                            break;
                        }

                        selectedResource = (Resources) object;
                        resourceId = selectedResource.getResourceId();
                        resource = (Resources) access.findByPrimaryKeyLongSession(resourceId);

                        monitor.setTextLine("Indexing resource " + i + " of " + totalRecords + " - " + resource.getTitle(), 1);

                        // index the containers
                        gatherer = new ContainerGatherer(resource, true);
                        ATContainerCollection containerCollection = gatherer.gatherContainers(monitor);

                        // index the boxes
                        boxLookupAndUpdate = new BoxLookupAndUpdate();
                        BoxLookupReturnRecordsCollection boxCollection = boxLookupAndUpdate.gatherContainersBySeries(resource, monitor, true);

                        // close the long session, otherwise memory would quickly run out
                        access.closeLongSession();
                        access.getLongSession();

                        i++;
                    }

                    System.out.println("Total Time to export: " + MyTimer.toString(timer.elapsedTimeMillis()));
                } catch (LookupException e) {
                    if (gui) {
                        monitor.close();
                        new ErrorDialog("Error loading resource", e).showDialog();
                    }
                    e.printStackTrace();
                } catch (PersistenceException e) {
                    if (gui) {
                        new ErrorDialog("Error looking up accession date", e).showDialog();
                    }
                    e.printStackTrace();
                } catch (SQLException e) {
                    if (gui) {
                        new ErrorDialog("Error resetting the long session", e).showDialog();
                    }
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    if (gui) {
                        monitor.close();
                        new ErrorDialog("Exception", e).showDialog();
                    }
                    e.printStackTrace();
                } finally {
                    if (gui) {
                        monitor.close();
                    }
                }
            }
        }, "Indexing Records ...");
        performer.start();
    }

    // code that is executed when plugin starts. not used here
    protected void doStart() {
    }

    // code that is executed after plugin stops. not used here
    protected void doStop() {
    }

    /**
     * Method to display the config dialog
     *
     * @param frame
     */
    public void showConfigDialog(Frame frame) {
        if (configDialog == null) {
            configDialog = new YalePluginTasksConfigDialog(frame);
            configDialog.pack();
        }

        // TODO load the config record from the database

        // display the dialog now
        configDialog.setVisible(true);
    }

    /**
     * Method to display the frame when running in stand alone mode
     */
    public void showApplicationFrame() {
        // display the dialog that allow running the commands
        YalePluginTasksFrame yalePluginTasksFrame = new YalePluginTasksFrame(this);
        yalePluginTasksFrame.pack();
        yalePluginTasksFrame.setVisible(true);
    }

    // main method for testing only
    public static void main(String[] args) {
        // load all the hibernate stuff that the AT application typically does
        startHibernate();

        // display the application frame
        YalePluginTasks yalePlugin = new YalePluginTasks();
        yalePlugin.showApplicationFrame();
    }

    /**
     * Method to load the hibernate engine and initial needed when running
     * as a standalone application
     */
    private static void startHibernate() {
        //get user preferences
        UserPreferences userPrefs = UserPreferences.getInstance();
        userPrefs.populateFromPreferences();

        // now bybass AT login since it assume that CLI will be used for command line.
        // Will have to change that in the future to a sure it's not abused
        if (!userPrefs.checkForDatabaseUrl()) {
            System.out.println("This appears to be the first time the AT was launched. \n" +
                    "Please fill out the database connection information");
            System.exit(1);
        }

        // start up hibernate
        try {
            userPrefs.updateSessionFactoryInfo();
        } catch (UnsupportedDatabaseType unsupportedDatabaseType) {
            System.out.println("Error connecting to database ...");
            System.exit(1);
        }

        // try connecting to the database
        try {
            connectAndTest();
        } catch (UnsupportedDatabaseType unsupportedDatabaseType) {
            System.out.println("Error connecting to database " + unsupportedDatabaseType);
            System.exit(1);
        }

        // Load the Lookup List
        if (!LookupListUtils.loadLookupLists()) {
            System.out.println("Failed to Load the Lookup List");
            System.exit(1);
        }

        // Loading Notes Etc. Types
        if (!NoteEtcTypesUtils.loadNotesEtcTypes()) {
            System.out.println("Failed to Load Notes Etc. Types");
            System.exit(1);
        }

        System.out.println("Loading Field Information");
        ATFieldInfo.loadFieldList();

        System.out.println("Loading Location Information");
        LocationsUtils.initLocationLookupList();

        System.out.println("Loading Default Value Information");
        DefaultValues.initDefaultValueLookup();

        System.out.println("Loading In-line tags");
        InLineTagsUtils.loadInLineTags();
    }

    /**
     * Connect and the database engine
     *
     * @throws UnsupportedDatabaseType
     */
    private static void connectAndTest() throws UnsupportedDatabaseType {
        while (!DatabaseConnectionUtils.testDbConnection()) {
            System.out.println("");
            System.exit(1);
        }

        try {
            while (!DatabaseConnectionUtils.checkVersion(DatabaseConnectionUtils.CHECK_VERSION_FROM_MAIN)) {
                System.out.println("Wrong database version connection");
                System.exit(1);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("The jdbc driver is missing");
            e.printStackTrace();
        }

        try {
            SessionFactory.testHibernate();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
