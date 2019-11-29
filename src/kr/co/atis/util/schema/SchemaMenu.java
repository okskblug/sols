package kr.co.atis.util.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.cache.CacheManager;
import com.matrixone.apps.cache.CacheManager._entityNames;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIMenu;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringItr;
import matrix.util.StringList;

public class SchemaMenu {
	private static final String _TAB 			= SchemaConstants._TAB;
	private static final String RECORDSEP 		= SchemaConstants.RECORDSEP;
	private static final String DOUBLE_QUOTE 	= SchemaConstants.DOUBLE_QUOTE;
	
	public static String getMenuMQL(Context context, String sName) throws Exception{

        try {
            StringBuilder sb 	= new StringBuilder();
            CacheManager.getInstance().clearTenant(context);
            UIMenu uiMenuBean 	= new UIMenu();
            HashMap mapMenuInfo = uiMenuBean.getMenu(context, sName);
            
            sb.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.MENU, sName, true));

            String strLabel 		= (String) mapMenuInfo.get(SchemaConstants.LABEL);
            String strDescription 	= (String) mapMenuInfo.get(SchemaConstants.SELECT_DESCRIPTION);
            String strHref 			= SchemaUtil.nullToEmpty((String) mapMenuInfo.get(SchemaConstants.HREF));
            String strAlt 			= SchemaUtil.nullToEmpty((String) mapMenuInfo.get(SchemaConstants.ALT));

            HashMap settingMap 		= (HashMap) mapMenuInfo.get(SchemaConstants.SETTINGS);
            HashMap propertiesMap 	= (HashMap) mapMenuInfo.get(SchemaConstants.PROPERTIES);
            MapList childrenList 	= (MapList) mapMenuInfo.get("children");

