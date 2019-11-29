package kr.co.atis.util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;

import kr.co.atis.main.BusinessViewMain;
import matrix.db.Context;
import matrix.util.StringList;

/**
 * Created by MSKIM
 * Modified by ihjang
 */
public class SchemaCompareMain {

	public static HSSFWorkbook workbook 	= new HSSFWorkbook();
	
	// ctx2 기준으로 ctx1 로 Add or Mod 진행
/*	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Context ctx1 = new Context("");	// MATRIX-R
			ctx1.setUser("admin_platform");
			ctx1.setPassword("Qwer1234");
			ctx1.connect();
			
			Context ctx2 = new Context("https://plmdev.hites.co.kr/internal");
			ctx2.setUser("admin_platform");
			ctx2.setPassword("Qwer1234");
			ctx2.connect();
            
			compare(ctx1, ctx2, SchemaConstants.ATTRIBUTE, 		new String[]{"range"});
			compare(ctx1, ctx2, SchemaConstants.TYPE, 			new String[]{"attribute", "trigger"});
			compare(ctx1, ctx2, SchemaConstants.POLICY,			new String[]{""});
			compare(ctx1, ctx2, SchemaConstants.RELATIONSHIP,	new String[]{"attribute", "trigger", "fromtype", "totype"});
			compare(ctx1, ctx2, SchemaConstants.COMMAND, 		new String[]{"href", "setting.value"});
			compare(ctx1, ctx2, SchemaConstants.MENU, 			new String[]{"href", "command", "setting.value"});
			compare(ctx1, ctx2, SchemaConstants.FORM, 			new String[]{"field.setting.value"});
			compare(ctx1, ctx2, SchemaConstants.TABLE, 			new String[]{"column.setting.value"});
			compare(ctx1, ctx2, SchemaConstants.CHANNEL, 		new String[]{"command", "setting.value"});
			compare(ctx1, ctx2, SchemaConstants.PORTAL, 		new String[]{"channel", "setting.value"});
			SchemaExport.exportList(workbook);
			System.err.println("Complete!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public static void compare(Context ctx1, Context ctx2, String sType, String[] arg) {
		try {
			Properties properties 	= SchemaProperties.getSchemaProperties();	// Get Properties
			StringBuilder sb 		= new StringBuilder();
			
			sb.append("list '").append(sType).append("'");
			if(sType.equalsIgnoreCase("table"))
				sb.append(" system");
			sb.append(" '").append(properties.getProperty("Search.Prefix")).append("*' select name");
			for (int i = 0; i < arg.length; i++) {
				sb.append(" ").append(arg[i]);
			}
			sb.append(" dump |");

			Map mCompareMap	= compare(ctx1, ctx2, sb.toString());
			System.err.println(sType + " Not Exists : " + mCompareMap.get(SchemaConstants.ADD_FLAG));
			System.err.println(sType + " Not Equals : " + mCompareMap.get(SchemaConstants.MOD_FLAG));

			StringList slNotExistsList	= (StringList) mCompareMap.get(SchemaConstants.ADD_FLAG);
			StringList slNotEqualsList	= (StringList) mCompareMap.get(SchemaConstants.MOD_FLAG);
			
			if(null != slNotExistsList && slNotExistsList.size() > 0) {
				String sResult	= SchemaExport.exportExcel(ctx2, null, workbook, sType, slNotExistsList, SchemaConstants.ADD_FLAG);
				SchemaExport.exportExcelSettingData(sType, SchemaConstants.ADD_FLAG, workbook, sResult);
			}
			if(null != slNotEqualsList && slNotEqualsList.size() > 0) {
				String sResult	= SchemaExport.exportExcel(ctx1, ctx2, workbook, sType, slNotEqualsList, SchemaConstants.MOD_FLAG);
				SchemaExport.exportExcelSettingData(sType, SchemaConstants.MOD_FLAG, workbook, sResult);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Map compare(Context ctx1, Context ctx2, String sQuery) {
		Map mReturnMap	= new HashMap();
		try {
			Map m1 = compareMap(ctx1, sQuery, false, new StringList());
			Map m2 = compareMap(ctx2, sQuery, false, new StringList());

			StringList slNotExistsList	= new StringList();
			StringList slNotEqualsList	= new StringList();
			StringList slDeleteList		= new StringList();
			
			Iterator it1 = m1.keySet().iterator();
			while (it1.hasNext()) {
				String sKey 	= (String) it1.next();
				StringList sVal = (StringList) m1.get(sKey);
				
				if (!m2.containsKey(sKey)) {	// Add List
					slNotExistsList.add(sKey);
				} else {
					if (!sVal.equals((StringList)m2.get(sKey))) {	// Mod List
						slNotEqualsList.add(sKey);
					}	
					m2.remove(sKey);
				}
			}
			
			// Del List
			Iterator it2 = m2.keySet().iterator();
			while(it2.hasNext())
			{
				String sKey 	= (String) it2.next();
				if (!m1.containsKey(sKey)) {
					slDeleteList.add(sKey);
				}
			}
			
			mReturnMap.put(SchemaConstants.ADD_FLAG, 	slNotExistsList);
			mReturnMap.put(SchemaConstants.MOD_FLAG, 	slNotEqualsList);
			mReturnMap.put(SchemaConstants.DEL_FLAG, 	slDeleteList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mReturnMap;
	}
	
	public static Map compareMap(Context ctx, String sQuery, boolean isObject, StringList slAttrList) {
		Map map = new HashMap();
		try {
			String sResult				= MqlUtil.mqlCommand(ctx, sQuery);
			StringList slResult 		= FrameworkUtil.splitString(sResult, "\n");
			int iListSize				= slResult.size();
			int iAttrSize				= slAttrList.size();
			
			for (int i = 0; i < iListSize; i++) {
				String sSelData			= (String) slResult.get(i);
				StringList slSelList 	= FrameworkUtil.splitString(sSelData, "|");
				String sKey				= "";
				
				if(isObject) {
					sKey				= new StringBuilder((String) slSelList.get(0)).append("|").append((String) slSelList.get(1)).append("|").append((String) slSelList.get(2)).toString();
					slSelList.remove(0);	// Type
					slSelList.remove(0);	// Name
					slSelList.remove(0);	// Revision
					
					Map mAttrMap			= new HashMap();
					for(int j = 0; j < iAttrSize; j++) {
						mAttrMap.put(slAttrList.get(j), slSelList.get(j));
					}
					map.put(sKey, mAttrMap);
				} else {
					sKey				= (String) slSelList.get(0);
					slSelList.remove(0);
					slSelList.sort();
					map.put(sKey, slSelList);
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	
	// Compare Object (Trigger, Generator)
	public static Map compareObject(Context ctx1, Context ctx2, String sQuery, StringList slAttrList) {
		Map mReturnMap	= new HashMap();
		try {
			Map m1 = compareMap(ctx1, sQuery, true, slAttrList);
			Map m2 = compareMap(ctx2, sQuery, true, slAttrList);

			StringList slNotExistsList	= new StringList();
			StringList slNotEqualsList	= new StringList();
			StringList slDeleteList		= new StringList();
			
			Iterator it1 = m1.keySet().iterator();
			while (it1.hasNext()) {
				String sKey 	= (String) it1.next();
				HashMap mVal 	= (HashMap) m1.get(sKey);
				if (!m2.containsKey(sKey)) {
					slNotExistsList.add(sKey);
				} else {
					if (!mVal.equals((Map) m2.get(sKey))) {
						slNotEqualsList.add(sKey);
					}
					m2.remove(sKey);
				}
			}
			
			Iterator it2 = m2.keySet().iterator();
			while(it2.hasNext())
			{
				String sKey 	= (String) it2.next();
				if (!m1.containsKey(sKey)) {
					slDeleteList.add(sKey);
				}
			}
			
			mReturnMap.put(SchemaConstants.ADD_FLAG, slNotExistsList);
			mReturnMap.put(SchemaConstants.MOD_FLAG, slNotEqualsList);
			mReturnMap.put(SchemaConstants.DEL_FLAG, slDeleteList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mReturnMap;
	}
	
	public static StringList compareListCommon(Context ctx1, Context ctx2, String sSearch, String sTxt, boolean isFirst, boolean isCompare) {
		return compareListCommon(ctx1, ctx2, sSearch, sTxt, isFirst, isCompare, false);
	}
	
	public static StringList compareListCommon(Context ctx1, Context ctx2, String sSearch, String sTxt, boolean isFirst, boolean isCompare, boolean isAuto) { 
		String sResult			= "";
		StringList slData		= new StringList();
		StringList slPreList	= new StringList();
		int iPreListSize		= 0;
		
		try {
			StringBuilder sb1 	= new StringBuilder();
			StringBuilder sb2 	= new StringBuilder();
			sb1.append("list '").append(sSearch).append("' ");
			sb2.append("list '").append(sSearch).append("' ");
			if(sSearch.equalsIgnoreCase("table")) {
				sb1.append(" system");
				sb2.append(" system");
			}
	
			if(!isFirst && isCompare) sb1.append(" modified after ").append(BusinessViewMain.sModified);
			
			if(!isFirst) {
				slPreList.addAll((StringList) BusinessViewMain.mDefaultListMap.get(sSearch + "_" + SchemaConstants.ADD_FLAG));
				slPreList.addAll((StringList) BusinessViewMain.mDefaultListMap.get(sSearch + "_" + SchemaConstants.MOD_FLAG));
				slPreList.addAll((StringList) BusinessViewMain.mDefaultListMap.get(sSearch + "_" + SchemaConstants.DEL_FLAG));
			}
			 
			sb1.append(" '").append(sTxt).append("*'");
			sb2.append(" '");

			iPreListSize	= slPreList.size();
			for(int i = 0; i < iPreListSize; i++) {
				if(i > 0)
					sb2.append(",");
				sb2.append(slPreList.get(i));
			}
			
			if(!isCompare)	// Default Mode
			{
				if(sSearch.equals(SchemaConstants.TRIGGER) || sSearch.equals(SchemaConstants.GENERATOR) || !BusinessViewMain.slMQLTypeList.contains(sSearch)) {
					sb1.delete(0, sb1.length());
					sb1.append("temp query bus '").append(sSearch).append("' '").append(sTxt).append("*' * select dump '|'");
					sResult			= MqlUtil.mqlCommand(ctx1, sb1.toString());
					sResult			= sResult.replaceAll(sSearch+"\\|", "");
				} else {
					sResult			= MqlUtil.mqlCommand(ctx1, sb1.toString());
				}
				
				slData			= FrameworkUtil.split(sResult, "\n");
				slData.sort();
				BusinessViewMain.iconsList.put(SchemaConstants.VIEW_TYPE, sSearch);	// Icon Control
			}
			else // Compare Mode
			{
				Map mCompareMap1		= new HashMap();
				Map mCompareMap2		= new HashMap();
				StringList args			= SchemaConstants.getCompareOption(sSearch);
				String[] arg			= (String[]) args.toArray(new String[args.size()]);
				StringList slAttrList	= new StringList();
	
				if(sSearch.equals(SchemaConstants.TRIGGER) || sSearch.equals(SchemaConstants.GENERATOR) || !BusinessViewMain.slMQLTypeList.contains(sSearch)) {
					BusinessViewMain.setObjectAttributeCheck(ctx1, sSearch);
					slAttrList.addAll((StringList) BusinessViewMain.mAttrSettingMap.get(sSearch));

					sb1.delete(0, sb1.length());
					sb2.delete(0, sb2.length());
					sb1.append("temp query bus '").append(sSearch).append("' '").append(sTxt).append("*' '*' ");
					sb2.append("temp query bus '").append(sSearch).append("' '*' '*' where \"");
					
					if(!isFirst) sb1.append(" where \"modified > '").append(BusinessViewMain.sModified).append("'\"");
					
					for(int i = 0; i < iPreListSize; i++) {
						StringList sTempList	= FrameworkUtil.split((String) slPreList.get(i), "|");
						if(i > 0)
							sb2.append(" || ");
						sb2.append("(name == '").append(sTempList.get(0)).append("' && revision == '").append(sTempList.get(1)).append("')");
					}
					
					sb1.append(" select ");
					sb2.append("\" select ");
					
		            for (Iterator it = slAttrList.iterator(); it.hasNext();) {
		            	String sAttr	= (String) it.next();
		            	sb1.append(" attribute[").append(sAttr).append("]");
		            	sb2.append(" attribute[").append(sAttr).append("]");
		            }
					
				} else {
	    			sb1.append(" select name");
	    			sb2.append("' select name");
	    			for (int i = 0; i < arg.length; i++) {
	    				sb1.append(" ").append(arg[i]);
	    				sb2.append(" ").append(arg[i]);
	    			}
				}
				sb1.append(" dump |");
				sb2.append(" dump |");
				
				if(sSearch.equals(SchemaConstants.TRIGGER) || sSearch.equals(SchemaConstants.GENERATOR) || !BusinessViewMain.slMQLTypeList.contains(sSearch)) {
					mCompareMap1.putAll(SchemaCompareMain.compareObject(ctx1, ctx2, sb1.toString(), slAttrList));
					if(!isFirst && iPreListSize > 0) mCompareMap2.putAll(SchemaCompareMain.compareObject(ctx1, ctx2, sb2.toString(), slAttrList));
				} else {
					mCompareMap1.putAll(SchemaCompareMain.compare(ctx1, ctx2, sb1.toString()));
					if(!isFirst && iPreListSize > 0) mCompareMap2.putAll(SchemaCompareMain.compare(ctx1, ctx2, sb2.toString()));
				}
				
				// setting Auto CompareMap
				BusinessViewMain.iconsList.put(SchemaConstants.VIEW_TYPE, "");	// Clear
				if(!isFirst && iPreListSize > 0) {
					mergedCompareMap(mCompareMap1, mCompareMap2, SchemaConstants.ADD_FLAG);	// mCompareMap2 -> mCompareMap1
					mergedCompareMap(mCompareMap1, mCompareMap2, SchemaConstants.MOD_FLAG);
					mergedCompareMap(mCompareMap1, mCompareMap2, SchemaConstants.DEL_FLAG);
				}
				
				settingAutoCompareMap(sSearch, SchemaConstants.ADD_FLAG, mCompareMap1);
				settingAutoCompareMap(sSearch, SchemaConstants.MOD_FLAG, mCompareMap1);
				settingAutoCompareMap(sSearch, SchemaConstants.DEL_FLAG, mCompareMap1);

				if(!isAuto) {
					StringList slAddList	= getSearchFilterList((StringList) BusinessViewMain.mDefaultListMap.get(new StringBuilder(sSearch).append("_").append(SchemaConstants.ADD_FLAG).toString()), sTxt);
					StringList slModList	= getSearchFilterList((StringList) BusinessViewMain.mDefaultListMap.get(new StringBuilder(sSearch).append("_").append(SchemaConstants.MOD_FLAG).toString()), sTxt);
					StringList slDelList	= getSearchFilterList((StringList) BusinessViewMain.mDefaultListMap.get(new StringBuilder(sSearch).append("_").append(SchemaConstants.DEL_FLAG).toString()), sTxt);
					slData.addAll(slAddList);
					slData.addAll(slModList);
					slData.addAll(slDelList);
					BusinessViewMain.iconsList.put(SchemaConstants.VIEW_TYPE, new StringBuilder("").append(slAddList.size()).append(",").append(slModList.size()).append(",").append(slDelList.size()).toString());	// Clear
				}
			}
		} catch (FrameworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return slData;
	}
	
	
	private static StringList getSearchFilterList(StringList slDataList, String sFilterText) {
		StringList slReturnData	= new StringList();

		int iListSize	= slDataList.size();
		for(int i = 0; i < iListSize; i++)
		{
			String sText	= (String) slDataList.get(i);
			if(sText.startsWith(sFilterText))
			{
				slReturnData.add(sText);
			}
		}
		
		return slReturnData;
	}
	
	
	/**
	 * setting Map
	 * @param sSearch	- MQL Type
	 * @param sFlag		- Flag (ADD, MOD, DEL, EQUALS)
	 * @param mCompareMap	- get Compare List Map
	 */
	private static void settingAutoCompareMap(String sSearch, String sFlag, Map mCompareMap) {
		String sKey	= new StringBuilder(sSearch).append("_").append(sFlag).toString();
		
		BusinessViewMain.mDefaultListMap.remove(sKey);
		BusinessViewMain.mDefaultListMap.put(sKey, setDataList(sSearch, (StringList) mCompareMap.get(sFlag)));
	}
	
	
	/**
	 * Merged Map (Map1, Map2)
	 * Map2 -> Map1
	 * @param map1
	 * @param map2
	 * @param sFlag
	 */
	private static void mergedCompareMap(Map map1, Map map2, String sFlag) {
		StringList slTempList1	= (StringList) map1.get(sFlag);
		StringList slTempList2	= (StringList) map2.get(sFlag);
		
		if(null == slTempList1)	slTempList1	= new StringList();			
		if(null == slTempList2)	slTempList2	= new StringList();			
		
		slTempList1.addAll(slTempList2);
		
		map1.put(sFlag, slTempList1);
	}
	
	
	private static StringList setDataList(String sSearch, StringList slInsertData)
	{
		StringList slReturnData	= new StringList();
		if(null == slInsertData)
			slInsertData	= new StringList();
		slInsertData.sort();
		int iListSize	= slInsertData.size();

		for(int i = 0; i < iListSize; i++)
		{
			if(sSearch.equals(SchemaConstants.TRIGGER) || sSearch.equals(SchemaConstants.GENERATOR) || !BusinessViewMain.slMQLTypeList.contains(sSearch)) {
				slReturnData.add(slInsertData.get(i).toString().replaceAll(sSearch+"\\|", ""));
			} else {
				slReturnData.add(slInsertData.get(i));
			}
		}
		
		String size	= (String) BusinessViewMain.iconsList.get(SchemaConstants.VIEW_TYPE) + "," + iListSize;
		BusinessViewMain.iconsList.put(SchemaConstants.VIEW_TYPE, size);	// Icon Control
		
		return slReturnData;
	}
}