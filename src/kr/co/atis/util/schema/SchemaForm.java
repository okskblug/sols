package kr.co.atis.util.schema;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIForm;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;

public class SchemaForm {
	private static final String _TAB 			= SchemaConstants._TAB;
	private static final String RECORDSEP 		= SchemaConstants.RECORDSEP;
	
	public static String getFormMQL(Context context, String sName) throws Exception{
    	return getFormSchema(context, sName, SchemaConstants.ADD_FLAG);
    }
	
	
	public static String getFormSchema(Context context, String sName, String sFlag) throws Exception{
        try {
            StringBuilder sb 	= new StringBuilder();
            UIForm uiFormBean	= new UIForm();
            
            HashMap mapFormInfo = uiFormBean.getForm(context, sName);
            sb.append(SchemaUtil.createBusinessOnlyHeader("WebForm", sName));
            
            if(sFlag.equals(SchemaConstants.ADD_FLAG))
            	sb.append("#");
            sb.append("del form \"").append(sName).append("\"").append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
            sb.append("add form \"").append(sName).append("\" ").append("web").append(RECORDSEP);

            MapList columnList 		= (MapList)mapFormInfo.get("fields");
            Iterator columnItr 		= columnList.iterator();
            while (columnItr.hasNext()) {
                HashMap fieldMap 	= (HashMap) columnItr.next();
                String sFieldName 	= uiFormBean.getName(fieldMap);
                String sFieldLabel 	= uiFormBean.getLabel(fieldMap);

                boolean hasHref = fieldMap.containsKey(SchemaConstants.HREF);
                SchemaUtil.settingDataLine(sb, SchemaConstants.FIELD);
                SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.NAME,  sFieldName,  12);
                SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.LABEL, sFieldLabel, 12);

                String expr 	= SchemaUtil.getData(context, SchemaConstants.FORM, sName, new StringBuffer("field[").append(sFieldName).append("].expression").toString());
                String exprType = SchemaUtil.getData(context, SchemaConstants.FORM, sName, new StringBuffer("field[").append(sFieldName).append("].expressiontype").toString());
                if( expr != null && !"".equals(expr) && !"null".equals(expr) ){
                	SchemaUtil.settingDataLine(sb.append(_TAB), exprType, expr, 12);
                }
                
                String alt 		= SchemaUtil.getData(context, SchemaConstants.FORM, sName, new StringBuffer("field[").append(sFieldName).append("].alt").toString());
                String range	= SchemaUtil.getData(context, SchemaConstants.FORM, sName, new StringBuffer("field[").append(sFieldName).append("].range").toString());
                String update	= SchemaUtil.getData(context, SchemaConstants.FORM, sName, new StringBuffer("field[").append(sFieldName).append("].update").toString());
                SchemaUtil.settingDataLine(sb.append(_TAB), "alt", alt, 12);
                SchemaUtil.settingDataLine(sb.append(_TAB), "range", range, 12);
                SchemaUtil.settingDataLine(sb.append(_TAB), "update", update, 12);

                if(hasHref) {
                    String href = uiFormBean.getHRef(fieldMap);
                    SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.HREF, href, 12);
                }

                HashMap settings = (HashMap)fieldMap.get(SchemaConstants.SETTINGS);
                if(settings == null){
                    System.out.println( "Field SETTING NULL ("+sName+")");
                }

                if( null != settings ){
                	sb.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.SETTING, settings, true));
                }
            }
 

            if(sFlag.equals(SchemaConstants.ADD_FLAG)) {
	            HashMap propertiesMap = (HashMap) mapFormInfo.get(SchemaConstants.PROPERTIES);
	            if( null != propertiesMap ) {
	            	sb.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.PROPERTY, propertiesMap));
	            }
            }
            sb.append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
            sb.append("add property form_").append(StringUtils.deleteWhitespace(sName)).append(" on program eServiceSchemaVariableMapping.tcl to form '").append(sName).append("';").append(SchemaConstants.RECORDSEP);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
