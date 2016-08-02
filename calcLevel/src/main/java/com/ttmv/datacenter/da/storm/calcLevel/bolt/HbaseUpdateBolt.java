package com.ttmv.datacenter.da.storm.calcLevel.bolt;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.ttmv.datacenter.da.storm.calcLevel.service.LevelHbaseDataService;
import com.ttmv.datacenter.da.storm.common.util.JsonUtil;
import com.ttmv.datacenter.da.storm.common.util.Number2StringUtil;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

@SuppressWarnings({ "serial", "rawtypes" })
public class HbaseUpdateBolt extends BaseRichBolt {

	private static Logger logger = Logger.getLogger(HbaseUpdateBolt.class);
	private OutputCollector collector;

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		List<Object> dataList = tuple.select(new Fields("datasList"));
		for (Object object : dataList) {
			try {
				logger.info("hbase Bolt获取的数据是:" + object.toString());
				JSONArray jsonArray = new JSONArray(object.toString());
				for (int i = 0; i < jsonArray.length(); i++) {
					Map dataMap = (Map) JsonUtil.getObjectFromJson(jsonArray.get(i).toString(), Map.class);
					Set keys = dataMap.keySet();
					for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
						String key = (String) iterator.next();
						logger.info("[[二级bolt]]HbaeBolt 获取一级bolt的key值:" + key);
						//BigDecimal value =new BigDecimal(dataMap.get(key).toString()) ;
						logger.info("[[二级bolt]]HbaeBolt 获取一级bolt的value值:" + dataMap.get(key).toString());
						// 将数据写入hbase
						try {
							LevelHbaseDataService.calHbaseData(key, Number2StringUtil.numberToString(dataMap.get(key).toString()));
						} catch (Exception e) {
							logger.error("[[二级bolt]]Hbase修改积分和月积分操作失败，失败的原因是:" , e);
						}
						
						try {
							// 根据key查询hbase里面的数据，计算对应的level,并将计算的数据推送给dams,如果库里存储的经验级别和当前计算的级别不一致，则向IM推送消息
							LevelHbaseDataService.calCurrentLevel(key);
						} catch (Exception e) {
							logger.error("根据key查询hbase里面的数据，计算对应的level,并将计算的数据推送给dams 失败，",e);
						}
					}
				}
			} catch (Exception e1) {
				logger.error("[[二级bolt]]hbase获取的数据json解析失败！",e1);
			}
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub

	}

}
