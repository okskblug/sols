package kr.co.atis.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalComboBoxButton;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;

import kr.co.atis.db.SchemaImport;
import kr.co.atis.uiutil.ButtonHideShowComponent;
import kr.co.atis.uiutil.ButtonTabComponent;
import kr.co.atis.uiutil.DialogSchemaChildrenView;
import kr.co.atis.uiutil.DialogSchemaCompare;
import kr.co.atis.uiutil.DialogSchemaView;
import kr.co.atis.uiutil.HighlightTextAreaLine;
import kr.co.atis.uiutil.IconListRenderer;
import kr.co.atis.uiutil.JMenuItemExport;
import kr.co.atis.uiutil.JTabbedPaneCustom;
import kr.co.atis.uiutil.ProgressBar;
import kr.co.atis.uiutil.RoundedPanel;
import kr.co.atis.uiutil.SteppedComboBox;
import kr.co.atis.uiutil.Switch;
import kr.co.atis.uiutil.TextLineNumber;
import kr.co.atis.uiutil.URLParseTable;
import kr.co.atis.util.SchemaAutoCompare;
import kr.co.atis.util.SchemaCompareMain;
import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaExport;
import kr.co.atis.util.SchemaLogs;
import kr.co.atis.util.SchemaProperties;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

/**
 * JTextArea - Include Text Line Number
 * @author ihjang
 *
 */
public class BusinessViewMain extends JFrame {
	public static final LookAndFeel laf		= UIManager.getLookAndFeel();
	public static Context ctx1	= null;	// Default Context
	public static Context ctx2	= null;	// Compare Context
	public static Context ctx3	= null;	// Default Context - Used by Background
	public static Context ctx4	= null;	// Compare Context - Used by Background
	
	public static StringList slMQLTypeList = new StringList();
	
	// Left Search Area
	private static SteppedComboBox comboMQLType;
	public static JProgressBar progress		= new JProgressBar(0, 100);	// Find progressbar
	
	private static JTabbedPane tabPane;	
    private static JTextField txtField;
    private JMenuItem tabComponentsItem;
    private JMenuItem tabScrollItem;
    private static JRadioButton optionDefault;
    private static JRadioButton optionCompare;
    public static Switch button3			= new Switch();	// Background On/Off
    public static Switch button4			= new Switch();	// Case sensitive
    private static JComboBox comboDefault;
    private static JComboBox comboCompare;
	public static JList list;
	public static String sSearch	= "";	// comboMQLType && FindButton
	private static Properties properties 	= null;
	
	private static JPopupMenu applyMenu		= new JPopupMenu("Apply1");
	private static JPopupMenu listMenu		= new JPopupMenu("Apply2");
	
	// Progress Bar
	public static JProgressBar progressLoad	= new JProgressBar(0, 100);	// Auto compare progressbar
	
	
    // Tab Title List
    public static Map mTitleMap				= new HashMap(); // String, StringList
    
    // List Icon Info
    public static Map iconsList 			= new HashMap();	// List Icon
    public static Map<Object, Icon> icons 	= new HashMap<Object, Icon>();	// Search Combobox Icon
    
	private static final Color lsb 				= new Color(0, 84, 255, 80);		// List Selection Background Color
    private static final Color UnSelColor 	= new Color(217, 229, 255);	// UnSelected Color
    private static final Color contAreaColor = Color.WHITE;
    
    
    public static Map mAttrSettingMap			= new HashMap();
    
    public static boolean isFirst				= true;	// SchemaAutoCompare Flag
    public static String sModified;
	public static String sModifiedTmp;
	
