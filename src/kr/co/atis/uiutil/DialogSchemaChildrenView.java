package kr.co.atis.uiutil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UICache;
import com.matrixone.apps.framework.ui.UIMenu;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

/**
 * Menu -> Test -> View Children 
 * @author ihjang
 */
public class DialogSchemaChildrenView extends JFrame {
	private static boolean isShift			= false;
	private static boolean findDerivative = false;
	
	private static JTabbedPane tabPane;
	protected JTree m_tree;
	protected DefaultTreeModel m_model;
	protected IconCellRenderer m_renderer;
	protected IconCellEditor m_editor;
	// Tab Title List
	public static Map mTitleMap  = new HashMap(); // String , StringList
	
	public DialogSchemaChildrenView() {}

	/**
	 * @param ctx1 - Context
	 * @param sType - MQL Type
	 * @param sTitle - MQL Name
	 * @param isTopdown - true (Top-down), false (Bottom-up)
	 */
	public DialogSchemaChildrenView(Context ctx1, String sType, String sTitle) {
		try {
			int iWidth			= 900;
			int iHeight			= 700;
			String sResult		= "";
			String sTemp       = "";
	
	        try {
				sResult               = MqlUtil.mqlCommand(ctx1, new StringBuilder("print ").append(sType).append(" '").append(sTitle).append("' select name dump ").toString());
			} catch (Exception e) {
				try {
					sTemp  = PropertyUtil.getSchemaProperty(ctx1, sTitle);	// Schema Property Check
			        if(!"".equals(sTemp)) {
				        sTitle = sTemp;
				    }
					sResult               = MqlUtil.mqlCommand(ctx1, new StringBuilder("print ").append(sType).append(" '").append(sTitle).append("' select name dump ").toString());
			    } catch (Exception ee) {
	        	   JOptionPane.showMessageDialog(null, e.getMessage());
		            return;
				}
	        }
	        
	        if(sType.equalsIgnoreCase("type"))  findDerivative = true;  // Derivative View Check
	        DefaultMutableTreeNode top = new DefaultMutableTreeNode(settingTreeIcon(sType, sTitle));
	        settingModelMenuCommand(ctx1, sType, sTitle, top);
	        
	        m_model = new DefaultTreeModel(top);
			m_tree = new JTree(m_model);
			m_tree.setSize(50, 50);
			m_tree.setRowHeight(20);

			
			m_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			m_tree.setShowsRootHandles(true);
			m_tree.setEditable(true);

			m_renderer = new IconCellRenderer();
			m_tree.setCellRenderer(m_renderer);
			m_editor = new IconCellEditor(m_tree);
			m_tree.setCellEditor(m_editor);
			m_tree.setInvokesStopCellEditing(true);

			m_tree.addMouseListener(new TreeExpander());
			
			tabPane = new JTabbedPaneCustom();
			tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			//tabPane.setBackground(new Color(255, 255, 255, 50));
			
			tabPane.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent mouseEvent) {
					if (SwingUtilities.isMiddleMouseButton(mouseEvent) && mouseEvent.getClickCount() == 1) {
						tabPane.remove(tabPane.getSelectedIndex());
					}
				}
			});

	        tabPane.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {}
				@Override
				public void keyReleased(KeyEvent e) {}
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.isShiftDown() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W){
						tabPane.removeAll();
					} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W){
						tabPane.remove(tabPane.getSelectedIndex());
					} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_PAGE_UP) {	// Ctrl + <- ==> Left Panel
						if(tabPane.getSelectedIndex() > 0)
							tabPane.setSelectedIndex(tabPane.getSelectedIndex() - 1);
					} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {	// Ctrl + -> ==> Right Panel
						if(tabPane.getComponentCount() > tabPane.getSelectedIndex() + 1)
							tabPane.setSelectedIndex(tabPane.getSelectedIndex() + 1);
					}
				}
			});
	        
			
			JScrollPane scrollPane 	= new JScrollPane(m_tree);
			scrollPane.setViewportView(m_tree);
			scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5)); // 경계 설정
			scrollPane.setBackground(Color.WHITE);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //가로바정책
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			JPanel contentPane		= new JPanel();
	        contentPane.setBackground(Color.WHITE);
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 0));
			contentPane.setLayout(new BorderLayout(1, 1));
			contentPane.add(tabPane, BorderLayout.CENTER);
			
			JSplitPane paneSplit 	= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			paneSplit.setContinuousLayout(true);
			paneSplit.setLeftComponent(scrollPane);
			paneSplit.setRightComponent(contentPane);
			paneSplit.setDividerLocation(300);      
			paneSplit.setDividerSize(2);
			
			setTitle(sTitle + " | Children View");
			setContentPane(paneSplit);
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        setSize(iWidth, iHeight);	// Popup Size
	        setVisible(true);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void settingModelMenuCommand(Context ctx1, String sType, String sTitle, DefaultMutableTreeNode node) {
		UIMenu uiMenuBean 		= new UIMenu();
		HashMap mInformation	= new HashMap();
		MapList childrenList	= new MapList();
		
		if(sType.equalsIgnoreCase(SchemaConstants.MENU)) {
			mInformation 	= uiMenuBean.getMenu(ctx1, sTitle);
			childrenList 	= (MapList) mInformation.get("children");
			
		} else if(sType.equalsIgnoreCase(SchemaConstants.COMMAND)) {	// Command
			mInformation 	= uiMenuBean.getCommand(ctx1, sTitle);
			String sHref	= StringUtils.defaultIfEmpty((String) mInformation.get(SchemaConstants.HREF) , "");
			settingModelCommandHref(ctx1, sHref, node);
			
		} else if(sType.equalsIgnoreCase(SchemaConstants.PORTAL)) {
			mInformation	= UICache.getPortal(ctx1, sTitle);
			MapList mChannelRows	= (MapList) mInformation.get("channel_rows");
			int iRowSize		= mChannelRows.size();
            for(int i = 0; i < iRowSize; i++)	// Channel Rows (each)
            {
            	Map mChannelRow	= (Map) mChannelRows.get(i);
            	childrenList.addAll((MapList) mChannelRow.get("channels"));
            }
            
		} else if(sType.equalsIgnoreCase(SchemaConstants.CHANNEL)) {
			mInformation	= UICache.getChannel(ctx1, sTitle);
			childrenList 	= (MapList) mInformation.get("commands");
			
		} else if(sType.equalsIgnoreCase(SchemaConstants.TYPE)) {
			settingModelAttributeList(ctx1, sTitle, node);	// Only Attribute
			if(findDerivative)
			    settingModelDerivativeType(ctx1, sTitle, node); // Child Type
		}

        if( childrenList != null && childrenList.size() > 0 )
        {
        	Iterator childrenItr = childrenList.iterator();
        	while(childrenItr.hasNext())
        	{
        		Map childMap = (Map)childrenItr.next();
        		String name = (String)childMap.get("name");
        		String type = (String)childMap.get("type");

        		DefaultMutableTreeNode child = new DefaultMutableTreeNode(settingTreeIcon(type, name));
        		node.add(child);
        		settingModelMenuCommand(ctx1, type, name, child);
        	}
        }
	}
	
	/**
	 * Parse Href
	 * @param ctx1
	 * @param sHref
	 * @param node
	 */
	private static void settingModelCommandHref(Context ctx1, String sHref, DefaultMutableTreeNode node) {
		if(!sHref.contains("?"))
			return;
		
		Map mViewOption			= SchemaConstants.getViewOptionChange();
		StringList slViewList	= SchemaConstants.getViewOption();
		StringList slParseList	= new StringList();
		StringList slHostList 	= FrameworkUtil.split(sHref, "?");
		slParseList.addAll(FrameworkUtil.split((String) slHostList.get(1), "&"));
		
		int iSize	= slParseList.size();
		for(int i = 0; i < iSize; i++)
		{
			StringList sList	= FrameworkUtil.split((String) slParseList.get(i), "=");
			if(sList.size() > 0) {
                String sKey			= (String) sList.get(0);
			    String sVal			= (String) sList.get(1);
			
			    if(slViewList.contains(sKey))
			    {
				    sKey			= (String) mViewOption.get(sKey);
				    DefaultMutableTreeNode child = new DefaultMutableTreeNode(settingTreeIcon(sKey, sVal));
            		node.add(child);
            		settingModelMenuCommand(ctx1, sKey, sVal, child);
                }
			}
		}
	}
	
	/**
	 * Type child -> Attribute List
	 * @param ctx1
	 * @param sName
	 * @param node
	 */
	private static void settingModelAttributeList(Context ctx1, String sName, DefaultMutableTreeNode node) {
		try {
			String sAttrList = MqlUtil.mqlCommand(ctx1, new StringBuilder("print type '").append(sName).append("' select immediateattribute dump ").toString());
			
			StringList slAttrList	= FrameworkUtil.split(sAttrList, ",");
			slAttrList.sort();
			
			int iSize	= slAttrList.size();
			for(int i = 0; i < iSize; i++)
			{
				String sChildName	= (String) slAttrList.get(i);
				node.add(new DefaultMutableTreeNode(settingTreeIcon(SchemaConstants.ATTRIBUTE, sChildName)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Type child -> derivative(immediate) List
	 * @param ctx1
	 * @param sName
	 * @param node
	 */
	private static void settingModelDerivativeType(Context ctx1, String sName, DefaultMutableTreeNode node) {
		try {
			String sDerivativeList= MqlUtil.mqlCommand(ctx1, new StringBuilder("print type '").append(sName).append("' select immediatederivative dump ").toString());
			
			StringList slTypeList	= FrameworkUtil.split(sDerivativeList, ",");
			slTypeList.sort();
			
			int iSize	= slTypeList.size();
			for(int i = 0; i < iSize; i++)
			{
				String sChildName	= (String) slTypeList.get(i);
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(settingTreeIcon(SchemaConstants.TYPE, sChildName));
				node.add(child);
				settingModelAttributeList(ctx1, sChildName, child);
				settingModelDerivativeType(ctx1, sChildName, child);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Setting Tree Icon
	 * @param sType
	 * @param sName
	 * @return
	 */
	private static IconData settingTreeIcon(String sType, String sName) {
		IconData icon = null;
		sType			= sType.toLowerCase();
		sName			= new StringBuilder("<html><p ").append(sType).append("=").append(sName).append(" style='padding: 10px;'>").append(sName).append("</p>").toString();
		Map mIconMap	= SchemaConstants.iconImageForList();
    	icon 			= new IconData((ImageIcon) mIconMap.get(sType), sName);
    	
		return icon;
		
	}
	
	class TreeExpander extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {	// Double Click
				TreePath selPath = m_tree.getPathForLocation(e.getX(), e.getY());
				if (selPath == null)
					return;
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) (selPath.getLastPathComponent());
//				if (node != null && addAncestors(node)) {
				if (node != null) {
					String sNode	= node.toString();
					sNode			= sNode.replaceAll("<html><p ", "");
					sNode			= sNode.substring(0, sNode.indexOf(" style"));
					String[] args	= sNode.split("=");

					boolean isAdd	= true;
					int existIndex	= 0;
					int iSize	= tabPane.getTabCount();
					for(int i = 0; i < iSize; i++)
					{
						ButtonTabComponent btc = (ButtonTabComponent) tabPane.getTabComponentAt(i);
						String sType    = btc.getType();
						String sTitle	= tabPane.getTitleAt(i);
						if(sTitle.equals(args[1]) && sType.equals(args[0]))
						{
							isAdd		= false;
							existIndex	= i;
						}
					}
					
					try {
						if(isAdd) {
							StringList slList   = new StringList();
							if(mTitleMap.containsKey(args[1])) {
								slList.addAll((StringList) mTitleMap.get(args[1]));
								slList.add(args[0]);
							} else {
								slList.add(args[0]);
						    }
						    mTitleMap.put(args[1], slList);
						
							String sResult	= SchemaUtil.getSchema(BusinessViewMain.ctx1, args);
							
							Border border 		= BorderFactory.createTitledBorder("");
							JTextArea txtArea 	= new JTextArea();
							txtArea.setLineWrap(false); //한줄이 너무 길면 자동으로 개행할지 설정
							txtArea.setColumns(50); //열의 크기(가로크기)
							txtArea.setRows(10); //행의 크기(세로크기)
							txtArea.setEditable(false);
							
							txtArea.setText(sResult);
							txtArea.getCaret().setVisible(true);
					        TextLineNumber tln		= new TextLineNumber(txtArea);
							JScrollPane scrollPane	= new JScrollPane(txtArea);
							scrollPane.setViewportView(txtArea);
							scrollPane.setBorder(border); // 경계 설정
							scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //가로바정책
							scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
							scrollPane.setRowHeaderView( tln );
							scrollPane.setBackground(new Color(0, 84, 255, 80));
							
							tabPane.addTab(args[1], scrollPane);
							tabPane.setTabComponentAt(iSize, new ButtonTabComponent(tabPane, args[0], true, mTitleMap));
							
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
					        		
					        		if(e.isShiftDown() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W){	// Ctrl + Shift + W ==> All Tab Close
					                    mTitleMap.clear();
					                    tabPane.removeAll();
					        		} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W){				// Ctrl + W ==> Active Tab Close
					                    removeTab();
					        		} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_PAGE_UP) {	// Ctrl + Page Up ==> Left Panel
					        			if(tabPane.getSelectedIndex() > 0)
					        				tabPane.setSelectedIndex(tabPane.getSelectedIndex() - 1);
					        		} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {	// Ctrl + Page Down ==> Right Panel
					        			if(tabPane.getTabCount() > tabPane.getSelectedIndex() + 1)
					        				tabPane.setSelectedIndex(tabPane.getSelectedIndex() + 1);
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
					        tabPane.setSelectedIndex(iSize);
						} else {
							tabPane.setSelectedIndex(existIndex);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					m_tree.expandPath(selPath);
					m_tree.repaint();
				}
			}
		}
	}
	
	public static void removeTab() {
		ButtonTabComponent btc = (ButtonTabComponent) tabPane.getTabComponentAt(tabPane.getSelectedIndex());
		String sType = btc.getType();
		String sName = tabPane.getTitleAt(tabPane.getSelectedIndex());
		StringList slList = (StringList) mTitleMap.get(sName);
		slList.remove(sType);
		mTitleMap.put(sName, slList);
		tabPane.remove(tabPane.getSelectedIndex());
	}
}



class IconCellRenderer extends JLabel implements TreeCellRenderer {
	protected Color m_textSelectionColor;
	protected Color m_textNonSelectionColor;
	protected Color m_bkSelectionColor;
	protected Color m_bkNonSelectionColor;
	protected Color m_borderSelectionColor;

	protected boolean m_selected;

	public IconCellRenderer() {
		super();
		m_textSelectionColor = UIManager.getColor("Tree.selectionForeground");
		m_textNonSelectionColor = UIManager.getColor("Tree.textForeground");
		m_bkSelectionColor = UIManager.getColor("Tree.selectionBackground");
		m_bkNonSelectionColor = UIManager.getColor("Tree.textBackground");
		m_borderSelectionColor = UIManager.getColor("Tree.selectionBorderColor");
		setOpaque(false);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus)

	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object obj = node.getUserObject();
		setText(obj.toString());
		if (obj instanceof IconData) {
			IconData idata = (IconData) obj;
			if (expanded)
				setIcon(idata.getOpenIcon());
			else
				setIcon(idata.getIcon());
		} else
			setIcon(null);

		setFont(tree.getFont());
		setForeground(sel ? m_textSelectionColor : m_textNonSelectionColor);
		setBackground(sel ? m_bkSelectionColor : m_bkNonSelectionColor);
		m_selected = sel;
		return this;
	}

	public void paint(Graphics g) {
		Color bColor = getBackground();
		Icon icon = getIcon();

		g.setColor(bColor);
		int offset = 0;
		if (icon != null && getText() != null)
			offset = (icon.getIconWidth() + getIconTextGap());
		g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);

		if (m_selected) {
			g.setColor(m_borderSelectionColor);
			g.drawRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
		}

		super.paint(g);
	}
}



class IconData {
	protected Icon m_icon;
	protected Icon m_openIcon;
	protected Object m_data;

	public IconData(Icon icon, Object data) {
		m_icon = icon;
		m_openIcon = null;
		m_data = data;
	}

	public IconData(Icon icon, Icon openIcon, Object data) {
		m_icon = icon;
		m_openIcon = openIcon;
		m_data = data;
	}

	public Icon getIcon() {
		return m_icon;
	}

	public Icon getOpenIcon() {
		return m_openIcon != null ? m_openIcon : m_icon;
	}

	public Object getObject() {
		return m_data;
	}

	public String toString() {
		return m_data.toString();
	}
}


class IconCellEditor extends JLabel implements TreeCellEditor, ActionListener {
	protected JTree m_tree = null;
	protected JTextField m_editor = null;
	protected IconData m_item = null;
	protected int m_lastRow = -1;
	protected long m_lastClick = 0;
	protected Vector m_listeners = null;

	public IconCellEditor(JTree tree) {
		super();
		m_tree = tree;
		m_listeners = new Vector();
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();
			if (obj instanceof IconData) {
				IconData idata = (IconData) obj;
				m_item = idata;
				// Reserve some more space...
				setText(idata.toString() + "     ");
				setIcon(idata.m_icon);
				setFont(tree.getFont());
				return this;
			}
		}
		// We don't support other objects...
		return null;
	}

	public Object getCellEditorValue() {
		if (m_item != null && m_editor != null)
			m_item.m_data = m_editor.getText();
		return m_item;
	}

	public boolean isCellEditable(EventObject evt) {
		if (evt instanceof MouseEvent) {
			MouseEvent mEvt = (MouseEvent) evt;
			if (mEvt.getClickCount() == 1) {
				int row = m_tree.getRowForLocation(mEvt.getX(), mEvt.getY());
				if (row != m_lastRow) {
					m_lastRow = row;
					m_lastClick = System.currentTimeMillis();
					return false;
				} else if (System.currentTimeMillis() - m_lastClick > 1000) {
					m_lastRow = -1;
					m_lastClick = 0;
					prepareEditor();
					mEvt.consume();
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	protected void prepareEditor() {
		if (m_item == null)
			return;
		String str = m_item.toString();

		m_editor = new JTextField(str);
		m_editor.addActionListener(this);
		m_editor.selectAll();
		m_editor.setFont(m_tree.getFont());

		add(m_editor);
		revalidate();

		TreePath path = m_tree.getPathForRow(m_lastRow);
		m_tree.startEditingAtPath(path);
	}

	protected void removeEditor() {
		if (m_editor != null) {
			remove(m_editor);
			m_editor.setVisible(false);
			m_editor = null;
			m_item = null;
		}
	}

	public void doLayout() {
		super.doLayout();
		if (m_editor != null) {
			int offset = getIconTextGap();
			if (getIcon() != null)
				offset += getIcon().getIconWidth();
			Dimension cSize = getSize();
			m_editor.setBounds(offset, 0, cSize.width - offset, cSize.height);
		}
	}

	public boolean shouldSelectCell(EventObject evt) {
		return true;
	}

	public boolean stopCellEditing() {
		if (null != m_item && null != m_editor)
			m_item.m_data = m_editor.getText();

		ChangeEvent e = new ChangeEvent(this);
		for (int k = 0; k < m_listeners.size(); k++) {
			CellEditorListener l = (CellEditorListener) m_listeners.elementAt(k);
			l.editingStopped(e);
		}

		removeEditor();
		return true;
	}

	public void cancelCellEditing() {
		ChangeEvent e = new ChangeEvent(this);
		for (int k = 0; k < m_listeners.size(); k++) {
			CellEditorListener l = (CellEditorListener) m_listeners.elementAt(k);
			l.editingCanceled(e);
		}

		removeEditor();
	}

	public void addCellEditorListener(CellEditorListener l) {
		m_listeners.addElement(l);
	}

	public void removeCellEditorListener(CellEditorListener l) {
		m_listeners.removeElement(l);
	}

	public void actionPerformed(ActionEvent e) {
		stopCellEditing();
		m_tree.stopEditing();
	}
}


