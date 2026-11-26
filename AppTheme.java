/*
 * EcoCycle - SDG 12 Responsible Consumption and Production
 * Checkpoint 3 - individual section (Section A)
 *
 * Author  : Jamie Stackpoole (25130340)
 * Purpose : One shared look-and-feel so every screen looks the same.
 *           Every screen builds its buttons, headers and forms through the
 *           methods below, so restyling the app means editing this one file.
 *           No business logic lives here - this class only styles components.
 */
package ecocycle;

import java.awt.Color;
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

/**
 * Shared styling helpers for every EcoCycle screen.
 */
public final class AppTheme {

    /* The background colour used by every screen. */
    public static final Color PANEL_BG = new Color(0xF2, 0xF7, 0xFB);

    /* Colours and fonts are private: screens get them through the methods
       below instead of using raw values, so nothing can drift out of sync. */
    private static final Color GREEN_DARK = new Color(0x0B, 0x53, 0x94);
    private static final Color WHITE = Color.WHITE;
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);

    /** Only static helpers here, so there is no reason to create one. */
    private AppTheme() {
    }

    /**
     * Creates a button that matches the rest of the application.
     *
     * @param text the text shown on the button
     * @return a styled, clickable button
     */
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(WHITE);
        button.setForeground(GREEN_DARK);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREEN_DARK, 2),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));
        return button;
    }

    /**
     * Creates the coloured header bar at the top of every screen, so the user
     * always knows which screen they are on.
     *
     * @param title    the screen title
     * @param subtitle a one line description of the screen
     * @return a panel ready to be added with BorderLayout.NORTH
     */
    public static JPanel createHeader(String title, String subtitle) {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(GREEN_DARK);

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

    /**
     * Builds an empty form panel for the input screens.
     *
     * <p>The panel is a two column grid: labels on the left, input fields on
     * the right. Each {@link #addFormRow(JPanel, String, JComponent)} call
     * adds the next row, so a form reads as a plain list of rows - no
     * layout mathematics needed.</p>
     *
     * @return an empty form panel ready for addFormRow calls
     */
    public static JPanel formPanel() {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 6));
        form.setBackground(PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(16, 30, 6, 30));
        return form;
    }

    /**
     * Adds one "label + input" row to a panel from {@link #formPanel()}.
     *
     * @param form      the panel to add the row to
     * @param label     the text shown on the left
     * @param component the input field shown on the right
     */
    public static void addFormRow(JPanel form, String label, JComponent component) {
        form.add(new JLabel(label));
        form.add(component);
    }

    /**
     * Shows a longer report in a scrollable box. All three sections use this
     * for reports that are too long for a plain message box.
     *
     * @param parent the window the box should appear over
     * @param title  the title of the box
     * @param text   the report itself
     */
    public static void showReport(java.awt.Component parent, String title, String text) {
        JTextArea area = new JTextArea(text, 18, 48);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(parent, new JScrollPane(area), title,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
