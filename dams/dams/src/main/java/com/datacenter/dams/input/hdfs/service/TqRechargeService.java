package com.datacenter.dams.input.hdfs.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

import com.datacenter.dams.util.ConsumeSpendConstant;

public class TqRechargeService extends BaseService {

	@Override
	public Map<String, BigDecimal> analysisHdfsDatas(List<String> dataList) throws Exception {
		Map<String, BigDecimal> resultMap = new HashMap<String, BigDecimal>();
		for (String data : dataList) {
			JSONObject jsonData = new JSONObject(data);
			String num = jsonData.getString("number");
			if (num != null && !"null".equals(num)) {
				BigDecimal currentNum = new BigDecimal(num);
				BigDecimal dataNum = resultMap.get(ConsumeSpendConstant.TQRECHARGE);
				if (null == dataNum) {
					resultMap.put(ConsumeSpendConstant.TQRECHARGE, currentNum);
				} else {
					resultMap.put(ConsumeSpendConstant.TQRECHARGE, currentNum.add(dataNum));
				}
			}
		}
		return resultMap;
	}

}
