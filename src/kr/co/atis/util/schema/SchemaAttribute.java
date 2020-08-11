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

public class SchemaAttribute {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	
	/**
     * Add Attribute MQL (Default Mode)
     * @param context
     * @param name
     * @return
     * @throws Exception
     */
    public static String getAttributeMQL(Context context, String name) throws Exception {
        StringBuilder sbResult = new StringBuilder();
        try {
        	sbResult.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.ATTRIBUTE, name, true));

        	String sBases 		= SchemaUtil.getData(context, SchemaConstants.ATTRIBUTE, name, "type multiline default hidden");
            String ranges 		= SchemaUtil.getData(context, SchemaConstants.ATTRIBUTE, name, "range");
            String sDesc  		= SchemaUtil.getData(context, SchemaConstants.ATTRIBUTE, name, "description");
            StringList base		= FrameworkUtil.split(sBases, SchemaConstants.SELECT_SEPERATOR);
            StringList slRange	= FrameworkUtil.split(ranges, SchemaConstants.SELECT_SEPERATOR);

            
            SchemaUtil.settingDataLine(sbResult, "description", sDesc, 					12);
            SchemaUtil.settingDataLine(sbResult, "type", 		(String) base.get(0), 	12);
            SchemaUtil.settingDataLine(sbResult, "default", 	(String) base.get(2), 	12);
            SchemaUtil.settingDataLine(sbResult, BooleanUtils.toBoolean((String)base.get(1))?"multiline":"notmultiline");
            boolean bHidden = BooleanUtils.toBoolean((String)base.get(3));
            SchemaUtil.settingDataLine(sbResult, bHidden?"hidden":"nothidden");
 
            if( slRange != null && slRange.size() > 0 )
            {
                for (Iterator rangeItr = slRange.iterator(); rangeItr.hasNext();) {
                    String strRange = (String) rangeItr.next();
                    strRange		= strRange.replace("= ", "");
                    sbResult.append(_TAB).append("range = \"").append(strRange).append("\"").append(SchemaConstants.RECORDSEP);
                }
            }
 
            sbResult.append(SchemaUtil.getPropertyForBusiness(context, SchemaConstants.ATTRIBUTE, name));	// Get Property
            sbResult.append(SchemaConstants.SEMI_COLON).append(SchemaConstants.RECORDSEP).append(SchemaConstants.RECORDSEP);
            sbResult.append("add property attribute_").append(StringUtils.deleteWhitespace(name)).append(" on program 'eServiceSchemaVariableMapping.tcl' to attribute '").append(name).append("';").append(SchemaConstants.RECORDSEP);

            // Parent Information
            sbResult.append(SchemaConstants.NOTE_AREA).append(SchemaConstants.RECORDSEP);
            sbResult.append("# Parent Information").append(SchemaConstants.RECORDSEP);
            String sTypeAttrs		= MqlUtil.mqlCommand(context, "list type * select name immediateattribute dump |");
            StringList slTypeAttr 	= FrameworkUtil.split(sTypeAttrs, "\n");
            if( slTypeAttr != null && slTypeAttr.size() > 0 )
            {
                int iListSize		= slTypeAttr.size();
                slTypeAttr.sort();
                for (int j = 0; j < iListSize; j++) {
                    String sAttr 		= (String) slTypeAttr.get(j);
                    StringList slAttrs 	= FrameworkUtil.split(sAttr, SchemaConstants.SELECT_SEPERATOR);
                    String sType		= (String) slAttrs.get(0);
            		slAttrs.remove(0);	// Index 0 : Type Name
                    if(slAttrs.contains(name))
                    {
                        sbResult.append("# mod type '").append(sType).append("' add attribute '").append(name).append("';").append(SchemaConstants.RECORDSEP);
                    }
                }
            }
            String sRelAttrs		= MqlUtil.mqlCommand(context, "list relationship * select name immediateattribute dump |");
            StringList slRelAttr 	= FrameworkUtil.split(sRelAttrs, "\n");
            if( slRelAttr != null && slRelAttr.size() > 0 )
            {
            	int iListSize		= slRelAttr.size();
            	slRelAttr.sort();
            	for (int j = 0; j < iListSize; j++) {
            		String sAttr 		= (String) slRelAttr.get(j);
            		StringList slAttrs 	= FrameworkUtil.split(sAttr, SchemaConstants.SELECT_SEPERATOR);
            		String sRel		= (String) slAttrs.get(0);
            		slAttrs.remove(0);	// Index 0 : Type Name
            		if(slAttrs.contains(name))
            		{
            			sbResult.append("# mod rel '").append(sRel).append("' add attribute '").append(name).append("';").append(SchemaConstants.RECORDSEP);
            		}
            	}
            }
            sbResult.append(SchemaConstants.NOTE_AREA).append(SchemaConstants.RECORDSEP);
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
    public static String getAttributeModMQL(Context ctx1, Context ctx2, String name) throws Exception {
        StringBuilder sbResult = new StringBuilder();
        try {
        	sbResult.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
        	sbResult.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.ATTRIBUTE, name, false));

