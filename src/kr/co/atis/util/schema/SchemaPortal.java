package kr.co.atis.util.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.matrixone.apps.cache.CacheManager;
import com.matrixone.apps.cache.CacheManager._entityNames;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UICache;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;

public class SchemaPortal {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	
	public static String getPortalMQL(Context context, String name) throws Exception {
        StringBuilder sbResult = new StringBuilder();
        try {
            HashMap mPortal			= UICache.getPortal(context, name);
            HashMap mSettings 		= (HashMap) mPortal.get("settings");
            HashMap mProperties 	= (HashMap) mPortal.get("properties");
            MapList mChannelRows	= (MapList) mPortal.get("channel_rows");

            sbResult.append(SchemaUtil.createBusinessHeaderInfo("Portal", name, true));
            SchemaUtil.settingDataLine(sbResult, "description", (String) mPortal.get("description"), 12);
            SchemaUtil.settingDataLine(sbResult, "alt", SchemaUtil.nullToEmpty((String) mPortal.get("alt")), 12);
            
            int iRowSize		= mChannelRows.size();
            int iCount			= 0;
            for(int i = 0; i < iRowSize; i++)	// Channel Rows (each)
            {
            	Map mChannelRow	= (Map) mChannelRows.get(i);
            	MapList mRows	= (MapList) mChannelRow.get("channels");
            	
            	iCount			= mRows.size();
            	if(iCount > 0)
            	{
            		sbResult.append(_TAB).append("channel ");
            		for(int j = 0; j < iCount; j++)	// Commands in channel
	            	{
	            		Map mChannel	= (Map) mRows.get(j);
	            		String sType	= (String) mChannel.get("type");
	            		String sName	= (String) mChannel.get("name");

	            		if(j > 0)
	            			sbResult.append(",");
	            		sbResult.append(sName);
	            	}
            		sbResult.append(RECORDSEP);
            	}
            }


            if(null != mSettings) {
            	sbResult.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.SETTING, mSettings));
            }

            if(null != mProperties) {
            	sbResult.append(SchemaUtil.getPropertyForBusinessMap(context, SchemaConstants.PROPERTY, mProperties));
            }
            sbResult.append(SchemaConstants.SEMI_COLON);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
    }
    
    
    public static String getPortalModMQL(Context ctx1, Context ctx2, String name) throws Exception {
        StringBuilder sbResult = new StringBuilder();
        try {
            ctx1					= BusinessViewMain.ctx1;
            ctx2					= BusinessViewMain.ctx2;
        	HashMap mPortalMod		= (HashMap) CacheManager.getInstance().getValue(ctx1, _entityNames.PORTALS, new String[] { name });
            CacheManager.getInstance().clearTenant(ctx1);
            HashMap mPortalOri		= (HashMap) CacheManager.getInstance().getValue(ctx2, _entityNames.PORTALS, new String[] { name });
            CacheManager.getInstance().clearTenant(ctx2);

            HashMap mSettingsMod 	= (HashMap) mPortalMod.get("settings");
            HashMap mSettingsOri 	= (HashMap) mPortalOri.get("settings");
            HashMap mPropertiesMod 	= (HashMap) mPortalMod.get("properties");
            HashMap mPropertiesOri 	= (HashMap) mPortalOri.get("properties");
            MapList mChannelRowsMod	= (MapList) mPortalMod.get("channel_rows");
            MapList mChannelRowsOri	= (MapList) mPortalOri.get("channel_rows");

            sbResult.append(SchemaUtil.createBusinessHeaderInfo("Portal", name, false));
            SchemaUtil.settingDataLine(sbResult.append(_TAB), "description", (String) mPortalOri.get("description"), 12);
            SchemaUtil.settingDataLine(sbResult.append(_TAB), "alt", SchemaUtil.nullToEmpty((String) mPortalOri.get("alt")), 12);
            
            StringBuilder sbRemove		= new StringBuilder();
            if(!mChannelRowsMod.equals(mChannelRowsOri))
            {
            	// Add Channel
            	MapList mlDupCheckList	= new MapList();
	            int iRowSize			= mChannelRowsOri.size();
	            int iCount				= 0;
	            for(int i = 0; i < iRowSize; i++)	// Channel Rows (each)
	            {
	            	Map mChannelRowOri	= (Map) mChannelRowsOri.get(i);
	            	MapList mRowsOri	= (MapList) mChannelRowOri.get("channels");
	            	
	            	iCount				= mRowsOri.size();
	            	if(iCount > 0)
	            	{
	            		String sBefore		= "";
	            		for(int j = 0; j < iCount; j++)	// Commands in channel
		            	{
		            		Map mChannel	= (Map) mRowsOri.get(j);
		            		String sType	= (String) mChannel.get("type");
		            		String sName	= (String) mChannel.get("name");
		            		
		            		sbResult.append(_TAB).append(_TAB).append("place ").append(sName);
		            		if(i > 0 && j == 0)
		            			sbResult.append(" newrow ");
		            		sbResult.append(" after '").append(sBefore).append("'").append(RECORDSEP);
		            		sBefore			= sName;

		            		mlDupCheckList.add(mChannel);
		            	}
	            	}
	            }
	            // Remove Channel
	            iRowSize				= mChannelRowsMod.size();
	            for(int i = 0; i < iRowSize; i++)	// Channel Rows (each)
	            {
	            	Map mChannelRowMod	= (Map) mChannelRowsMod.get(i);
	            	MapList mRowsMod	= (MapList) mChannelRowMod.get("channels");
	            	
	            	iCount				= mRowsMod.size();
	            	if(iCount > 0)
	            	{
	            		for(int j = 0; j < iCount; j++)	// Commands in channel
	            		{
	            			Map mChannel	= (Map) mRowsMod.get(j);
	            			String sType	= (String) mChannel.get("type");
	            			String sName	= (String) mChannel.get("name");
	            			
	            			if(mlDupCheckList.contains(mChannel))
	            				continue;
	            			
	            			sbRemove.append(_TAB).append("'").append(sName).append("'");
	            		}
	            	}
	            }
            }


            // Add Settings
            if(null != mSettingsOri) {
            	for(Iterator propKeyItr = mSettingsOri.keySet().iterator(); propKeyItr.hasNext();){
            		String sPropKey = (String) propKeyItr.next();
            		String sPropVal = (String) mSettingsOri.get(sPropKey);
            		
            		if((!mSettingsMod.containsKey(sPropKey)) ||
            			(mSettingsMod.containsKey(sPropKey) && !mSettingsMod.get(sPropVal).equals(mSettingsOri.get(sPropVal))))
            		{
            			sbResult.append(_TAB).append(_TAB).append("add setting").append(_TAB).append("'").append(sPropKey).append("' '").append(sPropVal).append("'").append(RECORDSEP);
            		}
            		
            		if(mSettingsMod.containsKey(sPropKey)) 
            		{
            			mSettingsMod.remove(sPropKey);
            		}
            	}
            }
            // Remove Settings
            if(null != mSettingsMod) {
            	for(Iterator propKeyItr = mSettingsMod.keySet().iterator(); propKeyItr.hasNext();){
            		String sPropKey = (String) propKeyItr.next();
            		String sPropVal = (String) mSettingsMod.get(sPropKey);
            		
        			sbResult.append(_TAB).append(_TAB).append("remove setting").append(_TAB).append("'").append(sPropKey).append("'").append(RECORDSEP);
            	}
            }

            if(sbRemove.length() > 0)
            {
            	sbResult.append("remove channel ").append(sbRemove).append(RECORDSEP);
            }
            sbResult.append(SchemaConstants.SEMI_COLON);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
    }
}
