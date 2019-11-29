package kr.co.atis.util.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.cache.CacheManager;
import com.matrixone.apps.cache.CacheManager._entityNames;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UICache;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;

public class SchemaChannel {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	private static final String CHANNEL		= SchemaConstants.CHANNEL;
	
	public static String getChannelMQL(Context context, String name) throws Exception 
	{
        StringBuilder sbResult 	= new StringBuilder();
        try {
            HashMap mChannel  	= UICache.getChannel(context, name);
            MapList mCommands 	= (MapList) mChannel.get("commands");
            HashMap mSettings 	= (HashMap) mChannel.get("settings");
            HashMap mProperties = (HashMap) mChannel.get("properties");

            String sHeight 		= SchemaUtil.getData(context, CHANNEL, name, "height");
            String sParents 	= SchemaUtil.getData(context, CHANNEL, name, "parent");

            sbResult.append(SchemaUtil.createBusinessHeaderInfo(CHANNEL, name, true));
            SchemaUtil.settingDataLine(sbResult, "description", (String) mChannel.get("description"), 	12);
            SchemaUtil.settingDataLine(sbResult, "height", 		sHeight,								12);

            if(null != mCommands) {
	            for(Iterator cmdItr = mCommands.iterator(); cmdItr.hasNext();) {
	                Map mCmd 		= (Map) cmdItr.next();
	                String sCmdType = (String) mCmd.get("type");
	                String sCmdName = (String) mCmd.get("name");
	                SchemaUtil.settingDataLine(sbResult, sCmdType, sCmdName, 12);
	            }
            }

            if(null != mSettings) {
            	sbResult.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.SETTING, mSettings));
            }

