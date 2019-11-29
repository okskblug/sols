package kr.co.atis.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.swing.SwingWorker;

import kr.co.atis.main.BusinessViewMain;
import matrix.db.Context;
import matrix.util.StringList;

/**
 * Only Compare Mode (Background)
 * @author ihjang
 *
 */
public class SchemaAutoCompare {
	private static Context context1	= null;
	private static Context context2	= null;
	private static boolean isChange	= false;
	/**
	 * Auto Compare Thread
	 * @param ctx1	- Default Context
	 * @param ctx2	- Compare Context
	 */
	public void settingAutoCompareThread(Context ctx1, Context ctx2) {
		context1	= ctx1;
		context2	= ctx2;
		
		// All code inside SwingWorker runs on a seperate thread
		SwingWorker worker = new SwingWorker() {
			@Override
			public Object doInBackground() {
				while (true) {
					System.err.println("Run Background");
					int i = 1;
					try {
						long a = System.currentTimeMillis();
						if (BusinessViewMain.button3.isOnOff()) {
							BusinessViewMain.sModifiedTmp = new SimpleDateFormat(SchemaConstants.FORMAT_MODIFIED, Locale.US).format(new Date());
							BusinessViewMain.progressLoad.setIndeterminate(true);
							SchemaLogs.writeLogFile("INFO  ", "Run Background "+i+" Cycle");	// [LOG]
							settingAutoCompareList(context1, context2, BusinessViewMain.isFirst);
							SchemaLogs.writeLogFile("INFO  ", "End Background "+i+" Cycle");	// [LOG]
							i++;
							
							if(isChange)
							{
								BusinessViewMain.mDefaultListMap.clear();
								BusinessViewMain.isFirst	= true;
								isChange	= false;
								continue;
							}
							
							// Thread.sleep(1000 * 5); // 2 min
							BusinessViewMain.progressLoad.setIndeterminate(false);
							BusinessViewMain.progressLoad.setValue(100);
							BusinessViewMain.isFirst = false;
							BusinessViewMain.sModified = BusinessViewMain.sModifiedTmp;
						}
						long b = System.currentTimeMillis();
						System.err.println(i + " Cycle >>> " + (b - a) + " ms");

						Thread.sleep(1000 * 60 * 10); // 10 min

					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}

			@Override
			public void done() {
			}
		};

		// Call the SwingWorker from within the Swing thread
		worker.execute();
	}

	/**
	 * Change Context
	 * @param ctx1
	 * @param ctx2
	 */
	public static void changeContextAutoCompare(Context ctx1, Context ctx2) {
		context1	= ctx1;
		context2	= ctx2;
		isChange	= true;
	}
	
	
	
	/**
	 * get Compare List
	 * @param ctx1	- Default Context
	 * @param ctx2	- Compare Context
	 * @param isFirst	- is First (true or false), true : modified option remove
	 */
	private static void settingAutoCompareList(Context ctx1, Context ctx2, boolean isFirst) throws Exception {
		StringList slTypeList	= new StringList();
		slTypeList.addAll(SchemaConstants.getMQLTypeList());

		try {
			int iListSize	= slTypeList.size();
			for(int j = 0; j < iListSize; j++) 
			{
				long a = System.currentTimeMillis();
				
				String sSearch			= (String) slTypeList.get(j);
				
				/*************************** Create Query ***************************/
				if(!isChange) {
					SchemaCompareMain.compareListCommon(ctx1, ctx2, sSearch, "", isFirst, true, true);
				}
				long b = System.currentTimeMillis();
				System.err.println("sSearch : " + sSearch + " >>> " + (b-a) + " ms");
				SchemaLogs.writeLogFile("INFO  ", "Background ["+sSearch+"] complete");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}


