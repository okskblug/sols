package kr.co.atis.util;


import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.engineering.EngineeringConstants;
import com.matrixone.apps.library.LibraryCentralConstants;
import com.matrixone.apps.program.ProgramCentralConstants;

import matrix.util.StringList;

/**
 * TES Constants
 * 
 * @author Seon-ho, Lee
 * @version 1.0 2017/08/10
 * @see DomainConstants, EngineeringConstants, ProgramCentralConstants
 */
public abstract interface SchemaConstants extends DomainConstants, EngineeringConstants, ProgramCentralConstants, LibraryCentralConstants {
	// ### String Constants
	public static final String STRING_HYPHEN = "-";
	public static final String STRING_PIPELINE = "|";
	public static final String SORT_DIRECTION_ASCENDING = "ascending";
	public static final String SORT_DIRECTION_DECENDING = "decending";

	public static final String SEPARATOR_PIPELINE = "|";
	public static final String SEPARATOR_VERTICAL_BAR = "|";
	
	public static final String ADD_FLAG		= "ADD";
	public static final String MOD_FLAG		= "MOD";
	public static final String DEL_FLAG		= "DEL";
	
	public static final String SAVE_DIR			= "C:/temp/";
	public static final String SAVE_FILE_PREFIX	= "MQL_COMPARE_";
	public static final String SAVE_FILE_EXTENSION	= ".xls";
	public static final String SAVE_FILE_EXTENSION1 = ".xlsx";
	public static final String SAVE_FILE_EXTENSION2	= ".mql";
	
	public static final String VIEW_TYPE	= "VIEW_TYPE";
	public static final String BTNTYPE_VIEW	= "VIEW";
	public static final String BTNTYPE_FIND	= "FIND";
	
	public static final String FORMAT_MODIFIED	= "MM/dd/yyyy hh:mm:ss aa";
	public static final String FORMAT_EXPORT	= "YYYYMMDDhhmmss";
	
	static String getSelector(String sAttribute) {
		return "attribute[" + sAttribute + "]";
	}
	
	public static final String ATTRIBUTE	= "attribute";
	public static final String TYPE			= "type";
	public static final String POLICY		= "policy";
	public static final String PROGRAM		= "program";
	public static final String RELATIONSHIP	= "relationship";
	public static final String FORMAT		= "format";
	public static final String ROLE			= "role";
	public static final String COMMAND		= "command";
	public static final String MENU			= "menu";
	public static final String FORM			= "form";
	public static final String TABLE		= "table";
	public static final String CHANNEL		= "channel";
	public static final String PORTAL		= "portal";
	public static final String TRIGGER 		= "eService Trigger Program Parameters";
	public static final String GENERATOR	= "eService Object Generator";
	public static final String NUMBER_GENERATOR	= "eService Number Generator";
	public static final String OBJECT		= "Etc Object";
	
	
	static String[] getMQLTypeList() {
		String[] optArr	= { 
				ATTRIBUTE,
				TYPE,
				POLICY,
				PROGRAM,
				RELATIONSHIP,
				FORMAT,
				ROLE,
				COMMAND,
				MENU,
				FORM,
				TABLE,
				CHANNEL,
				PORTAL,
				TRIGGER,
				GENERATOR,
				OBJECT
			};
		
		return optArr;
	}
	
	static String[] getHierarchyTypeList() {
		String[] optArr	= {
//				ATTRIBUTE,
				TYPE,	// ATTRIBUTE
//				POLICY,
//				PROGRAM,
//				RELATIONSHIP,
//				FORMAT,
//				ROLE,
				COMMAND,	// HREF (TABLE, FORM ...)
				MENU,		// MENU, COMMAND
//				FORM,
//				TABLE,
				CHANNEL,	// COMMAND
				PORTAL		// CHANNEL
//				TRIGGER,
//				GENERATOR,
//				OBJECT
			};
		
		return optArr;
	}
	
