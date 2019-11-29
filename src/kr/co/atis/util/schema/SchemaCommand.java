package kr.co.atis.util.schema;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.cache.CacheManager;
import com.matrixone.apps.cache.CacheManager._entityNames;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UIMenu;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringItr;
import matrix.util.StringList;

public class SchemaCommand {
	private static final String _TAB 			= SchemaConstants._TAB;
	private static final String RECORDSEP 		= SchemaConstants.RECORDSEP;
	private static final String DOUBLE_QUOTE 	= SchemaConstants.DOUBLE_QUOTE;
	private static final String COMMAND			= SchemaConstants.COMMAND;
	
	public static String getCommandMQL(Context context, String sName) throws Exception {
        try {

            StringBuilder sb 	= new StringBuilder();
            CacheManager.getInstance().clearTenant(context);
            UIMenu uiMenuBean 	= new UIMenu();
            HashMap mapMenuInfo = uiMenuBean.getCommand(context, sName);

            sb.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.COMMAND, sName, true));
 
            String strLabel 		= (String) mapMenuInfo.get(SchemaConstants.LABEL);
            String strDescription 	= (String) mapMenuInfo.get(DomainObject.SELECT_DESCRIPTION);
            String strHref 			= (String) mapMenuInfo.get(SchemaConstants.HREF);
            String strAlt 			= (String) mapMenuInfo.get(SchemaConstants.ALT);
            String strCode 			= (String) mapMenuInfo.get(SchemaConstants.CODE);
            HashMap settingMap 		= (HashMap) mapMenuInfo.get(SchemaConstants.SETTINGS);
            HashMap propertiesMap 	= (HashMap) mapMenuInfo.get(SchemaConstants.PROPERTIES);
            StringList slRoles 		= (StringList) mapMenuInfo.get("roles");
           
            String strUser 			= SchemaUtil.getData(context, COMMAND, sName, "user");
            slRoles 				= FrameworkUtil.split(strUser, SchemaConstants.SELECT_SEPERATOR);

            SchemaUtil.settingDataLine(sb, "description", 			strDescription, 12);
            SchemaUtil.settingDataLine(sb, SchemaConstants.LABEL, 	strLabel, 		12);
            SchemaUtil.settingDataLine(sb, SchemaConstants.HREF, 	strHref, 		12);
            SchemaUtil.settingDataLine(sb, SchemaConstants.ALT, 	strAlt, 		12);
            SchemaUtil.settingDataLine(sb, SchemaConstants.CODE, 	strCode, 		12);
 
