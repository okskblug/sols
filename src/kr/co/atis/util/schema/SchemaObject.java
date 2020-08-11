package kr.co.atis.util.schema;

import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class SchemaObject {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	private static final int iNameSize			= 40;
	
	/**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
	public static String getObjectMQL(Context context, String[] args) throws Exception {
		StringBuilder sb 			= new StringBuilder();
		try {
			String sType 		= args[1];
			String sName 		= args[2];
			String sRevision 	= args[3];
//			String sFlag		= "";
			
//			System.err.println(args[0] +  " : " + args[1] +  " : " + args[2] +  " : " + args[3]);
			
//			if(args.length > 4)
//				sFlag		= args[3];	// "" or "MOD"

			String sDefault			= " select policy vault owner state dump '|'";
			String sCmdBase			= new StringBuilder("print bus '").append(sType).append("' '").append(sName).append("' '").append(sRevision).append("'").toString();
			String sDefaultInfo		= new StringBuilder(sCmdBase).append(sDefault).toString();
			String sAttrList		= new StringBuilder(sCmdBase).append(" select attribute[*]").toString();
			String sDescInfo		= new StringBuilder(sCmdBase).append(" select description dump").toString();
			
			sb.append(SchemaUtil.createObjectMQLHeader(sName));
			sb.append("add bus '").append(sType).append("' '").append(sName).append("' '").append(sRevision).append("'").append(RECORDSEP);
			
			sDefaultInfo			= MqlUtil.mqlCommand(context, sDefaultInfo);
			StringList slInfo		= FrameworkUtil.split(sDefaultInfo, SchemaConstants.SELECT_SEPERATOR);
			String sDesc			= MqlUtil.mqlCommand(context, sDescInfo);
			sb.append(_TAB).append("policy '").append(SchemaUtil.nullToEmpty((String) slInfo.get(0))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("vault  '").append(SchemaUtil.nullToEmpty((String) slInfo.get(1))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("description  '").append(sDesc).append("'").append(RECORDSEP);
			sb.append(_TAB).append("owner  '").append(SchemaUtil.nullToEmpty((String) slInfo.get(2))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("state  '").append(SchemaUtil.nullToEmpty((String) slInfo.get(3))).append("'").append(RECORDSEP);
			sb.append(_TAB).append("schedule ''").append(RECORDSEP);
			
			sAttrList				= MqlUtil.mqlCommand(context, sAttrList);
			StringList slAttribute	= FrameworkUtil.split(sAttrList, "\n");
			slAttribute.sort();
			int iAttrSize			= slAttribute.size();
			for(int i = 0; i < iAttrSize; i++)
			{
				String sAttr		= SchemaUtil.nullToEmpty((String) slAttribute.get(i)).trim();
				if(sAttr.startsWith("attribute["))	// attribute =  같은 빈 Attr 가 있음.
				{
					sAttr				= sAttr.replace("attribute[", "").replace("]", "");
					StringList sInfo	= FrameworkUtil.split(sAttr, "=");
					String sAttrName = sInfo.get(0).toString().trim();
					int iAttrNameSize = sAttrName.length();
					
					sb.append(_TAB).append("'").append(sAttrName).append("'");
					if(iNameSize > iAttrNameSize) {
						for(int k = 0; k < (iNameSize - iAttrNameSize); k++) {
							sb.append(" ");
						}
					} else {
						sb.append(" ");
					}
					sb.append("'").append(sInfo.get(1).toString().trim()).append("'").append(RECORDSEP);
					//sb.append(_TAB).append("'").append(sInfo.get(0).toString().trim()).append("' '").append(sInfo.get(1).toString().trim()).append("'").append(RECORDSEP);
				}
			}
			sb.append(SchemaConstants.SEMI_COLON).append(RECORDSEP).append(RECORDSEP);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
	/**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
	public static String getObjectModMQL(Context ctx1, Context ctx2, String[] args) throws Exception {
		StringBuilder sb 			= new StringBuilder();
		try {
			sb.append(getObjectMQL(ctx1, args));
		} catch (Exception e) {

			e.printStackTrace();
		}
		return sb.toString();
	}
}