            if(null != mProperties) {
            	sbResult.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.PROPERTY, mProperties));
            }
            sbResult.append(SchemaConstants.SEMI_COLON);

            if( StringUtils.isNotEmpty(sParents) )
            {
                String[] parents = StringUtils.split(sParents, SchemaConstants.SELECT_SEPERATOR);
                if( parents != null && parents.length > 0 )
                {
                    sbResult.append(RECORDSEP).append(RECORDSEP);
                    sbResult.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
                    for(String sP : parents)
                    {
                        sbResult.append("# Parent Portal : '").append(sP).append("' ").append(RECORDSEP);
                    }
                    sbResult.append(SchemaConstants.NOTE_AREA);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
    }
    
    
    
    public static String getChannelModMQL(Context ctx1, Context ctx2, String name) throws Exception {
        StringBuilder sbResult 		= new StringBuilder();
        try {
        	ctx1					= BusinessViewMain.ctx1;
            ctx2					= BusinessViewMain.ctx2;
        	HashMap mChannelMod		= (HashMap) CacheManager.getInstance().getValue(ctx1, _entityNames.CHANNELS, new String[] { name });
            CacheManager.getInstance().clearTenant(ctx1);
            HashMap mChannelOri		= (HashMap) CacheManager.getInstance().getValue(ctx2, _entityNames.CHANNELS, new String[] { name });
            CacheManager.getInstance().clearTenant(ctx2);
            
            MapList mCommandsMod 	= (MapList) mChannelMod.get("commands");
            MapList mCommandsOri 	= (MapList) mChannelOri.get("commands");
            HashMap mSettingsMod 	= (HashMap) mChannelMod.get("settings");
            HashMap mSettingsOri 	= (HashMap) mChannelOri.get("settings");
//            HashMap mPropertiesMod 	= (HashMap) mChannelMod.get("properties");
//            HashMap mPropertiesOri 	= (HashMap) mChannelOri.get("properties");

            String sHeightMod 		= SchemaUtil.getData(ctx1, CHANNEL, name, "height");
            String sHeightOri 		= SchemaUtil.getData(ctx2, CHANNEL, name, "height");
//            String sParentsMod 		= (String) MqlUtil.mqlCommand(ctx1, new StringBuilder("print channel '").append(name).append("' select parent dump |").toString());
//            String sParentsOri 		= (String) MqlUtil.mqlCommand(ctx2, new StringBuilder("print channel '").append(name).append("' select parent dump |").toString());

            sbResult.append(SchemaUtil.createBusinessHeaderInfo(CHANNEL, name, false));

            if(!mChannelMod.get("description").equals(mChannelOri.get("description")))
            	SchemaUtil.settingDataLine(sbResult.append(_TAB), "description", (String) mChannelOri.get("description"), 	12);
            if(!sHeightMod.equals(sHeightOri))
            	SchemaUtil.settingDataLine(sbResult.append(_TAB), "height",		 sHeightOri, 								12);

            // Add Commands
            if(null != mCommandsOri) {
            	String sBefore			= "";
	            for(Iterator cmdItr = mCommandsOri.iterator(); cmdItr.hasNext();){
	                Map mCmdOri			= (Map) cmdItr.next();
	                String sCmdTypeOri 	= (String) mCmdOri.get("type");
	                String sCmdNameOri 	= (String) mCmdOri.get("name");
	                boolean isExists	= false;
	                
	                // place NAME 	| before	| NAME
	                // 				| after		| NAME
	                // place dataobject NAME [user USER] | before | NAME [user USER] |
	                //                                   | after  | NAME [user USER] |
	                // place webreport NAME [user USER]  | before | NAME [user USER] |
	                //                                   | after  | NAME [user USER] |
	                
	                for(int i = 0; i < mCommandsMod.size(); i++)
	                {
	                	Map mCmdMod			= (Map) mCommandsMod.get(i);
	                	String sCmdTypeMod 	= (String) mCmdMod.get("type");
	                	String sCmdNameMod 	= (String) mCmdMod.get("name");
	                	
	                	if(sCmdTypeOri.equals(sCmdTypeMod) && sCmdNameOri.equals(sCmdNameMod))
	                	{
	                		mCommandsMod.remove(i);
	                		isExists		= true;
	                		break;
	                	}
	                }
	                
	                if(!isExists)
	                {
	                	sbResult.append(_TAB).append(_TAB).append("place ");
	                	if(!sCmdTypeOri.equalsIgnoreCase("command")) {
	                		sbResult.append(sCmdTypeOri);
	                	}
	                	sbResult.append(" '").append(sCmdNameOri).append("' after '").append(sBefore).append("'").append(RECORDSEP);
	                }
	                sBefore					= sCmdNameOri;
	            }
            }
            // Remove Commands
            if(null != mCommandsMod) {
	            for(Iterator cmdItr = mCommandsMod.iterator(); cmdItr.hasNext();){
	                Map mCmdMod			= (Map) cmdItr.next();
	                String sCmdTypeMod 	= (String) mCmdMod.get("type");
	                String sCmdNameMod 	= (String) mCmdMod.get("name");
//	                boolean isExists	= false;

	                // remove | command NAME{,NAME}
	                //        | dataobject NAME [user USER_NAME]
	                //        | webreport NAME [user USER_NAME]
                	sbResult.append(_TAB).append(_TAB).append("remove ").append(sCmdTypeMod).append(" '").append(sCmdNameMod).append("'").append(RECORDSEP);
	            }
            }
            
            // Add Settings
            if(null != mSettingsOri) {
	            for(Iterator propKeyItr = mSettingsOri.keySet().iterator(); propKeyItr.hasNext();) {
	                String sPropKey = (String) propKeyItr.next();
	                String sPropVal = (String) mSettingsOri.get(sPropKey);
	                
	                if(null == mSettingsMod || (!mSettingsMod.containsKey(sPropKey)) ||
	                	(mSettingsMod.containsKey(sPropKey) && !mSettingsMod.get(sPropKey).equals(sPropVal))) {
	                	sbResult.append(_TAB).append(_TAB).append("add setting").append(_TAB).append("'").append(sPropKey).append("' '").append(sPropVal).append("'").append(RECORDSEP);
	                }
	                
	                if(null != mSettingsMod && mSettingsMod.containsKey(sPropKey))
	                	mSettingsMod.remove(sPropKey);
	            }
            }
            // Remove Settings
            if(null != mSettingsMod) {
            	for(Iterator propKeyItr = mSettingsMod.keySet().iterator(); propKeyItr.hasNext();) {
            		String sPropKey = (String) propKeyItr.next();
            		String sPropVal = (String) mSettingsMod.get(sPropKey);
            		
        			sbResult.append(_TAB).append(_TAB).append("remove setting").append(_TAB).append("'").append(sPropKey).append("'").append(RECORDSEP);
            	}
            }
            sbResult.append(SchemaConstants.SEMI_COLON);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
    }
}
