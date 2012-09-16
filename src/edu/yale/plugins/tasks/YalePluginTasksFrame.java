/*
 * Created by JFormDesigner on Wed Sep 12 19:21:00 EDT 2012
 */

package edu.yale.plugins.tasks;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import edu.yale.plugins.tasks.dbdialog.RemoteDBConnectDialog;

/**
 * @author Nathan Stevens
 */
public class YalePluginTasksFrame extends JFrame {
    // Used to connect to the AT database
    private RemoteDBConnectDialog dbdialog = null;

    /**
     * Main constructor
     */
    public YalePluginTasksFrame() {
        initComponents();
    }

    /**
     * Display the dialog that allows selection of a connection, and
     * ultimately a resource record
     */
    private void showDBDialogButtonActionPerformed() {
        if(dbdialog == null) {
            dbdialog = new RemoteDBConnectDialog(this);
        }

        dbdialog.pack();
        dbdialog.setVisible(true);
    }

    /**
     * Exit the application when the OK button is pressed
     */
    private void okButtonActionPerformed() {
        setVisible(false);
        System.exit(0);
    }

    /**
     * This will display the location assignment dialog
     */
    private void assignContainerButtonActionPerformed() {
        YaleLocationAssignmentResources locationAssignmentDialog = new YaleLocationAssignmentResources(this);
        locationAssignmentDialog.pack();
        locationAssignmentDialog.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        assignContainerButton = new JButton();
        label1 = new JLabel();
        boxSearchButton = new JButton();
        voyagerExportButton = new JButton();
        buttonBar = new JPanel();
        showDBDialogButton = new JButton();
        okButton = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle("Yale Tasks Application v 2.0");
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

                //---- assignContainerButton ----
                assignContainerButton.setText("Assign Container Information");
                assignContainerButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        assignContainerButtonActionPerformed();
                    }
                });
                contentPanel.add(assignContainerButton, cc.xy(1, 1));

                //---- label1 ----
                label1.setText(" Open the container information dialog ");
                contentPanel.add(label1, cc.xy(3, 1));

                //---- boxSearchButton ----
                boxSearchButton.setText("Box Search");
                contentPanel.add(boxSearchButton, cc.xy(1, 3));

                //---- voyagerExportButton ----
                voyagerExportButton.setText("Export Voyager Info");
                contentPanel.add(voyagerExportButton, cc.xy(1, 5));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                buttonBar.setLayout(new FormLayout(
                    new ColumnSpec[] {
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC,
                        FormFactory.GLUE_COLSPEC,
                        FormFactory.BUTTON_COLSPEC
                    },
                    RowSpec.decodeSpecs("pref")));

                //---- showDBDialogButton ----
                showDBDialogButton.setText("Database Connection");
                showDBDialogButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showDBDialogButtonActionPerformed();
                    }
                });
                buttonBar.add(showDBDialogButton, cc.xy(2, 1));

                //---- okButton ----
                okButton.setText("Exit");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed();
                    }
                });
                buttonBar.add(okButton, cc.xy(4, 1));
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
    private JButton assignContainerButton;
    private JLabel label1;
    private JButton boxSearchButton;
    private JButton voyagerExportButton;
    private JPanel buttonBar;
    private JButton showDBDialogButton;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
