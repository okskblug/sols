package kr.co.atis.uiutil;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

public class ButtonHideShowComponent extends JButton implements ActionListener {
	public ButtonHideShowComponent() {
//		int size = 17;
//		setPreferredSize(new Dimension(200, size));
		setUI(new BasicButtonUI());
		setContentAreaFilled(false);
		setFocusable(false);
		addMouseListener(buttonMouseListener);
		setRolloverEnabled(true);
		addActionListener(this);
		setBorder(null);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		
	}
	
	public void updateUI() {
		
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2	= (Graphics2D) g.create();
		// shift the image for pressed buttons
		if(getModel().isPressed()) {
			g2.translate(1,  1);
		}
		
		g2.setStroke(new BasicStroke(2));
		g2.setColor(new Color(220, 220, 220));
		int deltaX	= 10;
		int deltaY	= 3;
		int add	= 5;
		
		g2.setColor(Color.BLACK);
		if(getModel().isRollover()) {
			// ^
			
		}
		// ^
		// v
		g2.drawLine(deltaX, deltaY + add,			deltaX + add, deltaY);
		g2.drawLine(deltaX + add, deltaY,			deltaX + add + add, deltaY + add);
		g2.drawLine(deltaX + 2, deltaY + add - 1,		deltaX + add + add - 2, deltaY + add - 1);
		
		deltaY += add + 3;
		g2.drawLine(deltaX, deltaY,						deltaX + add, deltaY + add);
		g2.drawLine(deltaX +  add, deltaY + add,	deltaX + add + add, deltaY);
		g2.drawLine(deltaX + 2, deltaY + 1,			deltaX + add + add - 2, deltaY + 1);
		g2.dispose();
	}
	
	private final static MouseListener buttonMouseListener = new MouseAdapter() {
		public void mouseEntered(MouseEvent e) {
			Component component	= e.getComponent();
			if(component instanceof AbstractButton) {
				AbstractButton button	= (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
		
		public void mouseExited(MouseEvent e) {
			Component component	= e.getComponent();
			if(component instanceof AbstractButton) {
				AbstractButton button	= (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
	};
}