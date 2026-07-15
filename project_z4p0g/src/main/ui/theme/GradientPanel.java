package ui.theme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

// A panel painted with a vertical plum-to-rose gradient; used as the login backdrop
public class GradientPanel extends JPanel {
    private static final Color TOP = new Color(0x46215B);
    private static final Color BOTTOM = new Color(0xE290B2);

    // EFFECTS: create a gradient-painted panel with the given layout
    public GradientPanel(LayoutManager layout) {
        super(layout);
        setOpaque(true);
    }

    // MODIFIES: g
    // EFFECTS: paint the background gradient before children are painted
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, TOP, 0, getHeight(), BOTTOM));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
