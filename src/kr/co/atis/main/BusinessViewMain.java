package kr.co.atis.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.swing.JLayer;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalComboBoxButton;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;

import kr.co.atis.db.SchemaImport;
import kr.co.atis.uiutil.ButtonHideShowComponent;
import kr.co.atis.uiutil.ButtonTabComponent;
import kr.co.atis.uiutil.CBListener;
import kr.co.atis.uiutil.DialogSchemaChildrenView;
import kr.co.atis.uiutil.DialogSchemaCompare;
import kr.co.atis.uiutil.DialogSchemaView;
import kr.co.atis.uiutil.HighlightTextAreaLine;
import kr.co.atis.uiutil.IconListRenderer;
import kr.co.atis.uiutil.JMenuItemExport;
import kr.co.atis.uiutil.JTabbedPaneCustom;
import kr.co.atis.uiutil.ProgressBar;
import kr.co.atis.uiutil.ProgressCircleUI;
import kr.co.atis.uiutil.ProgressListener;
import kr.co.atis.uiutil.RoundedPanel;
import kr.co.atis.uiutil.SteppedComboBox;
import kr.co.atis.uiutil.Switch;
import kr.co.atis.uiutil.TextLineNumber;
import kr.co.atis.uiutil.URLParseTable;
import kr.co.atis.uiutil.WaitLayerUI;
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
 * Panel Structure
 * 1. Main
 *    - Top
 *    - Bottom
 *    	- Left (List)
 * 		- Right (View)
 * @author ihjang
 *
 */
public class BusinessViewMain extends JFrame {
	public static Context ctx1	= null;	// Default Context
	public static Context ctx2	= null;	// Compare Context
	public static Context ctx3	= null;	// Default Context - Used by Background
	public static Context ctx4	= null;	// Compare Context - Used by Background
	
	public static StringList slMQLTypeList = new StringList();
	
	// Left Search Area
	private static SteppedComboBox comboMQLType;
	public static WaitLayerUI layerUI = new WaitLayerUI();
	public static ProgressCircleUI glassPane;
	public static final JProgressBar progress2 = new JProgressBar() {
		@Override
		public void updateUI() {
			super.updateUI();
			setUI(new ProgressCircleUI());
			setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		}
	};
	public static float progressVal = 0;
	
	private static JTabbedPane tabPane;	
    private static JTextField txtField;
    private JMenuItem tabComponentsItem;
    private JMenuItem tabScrollItem;
    public static JMenuItem searchCaseSensitive;
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
    
	private static final Color lsb 			= new Color(0, 84, 255, 80);// List Selection Background Color
    
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
    public static int fontSize = 15;
    
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
		comboDefault.setLightWeightPopupEnabled(false); // z-index
		comboCompare.setPreferredSize(new Dimension(250, 25));
		comboCompare.setLightWeightPopupEnabled(false); // z-index
		
		comboDefault.setSelectedIndex(1);
        Component[] comp = comboDefault.getComponents();
        for (int i = 0; i < comp.length; i++) {
            if (comp[i] instanceof MetalComboBoxButton) {
                MetalComboBoxButton coloredArrowsButton = (MetalComboBoxButton) comp[i];
                coloredArrowsButton.setBackground(null);
                break;
            }
        }
        