            SchemaUtil.settingDataLine(sb, SchemaConstants.SELECT_DESCRIPTION, 	strDescription,	12);
            SchemaUtil.settingDataLine(sb, SchemaConstants.LABEL, 				strLabel, 		12);
            SchemaUtil.settingDataLine(sb, SchemaConstants.HREF, 				strHref, 		12);
            SchemaUtil.settingDataLine(sb, SchemaConstants.ALT, 				strAlt, 		12);
            if( settingMap != null ) {
            	sb.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.SETTING, settingMap));
            }
 
            if( childrenList != null )
            {
            	Iterator childrenItr = childrenList.iterator();
            	while(childrenItr.hasNext())
            	{
            		Map childMap = (Map)childrenItr.next();
            		String name = (String)childMap.get("name");
            		String type = (String)childMap.get("type");
            		SchemaUtil.settingDataLine(sb, type, name, 12);
            	}
            }

            if(propertiesMap!=null) {
            	sb.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.PROPERTY, propertiesMap));
            }
            sb.append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
            sb.append("add property menu_").append(StringUtils.deleteWhitespace(sName)).append(" on program 'eServiceSchemaVariableMapping.tcl' to menu '").append(sName).append("';").append(SchemaConstants.RECORDSEP);

            StringBuilder sb2 = new StringBuilder();
            sb2.append(RECORDSEP);
            sb2.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
            sb2.append("# Parent Information").append(RECORDSEP);
            
            String strParent 	= SchemaUtil.getData(context, "menu", sName, "parent");
            StringList list 	= FrameworkUtil.split(strParent, ",");
            StringItr listItr 	= new StringItr(list);
            while(listItr.next()){
                String parentName 	= listItr.obj();
                String type 		= "";
                try{
                	SchemaUtil.getData(context, "menu", parentName, "name");	// Check
                    type = "menu";
                }catch(Exception ex){
                    type = "command";
                }

                int iRet = 0;
                StringList slRet = new StringList();
                String ret	= SchemaUtil.getData(context, type, parentName, "child");	
                slRet 		= FrameworkUtil.split(ret, SchemaConstants.SELECT_SEPERATOR);
                iRet 		= slRet.indexOf(sName);

                if("menu".equals(type)) {
                    sb2.append("#mod ").append(type).append(" \"").append(parentName).append("\" ").append("add menu").append(" ").append(sName).append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
                    sb2.append("#mod ").append(type).append(" \"").append(parentName).append("\" ").append("order menu").append(" ").append(sName).append(" ").append(iRet+1).append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
                } else if( "command".equals(type)) {
                    sb2.append("#mod ").append(type).append(" \"").append(parentName).append("\" ").append("add command").append(" ").append(sName).append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
                    sb2.append("#mod ").append(type).append(" \"").append(parentName).append("\" ").append("order command").append(" ").append(sName).append(" ").append(iRet+1).append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
                }
            }

            sb2.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
            sb.append(sb2);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
	
	
	/**
     * ctx1 기준으로 ctx2 에 Add or Mod
     * ctx1 Ori, ctx2 Mod
     * @param ctx1
     * @param ctx2
     * @param name
     * @return
     * @throws Exception
     */
    public static String getMenuModMQL(Context ctx1, Context ctx2, String sName) throws Exception{
        try {
            StringBuilder sb 		= new StringBuilder();
            
            ctx1					= BusinessViewMain.ctx1;
            ctx2					= BusinessViewMain.ctx2;
            HashMap mapMenuInfoOri	= (HashMap) CacheManager.getInstance().getValue(ctx1, _entityNames.MENUS, new String[] { sName });
            CacheManager.getInstance().clearTenant(ctx1);
            HashMap mapMenuInfoMod	= (HashMap) CacheManager.getInstance().getValue(ctx2, _entityNames.MENUS, new String[] { sName });
            CacheManager.getInstance().clearTenant(ctx2);
            
            sb.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.MENU, sName, false));

            String strLabelOri 			= SchemaUtil.nullToEmpty((String) mapMenuInfoOri.get(SchemaConstants.LABEL));
            String strLabelMod 			= SchemaUtil.nullToEmpty((String) mapMenuInfoMod.get(SchemaConstants.LABEL));
            String strDescOri			= SchemaUtil.nullToEmpty((String) mapMenuInfoOri.get(SchemaConstants.SELECT_DESCRIPTION));
            String strDescMod			= SchemaUtil.nullToEmpty((String) mapMenuInfoMod.get(SchemaConstants.SELECT_DESCRIPTION));
            String strHrefOri 			= SchemaUtil.nullToEmpty((String) mapMenuInfoOri.get(SchemaConstants.HREF));
            String strHrefMod 			= SchemaUtil.nullToEmpty((String) mapMenuInfoMod.get(SchemaConstants.HREF));
            String strAltOri 			= SchemaUtil.nullToEmpty((String) mapMenuInfoOri.get(SchemaConstants.ALT));
            String strAltMod 			= SchemaUtil.nullToEmpty((String) mapMenuInfoMod.get(SchemaConstants.ALT));
            
            HashMap settingMapOriTmp	= (HashMap) mapMenuInfoOri.get(SchemaConstants.SETTINGS);
            HashMap settingMapModTmp	= (HashMap) mapMenuInfoMod.get(SchemaConstants.SETTINGS);
            HashMap settingMapOri 		= new HashMap();
            HashMap settingMapMod 		= new HashMap();
            if(null != settingMapOriTmp)
            	settingMapOri.putAll((HashMap) settingMapOriTmp.clone());
            if(null != settingMapModTmp)
            	settingMapMod.putAll((HashMap) settingMapModTmp.clone());
            
            HashMap propertiesMapOriTmp	= (HashMap) mapMenuInfoOri.get(SchemaConstants.PROPERTIES);
            HashMap propertiesMapModTmp	= (HashMap) mapMenuInfoMod.get(SchemaConstants.PROPERTIES);
            HashMap propertiesMapOri 	= new HashMap();
            HashMap propertiesMapMod 	= new HashMap();
            if(null != propertiesMapOriTmp)
            	propertiesMapOri.putAll((HashMap) propertiesMapOriTmp.clone());
            if(null != propertiesMapModTmp)
            	propertiesMapMod.putAll((HashMap) propertiesMapModTmp.clone());

            MapList childrenListOri		= (MapList) mapMenuInfoOri.get("children");
            MapList childrenListMod		= (MapList) mapMenuInfoMod.get("children");
            
            
            if(!strDescMod.equals(strDescOri))
            	SchemaUtil.settingDataLine(sb, SchemaConstants.SELECT_DESCRIPTION, 	strDescOri,	12);
            if(!strLabelMod.equals(strLabelOri))
            	SchemaUtil.settingDataLine(sb, SchemaConstants.LABEL, 				strLabelOri, 		12);
            if(!strHrefMod.equals(strHrefOri))
            	SchemaUtil.settingDataLine(sb, SchemaConstants.HREF, 				strHrefOri, 		12);
            if(!strAltMod.equals(strAltOri))
            	SchemaUtil.settingDataLine(sb, SchemaConstants.ALT, 				strAltOri, 		12);
            
            // Add Setting
            if( settingMapOri != null ){
                Iterator settingItr 	= settingMapOri.keySet().iterator();
                while(settingItr.hasNext())
                {
                    String settingName 	= (String)settingItr.next();
                    String settingValue = (String)settingMapOri.get(settingName);
                    if((!settingMapMod.containsKey(settingName)) || (settingMapMod.containsKey(settingName) && !((String)settingMapMod.get(settingName)).equals(settingValue))) {
                    	sb.append(_TAB).append("add ").append(SchemaConstants.SETTING).append(" ").append(DOUBLE_QUOTE).append(settingName).append(DOUBLE_QUOTE).append(" ").append(DOUBLE_QUOTE).append(settingValue).append(DOUBLE_QUOTE).append(RECORDSEP);//label
                    }
                    
                    settingMapMod.remove(settingName);
                }
            }
            // Remove Setting
            if( settingMapMod != null ){
            	Iterator settingItr = settingMapMod.keySet().iterator();
            	while(settingItr.hasNext())
            	{
            		String settingName 	= (String)settingItr.next();
            		String settingValue = (String)settingMapMod.get(settingName);
        			sb.append(_TAB).append("remove ").append(SchemaConstants.SETTING).append(" ").append(DOUBLE_QUOTE).append(settingName).append(DOUBLE_QUOTE).append(" ").append(RECORDSEP);//label
            	}
            }
            
            // Add Children
            if( childrenListOri != null )
            {
            	if(null == childrenListMod)
            		childrenListMod	= new MapList();
            	int iOriSize		= childrenListOri.size();
            	int iModSize		= childrenListMod.size();
            	for(int j = 0; j < iOriSize; j++)
            	{
             		Map childMap 	= (Map)childrenListOri.get(j);
            		String name 	= (String)childMap.get("name");
            		String type 	= (String)childMap.get("type");
            		boolean isExist	= false;
            
            		iModSize		= childrenListMod.size();
            		for(int i = 0; i < iModSize; i++)
            		{
            			Map mTmpMap		= (Map) childrenListMod.get(i);
            			String sTmpName	= (String)mTmpMap.get("name");
            			String sTmpType = (String)mTmpMap.get("type");
            			
            			if(name.equals(sTmpName) && type.equals(sTmpType)) {
            				childrenListMod.remove(i);
            				isExist	= true;
            				break;
            			}
            		}
            		
            		if(!isExist)
            		{
            			sb.append(_TAB).append("add ").append(type).append(" ").append(DOUBLE_QUOTE).append(name).append(DOUBLE_QUOTE).append(RECORDSEP);
            		}
            		sb.append(_TAB).append("order ").append(type).append(" ").append(DOUBLE_QUOTE).append(name).append(DOUBLE_QUOTE).append(" ").append(j + 1).append(RECORDSEP);
            		
            	}
            }
            // Remove Children
            if( childrenListMod != null )
            {
            	int iModSize		= childrenListMod.size();
            	for(int j = 0; j < iModSize; j++)
            	{
            		Map childMap 	= (Map)childrenListMod.get(j);
            		String name 	= (String)childMap.get("name");
            		String type 	= (String)childMap.get("type");
            		
            		sb.append(_TAB).append("remove ").append(type).append(" ").append(DOUBLE_QUOTE).append(name).append(DOUBLE_QUOTE).append(RECORDSEP);
            	}
            }
            sb.append(SchemaConstants.SEMI_COLON);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
