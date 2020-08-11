package kr.co.atis.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.util.schema.SchemaAttribute;
import kr.co.atis.util.schema.SchemaChannel;
import kr.co.atis.util.schema.SchemaCommand;
import kr.co.atis.util.schema.SchemaForm;
import kr.co.atis.util.schema.SchemaFormat;
import kr.co.atis.util.schema.SchemaGenerator;
import kr.co.atis.util.schema.SchemaMenu;
import kr.co.atis.util.schema.SchemaObject;
import kr.co.atis.util.schema.SchemaPolicy;
import kr.co.atis.util.schema.SchemaPortal;
import kr.co.atis.util.schema.SchemaProgram;
import kr.co.atis.util.schema.SchemaRelationship;
import kr.co.atis.util.schema.SchemaRole;
import kr.co.atis.util.schema.SchemaTable;
import kr.co.atis.util.schema.SchemaTrigger;
import kr.co.atis.util.schema.SchemaType;
import matrix.db.Context;
import matrix.util.StringList;

/**
 * explanation
 *
 * find schema (ex)
 * execute program  emxSchemaUtil href '';
 * 
 * make schema (ex)
 * execute program emxSchemaUtil Table    '';
 * execute program emxSchemaUtil Form     '';
 * execute program emxSchemaUtil command  '';
 * execute program emxSchemaUtil menu     '';
 *
 * result location (ex)
 * C:\Documents and Settings\Administrator\Local Settings\Temp
 * ==> you can find location. it's method
 * 1. open cmd
 * 2. set temp (enter)
 */

/**
 * @author sangwon Jeon Modified by ihjang - Add Mod MQL
 */

public class SchemaUtil {
	public static String TEMP_TRIGGER_NAME = null;
	public static String TEMP_TRIGGER_REV = null;
	public static final String TRIGGER_TYPE = "eService Trigger Program Parameters";
	private static String TEMP_DIR = null;
	private static String EXPORT_DIR = null;
	private static final String _TAB = SchemaConstants._TAB;
	private static final String RECORDSEP = SchemaConstants.RECORDSEP;
	private static StringBuilder REMOVE_SCHMA = new StringBuilder();
	private static StringBuilder EXPORT_SCHMA = new StringBuilder();
	private static StringBuilder IMPORT_SCHMA = new StringBuilder();
	public static StringBuilder STRING_RESOURCE = new StringBuilder();
	private static List<SimpleDateFormat> formatList = null;

	/**
	 * Export
	 */
	private static boolean isDateRemove = false;

	/**
	*
	*/
	public SchemaUtil() {
		TEMP_DIR = System.getenv("TEMP");
	}

	/**
	 * Used by Trigger 아마 전체 적으로 사용 할 듯.
	 * 
	 * @param context
	 * @param sAttr
	 * @return
	 * @throws Exception
	 */
	public static String getTriggerAttrValue(Context context, String sAttr) throws Exception {

		String rv = getAttributeValue(context, TRIGGER_TYPE, TEMP_TRIGGER_NAME, TEMP_TRIGGER_REV, sAttr);
		return StringUtils.isBlank(rv) ? "" : rv;
	}

	private static String getAttributeValue(Context context, String sType, String sName, String sRev, String attrName) throws Exception {

		return StringUtils.substringAfter(MqlUtil.mqlCommand(context, new StringBuilder("print bus '").append(sType).append("' '").append(sName).append("' '").append(sRev).append("' select attribute[").append(attrName).append("].value").toString()), "=").trim();
	}

	/**
	 * @param context
	 * @param sName
	 * @return
	 * @throws Exception
	 */
	public static String getTableMQL(Context context, String[] sNames) throws Exception {

		for (int i = 0; i < sNames.length; i++) {
			System.out.println("sNames : " + sNames[i]);
			SchemaTable.getTableMQL(context, sNames[i]);
		}

		return "";
	}