		comboDefault.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				mTitleMap.clear();	tabPane.removeAll();
				ctx1 = setContextInfo(comboDefault.getSelectedItem().toString());
				ctx3 = setContextInfo(comboDefault.getSelectedItem().toString());
				autoCompare.changeContextAutoCompare(ctx3, ctx4);	// Context Change
    			try {	// [LOG]
					SchemaLogs.writeLogFile("CHANGE", "Context Change [Default : " + comboDefault.getSelectedItem().toString() + "]");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		comboCompare.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				mTitleMap.clear();	tabPane.removeAll();
				ctx2 = setContextInfo(comboCompare.getSelectedItem().toString());
				ctx4 = setContextInfo(comboCompare.getSelectedItem().toString());
				autoCompare.changeContextAutoCompare(ctx3, ctx4);	// Context change
    			try {	// [LOG]
    				SchemaLogs.writeLogFile("CHANGE", "Context Change [Compare : " + comboCompare.getSelectedItem().toString() + "]");
				} catch (Exception e1) {
					e1.printStackTrace();
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
        
		
		/************************** Main Panel Split [B] ********************************/
        // Main Frame default configuration
//		Image im = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/logo1.png"));
//		setIconImage(im);
//	    setContentPane(paneMainSplit);
        JLayer<JSplitPane> jlayer = new JLayer<JSplitPane>(paneMainSplit, layerUI);
	    
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        progress2.setBackground(new Color(235, 245, 251, 10));
        progress2.setForeground(new Color(0xAA_34_98_D8, true));
        CBListener listener = new CBListener(null, null, progress2, paneMainSplit);
        progress2.addMouseListener(listener);
        progress2.addMouseMotionListener(listener);
		setGlassPane(progress2);
		add(jlayer);
        setSize(iWidth, iHeight);	// Popup Size
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setTitle("MQL Viewer");
        /************************** Main Panel Split [E] ********************************/
        
        
        /******************** Top Panel Child (Search, Context) [B] **************************/
        comboMQLType 		= new SteppedComboBox(SchemaConstants.getMQLTypeList());	// MQL Type Combobox
        comboMQLType.setPreferredSize(new Dimension(43, 25));
        comboMQLType.setRenderer(new IconListRenderer(icons));
        comboMQLType.setPopupWidth(250);
        comboMQLType.setBorder(null);
        comboMQLType.addKeyListener(new KeyAdapter() {
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
        txtField.setBorder(null);
        txtField.setText(properties.getProperty("Search.Prefix"));
        txtField.setPreferredSize(new Dimension(300, 25));
        txtField.addKeyListener(new KeyHandler());
        
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
		
		JPanel panelTop	= new JPanel();
		panelTop.setBorder(new EmptyBorder(5, 5, 5, 5));
        panelTop.setLayout(new GridBagLayout());
        panelTop.setBackground(new Color(0, 86, 133));
        
		JSeparator separator1	= new JSeparator(SwingConstants.VERTICAL);
		separator1.setPreferredSize(new Dimension(15, 1));
		separator1.setForeground(Color.LIGHT_GRAY);
		JLabel labelMain		= new JLabel(properties.getProperty("Label.Default"));
		JLabel labelSub			= new JLabel(properties.getProperty("Label.Compare"));
		JLabel labelSwitch		= new JLabel(properties.getProperty("Label.Switch"));
		labelMain.	setPreferredSize(new Dimension(150, 25));	labelMain.setFont(new Font("D2Coding", Font.BOLD, 15));		labelMain.setForeground(Color.WHITE);
		labelSub.	setPreferredSize(new Dimension(150, 25));	labelSub.setFont(new Font("D2Coding", Font.BOLD, 15));		labelSub.setForeground(Color.WHITE);
		labelSwitch.setPreferredSize(new Dimension(150, 25));	labelSwitch.setFont(new Font("D2Coding", Font.BOLD, 15));	labelSwitch.setForeground(Color.WHITE);
		
		optionDefault = new JRadioButton(properties.getProperty("Button.DefaultMode"));
        optionCompare = new JRadioButton(properties.getProperty("Button.CompareMode"));
        optionDefault.setBackground(new Color(0, 86, 133));
        optionCompare.setBackground(new Color(0, 86, 133));
        optionDefault.setFont(new Font("D2Coding", Font.BOLD, 15));	optionDefault.setForeground(Color.WHITE);
        optionCompare.setFont(new Font("D2Coding", Font.BOLD, 15));	optionCompare.setForeground(Color.WHITE);
        
        ButtonGroup modeGroup = new ButtonGroup();	// Context Mode - Radio Group
        modeGroup.add(optionDefault);
        modeGroup.add(optionCompare);
        optionDefault.setSelected(true);
        
		JPanel panelRound	= new RoundedPanel();
		panelRound.setLayout(new GridBagLayout());
		
		// Rounded Area
		GridBagConstraints c1 	= new GridBagConstraints();
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridx = 0;
        c1.gridy = 0;
        c1.insets = new Insets(0, 5, 0, 0);
        panelRound.add(comboMQLType, c1);
        
		c1.gridx = 1;
		panelRound.add(txtField, c1);	// Search Field
        
        c1.gridx = 2;
		panelRound.add(button1, c1);	// Find Button
        
		c1.gridx = 3;
		c1.insets = new Insets(0, 0, 0, 5);
		panelRound.add(button2, c1);	// View Button
        
        
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
		
		/****************** Background Switch not Delete [B] ****************************/
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
		/****************** Background Switch not Delete [E] ****************************/
		
		
		
		// Row 2
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 0;
		panelTop.add(optionDefault, c);	// Context Combobox Default
		
		c.gridx = 1;
		JPanel tempPanel	= new JPanel();
		tempPanel.setPreferredSize(new Dimension(200, 10));
		tempPanel.setBackground(new Color(0, 86, 133));
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
//		c.gridx = 0;
//		c.gridy = 2;
//		c.gridwidth = 6;
//		panelTop.add(progress, c);
		
		// Row 4
		//c.insets = new Insets(0, 0, 0, 0);
		//c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		// c.gridwidth = 12;
		JButton toggleBtn	= new ButtonHideShowComponent();
		toggleBtn.setPreferredSize(new Dimension(200, 40));
		
		toggleBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					comboCompare.setVisible(!comboCompare.isVisible());
					comboDefault.setVisible(!comboDefault.isVisible());
				}
			}
		});
		panelTop.add(toggleBtn, c);
		progressLoad.setPreferredSize(new Dimension(50, 10));
		/******************** Top Panel Child (Search, Context) [E] **************************/

		
		/********************* Left Panel Child (List) [B] ***************************/
		JScrollPane listScroll	= new JScrollPane();
        list 					= new JList();
        
