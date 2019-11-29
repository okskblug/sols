package kr.co.atis.util.schema;

import java.util.Iterator;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class SchemaRole {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	
	/**
     * Add Attribute MQL (Default Mode)
     * @param context
     * @param name
     * @return
     * @throws Exception
     */
	
//	modify role NAME [MOD_ITEM {MOD_ITEM}];
//	  where MOD_ITEM is:
//	    | [ add ] assign person PERSON_NAME [group GROUP_NAME]                 |
//	    | child ROLE_NAME {,ROLE_NAME}                                         |
//	    | description VALUE                                                    |
//	    | name NEW_NAME                                                        |
//	    | maturity [none|public|protected|private]                             |
//	    | category [none|oem|goldpartner|partner|supplier|customer|contractor] |
//	    | parent ROLE_NAME                                                     |
//	    | remove assign | person PERSON_NAME [group GROUP_NAME] |              |
//	                    | all                                                  |
//	    | remove child | ROLE_NAME {,ROLE_NAME} |                              |
//	                   | all                                                   |
//	    | remove parent                                                        |
//	    | site SITE_NAME                                                       |
//	    | [!|not]hidden                                                        |
//	    | AS_KIND_OF                                                           |
//	    | add    | property NAME to ADMIN [value STRING]                       |
//	    |        | property NAME [value STRING]                                |
//	    | remove | property NAME to ADMIN                                      |
//	    |        | property NAME                                               |
//	    | property NAME to ADMIN [value STRING]                                |
//	    | property NAME [value STRING]                                         |
	
//add role tesSoftware
//	description 'Software manage role'
//	property application value Framework
//	property 'installed date' value '02/28/2018'
//	property 'installer' value ATIS
//	property 'original name' value tesSoftware
//	property 'version' value 'R2017x'
//;
//add property role_tesSoftware on program eServiceSchemaVariableMapping.tcl to role tesSoftware;
//
//
//# Assignment Person
//mod role tesSoftware add assign person 'admin_platform';

//	MQL<41>print role tesMBOMManager select child
//
//	role   tesMBOMManager
//	MQL<42>print role tesMBOMManager select maturity
//
//	role   tesMBOMManager
//	    maturity =
//	role   tesMBOMManager
//	    category =
//	MQL<44>print role tesMBOMManager select parent
//
//	role   tesMBOMManager
//	MQL<45>print role tesMBOMManager select site
//
//	role   tesMBOMManager
//	    site =
//	    
	
    public static String getRoleMQL(Context context, String name) throws Exception {
        StringBuilder sbResult = new StringBuilder();
        try {
        	sbResult.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.ROLE, name, true));
            // site hidden  maturity category
            // child parent 
            String sBases 		= SchemaUtil.getData(context, "role", name, "hidden site maturity category");
            String sDesc 		= SchemaUtil.getData(context, "role", name, "description");
            String sChild 		= SchemaUtil.getData(context, "role", name, "child");
            String sPerson		= SchemaUtil.getData(context, "role", name, "person");
            String sParent		= SchemaUtil.getData(context, "role", name, "parent");
            StringList base		= FrameworkUtil.split(sBases, SchemaConstants.SELECT_SEPERATOR);
            StringList slChild	= FrameworkUtil.split(sChild, SchemaConstants.SELECT_SEPERATOR);
            StringList slPerson	= FrameworkUtil.split(sPerson, SchemaConstants.SELECT_SEPERATOR);
            StringList slParent	= FrameworkUtil.split(sParent, SchemaConstants.SELECT_SEPERATOR);

            SchemaUtil.settingDataLine(sbResult, "description", 	sDesc, 				  12);
            if(!"".equals((String) base.get(1)))
            	SchemaUtil.settingDataLine(sbResult, "site", 		(String) base.get(1), 12);
            if(!"".equals((String) base.get(2)))
            	SchemaUtil.settingDataLine(sbResult, "maturity", 	(String) base.get(2), 12);
            if(!"".equals((String) base.get(3)))
            	SchemaUtil.settingDataLine(sbResult, "category", 	(String) base.get(3), 12);
            boolean bHidden = BooleanUtils.toBoolean((String)base.get(0));
            SchemaUtil.settingDataLine(sbResult, bHidden?"hidden":"nothidden");

            
            if( slPerson != null && slPerson.size() > 0 ) {
            	for (Iterator rangeItr = slPerson.iterator(); rangeItr.hasNext();) {
            		sbResult.append(_TAB).append("assign person '").append((String) rangeItr.next()).append("'").append(RECORDSEP);
            	}
            }
            if( slChild != null && slChild.size() > 0 ) {
            	sbResult.append(_TAB).append("child       ");
                for (Iterator rangeItr = slChild.iterator(); rangeItr.hasNext();) {
                	sbResult.append("'").append((String) rangeItr.next()).append("'");
                	if(rangeItr.hasNext())
                		sbResult.append(",");
                }
                sbResult.append(RECORDSEP);
            }
            if( slParent != null && slParent.size() > 0 ) {
            	sbResult.append(_TAB).append("parent      ");
            	for (Iterator rangeItr = slParent.iterator(); rangeItr.hasNext();) {
            		sbResult.append("'").append((String) rangeItr.next()).append("'");
            		if(rangeItr.hasNext())
            			sbResult.append(",");
            	}
            	sbResult.append(RECORDSEP);
            }
 
            sbResult.append(SchemaUtil.getPropertyForBusiness(context, SchemaConstants.ROLE, name));	// Get Property
            sbResult.append(SchemaConstants.SEMI_COLON).append(RECORDSEP).append(RECORDSEP);
            sbResult.append("add property role_").append(StringUtils.deleteWhitespace(name)).append(" on program 'eServiceSchemaVariableMapping.tcl' to role '").append(name).append("';").append(RECORDSEP);
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
    public static String getRoleModMQL(Context ctx1, Context ctx2, String name) throws Exception {
        StringBuilder sbResult = new StringBuilder();
        try {
        	sbResult.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.ROLE, name, false));
            sbResult.append("#del role '").append(name).append("';").append(RECORDSEP);
            sbResult.append("mod role '").append(name).append("'").append(RECORDSEP);

            String sBasesOri		= SchemaUtil.getData(ctx1, "role", name, "hidden site maturity category");
            String sBasesMod		= SchemaUtil.getData(ctx2, "role", name, "hidden site maturity category");
            String sDescOri			= SchemaUtil.getData(ctx1, "role", name, "description");
            String sDescMod			= SchemaUtil.getData(ctx2, "role", name, "description");
            String sChildOri		= SchemaUtil.getData(ctx1, "role", name, "child");
            String sChildMod		= SchemaUtil.getData(ctx2, "role", name, "child");
            String sPersonOri		= SchemaUtil.getData(ctx1, "role", name, "person");
            String sPersonMod		= SchemaUtil.getData(ctx2, "role", name, "person");
            String sParentOri		= MqlUtil.mqlCommand(ctx1, new StringBuilder("print role '").append(name).append("' select parent dump ','").toString());
            String sParentMod		= MqlUtil.mqlCommand(ctx2, new StringBuilder("print role '").append(name).append("' select parent dump ','").toString());
            StringList baseOri		= FrameworkUtil.split(sBasesOri, SchemaConstants.SELECT_SEPERATOR);
            StringList baseMod		= FrameworkUtil.split(sBasesMod, SchemaConstants.SELECT_SEPERATOR);
            StringList slChildOri	= FrameworkUtil.split(sChildOri, SchemaConstants.SELECT_SEPERATOR);
            StringList slChildMod	= FrameworkUtil.split(sChildMod, SchemaConstants.SELECT_SEPERATOR);
            StringList slPersonOri	= FrameworkUtil.split(sPersonOri, SchemaConstants.SELECT_SEPERATOR);
            StringList slPersonMod	= FrameworkUtil.split(sPersonMod, SchemaConstants.SELECT_SEPERATOR);
//            StringList slParentOri	= FrameworkUtil.split(sParentOri, SchemaConstants.SELECT_SEPERATOR);
//            StringList slParentMod	= FrameworkUtil.split(sParentMod, SchemaConstants.SELECT_SEPERATOR);

            if(!sDescOri.equals(sDescMod))
            	SchemaUtil.settingDataLine(sbResult, "description",	sDescOri, 				 12);
            if(!baseOri.get(1).equals(baseMod.get(1)))
            	SchemaUtil.settingDataLine(sbResult, "site", 		(String) baseOri.get(1), 12);
            if(!baseOri.get(2).equals(baseMod.get(2)))
            	SchemaUtil.settingDataLine(sbResult, "maturity", 	(String) baseOri.get(2), 12);
            if(!baseOri.get(3).equals(baseMod.get(3)))
            	SchemaUtil.settingDataLine(sbResult, "category", 	(String) baseOri.get(3), 12);
            boolean bHidden = BooleanUtils.toBoolean((String)baseOri.get(0));
            if(!baseMod.get(0).equals(baseOri.get(0)))
            	SchemaUtil.settingDataLine(sbResult, bHidden?"hidden":"nothidden");

            // add person
            if( slPersonOri != null && slPersonOri.size() > 0 ) {
            	for (Iterator rangeItr = slPersonOri.iterator(); rangeItr.hasNext();) {
            		String sPerson	= (String) rangeItr.next();
            		if(!slPersonMod.contains(sPerson))
            			sbResult.append(_TAB).append("add assign person '").append(sPerson).append("'").append(RECORDSEP);
            		
            		slPersonMod.remove(sPerson);
            	}
            }
            // remove person
            if( slPersonMod != null && slPersonMod.size() > 0 ) {
            	for (Iterator rangeItr = slPersonMod.iterator(); rangeItr.hasNext();) {
            		String sPerson	= (String) rangeItr.next();
        			sbResult.append(_TAB).append("remove assign person '").append(sPerson).append("'").append(RECORDSEP);
            	}
            }
            
            // add child
            if( slChildOri != null && slChildOri.size() > 0 ) {
            	sbResult.append(_TAB).append("child       ");
                for (Iterator rangeItr = slChildOri.iterator(); rangeItr.hasNext();) {
                	String sChild	= (String) rangeItr.next();
                	sbResult.append("'").append(sChild).append("'");
                	if(rangeItr.hasNext())
                		sbResult.append(",");
                	slChildMod.remove(sChild);
                }
                sbResult.append(RECORDSEP);
            }
            // remove child
            if( slChildMod != null && slChildMod.size() > 0 ) {
            	sbResult.append(_TAB).append("remove child ");
            	for (Iterator rangeItr = slChildMod.iterator(); rangeItr.hasNext();) {
            		String sChild	= (String) rangeItr.next();
            		sbResult.append("'").append(sChild).append("'");
            		if(rangeItr.hasNext())
            			sbResult.append(",");
            	}
            	sbResult.append(RECORDSEP);
            }
            
            // add parent
            if(!sParentOri.equals(sParentMod)) {
            	sbResult.append(_TAB).append("remove parent").append(RECORDSEP);
            	sbResult.append(_TAB).append("parent '").append(sParentOri).append("'").append(RECORDSEP);
            }
            sbResult.append(SchemaConstants.SEMI_COLON).append(RECORDSEP).append(RECORDSEP);
            sbResult.append("#add property role_").append(StringUtils.deleteWhitespace(name)).append(" on program 'eServiceSchemaVariableMapping.tcl' to role '").append(name).append("';").append(RECORDSEP);
        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
    }
}
