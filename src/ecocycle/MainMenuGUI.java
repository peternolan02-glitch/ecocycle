/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - Main Menu & Launcher
 *
 * Author  : Jamie Stackpoole / 25130340
 * Author  : Peter Nolan / 24338893
 * Package : ecocycle
 * Purpose : The main landing screen and the EcoCycleApp launcher that opens it.
 */
package ecocycle; // shared package for the main menu and launcher

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import footprint.FootprintMenuGUI;
import marketplace.MarketplaceMenuGUI;

// Main landing menu screen that opens each section of the app
public class MainMenuGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    // Builds and lays out the main menu window
    public MainMenuGUI() {
        super("EcoCycle - Main Menu");
        setSize(560, 470);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centres the window on screen
        getContentPane().setBackground(AppTheme.PANEL_BG);

        // Top header banner
        getContentPane().add(
                AppTheme.createHeader("EcoCycle",
                        "Waste less. Measure your impact. Reuse what you have. - SDG 12"),
                BorderLayout.NORTH);

        // Four main buttons stacked in a single column
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        buttonPanel.setBackground(AppTheme.PANEL_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 70, 20, 70));

        JButton recyclingButton = AppTheme.createButton("1. Waste & Recycling Tracker");
        JButton footprintButton = AppTheme.createButton("2. Carbon Footprint Logger");
        JButton marketButton = AppTheme.createButton("3. Reuse Marketplace");
        JButton exitButton = AppTheme.createButton("Exit");

        // Section A is not yet merged into this branch, so show a friendly notice
        recyclingButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Section A (Waste & Recycling Tracker) is maintained by Jamie and will be merged for the final submission.",
                "Section A",
                JOptionPane.INFORMATION_MESSAGE));

        // Open Section B (Valentin) or Section C (Peter)
        footprintButton.addActionListener(e -> openSection(new FootprintMenuGUI()));
        marketButton.addActionListener(e -> openSection(new MarketplaceMenuGUI()));
        exitButton.addActionListener(e -> dispose()); // closes the app

        buttonPanel.add(recyclingButton);
        buttonPanel.add(footprintButton);
        buttonPanel.add(marketButton);
        buttonPanel.add(exitButton);

        getContentPane().add(buttonPanel, BorderLayout.CENTER);

        // Footer text at the bottom
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(AppTheme.PANEL_BG);
        footer.add(new JLabel("Prototype - Checkpoint 3 - SDG 12"));
        getContentPane().add(footer, BorderLayout.SOUTH);
    }

    // Opens the chosen section window and closes this one so only one window is open at a time
    private void openSection(JFrame nextScreen) {
        nextScreen.setVisible(true);
        dispose();
    }
}

// Entry point class that starts the application (matches main.class in project.properties)
class EcoCycleApp {

    // Starts the app on Swing's Event Dispatch Thread (the safe thread for Swing UI)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuGUI().setVisible(true));
    }
}