        listScroll.setViewportView(list);	// Scroll setting
        listScroll.setBorder(null);
        listScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);	// horizontal scrollbar is hide
        
        //list
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);	// Multiple select
        list.addMouseListener(new MouseAdapter() {	// List Double Click
        	public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {	// Double-click detected
                	ProgressBar.buttonClicked(SchemaConstants.BTNTYPE_VIEW);
                } else if (e.getClickCount() == 3) {}	// Triple-click detected
            }
		});
        list.addKeyListener(new KeyAdapter() {
        	@Override
            public void keyPressed(KeyEvent e) {
        	    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
        	        ProgressBar.buttonClicked(SchemaConstants.BTNTYPE_VIEW);
                }
            }
		});
        list.setFixedCellHeight(25);
		list.setSelectionBackground(lsb);
        /********************* Left Panel Child (List) [E] ***************************/
        
        
        /******************* Right Panel Child (Content) *************************/
		tabPane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				if (SwingUtilities.isMiddleMouseButton(mouseEvent) && mouseEvent.getClickCount() == 1) {
					removeTab();
				}
			}
		});
        tabPane.addKeyListener(new KeyAdapter() {
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
	

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		Enumeration keys = UIManager.getDefaults().keys();
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
		// create Options menu
		tabComponentsItem = new JCheckBoxMenuItem("Use Tab Components          ", true);
		tabComponentsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK));
		
		tabScrollItem = new JCheckBoxMenuItem("Use Tab Scroll", true);

		// Theme Change Testing - [B]
		JMenu menuTheme = new JMenu("Theme (TEST...)");
		UIManager.LookAndFeelInfo looks[] = UIManager.getInstalledLookAndFeels();
		ButtonGroup group = new ButtonGroup();
		for (LookAndFeelInfo info : looks) {
			JRadioButtonMenuItem button = new JRadioButtonMenuItem(info.getName());
			button.setActionCommand(info.getClassName());
			group.add(button);
			menuTheme.add(button).addActionListener(e -> {
				String lafClassName = null;
				lafClassName = e.getActionCommand();
				String finalLafClassName = lafClassName;
				try {
					UIManager.setLookAndFeel(finalLafClassName);
					SwingUtilities.updateComponentTreeUI(paneMainSplit);
					setUIFont(new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, fontSize));	// All Component Setting Font
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(paneMainSplit, "Can't change look and feel", "Invalid PLAF", JOptionPane.ERROR_MESSAGE);
				}
			});
			if("com.sun.java.swing.plaf.windows.WindowsLookAndFeel".equals(info.getClassName()))
				button.setSelected(true);
		}
		// Theme Change Testing - [E]

		searchCaseSensitive = new JCheckBoxMenuItem("Case sensitive", true);
		
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

		JMenuItem menuItemExit		= new JMenuItem(properties.getProperty("Search.Menu.Exit"));
		menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		
		JMenuItem menuItemParser	= new JMenuItem("URL Parser        ", setImageIcon("/images/parser.png"));
		menuItemParser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK));
		
		JMenuItem menuItemLogs		= new JMenuItem(properties.getProperty("Search.MenuItem.LogsView"), setImageIcon("/images/LOG16.png"));
		menuItemLogs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_MASK));
		
		JMenuItem menuItemChildren	= new JMenuItem(properties.getProperty("Search.MenuItem.Children") + "      ", setImageIcon("/images/node-tree.png"));
		menuItemChildren.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));

		JMenu menuFile 		= new JMenu(properties.getProperty("Search.Menu.File"));
		JMenu menuView		= new JMenu(properties.getProperty("Search.Menu.View"));
		JMenu menuSettings	= new JMenu(properties.getProperty("Search.Menu.Settings"));
		menuFile.setMnemonic(KeyEvent.VK_F); // alt + F
		menuView.setMnemonic(KeyEvent.VK_W); // alt + W
		menuSettings.setMnemonic(KeyEvent.VK_T); // alt + T
		
		menuFile.add(menuItemLogs).addActionListener(e -> {try {	SchemaLogs.logsFileChooser();	} catch (Exception ee) {ee.printStackTrace();}});
		menuFile.add(exportMQL);
		menuFile.add(new JSeparator());
		menuFile.add(menuItemExit).addActionListener(e -> {System.exit(0);});
		menuView.add(menuItemParser).addActionListener(e -> {
			String sURL			= JOptionPane.showInputDialog(null, properties.getProperty("Message.InputURL"));	// Input MQL Name
			if(null == sURL)	return;
			new URLParseTable(sURL);
		});
		
		menuView.add(new JSeparator());
		menuView.add(menuItemChildren).addActionListener(e -> {
			JPanel panel 			= new JPanel(new GridLayout(0, 1));

			String[] items 			= SchemaConstants.getHierarchyTypeList();
			JComboBox<String> combo = new JComboBox<>(items);
			combo.setRenderer(new IconListRenderer(icons));
	        JTextField field1 		= new JTextField("");
	        
	        panel.add(new JLabel("Type : "));
	        panel.add(combo);
	        panel.add(new JLabel("Name : "));
	        panel.add(field1);
	        int result = JOptionPane.showConfirmDialog(null, panel, "Selected Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	        if (result == JOptionPane.OK_OPTION) {
	        	String sType	= combo.getSelectedItem().toString();
	        	String sName	= field1.getText();
	        	if(null == sName)	return;
				new DialogSchemaChildrenView(ctx1, sType, sName);
	        } else {
	            System.out.println("Cancelled");
	        }
		});
		
		menuSettings.add(tabComponentsItem).addActionListener(e -> {
			for (int i = 0; i < tabPane.getTabCount(); i++) {
				if (tabComponentsItem.isSelected()) {
					initTabComponent(i);
				} else {
					tabPane.setTabComponentAt(i, null);
				}
			}
		});
		
		JMenuItem zoomIn	= new JMenuItem("Zoom In");
		zoomIn.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// if (e.isControlDown())
		        {
		            if (e.getWheelRotation() < 0) {
		            	UIManager.put("TextArea.font", new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, (++fontSize)));
						UIManager.put("Panel.font", new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, (fontSize)));
						SwingUtilities.updateComponentTreeUI(paneMainSplit);