	public static JSplitPane paneMainSplit 	= new JSplitPane(JSplitPane.VERTICAL_SPLIT);	// Main   Split Pane (Left - [Search, List], Right - [Context Menu, Content])
	public static JSplitPane paneBottom		= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);	// Right  Split Pane (Top - [Search], Bottom - [List])

	
	public static SchemaAutoCompare autoCompare	= new SchemaAutoCompare();
	private static boolean isShift			= false;
    /**
     * All List Information - Compare List
     */
    public static HashMap mDefaultListMap	= new HashMap();
    
	/**
	 * Set Context Information
	 * @param ctx
	 * @return
	 */
	private static Context setContextInfo (String ctx) {
		Context context = null;	
		try {
			if(ctx.equals("localhost")) ctx = "";

			context = new Context(ctx);
			context.setUser("admin_platform");
			context.setPassword("Qwer1234");
			context.connect();
		} catch (MatrixException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return context;
	}

	/**
	 * View Main
	 */
	public BusinessViewMain() {
		StringList contextList	= FrameworkUtil.split(properties.getProperty("Matrix.Context").trim(), ",");
		String[] contextArr 	= (String[]) contextList.toArray((new String[contextList.size()]));
		
		comboDefault 	= new JComboBox(contextArr);
		comboCompare 	= new JComboBox(contextArr);
		comboDefault.setPreferredSize(new Dimension(250, 25));
		comboDefault.setLightWeightPopupEnabled(false); // z-index 理쒖긽�쐞
		comboCompare.setPreferredSize(new Dimension(250, 25));
		comboCompare.setLightWeightPopupEnabled(false); // z-index 理쒖긽�쐞
		
		comboDefault.setSelectedIndex(1);
        Component[] comp = comboDefault.getComponents();
        for (int i = 0; i < comp.length; i++) {
            if (comp[i] instanceof MetalComboBoxButton) {
                MetalComboBoxButton coloredArrowsButton = (MetalComboBoxButton) comp[i];
                coloredArrowsButton.setBackground(null);
                break;
            }
        }
        
		comboDefault.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					mTitleMap.clear();
					tabPane.removeAll();
					ctx1 = setContextInfo(comboDefault.getSelectedItem().toString());
					ctx3 = setContextInfo(comboDefault.getSelectedItem().toString());
					autoCompare.changeContextAutoCompare(ctx3, ctx4);	// Context 蹂�寃�
	//    			[LOG]
	    			try {
						SchemaLogs.writeLogFile("CHANGE", "Context Change [Default : " + comboDefault.getSelectedItem().toString() + "]");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		comboCompare.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					mTitleMap.clear();
					tabPane.removeAll();
					ctx2 = setContextInfo(comboCompare.getSelectedItem().toString());
					ctx4 = setContextInfo(comboCompare.getSelectedItem().toString());
					autoCompare.changeContextAutoCompare(ctx3, ctx4);	// Context 蹂�寃�
	//    			[LOG]
	    			try {
	    				SchemaLogs.writeLogFile("CHANGE", "Context Change [Compare : " + comboCompare.getSelectedItem().toString() + "]");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		ctx1 = setContextInfo(comboDefault.getSelectedItem().toString());
		ctx3 = setContextInfo(comboDefault.getSelectedItem().toString());
		ctx2 = setContextInfo(comboCompare.getSelectedItem().toString());
		ctx4 = setContextInfo(comboCompare.getSelectedItem().toString());
		
		slMQLTypeList.addAll(SchemaConstants.getMQLTypeList());
		
		if(button3.isOnOff()) {
//			autoCompare.settingAutoCompareThread(ctx3, ctx4);
		}
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initMenu();
				createTabbedPane();
			}
		});
	}
 
	
	/**
	 * UI configuration
	 */
	void createTabbedPane() {
		setTabbedPaneDesign();
	    
		int iWidth					= Integer.parseInt(properties.getProperty("Window.Width"));
		int iHeight					= Integer.parseInt(properties.getProperty("Window.Height"));
        
		
		/************************** paneMainSplit ********************************/
        // Main Frame default configuration
//		Image im = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/logo1.png"));
//		setIconImage(im);
	    setContentPane(paneMainSplit);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(iWidth, iHeight);	// Popup Size
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setTitle("MQL Viewer");
        /************************** paneMainSplit ********************************/
        
        
        /******************** Left Panel Child (Search) **************************/
        comboMQLType 		= new SteppedComboBox(SchemaConstants.getMQLTypeList());	// MQL Type Combobox
        comboMQLType.setPreferredSize(new Dimension(45, 25));
        comboMQLType.setRenderer(new IconListRenderer(icons));
        comboMQLType.setPopupWidth(250);
        comboMQLType.addKeyListener(new KeyListener() {
        	@Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
            	    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            	        ProgressBar.buttonClicked(SchemaConstants.BTNTYPE_FIND);
                    }
                }
            });
        
        JButton button1		= new JButton(setImageIcon("/images/findFocusOut.png"));	// Find Button
        JButton button2		= new JButton(setImageIcon("/images/viewFocusOut.png"));	// View Button
        button1.setPreferredSize(new Dimension(30, 30));
        button2.setPreferredSize(new Dimension(30, 30));
        button1.setRolloverIcon(setImageIcon("/images/findFocusIn.png"));
        button2.setRolloverIcon(setImageIcon("/images/viewFocusIn.png"));
        button1.setBorderPainted(false);
        button2.setBorderPainted(false);
        button1.setContentAreaFilled(false);
        button2.setContentAreaFilled(false);
        
        txtField			= new JTextField();	// Search Field
//        txtField.setBorder(new LineBorder(new Color(0, 0, 0, 80), 1, true));
        txtField.setBorder(null);
        txtField.setText(properties.getProperty("Search.Prefix"));
        txtField.setPreferredSize(new Dimension(300, 25));
        txtField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					ProgressBar.buttonClicked(SchemaConstants.BTNTYPE_FIND);
				}
			}
		});
        
        progress.setBackground(contAreaColor);	// Search Progress bar
		progress.setBorderPainted(false);
		progress.setPreferredSize(new Dimension(200, 10));
        
        // Find Button Action
		button1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				if (SwingUtilities.isLeftMouseButton(mouseEvent) && mouseEvent.getClickCount() == 1) {
					ProgressBar.buttonClicked(SchemaConstants.BTNTYPE_FIND);
				}
			}
		});
        
        // View Button Action
		button2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				if (SwingUtilities.isLeftMouseButton(mouseEvent) && mouseEvent.getClickCount() == 1) {
					ProgressBar.buttonClicked(SchemaConstants.BTNTYPE_VIEW);
				}
			}
		});
		
		/***************** Top Panel Child (Search, Context) *********************/
		JPanel panelTop	= new JPanel();
		panelTop.setBorder(new EmptyBorder(5, 5, 5, 5));
        panelTop.setLayout(new GridBagLayout());
        panelTop.setBackground(contAreaColor);
        
		JSeparator separator1	= new JSeparator(SwingConstants.VERTICAL);
		separator1.setPreferredSize(new Dimension(15, 1));
		separator1.setForeground(Color.LIGHT_GRAY);
