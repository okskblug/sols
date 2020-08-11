package kr.co.atis.uiutil;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import com.matrixone.apps.domain.util.FrameworkUtil;

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
public class DialogSchemaCompare extends JFrame{
	private static boolean isShift			= false;
	
	public DialogSchemaCompare() {}
	
	public DialogSchemaCompare(Context ctx1, Context ctx2, String sType, String sTitle, boolean isDefault) {
		try {
			int iWidth			= 800;
			int iHeight			= 600;
			String sResult1		= "";
			String sResult2		= "";
			String sHeader		= new StringBuilder(sTitle).append("_").append((isDefault) ? "DEFAULT" : "COMPARE").toString();
	
	        setTitle(sHeader);
			StringList slInfo	= new StringList();
			slInfo				= FrameworkUtil.split(sTitle, "|");
			slInfo.add(0, sType);
			if(sType.equals(SchemaConstants.TRIGGER) || sType.equals(SchemaConstants.GENERATOR) || !BusinessViewMain.slMQLTypeList.contains(sType)) {
				slInfo.add(SchemaConstants.ADD_FLAG);
			}
			String[] args				= (String[]) slInfo.toArray(new String[slInfo.size()]);
			sResult1					= SchemaUtil.getSchema(ctx1, args);
			sResult2					= SchemaUtil.getSchema(ctx2, args);
			Map mReturnMap				= TextLineCompare(sType, sResult1, sResult2);
			
			//StringList slLineList1		= (StringList) mReturnMap.get("DEFAULT");
			//StringList slLineList2		= (StringList) mReturnMap.get("COMPARE");
			JScrollPane scrollPane1		= DialogSchemaView.DialogSchemaViewCommon(new JFrame(), sType, sTitle, sResult1);
			JScrollPane scrollPane2		= DialogSchemaView.DialogSchemaViewCommon(new JFrame(), sType, sTitle, sResult2);
			
			JTabbedPane tabPane1 = new JTabbedPaneCustom();
			JTabbedPane tabPane2 = new JTabbedPaneCustom();
			tabPane1.addTab("Default", scrollPane1);
			tabPane2.addTab("Compare", scrollPane2);
			tabPane1.setTabComponentAt(0, new ButtonTabComponent(tabPane1, sType, false, new HashMap()));
			tabPane2.setTabComponentAt(0, new ButtonTabComponent(tabPane2, sType, false, new HashMap()));

			JSplitPane paneMainSplit 	= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			paneMainSplit.setContinuousLayout(true);
			paneMainSplit.setLeftComponent(tabPane1);
			paneMainSplit.setRightComponent(tabPane2);
			paneMainSplit.setDividerSize(2);
			
			setContentPane(paneMainSplit);
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        setSize(iWidth, iHeight);	// Popup Size
	        setExtendedState(JFrame.MAXIMIZED_BOTH);
	        setVisible(true);
	        setTitle("Compare View");

	        paneMainSplit.setDividerLocation((paneMainSplit.getWidth() / 2));

	        addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                	paneMainSplit.setDividerLocation((paneMainSplit.getWidth() / 2));
                }
            });
	        
			// Key Event, Mouse Event use (BusinessViewMain, DialogSchemaCompare, DialogSchemaView)
//	        txtArea1.addKeyListener(new KeyAdapter() {
//	        	@Override
//	        	public void keyReleased(KeyEvent e) {
//	    			isShift	= false;
//	        	}
//	        	
//				@Override
//				public void keyPressed(KeyEvent e) {
//	        		if(e.isShiftDown()) {
//	        			isShift	= true;
//	        		}
//	        		
//	        		// Highlighter
//	        		HighlightTextAreaLine.tAreaHighlighterKeyCheck(e, txtArea1);
//				}
//			});
//	        txtArea1.addMouseListener(new MouseAdapter() {
//				@Override
//				public void mousePressed(MouseEvent evt) {
//					if(!isShift)
//						HighlightTextAreaLine.tAreaHighlighter(txtArea1);
//				}
//			});
	        
