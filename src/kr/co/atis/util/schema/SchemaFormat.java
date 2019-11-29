package kr.co.atis.util.schema;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.util.FrameworkUtil;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class SchemaFormat {
	private static final String _TAB 		= SchemaConstants._TAB;
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	
	/**
     * Add Attribute MQL (Default Mode)
     * @param context
     * @param name
     * @return
     * @throws Exception
     */
    public static String getFormatMQL(Context context, String name) throws Exception {
        StringBuilder sbResult = new StringBuilder();
        try {
        	sbResult.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.FORMAT, name, true));
//            description VALUE |
//            icon FILENAME     |
//            mime MIMETYPE     |
//            suffix VALUE      |
//            creator VALUE     |
//            type VALUE        |
//            version VALUE     |
//            [!|not]hidden     |
//            property NAME [value STRING]            |
//            property NAME to ADMIN [value STRING]   |      

        	String sResult 		= SchemaUtil.getData(context, "format", name, "hidden version filesuffix mime");
        	String sDesc 		= SchemaUtil.getData(context, "format", name, "description");
            StringList slResult	= FrameworkUtil.split(sResult, SchemaConstants.SELECT_SEPERATOR);

            SchemaUtil.settingDataLine(sbResult, "version", 	(String) slResult.get(1), 	12);
            SchemaUtil.settingDataLine(sbResult, "description", sDesc, 						12);
            SchemaUtil.settingDataLine(sbResult, "suffix", 		(String) slResult.get(2), 	12);
            SchemaUtil.settingDataLine(sbResult, "mime", 		(String) slResult.get(3), 	12);
            boolean bHidden = BooleanUtils.toBoolean((String)slResult.get(0));
            SchemaUtil.settingDataLine(sbResult, bHidden?"hidden":"nothidden");
            
//            name version filesuffix view edit print mime hidden
            sbResult.append(SchemaUtil.getPropertyForBusiness(context, SchemaConstants.FORMAT, name));	// Get Property
            sbResult.append(SchemaConstants.SEMI_COLON).append(SchemaConstants.RECORDSEP).append(SchemaConstants.RECORDSEP);
            sbResult.append("add property format_").append(StringUtils.deleteWhitespace(name)).append(" on program 'eServiceSchemaVariableMapping.tcl' to format '").append(name).append("';").append(SchemaConstants.RECORDSEP);
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
    public static String getFormatModMQL(Context ctx1, Context ctx2, String name) throws Exception {
        StringBuilder sbResult = new StringBuilder();
        try {
        	sbResult.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.FORMAT, name, false));
        	sbResult.append("Required DEV");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return sbResult.toString();
    }
}
