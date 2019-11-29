package kr.co.atis.util.schema;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.util.MqlUtil;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;

public class SchemaTrigger {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	
	/**
     * Copy from getCreateTriggerMQL()
     * 저장 부분 제거.
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
	public static String getTriggerMQL(Context context, String[] args) throws Exception {
		StringBuilder sb 			= new StringBuilder();
		try {
			String sTriggerName 	= args[0];
			String sTriggerRevision = args[1];
			String sTriggerFlag		= "";
			
			if(args.length > 3)
				sTriggerFlag		= args[2];	// "ADD" or "MOD"

			SchemaUtil.TEMP_TRIGGER_NAME		= sTriggerName;
			SchemaUtil.TEMP_TRIGGER_REV 		= sTriggerRevision;

			String sCmdBase 		= new StringBuilder("print bus 'eService Trigger Program Parameters' '").append(sTriggerName).append("' '").append(sTriggerRevision).append("'").toString();
			String sCmdSelMehod 	= new StringBuilder(sCmdBase).append(" select attribute[eService Method Name].value").toString();
			String sCmdProgArg1 	= new StringBuilder(sCmdBase).append(" select attribute[eService Program Argument 1].value").toString();

			String mqlRetProgArg1 	= MqlUtil.mqlCommand(context, sCmdProgArg1);
			mqlRetProgArg1 			= StringUtils.substringAfter(mqlRetProgArg1, "=").trim();
			String sMethodName 		= StringUtils.substringAfter(MqlUtil.mqlCommand(context, sCmdSelMehod), "=").trim();

			sb.append(SchemaUtil.createObjectMQLHeader(sTriggerName));
			if(!SchemaConstants.MOD_FLAG.equals(sTriggerFlag))
				sb.append("#");
			sb.append("del bus 'eService Trigger Program Parameters' '").append(sTriggerName).append("' '").append(sTriggerRevision).append("';").append(RECORDSEP);
			sb.append("add bus 'eService Trigger Program Parameters' '").append(sTriggerName).append("' '").append(sTriggerRevision).append("'").append(RECORDSEP);
			sb.append(_TAB).append("policy 'eService Trigger Program Policy'").append(RECORDSEP);
			sb.append(_TAB).append("vault 'eService Administration'").append(RECORDSEP);
			sb.append(_TAB).append("owner 'creator'").append(RECORDSEP);
			sb.append(_TAB).append("state 'Active' schedule ''").append(RECORDSEP);
			sb.append(_TAB).append(_TAB).append(SchemaUtil.appendEmptySpace("eService Sequence Number", 33)).append("'").append(SchemaUtil.getTriggerAttrValue(context, "eService Sequence Number")).append("'").append(RECORDSEP);
			sb.append(_TAB).append(_TAB).append(SchemaUtil.appendEmptySpace("eService Program Name", 33)).append("'").append(SchemaUtil.getTriggerAttrValue(context, "eService Program Name")).append("'").append(RECORDSEP);

			if (StringUtils.isNotBlank(sMethodName)) {
				sb.append(_TAB).append(_TAB).append(SchemaUtil.appendEmptySpace("eService Method Name", 33)).append("'").append(sMethodName).append("'").append(RECORDSEP);
			}
			sb.append(_TAB).append(_TAB).append(SchemaUtil.appendEmptySpace("eService Constructor Arguments", 33)).append("'").append(SchemaUtil.getTriggerAttrValue(context, "eService Constructor Arguments")).append("'").append(RECORDSEP);

			// eService Program Argument 1 ~ 15 Check.
			for (int i = 1; i <= 15; i++) {
				String attrName 		= "eService Program Argument " + i;
				String sCmdProgArg 		= new StringBuilder(sCmdBase).append(" select attribute[").append(attrName).append("].value").toString();
				String sMqlRetPrgArg 	= MqlUtil.mqlCommand(context, sCmdProgArg);
				String attrValue= StringUtils.substringAfter(sMqlRetPrgArg, "=").trim();

				if (StringUtils.isNotBlank(attrValue)) {
					
					sb.append(_TAB).append(_TAB).append(SchemaUtil.appendEmptySpace(attrName, 33)).append("'").append(attrValue).append("'").append(RECORDSEP);
				}
			}
			sb.append(";").append(RECORDSEP);
			sb.append("promote bus 'eService Trigger Program Parameters' '").append(sTriggerName).append("' '").append(sTriggerRevision).append("';").append(RECORDSEP);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
	/**
     * Copy from getCreateTriggerMQL()
     * 저장 부분 제거.
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
	public static String getTriggerModMQL(Context ctx1, Context ctx2, String[] args) throws Exception {
		StringBuilder sb 			= new StringBuilder();
		try {
			sb.append(getTriggerMQL(ctx1, args));
		} catch (Exception e) {

			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
	/**
	 * DeleteTriggerMQL()
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static String getTriggerDeleteMQL(Context context, String[] args) throws Exception {
		StringBuilder sb 			= new StringBuilder();
		try {
			String sTriggerName 	= args[0];
			String sTriggerRevision = args[1];
			
			sb.append("#delete bus 'eService Trigger Program Parameters' '").append(sTriggerName).append("' '").append(sTriggerRevision).append("'").toString();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return sb.toString();
	}
}
