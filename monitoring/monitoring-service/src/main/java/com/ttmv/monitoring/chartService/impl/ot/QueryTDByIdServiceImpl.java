package com.ttmv.monitoring.chartService.impl.ot;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ttmv.monitoring.chartService.impl.QueryDataById;
import com.ttmv.monitoring.entity.TranscodingData;
import com.ttmv.monitoring.inter.ITranscodingDataInter;

/**
 * CPU动态折线图
 * @author wll
 *
 */
public class QueryTDByIdServiceImpl  extends QueryDataById {

	private static Logger logger = LogManager.getLogger(QueryTDByIdServiceImpl.class);
	
	private ITranscodingDataInter iTranscodingDataInter;

	@Override
	protected List getQuery(Object obj) throws Exception {
		if(obj == null )
			throw new Exception("创建查询对象时候，传入的参数为空");
		List list = new ArrayList();
		list.add(iTranscodingDataInter.queryTranscodingData((BigInteger)obj));
		return list;
	}

	@Override
	protected Set<String> getCheckDataWhiteSet() {
		return null;
	}

	@Override
	protected Object getDataType() {
		return new TranscodingData();
	}

	@Override
	protected void addAttributesToQuery(Map reqMap, Object data) {

	}

	public ITranscodingDataInter getiTranscodingDataInter() {
		return iTranscodingDataInter;
	}

	public void setiTranscodingDataInter(ITranscodingDataInter iTranscodingDataInter) {
		this.iTranscodingDataInter = iTranscodingDataInter;
	}
}
