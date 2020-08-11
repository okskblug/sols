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

public class SchemaPolicy {
	private static final String _TAB = SchemaConstants._TAB;
	private static final String RECORDSEP = SchemaConstants.RECORDSEP;

	public static String getPolicyMQL(Context context, String name) throws Exception {
        StringBuilder sb	= new StringBuilder();
        try {
            String s1  		= SchemaUtil.getData(context, "policy", name, "description revision hidden store defaultformat");
            StringList sl1 	= FrameworkUtil.split(s1, SchemaConstants.SELECT_SEPERATOR);
            int iDesc       = 0;
            int iRev        = 1;
            int iHidden 	= 2;
            int iStore      = 3;
            int iDefault	= 4;
 
            sb.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.POLICY, name, true));
            SchemaUtil.settingDataLine(sb, "description", 	(String) sl1.get(iDesc), 	16);
            SchemaUtil.settingDataLine(sb, "sequence", 		(String) sl1.get(iRev), 	16);
            SchemaUtil.settingDataLine(sb, "store", 		(String) (sl1.size() > iStore ? sl1.get(iStore) : ""), 16);
            SchemaUtil.settingDataLine(sb, new StringBuffer(sl1.size() > iHidden ? (BooleanUtils.toBoolean((String)sl1.get(iHidden))?"":"not") : "").append("hidden").toString());
            
            String s2 		= SchemaUtil.getData(context, "policy", name, "type");
            StringList sl2 	= FrameworkUtil.split(s2, SchemaConstants.SELECT_SEPERATOR);
            SchemaUtil.settingDataLine(sb, "type", StringUtils.join(sl2, "\",\""), 	16);
            
            String sFormat 		= SchemaUtil.getData(context, "policy", name, "format");
            StringList slFormat	= FrameworkUtil.split(sFormat, SchemaConstants.SELECT_SEPERATOR);
            slFormat.sort();
            if(!sFormat.trim().equals("")) 
            	SchemaUtil.settingDataLine(sb, "format", StringUtils.join(slFormat, "\",\""), 	16);
        	if(iDefault < sl1.size() && !"".equals(sl1.get(iDefault))) {
    			SchemaUtil.settingDataLine(sb, "defaultFormat", (String) sl1.get(iDefault), 	16);
        	}

        	String s3		= SchemaUtil.getData(context, "policy", name, "allstate state");
            StringList sl3 	= FrameworkUtil.split(s3, SchemaConstants.SELECT_SEPERATOR);

