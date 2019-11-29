package kr.co.atis.uiutil;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PropertyUtil;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

/**
 * New Dialog (MQL Popup Dialog)
 * @author ihjang
 *
 */
public class DialogSchemaView extends JFrame{
	private static boolean isShift			= false;
	private static int iWidth	= 800;
	private static int iHeight	= 600;
	
	public static JTabbedPane tabPane	= new JTabbedPaneCustom();
	
	public DialogSchemaView() {}
	
	/**
	 * Schema View
	 * @param ctx1
	 * @param sType
	 * @param sTitle
	 */
	public DialogSchemaView(Context ctx1, String sType, String sTitle) {
		try {
			System.err.println("Schema View");
			String sResult		= "";
			String sTemp		= PropertyUtil.getSchemaProperty(ctx1, sTitle);	// Schema Property Check
			if(!"".equals(sTemp))
				sTitle			= sTemp;
			
			StringList slInfo	= new StringList();
			slInfo				= FrameworkUtil.split(sTitle, "|");
			slInfo.add(0, sType);
			if(sType.equals(SchemaConstants.OBJECT)) {
					slInfo.clear();
					StringList slBusSelect = new StringList();
					slBusSelect.add(SchemaConstants.SELECT_TYPE);
					slBusSelect.add(SchemaConstants.SELECT_NAME);
					slBusSelect.add(SchemaConstants.SELECT_REVISION);
					DomainObject dmObj = DomainObject.newInstance(ctx1, sTitle);
					Map mInfo  = dmObj.getInfo(ctx1, slBusSelect);
					slInfo.add((String) mInfo.get(SchemaConstants.SELECT_TYPE));
					slInfo.add((String) mInfo.get(SchemaConstants.SELECT_NAME));
					slInfo.add((String) mInfo.get(SchemaConstants.SELECT_REVISION));
					slInfo.add(SchemaConstants.ADD_FLAG);
					
			} else if(sType.equals(SchemaConstants.TRIGGER) || sType.equals(SchemaConstants.GENERATOR) || !BusinessViewMain.slMQLTypeList.contains(sType)) {
				slInfo.add(SchemaConstants.ADD_FLAG);
			}
			String[] args			= (String[]) slInfo.toArray(new String[slInfo.size()]);
			sResult					= SchemaUtil.getSchema(ctx1, args);
			/*********************************************************/
			BusinessViewMain.initUIManager();
			
			setTitle(sTitle);
			setBackground(Color.WHITE);
			
			JTabbedPane tabPane	= new JTabbedPaneCustom();
			tabPane.addTab(sTitle, DialogSchemaViewCommon(this, sType, sTitle, sResult));
			tabPane.setTabComponentAt(0, new ButtonTabComponent(tabPane, sType, false, new HashMap()));
			
			setContentPane(tabPane);
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        setSize(iWidth, iHeight);	// Popup Size
	        setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Logs View
	 * @param sTitle
	 * @param sFile
	 */
	//public DialogSchemaView(String sTitle, String sFile) {
	public DialogSchemaView(File[] files) {
		try {
			System.err.println("Log View");
			JTabbedPane tabPane	= new JTabbedPaneCustom();
			tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			for(int i = 0; i < files.length; i++) {
				File file		= files[i];
				String sTitle	= file.getName();
				String sFile	= file.getPath();
				
				BufferedReader inputFile = new BufferedReader(new FileReader(sFile));
				String line;
				StringBuilder sbLine	= new StringBuilder();
				while ((line = inputFile.readLine()) != null)
				{
					sbLine.append( line ).append( "\n" );
				}
				inputFile.close();
				String sResult	= sbLine.toString();
				
				
				tabPane.addTab(sTitle, DialogSchemaViewCommon(this, "LOG", sTitle, sResult));
				tabPane.setTabComponentAt(0, new ButtonTabComponent(tabPane, "LOG", false, new HashMap()));
			}
			/*********************************************************/
			BusinessViewMain.initUIManager();
			
			setTitle("LOG");
			setBackground(Color.WHITE);
			
			setContentPane(tabPane);
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        setSize(iWidth, iHeight);	// Popup Size
	        setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static JScrollPane DialogSchemaViewCommon(final JFrame frame, String sType, String sTitle, String sResult) throws Exception {
		try {
			JTextArea txtArea		= new JTextArea();
			txtArea.setLineWrap(false);	// 한줄이 너무 길면 자동으로 개행할지 설정
			txtArea.setColumns(50);	// 열의 크기(가로)
			txtArea.setRows(10);		// 행의 크기(세로)
			txtArea.setEditable(false);
			txtArea.setText(sResult);
			txtArea.getCaret().setVisible(true);
	        TextLineNumber tln		= new TextLineNumber(txtArea);
			JScrollPane scrollPane	= new JScrollPane(txtArea);
			scrollPane.setViewportView(txtArea);
			//scrollPane.setBorder(border); // 경계 설정
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //가로바정책
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setRowHeaderView( tln );
			scrollPane.setBackground(Color.WHITE);
				
			// Key Event, Mouse Event use (BusinessViewMain, DialogSchemaCompare, DialogSchemaView)
			txtArea.addKeyListener(new KeyAdapter() {
				@Override
	        	public void keyReleased(KeyEvent e) {
	    			isShift	= false;
	        	}
				
				@Override
				public void keyPressed(KeyEvent e) {
	        		if(e.isShiftDown()) {
	        			isShift	= true;
	        		}
	        		
					if(sType.equals("LOG")) {
						if(e.isShiftDown() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W) {	// Ctrl + Shift + W
							frame.dispose();
						} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W) {	// Ctrl+ W
							if(tabPane.getTabCount() == 1) {
								frame.dispose();
							} else {
								tabPane.remove(tabPane.getSelectedIndex());
							}
						} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_PAGE_UP) { // Ctrl + pageUp
							if(tabPane.getSelectedIndex() > 0)
								tabPane.setSelectedIndex(tabPane.getSelectedIndex() - 1);
						} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {	// Ctrl + pageDown
							if(tabPane.getTabCount() > tabPane.getSelectedIndex() + 1)
								tabPane.setSelectedIndex(tabPane.getSelectedIndex() + 1);
						}
					}
					
	        		// Highlighter
	        		HighlightTextAreaLine.tAreaHighlighterKeyCheck(e, txtArea);
				}
			});
	        txtArea.addMouseListener(new MouseAdapter() {
	        	@Override
	        	public void mousePressed(MouseEvent evt) {
	        		if(!isShift)
	        			HighlightTextAreaLine.tAreaHighlighter(txtArea);
	        	}
	        });
	        
	        txtArea.setCaretPosition(0);
	
			return scrollPane;
		} catch (Exception e) {
			throw e;
		}
	}
}