//		JSeparator separator2	= new JSeparator(SwingConstants.VERTICAL);
//		separator2.setPreferredSize(new Dimension(15, 1));
//		separator2.setForeground(Color.LIGHT_GRAY);
//		JSeparator separator3	= new JSeparator(SwingConstants.VERTICAL);
//		separator3.setPreferredSize(new Dimension(15, 1));
//		separator3.setForeground(Color.LIGHT_GRAY);
		JLabel labelMain		= new JLabel(properties.getProperty("Label.Default"));
		JLabel labelSub			= new JLabel(properties.getProperty("Label.Compare"));
		JLabel labelSwitch		= new JLabel(properties.getProperty("Label.Switch"));
		labelMain.setPreferredSize( new Dimension(150, 25));
		labelSub.setPreferredSize( new Dimension(150, 25));
		labelSwitch.setPreferredSize( new Dimension(150, 25));
		
		optionDefault = new JRadioButton(properties.getProperty("Button.DefaultMode"));
        optionCompare = new JRadioButton(properties.getProperty("Button.CompareMode"));
        optionDefault.setBackground(contAreaColor);
        optionCompare.setBackground(contAreaColor);
        
        ButtonGroup modeGroup = new ButtonGroup();	// Context Mode - Radio Group
        modeGroup.add(optionDefault);
        modeGroup.add(optionCompare);
        optionDefault.setSelected(true);
        
        
        
		JPanel panelRound	= new RoundedPanel();
		panelRound.setBorder(new EmptyBorder(5, 5, 5, 5));
		panelRound.setLayout(new GridBagLayout());
		panelRound.setBackground(contAreaColor);
		panelRound.setPreferredSize(new Dimension(300, 40));
        
		
		// Rounded Area
		GridBagConstraints c1 	= new GridBagConstraints();
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridx = 0;
        c1.gridy = 0;
        panelRound.add(comboMQLType, c1);
        
        c1.insets = new Insets(0, 10, 0, 0);
		c1.gridx = 1;
		panelRound.add(txtField, c1);	// Search Field
        
        c1.gridx = 2;
		panelRound.add(button1, c1);	// Find Button
        
		c1.gridx = 3;
		panelRound.add(button2, c1);	//
        
        
        // Row 1
        GridBagConstraints c 	= new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		panelTop.add(panelRound, c);	// MQL Combobox
		
		c.gridwidth = 1;
		c.gridx = 3;
		c.gridheight = 2;
		c.fill = GridBagConstraints.VERTICAL;
		c.insets = new Insets(0, 10, 0, 10);
		panelTop.add(separator1, c);	// Sep
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 4;
		c.gridheight = 1;
		panelTop.add(labelMain, c);	// Context Label Default
		
		c.gridx = 5;
		c.insets = new Insets(0, 0, 0, 0);
		panelTop.add(comboDefault, c);
		
		
		//c.insets = new Insets(0, 30, 0, 0);
		//c.gridx = 9;
		//c.gridheight = 2;
		//c.fill = GridBagConstraints.VERTICAL;
		//panelTop.add(separator3, c);	// Sep
		
		//c.fill = GridBagConstraints.HORIZONTAL;
		//c.gridx = 10;
		//c.gridheight = 1;
		//panelTop.add(labelSwitch, c);	// Background Label
		
		//c.gridx = 11;
		//c.insets = new Insets(0, 0, 0, 0);
		//panelTop.add(button3, c);	// Background Switch
		
		
		
		// Row 2
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 0;
		panelTop.add(optionDefault, c);	// Context Combobox Default
		
		c.gridx = 1;
		JPanel tempPanel	= new JPanel();
		tempPanel.setBackground(Color.WHITE);
		tempPanel.setPreferredSize(new Dimension(200, 10));
		panelTop.add(tempPanel, c);
		
		c.gridx = 2;
		panelTop.add(optionCompare, c);
		
		c.gridx = 4;
		c.insets = new Insets(0, 10, 0, 10);
		panelTop.add(labelSub, c);
		
		c.gridx = 5;
		c.insets = new Insets(0, 0, 0, 0);
		panelTop.add(comboCompare, c);

		
		// Row 3
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 6;
		panelTop.add(progress, c);
		
		// Row 4
		//c.insets = new Insets(0, 0, 0, 0);
		//c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		// c.gridwidth = 12;
		JButton toggleBtn	= new ButtonHideShowComponent();
		toggleBtn.setPreferredSize(new Dimension(200, 40));
		
		toggleBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				if(SwingUtilities.isLeftMouseButton(mouseEvent)) {
					comboCompare.setVisible(!comboCompare.isVisible());
					comboDefault.setVisible(!comboDefault.isVisible());
				}
			}
		});
		panelTop.add(toggleBtn, c);
		
		progressLoad.setBackground(contAreaColor);
		progressLoad.setForeground(new Color(0, 255, 0, 80));
		progressLoad.setPreferredSize(new Dimension(50, 10));
		Border border	= BorderFactory.createTitledBorder(null, null, 0, 0, null, Color.BLUE);
		progressLoad.setBorder(border);
		
		/***************** Top Panel Child (Search, Context) *********************/

		
		/********************* Left Panel Child (List) ***************************/
		JScrollPane listScroll	= new JScrollPane();
        list 					= new JList();
        
        listScroll.setViewportView(list);	// Scroll setting
        listScroll.setBorder(null);
        listScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        //list
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.addMouseListener(new MouseAdapter() {	// List Double Click
        	public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {	// Double-click detected
                	ProgressBar.buttonClicked(SchemaConstants.BTNTYPE_VIEW);
                } else if (evt.getClickCount() == 3) {}	// Triple-click detected
            }
		});
		/********************* Left Panel Child (List) ***************************/
        
        
        /******************* Right Panel Child (Content) *************************/
		tabPane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				if (SwingUtilities.isMiddleMouseButton(mouseEvent) && mouseEvent.getClickCount() == 1) {
					removeTab();
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
					mTitleMap.clear();
					tabPane.removeAll();
				} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W){
					removeTab();
				} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_PAGE_UP) {	// Ctrl + <- ==> Left Panel
					if(tabPane.getSelectedIndex() > 0)
						tabPane.setSelectedIndex(tabPane.getSelectedIndex() - 1);
				} else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {	// Ctrl + -> ==> Right Panel
					if(tabPane.getComponentCount() > tabPane.getSelectedIndex() + 1)
						tabPane.setSelectedIndex(tabPane.getSelectedIndex() + 1);
				}
			}
		});
        
        JPanel contentPane		= new JPanel();
        contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(1, 1));
		contentPane.add(tabPane, BorderLayout.CENTER);
		/******************* Right Panel Child (Content) *************************/
		
		
		//JSplitPane Left - [Search, List], Right - [Context Menu, Content]
		paneMainSplit.setContinuousLayout(true);
		paneMainSplit.setTopComponent(panelTop);
		paneMainSplit.setBottomComponent(paneBottom);
		paneMainSplit.setDividerLocation(80);      
		paneMainSplit.setDividerSize(0);
		

		//JSplitPane Top - [Search], Bottom - [List]
		paneBottom.setContinuousLayout(true);
		paneBottom.setLeftComponent(listScroll);
		paneBottom.setRightComponent(contentPane);
		paneBottom.setDividerLocation(300);      
		paneBottom.setDividerSize(0);
		paneBottom.setBorder(null);
		
	}
	
	//private void setIconImage(URL resource) {
		// TODO Auto-generated method stub
		
	//}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource) {
				UIManager.put(key, f);
			}
		}
	}

	
	private static void initTabComponent(int i) {
		tabPane.setTabComponentAt(i, new ButtonTabComponent(tabPane, sSearch, true, mTitleMap));
		tabPane.setSelectedIndex(i);
	}
	
	/**
	 * Menu Bar
	 */
	private void initMenu() {
		iconsList.putAll(SchemaConstants.iconImageForTab());
		icons.putAll(SchemaConstants.iconImageForList());
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(new Color(250, 250, 250));
		// create Options menu
		tabComponentsItem = new JCheckBoxMenuItem("Use Tab Components          ", true);
		tabComponentsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK));
		tabComponentsItem.setBackground(Color.WHITE);
		tabComponentsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < tabPane.getTabCount(); i++) {
					if (tabComponentsItem.isSelected()) {
						initTabComponent(i);
					} else {
						tabPane.setTabComponentAt(i, null);
					}
				}
			}
		});
		
		tabScrollItem = new JCheckBoxMenuItem("Use Tab Scroll", true);
		tabScrollItem.setBackground(Color.WHITE);
		tabScrollItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabPane.setTabLayoutPolicy(tabScrollItem.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
			}
		});
		
		
		JMenu exportMQL 			= new JMenu(properties.getProperty("Search.Menu.ExecuteMQL"));
		exportMQL.add(new JMenuItemExport("Attribute", 					SchemaConstants.ATTRIBUTE, 		(ImageIcon) icons.get(SchemaConstants.ATTRIBUTE)));
		exportMQL.add(new JMenuItemExport("Type", 						SchemaConstants.TYPE, 			(ImageIcon) icons.get(SchemaConstants.TYPE)));
		exportMQL.add(new JMenuItemExport("Policy", 					SchemaConstants.POLICY, 		(ImageIcon) icons.get(SchemaConstants.POLICY)));
		exportMQL.add(new JMenuItemExport("Program",					SchemaConstants.PROGRAM,		(ImageIcon) icons.get(SchemaConstants.PROGRAM)));
		exportMQL.add(new JMenuItemExport("Relationship", 				SchemaConstants.RELATIONSHIP, 	(ImageIcon) icons.get(SchemaConstants.RELATIONSHIP)));
		exportMQL.add(new JMenuItemExport("Format", 					SchemaConstants.FORMAT, 		(ImageIcon) icons.get(SchemaConstants.FORMAT)));
		exportMQL.add(new JMenuItemExport("Role", 						SchemaConstants.ROLE, 			(ImageIcon) icons.get(SchemaConstants.ROLE)));
		exportMQL.add(new JMenuItemExport("Command", 					SchemaConstants.COMMAND, 		(ImageIcon) icons.get(SchemaConstants.COMMAND)));
		exportMQL.add(new JMenuItemExport("Menu", 						SchemaConstants.MENU, 			(ImageIcon) icons.get(SchemaConstants.MENU)));
		exportMQL.add(new JMenuItemExport("Form", 						SchemaConstants.FORM, 			(ImageIcon) icons.get(SchemaConstants.FORM)));
		exportMQL.add(new JMenuItemExport("Table", 						SchemaConstants.TABLE, 			(ImageIcon) icons.get(SchemaConstants.TABLE)));
		exportMQL.add(new JMenuItemExport("Channel", 					SchemaConstants.CHANNEL, 		(ImageIcon) icons.get(SchemaConstants.CHANNEL)));
		exportMQL.add(new JMenuItemExport("Portal", 					SchemaConstants.PORTAL, 		(ImageIcon) icons.get(SchemaConstants.PORTAL)));
		exportMQL.add(new JMenuItemExport(SchemaConstants.TRIGGER,		SchemaConstants.TRIGGER, 		(ImageIcon) icons.get(SchemaConstants.TRIGGER)));
		exportMQL.add(new JMenuItemExport(SchemaConstants.GENERATOR, 	SchemaConstants.GENERATOR, 		(ImageIcon) icons.get(SchemaConstants.GENERATOR)));

		
		JMenuItem menuItemExit	= new JMenuItem(properties.getProperty("Search.Menu.Exit"));
		menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		menuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		
		JMenuItem menuItemParser	= new JMenuItem("URL Parser        ", setImageIcon("/images/parser.png"));
		menuItemParser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK));
		menuItemParser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String sURL			= JOptionPane.showInputDialog(null, properties.getProperty("Message.InputURL"));	// Input MQL Name
				
				if(null == sURL)
					return;
				
				new URLParseTable(sURL);
			}
		});
		
		JMenuItem menuItemLogs		= new JMenuItem(properties.getProperty("Search.MenuItem.LogsView"), setImageIcon("/images/LOG16.png"));
		menuItemLogs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_MASK));
		menuItemLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					SchemaLogs.logsFileChooser();
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});
		
		JMenuItem menuItemChildren		= new JMenuItem(properties.getProperty("Search.MenuItem.Children") + "      ", setImageIcon("/images/node-tree.png"));
		menuItemChildren.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
		menuItemChildren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel panel 			= new JPanel(new GridLayout(0, 1));

				String[] items 			= SchemaConstants.getHierarchyTypeList();
				JComboBox<String> combo = new JComboBox<>(items);
				combo.setRenderer(new IconListRenderer(icons));
		        JTextField field1 		= new JTextField("");
