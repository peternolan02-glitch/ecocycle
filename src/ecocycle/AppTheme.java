/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - Shared UI Theme
 *
 * Package : ecocycle
 * Purpose : Shared colours, fonts, and helper methods so every screen looks consistent.
 */
package ecocycle; // shared package so all 3 sections use the same styling

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Shared styling helper class for all screens in EcoCycle (final so it cannot be subclassed)
public final class AppTheme {

    // Light background colour used on every window and panel
    public static final Color PANEL_BG = new Color(0xF2, 0xF7, 0xFB);

    // Main dark accent colour for headers and buttons
    private static final Color THEME_GREEN = new Color(0x0B, 0x53, 0x94);
    private static final Color WHITE = Color.WHITE;

    // Shared fonts for titles, subtitles, and buttons
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);

    private AppTheme() {
        // private constructor so nobody creates an AppTheme object - all methods here are static
    }

    // Creates a styled button so every button in the app looks the same
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(WHITE);
        button.setForeground(THEME_GREEN);
        button.setFocusPainted(false); // removes the dotted focus box around the text
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // hand pointer on hover
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(THEME_GREEN, 2),
                BorderFactory.createEmptyBorder(10, 18, 10, 18))); // border + inner padding
        return button;
    }

    // Creates the coloured header banner at the top of a screen with a title and subtitle
    public static JPanel createHeader(String title, String subtitle) {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(THEME_GREEN);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(14, 16, 2, 16));

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(WHITE);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));

        header.add(titleLabel);
        header.add(subtitleLabel);
        return header;
    }

    // Creates a neat 2-column panel for forms (labels on the left, inputs on the right)
    public static JPanel formPanel() {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 6));
        form.setBackground(PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(16, 30, 6, 30));
        return form;
    }

    // Adds one label + input field row into a form panel
    public static void addFormRow(JPanel form, String label, JComponent component) {
        form.add(new JLabel(label));
        form.add(component);
    }

    // Opens a scrollable popup window to display longer text reports (like stats or listings)
    public static void showReport(Component parent, String title, String text) {
        JTextArea area = new JTextArea(text, 18, 48);
        area.setEditable(false); // read-only so the user cannot edit the report text
        area.setFont(new Font("Monospaced", Font.PLAIN, 12)); // monospaced font keeps columns lined up
        JOptionPane.showMessageDialog(parent, new JScrollPane(area), title,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
