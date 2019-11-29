package kr.co.atis.util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;

import kr.co.atis.main.BusinessViewMain;
import matrix.db.Context;
import matrix.util.StringList;

public class SchemaExport {
	
	public static String exportList(HSSFWorkbook workbook) {
		return exportList(workbook, "");
	}
	public static String exportList(HSSFWorkbook workbook, String savePath) {
		String excelFilePath			= "";
		try {
			if(workbook != null) {
				// Excel Export - Server [B]
				SimpleDateFormat sdf	= new SimpleDateFormat(SchemaConstants.FORMAT_EXPORT);
				String tempWorkspace 	= SchemaConstants.SAVE_DIR;
				String excelFileName 	= new StringBuilder(SchemaConstants.SAVE_FILE_PREFIX).append(sdf.format(new Date())).append(SchemaConstants.SAVE_FILE_EXTENSION).toString();
				
				if(!"".equals(savePath)) {
					tempWorkspace		= FilenameUtils.getFullPath(savePath);
					excelFileName		= FilenameUtils.getBaseName(savePath) + SchemaConstants.SAVE_FILE_EXTENSION;
				}
					
				excelFilePath 			= (tempWorkspace + excelFileName).replace("\\", "/");
				File tempFolder			= new File(tempWorkspace);	// Create Folder
				if(!tempFolder.exists())
					tempFolder.mkdirs();
				
				FileOutputStream fileOut = new FileOutputStream(excelFilePath); 
				//write this workbook to an Outputstream. 
				workbook.write(fileOut);
				fileOut.flush(); 
				fileOut.close();
				System.err.println("Export Complete");
			}
  		} catch (Exception e) {
  			e.printStackTrace();
		}
		
		return excelFilePath;
	}
	
	public static void exportExcelChooser(HSSFWorkbook workbook) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
        JFileChooser chooser 	= new JFileChooser();
        int result 				= chooser.showSaveDialog(null);
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        String sExportName		= "";
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            sExportName			= SchemaExport.exportList(workbook, file.toString());
            sExportName			= sExportName.substring(0, sExportName.lastIndexOf("/"));
            