//	        txtArea2.addKeyListener(new KeyAdapter() {
//	        	@Override
//	        	public void keyReleased(KeyEvent e) {
//	    			isShift	= false;
//	        	}
//	        	
//	        	@Override
//	        	public void keyPressed(KeyEvent e) {
//	        		if(e.isShiftDown()) {
//	        			isShift	= true;
//	        		}
//	        		
//	        		// Highlighter
//	        		HighlightTextAreaLine.tAreaHighlighterKeyCheck(e, txtArea2);
//	        	}
//	        });
//	        txtArea2.addMouseListener(new MouseAdapter() {
//	        	@Override
//	        	public void mousePressed(MouseEvent evt) {
//	        		if(!isShift)
//	        			HighlightTextAreaLine.tAreaHighlighter(txtArea2);
//	        	}
//	        });

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param sType
	 * @param sText1
	 * @param sText2
	 * @return
	 */
	private static Map TextLineCompare(String sType, String sText1, String sText2) {
		Map mReturnMap	= new HashMap();
		
		StringList slLineList1		= new StringList();
		StringList slLineList2		= new StringList();
		StringList slResult1		= FrameworkUtil.split(sText1, "\n");
		StringList slResult2		= FrameworkUtil.split(sText2, "\n");

		int iSize1					= slResult1.size();
		int iSize2					= slResult2.size();
//		boolean isSubFlag			= false;
//		boolean inSubArea			= false;
		
		int idx2					= 0;
		int idx1 					= 0;
		boolean isEnd				= false;
		for(idx1 = 0; idx1 < iSize1; idx1++)
		{
			String sResult1			= ((String) slResult1.get(idx1)).trim();
			String sResult2			= ((String) slResult2.get(idx2)).trim();
			
//			if(	   (sType.equals(SchemaConstants.POLICY) && sResult1.startsWith("state"))
//				|| (sType.equals(SchemaConstants.RELATIONSHIP) && (sResult1.startsWith("from") || sResult1.startsWith("to")))
//				|| (sType.equals(SchemaConstants.FORM) && sResult1.startsWith("field"))
//				|| (sType.equals(SchemaConstants.TABLE) && sResult1.startsWith("column"))) {
//				isSubFlag			= true;
//				inSubArea			= true;
//			} else {
//				isSubFlag			= false;
//			}
			
			// policy, relationship, form, table - property
			if(!sResult1.equals(sResult2))
			{
				for(int j = (idx2 + 1); j < iSize2; j++) 
				{
					sResult2		= ((String) slResult2.get(j)).trim();
					
					if(!sResult1.equals(sResult2)) 
					{
						slLineList2.add(""+j);
					}
				}
			}
		}
		
		
		/**
		 * 1) 같을 경우 Skip
		 * 
		 * 2) 다를 경우 다음 Row 와 같을 때 까지 비교
		 * 2-1) policy, relationship, form, table 이 sub item 을 기준으로 비교
		 * 2-2) sub item name 부터 다를 경우 break;
		 * 2-2) sub item 은 다음 sub item 이 나오거나, property 를 시작으로 하는 데이터가 나올 경우 break; 
		 * 2-3) 다음 Row 가 다를 경우 list2 에 add
		 * 2-4) 다음 Row 가 같을 경우 break
		 * 2-5) 끝까지 같은게 나오지 않을 경우. - list1 에 add
		 * 
		 * index 는 서로 다르게 진행.
		 */
//		 Main Compare - 1Tab
//		 Sub Compare - 2Tab 
//			Policy - state
//			relationship - [from,to]
//			form - field
//			Table - Column)
//		HighlightTextAreaLine.tAreaHighlighterCompare(txtArea1, );
		
//		System.err.println(slTempResult1);
//		System.err.println(slTempResult2);
		
		mReturnMap.put("DEFAULT", slLineList1);
		mReturnMap.put("COMPARE", slLineList2);
		
		return mReturnMap;
	}
}
