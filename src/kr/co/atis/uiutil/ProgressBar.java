package kr.co.atis.uiutil;

import java.awt.Color;

import javax.swing.SwingWorker;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.util.SchemaConstants;

public class ProgressBar {
	
	public static void buttonClicked(String sButtonType) {
		if(sButtonType.equalsIgnoreCase(SchemaConstants.BTNTYPE_VIEW)) {
			if(BusinessViewMain.list.getSelectedValuesList().size() <= 0)
				return;
		}
		
		SwingWorker worker = new SwingWorker() {
			@Override
			public Object doInBackground() {
				// Find Button
				if(sButtonType.equalsIgnoreCase(SchemaConstants.BTNTYPE_FIND)) {
					BusinessViewMain.findButtonAction();
					
				// View Button
				} else if(sButtonType.equalsIgnoreCase(SchemaConstants.BTNTYPE_VIEW)) {
					BusinessViewMain.viewButtonAction();
				}
				return null;
			}

			@Override
			public void done() {
			}
		};
		
		worker.execute();
	}
	
	public static void excelExportClick(String sButtonType) {
		if(sButtonType.equalsIgnoreCase(SchemaConstants.BTNTYPE_VIEW)) {
			if(BusinessViewMain.list.getSelectedValuesList().size() <= 0)
				return;
		}
		
		SwingWorker worker = new SwingWorker() {
			@Override
			public Object doInBackground() {
				// Find Button
				if(sButtonType.equalsIgnoreCase(SchemaConstants.BTNTYPE_FIND)) {
					BusinessViewMain.findButtonAction();
					
				// View Button
				} else if(sButtonType.equalsIgnoreCase(SchemaConstants.BTNTYPE_VIEW)) {
					BusinessViewMain.viewButtonAction();
				}
				return null;
			}

			@Override
			public void done() {
			}
		};
		
		worker.execute();
	}
	
	/**
	 * Progressbar color
	 * @param count
	 */
	@Deprecated
	public static void setProgressColor(int count) {
		Color color = null;
		
		if(count > 90)			{color = new Color(29, 219, 22, 95);	// Green			
		} else if(count > 80)	{color = new Color(85, 242, 0, 95);
		} else if(count > 70)	{color = new Color(171, 242, 0, 95);
		} else if(count > 60)	{color = new Color(255, 228, 0, 95);
		} else if(count > 45)	{color = new Color(255, 187, 0, 95);
		} else if(count > 30)	{color = new Color(255, 140, 0, 95);
		} else if(count > 20)	{color = new Color(255, 94, 0, 95);
		} else if(count > 10)	{color = new Color(255, 47, 0, 95);
		} else if(count > 0)	{color = new Color(255, 0, 0, 95);	// RED
		}
		
//		BusinessViewMain.progress.setForeground(color);
	}
}