			int response = JOptionPane.showConfirmDialog(null, "폴더를 여시겠습니까?\n다운로드 폴더 : " + sExportName, "알림", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if (response == JOptionPane.YES_OPTION) {
				Desktop desktop = Desktop.getDesktop();        
	            File dirToOpen = new File(sExportName);
	            try {
	            	desktop.open(dirToOpen);
	                System.out.println("open");        
	            } catch (Exception E) {        
	                System.out.println("File Not Found");        
	            }
			}
        }
        UIManager.setLookAndFeel(BusinessViewMain.laf);
	}
	
	
	public static String exportExcel(Context ctx1, Context ctx2, HSSFWorkbook workbook, String sType, StringList slExportList, String sFlag) throws Exception {
		StringBuilder sbSchema			= new StringBuilder();
		try {
			int iExportListSize			= slExportList.size();
			StringList slExportParam	= new StringList();
			for(int i = 0; i < iExportListSize; i++)
			{
				slExportParam.clear();
				slExportParam.add(sType);
				slExportParam.addAll(FrameworkUtil.split((String) slExportList.get(i), "|"));
				slExportParam.add(sFlag);
				if(SchemaConstants.ADD_FLAG.equals(sFlag)) {
					sbSchema.append(SchemaUtil.getSchema(ctx1, (String[]) slExportParam.toArray(new String[slExportParam.size()])));
					
				} else if(SchemaConstants.MOD_FLAG.equals(sFlag)) {
					sbSchema.append(SchemaUtil.getSchemaModify(ctx1, ctx2, (String[]) slExportParam.toArray(new String[slExportParam.size()])));
				}
				sbSchema.append("\n\n\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return sbSchema.toString();
	}
	
	public static void exportExcelSettingData(String sType, String sFlag, HSSFWorkbook workbook, String sSchema) throws Exception {
		HSSFSheet sheet 		= null;
		HSSFRow row 			= null;
		
		////////////////////////////////////////////////
		// 1. Create Workbook, Sheet, Style
		////////////////////////////////////////////////
		StringBuilder sbSheetName	= new StringBuilder();
		sbSheetName.append(sType).append("_").append(sFlag);
		sheet 					= workbook.createSheet(sbSheetName.toString());
		// Style
		HSSFFont font			= workbook.createFont();
		font.setFontHeightInPoints((short) 12);	// Font Size
		HSSFCellStyle style		= workbook.createCellStyle();
		style.setFont(font);
		
		StringList slSchema			= FrameworkUtil.split(sSchema, "\n");
		int iSchemaSize				= slSchema.size();
		for(int i = 0; i < iSchemaSize; i++)
		{
			row = sheet.createRow(i);
			setCellData(row, (String) slSchema.get(i), 	0, 	style);
		}
		
		sheet.autoSizeColumn(0);
	}
	
	private static void setCellData(HSSFRow row, String value, int index, HSSFCellStyle style) throws Exception
	{
		HSSFCell cell = null;
		cell = row.createCell(index);	
		cell.setCellValue(value);	
		cell.setCellStyle(style);
	}
	
	
	
	
	/****************************************************************************************************/
	/*********** Export MQL File (*.mql) ****************************************************************/
	/****************************************************************************************************/
	public static void exportMQLFile(String sType) {
		try {
			Context context		= BusinessViewMain.ctx1;
			LookAndFeel laf		= UIManager.getLookAndFeel();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			String sFindMQL		= JOptionPane.showInputDialog(null, "Execute " + sType + " Name 입력.\nex) *, ATSMenu*");	// Input MQL Name
			if(null == sFindMQL)
				return;
			
			sFindMQL			= sFindMQL.trim();
			int response 		= JOptionPane.showConfirmDialog(null, sFindMQL + " 를 Export 하시겠습니까?", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if(sFindMQL.startsWith("*"))
				sFindMQL		= sFindMQL.substring(1);

			if(sFindMQL.endsWith("*"))
				sFindMQL		= sFindMQL.substring(0, sFindMQL.length() - 1);
			
			if (response == JOptionPane.YES_OPTION) {
	    		String sResult		= "";
	    		StringList slData	= new StringList();
	    		
	    		StringBuilder sb 	= new StringBuilder();
	    		sb.append("list ").append(sType);
	    		if(sType.equalsIgnoreCase("table")) {
	    			sb.append(" system");
	    		}
	    		sb.append(" '").append(sFindMQL).append("*'");
	    		
				if(sType.equals(SchemaConstants.TRIGGER) || sType.equals(SchemaConstants.GENERATOR) || !BusinessViewMain.slMQLTypeList.contains(sType)) {
					sb.delete(0, sb.length());
					sb.append("temp query bus '").append(sType).append("' '").append(sFindMQL).append("*' * select dump '|'");
					sResult					= MqlUtil.mqlCommand(context, sb.toString());
					sResult					= sResult.replaceAll(sType+"\\|", "");
				} else {
					sResult					= MqlUtil.mqlCommand(context, sb.toString());
				}
				slData						= FrameworkUtil.split(sResult, "\n");
				
				int iListSize				= slData.size();
				StringList slDupChkList		= new StringList();
				for(int i = 0; i < iListSize; i++)
				{
					String sInfo			= (String) slData.get(i);
					StringList slInfo		= new StringList();
	    			slInfo					= FrameworkUtil.split(sInfo, "|");
	    			slInfo.add(0, sType);
	    			
	    			if(sType.equals(SchemaConstants.TRIGGER) || sType.equals(SchemaConstants.GENERATOR) || !BusinessViewMain.slMQLTypeList.contains(sType)) {
    					slInfo.add(SchemaConstants.ADD_FLAG);
	    			}
	    			
	    			String[] args			= (String[]) slInfo.toArray(new String[slInfo.size()]);
	    			String sTempName		= args[1];
	    			sResult					= SchemaUtil.getSchema(context, args);
	    			
	    			if(sType.equals(SchemaConstants.TRIGGER) || sType.equals(SchemaConstants.GENERATOR) || !BusinessViewMain.slMQLTypeList.contains(sType)) {
	    				sTempName			= args[0];
	    			}
	    			
					saveFile(sType, sTempName, sResult, SchemaConstants.SAVE_FILE_EXTENSION2, slDupChkList.contains(sTempName));
					if(!slDupChkList.contains(sTempName)) slDupChkList.add(sTempName);
				}
				
				response = JOptionPane.showConfirmDialog(null, "폴더를 여시겠습니까?\n다운로드 폴더 : C:/temp/" + sType + "/", "알림", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				UIManager.setLookAndFeel(laf);
				
				if (response == JOptionPane.YES_OPTION) {
					Desktop desktop = Desktop.getDesktop();        
		            File dirToOpen = new File("C:\\temp\\" + sType + "\\");
		            try {
		            	desktop.open(dirToOpen);
		                System.out.println("open");        
		            } catch (Exception E) {        
		                System.out.println("File Not Found");        
		            }
				}
				
				SchemaLogs.writeLogFile("INFO  ", "Execute MQL [" + sType + " : " + sFindMQL + "*]");	// [LOG]
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save file (*.mql)
	 * @param sType - attribute, type, policy...
	 * @param sName - mql name - ex) *, ATSMenu*, ...
	 * @param sb - MQL String
	 * @param suffix - extention
	 * @param isAppend - MQL String append to MQL Document
	 * @throws IOException
	 */
	private static void saveFile(String sType, String sName, String sb, String suffix, boolean isAppend) throws IOException {
		StringBuilder sbMQL			= new StringBuilder();
		
		String documentDirectory 	= SchemaConstants.SAVE_DIR + sType + File.separator;
		FileWriter statusLog 		= null;
		
		File folder 				= new File(documentDirectory);
		boolean bFolderExists		= folder.exists();	// Folder Check
		if (!bFolderExists) {
			boolean bCreated		= folder.mkdirs();
		}
		File oldFile 				= new File(documentDirectory + sName + suffix);
		boolean bExists 			= oldFile.exists();	// File Check 
		if (bExists && !isAppend) {	// first time
			boolean bDelete 		= oldFile.delete();
		} else if(bExists && isAppend) {
			sbMQL.append(SchemaConstants.RECORDSEP).append(SchemaConstants.RECORDSEP).append(SchemaConstants.RECORDSEP);
		}
		
		sbMQL.append(sb);

		String outFilePath = documentDirectory + sName + suffix;
		System.out.println("Save File Path : " + outFilePath);
		statusLog = new FileWriter(outFilePath, true);

		statusLog.flush();
		statusLog.write(sbMQL.toString());
		statusLog.flush();
		statusLog.close();
	}
}
