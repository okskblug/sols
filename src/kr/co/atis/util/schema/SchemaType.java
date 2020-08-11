package kr.co.atis.util.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.util.FrameworkUtil;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class SchemaType {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	
	public static String getTypeMQL(Context context, String name) throws Exception {
        StringBuilder sbResult = new StringBuilder();
        try {
        	String s1 		= SchemaUtil.getData(context, "type", name, "abstract description hidden");
            StringList sl1 	= FrameworkUtil.split(s1, SchemaConstants.SELECT_SEPERATOR);
 
            sbResult.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.TYPE, name, true));
            String sDerived	= SchemaUtil.getData(context, "type", name, "derived");

//            sbResult.append("#del type '").append(name).append("';").append(SchemaConstants.RECORDSEP);
//            sbResult.append("add type '").append(name).append("'").append(SchemaConstants.RECORDSEP);

            SchemaUtil.settingDataLine(sbResult, "description", (String) sl1.get(1), 12);
            if(!"".equals(sDerived))
            	SchemaUtil.settingDataLine(sbResult, "derived", sDerived, 12);
            SchemaUtil.settingDataLine(sbResult, "abstract", (String) sl1.get(0), 12);
            if(sl1.get(2).toString().equalsIgnoreCase("true"))
            	SchemaUtil.settingDataLine(sbResult, "hidden", (String) sl1.get(2), 12);
 
//            String s2 = MqlUtil.mqlCommand(context, new StringBuilder("print type '").append(name).append("' select attribute dump ").append(SchemaConstants.SELECT_SEPERATOR).toString());
            String s3 = SchemaUtil.getData(context, "type", name, "immediateattribute");

//            StringList sl2 = FrameworkUtil.split(s2, SchemaConstants.SELECT_SEPERATOR);
            StringList sl3 = FrameworkUtil.split(s3, SchemaConstants.SELECT_SEPERATOR);

            sl3.sort();
            for (Iterator itr1 = sl3.iterator(); itr1.hasNext();) {
                String s4 = (String) itr1.next();
                SchemaUtil.settingDataLine(sbResult, "attribute", s4, 12);
            }

            sbResult.append(SchemaUtil.getTriggerForBusiness(context, "type", new StringBuilder("print 'type' '").append(name).append("' select immediatetrigger dump ").append(SchemaConstants.SELECT_SEPERATOR).toString()));

            String s5 = SchemaUtil.getData(context, "type", name, "property[application].value property[version].value property[installer].value property[installed date].value property[original name].value");
            StringList sl4 = FrameworkUtil.split(s5, SchemaConstants.SELECT_SEPERATOR);

            if(null != sl4) {
            	int iListSize	= sl4.size();
	            if(iListSize > 0) sbResult.append(_TAB).append("property   'application'     value '").append(sl4.get(0)).append("'").append(SchemaConstants.RECORDSEP);
	            if(iListSize > 1) sbResult.append(_TAB).append("property   'version'         value '").append(sl4.get(1)).append("'").append(SchemaConstants.RECORDSEP);
	            if(iListSize > 2) sbResult.append(_TAB).append("property   'installer'       value '").append(sl4.get(2)).append("'").append(SchemaConstants.RECORDSEP);
	            if(iListSize > 3) sbResult.append(_TAB).append("property   'installed date'  value '").append(sl4.get(3)).append("'").append(SchemaConstants.RECORDSEP);
	            if(iListSize > 4) sbResult.append(_TAB).append("property   'original name'   value '").append(sl4.get(4)).append("'").append(SchemaConstants.RECORDSEP);
            }
            sbResult.append(";").append(SchemaConstants.RECORDSEP);
            sbResult.append("add property type_").append(StringUtils.deleteWhitespace(name)).append(" on program eServiceSchemaVariableMapping.tcl to type '").append(name).append("';").append(SchemaConstants.RECORDSEP);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
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
    public static String getTypeModMQL(Context ctx1, Context ctx2, String name) throws Exception {
        StringBuilder sbResult = new StringBuilder();
        try {
        	String s1Ori 		= SchemaUtil.getData(ctx1, "type", name, "abstract description hidden");
            String s1Mod 		= SchemaUtil.getData(ctx2, "type", name, "abstract description hidden");
            
            StringList sl1Ori	= FrameworkUtil.split(s1Ori, SchemaConstants.SELECT_SEPERATOR);
            StringList sl1Mod	= FrameworkUtil.split(s1Mod, SchemaConstants.SELECT_SEPERATOR);
 
            sbResult.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
            sbResult.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.TYPE, name, false));
            
            String sDerived1 	= SchemaUtil.getData(ctx1, "type", name, "derived");
            String sDerived2 	= SchemaUtil.getData(ctx2, "type", name, "derived");

            if(!sl1Mod.get(1).equals(sl1Ori.get(1)))
            	SchemaUtil.settingDataLine(sbResult, "description", (String) sl1Ori.get(1), 12);
            if(!sDerived1.equals(sDerived2))
            	SchemaUtil.settingDataLine(sbResult, "derived", sDerived1, 12);
            if(!sl1Mod.get(0).equals(sl1Ori.get(0)))
            	SchemaUtil.settingDataLine(sbResult, "abstract", (String) sl1Ori.get(0), 12);
            if(!sl1Mod.get(2).equals(sl1Ori.get(2)))
            	SchemaUtil.settingDataLine(sbResult, "hidden", (String) sl1Ori.get(2), 12);
            
            String s3Ori 		= SchemaUtil.getData(ctx1, "type", name, "immediateattribute");
            String s3Mod 		= SchemaUtil.getData(ctx2, "type", name, "immediateattribute");
            StringList sl3Ori 	= FrameworkUtil.split(s3Ori, SchemaConstants.SELECT_SEPERATOR);
            StringList sl3Mod 	= FrameworkUtil.split(s3Mod, SchemaConstants.SELECT_SEPERATOR);
            sl3Ori.sort();
            sl3Mod.sort();

            for (Iterator itr1 = sl3Ori.iterator(); itr1.hasNext();) {
                String s4		= (String) itr1.next();
                if(!sl3Mod.contains(s4)) {
                	sbResult.append(_TAB).append("add attribute").append(_TAB).append(" '").append(s4).append("'").append(SchemaConstants.RECORDSEP);
                } else {
                	sl3Mod.remove(s4);
                }
            }
            for (Iterator itr1 = sl3Mod.iterator(); itr1.hasNext();) {
            	String s4		= (String) itr1.next();
        		sbResult.append(_TAB).append("remove attribute").append(_TAB).append(" '").append(s4).append("'").append(SchemaConstants.RECORDSEP);
            }

            
            /******************** Get Trigger ********************/
            String sTriggerOri 	= SchemaUtil.getData(ctx1, "type", name, "immediatetrigger");
            String sTriggerMod 	= SchemaUtil.getData(ctx2, "type", name, "immediatetrigger");

            Map mTriggerModMap	= new HashMap();	// Mod Check Map
            if( sTriggerMod != null && !"".equals(sTriggerMod) )
            {
            	StringList slTriggerMod 	= FrameworkUtil.split(sTriggerMod, SchemaConstants.SELECT_SEPERATOR);
            	for (Iterator itrTrigger = slTriggerMod.iterator(); itrTrigger.hasNext();)
                {
                    String sTr 				= (String) itrTrigger.next();
                    String[] strArrTrg 		= sTr.split(":");
 
                    String strTriggerType 	= strArrTrg[0];
                    String strTriggerName 	= StringUtils.substringBetween(strArrTrg[1], "(", ")");
                    mTriggerModMap.put(strTriggerType, strTriggerName);
                }
            }
            
            if( sTriggerOri != null && !"".equals(sTriggerOri) )
            {
                StringList slTriggerOri		= FrameworkUtil.split(sTriggerOri, SchemaConstants.SELECT_SEPERATOR);
                for (Iterator itrTrigger = slTriggerOri.iterator(); itrTrigger.hasNext();)
                {
                    String sTr 				= (String) itrTrigger.next();
                    String[] strArrTrg 		= sTr.split(":");
 
                    String strTriggerType 	= strArrTrg[0];
                    String strTriggerName 	= StringUtils.substringBetween(strArrTrg[1], "(", ")");

                    if((!mTriggerModMap.containsKey(strTriggerType)) || 
                		(mTriggerModMap.containsKey(strTriggerType) && !mTriggerModMap.get(strTriggerType).equals(strTriggerName)))
                    {
                    	sbResult.append(_TAB).append("add trigger ");
                    	if( strTriggerType.endsWith("Action") ){
                    		sbResult.append(StringUtils.substringBefore(strTriggerType, "Action")).append(" action");
                    	} else if( strTriggerType.endsWith("Override") ) {
                    		sbResult.append(StringUtils.substringBefore(strTriggerType, "Override")).append(" override");
                    	} else if( strTriggerType.endsWith("Check") ) {
                    		sbResult.append(StringUtils.substringBefore(strTriggerType, "Check")).append(" check");
                    	}
                    	sbResult.append(" emxTriggerManager input '").append(strTriggerName).append("'").append(SchemaConstants.RECORDSEP);
                    }
                    
                    if(mTriggerModMap.containsKey(strTriggerType)) {
                    	mTriggerModMap.remove(strTriggerType);
                    }
                }
            }
            
            
            if( null != mTriggerModMap && mTriggerModMap.size() > 0 )
            {
                for (Iterator itrTrigger = mTriggerModMap.keySet().iterator(); itrTrigger.hasNext();)
                {
                    String strTriggerType 	= (String) itrTrigger.next();

                    sbResult.append(_TAB).append("remove trigger ");
                	if( strTriggerType.endsWith("Action") ){
                		sbResult.append(StringUtils.substringBefore(strTriggerType, "Action")).append(" action").append(SchemaConstants.RECORDSEP);
                	} else if( strTriggerType.endsWith("Override") ) {
                		sbResult.append(StringUtils.substringBefore(strTriggerType, "Override")).append(" override").append(SchemaConstants.RECORDSEP);
                	} else if( strTriggerType.endsWith("Check") ) {
                		sbResult.append(StringUtils.substringBefore(strTriggerType, "Check")).append(" check").append(SchemaConstants.RECORDSEP);
                	}
                }
            }
            
            sbResult.append(SchemaConstants.SEMI_COLON).append(SchemaConstants.RECORDSEP);
            sbResult.append("#add property type_").append(StringUtils.deleteWhitespace(name)).append(" on program eServiceSchemaVariableMapping.tcl to type '").append(name).append("';").append(SchemaConstants.RECORDSEP);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
    }
}
