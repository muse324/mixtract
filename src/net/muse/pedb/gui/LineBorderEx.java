package net.muse.pedb.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;

/**
 * Improved LineBorder which can draw large and fine round corners.
 * @author Q's Lab (qoofast)
 */
public class LineBorderEx extends LineBorder {

    protected int arcH, arcV;
    protected Stroke stroke;
    protected boolean antialias;

    public LineBorderEx(Color color) {
        this(color, 1, 0, 0);
    }

    public LineBorderEx(Color color, int thickness) {
        this(color, thickness, 0, 0);
    }

    public LineBorderEx(Color color, int thickness, int arcRadius) {
        this(color, thickness, arcRadius, arcRadius);
    }

    public LineBorderEx(Color color, int thickness, int radH, int radV) {
        super(color, thickness, radH * radV > 0);
        this.antialias = getRoundedCorners();
        this.arcH = radH;
        this.arcV = radV;
        if (getRoundedCorners()) {
            stroke = new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        } else {
            stroke = new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        }
    }

    public void setAntiAliasEnable(boolean enable) {
        this.antialias = enable;
    }

    public boolean isAntiAliasEnable() {
        return antialias;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        Color oldColor = g.getColor();
        Stroke oldStroke = g2d.getStroke();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, isAntiAliasEnable()
                ? RenderingHints.VALUE_ANTIALIAS_ON
                : RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setColor(lineColor);
        g2d.setStroke(stroke);
        int p = thickness >> 1;
        if (!roundedCorners) {
            System.out.println("!");
            g2d.drawRect(x + p, y + p, width - thickness, height - thickness);
        } else {
            int r = Math.min(width, height);
            int rw = arcH;
            int rh = arcV;
            g2d.drawRoundRect(x + p, y + p, width - thickness, height - thickness, rw, rh);
        }
        g.setColor(oldColor);
        g2d.setStroke(oldStroke);
    }

    public boolean contains(JComponent c, int x, int y) {
        Rectangle rct = c.getBounds();
        int r = Math.min(rct.width, rct.height);
        int rw = arcH;
        int rh = arcV;
        RoundRectangle2D rrct = new RoundRectangle2D.Double(rct.x, rct.y, rct.width, rct.height,
                rw, rh);
        return (rrct.contains(x, y));
    }

    public double HorizontalArcRatio() {
        return arcH;
    }

    public double VerticalArcRatio() {
        return arcV;
    }
}