//		                zoomIn(e);
		            } else {
		            	UIManager.put("TextArea.font", new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, (--fontSize)));
		            	UIManager.put("Panel.font", new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, (fontSize)));
		            	SwingUtilities.updateComponentTreeUI(paneMainSplit);
//		                zoomOut(e);
		            }
		            e.consume();
		        }
			}
		});
		
		
		
		
		JMenuItem zoomOut	= new JMenuItem("Zoom Out");
		zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK));
		
		
		menuSettings.add(tabScrollItem).addActionListener(e -> {tabPane.setTabLayoutPolicy(tabScrollItem.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);});
		menuSettings.add(new JSeparator());
		menuSettings.add(searchCaseSensitive);
		menuSettings.add(menuTheme);
		menuSettings.add(new JSeparator());
//		menuSettings.add(zoomIn).addActionListener(e -> {
//			UIManager.put("TextArea.font", new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, (++fontSize)));
//			UIManager.put("Panel.font", new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, (fontSize)));
//			SwingUtilities.updateComponentTreeUI(paneMainSplit);
//		});
//		menuSettings.add(zoomOut).addActionListener(e -> {
//			if(fontSize > 5) {
//				UIManager.put("TextArea.font", new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, (--fontSize)));
//				UIManager.put("Panel.font", new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, (fontSize)));
//				SwingUtilities.updateComponentTreeUI(paneMainSplit);
//			}
//		});
		menuBar.add(menuFile);
		menuBar.add(menuView);
		menuBar.add(menuSettings);
		setJMenuBar(menuBar);
		
		
		/********************** Popup Menu **********************/
		JMenuItem compareView	= new JMenuItem(properties.getProperty("Label.CompareView"), setImageIcon("/images/comparison16.png"));
		JMenuItem insert		= new JMenuItem(properties.getProperty("Label.InsertView"), setImageIcon("/images/insertList.png"));
		applyMenu.add(compareView);
		applyMenu.add(new JSeparator());
		applyMenu.add(insert);

		
		compareView.addActionListener(e -> {showAddMQLDialog(false);	});
		insert.addActionListener(e -> {
			int response = JOptionPane.showConfirmDialog(null, properties.getProperty("Message.InsertAlert"), "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
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
		});
		
		
		/********************** JList Popup Menu **********************/
		JMenuItem dialogNewWin		= new JMenuItem("Open New Window", setImageIcon("/images/view.png"));
		JMenuItem exportList		= new JMenuItem("Export Select", setImageIcon("/images/exportExcel.png"));
		JMenuItem exportListAll		= new JMenuItem("Export All", setImageIcon("/images/exportExcel.png"));
		
		listMenu.add(dialogNewWin).addActionListener(e -> {
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
		});
		listMenu.add(new JSeparator());
		listMenu.add(exportList).addActionListener(e -> {
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
	    			exportExcelCommon(ctx1, ctx2, workbook, sSearch, slModList, SchemaConstants.MOD_FLAG);
	    		}
    			if(null != slDelList && slDelList.size() > 0) {
	    			exportExcelCommon(ctx1, null, workbook, sSearch, slDelList, SchemaConstants.DEL_FLAG);
	    		}
    			
    			// Export Chooser
    			SchemaExport.exportExcelChooser(workbook);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		listMenu.add(exportListAll).addActionListener(e -> {
			try {
				HSSFWorkbook workbook	= new HSSFWorkbook();
				StringList slSelectList	= new StringList();
				slSelectList.addAll(getJListAllItem());
//				List arr				= list.getSelectedValuesList();	// Selected Title
//	    		int[] arrIndex 			= list.getSelectedIndices();	// Selected Index

	    		int iArrSize			= slSelectList.size();
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
//	    			int iArrIndex		= slSelectList[i];
	    			
	    			if(i < iAddIndex) {
	    				slAddList.add((String) slSelectList.get(i));
	    			} else if(iAddIndex <= i && i < (iAddIndex + iModIndex)) {
	    				slModList.add((String) slSelectList.get(i));
	    			} else {
	    				slDelList.add((String) slSelectList.get(i));
	    			}
	    		}
	    		
	    		if(null != slAddList && slAddList.size() > 0) {
	    			exportExcelCommon(ctx1, null, workbook, sSearch, slAddList, SchemaConstants.ADD_FLAG);
	    		}
    			if(null != slModList && slModList.size() > 0) {
	    			exportExcelCommon(ctx1, ctx2, workbook, sSearch, slModList, SchemaConstants.MOD_FLAG);
	    		}
    			if(null != slDelList && slDelList.size() > 0) {
	    			exportExcelCommon(ctx1, null, workbook, sSearch, slDelList, SchemaConstants.DEL_FLAG);
	    		}
    			
    			// Export Chooser
    			SchemaExport.exportExcelChooser(workbook);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}
	
	private static void exportExcelCommon(Context ctx1, Context ctx2, HSSFWorkbook workbook, String sSearch, StringList slDataList, String Flag) throws Exception {
		StringBuilder sbResult	= new StringBuilder();
		sbResult.append(SchemaExport.exportExcel(ctx1, ctx2, workbook, sSearch, slDataList, Flag));
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
	}
	
	public static void viewButtonAction() {
		System.err.println("View Button Action");
		boolean layerStop = false;
		try {
    		List arr		= list.getSelectedValuesList();	// Selected Title
    		int[] arrIndex 	= list.getSelectedIndices();	// Selected Index
		
    		int iArrSize	= arr.size();
    		int useIndex	= -1;

    		if (iArrSize > 1) {
    			progressVal = 0;
    			progress2.setVisible(true);
	    		SwingWorker<String, Void> worker = new BackgroundTask2() {
					@Override
					public void done() {
						progress2.setVisible(false);
					}
				};
				worker.addPropertyChangeListener(new ProgressListener(progress2));
				worker.execute();
    		} else {
    			layerUI.start();
    			layerStop	= true;
    		}
			
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

    			// [LOG]
    			SchemaLogs.writeLogFile("VIEW  ", args[0] + " : " + args[1]);
				if(i >= 0 && iArrSize != 0) {
    				progressVal = (int) ((float) (i + 1) / (float) iArrSize * 100);
    			} 
    		}
		} catch (FrameworkException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if(layerStop) layerUI.stop();
		}
	}
	
	public static void findButtonAction() {
		System.err.println("Find Button Action");
		long a = System.currentTimeMillis();
    	String sTxt	= txtField.getText().trim();
    	
    	try {
    		sSearch				= (String) comboMQLType.getSelectedItem();
    		if(sSearch.equals(SchemaConstants.OBJECT)) {	// Etc Object
    			String sFindMQL		= JOptionPane.showInputDialog(null, "Please, input Object Type.\nex) Part, Part Family...");	// Input MQL Name
    			if(null == sFindMQL)
    				return;
    			
    			sSearch				= sFindMQL.trim();
    		}
    		layerUI.start();
    		
    		StringList slData	= SchemaCompareMain.compareListCommon(ctx1, ctx2, sSearch, sTxt, isFirst, optionCompare.isSelected());
            
			list.removeAll();
			String[] arr		= (String[]) slData.toArray(new String[slData.size()]);
			list.setListData(arr);
			list.setCellRenderer(new IconListRenderer(iconsList));
			list.setComponentPopupMenu(listMenu);
			long b = System.currentTimeMillis();
			System.err.println((b-a) + " ms");
			
			// [LOG]
			SchemaLogs.writeLogFile("SEARCH", sSearch + " : " + sTxt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			layerUI.stop();
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
        txtArea.setLineWrap(false); // Auto Wrap false 
        txtArea.setColumns(50); 	//  Col Size 50
        txtArea.setRows(10); 		// Row Size 10
        txtArea.setEditable(false);
        txtArea.setText(sResult);
        txtArea.getCaret().setVisible(true);
        
        if(!optionDefault.isSelected()) {
	        txtArea.setComponentPopupMenu(applyMenu);
        }
        
		// Key Event, Mouse Event use (BusinessViewMain, DialogSchemaCompare, DialogSchemaView)
        txtArea.addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyReleased(KeyEvent e) {
        		// Highlighter
        		HighlightTextAreaLine.tAreaHighlighterKeyCheck(e, txtArea);
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
        	}
		});
        
        txtArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent evt) {
				if(!isShift)
					HighlightTextAreaLine.tAreaHighlighter(txtArea);
			}
		});
        
        TextLineNumber tln 		= new TextLineNumber(txtArea);
		JScrollPane scrollPane 	= new JScrollPane(txtArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		scrollPane.setViewportView(txtArea);
		scrollPane.setRowHeaderView( tln );
		scrollPane.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isControlDown())
		        {
					int iFontSize = tln.getFont().getSize();
		            if (e.getWheelRotation() < 0) {
		            	iFontSize++;
		            } else {
		            	iFontSize--;
		            }
		            tln.	setFont(new Font("D2Coding", java.awt.Font.PLAIN, iFontSize));
		            txtArea.setFont(new Font("D2Coding", java.awt.Font.PLAIN, iFontSize));
		        }
			}
		});
		
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
			e.printStackTrace();
		    /*
		        ClassNotFoundException
		        InstantiationException
		        IllegalAccessException
		        UnsupportedLookAndFeelException e
		     */
		}
	}
	
	public static void initUIManager() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

