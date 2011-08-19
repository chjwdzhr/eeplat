package com.exedosoft.plat.action.customize.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.exedosoft.plat.action.DOAbstractAction;
import com.exedosoft.plat.bo.BOInstance;
import com.exedosoft.plat.bo.DOBO;
import com.exedosoft.plat.bo.DODataSource;
import com.exedosoft.plat.gene.jquery.ATableForwarderJquery;
import com.exedosoft.plat.util.DOGlobals;

public class DOGeneConfigTable extends DOAbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(DOGeneConfigTable.class);

	/**
	 * Save 的情况，所以Parameter 取值时不考虑auto_condition（查询） 的情况
	 */

	public String excute() {

		DODataSource dss = DOGlobals.getInstance().getSessoinContext().getTenancyValues().getDataDDS();
		String dataSourceUid = dss.getObjUid();

		String[] tables = this.actionForm.getValueArray("checkinstance");
		String bpUid = this.actionForm.getValue("bpUid");
		String[] keyCols = this.actionForm.getValueArray("keyCol");
		String[] valueCols = this.actionForm.getValueArray("valueCol");
		String[] hiddenTables = this.actionForm
				.getValueArray("checkinstance_hidden");

		List<BOInstance> allCheckInstances = new ArrayList<BOInstance>();
		StringBuilder echo = new StringBuilder();
		for (int i = 0; i < hiddenTables.length; i++) {
			String aHiddenTable = hiddenTables[i];
			for (int check = 0; check < tables.length; check++) {
				if (aHiddenTable.equals(tables[check])) {
					BOInstance aIns = new BOInstance();
					if (i < keyCols.length) {
						if (keyCols[i] != null && !keyCols[i].trim().equals("")) {
							aIns.putValue("tableName", aHiddenTable);
							aIns.putValue("keycol", keyCols[i]);
							aIns.putValue("valuecol", valueCols[i]);
							allCheckInstances.add(aIns);
						} else {
							echo.append("表::" + aHiddenTable
									+ "没有定义主键，无法进行初始化!");
						}
					} else {
						/////////////造成这个问题，有可能是 key 值没有选择。要注意一下

						this.setEchoValue("浏览器内部错误，请重试或选用firefox!");
						return NO_FORWARD;
					}
				}
			}
		}

		if (allCheckInstances.size() == 0) {
			this.setEchoValue("没有选中数据表或没有定义主键!");
			return NO_FORWARD;
		}

		log.info("echo::" + echo);

		// ////////////////清楚缓存
		try {

			BOInstance biTenancy = (BOInstance) DOGlobals.getInstance()
			.getSessoinContext().getTenancyValues().getTenant();

			for (java.util.Iterator<BOInstance> it = allCheckInstances
					.iterator(); it.hasNext();) {
				String tenancyId = "";
				BOInstance bi = it.next();
				String aTable = bi.getValue("tableName");
				if(aTable.startsWith("tenancy;")){
					aTable = aTable.substring(8);
					tenancyId = biTenancy.getValue("name");
				}
				String keycol = bi.getValue("keycol");
				String valuecol = bi.getValue("valuecol");

				ATableForwarderJquery af = new ATableForwarderJquery(aTable,
						keycol, valuecol, dataSourceUid, bpUid, tenancyId);
				af.forwardAll();
			}

		} catch (Exception ex1) {
			ex1.printStackTrace();
			this.setEchoValue(ex1.getMessage());
			return NO_FORWARD;

		}
		this.setEchoValue("初始化成功!");
		return DEFAULT_FORWARD;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}