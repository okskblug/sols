package kr.co.atis.util.schema;

import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class SchemaGenerator {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	
	/**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
	public static String getGeneratorMQL(Context context, String[] args) throws Exception {
		StringBuilder sbDel			= new StringBuilder();
		StringBuilder sb 			= new StringBuilder();
		try {
			String sGeneratorName 	= args[0];
			String sGeneratorRevision = args[1];
			String sGeneratorFlag	= "";
			
			if(args.length > 3)
				sGeneratorFlag		= args[2];	// "" or "MOD"

			String sDefault			= " select policy vault owner state dump '|'";
			String sCmdBase			= new StringBuilder("print bus 'eService Object Generator' ").append(sGeneratorName).append(" ").append("'").append(sGeneratorRevision).append("'").toString();
			String sNumberGenerator	= new StringBuilder(sCmdBase).append(" select from[eService Number Generator].to.name from[eService Number Generator].to.revision dump '|'").toString();
			String sDefaultInfo		= new StringBuilder(sCmdBase).append(sDefault).toString();
			String sAttrList		= new StringBuilder(sCmdBase).append(" select attribute[*]").toString();
			
			sb.append("add bus 'eService Object Generator' '").append(sGeneratorName).append("' '").append(sGeneratorRevision).append("'").append(RECORDSEP);
			
			sDefaultInfo			= MqlUtil.mqlCommand(context, sDefaultInfo);
			StringList slInfo		= FrameworkUtil.split(sDefaultInfo, SchemaConstants.SELECT_SEPERATOR);
			sb.append(_TAB).append("policy '").append(SchemaUtil.nullToEmpty((String) slInfo.get(0))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("vault  '").append(SchemaUtil.nullToEmpty((String) slInfo.get(1))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("owner  '").append(SchemaUtil.nullToEmpty((String) slInfo.get(2))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("state  '").append(SchemaUtil.nullToEmpty((String) slInfo.get(3))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("schedule ''").append(RECORDSEP);
			
			sAttrList				= MqlUtil.mqlCommand(context, sAttrList);
			StringList slAttribute	= FrameworkUtil.split(sAttrList, "\n");
			int iAttrSize			= slAttribute.size();
			for(int i = 0; i < iAttrSize; i++)
			{
				String sAttr		= SchemaUtil.nullToEmpty((String) slAttribute.get(i)).trim();
				if(sAttr.startsWith("attribute["))	// attribute =  같은 빈 Attr 가 있음.
				{
					sAttr			= sAttr.replace("attribute[", "").replace("]", "");
					StringList sInfo	= FrameworkUtil.split(sAttr, "=");
					sb.append(_TAB).append(_TAB).append("'").append(sInfo.get(0).toString().trim()).append("' '").append(sInfo.get(1).toString().trim()).append("'").append(RECORDSEP);
				}
			}
			sb.append(SchemaConstants.SEMI_COLON).append(RECORDSEP).append(RECORDSEP);
			
			
			sNumberGenerator			= MqlUtil.mqlCommand(context, sNumberGenerator);
			StringList slNumberInfo		= FrameworkUtil.split(sNumberGenerator, SchemaConstants.SELECT_SEPERATOR);
			String sNumberBase			= new StringBuilder("print bus 'eService Number Generator' '").append(SchemaUtil.nullToEmpty((String) slNumberInfo.get(0))).append("' '").append(SchemaUtil.nullToEmpty((String) slNumberInfo.get(1))).append("'").toString();
			String sNumberDefault		= new StringBuilder(sNumberBase).append(sDefault).toString();
			String sNextNumber			= new StringBuilder(sNumberBase).append(" select attribute[eService Next Number] dump").toString();

			sb.append("add bus 'eService Number Generator' '").append(SchemaUtil.nullToEmpty((String) slNumberInfo.get(0))).append("' '").append(SchemaUtil.nullToEmpty((String) slNumberInfo.get(1))).append("'").append(RECORDSEP);
			
			sNumberDefault				= MqlUtil.mqlCommand(context, sNumberDefault);
			StringList slDefaultInfo	= FrameworkUtil.split(sNumberDefault, SchemaConstants.SELECT_SEPERATOR);
			sb.append(_TAB).append("policy '").append(SchemaUtil.nullToEmpty((String) slDefaultInfo.get(0))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("vault  '").append(SchemaUtil.nullToEmpty((String) slDefaultInfo.get(1))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("owner  '").append(SchemaUtil.nullToEmpty((String) slDefaultInfo.get(2))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("state  '").append(SchemaUtil.nullToEmpty((String) slDefaultInfo.get(3))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("schedule ''").append(RECORDSEP);
			
			sNextNumber					= MqlUtil.mqlCommand(context, sNextNumber);
			sb.append(_TAB).append(_TAB).append("'eService Next Number' '").append(sNextNumber).append("'").append(RECORDSEP);
			sb.append(SchemaConstants.SEMI_COLON).append(RECORDSEP).append(RECORDSEP);
			
			sb.append("add connection 'eService Number Generator'").append(RECORDSEP);
			sb.append(_TAB).append("from 'eService Object Generator' '").append(sGeneratorName).append("' '").append(sGeneratorRevision).append("'").append(RECORDSEP);
			sb.append(_TAB).append("to 'eService Number Generator' '").append(SchemaUtil.nullToEmpty((String) slNumberInfo.get(0))).append("' '").append(SchemaUtil.nullToEmpty((String) slNumberInfo.get(1))).append("'").append(RECORDSEP);
			sb.append(SchemaConstants.SEMI_COLON);
			
			sbDel.append(SchemaUtil.createObjectMQLHeader(sGeneratorName));
			if(!SchemaConstants.MOD_FLAG.equals(sGeneratorFlag))
				sbDel.append("#");
			sbDel.append("del bus 'eService Object Generator' '").append(sGeneratorName).append("' '").append(sGeneratorRevision).append("';").append(RECORDSEP);
			if(!SchemaConstants.MOD_FLAG.equals(sGeneratorFlag))
				sbDel.append("#");
			sbDel.append("del bus 'eService Number Generator' '").append(SchemaUtil.nullToEmpty((String) slNumberInfo.get(0))).append("' '").append(SchemaUtil.nullToEmpty((String) slNumberInfo.get(1))).append("';").append(RECORDSEP).append(RECORDSEP);
			sbDel.append(sb);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbDel.toString();
	}
	
	
	/**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
	public static String getGeneratorModMQL(Context ctx1, Context ctx2, String[] args) throws Exception {
		StringBuilder sb 			= new StringBuilder();
		try {
			sb.append(getGeneratorMQL(ctx1, args));
		} catch (Exception e) {

			e.printStackTrace();
		}
		return sb.toString();
	}
}