            sb.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.SETTING, settingMap));
            
            StringItr roleItr = new StringItr(slRoles);
            while(roleItr.next()){
                String userName = roleItr.obj();
                SchemaUtil.settingDataLine(sb, "user", userName, 12);
            }

            if(null != propertiesMap) {
            	sb.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.PROPERTY, propertiesMap));
            }
            sb.append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
            sb.append("add property command_").append(StringUtils.deleteWhitespace(sName)).append(" on program 'eServiceSchemaVariableMapping.tcl' to command '").append(sName).append("';").append(SchemaConstants.RECORDSEP);

            StringBuilder sb2 = new StringBuilder();
            sb2.append(RECORDSEP);
            sb2.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
            sb2.append("# Parent Information").append(RECORDSEP);
            String strParent = MqlUtil.mqlCommand(context, new StringBuilder("print command ").append(sName).append(" select menu channel").toString());	// Parent Menu, Parent Channel
 
            StringList list 	= FrameworkUtil.split(strParent, "\n");
            list.sort();
            StringItr listItr 	= new StringItr(list);
            while(listItr.next()){
                String parent = listItr.obj();
                if(!parent.contains("="))
                	continue;
                StringList parentInfo = FrameworkUtil.split(parent, "=");
                String type = ((String) parentInfo.get(0)).trim();
                String parentName = ((String) parentInfo.get(1)).trim();
//                try {
//                	SchemaUtil.getData(context, "menu", parentName, "name");
//                    type = "menu";
//                } catch(Exception ex) {
//                    type = "channel";
//                }

                int iRet = 0;
                StringList slRet = new StringList();
                if("menu".equals(type)){
                	String ret	= SchemaUtil.getData(context, type, parentName, "child");
                    slRet = FrameworkUtil.split(ret, SchemaConstants.SELECT_SEPERATOR);
                    iRet = slRet.indexOf(sName);
                }else if( "channel".equals(type)){
                	String ret	= SchemaUtil.getData(context, type, parentName, "command");
                    slRet = FrameworkUtil.split(ret, SchemaConstants.SELECT_SEPERATOR);
                    iRet = slRet.indexOf(sName);
                }

                if("menu".equals(type)){
                    sb2.append("# mod ").append(type).append(" \"").append(parentName).append("\" ").append("add command").append(" ").append(sName).append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
                    sb2.append("# mod ").append(type).append(" \"").append(parentName).append("\" ").append("order command").append(" ").append(sName).append(" ").append(iRet+1).append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
                }else if( "channel".equals(type)){
                    String after = "";
                    if(iRet > 0 ){
                        after = (String)slRet.get(iRet-1);
                    }
                    sb2.append("# mod ").append(type).append(" \"").append(parentName).append("\" ").append("place").append(" ").append(sName).append(" ").append("after").append(" \"").append(after).append("\"").append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
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
    public static String getCommandModMQL(Context ctx1, Context ctx2, String sName) throws Exception{
        String sExtension = StringUtils.substring(sName, sName.lastIndexOf(".")+1);
        try {
            StringBuilder sb 		= new StringBuilder();
            UIMenu uiMenuBean 		= new UIMenu();
            ctx1					= BusinessViewMain.ctx1;
            ctx2					= BusinessViewMain.ctx2;
            HashMap mapMenuInfoOri	= (HashMap) CacheManager.getInstance().getValue(ctx1, _entityNames.COMMANDS, new String[] { sName });
            CacheManager.getInstance().clearTenant(ctx1);
            HashMap mapMenuInfoMod	= (HashMap) CacheManager.getInstance().getValue(ctx2, _entityNames.COMMANDS, new String[] { sName });
            CacheManager.getInstance().clearTenant(ctx2);
            
            sb.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
            sb.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.COMMAND, sName, false));
 
            String strLabelOri 			= StringUtils.trimToEmpty((String)mapMenuInfoOri.get(SchemaConstants.LABEL));
            String strLabelMod 			= StringUtils.trimToEmpty((String)mapMenuInfoMod.get(SchemaConstants.LABEL));
            String strDescOri 			= StringUtils.trimToEmpty((String)mapMenuInfoOri.get(DomainObject.SELECT_DESCRIPTION));
            String strDescMod 			= StringUtils.trimToEmpty((String)mapMenuInfoMod.get(DomainObject.SELECT_DESCRIPTION));
            String strHrefOri 			= StringUtils.trimToEmpty((String)mapMenuInfoOri.get(SchemaConstants.HREF));
            String strHrefMod 			= StringUtils.trimToEmpty((String)mapMenuInfoMod.get(SchemaConstants.HREF));
            String strAltOri 			= StringUtils.trimToEmpty((String)mapMenuInfoOri.get(SchemaConstants.ALT));
            String strAltMod 			= StringUtils.trimToEmpty((String)mapMenuInfoMod.get(SchemaConstants.ALT));
            String strCodeOri 			= StringUtils.trimToEmpty((String)mapMenuInfoOri.get(SchemaConstants.CODE));
            String strCodeMod 			= StringUtils.trimToEmpty((String)mapMenuInfoMod.get(SchemaConstants.CODE));
            HashMap settingMapOri 		= (HashMap)mapMenuInfoOri.get(SchemaConstants.SETTINGS);
            HashMap settingMapMod 		= (HashMap)mapMenuInfoMod.get(SchemaConstants.SETTINGS);
//            HashMap propertiesMapOri 	= (HashMap)mapMenuInfoOri.get(SchemaConstants.PROPERTIES);
//            HashMap propertiesMapMod 	= (HashMap)mapMenuInfoMod.get(SchemaConstants.PROPERTIES);
            StringList slRolesOri 		= (StringList)mapMenuInfoOri.get("roles");
            StringList slRolesMod 		= (StringList)mapMenuInfoMod.get("roles");
           
            String strUserOri			= SchemaUtil.getData(ctx1, COMMAND, sName, "user");
            String strUserMod 			= SchemaUtil.getData(ctx2, COMMAND, sName, "user");
            slRolesOri 					= FrameworkUtil.split(strUserOri, SchemaConstants.SELECT_SEPERATOR);
            slRolesMod 					= FrameworkUtil.split(strUserMod, SchemaConstants.SELECT_SEPERATOR);

            if(!strDescMod.equals(strDescOri))
            	SchemaUtil.settingDataLine(sb, "description", 			strDescOri, 12);
            if(!strLabelMod.equals(strLabelOri))
            	SchemaUtil.settingDataLine(sb, SchemaConstants.LABEL, 	strLabelOri,12);
            if(!strHrefMod.equals(strHrefOri))
            	SchemaUtil.settingDataLine(sb, SchemaConstants.HREF, 	strHrefOri, 12);
            if(!strAltMod.equals(strAltOri))
            	SchemaUtil.settingDataLine(sb, SchemaConstants.ALT, 	strAltOri, 	12);
            if(!strCodeMod.equals(strCodeOri))
            	SchemaUtil.settingDataLine(sb, SchemaConstants.CODE, 	strCodeOri, 12);
 
            // Add Setting
            Iterator settingItrOri = settingMapOri.keySet().iterator();
            while(settingItrOri.hasNext())
            {
                String settingName 	= (String) settingItrOri.next();
                String settingValue = (String) settingMapOri.get(settingName);
                if(!settingMapMod.containsKey(settingName) || (settingMapMod.containsKey(settingName) && !((String) settingMapMod.get(settingName)).toString().equals(settingValue)))
                	sb.append(_TAB).append("add ").append(SchemaConstants.SETTING).append(" \"").append(settingName).append("\" \"").append(settingValue).append("\"").append(RECORDSEP);//label
                settingMapMod.remove(settingName);
            }
            // Remove Setting
            Iterator settingItrMod	= settingMapMod.keySet().iterator();
            while(settingItrMod.hasNext())
            {
            	String settingName 	= (String) settingItrMod.next();
//            	String settingValue = (String) settingMapMod.get(settingName);
        		sb.append(_TAB).append("remove ").append(SchemaConstants.SETTING).append(" \"").append(settingName).append("\"").append(RECORDSEP);
            }
            

            // Add Role
            StringItr roleItrOri = new StringItr(slRolesOri);
            while(roleItrOri.next()){
                String userName = roleItrOri.obj();
                if(!slRolesMod.contains(userName))
                	sb.append(_TAB).append("add user").append(" ").append(DOUBLE_QUOTE).append(userName).append(DOUBLE_QUOTE).append(RECORDSEP);
                else
                	slRolesMod.remove(userName);
            }
            // Remove Role
            StringItr roleItrMod = new StringItr(slRolesMod);
            while(roleItrMod.next()){
            	String userName = roleItrMod.obj();
        		sb.append(_TAB).append("remove user").append(" ").append(DOUBLE_QUOTE).append(userName).append(DOUBLE_QUOTE).append(RECORDSEP);
            }
            sb.append(SchemaConstants.SEMI_COLON);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
