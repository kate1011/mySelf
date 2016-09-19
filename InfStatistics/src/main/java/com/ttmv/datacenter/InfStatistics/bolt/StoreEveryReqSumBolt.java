package com.ttmv.datacenter.InfStatistics.bolt;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ttmv.datacenter.InfStatistics.service.StoreSumDataService;
import com.ttmv.datacenter.InfStatistics.util.Constants;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
@SuppressWarnings({ "rawtypes", "serial" })
public class StoreEveryReqSumBolt extends BaseRichBolt{

	private static Logger logger = Logger.getLogger(StoreEveryReqSumBolt.class);
	private OutputCollector collector;
	
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		try {
			String interfJson = tuple.getStringByField(Constants.FILTER_FIELD_NAME);
			long time1 = System.currentTimeMillis();
			StoreSumDataService.addEveryReqSum(interfJson);
			long time2 = System.currentTimeMillis();
			long period = time2 - time1;
			logger.info("StoreEveryReqSumBolt 完成一次业务花费的时间:" + period);
			collector.emit(new Values(interfJson));
		} catch (Exception e) {
			logger.error("每个接口请求总量统计存储hbase失败！！", e);
			//collector.fail(tuple);
		} 
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(Constants.SEND_EVERY_REQSUM_BOLT));
		
	}

}
