package kr.co.atis.util.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.cache.CacheManager;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIForm;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class SchemaForm {
	private static final String _TAB 			= SchemaConstants._TAB;
	private static final String RECORDSEP 		= SchemaConstants.RECORDSEP;
	
	public static String getFormMQL(Context context, String sName) throws Exception{
    	return getFormSchema(context, sName, SchemaConstants.ADD_FLAG);
    }
	
	
	public static String getFormSchema(Context context, String sName, String sFlag) throws Exception{
        try {
            StringBuilder sb 	= new StringBuilder();
            
            CacheManager.getInstance().clearTenant(context, CacheManager._entityNames.FORMS);
            HashMap mapFormInfo = (HashMap) CacheManager.getInstance().getValue(context, CacheManager._entityNames.FORMS, new String[]{ sName });
            sb.append(SchemaUtil.createBusinessOnlyHeader("WebForm", sName));
            
            if(sFlag.equals(SchemaConstants.ADD_FLAG)) {
            	sb.append("#");
            }
            sb.append("del form \"").append(sName).append("\"").append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
            sb.append("add form \"").append(sName).append("\" ").append("web").append(RECORDSEP);

            MapList columnList 		= (MapList)mapFormInfo.get("fields");
            Iterator columnItr 		= columnList.iterator();
            while (columnItr.hasNext()) {
                HashMap fieldMap 	= (HashMap) columnItr.next();
                String sFieldName 	= (String) fieldMap.get("name");
                String sFieldLabel 	= (String) fieldMap.get("label");

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

                boolean hasHref = fieldMap.containsKey(SchemaConstants.HREF);
                if(hasHref) {
                    String href = StringUtils.trimToEmpty((String) fieldMap.get(SchemaConstants.HREF));
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
	
	
	public static String getFormSchemaMod(Context ctx1, Context ctx2, String sName) throws Exception{
        try {
            CacheManager.getInstance().clearTenant(ctx1, CacheManager._entityNames.FORMS);
            HashMap mFormOri = (HashMap) CacheManager.getInstance().getValue(ctx1, CacheManager._entityNames.FORMS, new String[]{ sName });
            CacheManager.getInstance().clearTenant(ctx2, CacheManager._entityNames.FORMS);
            HashMap mFormMod = (HashMap) CacheManager.getInstance().getValue(ctx2, CacheManager._entityNames.FORMS, new String[]{ sName });
            
            StringBuilder sb 	= new StringBuilder();
            sb.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
            sb.append(SchemaUtil.createBusinessOnlyHeader("WebForm", sName));
            sb.append("mod form \"").append(sName).append("\" ").append("web").append(RECORDSEP);

            MapList columnListOri	= (MapList) mFormOri.get("fields");
            MapList columnListMod	= (MapList) mFormMod.get("fields");
            
            HashMap mFieldsMapMod	= new HashMap();
            Iterator columnItrMod	= columnListOri.iterator();
            while (columnItrMod.hasNext()) {
                HashMap fieldMap 	= (HashMap) columnItrMod.next();
                String sFieldName 	= (String) fieldMap.get("name");
                mFieldsMapMod.put(sFieldName, fieldMap);
            }
            
            StringList slNameList	= new StringList();
            Iterator columnItrOri	= columnListOri.iterator();
            while(columnItrOri.hasNext()) {
            	HashMap fieldMapOri	= (HashMap) columnItrOri.next();
            	HashMap fieldMapMod	= new HashMap();
            	String sFieldName	= (String) fieldMapOri.get("name");
            	String sFieldLabel	= (String) fieldMapOri.get("label");
            	slNameList.add(sFieldName);

            	if(mFieldsMapMod.containsKey(sFieldName)) {
            		fieldMapMod.putAll((Map) ((HashMap) mFieldsMapMod.get(sFieldName)).clone());
            		if(mFieldsMapMod.equals(fieldMapOri)) {
            			continue;
            		} else {
            			SchemaUtil.settingDataLine(sb, SchemaConstants.FIELD + " mod");
            			SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.NAME, sFieldName, 12);
            		}
            	} else {
            		SchemaUtil.settingDataLine(sb, SchemaConstants.FIELD + " mod");
        			SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.NAME, sFieldName, 12);
            	}
            	
                String sFieldLabelMod 	= (String) fieldMapMod.get("label");
                if(sFieldLabel.equals(sFieldLabelMod))
                	SchemaUtil.settingDataLine(sb.append(_TAB),  SchemaConstants.LABEL, sFieldLabel, 12);
                
                String exprOri		= "";
                String exprTypeOri	= "";
                String exprMod		= "";
                String exprTypeMod	= "";
                if(fieldMapOri.containsKey("expression_businessobject")) {
                	exprOri			= (String) fieldMapOri.get("expression_businessobject");
                	exprTypeOri		= "businessobject";
                	exprMod			= StringUtils.trimToEmpty((String) fieldMapMod.get("expression_businessobject"));
                	exprTypeMod		= "businessobject";
                } else if(fieldMapOri.containsKey("expression_relationship")) {
                	exprOri			= (String) fieldMapOri.get("expression_relationship");
                	exprTypeOri		= "relationship";
                	exprMod			= StringUtils.trimToEmpty((String) fieldMapMod.get("expression_relationship"));
                	exprTypeMod		= "relationship";
                }
                if(!exprOri.equals(exprMod) || !exprTypeOri.equals(exprTypeMod)) {
                	SchemaUtil.settingDataLine(sb.append(_TAB), exprOri, exprTypeOri, 12);
                }
                
                String altOri		= StringUtils.trimToEmpty((String) fieldMapOri.get("alt"));
                String altMod		= StringUtils.trimToEmpty((String) fieldMapMod.get("alt"));
                if(!altOri.equals(altMod)) {
                	SchemaUtil.settingDataLine(sb.append(_TAB), "alt", altOri, 12);
                }
                
                String rangeOri		= StringUtils.trimToEmpty((String) fieldMapOri.get("range"));
                String rangeMod		= StringUtils.trimToEmpty((String) fieldMapMod.get("range"));
                if(!rangeOri.equals(rangeMod)) {
                	SchemaUtil.settingDataLine(sb.append(_TAB), "range", rangeOri, 12);
                }
                
                String updateOri	= StringUtils.trimToEmpty((String) fieldMapOri.get("update"));
                String updateMod	= StringUtils.trimToEmpty((String) fieldMapMod.get("update"));
                if(!updateOri.equals(updateMod)) {
                	SchemaUtil.settingDataLine(sb.append(_TAB), "update", updateOri, 12);
                }
                
                boolean hasHrefOri 	= fieldMapOri.containsKey(SchemaConstants.HREF);
                boolean hasHrefMod	= fieldMapMod.containsKey(SchemaConstants.HREF);
                String hrefOri		= StringUtils.trimToEmpty((String) fieldMapOri.get(SchemaConstants.HREF));
                String hrefMod		= StringUtils.trimToEmpty((String) fieldMapMod.get(SchemaConstants.HREF));
                
                if(hasHrefOri && hasHrefMod) {
                	if(!hrefOri.equals(hrefMod)) {
                		SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.HREF, hrefOri, 12);	
                	}
                } else if((hasHrefOri && hasHrefMod) || (!hasHrefOri && hasHrefMod)) {
                	SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.HREF, hrefOri, 12);
                }

                HashMap settingsOri	= (HashMap) fieldMapOri.get(SchemaConstants.SETTINGS);
                HashMap settingsMod	= fieldMapMod.containsKey(SchemaConstants.SETTINGS) ? ((HashMap) fieldMapOri.get(SchemaConstants.SETTINGS)) : new HashMap();
                if(!settingsOri.equals(settingsMod)){
                	Iterator itr	= settingsOri.keySet().iterator();
                	while(itr.hasNext()) {
                		String sKey	= (String) itr.next();
                		String sVal	= (String) settingsOri.get(sKey);
                		sVal		= sVal.replace("\"", "'");
                		
                		if(settingsMod.containsKey(sKey)) {
                			String sValMod	= (String) settingsMod.get(sKey);
                			settingsMod.remove(sKey);
                			if(sVal.equals(sValMod)) {
                				continue;
                			}
                		}
                		sb.append(_TAB).append(_TAB).append("add	setting").append(_TAB);
                		sb.append(SchemaUtil.appendEmptySpace(sKey, 25)).append(" \"").append(sVal).append("\"").append(RECORDSEP);
                	}
                	
                	Iterator itrMod	= settingsMod.keySet().iterator();
                	while(itrMod.hasNext()) {
                		String sKey	= (String) itrMod.next();
                		
                		sb.append(_TAB).append(_TAB).append("remove setting").append(_TAB);
                		sb.append(" \"").append(SchemaUtil.appendEmptySpace(sKey, 25)).append("\"").append(RECORDSEP);
                	}
                }
            }
 
            Iterator modFieldItr	= mFieldsMapMod.keySet().iterator();
            while(modFieldItr.hasNext()) {
            	String sFieldName	= (String) modFieldItr.next();
            	if(!slNameList.contains(sFieldName)) {
            		SchemaUtil.settingDataLine(sb, SchemaConstants.FIELD + " del");
            		SchemaUtil.settingDataLine(sb.append(_TAB), SchemaConstants.NAME, sFieldName, 12);
            	}
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
