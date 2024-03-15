package com.kisera.pharma;

//Importation of relevant libraries used in this program
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class pharma extends JFrame{

    //Setting up attributes
    private JPanel landingPanel;
    private JLabel landingIcon;
    private JLabel welcomeNoteLabel;
    private JLabel docLoginLabel;
    private JLabel pharmacistLoginLabel;
    private JButton docLoginButton;
    private JButton pharmacistLoginButton;
    private JLabel copyrightLabel;

    //This is the main constructor
    public pharma()
    {
        setContentPane(landingPanel);
        setTitle("Pharma");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,500);
        setLocationRelativeTo(null);
        setVisible(true);

        //Implementing the button to open doctor's panel
        docLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new docClass();
                dispose();
            }
        });

        //Implementing the button to open pharmacist's panel
        pharmacistLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new pharmacistClass();
                dispose();
            }
        });
    }

    //Executing the constructor
    public static void main(String[] args) {
        new pharma();
    }

}
