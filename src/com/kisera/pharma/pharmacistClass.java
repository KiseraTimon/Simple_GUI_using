package com.kisera.pharma;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class pharmacistClass extends JFrame {

    private JPanel pharmacistPanel;
    private JLabel welcomePharmLabel;
    private JLabel welcomeIcon;
    private JLabel authPersonLabel;
    private JButton addStockButton;
    private JButton sellMedsButton;
    private JButton deleteMedsButton;
    private JButton searchPatButton;
    private JButton searchMedsButton;
    private JButton backButton;
    private JLabel copyrightLabel;

    pharmacistClass()
    {
        setContentPane(pharmacistPanel);
        setTitle("Pharmacist");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,500);
        setLocationRelativeTo(null);
        setVisible(true);

        //Adding stock
        addStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String medName;
                int medId, quantity, expiry, option;

                medName = JOptionPane.showInputDialog(null,"Enter the name of the medicine");
                medId = Integer.parseInt(JOptionPane.showInputDialog("Assign " +medName+" a unique identifier"));
                quantity = Integer.parseInt(JOptionPane.showInputDialog("Enter the units to add"));
                expiry = Integer.parseInt(JOptionPane.showInputDialog("Enter the expiry date of the medicine"));

                //Confirmation dialogue and database
                option = JOptionPane.showConfirmDialog(null, "Select yes to complete the addition process");
                if(option == JOptionPane.YES_OPTION)
                {
                    //Adding stock to the database
                    try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pharma?user=root&password=timonkisera123456_")) {
                        String insertQuery = "INSERT INTO medicine (medID, medName, quantity, expiry) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                            preparedStatement.setInt(1, medId);
                            preparedStatement.setString(2, medName);
                            preparedStatement.setInt(3, quantity);
                            preparedStatement.setInt(4, expiry);
                            preparedStatement.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Stock added successfully");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error adding stock to the database");
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"Process Terminated");
                }
            }
        });

        //Deleting stock
        deleteMedsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String medName;
                int medId, expiry, option;

                medName = JOptionPane.showInputDialog(null,"Enter the name of the medicine");
                medId = Integer.parseInt(JOptionPane.showInputDialog("Enter the medicine's unique identifier"));
                expiry = Integer.parseInt(JOptionPane.showInputDialog("Enter the expiry date of the medicine"));

                //Confirmation dialogue and database
                option = JOptionPane.showConfirmDialog(null, "Select yes to complete the deletion process");
                if(option == JOptionPane.YES_OPTION)
                {
                    //Functionality to delete stock to the database
                    try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pharma?user=root&password=timonkisera123456_")) {
                        String deleteQuery = "DELETE FROM medicine WHERE medName = ? AND medID = ? AND expiry = ?";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                            preparedStatement.setString(1, medName);
                            preparedStatement.setInt(2, medId);
                            preparedStatement.setInt(3, expiry);

                            int rowsAffected = preparedStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(null, "Medicine deleted successfully");
                            } else {
                                JOptionPane.showMessageDialog(null, "No matching medicine found");
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting medicine from the database");
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"Process Terminated");
                }
            }
        });

        //Searching medicine
        searchMedsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String medName;

                medName = JOptionPane.showInputDialog("Enter the name of the medicine");

                //Database functionality to return tuples having a common entry
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pharma?user=root&password=timonkisera123456_")) {
                    String selectQuery = "SELECT * FROM medicine WHERE medName LIKE ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                        preparedStatement.setString(1, "%" + medName + "%");

                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            StringBuilder result = new StringBuilder();
                            while (resultSet.next()) {
                                result.append("Medicine ID: ").append(resultSet.getInt("medID"))
                                        .append(", Medicine Name: ").append(resultSet.getString("medName"))
                                        .append(", Quantity: ").append(resultSet.getInt("quantity"))
                                        .append(", Expiry: ").append(resultSet.getInt("expiry"))
                                        .append("\n");
                            }

                            if (result.length() > 0) {
                                JOptionPane.showMessageDialog(null, "Search results:\n" + result.toString());
                            } else {
                                JOptionPane.showMessageDialog(null, "No matching medicine found");
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error searching for medicine in the database");
                }
            }
        });

        //Searching patient
        searchPatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int patID;

                patID = Integer.parseInt(JOptionPane.showInputDialog("Enter the patient's ID number"));
                //Database functionality to return tuples having common entry
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pharma?user=root&password=timonkisera123456_")) {
                    String selectQuery = "SELECT p.patID, p.patName, p.quantity, p.drugID, m.medName " +
                            "FROM patients p " +
                            "JOIN medicine m ON p.drugID = m.medID " +
                            "WHERE p.patID = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                        preparedStatement.setInt(1, patID);
                        ResultSet resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            // Patient found, display information
                            int patientID = resultSet.getInt("patID");
                            String patientName = resultSet.getString("patName");
                            int quantity = resultSet.getInt("quantity");
                            int drugID = resultSet.getInt("drugID");
                            String medName = resultSet.getString("medName");

                            String resultMessage = "Patient ID: " + patientID + "\nPatient Name: " + patientName +
                                    "\nQuantity: " + quantity + "\nDrug ID: " + drugID +
                                    "\nMedicine Name: " + medName;
                            JOptionPane.showMessageDialog(null, resultMessage, "Patient Information", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            // Patient not found
                            JOptionPane.showMessageDialog(null, "Patient not found", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error searching for patient", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        sellMedsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int patID, quantity, option;

                patID = Integer.parseInt(JOptionPane.showInputDialog("Enter the patient's ID number"));
                String medName = JOptionPane.showInputDialog("Enter the name of the medicine");
                quantity = Integer.parseInt(JOptionPane.showInputDialog("Enter the quantity"));

                // Confirmation dialogue and database
                option = JOptionPane.showConfirmDialog(null, "Select yes to dispense medicine");
                if (option == JOptionPane.YES_OPTION) {
                    try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pharma?user=root&password=timonkisera123456_")) {
                        // Check if the patient is prescribed the specified medicine
                        String checkPrescriptionQuery = "SELECT * FROM patients WHERE patID = ? AND drugID IN (SELECT medID FROM medicine WHERE medName = ?)";
                        try (PreparedStatement checkPrescriptionStatement = connection.prepareStatement(checkPrescriptionQuery)) {
                            checkPrescriptionStatement.setInt(1, patID);
                            checkPrescriptionStatement.setString(2, medName);
                            ResultSet resultSet = checkPrescriptionStatement.executeQuery();

                            if (resultSet.next()) {
                                // Patient is prescribed the medicine, get the prescribed quantity
                                int prescribedQuantity = resultSet.getInt("quantity");

                                if (quantity == prescribedQuantity) {
                                    // Proceed to dispense
                                    // Check if the requested quantity is available in the inventory
                                    String checkAvailabilityQuery = "SELECT quantity FROM medicine WHERE medName = ?";
                                    try (PreparedStatement availabilityStatement = connection.prepareStatement(checkAvailabilityQuery)) {
                                        availabilityStatement.setString(1, medName);
                                        resultSet = availabilityStatement.executeQuery();

                                        if (resultSet.next()) {
                                            int availableQuantity = resultSet.getInt("quantity");

                                            if (quantity <= availableQuantity) {
                                                // Update the quantity in the database
                                                String updateQuantityQuery = "UPDATE medicine SET quantity = ? WHERE medName = ?";
                                                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuantityQuery)) {
                                                    updateStatement.setInt(1, availableQuantity - quantity);
                                                    updateStatement.setString(2, medName);
                                                    updateStatement.executeUpdate();
                                                    JOptionPane.showMessageDialog(null, "Medicine dispensed successfully");

                                                    // Delete the patient and associated tuples
                                                    String deletePatientQuery = "DELETE FROM patients WHERE patID = ?";
                                                    try (PreparedStatement deletePatientStatement = connection.prepareStatement(deletePatientQuery)) {
                                                        deletePatientStatement.setInt(1, patID);
                                                        deletePatientStatement.executeUpdate();
                                                        JOptionPane.showMessageDialog(null, "Patient and associated fields deleted");
                                                    }
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(null, "Not enough quantity available in the inventory");
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Medicine not found in the inventory");
                                        }
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Requested quantity does not match the prescribed quantity");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Patient is not prescribed the specified medicine");
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error dispensing medicine");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Process Terminated");
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new pharma();
               dispose();
            }
        });
    }

    public static void main(String[] args) {
        new pharmacistClass();
    }

}
