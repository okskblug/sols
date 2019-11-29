package kr.co.atis.util.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.util.FrameworkUtil;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class SchemaRelationship {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	
	public static String getRelationshipMQL(Context context, String name) throws Exception {
    	StringBuilder sbResult = new StringBuilder();
        try {
        	sbResult.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.RELATIONSHIP, name, true));
            
        	String sDerived = SchemaUtil.getData(context, "relationship", name, "derived");
            if(!"".equals(sDerived))
            	SchemaUtil.settingDataLine(sbResult, "derived", sDerived, 12);
            
            String s1		= SchemaUtil.getData(context, "relationship", name, "abstract dynamic compositional sparse");
            StringList sl1 	= FrameworkUtil.split(s1, SchemaConstants.SELECT_SEPERATOR);
            SchemaUtil.settingDataLine(sbResult, "abstract", 		(String) sl1.get(0), 12);
            SchemaUtil.settingDataLine(sbResult, "dynamic", 		(String) sl1.get(1), 12);
            SchemaUtil.settingDataLine(sbResult, "compositional", 	(String) sl1.get(2), 12);
            SchemaUtil.settingDataLine(sbResult, "sparse", 			(String) sl1.get(3), 12);
            
            String sDesc			= SchemaUtil.getData(context, "relationship", name, "description");
            SchemaUtil.settingDataLine(sbResult, "description", sDesc, 12);

        	String fromselect 		= "frommeaning  fromreviseaction fromcloneaction fromcardinality frompropagatemodify frompropagateconnection ";
        	String toselect 		= "tomeaning  toreviseaction tocloneaction tocardinality topropagatemodify topropagateconnection ";
        	String result			= SchemaUtil.getData(context, "relationship", name, new StringBuffer("hidden preventduplicates ").append(fromselect).append(" ").append(toselect).toString());
        	StringList resultList 	= FrameworkUtil.split( result , SchemaConstants.SELECT_SEPERATOR );
        	
        	String sAttr			= SchemaUtil.getData(context, "relationship", name, "immediateattribute");
        	StringList attrList 	= FrameworkUtil.split( sAttr , SchemaConstants.SELECT_SEPERATOR );
        	int iListSize			= attrList.size();
        	for(int i = 0; i < iListSize; i++)
        	{
        		SchemaUtil.settingDataLine(sbResult, "attribute", (String) attrList.get(i), 12);
        	}

        	sbResult.append(SchemaUtil.getTriggerForBusiness(context, "relationship", new StringBuilder("print 'relationship' '").append(name).append("' select immediatetrigger dump ").append(SchemaConstants.SELECT_SEPERATOR).toString()));

        	String sFromType	= SchemaUtil.getData(context, "relationship", name, "fromtype");
        	String sToType		= SchemaUtil.getData(context, "relationship", name, "totype");
        	
        	boolean bModify 	= true;
        	boolean bConnection = true;
        	boolean bHidden 	= BooleanUtils.toBoolean((String)resultList.get(0));
        	SchemaUtil.settingDataLine(sbResult, bHidden?"hidden":"nothidden");
        	
        	SchemaUtil.settingDataLine(sbResult, "from");
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), "type", sFromType.replaceAll("\\|", "','"), 12);
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), "meaning", 	(String) resultList.get(2), 12);
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), "cardinality",(String) resultList.get(5), 12);
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), "revision", 	(String) resultList.get(3), 12);
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), "clone", 		(String) resultList.get(4), 12);
        	bModify 			= BooleanUtils.toBoolean((String)resultList.get(6));
        	bConnection 		= BooleanUtils.toBoolean((String)resultList.get(7));
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), bModify?"propagatemodify":"notpropagatemodify");
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), bConnection?"propagateconnection":"notpropagateconnection");
        	
        	SchemaUtil.settingDataLine(sbResult, "to");
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), "type", sToType.replaceAll("\\|", "','"), 12);
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), "meaning", 	(String) resultList.get(8), 12);
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), "cardinality",(String) resultList.get(11), 12);
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), "revision", 	(String) resultList.get(9), 12);
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), "clone", 		(String) resultList.get(10), 12);
        	bModify 			= BooleanUtils.toBoolean((String)resultList.get(12));
        	bConnection 		= BooleanUtils.toBoolean((String)resultList.get(13));
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), bModify?"propagatemodify":"notpropagatemodify");
        	SchemaUtil.settingDataLine(sbResult.append(_TAB), bConnection?"propagateconnection":"notpropagateconnection");
        	
        	sbResult.append(SchemaUtil.getPropertyForBusiness(context, SchemaConstants.RELATIONSHIP, name));
            sbResult.append(";").append(RECORDSEP).append(RECORDSEP);
            sbResult.append("add property 'relationship_").append(StringUtils.deleteWhitespace(name)).append("' on program 'eServiceSchemaVariableMapping.tcl' to relationship '").append(name).append("';").append(RECORDSEP);
        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
    }
    
    
    
    public static String getRelationshipModMQL(Context ctx1, Context ctx2, String name) throws Exception {
    	StringBuilder sbResult 	= new StringBuilder();
    	StringBuilder sbFrom	= new StringBuilder();
    	StringBuilder sbTo		= new StringBuilder();
        try {
        	sbResult.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.RELATIONSHIP, name, false));
            
            String s1Ori 				= SchemaUtil.getData(ctx1, "relationship", name, "abstract sparse");
            String s1Mod 				= SchemaUtil.getData(ctx2, "relationship", name, "abstract sparse");
            StringList sl1Ori 			= FrameworkUtil.split(s1Ori, SchemaConstants.SELECT_SEPERATOR);
            StringList sl1Mod 			= FrameworkUtil.split(s1Mod, SchemaConstants.SELECT_SEPERATOR);
            
            for(int i = 0; i < sl1Ori.size(); i++) {
            	String sValOri			= (String) sl1Ori.get(i);
            	String sValMod			= (String) sl1Mod.get(i);
            	
            	if(!sValOri.equals(sValMod)) {
            		if(i == 0) {
            			SchemaUtil.settingDataLine(sbResult, "abstract", sValOri, 12);
            		} else if(i == 1) {
            			SchemaUtil.settingDataLine(sbResult, "sparse", sValOri, 12);
            		}
            	}
            }
            
        	String fromselect 			= "frommeaning  fromreviseaction fromcloneaction fromcardinality frompropagatemodify frompropagateconnection ";
        	String toselect 			= "tomeaning  toreviseaction tocloneaction tocardinality topropagatemodify topropagateconnection ";
        	String resultOri			= SchemaUtil.getData(ctx1, "relationship", name, new StringBuffer("hidden preventduplicates ").append(fromselect).append(" ").append(toselect).toString());
        	String resultMod			= SchemaUtil.getData(ctx2, "relationship", name, new StringBuffer("hidden preventduplicates ").append(fromselect).append(" ").append(toselect).toString());
        	StringList resultListOri	= FrameworkUtil.split( resultOri , SchemaConstants.SELECT_SEPERATOR );
        	StringList resultListMod	= FrameworkUtil.split( resultMod , SchemaConstants.SELECT_SEPERATOR );
        	
        	String sAttrOri				= SchemaUtil.getData(ctx1, "relationship", name, "immediateattribute");
        	String sAttrMod				= SchemaUtil.getData(ctx2, "relationship", name, "immediateattribute");
        	StringList attrListOri	 	= FrameworkUtil.split( sAttrOri , SchemaConstants.SELECT_SEPERATOR );
        	StringList attrListMod	 	= FrameworkUtil.split( sAttrMod , SchemaConstants.SELECT_SEPERATOR );
        	
        	int iSizeOri				= attrListOri.size();
        	for(int i = 0; i < iSizeOri; i++)
        	{
        		String sAttr			= (String) attrListOri.get(i);
        		if(!attrListMod.contains(sAttr)) {
        			sbResult.append(_TAB).append("add attribute '").append(sAttr).append("'").append(RECORDSEP);
        		} else {
        			attrListMod.remove(sAttr);
        		}
        	}
        	int iSizeMod				= attrListMod.size();
        	for(int i = 0; i < iSizeMod; i++)
        	{
        		String sAttr			= (String) attrListMod.get(i);
    			sbResult.append(_TAB).append("remove attribute '").append(sAttr).append("'").append(RECORDSEP);
        	}

        	/******************** Get Trigger ********************/ 
        	String sTriggerOri	= SchemaUtil.getData(ctx1, "relationship", name, "immediatetrigger");
        	String sTriggerMod	= SchemaUtil.getData(ctx2, "relationship", name, "immediatetrigger");

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
                    	sbResult.append(" emxTriggerManager input '").append(strTriggerName).append("'").append(RECORDSEP);
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
                		sbResult.append(StringUtils.substringBefore(strTriggerType, "Action")).append(" action").append(RECORDSEP);
                	} else if( strTriggerType.endsWith("Override") ) {
                		sbResult.append(StringUtils.substringBefore(strTriggerType, "Override")).append(" override").append(RECORDSEP);
                	} else if( strTriggerType.endsWith("Check") ) {
                		sbResult.append(StringUtils.substringBefore(strTriggerType, "Check")).append(" check").append(RECORDSEP);
                	}
                }
            }

            String sFromTypeOri	= SchemaUtil.getData(ctx1, "relationship", name, "fromtype");
            String sFromTypeMod	= SchemaUtil.getData(ctx2, "relationship", name, "fromtype");
            String sToTypeOri	= SchemaUtil.getData(ctx1, "relationship", name, "totype");
            String sToTypeMod	= SchemaUtil.getData(ctx2, "relationship", name, "totype");
        	StringList slFromTypeOri	= FrameworkUtil.split(sFromTypeOri, SchemaConstants.SELECT_SEPERATOR);
        	StringList slFromTypeMod	= FrameworkUtil.split(sFromTypeMod, SchemaConstants.SELECT_SEPERATOR);
        	StringList slToTypeOri	= FrameworkUtil.split(sToTypeOri, SchemaConstants.SELECT_SEPERATOR);
        	StringList slToTypeMod	= FrameworkUtil.split(sToTypeMod, SchemaConstants.SELECT_SEPERATOR);
        	slFromTypeOri.sort();
        	slFromTypeMod.sort();
        	slToTypeOri.sort();
        	slToTypeMod.sort();
        	
        	boolean bModifyOri 	= true;
        	boolean bModifyMod 	= true;
        	boolean bConnectOri = true;
        	boolean bConnectMod = true;
        	boolean bHiddenOri 	= BooleanUtils.toBoolean((String)resultListOri.get(0));
        	boolean bHiddenMod 	= BooleanUtils.toBoolean((String)resultListMod.get(0));
        	
        	if(bHiddenOri != bHiddenMod)
        		SchemaUtil.settingDataLine(sbResult, bHiddenOri?"hidden":"nothidden");
        	
        	/************************ From ************************/
        	if(!slFromTypeOri.equals(slFromTypeMod))
        	{
        		StringList slTempOri	= new StringList();
				StringList slTempMod	= new StringList();
				
				slTempOri.addAll((StringList) slFromTypeOri.clone());
				slTempMod.addAll((StringList) slFromTypeMod.clone());
				
				slTempOri.removeAll(slTempMod);
				slTempMod.removeAll(slFromTypeOri);
				
				if(slTempMod.size() > 0) {
					sbFrom.append(_TAB).append("remove type").append(_TAB).append("'").append(StringUtils.join(slTempMod, "','")).append("'").append(RECORDSEP);
				}
				
				if(slTempOri.size() > 0) {
					sbFrom.append(_TAB).append("add type").append(_TAB).append("'").append(StringUtils.join(slTempOri, "','")).append("'").append(RECORDSEP);
				}
        	}
        	
        	if(!resultListOri.get(2).equals(resultListMod.get(2)))
        		SchemaUtil.settingDataLine(sbFrom.append(_TAB), "meaning", 		(String) resultListOri.get(2), 12);
        	
        	if(!resultListOri.get(5).equals(resultListMod.get(5)))
        		SchemaUtil.settingDataLine(sbFrom.append(_TAB), "cardinality", 	(String) resultListOri.get(5), 12);
        	
        	if(!resultListOri.get(3).equals(resultListMod.get(3)))
        		SchemaUtil.settingDataLine(sbFrom.append(_TAB), "revision", 		(String) resultListOri.get(3), 12);
        	
        	if(!resultListOri.get(4).equals(resultListMod.get(4)))
        		SchemaUtil.settingDataLine(sbFrom.append(_TAB), "clone", 			(String) resultListOri.get(4), 12);
        	
        	bModifyOri 			= BooleanUtils.toBoolean((String)resultListOri.get(6));
        	bModifyMod 			= BooleanUtils.toBoolean((String)resultListMod.get(6));
        	bConnectOri 		= BooleanUtils.toBoolean((String)resultListOri.get(7));
        	bConnectMod 		= BooleanUtils.toBoolean((String)resultListMod.get(7));
        	if(bModifyOri != bModifyMod)
        		SchemaUtil.settingDataLine(sbFrom.append(_TAB), bModifyOri?"propagatemodify":"notpropagatemodify");
        	if(bConnectOri != bConnectMod)
        		SchemaUtil.settingDataLine(sbFrom.append(_TAB), bConnectOri?"propagateconnection":"notpropagateconnection");
        	
        	if(!sbFrom.toString().equals(""))
        	{
        		SchemaUtil.settingDataLine(sbResult, "from");
        		sbResult.append(sbFrom);
        	}
        	/************************ From ************************/
        	
        	
        	/************************  To  ************************/
        	if(!slToTypeOri.equals(slToTypeMod))
        	{
        		StringList slTempOri	= new StringList();
				StringList slTempMod	= new StringList();
				
				slTempOri.addAll((StringList) slToTypeOri.clone());
				slTempMod.addAll((StringList) slToTypeMod.clone());
				
				slTempOri.removeAll(slTempMod);
				slTempMod.removeAll(slToTypeOri);
				
				if(slTempMod.size() > 0) {
					sbTo.append(_TAB).append("remove type").append(_TAB).append("'").append(StringUtils.join(slTempMod, "','")).append("'").append(RECORDSEP);
				}
				
				if(slTempOri.size() > 0) {
					sbTo.append(_TAB).append("add type").append(_TAB).append("'").append(StringUtils.join(slTempOri, "','")).append("'").append(RECORDSEP);
				}
        	}
        	
        	if(!resultListOri.get(8).equals(resultListMod.get(8)))
        		SchemaUtil.settingDataLine(sbTo.append(_TAB), "meaning", 		(String) resultListOri.get(8), 12);
        	
        	if(!resultListOri.get(11).equals(resultListMod.get(11)))
        		SchemaUtil.settingDataLine(sbTo.append(_TAB), "cardinality", 		(String) resultListOri.get(11), 12);
        	
        	if(!resultListOri.get(9).equals(resultListMod.get(9)))
        		SchemaUtil.settingDataLine(sbTo.append(_TAB), "revision", 		(String) resultListOri.get(9), 12);
        	
        	if(!resultListOri.get(10).equals(resultListMod.get(10)))
        		SchemaUtil.settingDataLine(sbTo.append(_TAB), "clone", 		(String) resultListOri.get(10), 12);
        	
        	bModifyOri 			= BooleanUtils.toBoolean((String)resultListOri.get(12));
        	bModifyMod 			= BooleanUtils.toBoolean((String)resultListMod.get(12));
        	bConnectOri 		= BooleanUtils.toBoolean((String)resultListOri.get(13));
        	bConnectMod 		= BooleanUtils.toBoolean((String)resultListMod.get(13));
        	
        	
        	if(bModifyOri != bModifyMod)
        		SchemaUtil.settingDataLine(sbTo.append(_TAB), bModifyOri?"propagatemodify":"notpropagatemodify");
        	if(bConnectOri != bConnectMod)
        		SchemaUtil.settingDataLine(sbTo.append(_TAB), bConnectOri?"propagateconnection":"notpropagateconnection");
        	
        	if(!sbTo.toString().equals(""))
        	{
        		SchemaUtil.settingDataLine(sbResult, "to");
        		sbResult.append(sbTo);
        	}
        	/************************  To  ************************/
        	
            sbResult.append(SchemaConstants.SEMI_COLON);
        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
    }
}
