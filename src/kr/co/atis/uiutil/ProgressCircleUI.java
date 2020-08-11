package kr.co.atis.uiutil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class ProgressCircleUI extends BasicProgressBarUI {
	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension d = super.getPreferredSize(c);
		int v = Math.max(d.width, d.height);
		d.setSize(v, v);
		return d;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		// public void paintDeterminate(Graphics g, JComponent c) {
		Insets b = progressBar.getInsets(); // area for border
//		System.err.println(progressBar.getWidth() + " : " +  b.right + " : " +  b.left);
//		int barRectWidth = progressBar.getWidth() - b.right - b.left;
//		System.err.println(progressBar.getHeight() + " : " +  b.top + " : " +  b.bottom);
//		int barRectHeight = progressBar.getHeight() - b.top - b.bottom;
		int barRectWidth = 200;
		int barRectHeight = 200;
		if (barRectWidth <= 0 || barRectHeight <= 0) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double degree = 360 * progressBar.getPercentComplete();
		double sz = Math.min(barRectWidth, barRectHeight);
//		double cx = b.left + barRectWidth * .5;
		double cx = b.left + (progressBar.getWidth() - (barRectWidth / 2)) * .5;
//		double cy = b.top + barRectHeight * .5;
		double cy = b.top + (progressBar.getHeight() - (barRectHeight / 2)) * .5;
		double or = sz * .5;
		// double ir = or - 20;
		 double ir = or - 15;
//		double ir = or * .5; // .8;
		Shape inner = new Ellipse2D.Double(cx - ir, cy - ir, ir * 2, ir * 2);
		Shape outer = new Ellipse2D.Double(cx - or, cy - or, sz, sz);
		Shape sector = new Arc2D.Double(cx - or, cy - or, sz, sz, 90 - degree, degree, Arc2D.PIE);

		Area foreground = new Area(sector);
		Area background = new Area(outer);
		Area hole = new Area(inner);

		foreground.subtract(hole);
		background.subtract(hole);

		// draw the track
		g2.setPaint(new Color(0xDD_DD_DD));
//		setBackground(new Color(249, 235, 234, 50));
		g2.fill(background);

		// draw the circular sector
		// AffineTransform at = AffineTransform.getScaleInstance(-1.0, 1.0);
		// at.translate(-(barRectWidth + b.left * 2), 0);
		// AffineTransform at =
		// AffineTransform.getRotateInstance(Math.toRadians(degree), cx, cy);
		// g2.fill(at.createTransformedShape(area));
		g2.setPaint(progressBar.getForeground());
		g2.fill(foreground);
		g2.dispose();

		// Deal with possible text painting
		if (progressBar.isStringPainted()) {
			paintString(g, b.left, b.top, barRectWidth, barRectHeight, 0, b);
		}
	}
}




