package kr.co.atis.uiutil;

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
		this.addActionListener(e -> {
			SchemaExport.exportMQLFile(sType);
		});
	}
}