			// allstate
			if(((String) sl3.get(0)).equalsIgnoreCase("true")) {
				sl3.remove(0);
				
				SchemaUtil.settingDataLine(sb, "'allstate'");
				
				String s8		= SchemaUtil.getData(context, "policy", name, "allstate.owneraccess");
				SchemaUtil.settingDataLine(sb.append(_TAB), "owner", s8, 	16);
				
				String s		= SchemaUtil.getData(context, "policy", name, "allstate.publicaccess");
				SchemaUtil.settingDataLine(sb.append(_TAB), "public", s, 	16);
				
				String s10	= MqlUtil.mqlCommand(context, new StringBuilder("print policy '").append(name).append("' select allstate.user.login").toString());
				String s11	= MqlUtil.mqlCommand(context, new StringBuilder("print policy '").append(name).append("' select allstate.user").toString());
				String s12	= MqlUtil.mqlCommand(context, new StringBuilder("print policy '").append(name).append("' select allstate.user.access").toString());
				
				StringList sl4 	= FrameworkUtil.split(s10, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user
                StringList sl5 	= FrameworkUtil.split(s11, "\n");	// 0 line ~ user
				StringList sl6 	= FrameworkUtil.split(s12, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user

				if(sl5.size() > 0 && !((String) sl5.get(0)).trim().startsWith("policy")) {	// exclude public, owner
					for(int i = 0; i < sl4.size(); i++)
					{
						String str2	= StringUtils.trim((String) sl4.get(i + 2));
						String str3	= StringUtils.trim((String) sl5.get(i));
						String str4	= StringUtils.trim((String) sl6.get(i + 2));
						
						if(StringUtils.isNotBlank(str2) && !"null".equals(str2) && !str2.startsWith("policy"))
						{
							String sLogin		= StringUtils.split(str2, "-")[1].trim();
							String sUser	  	= StringUtils.split(str3, "-")[1].trim();
							StringList slUsers = FrameworkUtil.split(sUser, "|");
							String sAccess	= StringUtils.split(str4, "-")[1].trim();
							sb.append(_TAB).append(_TAB);
							if(sLogin.equalsIgnoreCase("true")) {
								sb.append("login user");
							} else {
								sb.append("user");
							}
							sb.append(" '").append(slUsers.get(0)).append("' ");
							if(slUsers.size() > 1) {
								sb.append(" key '").append(slUsers.get(1)).append("' ");
							}
							sb.append(" ").append(sAccess).append(RECORDSEP);
						}
					}
				}
			} else {
				sl3.remove(0);
			}
						
				
            for (Iterator itr3 = sl3.iterator(); itr3.hasNext();)
            {
                String str = (String) itr3.next();
                sb.append(_TAB).append("state '").append(str).append("'").append(RECORDSEP);
                
                String result	= SchemaUtil.getData(context, "policy", name, new StringBuffer("state[").append(str).append("].revisionable").append(" state[").append(str).append("].versionable").append(" state[").append(str).append("].autopromote").append(" state[").append(str).append("].checkouthistory").toString());
                StringList slResult	= FrameworkUtil.split(result, SchemaConstants.SELECT_SEPERATOR);
                
                SchemaUtil.settingDataLine(sb.append(_TAB), "revision", 		(String) slResult.get(0), 16);
                SchemaUtil.settingDataLine(sb.append(_TAB), "version", 			(String) slResult.get(0), 16);
                SchemaUtil.settingDataLine(sb.append(_TAB), "promote", 			(String) slResult.get(0), 16);
                SchemaUtil.settingDataLine(sb.append(_TAB), "checkouthistory", 	(String) slResult.get(0), 16);
                
                String s8 = SchemaUtil.getData(context, "policy", name, new StringBuffer("state[").append(str).append("].owneraccess").toString());
//                SchemaUtil.settingDataLine(sb.append(_TAB), "owner", s8, 16);
                sb.append(_TAB).append(_TAB).append(appendEmptySpace("owner", 16)).append(s8).append(SchemaConstants.RECORDSEP);

                String s9 = SchemaUtil.getData(context, "policy", name, new StringBuffer("state[").append(str).append("].publicaccess").toString());
//                SchemaUtil.settingDataLine(sb.append(_TAB), "public", s9, 16);
                sb.append(_TAB).append(_TAB).append(appendEmptySpace("public", 16)).append(s9).append(SchemaConstants.RECORDSEP);
                
                sb.append(SchemaUtil.getTriggerForBusiness(context, "policy", new StringBuilder("print policy '").append(name).append("' select state[").append(str).append("].trigger dump ").append(SchemaConstants.SELECT_SEPERATOR).toString()));
                
                String s10 		= MqlUtil.mqlCommand(context, new StringBuilder("print policy '").append(name).append("' select state[").append(str).append("].user.login").toString());
                String s11 		= MqlUtil.mqlCommand(context, new StringBuilder("print policy '").append(name).append("' select state[").append(str).append("].user").toString());                
                String s12 		= MqlUtil.mqlCommand(context, new StringBuilder("print policy '").append(name).append("' select state[").append(str).append("].user.access").toString());

                StringList sl4 	= FrameworkUtil.split(s10, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user
                StringList sl5 	= FrameworkUtil.split(s11, "\n");	// 0 line ~ user
				StringList sl6 	= FrameworkUtil.split(s12, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user

				if(sl5.size() > 0) {	// exclude public, owner
					for(int i = 1; i < sl5.size(); i++)
	                {
	                	String str2 = StringUtils.trim((String) sl4.get(i + 2));
	                	String str3 = StringUtils.trim((String) sl5.get(i));
	                	String str4 = StringUtils.trim((String) sl6.get(i + 2));                
	                
	                	if(StringUtils.isNotBlank(str2) && !"null".equals(str2) && !str2.startsWith("policy"))
	                	{
	                		String sLogin		= StringUtils.split(str2, "=")[1].trim();
	                		String sUser  		= StringUtils.split(str3, "=")[1].trim();
	                		StringList slUsers = FrameworkUtil.split(sUser, "|");
	                		String sAccess		= StringUtils.split(str4, "=")[1].trim();
	                		sb.append(_TAB).append(_TAB);
	                		if(sLogin.equalsIgnoreCase("true")) {
	                			sb.append("login user");
	                		} else {
	                			sb.append("user");
	                		}
	                		sb.append(" '").append(slUsers.get(0)).append("' ");
	                		if(slUsers.size() > 1) {
	                			sb.append(" key '").append(slUsers.get(1)).append("' ");
	                		}
	                		sb.append("  ").append(sAccess).append(RECORDSEP);
	                	}
	                }
				}
            }             

            sb.append(SchemaUtil.getPropertyForBusiness(context, SchemaConstants.POLICY, name));
            sb.append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
            sb.append("add property 'policy_").append(name).append("' on prog eServiceSchemaVariableMapping.tcl to policy '").append(name).append("';");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return sb.toString();
    }

	public static String getPolicyModMQL(Context ctx1, Context ctx2, String name) throws Exception {
		StringBuilder sb = new StringBuilder();
		try {
			String s1Ori = SchemaUtil.getData(ctx1, "policy", name, "description revision store hidden defaultformat");
			String s1Mod = SchemaUtil.getData(ctx2, "policy", name, "description revision store hidden defaultformat");
			StringList sl1Ori = FrameworkUtil.split(s1Ori, SchemaConstants.SELECT_SEPERATOR);
			StringList sl1Mod = FrameworkUtil.split(s1Mod, SchemaConstants.SELECT_SEPERATOR);
			int iDesc = 0;
			int iRev = 1;
			int iStore = 2;
			int iHidden = 3;
			int iDefault = 4;

			sb.append(SchemaConstants.NOTE_AREA).append(RECORDSEP);
			sb.append(SchemaUtil.createBusinessHeaderInfo(SchemaConstants.POLICY, name, false));

			if (!sl1Ori.get(iDesc).equals(sl1Mod.get(iDesc)))
				SchemaUtil.settingDataLine(sb, "description", (String) sl1Ori.get(iDesc), 16);
			if (!sl1Ori.get(iRev).equals(sl1Mod.get(iRev)))
				SchemaUtil.settingDataLine(sb, "sequence", (String) sl1Ori.get(iRev), 16);
			if (!sl1Ori.get(iStore).equals(sl1Mod.get(iStore)))
				SchemaUtil.settingDataLine(sb, "store", (String) sl1Ori.get(iStore), 16);
			if (!sl1Ori.get(iHidden).equals(sl1Mod.get(iHidden)))
				SchemaUtil.settingDataLine(sb, BooleanUtils.toBoolean((String) sl1Ori.get(iHidden)) ? "hidden" : "nothidden");
			if (!sl1Ori.get(iDefault).equals(sl1Mod.get(iDefault)))
				SchemaUtil.settingDataLine(sb, "defaultFormat", (String) sl1Ori.get(iDefault), 16);

			String s2Ori = SchemaUtil.getData(ctx1, "policy", name, "type");
			String s2Mod = SchemaUtil.getData(ctx2, "policy", name, "type");
			StringList sl2Ori = FrameworkUtil.split(s2Ori, SchemaConstants.SELECT_SEPERATOR);
			StringList sl2Mod = FrameworkUtil.split(s2Mod, SchemaConstants.SELECT_SEPERATOR);
			sl2Ori.sort();
			sl2Mod.sort();
			if (!sl2Ori.equals(sl2Mod)) {
				StringList slTempOri	= new StringList();
				StringList slTempMod	= new StringList();
				
				slTempOri.addAll((StringList) sl2Ori.clone());
				slTempMod.addAll((StringList) sl2Mod.clone());
				
				slTempOri.removeAll(slTempMod);
				slTempMod.removeAll(sl2Ori);
				
				if(slTempMod.size() > 0) {
					sb.append(_TAB).append("remove type").append(_TAB).append("'").append(StringUtils.join(slTempMod, "','")).append("'").append(RECORDSEP);
				}
				
				if(slTempOri.size() > 0) {
					sb.append(_TAB).append("add type").append(_TAB).append("'").append(StringUtils.join(slTempOri, "','")).append("'").append(RECORDSEP);
				}
			}

			String sFormatOri = SchemaUtil.getData(ctx1, "policy", name, "format");
			String sFormatMod = SchemaUtil.getData(ctx2, "policy", name, "format");
			StringList slFormatOri = FrameworkUtil.split(sFormatOri, SchemaConstants.SELECT_SEPERATOR);
			StringList slFormatMod = FrameworkUtil.split(sFormatMod, SchemaConstants.SELECT_SEPERATOR);
			slFormatOri.sort();
			slFormatMod.sort();
			if (!slFormatOri.equals(slFormatMod)) {
				sb.append(_TAB).append("add format").append(_TAB).append("'").append(StringUtils.join(slFormatOri, "','")).append("'").append(RECORDSEP);
			}

			// State
			String s3Ori = SchemaUtil.getData(ctx1, "policy", name, "state");
			String s3Mod = SchemaUtil.getData(ctx2, "policy", name, "state");
			StringList sl3Ori = FrameworkUtil.split(s3Ori, SchemaConstants.SELECT_SEPERATOR);
			StringList sl3Mod = FrameworkUtil.split(s3Mod, SchemaConstants.SELECT_SEPERATOR);
			int iStateSize = sl3Ori.size();
			String sPreState = "";
			for (int i = 0; i < iStateSize; i++) {
				String strOri = (String) sl3Ori.get(i);
				String strMod = (String) sl3Mod.get(i);

				// 1. 순서가 같은 경우 (내용 확인)
				// 1-1) 변경 내용 있으면 state STATE_NAME
				// 1-2) 변경 내용 없으면 skip
				if (strOri.equals(strMod)) {
					String sStateMQL = SchemaUtil.getPolicyStateModMQL(ctx1, ctx2, name, strOri);
					if (!"".equals(sStateMQL)) {
						SchemaUtil.settingDataLine(sb, "state", strOri, 16);
						sb.append(sStateMQL);
					}
				}
				// 2. 순서가 다른 경우
				else {
					// 2-1) 순서만 다른 경우 위치 변경 후 (내용 확인)
					// 2-2) 없는 경우 (추가)
					String sNextState = StringUtils.trimToEmpty((String) sl3Ori.get(i + 1));
					sb.append(_TAB).append("add state '").append(strOri).append("' before '").append(sNextState).append("'").append(RECORDSEP);
					if (!sl3Mod.contains(strOri)) {
						SchemaUtil.settingDataLine(sb, "state", strOri, 16);
		                String s4Ori 		= SchemaUtil.getData(ctx1, "policy", name, new StringBuffer("state[").append(strOri).append("].revisionable state[").append(strOri).append("].versionable state[").append(strOri).append("].autopromote state[").append(strOri).append("].checkouthistory").toString());
						StringList sl4Ori = FrameworkUtil.split(s4Ori, SchemaConstants.SELECT_SEPERATOR);
						SchemaUtil.settingDataLine(sb.append(_TAB), "state", strOri, 16);
						SchemaUtil.settingDataLine(sb.append(_TAB), "revision", (String) sl4Ori.get(0), 16);
						SchemaUtil.settingDataLine(sb.append(_TAB), "version", (String) sl4Ori.get(1), 16);
						SchemaUtil.settingDataLine(sb.append(_TAB), "promote", (String) sl4Ori.get(2), 16);
						SchemaUtil.settingDataLine(sb.append(_TAB), "checkouthistory", (String) sl4Ori.get(3), 16);
						String s8 = SchemaUtil.getData(ctx1, "policy", name, new StringBuffer("state[").append(strOri).append("].owneraccess").toString());
						SchemaUtil.settingDataLine(sb.append(_TAB), "owner", s8, 16);
						String s9 = SchemaUtil.getData(ctx1, "policy", name, new StringBuffer("state[").append(strOri).append("].publicaccess").toString());
						SchemaUtil.settingDataLine(sb.append(_TAB), "public", s9, 16);

//						String s10 = SchemaUtil.getData(ctx1, "policy", name, new StringBuffer("state[").append(strOri).append("].access").toString());
//						StringList sl4 = FrameworkUtil.split(s10, "\n");
//						for (Iterator itr4 = sl4.iterator(); itr4.hasNext();) {
//							String str2 = StringUtils.trim((String) itr4.next());
//							if (StringUtils.isNotBlank(str2) && !"null".equals(str2) && !str2.startsWith("policy")) {
//								String[] strArr1 = StringUtils.split(str2, "=");
//								String strUser = StringUtils.remove(strArr1[0].trim(), "state[" + strOri + "].access");
//								strUser = StringUtils.substringBetween(strUser, "[", "]");
//								sb.append(_TAB).append(_TAB).append("user").append("  '").append(strUser).append("' ").append(strArr1[1].trim()).append(RECORDSEP);
//							}
//						}
						
						
						String s10Ori 		= MqlUtil.mqlCommand(ctx1, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user.login").toString());
						String s10Mod 		= MqlUtil.mqlCommand(ctx2, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user.login").toString());
						String s11Ori 		= MqlUtil.mqlCommand(ctx1, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user").toString());                
		                String s11Mod 		= MqlUtil.mqlCommand(ctx2, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user").toString());                
		                String s12Ori 		= MqlUtil.mqlCommand(ctx1, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user.access").toString());
		                String s12Mod 		= MqlUtil.mqlCommand(ctx2, new StringBuilder("print policy '").append(name).append("' select state[").append(strOri).append("].user.access").toString());

		                StringList sl5Ori 	= FrameworkUtil.split(s10Ori, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user
		                StringList sl6Ori 	= FrameworkUtil.split(s11Ori, "\n");	// 0 line ~ user
						StringList sl7Ori 	= FrameworkUtil.split(s12Ori, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user
						StringList sl5Mod 	= FrameworkUtil.split(s10Mod, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user
						StringList sl6Mod 	= FrameworkUtil.split(s11Mod, "\n");	// 0 line ~ user
						StringList sl7Mod 	= FrameworkUtil.split(s12Mod, "\n");	// 0 line : public, 1 line : owner, 2 line ~ user

						StringList slAccOri	= new StringList();
						StringList slAccMod	= new StringList();
						if(sl6Ori.size() > 0) {	// exclude public, owner
							for(int j = 1; j < sl6Ori.size(); j++)
			                {
			                	String str2 = StringUtils.trim((String) sl5Ori.get(j + 2));
			                	String str3 = StringUtils.trim((String) sl6Ori.get(j));
			                	String str4 = StringUtils.trim((String) sl7Ori.get(j + 2));                
			                
			                	if(StringUtils.isNotBlank(str2) && !"null".equals(str2) && !str2.startsWith("policy"))
			                	{
			                		String sLogin		= StringUtils.split(str2, "=")[1].trim();
			                		String sUser  		= StringUtils.split(str3, "=")[1].trim();
			                		StringList slUsers = FrameworkUtil.split(sUser, "|");
			                		String sAccess		= StringUtils.split(str4, "=")[1].trim();
			                		StringBuffer sbTmp	= new StringBuffer(_TAB).append(_TAB);
			                		if(sLogin.equalsIgnoreCase("true")) {
			                			sbTmp.append("login user");
			                		} else {
			                			sbTmp.append("user");
			                		}
			                		sbTmp.append(" '").append(slUsers.get(0)).append("' ");
			                		if(slUsers.size() > 1) {
			                			sbTmp.append(" key '").append(slUsers.get(1)).append("' ");
			                		}
			                		sbTmp.append("  ").append(sAccess).append(RECORDSEP);
			                		slAccOri.add(sbTmp.toString());
			                	}
			                }
						}
						if(sl6Mod.size() > 0) {	// exclude public, owner
							for(int j = 1; j < sl6Mod.size(); j++)
							{
								String str2 = StringUtils.trim((String) sl5Mod.get(j + 2));
								String str3 = StringUtils.trim((String) sl6Mod.get(j));
								String str4 = StringUtils.trim((String) sl7Mod.get(j + 2));                
								
								if(StringUtils.isNotBlank(str2) && !"null".equals(str2) && !str2.startsWith("policy"))
								{
									String sLogin		= StringUtils.split(str2, "=")[1].trim();
									String sUser  		= StringUtils.split(str3, "=")[1].trim();
									StringList slUsers = FrameworkUtil.split(sUser, "|");
									String sAccess		= StringUtils.split(str4, "=")[1].trim();
									StringBuffer sbTmp	= new StringBuffer(_TAB).append(_TAB);
									if(sLogin.equalsIgnoreCase("true")) {
										sbTmp.append("login user");
									} else {
										sbTmp.append("user");
									}
									sbTmp.append(" '").append(slUsers.get(0)).append("' ");
									if(slUsers.size() > 1) {
										sbTmp.append(" key '").append(slUsers.get(1)).append("' ");
									}
									sbTmp.append("  ").append(sAccess).append(RECORDSEP);
									slAccMod.add(sbTmp.toString());
								}
							}
						}
						
						for(int j = 0; j < slAccOri.size(); j++) {
							String sOri	= (String) slAccOri.get(j);
							if(!slAccMod.contains(sOri)) {
								sb.append(sOri);
							}
						}
						for(int j = 0; j < slAccMod.size(); j++) {
							String sMod	= (String) slAccMod.get(j);
							if(!slAccOri.contains(sMod)) {
								sb.append("remove ").append(sMod);
							}
						}
					}
				}
			}

			String propertysOri 	= SchemaUtil.getData(ctx1, "policy", name, "property.name");
			String propertysMod 	= SchemaUtil.getData(ctx2, "policy", name, "property.name");
			String propertyValsOri 	= SchemaUtil.getData(ctx1, "policy", name, "property.value");
			String propertyValsMod 	= SchemaUtil.getData(ctx2, "policy", name, "property.value");
			StringList slpropertyOri = FrameworkUtil.split(propertysOri, SchemaConstants.SELECT_SEPERATOR);
			StringList slpropertyMod = FrameworkUtil.split(propertysMod, SchemaConstants.SELECT_SEPERATOR);
			StringList slpropertyValueOri = FrameworkUtil.split(propertyValsOri, SchemaConstants.SELECT_SEPERATOR);
			StringList slpropertyValueMod = FrameworkUtil.split(propertyValsMod, SchemaConstants.SELECT_SEPERATOR);

			int iPropOriSize = slpropertyOri.size();
			int iModIndex = 0;
			for (int i = 0; i < iPropOriSize; i++) {
				String sPropKey = (String) slpropertyOri.get(i);
				String sPropVal = (String) slpropertyValueOri.get(i);

				if (slpropertyMod.contains(sPropKey)) {
					iModIndex = slpropertyMod.indexOf(sPropKey);
					String sTempVal = (String) slpropertyValueMod.get(iModIndex);
					if (!sPropVal.equals(sTempVal)) {
						sPropKey = "\"" + sPropKey + "\"";
						if (sPropKey.length() < 20) {
							for (int j = sPropKey.length(); j < 20; j++) {
								sPropKey += " ";
							}
						}

						sb.append(_TAB).append("add property ").append(sPropKey).append(" value \"").append(sPropVal).append("\"").append(RECORDSEP);
					}

					slpropertyValueMod.remove(iModIndex);
					slpropertyMod.remove(iModIndex);
				} else {
					sPropKey = "\"" + sPropKey + "\"";
					if (sPropKey.length() < 20) {
						for (int j = sPropKey.length(); j < 20; j++) {
							sPropKey += " ";
						}
					}
					sb.append(_TAB).append("add property ").append(sPropKey).append(" value \"").append(sPropVal).append("\"").append(RECORDSEP);
				}
			}

			int iPropModSize = slpropertyMod.size();
			for (int i = 0; i < iPropModSize; i++) {
				String sPropKey = (String) slpropertyMod.get(i);
				sPropKey = "\"" + sPropKey + "\"";
				if (sPropKey.length() < 20) {
					for (int j = sPropKey.length(); j < 20; j++) {
						sPropKey += " ";
					}
				}
				sb.append(_TAB).append("remove property ").append(sPropKey).append(RECORDSEP);
			}

			sb.append(SchemaConstants.SEMI_COLON).append(RECORDSEP);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return sb.toString();
	}
	
	private static String appendEmptySpace(String sKey, int maxLth) {
		StringBuilder sbKey = new StringBuilder(sKey);
		int iPropertyLth = sbKey.length();
		if (iPropertyLth < maxLth) {
			for (int j = iPropertyLth; j < maxLth; j++) {
				sbKey.append(" ");
			}
		}

		return sbKey.toString();
	}
}
