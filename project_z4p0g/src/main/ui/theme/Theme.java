package ui.theme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

// Central colour palette, typography, and factory helpers shared by all GUI windows
public final class Theme {
    // brand colours: a plum / rose palette suited to a cosmetics shop
    public static final Color PRIMARY = new Color(0x7A3B8A);
    public static final Color PRIMARY_DARK = new Color(0x542A62);
    public static final Color PRIMARY_SOFT = new Color(0xF0E4F3);
    public static final Color DANGER = new Color(0xB3423A);
    public static final Color DANGER_SOFT = new Color(0xF7E4E2);
    public static final Color SUCCESS = new Color(0x2E7D32);

    // neutral surfaces and text
    public static final Color BACKGROUND = new Color(0xF7F3F8);
    public static final Color CARD = Color.WHITE;
    public static final Color STRIPE = new Color(0xFAF6FB);
    public static final Color BORDER = new Color(0xE3D9E8);
    public static final Color TEXT = new Color(0x2B2430);
    public static final Color TEXT_MUTED = new Color(0x77687F);

    private static final String FONT_FAMILY = pickFontFamily();

    private Theme() {
    }

    // EFFECTS: switch to the platform look and feel so dialogs and scroll bars match the OS;
    //          keep the default look when the platform one is unavailable
    public static void installLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // the cross-platform look and feel is used instead
        }
    }

    // EFFECTS: return "Segoe UI" when installed (Windows), otherwise the logical sans-serif family
    private static String pickFontFamily() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (String family : ge.getAvailableFontFamilyNames()) {
            if (family.equals("Segoe UI")) {
                return family;
            }
        }
        return Font.SANS_SERIF;
    }

    // EFFECTS: create a font in the shared UI family with the given style and size
    public static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    // EFFECTS: create a small, muted, upper-case caption label used above input fields
    public static JLabel fieldCaption(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(font(Font.BOLD, 11));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    // EFFECTS: create a section heading label used on card panels
    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.BOLD, 14));
        label.setForeground(PRIMARY_DARK);
        return label;
    }

    // EFFECTS: create a text field styled to match the theme
    public static JTextField textField(int columns) {
        JTextField field = new JTextField(columns);
        styleField(field);
        return field;
    }

    // EFFECTS: create a password field styled to match the theme
    public static JPasswordField passwordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        styleField(field);
        return field;
    }

    // MODIFIES: field
    // EFFECTS: apply the shared font, colours, and padding to a text field
    private static void styleField(JTextField field) {
        field.setFont(font(Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBackground(CARD);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }
}
