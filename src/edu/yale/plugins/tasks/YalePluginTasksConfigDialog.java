/*
 * Created by JFormDesigner on Tue Nov 13 19:18:55 EST 2012
 */

package edu.yale.plugins.tasks;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Nathan Stevens
 */
public class YalePluginTasksConfigDialog extends JDialog {
    public YalePluginTasksConfigDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public YalePluginTasksConfigDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    /**
     * See if to use the cache records
     *
     * @return
     */
    public boolean getUseCacheRecords() {
        return useCacheRecordsCheckBox.isEnabled();
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

    private void okButtonActionPerformed() {
        // save setting to the database

        setVisible(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        useCacheRecordsCheckBox = new JCheckBox();
        saveCacheToDBCheckBox = new JCheckBox();
        runIndexButton = new JButton();
        updateAllRecordsCheckBox = new JCheckBox();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Yale Tasks Config Dialog");
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
                        FormFactory.DEFAULT_ROWSPEC
                    }));

                //---- useCacheRecordsCheckBox ----
                useCacheRecordsCheckBox.setText("Use Cache Records");
                contentPanel.add(useCacheRecordsCheckBox, cc.xy(1, 1));

                //---- saveCacheToDBCheckBox ----
                saveCacheToDBCheckBox.setText("Always Save Cache to Database");
                contentPanel.add(saveCacheToDBCheckBox, cc.xy(1, 3));

                //---- runIndexButton ----
                runIndexButton.setText("Run Index");
                contentPanel.add(runIndexButton, cc.xy(1, 5));

                //---- updateAllRecordsCheckBox ----
                updateAllRecordsCheckBox.setText("Update All Records");
                contentPanel.add(updateAllRecordsCheckBox, cc.xy(3, 5));
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
                    }
                });
                buttonBar.add(okButton, cc.xy(2, 1));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
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
    private JButton runIndexButton;
    private JCheckBox updateAllRecordsCheckBox;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
