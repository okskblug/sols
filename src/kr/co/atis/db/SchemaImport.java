package kr.co.atis.db;

import java.util.List;

import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class SchemaImport {

	/**
	 * Import MQL from Tab
	 * @param context
	 * @param sMQLQuery
	 * @return
	 * @throws Exception
	 */
	public static String importMQL(Context context, String sMQLQuery) throws Exception
	{
		String sReturn	= "";
		try {
			StringList slQuerys	= FrameworkUtil.split(sMQLQuery, ";");
			int iQuerySize		= slQuerys.size();
			
			for(int i = 0; i < iQuerySize; i++) {
				MqlUtil.mqlCommand(context, (String) slQuerys.get(i));
			}
			
		} catch (Exception e) {
			
			throw e;
		}
		
		return sReturn;
	}

	/**
	 * Compare Mode Only
	 * Import MQL from JList (Selected) (ctx1 -> ctx2) Add or Mod
	 * @param ctx1 - Context Export
	 * @param ctx2 - Context Import
	 * @param slSelectedList - Selected item list
	 * @param sType - MQL Type (Attribute, Type, Policy ...)
	 * @return
	 * @throws Exception
	 */
	public static String importListMQL(Context ctx1, Context ctx2, List slSelectedList, int[] iSelectedIndex) throws Exception
	{
		try {
			String sSearchType	= BusinessViewMain.sSearch;
			String sSearchFlag	= (String) BusinessViewMain.iconsList.get(SchemaConstants.VIEW_TYPE);
			StringList slFlag	= FrameworkUtil.split(sSearchFlag, ",");
			int iAddIndex		= Integer.parseInt((String) slFlag.get(0));
			int iModIndex		= Integer.parseInt((String) slFlag.get(1));
			
			int iListSize		= slSelectedList.size();
    		for(int i = 0; i < iListSize; i++)
    		{
    			/*************** Get MQL ***************/
    			String sInfo		= (String) slSelectedList.get(i);
    			int iIndex			= i;
    			if(null != iSelectedIndex)	// Selected Insert
    				iIndex			= iSelectedIndex[i];
    			
    			
    			StringList slInfo	= new StringList();
    			slInfo				= FrameworkUtil.split(sInfo, "|");	// Business, Trigger, Generator
    			slInfo.add(0, sSearchType);
    			if(sSearchType.equals(SchemaConstants.TRIGGER) || sSearchType.equals(SchemaConstants.GENERATOR) || !BusinessViewMain.slMQLTypeList.contains(sSearchType)) {
					slInfo.add(SchemaConstants.MOD_FLAG);
    			}
    			String[] args		= (String[]) slInfo.toArray(new String[slInfo.size()]);
    			String sResult		= "";
    			
    			if(iIndex < iAddIndex) {
    				sResult			= SchemaUtil.getSchema(ctx1, args);
    			} else if(iAddIndex <= iIndex && iIndex < (iAddIndex + iModIndex)) {
    				sResult			= SchemaUtil.getSchemaModify(ctx1, ctx2, args);
    			} else {
    			}
    			
    			importMQL(ctx2, sResult);
    			/*************** Get MQL ***************/
    		}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return "";
	}
}
