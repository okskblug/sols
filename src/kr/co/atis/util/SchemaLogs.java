package kr.co.atis.util;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.uiutil.DialogSchemaView;

public class SchemaLogs {

	/**
	 * Append Log, each Date
	 * @param sWriteType
	 * @param sLog
	 * @throws Exception
	 */
	public static void writeLogFile(String sWriteType, String sLog) throws Exception {
        FileWriter statusLog 		= null;

        String sToday				= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        StringBuilder sbFilePath	= new StringBuilder("./logs/logs.").append(sToday).append(".log");
        statusLog   				= new FileWriter(sbFilePath.toString(), true);
        
        statusLog.flush();
        if(sWriteType.startsWith("START"))
        	statusLog.write("\r\n\r\n");
        statusLog.write(new StringBuilder("[").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("] [").append(sWriteType).append("]  ").toString());
        statusLog.write(sLog);
        statusLog.write("\r\n");
        statusLog.flush();
        statusLog.close();
	}
	
	
	/**
	 * Log Open
	 * @throws Exception
	 */
	public static void logsFileChooser() throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
        JFileChooser chooser 	= new JFileChooser(System.getProperty("user.dir") + "/logs");
        chooser.setMultiSelectionEnabled(true);// MultiSelect
        chooser.setFileFilter(new FileNameExtensionFilter("log", "log"));
        int result 				= chooser.showOpenDialog(null);

        UIManager.setLookAndFeel(BusinessViewMain.laf);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] file = chooser.getSelectedFiles();
            
            new DialogSchemaView(file); 
        }
	}
}