	public static StringList getCompareOption(String sType) {
		StringList slOptionList	= new StringList();
		
		if(sType.equalsIgnoreCase(ATTRIBUTE)) {
			slOptionList.add("range");
			
		} else if(sType.equalsIgnoreCase(TYPE)) {
			slOptionList.add("immediateattribute");
			slOptionList.add("immediatetrigger");
			
		} else if(sType.equalsIgnoreCase(POLICY)) {
			slOptionList.add("type");
			slOptionList.add("state");
			
		} else if(sType.equalsIgnoreCase(PROGRAM)) {
			slOptionList.add("name");
			
		} else if(sType.equalsIgnoreCase(RELATIONSHIP)) {
			slOptionList.add("abstract");
			slOptionList.add("sparse");
			slOptionList.add("immediateattribute");
			slOptionList.add("immediatetrigger");
			slOptionList.add("fromtype");
			slOptionList.add("totype");
			
		} else if(sType.equalsIgnoreCase(FORMAT)) {
			slOptionList.add("hidden");
			slOptionList.add("mime");
			slOptionList.add("filesuffix");
			slOptionList.add("version");
			
		} else if(sType.equalsIgnoreCase(ROLE)) {
			slOptionList.add("maturity");
			slOptionList.add("category");
			slOptionList.add("site");
			slOptionList.add("hidden");
			slOptionList.add("parent");
			slOptionList.add("person");
			slOptionList.add("child");
			
		} else if(sType.equalsIgnoreCase(COMMAND)) {
			slOptionList.add("href");
			slOptionList.add("setting.value");
			
		} else if(sType.equalsIgnoreCase(MENU)) {
			slOptionList.add("command");
			slOptionList.add("menu");
			slOptionList.add("href");
			slOptionList.add("setting.value");
			
		} else if(sType.equalsIgnoreCase(FORM)) {
			slOptionList.add("field.setting.value");
			
		} else if(sType.equalsIgnoreCase(TABLE)) {
			slOptionList.add("column.setting.value");
			
		} else if(sType.equalsIgnoreCase(CHANNEL)) {
			slOptionList.add("command");
			slOptionList.add("setting.value");
			
		} else if(sType.equalsIgnoreCase(PORTAL)) {
			slOptionList.add("channel");
			slOptionList.add("setting.value");
		}
		
		return slOptionList;
	}
	
	
	/**
	 * getViewOption, getViewOptionChange Set
	 * required LowerCase
	 * @return
	 */
	public static StringList getViewOption() {
		StringList slViewList	= new StringList();
		
		slViewList.add("table");
		slViewList.add("toolbar");
		slViewList.add("portal");
		slViewList.add("form");
		slViewList.add("type");
		slViewList.add("relationship");
		slViewList.add("edit");
		slViewList.add("tablemenu");
		slViewList.add("expandprogrammenu");
		slViewList.add("resequencerelationship");
		slViewList.add("editrelationship");
		slViewList.add("objectid");
		slViewList.add("parentoid");
		slViewList.add("commandname");
		slViewList.add("portalcmdname");
		
		return slViewList;
	}
	
	public static Map getViewOptionChange() {
		Map mViewMap			= new HashMap();
		
		mViewMap.put("table", 			"table");
		mViewMap.put("commandname", 	"command");
		mViewMap.put("portalcmdname", 	"command");
		mViewMap.put("toolbar", 		"menu");
		mViewMap.put("tablemenu", 		"menu");
		mViewMap.put("expandprogrammenu", 		"menu");
		mViewMap.put("portal", 			"portal");
		mViewMap.put("form", 			"form");
		mViewMap.put("type", 			"type");
		mViewMap.put("relationship", 	"relationship");
		mViewMap.put("editrelationship", 	"relationship");
		mViewMap.put("resequencerelationship", 	"relationship");
		mViewMap.put("objectid", 	"Etc Object");
		mViewMap.put("parentoid", 	"Etc Object");
		
		return mViewMap;
	}
			
	
	static Map iconImageForTab()
	{
		Map mIconMap	= new HashMap();
		mIconMap.put(ADD_FLAG,		new ImageIcon(new Object(){}.getClass().getResource("/images/AddMQL.png")));
		mIconMap.put(MOD_FLAG,		new ImageIcon(new Object(){}.getClass().getResource("/images/ModifyMQL.png")));
		mIconMap.put(DEL_FLAG,		new ImageIcon(new Object(){}.getClass().getResource("/images/DeleteMQL.png")));
		mIconMap.put(ATTRIBUTE,		new ImageIcon(new Object(){}.getClass().getResource("/images/attrib.gif")));
		mIconMap.put(TYPE,			new ImageIcon(new Object(){}.getClass().getResource("/images/type.gif")));
		mIconMap.put(POLICY,		new ImageIcon(new Object(){}.getClass().getResource("/images/policy.gif")));
		mIconMap.put(PROGRAM,		new ImageIcon(new Object(){}.getClass().getResource("/images/program.gif")));
		mIconMap.put(RELATIONSHIP,	new ImageIcon(new Object(){}.getClass().getResource("/images/relship.gif")));
		mIconMap.put(FORMAT,		new ImageIcon(new Object(){}.getClass().getResource("/images/format.gif")));
		mIconMap.put(ROLE,			new ImageIcon(new Object(){}.getClass().getResource("/images/role.gif")));
		mIconMap.put(COMMAND,		new ImageIcon(new Object(){}.getClass().getResource("/images/command.gif")));
		mIconMap.put(MENU,			new ImageIcon(new Object(){}.getClass().getResource("/images/menu.gif")));
		mIconMap.put(FORM,			new ImageIcon(new Object(){}.getClass().getResource("/images/form.gif")));
		mIconMap.put(TABLE,			new ImageIcon(new Object(){}.getClass().getResource("/images/table.gif")));
		mIconMap.put(CHANNEL,		new ImageIcon(new Object(){}.getClass().getResource("/images/channel.gif")));
		mIconMap.put(PORTAL,		new ImageIcon(new Object(){}.getClass().getResource("/images/portal.gif")));
		mIconMap.put(TRIGGER,		new ImageIcon(new Object(){}.getClass().getResource("/images/trigger.gif")));
		mIconMap.put(GENERATOR,		new ImageIcon(new Object(){}.getClass().getResource("/images/object.gif")));
		mIconMap.put(OBJECT,		new ImageIcon(new Object(){}.getClass().getResource("/images/object.gif")));
		mIconMap.put("LOG",			new ImageIcon(new Object(){}.getClass().getResource("/images/LOG16.png")));
		
		return mIconMap;
	}
	