//		        JCheckBox checkbox		= new JCheckBox("Top-down (true), Bottom-up (false)", true);	// default - true
		        
		        panel.add(new JLabel("Type : "));
		        panel.add(combo);
		        panel.add(new JLabel("Name : "));
		        panel.add(field1);
//		        panel.add(checkbox);
		        int result = JOptionPane.showConfirmDialog(null, panel, "Selected Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		        String sType	= "";
		        String sName	= "";
		        if (result == JOptionPane.OK_OPTION) {
		        	sType		= combo.getSelectedItem().toString();
		        	sName		= field1.getText();
		        	
		        	if(null == sName)
						return;
					
					new DialogSchemaChildrenView(ctx1, sType, sName);
					
		        } else {
		            System.out.println("Cancelled");
		        }
			}
		});
		
		JSeparator js1		= new JSeparator();
		JSeparator js2		= new JSeparator();
		js1.setForeground(Color.LIGHT_GRAY);
		js2.setForeground(Color.LIGHT_GRAY);
		JMenu menuFiles 	= new JMenu(properties.getProperty("Search.Menu.Files"));
		JMenu menuWindow 	= new JMenu(properties.getProperty("Search.Menu.Window"));
		JMenu menuTest		= new JMenu(properties.getProperty("Search.Menu.Test"));
		menuFiles.setMnemonic(KeyEvent.VK_F); // alt + F
		menuWindow.setMnemonic(KeyEvent.VK_W); // alt + W
		
		menuFiles.add(tabComponentsItem);
		menuFiles.add(tabScrollItem);
		menuFiles.add(js1);
		menuFiles.add(exportMQL);
		menuFiles.add(js2);
		menuFiles.add(menuItemExit);
		menuWindow.add(menuItemParser);
		menuWindow.add(menuItemLogs);
		menuTest.add(menuItemChildren);
		menuBar.add(menuFiles);
		menuBar.add(menuWindow);
		menuBar.add(menuTest);
		setJMenuBar(menuBar);
		
		
		/********************** Popup Menu **********************/
		JMenuItem compareView	= new JMenuItem(properties.getProperty("Label.CompareView"), setImageIcon("/images/comparison16.png"));
		JSeparator js			= new JSeparator();
		JMenuItem insert		= new JMenuItem(properties.getProperty("Label.InsertView"), setImageIcon("/images/insertList.png"));
		applyMenu.add(compareView);
		applyMenu.add(js);
		applyMenu.add(insert);

		
		compareView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAddMQLDialog(false);
			}
		});
		
		insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(null, properties.getProperty("Message.InsertAlert"), "�븣由�", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (response == JOptionPane.NO_OPTION) {
				} else if (response == JOptionPane.YES_OPTION) {
					JScrollPane sc 	= (JScrollPane) tabPane.getSelectedComponent();
					JViewport vp	= sc.getViewport();
					JTextArea ta	= (JTextArea) vp.getComponent(0);
					
					SchemaImport si	= new SchemaImport();
					try {
						si.importMQL(ctx2, ta.getText());
						JOptionPane.showMessageDialog(null, "Insert MQL Success!!!");
						SchemaLogs.writeLogFile("INFO  ", "\r\n[SUCCESS]\r\n"+ta.getText());	// [LOG]
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, e1.getMessage());
						try {
							SchemaLogs.writeLogFile("INFO  ", "\r\n[FAIL]\r\n"+ta.getText()+"\r\n"+e1.getMessage());	// [LOG]
						} catch (Exception ee) {
							ee.printStackTrace();
						}
					}
				} else if (response == JOptionPane.CLOSED_OPTION) {
				}
			}
		});
		
		
		/********************** JList Popup Menu **********************/
		JMenuItem dialogNewWin		= new JMenuItem("Open New Window", setImageIcon("/images/view.png"));
		JMenuItem exportList		= new JMenuItem("Export Select", setImageIcon("/images/exportExcel.png"));
		JMenuItem exportListAll		= new JMenuItem("Export All", setImageIcon("/images/exportExcel.png"));
		JSeparator menuSep			= new JSeparator(SwingConstants.HORIZONTAL);
		menuSep.setPreferredSize(new Dimension(25, 1));
		
		listMenu.add(dialogNewWin);
		listMenu.add(menuSep);
		listMenu.add(exportList);
		listMenu.add(exportListAll);
		
		dialogNewWin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List arr				= list.getSelectedValuesList();	// Selected Title
					int iListSize			= arr.size();
					for(int i = 0; i < iListSize; i++)
					{
						new DialogSchemaView(ctx1, sSearch, (String) arr.get(i));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		exportList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					HSSFWorkbook workbook	= new HSSFWorkbook();
					List arr				= list.getSelectedValuesList();	// Selected Title
		    		int[] arrIndex 			= list.getSelectedIndices();	// Selected Index

		    		int iArrSize			= arr.size();
		    		String sViewType		= ((String) iconsList.get(SchemaConstants.VIEW_TYPE)).trim();
		    		StringList slTypeList	= new StringList();
		    		slTypeList.addAll(slMQLTypeList);

		    		if(!sViewType.contains(",")) {
		    			sViewType			= new StringBuilder("").append(list.getModel().getSize()).append(",0,0").toString();
		    		}
		    		
		    		StringList slFlag		= FrameworkUtil.split(sViewType, ",");
		    		int iAddIndex			= Integer.parseInt((String) slFlag.get(0));
		    		int iModIndex			= Integer.parseInt((String) slFlag.get(1));

		    		StringList slAddList	= new StringList();
		    		StringList slModList	= new StringList();
		    		StringList slDelList	= new StringList();
		    		for(int i = 0; i < iArrSize; i++)
		    		{
		    			int iArrIndex		= arrIndex[i];
		    			
		    			if(iArrIndex < iAddIndex) {
		    				slAddList.add((String) arr.get(i));
		    			} else if(iAddIndex <= iArrIndex && iArrIndex < (iAddIndex + iModIndex)) {
		    				slModList.add((String) arr.get(i));
		    			} else {
		    				slDelList.add((String) arr.get(i));
		    			}
		    		}
		    		
		    		if(null != slAddList && slAddList.size() > 0) {
		    			exportExcelCommon(ctx1, null, workbook, sSearch, slAddList, SchemaConstants.ADD_FLAG);
		    		}
	    			if(null != slModList && slModList.size() > 0) {
		    			exportExcelCommon(ctx1, null, workbook, sSearch, slModList, SchemaConstants.MOD_FLAG);
		    		}
	    			if(null != slDelList && slDelList.size() > 0) {
		    			exportExcelCommon(ctx1, null, workbook, sSearch, slDelList, SchemaConstants.DEL_FLAG);
		    		}
	    			
	    			// Export Chooser
	    			SchemaExport.exportExcelChooser(workbook);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		exportListAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					StringList slSelectList	= new StringList();
					slSelectList.addAll(getJListAllItem());
					HSSFWorkbook workbook	=  new HSSFWorkbook();
					exportExcelCommon(ctx1, null, workbook, sSearch, slSelectList, SchemaConstants.ADD_FLAG);
					
	    			// Export Chooser
	    			SchemaExport.exportExcelChooser(workbook);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	private static void exportExcelCommon(Context ctx1, Context ctx2, HSSFWorkbook workbook, String sSearch, StringList slDataList, String Flag) throws Exception {
		StringBuilder sbResult	= new StringBuilder();
		sbResult.append(SchemaExport.exportExcel(ctx1, null, workbook, sSearch, slDataList, Flag));
		SchemaExport.exportExcelSettingData(sSearch, Flag, workbook, sbResult.toString());
	}

	private static List getJListAllItem()
	{
		int iListSize	= list.getModel().getSize();
		List returnList	= new ArrayList();
		
		for(int i = 0; i < iListSize; i++) {
			returnList.add(list.getModel().getElementAt(i));
		}
		
		return returnList;
	}
	
	
	private static void setTabbedPaneDesign() {
		tabPane = new JTabbedPaneCustom();
		tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		//tabPane.setBackground(Color.WHITE);
	}
	
	public static void viewButtonAction() {
		System.err.println("View Button Action");
		progress.setIndeterminate(true);
		try {
    		List arr		= list.getSelectedValuesList();	// Selected Title
    		int[] arrIndex 	= list.getSelectedIndices();	// Selected Index
		
    		int iArrSize	= arr.size();
    		int useIndex	= -1;
    		for(int i = 0; i < iArrSize; i++)
    		{
    			/*************** Get MQL ***************/
    			String sInfo		= (String) arr.get(i);
    		
    			if(mTitleMap.containsKey(sInfo) && ((StringList) mTitleMap.get(sInfo)).contains(sSearch))
    				continue;
    			useIndex++;
    			
    			int iArrIndex		= arrIndex[i];
    			StringList slInfo	= new StringList();
    			slInfo				= FrameworkUtil.split(sInfo, "|");
    			slInfo.add(0, sSearch);
    			if(sSearch.equals(SchemaConstants.TRIGGER) || sSearch.equals(SchemaConstants.GENERATOR) || !slMQLTypeList.contains(sSearch)) {
    				if(!optionDefault.isSelected())
    					slInfo.add(SchemaConstants.MOD_FLAG);
    				else
    					slInfo.add(SchemaConstants.ADD_FLAG);
    			}
    			String[] args			= (String[]) slInfo.toArray(new String[slInfo.size()]);
    			String sResult			= "";
    			
    			if(optionDefault.isSelected())	// Default Mode
        		{
					sResult				= SchemaUtil.getSchema(ctx1, args);
        		}
        		else // Compare Mode
        		{
        			String sViewType	= (String) iconsList.get(SchemaConstants.VIEW_TYPE);
        			StringList slFlag	= FrameworkUtil.split(sViewType, ",");
        			int iAddIndex		= Integer.parseInt((String) slFlag.get(0));
        			int iModIndex		= Integer.parseInt((String) slFlag.get(1));
        			
        			if(iArrIndex < iAddIndex) {
        				sResult			= SchemaUtil.getSchema(ctx1, args);
        			} else if(iAddIndex <= iArrIndex && iArrIndex < (iAddIndex + iModIndex)) {
        				sResult			= SchemaUtil.getSchemaModify(ctx1, ctx2, args);
        			} else {	// Delete
        				sResult			= SchemaUtil.getSchemaDelete(ctx2, args);
        			}
        		}
    			/*************** Get MQL ***************/
    			
    			setTabContent(sResult, (String) arr.get(i), i, useIndex);

//    			[LOG]
    			SchemaLogs.writeLogFile("VIEW  ", args[0] + " : " + args[1]);
    			if(i > 0) {
    				progress.setIndeterminate(false);
    				progress.setValue((int) ((float) (i + 1) / (float) iArrSize * 100));
    				ProgressBar.setProgressColor(progress.getValue());
    			}
    		}
		} catch (FrameworkException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			progress.setIndeterminate(false);
		}
	}
	
	public static void findButtonAction() {
		System.err.println("Find Button Action");
		long a = System.currentTimeMillis();
    	String sTxt	= txtField.getText().trim();
    	LookAndFeel laf		= UIManager.getLookAndFeel();
    	try {
    		sSearch				= (String) comboMQLType.getSelectedItem();
    		if(sSearch.equals(SchemaConstants.OBJECT)) {	// Etc Object
    			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    			String sFindMQL		= JOptionPane.showInputDialog(null, "Type �엯�젰.\nex) Part, Part Family...");	// Input MQL Name
    			if(null == sFindMQL)
    				return;
    			
    			sSearch				= sFindMQL.trim();
    		}
    		
    		progress.setIndeterminate(true);
    		progress.setForeground(new Color(29, 219, 22, 95));
    		
    		StringList slData	= SchemaCompareMain.compareListCommon(ctx1, ctx2, sSearch, sTxt, isFirst, optionCompare.isSelected());
            
			list.removeAll();
			String[] arr		= (String[]) slData.toArray(new String[slData.size()]);
			list.setListData(arr);
			list.setFixedCellHeight(25);
			list.setSelectionBackground(lsb);
			list.setCellRenderer(new IconListRenderer(iconsList));
			list.setComponentPopupMenu(listMenu);
			long b = System.currentTimeMillis();
			System.err.println((b-a) + " ms");
			
//			[LOG]
			SchemaLogs.writeLogFile("SEARCH", sSearch + " : " + sTxt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			progress.setIndeterminate(false);
			try {
				UIManager.setLookAndFeel(laf);
			} catch (Exception e3) {
				e3.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Attribute List from object
	 * @param context
	 * @param sType
	 */
	public static void setObjectAttributeCheck(Context context, String sType) {
		try {
			StringBuilder sbQuery	= new StringBuilder();

			if(mAttrSettingMap.containsKey(sType))
				return;
			
			sbQuery.append("print type '").append(sType).append("' select attribute dump '|'");
			String sAttributes	= MqlUtil.mqlCommand(ctx1, sbQuery.toString());
			StringList slAttr	= FrameworkUtil.split(sAttributes, "|");
			slAttr.sort();

			StringList slAttrList		= new StringList();
			if(sType.equals(SchemaConstants.GENERATOR)) {
				StringList slNumberGenAttr	= new StringList();
				slAttrList.addAll(slAttr);
				slAttr.clear();
				sAttributes	= MqlUtil.mqlCommand(ctx1, sbQuery.toString().replace(SchemaConstants.GENERATOR, SchemaConstants.NUMBER_GENERATOR));
				slAttr	= FrameworkUtil.split(sAttributes, "|");
				slNumberGenAttr.addAll(slAttr);
				mAttrSettingMap.put(SchemaConstants.NUMBER_GENERATOR, slNumberGenAttr);
			} else {
				slAttrList.addAll(slAttr);
			}
			mAttrSettingMap.put(sType, slAttrList);
		
		} catch (FrameworkException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Setting Tab Content (Business, Trigger, Generator)
	 * @param sResult
	 * @param arr
	 * @param i
	 * @param useIndex
	 */
	private static void setTabContent(String sResult, String sTitle, int i, int useIndex) {
		int tabSize	= tabPane.getTabCount();
		
		// Border border 		= BorderFactory.createTitledBorder("");
		JTextArea txtArea	= new JTextArea();
        txtArea.setLineWrap(false); //�븳以꾩씠 �꼫臾� 湲몃㈃ �옄�룞�쑝濡� 媛쒗뻾�븷吏� �꽕�젙
        txtArea.setColumns(50); //�뿴�쓽 �겕湲�(媛�濡쒗겕湲�)
        txtArea.setRows(10); //�뻾�쓽 �겕湲�(�꽭濡쒗겕湲�)
        txtArea.setEditable(false);
        txtArea.setText(sResult);
        txtArea.getCaret().setVisible(true);
        txtArea.setBackground(Color.WHITE);
        
        if(!optionDefault.isSelected()) {
	        txtArea.setComponentPopupMenu(applyMenu);
        }
        
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
        			int iSize	= tabPane.getTabCount();
        			for(int i = 0; i < iSize; i++) {
        				tabPane.remove(0);
        			}
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
        
        TextLineNumber tln 		= new TextLineNumber(txtArea);
		JScrollPane scrollPane 	= new JScrollPane(txtArea);
		scrollPane.setViewportView(txtArea);
		//scrollPane.setBorder(border);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); // Horizontal Scroll Always View
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setRowHeaderView( tln );
		scrollPane.setBackground(Color.WHITE);
		
		txtArea.setCaretPosition(0);
		
        tabPane.add(sTitle, scrollPane);
        initTabComponent(tabSize);
        StringList slList = new StringList();
        if(mTitleMap.containsKey(sTitle)) {
        	slList = (StringList) mTitleMap.get(sTitle);
            slList.add(sSearch);
        } else {
        	slList.add(sSearch);
        }
        mTitleMap.put(sTitle, slList);
	}
	
	
	/**
	 * Main Method
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			SchemaLogs.writeLogFile("START ", "MQL Viewer Start...");	// [LOG]
			
		    initUIManager();	// UI Style
		    properties	= initProperties();	// Get Properties
			new BusinessViewMain();
		} catch (Exception e) {
		    /*
		        ClassNotFoundException
		        InstantiationException
		        IllegalAccessException
		        UnsupportedLookAndFeelException e
		     */
		}
	}
	
	public static void initUIManager() {
		UIManager.put("TabbedPane.borderHightlightColor", Color.WHITE); 
	    UIManager.put("TabbedPane.darkShadow", Color.WHITE); 
	    UIManager.put("TabbedPane.selected", new ColorUIResource(new Color(230, 230, 230, 90)));
		UIManager.put("ComboBox.background", new ColorUIResource(Color.WHITE));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(UnSelColor));
//        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(lsb));
//        218, 224, 241
//        180, 205, 230
        UIManager.put("Menu.border", BorderFactory.createLineBorder(Color.WHITE, 1));
        UIManager.put("Menu.background", Color.WHITE);
        UIManager.put("Menu.selectionBackground", new Color(212, 244, 250));
        UIManager.put("MenuBar.background", new Color(200, 0, 0));
        UIManager.put("MenuBar.selectionBackground", new Color(212, 244, 250));        
        
        UIManager.put("MenuItem.border", BorderFactory.createLineBorder(Color.WHITE, 1));
        UIManager.put("MenuItem.background", Color.WHITE);
        UIManager.put("MenuItem.selectionBackground", new Color(212, 244, 250));
        
        UIManager.put("PopupMenu.background", Color.WHITE);
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
//        setUIFont(new javax.swing.plaf.FontUIResource("NanumGothicCoding", java.awt.Font.PLAIN, 14));	// All Component Setting Font
        setUIFont(new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, 15));	// All Component Setting Font
//        setUIFont(new javax.swing.plaf.FontUIResource("Consolas", java.awt.Font.PLAIN, 14));	// All Component Setting Font
        
//        try {
//        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//            if ("Nimbus".equals(info.getName())) {
//                javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                break;
//            }
//        }
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
	}
	
	private static void put(String string, Border createLineBorder) {
		// TODO Auto-generated method stub
		
	}

	public static Properties initProperties() {
		try {
			return SchemaProperties.getSchemaProperties();	// Get Properties
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static void showAddMQLDialog(boolean isDefault) {
		// All code inside SwingWorker runs on a seperate thread
		SwingWorker worker = new SwingWorker() {
			@Override
			public Object doInBackground() {
				ButtonTabComponent btc = (ButtonTabComponent) tabPane.getTabComponentAt(tabPane.getSelectedIndex());
				String sType = btc.getType();
                String sTitle = tabPane.getTitleAt(tabPane.getSelectedIndex()).trim();
				new DialogSchemaCompare(ctx1, ctx2, sType, sTitle, isDefault);
				
				return null;
			}

			@Override
			public void done() {}
		};

		// Call the SwingWorker from within the Swing thread
		worker.execute();
	}
	
	private ImageIcon setImageIcon(String sImgUrl) {
		return new ImageIcon(getClass().getResource(sImgUrl));
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