            String sBasesOri 	= SchemaUtil.getData(ctx1, SchemaConstants.ATTRIBUTE, name, "type multiline default hidden");
            String sBasesMod 	= SchemaUtil.getData(ctx2, SchemaConstants.ATTRIBUTE, name, "type multiline default hidden");
            String rangesOri 	= SchemaUtil.getData(ctx1, SchemaConstants.ATTRIBUTE, name, "range");
            String rangesMod 	= SchemaUtil.getData(ctx2, SchemaConstants.ATTRIBUTE, name, "range");
            String sDescOri  	= SchemaUtil.getData(ctx1, SchemaConstants.ATTRIBUTE, name, "description");
            String sDescMod  	= SchemaUtil.getData(ctx2, SchemaConstants.ATTRIBUTE, name, "description");
            
            StringList baseOri	= FrameworkUtil.split(sBasesOri, SchemaConstants.SELECT_SEPERATOR);
            StringList baseMod	= FrameworkUtil.split(sBasesMod, SchemaConstants.SELECT_SEPERATOR);
            if(!sDescOri.equals(sDescMod))
            	SchemaUtil.settingDataLine(sbResult, "description", sDescOri, 					12);
            if(!baseOri.get(0).equals(baseMod.get(0)))
            	SchemaUtil.settingDataLine(sbResult, "type", 		(String) baseOri.get(0),	12);
            if(!baseOri.get(2).equals(baseMod.get(2)))
            	SchemaUtil.settingDataLine(sbResult, "default", 	(String) baseOri.get(2),	12);
            if(!baseOri.get(1).equals(baseMod.get(1)))
            	SchemaUtil.settingDataLine(sbResult, BooleanUtils.toBoolean((String)baseOri.get(1))?"multiline":"notmultiline");
            boolean bHiddenOri = BooleanUtils.toBoolean((String)baseOri.get(3));
            if(!baseMod.get(3).equals(baseOri.get(3)))
            	SchemaUtil.settingDataLine(sbResult, bHiddenOri?"hidden":"nothidden");

            StringList slRangeOri = FrameworkUtil.split(rangesOri, SchemaConstants.SELECT_SEPERATOR);
            StringList slRangeMod = FrameworkUtil.split(rangesMod, SchemaConstants.SELECT_SEPERATOR);
            if( slRangeOri != null && slRangeOri.size() > 0 )
            {
                for (Iterator rangeItr = slRangeOri.iterator(); rangeItr.hasNext();) 
                {
                    String strRange = (String) rangeItr.next();
                    if(!slRangeMod.contains(strRange)) {
	                    strRange	= strRange.replace("= ", "");
	                    sbResult.append(_TAB).append("add range = \"").append(strRange).append("\"").append(SchemaConstants.RECORDSEP);
                    } else {
                    	slRangeMod.remove(strRange);
                    }
                }
                
                for (Iterator rangeItr = slRangeMod.iterator(); rangeItr.hasNext();) 
                {
                	String strRange = (String) rangeItr.next();
            		strRange		= strRange.replace("= ", "");
            		sbResult.append(_TAB).append("remove range = \"").append(strRange).append("\"").append(SchemaConstants.RECORDSEP);
                }
            }
            sbResult.append(";").append(SchemaConstants.RECORDSEP).append(SchemaConstants.RECORDSEP);
            sbResult.append("#add property attribute_").append(StringUtils.deleteWhitespace(name)).append(" on program 'eServiceSchemaVariableMapping.tcl' to attribute '").append(name).append("';").append(SchemaConstants.RECORDSEP);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
    }
}
