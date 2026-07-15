package ui.theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

// A flat button with rounded corners and hover / pressed / focus shading in theme colours
public class RoundedButton extends JButton {
    private static final int ARC = 12;
    private final Color background;

    // EFFECTS: create a rounded button with the given caption and colours
    public RoundedButton(String text, Color background, Color foreground) {
        super(text);
        this.background = background;
        setForeground(foreground);
        setFont(Theme.font(Font.BOLD, 13));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    // EFFECTS: create a primary (plum) action button
    public static RoundedButton primary(String text) {
        return new RoundedButton(text, Theme.PRIMARY, Color.WHITE);
    }

    // EFFECTS: create a soft (tonal) secondary button with dark text
    public static RoundedButton tonal(String text) {
        return new RoundedButton(text, Theme.PRIMARY_SOFT, Theme.PRIMARY_DARK);
    }

    // EFFECTS: create a danger-tinted button for destructive actions
    public static RoundedButton danger(String text) {
        return new RoundedButton(text, Theme.DANGER_SOFT, Theme.DANGER);
    }

    // MODIFIES: g
    // EFFECTS: paint the rounded background, shaded for hover / pressed states, plus a focus ring
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(currentBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
        if (isFocusOwner()) {
            Color fg = getForeground();
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 110));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, ARC, ARC);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    // EFFECTS: return the background colour matching the button's current state
    private Color currentBackground() {
        if (getModel().isPressed()) {
            return shade(background, -28);
        }
        if (getModel().isRollover()) {
            return isLight(background) ? shade(background, -14) : shade(background, 22);
        }
        return background;
    }

    // EFFECTS: return true when the colour is perceptually light
    private static boolean isLight(Color c) {
        return (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()) / 255 > 0.6;
    }

    // EFFECTS: return the colour with all channels shifted by the given amount, clamped to range
    private static Color shade(Color c, int amount) {
        return new Color(clamp(c.getRed() + amount), clamp(c.getGreen() + amount), clamp(c.getBlue() + amount));
    }

    // EFFECTS: return the channel value clamped to the valid 0-255 range
    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