	public static String getCommandMQL(Context context, String[] sNames) throws Exception {
		for (int i = 0; i < sNames.length; i++) {
			SchemaCommand.getCommandMQL(context, sNames[i]);
		}

		return "";
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Modify MQL Added by ihjang on 2018-02-07
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static String getSchema(Context context, String[] args) throws Exception {
		System.out.println("SchemaUtil.getSchema ADD");
		StringBuilder sbResult = new StringBuilder();
		StringBuilder sbMQLCmd = new StringBuilder();
		// String mqlCmd = "";
		try {
			if (args.length >= 3) {
				TEMP_DIR = args[2];
			}

			String sSchemaType = args[0];

			for (String arg : args) {
				System.out.println("    arg : " + arg);
			}

			if (SchemaConstants.ATTRIBUTE.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaAttribute.getAttributeMQL(context, args[1]));

			} else if (SchemaConstants.TYPE.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaType.getTypeMQL(context, args[1]));

			} else if (SchemaConstants.TABLE.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaTable.getTableMQL(context, args[1]));

			} else if (SchemaConstants.FORM.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaForm.getFormMQL(context, args[1]));

			} else if (SchemaConstants.FIND_HREF.equalsIgnoreCase(sSchemaType)) {
				// Not Used
//				sbMQLCmd.append(findCommandNMenu(context, args[1]));

			} else if (SchemaConstants.TRIGGER.equalsIgnoreCase(sSchemaType)) {
				args[0] = args[2];	// args[0] = Name
				args[1] = args[3];	// args[1] = Revision
				args[2] = args[4];	// args[2] = "ADD" or "MOD"
				sbMQLCmd.append(SchemaTrigger.getTriggerMQL(context, args));

			} else if (SchemaConstants.GENERATOR.equalsIgnoreCase(sSchemaType)) {
				args[0] = args[2];	// args[0] = Name
				args[1] = args[3];	// args[1] = Revision
				args[2] = args[4];	// args[2] = "" or "MOD"
				sbMQLCmd.append(SchemaGenerator.getGeneratorMQL(context, args));

			} else if (SchemaConstants.COMMAND.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaCommand.getCommandMQL(context, args[1]));

			} else if (SchemaConstants.MENU.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaMenu.getMenuMQL(context, args[1]));

			} else if (SchemaConstants.CHANNEL.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaChannel.getChannelMQL(context, args[1]));

			} else if (SchemaConstants.PORTAL.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaPortal.getPortalMQL(context, args[1]));

			} else if (SchemaConstants.RELATIONSHIP.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaRelationship.getRelationshipMQL(context, args[1]));

			} else if (SchemaConstants.FORMAT.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaFormat.getFormatMQL(context, args[1]));

			} else if (SchemaConstants.ROLE.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaRole.getRoleMQL(context, args[1]));

			} else if (SchemaConstants.POLICY.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaPolicy.getPolicyMQL(context, args[1]));

			} else if (SchemaConstants.PROGRAM.equalsIgnoreCase(sSchemaType)) {
				sbMQLCmd.append(SchemaProgram.getProgramMQL(context, args[1]));

			} else if (SchemaConstants.SCHEMA_INSTALL.equalsIgnoreCase(sSchemaType)) {

				EXPORT_DIR = TEMP_DIR + File.separator + "MQL" + File.separator;
				REMOVE_SCHMA = new StringBuilder();
				EXPORT_SCHMA = new StringBuilder();
				IMPORT_SCHMA = new StringBuilder();
				STRING_RESOURCE = new StringBuilder();

//				schemaMigration(context, args[1]);
			} else if (!BusinessViewMain.slMQLTypeList.contains(sSchemaType)) { // another type !!!!!!! Last case
//			} else if (SchemaConstants.OBJECT.equalsIgnoreCase(sSchemaType)) { // another type !!!!!!! Last case
				sbMQLCmd.append(SchemaObject.getObjectMQL(context, args));
				sSchemaType = args[1];
				args[0] = args[2];
				args[1] = args[3];
			}

			// get final Modified
			if (sbMQLCmd.length() > 0) {
				if (SchemaConstants.TRIGGER.equalsIgnoreCase(sSchemaType) || SchemaConstants.GENERATOR.equalsIgnoreCase(sSchemaType) || !BusinessViewMain.slMQLTypeList.contains(sSchemaType)) {
					sbResult.append(getLastFinalModifiedObject(context, sSchemaType, args[0], args[1]));
				} else {
					sbResult.append(getLastFinalModifiedBusiness(context, sSchemaType, args[1]));
				}
				sbResult.append(sbMQLCmd);
			}
		} catch (IllegalArgumentException ie) {
			System.out.println("\n\n" + ie.toString());
			System.out.println("\nUsage) exec prog emxSchemaUtil ADMIN_TYPE ADMIN_TYPE_NAME [OUTPUT_DIR]\n");
			System.out.println("\t- ADMIN_TYPE : command, form, menu, table, trigger");
			System.out.println("\t- ADMIN_TYPE_NAME : administrative object's name like ENCSearchMenu");
			System.out.println("\t- OUTPUT : output directory. Default is " + System.getenv("TEMP") + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			return sbResult.toString();
		}
	}

	/**
	 * get Modified schema
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static String getSchemaModify(Context ctx1, Context ctx2, String[] args) throws Exception {

		System.out.println("SchemaUtil.getSchema MOD");
		String mqlCmd = "";
		try {
			if (args == null) {
				throw new IllegalArgumentException();
			}

			if (args.length < 2) {
				throw new IllegalArgumentException();
			}

			if (args.length >= 3) {
				TEMP_DIR = args[2];
			}

			String sSchemaType = args[0];

			for (String arg : args) {
				System.out.println(" arg : " + arg);
			}

			if (SchemaConstants.TABLE.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaTable.getTableModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.FORM.equalsIgnoreCase(sSchemaType)) {
//				mqlCmd = SchemaForm.getFormMQL(ctx1, args[1]);
				mqlCmd = SchemaForm.getFormSchemaMod(ctx1, ctx2, args[1]);
				

			} else if (SchemaConstants.FIND_HREF.equalsIgnoreCase(sSchemaType)) {
				// mqlCmd = findCommandNMenu(context, args[1]);

			} else if (SchemaConstants.TRIGGER.equalsIgnoreCase(sSchemaType)) {
				args[0] = args[1];
				args[1] = args[2];
				args[2] = args[3];
				mqlCmd = SchemaTrigger.getTriggerModMQL(ctx1, ctx2, args);

			} else if (SchemaConstants.GENERATOR.equalsIgnoreCase(sSchemaType)) {
				args[0] = args[1];
				args[1] = args[2];
				mqlCmd = SchemaGenerator.getGeneratorModMQL(ctx1, ctx2, args);

			} else if (SchemaConstants.COMMAND.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaCommand.getCommandModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.MENU.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaMenu.getMenuModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.CHANNEL.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaChannel.getChannelModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.PORTAL.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaPortal.getPortalModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.RELATIONSHIP.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaRelationship.getRelationshipModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.FORMAT.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaFormat.getFormatModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.ROLE.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaRole.getRoleModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.ATTRIBUTE.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaAttribute.getAttributeModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.TYPE.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaType.getTypeModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.POLICY.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaPolicy.getPolicyModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.PROGRAM.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = SchemaProgram.getProgramModMQL(ctx1, ctx2, args[1]);

			} else if (SchemaConstants.SCHEMA_INSTALL.equalsIgnoreCase(sSchemaType)) {

				EXPORT_DIR = TEMP_DIR + File.separator + "MQL" + File.separator;
				REMOVE_SCHMA = new StringBuilder();
				EXPORT_SCHMA = new StringBuilder();
				IMPORT_SCHMA = new StringBuilder();
				STRING_RESOURCE = new StringBuilder();

				// schemaMigration(context, args[1]);
			}
		} catch (IllegalArgumentException ie) {

			System.out.println("\n\n" + ie.toString());
			System.out.println("\nUsage) exec prog emxSchemaUtil ADMIN_TYPE ADMIN_TYPE_NAME [OUTPUT_DIR]\n");
			System.out.println("\t- ADMIN_TYPE : command, form, menu, table, trigger");
			System.out.println("\t- ADMIN_TYPE_NAME : administrative object's name like ENCSearchMenu");
			System.out.println("\t- OUTPUT : output directory. Default is " + System.getenv("TEMP") + "\n");
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		} finally {
			return mqlCmd;
		}
	}

	/**
	 * get Delete schema (Trigger or Object only)
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static String getSchemaDelete(Context context, String[] args) throws Exception {

		System.out.println("SchemaUtil.getSchema DEL");
		String mqlCmd = "";
		try {
			if (args == null) {
				throw new IllegalArgumentException();
			}

			if (args.length < 2) {
				throw new IllegalArgumentException();
			}

			if (args.length >= 3) {
				TEMP_DIR = args[2];
			}

			String sSchemaType = args[0];

			for (String arg : args) {
				System.out.println(" arg : " + arg);
			}

			if (SchemaConstants.TRIGGER.equalsIgnoreCase(sSchemaType)) {
				args[0] = args[1];
				args[1] = args[2];
				args[2] = args[3];
				mqlCmd = SchemaTrigger.getTriggerDeleteMQL(context, args);

			} else if (SchemaConstants.GENERATOR.equalsIgnoreCase(sSchemaType)) {
				args[0] = args[1];
				args[1] = args[2];
				// mqlCmd = SchemaGenerator.getGeneratorModMQL(ctx1, args);
			} else if (SchemaConstants.TABLE.equalsIgnoreCase(sSchemaType) 			|| SchemaConstants.FORM.equalsIgnoreCase(sSchemaType) 
					|| SchemaConstants.COMMAND.equalsIgnoreCase(sSchemaType) 		|| SchemaConstants.MENU.equalsIgnoreCase(sSchemaType)
					|| SchemaConstants.CHANNEL.equalsIgnoreCase(sSchemaType) 		|| SchemaConstants.PORTAL.equalsIgnoreCase(sSchemaType)
					|| SchemaConstants.RELATIONSHIP.equalsIgnoreCase(sSchemaType) 	|| SchemaConstants.FORMAT.equalsIgnoreCase(sSchemaType)
					|| SchemaConstants.ROLE.equalsIgnoreCase(sSchemaType) 			|| SchemaConstants.ATTRIBUTE.equalsIgnoreCase(sSchemaType)
					|| SchemaConstants.TYPE.equalsIgnoreCase(sSchemaType) 			|| SchemaConstants.POLICY.equalsIgnoreCase(sSchemaType)
					|| SchemaConstants.PROGRAM.equalsIgnoreCase(sSchemaType)) {
				mqlCmd = getDeleteMQL(context, sSchemaType, args[1]);
			}
		} catch (IllegalArgumentException ie) {

			System.out.println("\n\n" + ie.toString());
			System.out.println("\nUsage) exec prog emxSchemaUtil ADMIN_TYPE ADMIN_TYPE_NAME [OUTPUT_DIR]\n");
			System.out.println("\t- ADMIN_TYPE : command, form, menu, table, trigger");
			System.out.println("\t- ADMIN_TYPE_NAME : administrative object's name like ENCSearchMenu");
			System.out.println("\t- OUTPUT : output directory. Default is " + System.getenv("TEMP") + "\n");
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		} finally {
			return mqlCmd;
		}
	}

	/**
	 * Used by Policy
	 * 
	 * @param ctx1
	 * @param ctx2
	 * @param name
	 * @param strOri
	 * @return
	 * @throws Exception
	 */
	public static String getPolicyStateModMQL(Context ctx1, Context ctx2, String name, String strOri) throws Exception {
		StringBuilder sb = new StringBuilder();

		String s4Ori = MqlUtil.mqlCommand(ctx1, "print policy '" + name + "' select state[" + strOri + "].revisionable state[" + strOri + "].versionable state[" + strOri + "].autopromote state[" + strOri + "].checkouthistory dump " + SchemaConstants.SELECT_SEPERATOR);
		String s4Mod = MqlUtil.mqlCommand(ctx2, "print policy '" + name + "' select state[" + strOri + "].revisionable state[" + strOri + "].versionable state[" + strOri + "].autopromote state[" + strOri + "].checkouthistory dump " + SchemaConstants.SELECT_SEPERATOR);
		StringList sl4Ori = FrameworkUtil.split(s4Ori, SchemaConstants.SELECT_SEPERATOR);
		StringList sl4Mod = FrameworkUtil.split(s4Mod, SchemaConstants.SELECT_SEPERATOR);

		if (!sl4Ori.get(0).equals(sl4Mod.get(0)))
			sb.append(_TAB).append(_TAB).append("revision").append(" ").append(sl4Mod.get(0).toString().toLowerCase()).append(RECORDSEP);

		if (!sl4Ori.get(1).equals(sl4Mod.get(1)))
			sb.append(_TAB).append(_TAB).append("version").append("  ").append(sl4Mod.get(1).toString().toLowerCase()).append(RECORDSEP);

		if (!sl4Ori.get(2).equals(sl4Mod.get(2)))
			sb.append(_TAB).append(_TAB).append("promote").append("  ").append(sl4Mod.get(2).toString().toLowerCase()).append(RECORDSEP);

		if (!sl4Ori.get(3).equals(sl4Mod.get(3)))
			sb.append(_TAB).append(_TAB).append("checkouthistory").append("  ").append(sl4Mod.get(3).toString().toLowerCase()).append(RECORDSEP);

		String s8Ori = MqlUtil.mqlCommand(ctx1, "print policy '" + name + "' select state[" + strOri + "].owneraccess dump " + SchemaConstants.SELECT_SEPERATOR);
		String s8Mod = MqlUtil.mqlCommand(ctx2, "print policy '" + name + "' select state[" + strOri + "].owneraccess dump " + SchemaConstants.SELECT_SEPERATOR);
		if (!s8Ori.equals(s8Mod))
			sb.append(_TAB).append(_TAB).append("owner").append("  ").append(s8Ori).append(RECORDSEP);

		String s9Ori = MqlUtil.mqlCommand(ctx1, "print policy '" + name + "' select state[" + strOri + "].publicaccess dump " + SchemaConstants.SELECT_SEPERATOR);
		String s9Mod = MqlUtil.mqlCommand(ctx2, "print policy '" + name + "' select state[" + strOri + "].publicaccess dump " + SchemaConstants.SELECT_SEPERATOR);
		if (!s9Ori.equals(s9Mod))
			sb.append(_TAB).append(_TAB).append("public").append("  ").append(s9Ori).append(RECORDSEP);

//		String s10Ori = MqlUtil.mqlCommand(ctx1, "print policy '" + name + "' select state[" + strOri + "].access");
//		String s10Mod = MqlUtil.mqlCommand(ctx2, "print policy '" + name + "' select state[" + strOri + "].access");
//		StringList sl5Ori = FrameworkUtil.split(s10Ori, "\n");
//		StringList sl5Mod = FrameworkUtil.split(s10Mod, "\n");
//		for (Iterator itr5 = sl5Ori.iterator(); itr5.hasNext();) {
//			String str2 = StringUtils.trim((String) itr5.next());
//			if (StringUtils.isNotBlank(str2) && !"null".equals(str2) && !str2.startsWith("policy")) {
//				String[] strArr1 = StringUtils.split(str2, "=");
//				String strUser = StringUtils.remove(strArr1[0].trim(), "state[" + strOri + "].access");
//				strUser = StringUtils.substringBetween(strUser, "[", "]");
//				sb.append(_TAB).append(_TAB).append("user").append("  '").append(strUser).append("' ").append(strArr1[1].trim()).append(RECORDSEP);
//			}
//		}
		String s10Ori 		= MqlUtil.mqlCommand(ctx1, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user.login").toString());
		String s10Mod 		= MqlUtil.mqlCommand(ctx2, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user.login").toString());
		String s11Ori 		= MqlUtil.mqlCommand(ctx1, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user").toString());                
        String s11Mod 		= MqlUtil.mqlCommand(ctx2, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user").toString());                
        String s12Ori 		= MqlUtil.mqlCommand(ctx1, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user.access").toString());
        String s12Mod 		= MqlUtil.mqlCommand(ctx2, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user.access").toString());

        StringList sl5Ori 	= FrameworkUtil.split(s10Ori, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user
        StringList sl6Ori 	= FrameworkUtil.split(s11Ori, "\n");	// 0 line ~ user
		StringList sl7Ori 	= FrameworkUtil.split(s12Ori, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user
		StringList sl5Mod 	= FrameworkUtil.split(s10Mod, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user
		StringList sl6Mod 	= FrameworkUtil.split(s11Mod, "\n");	// 0 line ~ user
		StringList sl7Mod 	= FrameworkUtil.split(s12Mod, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user

		StringList slAccOri	= new StringList();
		StringList slAccMod	= new StringList();
		if(sl6Ori.size() > 0) {	// exclude public, owner
			for(int j = 1; j < sl6Ori.size(); j++)
            {
            	String str2 = StringUtils.trim((String) sl5Ori.get(j + 2));
            	String str3 = StringUtils.trim((String) sl6Ori.get(j));
            	String str4 = StringUtils.trim((String) sl7Ori.get(j + 2));                
            
            	if(StringUtils.isNotBlank(str2) && !"null".equals(str2) && !str2.startsWith("policy"))
            	{
            		String sLogin		= StringUtils.split(str2, "=")[1].trim();
            		String sUser  		= StringUtils.split(str3, "=")[1].trim();
            		StringList slUsers = FrameworkUtil.split(sUser, "|");
            		String sAccess		= StringUtils.split(str4, "=")[1].trim();
            		StringBuffer sbTmp	= new StringBuffer(_TAB).append(_TAB);
            		if(sLogin.equalsIgnoreCase("true")) {
            			sbTmp.append("login user");
            		} else {
            			sbTmp.append("user");
            		}
            		sbTmp.append(" '").append(slUsers.get(0)).append("' ");
            		if(slUsers.size() > 1) {
            			sbTmp.append(" key '").append(slUsers.get(1)).append("' ");
            		}
            		sbTmp.append("  ").append(sAccess).append(RECORDSEP);
            		slAccOri.add(sbTmp.toString());
            	}
            }
		}
		if(sl6Mod.size() > 0) {	// exclude public, owner
			for(int j = 1; j < sl6Mod.size(); j++)
			{
				String str2 = StringUtils.trim((String) sl5Mod.get(j + 2));
				String str3 = StringUtils.trim((String) sl6Mod.get(j));
				String str4 = StringUtils.trim((String) sl7Mod.get(j + 2));                
				
				if(StringUtils.isNotBlank(str2) && !"null".equals(str2) && !str2.startsWith("policy"))
				{
					String sLogin		= StringUtils.split(str2, "=")[1].trim();
					String sUser  		= StringUtils.split(str3, "=")[1].trim();
					StringList slUsers = FrameworkUtil.split(sUser, "|");
					String sAccess		= StringUtils.split(str4, "=")[1].trim();
					StringBuffer sbTmp	= new StringBuffer(_TAB).append(_TAB);
					if(sLogin.equalsIgnoreCase("true")) {
						sbTmp.append("login user");
					} else {
						sbTmp.append("user");
					}
					sbTmp.append(" '").append(slUsers.get(0)).append("' ");
					if(slUsers.size() > 1) {
						sbTmp.append(" key '").append(slUsers.get(1)).append("' ");
					}
					sbTmp.append("  ").append(sAccess).append(RECORDSEP);
					slAccMod.add(sbTmp.toString());
				}
			}
		}
		
		for(int j = 0; j < slAccOri.size(); j++) {
			String sOri	= (String) slAccOri.get(j);
			if(!slAccMod.contains(sOri)) {
				sb.append(sOri);
			}
		}
		for(int j = 0; j < slAccMod.size(); j++) {
			String sMod	= (String) slAccMod.get(j);
			if(!slAccOri.contains(sMod)) {
				sb.append("remove ").append(sMod);
			}
		}

		return sb.toString();
	}

	static public boolean isNullString(String string) {
		return StringUtils.isEmpty(("null".equals(string) || "undefined".equals(string) ? "" : string));
	}

	/**
	 * Used by Menu, Portal, Generator
	 * 
	 * @param str
	 * @return
	 */
	public static String nullToEmpty(String str) {
		return ((isNullString(str)) ? "" : str);
	}

	/**
	 * Used by Attribute, Type, Policy, Relationship, Command, Menu, Policy,
	 * Table, Channel, Portal
	 * 
	 * @param sType
	 * @param sName
	 * @return
	 */
	public static String createBusinessHeaderInfo(String sType, String sName, boolean isCreate) {
		StringBuilder sb = new StringBuilder(createBusinessOnlyHeader(sType, sName));

		if (isCreate) {
			sb.append("#copy ").append(sType).append(" '").append(sName).append("' '").append(sName).append("_backup';").append(RECORDSEP);
			sb.append("#del ").append(sType).append(" '").append(sName).append("';").append(SchemaConstants.RECORDSEP);
			sb.append("add ").append(sType).append(" '").append(sName).append("'").append(SchemaConstants.RECORDSEP);
		} else {
			sb.append("#del ").append(sType).append(" '").append(sName).append("';").append(SchemaConstants.RECORDSEP);
			sb.append("mod ").append(sType).append(" '").append(sName).append("'").append(SchemaConstants.RECORDSEP);
		}

		return sb.toString();
	}

	public static String createBusinessOnlyHeader(String sType, String sName) {
		StringBuilder sb = new StringBuilder();

		sb.append("# Extract : ").append(new SimpleDateFormat("yyyy. MM. dd").format(new Date())).append(RECORDSEP);
		sb.append("# Admin Type : ").append(sType).append(RECORDSEP);
		sb.append("# Name : ").append(sName).append(RECORDSEP);
		sb.append(SchemaConstants.NOTE_AREA).append(RECORDSEP).append(RECORDSEP);

		return sb.toString();
	}

	/**
	 * Used by Trigger, Generator
	 * 
	 * @param sName
	 * @return
	 */
	public static String createObjectMQLHeader(String sName) {
		StringBuilder sb = new StringBuilder();

		sb.append("# Extract : ").append(new SimpleDateFormat("yyyy. MM. dd").format(new Date())).append(RECORDSEP);
		sb.append("# Name : ").append(sName).append(RECORDSEP);
		sb.append(SchemaConstants.NOTE_AREA).append(RECORDSEP).append(RECORDSEP);

		return sb.toString();
	}

	/**
	 * Used by Type, Policy, Relationship
	 * Type, Relationship exists immediatetrigger
	 * 
	 * @param context
	 * @param sType
	 * @param sQuery
	 * @return
	 */
	public static String getTriggerForBusiness(Context context, String sType, String sQuery) {
		StringBuilder sbResult = new StringBuilder();

		try {
			String sTrigger = MqlUtil.mqlCommand(context, sQuery);
			String sTriggerImm = MqlUtil.mqlCommand(context, sQuery.replace("immediatetrigger", "trigger"));
			StringList slTriggerImm = new StringList();
			// Immediate Trigger
			if (sTrigger != null && !"".equals(sTrigger)) {
				if (SchemaConstants.POLICY.equalsIgnoreCase(sType))
					sbResult.append(_TAB);
				sbResult.append(_TAB).append("# Immediate Trigger").append(RECORDSEP);
				StringList slTrigger = FrameworkUtil.split(sTrigger, "|");
				for (Iterator itrTrigger = slTrigger.iterator(); itrTrigger.hasNext();) {
					String sTr = (String) itrTrigger.next();
					slTriggerImm.add(sTr);
					String[] strArrTrg = sTr.split(":");

					String strTriggerType = strArrTrg[0];
					String strTriggerName = StringUtils.substringBetween(strArrTrg[1], "(", ")");

					if (SchemaConstants.POLICY.equalsIgnoreCase(sType))
						sbResult.append(_TAB);
					sbResult.append(_TAB).append("trigger ");
					if (strTriggerType.endsWith("Action")) {

						sbResult.append(StringUtils.substringBefore(strTriggerType, "Action")).append(" action");
					} else if (strTriggerType.endsWith("Override")) {

						sbResult.append(StringUtils.substringBefore(strTriggerType, "Override")).append(" override");
					} else if (strTriggerType.endsWith("Check")) {

						sbResult.append(StringUtils.substringBefore(strTriggerType, "Check")).append(" check");
					}
					sbResult.append(" emxTriggerManager input '").append(strTriggerName).append("'").append(RECORDSEP);
				}
			}

			// Inherited Trigger
			if (sTriggerImm != null && !"".equals(sTriggerImm) && !sType.equalsIgnoreCase(SchemaConstants.POLICY)) {
				sbResult.append(_TAB).append("# Inherited Trigger").append(RECORDSEP);
				StringList slTrigger = FrameworkUtil.split(sTriggerImm, "|");
				for (Iterator itrTrigger = slTrigger.iterator(); itrTrigger.hasNext();) {
					String sTr = (String) itrTrigger.next();
					if (slTriggerImm.indexOf(sTr) >= 0) {
						slTriggerImm.remove(slTriggerImm.indexOf(sTr));
						continue;
					}
					String[] strArrTrg = sTr.split(":");
					String strTriggerType = strArrTrg[0];
					String strTriggerName = StringUtils.substringBetween(strArrTrg[1], "(", ")");

					if (SchemaConstants.POLICY.equalsIgnoreCase(sType))
						sbResult.append(_TAB);
					sbResult.append(_TAB).append("#trigger ");
					if (strTriggerType.endsWith("Action")) {

						sbResult.append(StringUtils.substringBefore(strTriggerType, "Action")).append(" action");
					} else if (strTriggerType.endsWith("Override")) {

						sbResult.append(StringUtils.substringBefore(strTriggerType, "Override")).append(" override");
					} else if (strTriggerType.endsWith("Check")) {

						sbResult.append(StringUtils.substringBefore(strTriggerType, "Check")).append(" check");
					}
					sbResult.append(" emxTriggerManager input '").append(strTriggerName).append("'").append(RECORDSEP);
				}
			}
		} catch (FrameworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sbResult.toString();
	}


	/**
	 * Used by Attribute,
	 * 
	 * @param context
	 * @param sType
	 * @param name
	 * @return
	 */
	public static String getPropertyForBusiness(Context context, String sType, String name) {
		StringBuilder sbResult = new StringBuilder();
		try {
			String propertys = MqlUtil.mqlCommand(context, new StringBuilder("print '").append(sType).append("' '").append(name).append("' select property.name dump '#@#'").toString());
			String propertyVals = MqlUtil.mqlCommand(context, new StringBuilder("print '").append(sType).append("' '").append(name).append("' select property.value dump '#@#'").toString());
			String[] slproperty = propertys.split("#@#");
			String[] slpropertyValue = propertyVals.split("#@#");

			if(propertys.trim().equals("")) return "";
			
			int iValSize = slpropertyValue.length;
			for (int i = 0; i < iValSize; i++) {
				String prop = (String) slpropertyValue[i];
				String sPk = (String) slproperty[i];

				sbResult.append(_TAB).append("property ").append(_TAB).append(appendEmptySpace(sPk)).append(" value '").append(prop).append("'").append(RECORDSEP);
			}
		} catch (FrameworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sbResult.toString();
	}

	public static String getPropertyForBusinessMap(Context context, String sKey, Map mProperties) {
		return getPropertyForBusinessMap(context, sKey, mProperties, false);
	}

	public static String getPropertyForBusinessMap(Context context, String sKey, Map mProperties, boolean isAddTab) {
		StringBuilder sbResult = new StringBuilder();

		for (Iterator propKeyItr = mProperties.keySet().iterator(); propKeyItr.hasNext();) {
			String sPropKey = (String) propKeyItr.next();
			String sPropVal = (String) mProperties.get(sPropKey);
			sPropVal = sPropVal.replaceAll("\"", "'");

			if (isAddTab)
				sbResult.append(_TAB);
			if (SchemaConstants.PROPERTY.equals(sKey))
				sbResult.append(_TAB).append(sKey).append(_TAB).append(appendEmptySpace(sPropKey)).append(" value \"").append(sPropVal).append("\"").append(RECORDSEP);
			else
				sbResult.append(_TAB).append(sKey).append(_TAB).append(appendEmptySpace(sPropKey, 25)).append(" \"").append(sPropVal).append("\"").append(RECORDSEP);
		}

		return sbResult.toString();
	}

	/**
	 * Property Empty Size Append (Key Length 20)
	 * 
	 * @param sKey
	 * @return
	 */
	public static String appendEmptySpace(String sKey) {
		return appendEmptySpace(sKey, 20);
	}

	public static String appendEmptySpace(String sKey, int maxLth) {
		StringBuilder sbKey = new StringBuilder("'").append(sKey).append("'");
		int iPropertyLth = sbKey.length();
		if (iPropertyLth < maxLth) {
			for (int j = iPropertyLth; j < maxLth; j++) {
				sbKey.append(" ");
			}
		}

		return sbKey.toString();
	}

	/**
	 * Object (Trigger, Generator) - get Modified
	 * 
	 * @param context
	 * @param sName
	 * @return
	 */
	private static String getLastFinalModifiedObject(Context context, String sType, String sName, String sRevision) throws Exception {
//		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa", Locale.US);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy. MM. dd hh:mm:ss aaa", Locale.US);
		StringBuilder sbResult = new StringBuilder();
		String sResult = "";
		try {
			sbResult.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
			sResult = MqlUtil.mqlCommand(context, new StringBuilder("print bus '").append(sType).append("' '").append(sName).append("' '").append(sRevision).append("' select originated modified dump ").toString());
			String[] results = sResult.split(",");
			Date originated = autoChangeDate(results[0]);
			Date modified = autoChangeDate(results[1]);
			sbResult.append("# Created       : ").append(sdf2.format(originated)).append(RECORDSEP);
			sbResult.append("# Last Modified : ").append(sdf2.format(modified)).append(RECORDSEP);
		} catch (Exception e) {
			throw e;
		}
		return sbResult.toString();
	}

	/**
	 * Not Object (Trigger, Generator) - get Modified
	 * 
	 * @param context
	 * @param sName
	 * @return
	 */
	private static String getLastFinalModifiedBusiness(Context context, String sType, String sName) throws Exception {
//		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.US);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy. MM. dd hh:mm:ss aaa", Locale.US);
		StringBuilder sbResult = new StringBuilder();
		String sResult = "";
		try {
			sbResult.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
			sResult = MqlUtil.mqlCommand(context, new StringBuilder("print '").append(sType).append("' '").append(sName).append("' ").append(sType.equalsIgnoreCase("table") ? "system" : "").append(" select originated modified dump ','").toString());
			String[] results = sResult.split(",");

			Date originated = autoChangeDate(results[0]);
			Date modified = autoChangeDate(results[1]);
			
			sbResult.append("# Created       : ").append(sdf2.format(originated)).append(RECORDSEP);
			sbResult.append("# Last Modified : ").append(sdf2.format(modified)).append(RECORDSEP);
		} catch (Exception e) {
			throw e;
		}
		return sbResult.toString();
	}

	public static String getData(Context context, String sType, String sName, String sSelect) {

		try {
			return MqlUtil.mqlCommand(context, new StringBuilder("print ").append(sType).append(" '").append(sName).append("' ").append(sType.equalsIgnoreCase("table") ? "system" : "").append(" select ").append(sSelect).append(" dump |").toString());
		} catch (FrameworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static void settingDataLine(StringBuilder sbResult, String sKey) { // Key Only View
		settingDataLine(sbResult, sKey, "", 0, false);
	}

	public static void settingDataLine(StringBuilder sbResult, String sKey, String sData, int iSpace) {
		settingDataLine(sbResult, sKey, sData, iSpace, true);
	}

	public static void settingDataLine(StringBuilder sbResult, String sKey, String sData, int iSpace, boolean isViewData) {
		sbResult.append(_TAB).append(sKey);
		if (isViewData) {
			for (int i = 0; i < iSpace - sKey.length(); i++) {
				sbResult.append(" ");
			}
			sbResult.append("\"").append(sData).append("\"");
		}
		sbResult.append(SchemaConstants.RECORDSEP);
	}
	
	private static String getDeleteMQL(Context context, String sType, String sName) {
		StringBuffer sb	= new StringBuffer();
		
		sb.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
		sb.append(createBusinessOnlyHeader(sType, sName));
		
		sb.append("#del '").append(sType).append("' '").append(sName).append("'");
		if(sType.equalsIgnoreCase(SchemaConstants.TABLE))
			sb.append(" system");
		sb.append(";");

		
		return sb.toString();
	}
	
	public static Date autoChangeDate(String date)
	{
		formatList = null;
		if (formatList == null) {
			formatList = new ArrayList<SimpleDateFormat>();
//			formatList.add(defaultDisplay);
//			formatList.add(dateFormat);
//			formatList.add(calFormat);
//			formatList.add(envFormat);
			formatList.add(new SimpleDateFormat("yyyy. M. d"));
			formatList.add(new SimpleDateFormat("yyyy. MM. dd"));
			formatList.add(new SimpleDateFormat("yyyy-MM-dd"));
			formatList.add(new SimpleDateFormat("yyyy-MM-dd a h:mm:ss"));
			formatList.add(new SimpleDateFormat("yyyy-MM-dd aaa h:mm:ss"));
			formatList.add(new SimpleDateFormat("yyyy-MM-dd a hh:mm:ss"));
			formatList.add(new SimpleDateFormat("yyyymmdd"));
			formatList.add(new SimpleDateFormat("yyyy/MM/dd"));
			formatList.add(new SimpleDateFormat("MM/dd/yyyy"));
			formatList.add(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss"));
			formatList.add(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa"));
		}
		Date dReturn = recursiveFindFormat(date, formatList);
		return dReturn;
	}

	/**
	 * recursive find format
	 * 
	 * @param String
	 * @param int
	 * @param String
	 * @return Date
     */
	private static Date recursiveFindFormat(String date, List<SimpleDateFormat> formatList)
    {
		Date dDate = null;
		SimpleDateFormat sdf = null;
		for (Iterator<SimpleDateFormat> itr = formatList.iterator(); itr.hasNext();) {
			try {
				sdf = itr.next();
				dDate = sdf.parse(date);
				int i = date.indexOf("/");
				if (i >= 0) {
					String pattern = sdf.toPattern();
					if (date.substring(0, i).length() != pattern.substring(0, pattern.indexOf("/")).length()) {
						continue;
					}
				}
				break;
			} catch (Exception e) {
			}
		}
		return dDate;
	}
}
