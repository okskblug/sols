package kr.co.atis.uiutil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;

public class ProgressListener implements PropertyChangeListener {
	private final JProgressBar progressBar;

	public ProgressListener(JProgressBar progressBar) {
		this.progressBar = progressBar;
		this.progressBar.setValue(0);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String strPropertyName = e.getPropertyName();
		if ("progress".equals(strPropertyName)) {
			progressBar.setIndeterminate(false);
			int progress = (Integer) e.getNewValue();
			progressBar.setValue(progress);
		}
	}
}