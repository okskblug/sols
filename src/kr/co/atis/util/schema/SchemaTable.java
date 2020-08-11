package kr.co.atis.util.schema;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UITable;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class SchemaTable {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	
	public static String getTableMQL(Context context, String sName) throws Exception
    {
    	String sReturn	= ""; 
    	try {
    		sReturn		= getTableSchema(context, sName, SchemaConstants.ADD_FLAG);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    	return sReturn;
    }
    
    public static String getTableModMQL(Context ctx1, Context ctx2, String sName) throws Exception
    {
    	String sReturn	= ""; 
    	try {
    		sReturn		= getTableSchema(ctx1, sName, SchemaConstants.MOD_FLAG);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    	return sReturn;
    }
    
    public static String getTableSchema(Context context, String sName, String sFlag) throws Exception
    {
        try {
            StringBuilder sb 		= new StringBuilder();
//            CacheManager.getInstance().clearTenant(context);
            UITable uiTableBean 	= new UITable();
            HashMap mapTableInfo 	= uiTableBean.getTable(context, sName);
            
            sb.append(SchemaUtil.createBusinessOnlyHeader(SchemaConstants.TABLE, sName));
            if(sFlag.equals(SchemaConstants.ADD_FLAG))
            	sb.append("#");
            sb.append("del table ").append(SchemaConstants.DOUBLE_QUOTE).append(sName).append(SchemaConstants.DOUBLE_QUOTE).append(" system").append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
            sb.append("add table ").append(SchemaConstants.DOUBLE_QUOTE).append(sName).append(SchemaConstants.DOUBLE_QUOTE).append(" system").append(RECORDSEP);

            MapList columnList 		= (MapList)mapTableInfo.get("columns");
            Iterator columnItr 		= columnList.iterator();
            while (columnItr.hasNext()) {
                HashMap columnMap 	= (HashMap) columnItr.next();
                StringList roles 	= (StringList) columnMap.get("roles");
                String name 		= (String) columnMap.get("name");
                String sColumnName 	= uiTableBean.getName(columnMap);
                String sColumnLabel = uiTableBean.getLabel(columnMap);

                boolean hasHref 	= columnMap.containsKey(SchemaConstants.HREF);
                boolean hasSortType = columnMap.containsKey(SchemaConstants.SORTTYPE);

                SchemaUtil.settingDataLine(sb, SchemaConstants.COLUMN);
                SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.NAME, 	sColumnName, 	12);
                SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.LABEL, 	sColumnLabel.replaceAll("\"", "'"), 	12);

                
                String expr 	= SchemaUtil.getData(context, "table", sName, new StringBuffer("column[").append(sColumnName).append("].expression").toString());
                String exprType = SchemaUtil.getData(context, "table", sName, new StringBuffer("column[").append(sColumnName).append("].expressiontype").toString());
                
                if( expr != null && !"".equals(expr) && !"null".equals(expr) ){
                	SchemaUtil.settingDataLine(sb.append(_TAB), exprType, expr, 12);
                }

                String alt 		= SchemaUtil.getData(context, "table", sName, new StringBuffer("column[").append(sColumnName).append("].alt").toString());
                String range 	= SchemaUtil.getData(context, "table", sName, new StringBuffer("column[").append(sColumnName).append("].range").toString());
                String update 	= SchemaUtil.getData(context, "table", sName, new StringBuffer("column[").append(sColumnName).append("].update").toString());
                SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.ALT, 	alt, 	12);
                SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.RANGE, 	range, 	12);
                SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.UPDATE, update, 12);

                if(hasHref)
                {
                    String href = uiTableBean.getControlMapStringElement(columnMap, SchemaConstants.HREF);
                    SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.HREF, href, 12);
                }

                if(hasSortType)
                {
                    String sortType = uiTableBean.getControlMapStringElement(columnMap, SchemaConstants.SORTTYPE);
                    SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.SORTTYPE, sortType, 12);
                }

                HashMap settings = (HashMap)columnMap.get(SchemaConstants.SETTINGS);
                sb.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.SETTING, settings, true));

                String sUsers 		= SchemaUtil.getData(context, "table", sName, new StringBuffer("column[").append(sColumnName).append("].user").toString());
                if(sUsers.length() > 0) {
                	StringList slUsers	= FrameworkUtil.split(sUsers, "|");
                	for(Iterator itrs	= slUsers.iterator(); itrs.hasNext();) {
                		sb.append(_TAB).append(_TAB).append("user ").append(_TAB).append("'").append((String) itrs.next()).append("'").append(RECORDSEP);
                	}
                }
/*                if( roles != null && roles.size() > 0 )
                {
                    for (Iterator roleItr = roles.iterator(); roleItr.hasNext();) {
                        String sRole = (String) roleItr.next();
                        sb.append(_TAB).append(_TAB).append("user \"").append(sRole).append("\"").append(RECORDSEP);
                    }
                }*/
            }
            
            HashMap mProperties = (HashMap) mapTableInfo.get("properties");
            if(null != mProperties) {
            	sb.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.PROPERTY, mProperties));
            }
            
            sb.append(SchemaConstants.SEMI_COLON);
            sb.append(RECORDSEP).append(RECORDSEP);
            sb.append("add property 'table_").append(StringUtils.deleteWhitespace(sName)).append("' on program 'eServiceSchemaVariableMapping.tcl' to table '").append(sName).append("' system ").append(SchemaConstants.SEMI_COLON);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}