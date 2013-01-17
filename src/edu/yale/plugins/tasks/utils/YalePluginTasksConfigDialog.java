/*
 * Created by JFormDesigner on Tue Nov 13 19:18:55 EST 2012
 */

package edu.yale.plugins.tasks.utils;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import edu.yale.plugins.tasks.YalePluginTasks;

/**
 * @author Nathan Stevens
 */
public class YalePluginTasksConfigDialog extends JDialog {
    private YalePluginTasks yalePluginTasks;

    public YalePluginTasksConfigDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public YalePluginTasksConfigDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    /**
     * Method that is called when the user is not level 5 to hide certain fields
     */
    public void limitAccess() {
        useCacheRecordsCheckBox.setEnabled(false);
        saveCacheToDBCheckBox.setEnabled(false);
        exportVoyagerCheckBox.setEnabled(false);
        deleteIndexButton.setEnabled(false);
        deleteCountLabel.setEnabled(false);
        runIndexButton.setEnabled(false);
        updateAllRecordsCheckBox.setEnabled(false);
    }

    /**
     * Method to set the yale plugin task object
     *
     * @param yalePluginTasks
     */
    public void setYalePluginTasks(YalePluginTasks yalePluginTasks) {
        this.yalePluginTasks = yalePluginTasks;
    }

    /**
     * See if to use the cache records
     *
     * @return
     */
    public boolean getUseCacheRecords() {
        return useCacheRecordsCheckBox.isSelected();
    }

    /**
     * Method to see if to always save records to the database
     * Mainly used for testing program
     *
     * @return
     */
    public boolean getAlwaysSaveCache() {
        return saveCacheToDBCheckBox.isSelected();
    }

    /**
     * Method to check to see if to always export the voyager information
     *
     * @return
     */
    public boolean getAlwaysExportVoyagerInformation() {
       return exportVoyagerCheckBox.isSelected();
    }

    private void okButtonActionPerformed() {
        // TODO save setting to the database
        deleteCountLabel.setText("0 records deleted");
        setVisible(false);
    }

    /**
     * Method to run the indexer
     */
    private void runIndexButtonActionPerformed() {
        yalePluginTasks.indexRecords(this, updateAllRecordsCheckBox.isSelected(), true);
    }

    /**
     * Cancel button pressed so just set the window invisible
     */
    private void cancelButtonActionPerformed() {
        deleteCountLabel.setText("0 records deleted");
        setVisible(false);
    }

    /**
     * Method to delete the index records in from the database by making sql call
     */
    private void deleteIndexButtonActionPerformed() {
        try {
            String message = "Are you sure you want to completely delete the indexed records?\n" +
                    "This action cannot be undone.";

            int n = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            // if user selected yes proceed with delete
            if(n == JOptionPane.YES_OPTION) {
                int count = PluginDataUtils.deleteAllIndexRecords();
                deleteCountLabel.setText(count + " records deleted");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to set the cell highlight color. This setting is stored to the database
     */
    private void highlightButtonActionPerformed() {
        Color newColor = JColorChooser.showDialog(
                this,
                "Choose Highlight Color",
                highlightLabel.getBackground());

        if (newColor != null) {
            highlightLabel.setBackground(newColor);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        useCacheRecordsCheckBox = new JCheckBox();
        saveCacheToDBCheckBox = new JCheckBox();
        exportVoyagerCheckBox = new JCheckBox();
        deleteIndexButton = new JButton();
        deleteCountLabel = new JLabel();
        runIndexButton = new JButton();
        updateAllRecordsCheckBox = new JCheckBox();
        highlightButton = new JButton();
        highlightLabel = new JLabel();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Yale Tasks Config Dialog v2.01");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    new ColumnSpec[] {
                        FormFactory.DEFAULT_COLSPEC,
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC
                    },
                    new RowSpec[] {
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC
                    }));

                //---- useCacheRecordsCheckBox ----
                useCacheRecordsCheckBox.setText("Use Cache Records");
                useCacheRecordsCheckBox.setSelected(true);
                contentPanel.add(useCacheRecordsCheckBox, cc.xy(1, 1));

                //---- saveCacheToDBCheckBox ----
                saveCacheToDBCheckBox.setText("Always Save Cache to Database");
                saveCacheToDBCheckBox.setSelected(true);
                contentPanel.add(saveCacheToDBCheckBox, cc.xy(1, 3));

                //---- exportVoyagerCheckBox ----
                exportVoyagerCheckBox.setText("Always Export Voyager Information");
                contentPanel.add(exportVoyagerCheckBox, cc.xy(1, 5));

                //---- deleteIndexButton ----
                deleteIndexButton.setText("Delete All Index Records");
                deleteIndexButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        deleteIndexButtonActionPerformed();
                    }
                });
                contentPanel.add(deleteIndexButton, cc.xy(1, 7));

                //---- deleteCountLabel ----
                deleteCountLabel.setText("O records deleted");
                contentPanel.add(deleteCountLabel, cc.xy(3, 7));

                //---- runIndexButton ----
                runIndexButton.setText("Run Index");
                runIndexButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        runIndexButtonActionPerformed();
                    }
                });
                contentPanel.add(runIndexButton, cc.xy(1, 9));

                //---- updateAllRecordsCheckBox ----
                updateAllRecordsCheckBox.setText("Update All Records");
                contentPanel.add(updateAllRecordsCheckBox, cc.xy(3, 9));

                //---- highlightButton ----
                highlightButton.setText("Set Highlighted Color");
                highlightButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        highlightButtonActionPerformed();
                    }
                });
                contentPanel.add(highlightButton, cc.xy(1, 11));

                //---- highlightLabel ----
                highlightLabel.setText(" Highlighted");
                highlightLabel.setBackground(new Color(0, 51, 255));
                highlightLabel.setOpaque(true);
                highlightLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
                contentPanel.add(highlightLabel, cc.xy(3, 11));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                buttonBar.setLayout(new FormLayout(
                    new ColumnSpec[] {
                        FormFactory.GLUE_COLSPEC,
                        FormFactory.BUTTON_COLSPEC,
                        FormFactory.RELATED_GAP_COLSPEC,
                        FormFactory.BUTTON_COLSPEC
                    },
                    RowSpec.decodeSpecs("pref")));

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed();
                        okButtonActionPerformed();
                    }
                });
                buttonBar.add(okButton, cc.xy(2, 1));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed();
                    }
                });
                buttonBar.add(cancelButton, cc.xy(4, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JCheckBox useCacheRecordsCheckBox;
    private JCheckBox saveCacheToDBCheckBox;
    private JCheckBox exportVoyagerCheckBox;
    private JButton deleteIndexButton;
    private JLabel deleteCountLabel;
    private JButton runIndexButton;
    private JCheckBox updateAllRecordsCheckBox;
    private JButton highlightButton;
    private JLabel highlightLabel;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    // Method to return the highlighted color
    public Color getHighlightColor() {
        return highlightLabel.getBackground();
    }
}
