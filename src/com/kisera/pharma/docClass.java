package com.kisera.pharma;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.*;

public class docClass extends JFrame {

    private JPanel docMainPanel;
    private JLabel welcomeDoc;
    private JLabel welcomeDocNote;
    private JLabel authPersonNote;
    private JLabel searchMedLabel;
    private JButton refreshButton;
    private JLabel prescribeLabel;
    private JTextField patIDTextField;
    private JButton assignButton;
    private JComboBox selectMedCombo;
    private JButton backButton;
    private JTextField patNameTextField;
    private JTextField quantityTextField;
    private JLabel copyrightLabel;

    docClass()
    {
        setContentPane(docMainPanel);
        setTitle("Doctor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,500);
        setLocationRelativeTo(null);
        setVisible(true);

        //Refreshes drugs in the inventory
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Functionality to search the database inventory
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pharma?user=root&password=timonkisera123456_")) {
                    String selectQuery = "SELECT medName FROM medicine";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                        ResultSet resultSet = preparedStatement.executeQuery();

                        // Clear existing items
                        selectMedCombo.removeAllItems();

                        // Add medicine names to the JComboBox
                        while (resultSet.next()) {
                            String medName = resultSet.getString("medName");
                            selectMedCombo.addItem(medName);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error refreshing medicine list", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Appends selected drugs to patient
        assignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String patientID = patIDTextField.getText();
                String patientName = patNameTextField.getText();
                String selectedMedicine = (String) selectMedCombo.getSelectedItem();
                String quantityStr = quantityTextField.getText();

                if (patientID.isEmpty() || patientName.isEmpty() || selectedMedicine == null || quantityStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter patient ID, patient name, select a medicine, and enter quantity", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity = Integer.parseInt(quantityStr);

                // Database functionality
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pharma?user=root&password=timonkisera123456_")) {
                    String insertQuery = "INSERT INTO patients (patID, patName, drugID, quantity) VALUES (?, ?, (SELECT medID FROM medicine WHERE medName = ?), ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        preparedStatement.setInt(1, Integer.parseInt(patientID));
                        preparedStatement.setString(2, patientName);
                        preparedStatement.setString(3, selectedMedicine);
                        preparedStatement.setInt(4, quantity);
                        preparedStatement.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Prescription assigned successfully");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error assigning prescription to patient", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Exits the console
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new pharma();
                dispose();
            }
        });

        //Adding placeholders
        patIDTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (patIDTextField.getText().equals("Enter patient ID"))
                {
                    patIDTextField.setText("");
                    patIDTextField.setForeground(new Color(0,0,0));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (patIDTextField.getText().equals(""))
                {
                    patIDTextField.setText("Enter patient ID");
                    patIDTextField.setForeground(new Color(153,153,153));
                }
            }
        });

        //Adding placeholders
        patNameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (patNameTextField.getText().equals("Enter patient name")) {
                    patNameTextField.setText("");
                    patNameTextField.setForeground(new Color(0, 0, 0));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (patNameTextField.getText().equals("")) {
                    patNameTextField.setText("Enter patient name");
                    patNameTextField.setForeground(new Color(153, 153, 153));
                }
            }
        });

        //Adding placeholders
        quantityTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (quantityTextField.getText().equals("Enter the quantity")) {
                    quantityTextField.setText("");
                    quantityTextField.setForeground(new Color(0, 0, 0));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (quantityTextField.getText().equals("")) {
                    quantityTextField.setText("Enter the quantity");
                    quantityTextField.setForeground(new Color(153, 153, 153));
                }
            }
        });
    }

    public static void main(String[] args) {
        new docClass();
    }

}
