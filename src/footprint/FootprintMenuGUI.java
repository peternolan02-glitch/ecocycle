/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 2 - GUI prototype
 *
 * Author  : Valentin Gabriel Luchian / 24318230
 * Package : footprint  (Section B)
 * Purpose : Section B's three screens in one file: the section menu first,
 *           then the two forms it opens. Only the menu is public - the forms
 *           are package-private because only this section uses them.
 */
package footprint;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import ecocycle.AppTheme;
import ecocycle.MainMenuGUI;

/**
 * Menu for Section B (Carbon Footprint Logger).
 */
public class FootprintMenuGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Builds the section menu window. */
    public FootprintMenuGUI() {
        super("EcoCycle - Carbon Footprint Logger");
        setSize(600, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.PANEL_BG);

        getContentPane().add(
                AppTheme.createHeader("Section B - Carbon Footprint Logger",
                        "Owner: Valentin Gabriel Luchian - log travel and energy, track your CO2e"),
                BorderLayout.NORTH);

        /* The four feature buttons, stacked in a column. */
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        buttonPanel.setBackground(AppTheme.PANEL_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(18, 70, 18, 70));

        JButton addButton = AppTheme.createButton("1. Log an Activity");
        JButton viewButton = AppTheme.createButton("2. Display / Search / Delete");
        JButton totalButton = AppTheme.createButton("3. Weekly Total (Checkpoint 3)");
        JButton tipsButton = AppTheme.createButton("4. Low-carbon Tips (Checkpoint 3)");

        addButton.addActionListener(e -> open(new LogActivityFormGUI()));
        viewButton.addActionListener(e -> open(new GoalSearchFormGUI()));
        totalButton.addActionListener(e -> AppTheme.showNotYet(this, "The weekly total"));
        tipsButton.addActionListener(e -> AppTheme.showNotYet(this, "Low-carbon tips"));

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(totalButton);
        buttonPanel.add(tipsButton);

        getContentPane().add(buttonPanel, BorderLayout.CENTER);

        JButton backButton = AppTheme.createButton("< Back to Main Menu");
        backButton.addActionListener(e -> {
            new MainMenuGUI().setVisible(true);
            dispose();
        });
        JPanel bottom = new JPanel();
        bottom.setBackground(AppTheme.PANEL_BG);
        bottom.add(backButton);
        getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Opens another screen and closes this one, so only one window is on
     * screen at a time.
     *
     * @param nextScreen the window to show
     */
    private void open(JFrame nextScreen) {
        nextScreen.setVisible(true);
        dispose();
    }
}