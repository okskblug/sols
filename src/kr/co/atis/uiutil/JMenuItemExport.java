package kr.co.atis.uiutil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import kr.co.atis.util.SchemaExport;

/**
 * Menu Item (Add Icon)
 * @author ihjang
 */
public class JMenuItemExport extends JMenuItem{
	public JMenuItemExport() {
	}
	
	public JMenuItemExport(String sLabel, String sType, ImageIcon icon) {
		this.setLabel(sLabel);
		this.setIcon(icon);
		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SchemaExport.exportMQLFile(sType);
			}
		});
	}
}