//        setUIFont(new javax.swing.plaf.FontUIResource("NanumGothicCoding", java.awt.Font.PLAIN, 14));	// All Component Setting Font
        setUIFont(new javax.swing.plaf.FontUIResource("D2Coding", java.awt.Font.PLAIN, fontSize));	// All Component Setting Font
//        setUIFont(new javax.swing.plaf.FontUIResource("Consolas", java.awt.Font.PLAIN, 14));	// All Component Setting Font
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
	
		
	public class KeyHandler implements KeyListener{
        HashSet<Integer> pressedKeys = new HashSet<Integer>();
        Timer timer;
 
        public KeyHandler()
        {
            timer = new Timer(250, new ActionListener(){ // 300ms마다 액션 이벤트 발생
                @Override
                public void actionPerformed(ActionEvent arg0) // 300ms마다 발생한 액션 이벤트 처리
                {  
                    if(!pressedKeys.isEmpty()){
                    	pressedKeys.clear();
                    } else {
                        timer.stop();
                        sSearch				= (String) comboMQLType.getSelectedItem();
                    	if(!sSearch.equals(SchemaConstants.OBJECT)) {	// Etc Object
                    		ProgressBar.buttonClicked(SchemaConstants.BTNTYPE_FIND);
                    	}
                    }
                }
            });
        }
 
        @Override
        public void keyPressed(KeyEvent keyEvent){
        	if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
				ProgressBar.buttonClicked(SchemaConstants.BTNTYPE_FIND);
			}
        }
        
        @Override
        public void keyReleased(KeyEvent keyEvent){
            //HashSet에서 키코드를 제거한다
            int keyCode = keyEvent.getKeyCode();
            pressedKeys.remove(keyCode);
        }
        @Override
        public void keyTyped(KeyEvent keyEvent){
        	//발생한 키코드를 HsshSet에 저장한다
            int keyCode = keyEvent.getKeyCode();
            pressedKeys.add(keyCode);
            if(!timer.isRunning()) timer.start();
        }
    }
}

class BackgroundTask2 extends SwingWorker<String, Void> {
	@Override
	public String doInBackground() {
		int current = 0;
		int lengthOfTask = 100;
		while (current < lengthOfTask) {
			try { // dummy task
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				return "Interrupted";
			}
			setProgress(100 * current / lengthOfTask);
			current = (int) BusinessViewMain.progressVal;
		}
		return "Done";
	}
}

class BackgroundTask extends SwingWorker<String, Void> {
	@Override
	public String doInBackground() {
		int current = 0;
		int lengthOfTask = 100;
		while (current <= lengthOfTask && !isCancelled()) {
			try { // dummy task
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				return "Interrupted";
			}
			setProgress(100 * current / lengthOfTask);
			current++;
		}
		return "Done";
	}
}