	static Map iconImageForList()
	{
		Map mIconMap	= new HashMap();
		mIconMap.put(ATTRIBUTE,		new ImageIcon(new Object(){}.getClass().getResource("/images/attrib.gif")));
		mIconMap.put(TYPE,			new ImageIcon(new Object(){}.getClass().getResource("/images/type.gif")));
		mIconMap.put(POLICY,		new ImageIcon(new Object(){}.getClass().getResource("/images/policy.gif")));
		mIconMap.put(PROGRAM,		new ImageIcon(new Object(){}.getClass().getResource("/images/program.gif")));
		mIconMap.put(RELATIONSHIP,	new ImageIcon(new Object(){}.getClass().getResource("/images/relship.gif")));
		mIconMap.put(FORMAT,		new ImageIcon(new Object(){}.getClass().getResource("/images/format.gif")));
		mIconMap.put(ROLE,			new ImageIcon(new Object(){}.getClass().getResource("/images/role.gif")));
		mIconMap.put(COMMAND,		new ImageIcon(new Object(){}.getClass().getResource("/images/command.gif")));
		mIconMap.put(MENU,			new ImageIcon(new Object(){}.getClass().getResource("/images/menu.gif")));
		mIconMap.put(FORM,			new ImageIcon(new Object(){}.getClass().getResource("/images/form.gif")));
		mIconMap.put(TABLE,			new ImageIcon(new Object(){}.getClass().getResource("/images/table.gif")));
		mIconMap.put(CHANNEL,		new ImageIcon(new Object(){}.getClass().getResource("/images/channel.gif")));
		mIconMap.put(PORTAL,		new ImageIcon(new Object(){}.getClass().getResource("/images/portal.gif")));
		mIconMap.put(TRIGGER,		new ImageIcon(new Object(){}.getClass().getResource("/images/trigger.gif")));
		mIconMap.put(GENERATOR,		new ImageIcon(new Object(){}.getClass().getResource("/images/object.gif")));
		mIconMap.put(OBJECT,		new ImageIcon(new Object(){}.getClass().getResource("/images/object.gif")));
		mIconMap.put("LOG",			new ImageIcon(new Object(){}.getClass().getResource("/images/LOG16.png")));
		
		return mIconMap;
	} 
	
	// Schema Util Constants
	public static final String CODE = "code";
    public static final String FIND_TRIGGER = "trigger";
    public static final String FIND_HREF = "href";
    public static final String EXPRESSIONTYPE = "expressiontype";
    public static final String EXPRESSION = "expression";
    public static final String DUMP = "dump";
    public static final String DOT = ".";
    public static final String SYSTEM = "system";
    public static final String SELECT = "select";
    public static final String SEMI_COLON = ";";
    public static final String VALUE = "value";
    public static final String PROPERTIES = "properties";
    public static final String PROPERTY = "property";
    public static final String SETTING = "setting";
    public static final String SETTINGS = "settings";
    public static final String UPDATE = "update";
    public static final String RANGE = "range";
    public static final String ALT = "alt";
    public static final String PRINT_TABLE = "print table ";
    public static final String PRINT_FORM = "print form ";
    public static final String LABEL = "label";
    public static final String NAME = "name";
    public static final String SORTTYPE = "sorttype";
    public static final String HREF = "href";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String COLUMN = "column";
    public static final String FIELD = "field";
    public static final String _TAB = "    ";
    public static final String TAB = "\t";
    public static final String RECORDSEP = "\n";
    public static final String SCHEMA_TABLE = "Table";
    public static final String SCHEMA_FORM = "Form";
    public static final String SCHEMA_COMMAND = "command";
    public static final String SCHEMA_MENU = "menu";
    public static final String SCHEMA_INSTALL = "install";
    public static final String SCHEMA_CHANNEL = "channel";
    public static final String SCHEMA_RELATIONSHIP = "relationship";
    public static final String SCHEMA_ATTRIBUTE = "attribute";
    public static final String SCHEMA_TYPE = "type";
    public static final String SCHEMA_POLICY = "policy";
    public static final String SCHEMA_PORTAL = "portal";
    public static final String SPACE = " ";
    public static final String SINGLE_QUOTE = "'";
    public static final String START_SQUARE_BRACKET = "[";
    public static final String END_SQUARE_BRACKET = "]";
    public static final String START_ANGLE_BRACKET = "{";
    public static final String END_ANGLE_BRACKET = "}";
    public static final String START_ROUND_BRACKET = "(";
    public static final String END_ROUND_BRACKET = ")";
    public static final String AND = " && ";
    public static final String OR = " || ";
    public static final String EQ = " == ";
    public static final String NEQ = " != ";
    public static final String SELECT_SEPERATOR = "|";
    public static final String NOTE_AREA	= "################################################################################################";
}
