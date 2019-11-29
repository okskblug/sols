package kr.co.atis.util.schema;

import kr.co.atis.util.SchemaConstants;
import kr.co.atis.util.SchemaUtil;
import matrix.db.Context;

public class SchemaProgram {
	private static final String RECORDSEP 	= SchemaConstants.RECORDSEP;
	
	/**
     * Add Program MQL (Default Mode)
     * @param context
     * @param name
     * @return
     * @throws Exception
     */
    public static String getProgramMQL(Context context, String name) throws Exception {
    	return getProgramSchema(context, name, true);
    }
    
    
    
    /**
     * Mod Program MQL (Add Only)
     * @param ctx1	
     * @param ctx2
     * @param name
     * @return
     * @throws Exception
     */
    public static String getProgramModMQL(Context ctx1, Context ctx2, String name) throws Exception {
        return getProgramSchema(ctx1, name, false);
    }
    
    private static String getProgramSchema(Context context, String name, boolean isAdd) throws Exception {
    	StringBuilder sbResult = new StringBuilder();
    	try {
    		sbResult.append(SchemaUtil.createBusinessOnlyHeader(SchemaConstants.PROGRAM, name));
    		if(isAdd)
    			sbResult.append("#");
    		sbResult.append("del program '").append(name).append("';").append(RECORDSEP);
    		sbResult.append("add program '").append(name).append("' java;").append(RECORDSEP);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    	
    	return sbResult.toString();
    }
}
