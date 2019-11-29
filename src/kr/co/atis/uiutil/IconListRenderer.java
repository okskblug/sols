package kr.co.atis.uiutil;

import java.awt.Component;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import com.matrixone.apps.domain.util.FrameworkUtil;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.util.StringList;

/**
 * Menu, List (Icon Renderer)
 * @author ihjang
 *
 */
public class IconListRenderer extends DefaultListCellRenderer {

	private Map icons = null;
	private String viewFlag	= "";
	
	public IconListRenderer(Map icons) {
		this.viewFlag	= SchemaUtil.nullToEmpty((String) icons.get(SchemaConstants.VIEW_TYPE));
		this.icons 		= icons;
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		// Get the renderer component from parent class
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		// Get icon to use for the list item value
		int iAddIndex	= 0;
		int iModIndex	= 0;
		Icon icon 	= null;
		if(viewFlag.contains(",")) {	// Add, Mod index
			StringList slFlag	= FrameworkUtil.split(viewFlag, ",");
			iAddIndex			= Integer.parseInt((String) slFlag.get(0));
			iModIndex			= Integer.parseInt((String) slFlag.get(1));
			if(index < iAddIndex) {
				icon = (Icon) icons.get(SchemaConstants.ADD_FLAG);
			} else if(iAddIndex <= index && index < (iAddIndex + iModIndex)) {
				icon = (Icon) icons.get(SchemaConstants.MOD_FLAG);
			} else {
				icon = (Icon) icons.get(SchemaConstants.DEL_FLAG);
			}
		} else if(null != viewFlag && !"".equals(viewFlag)) {
			if(!icons.containsKey(viewFlag)) {
				icon = (Icon) icons.get(SchemaConstants.OBJECT);
			} else {
				icon = (Icon) icons.get(viewFlag);
			}
		} else {
			icon = (Icon) icons.get(value);
		}
		
		// Set icon to display for value
		label.setIcon(icon);
		
		return label;
	}
}


