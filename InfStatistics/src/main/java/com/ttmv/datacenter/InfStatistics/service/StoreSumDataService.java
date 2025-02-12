package com.ttmv.datacenter.InfStatistics.service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttmv.datacenter.InfStatistics.entity.InterfInfoEntity;
import com.ttmv.datacenter.InfStatistics.util.Constants;
import com.ttmv.datacenter.InfStatistics.util.DateUtil;
import com.ttmv.datacenter.InfStatistics.util.HbaseUtil;
import com.ttmv.datacenter.InfStatistics.util.JsonUtil;
import com.ttmv.datacenter.InfStatistics.util.RedisUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class StoreSumDataService {
	private static Logger logger = Logger.getLogger(StoreSumDataService.class);

	/**
	 * json解析
	 * 
	 * @param json
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static InterfInfoEntity getEntity(String json, String tableName) throws Exception {
		/*boolean isExit = HbaseUtil.isExitHbaseTable(tableName);
		if (!isExit) {
			HbaseUtil.createTable(tableName, Constants.HBASE_FAMILY);
		}*/
		InterfInfoEntity infoEntity = (InterfInfoEntity) JsonUtil.getObjectFromJson(json, InterfInfoEntity.class);
		return infoEntity;
	}

	/***
	 * hbase添加接口调用次数数据
	 * 
	 * @param json
	 * @param redisQueueName
	 * @throws Exception
	 */
	public static void addReqSum(String json) throws Exception {
		InterfInfoEntity infoEntity = getEntity(json, Constants.HBASE_INTERF_REQ_SUM);
		String rowkey = infoEntity.getServer_ip();
		HbaseUtil.automModifyValue(Constants.HBASE_INTERF_REQ_SUM, ipComponent(rowkey), Constants.HBASE_FAMILY,
				Constants.HBASE_INTERF_SUM_COLUMN, "1");

	}

	public static void sendReqSum(String json, String redisQueueName) throws Exception {
		InterfInfoEntity infoEntity = getEntity(json, Constants.HBASE_INTERF_REQ_SUM);
		String rowkey = infoEntity.getServer_ip();
		Map map = new HashMap();
		byte[] datas = HbaseUtil.getStormColumnData(Constants.HBASE_INTERF_REQ_SUM, ipComponent(rowkey), Constants.HBASE_FAMILY,
				Constants.HBASE_INTERF_SUM_COLUMN);
		BigInteger num = new BigInteger("0");
		if (null != datas && datas.length > 0) {
			num = new BigInteger(datas);
		}
		map.put("reqSum", num);
		map.put("host", rowkey);
		logger.info("接口请求总数统向redis发送的信息:" + JsonUtil.getObjectToJson(map));
		//pushDataToRedis(redisQueueName, ipComponent(rowkey), map);
	}

	/**
	 * hbase 添加接口调用成功失败数据
	 * 
	 * @param json
	 * @param redisQueueName
	 * @throws Exception
	 */
	public static void addReqStatus(String json) throws Exception {
		InterfInfoEntity infoEntity = getEntity(json, Constants.HBASE_INTERF_REQ_STATUS);
		String rowkey = infoEntity.getServer_ip();
		String res_code = infoEntity.getRes_code();
		if ("AAAAAAA".equals(res_code)) {
			HbaseUtil.automModifyValue(Constants.HBASE_INTERF_REQ_STATUS, ipComponent(rowkey), Constants.HBASE_FAMILY,
					Constants.HBASE_INTERF_STATUS_SUCCESS, "1");
		} else {
			HbaseUtil.automModifyValue(Constants.HBASE_INTERF_REQ_STATUS, ipComponent(rowkey), Constants.HBASE_FAMILY,
					Constants.HBASE_INTERF_STATUS_ERROR, "1");
		}
	}

	public static void sendReqStatus(String json, String redisQueueName) throws Exception {
		InterfInfoEntity infoEntity = getEntity(json, Constants.HBASE_INTERF_REQ_STATUS);
		String rowkey = infoEntity.getServer_ip();
		Map map = sucErrMap(ipComponent(rowkey));
		logger.info("接口请求成功总数、失败总数、比例统计向redis发送的信息:" + JsonUtil.getObjectToJson(map));
		//pushDataToRedis(redisQueueName, ipComponent(rowkey), map);
		map=null;

	}

	/***
	 * 计算接口调用成功失败数、成功失败比例
	 * 
	 * @param rowkey
	 * @return
	 * @throws Exception
	 */
	public static Map sucErrMap(String rowkey) throws Exception {
		byte[] success = HbaseUtil.getStormColumnData(Constants.HBASE_INTERF_REQ_STATUS, rowkey, Constants.HBASE_FAMILY,
				Constants.HBASE_INTERF_STATUS_SUCCESS);
		byte[] error = HbaseUtil.getStormColumnData(Constants.HBASE_INTERF_REQ_STATUS, rowkey, Constants.HBASE_FAMILY,
				Constants.HBASE_INTERF_STATUS_ERROR);
		BigInteger succNum = new BigInteger("0");
		BigInteger errNum = new BigInteger("0");

		if (null != success && success.length > 0) {
			succNum = new BigInteger(success);
		}
		if (null != error && error.length > 0) {
			errNum = new BigInteger(error);
		}
		Map map = new HashMap();
		map.put("host", rowkey);
		map.put("success", succNum);
		map.put("error", errNum);
		map.put("ratio", String.valueOf(succNum.doubleValue() / (succNum.add(errNum).doubleValue())));
		return map;
	}

	/**
	 * 每个接口请求总量统计
	 * 
	 * @param json
	 * @param redisQueueName
	 * @throws Exception
	 */
	public static void addEveryReqSum(String json) throws Exception {
		InterfInfoEntity infoEntity = getEntity(json, Constants.HBASE_INTERF_EVERY_REQ_SUM);
		String service_code = infoEntity.getService_code();
		String server_ip = infoEntity.getServer_ip();
		String rowkey = service_code + ipComponent(server_ip);
		HbaseUtil.automModifyValue(Constants.HBASE_INTERF_EVERY_REQ_SUM, rowkey, Constants.HBASE_FAMILY,
				Constants.HBASE_INTERF_SUM_COLUMN, "1");

	}

	public static void senEveryReqSum(String json, String redisQueueName) throws Exception {
		InterfInfoEntity infoEntity = getEntity(json, Constants.HBASE_INTERF_EVERY_REQ_SUM);
		String service_code = infoEntity.getService_code();
		String server_ip = infoEntity.getServer_ip();
		String rowkey = service_code + ipComponent(server_ip);
		byte[] datas = HbaseUtil.getStormColumnData(Constants.HBASE_INTERF_EVERY_REQ_SUM, rowkey, Constants.HBASE_FAMILY,
				Constants.HBASE_INTERF_SUM_COLUMN);
		Map map = new HashMap();
		BigInteger num = new BigInteger("0");
		if (null != datas && datas.length > 0) {
			num = new BigInteger(datas);
		}
		map.put(service_code, num);
		map.put("host", server_ip);
		logger.info("每个接口请求总数统计向redis发送的信息:" + JsonUtil.getObjectToJson(map));
		//pushDataToRedis(redisQueueName, rowkey, map);

	}

	/***
	 * 根据业务+ip+接口 统计每分钟
	 * 
	 * @param json
	 * @param redisQueueName
	 * @throws Exception
	 */
	public static void addEveryInterfMinReqSum(String json) throws Exception {
		InterfInfoEntity infoEntity = getEntity(json, Constants.HBASE_INTERF_REQ_CURRENT);
		String server_ip = infoEntity.getServer_ip();
		String service_code = infoEntity.getService_code();
		String req_timeStamp = infoEntity.getReq_timeStamp();
		String dateTime = DateUtil.getMinuteTime(req_timeStamp);
		String rowkey = service_code + dateTime + ipComponent(server_ip);
		HbaseUtil.automModifyValue(Constants.HBASE_INTERF_REQ_CURRENT, rowkey, Constants.HBASE_FAMILY,
				Constants.HBASE_INTERF_SUM_COLUMN, "1");

	}

	public static void sendEveryInterfMinReqSum(String json, String redisQueueName) throws Exception {
		InterfInfoEntity infoEntity = getEntity(json, Constants.HBASE_INTERF_REQ_CURRENT);
		String server_ip = infoEntity.getServer_ip();
		String service_code = infoEntity.getService_code();
		String req_timeStamp = infoEntity.getReq_timeStamp();

		String dateTime = DateUtil.getMinuteTime(req_timeStamp);
		String rowkey = service_code + dateTime + ipComponent(server_ip);
		byte[] datas = HbaseUtil.getStormColumnData(Constants.HBASE_INTERF_REQ_CURRENT, rowkey, Constants.HBASE_FAMILY,
				Constants.HBASE_INTERF_SUM_COLUMN);
		Map map = new HashMap();
		BigInteger num = new BigInteger("0");
		if (null != datas && datas.length > 0) {
			num = new BigInteger(datas);
		}
		map.put("host", server_ip);
		map.put("req_timeStamp", req_timeStamp);
		map.put("service_code", service_code);
		map.put("sum", num);
		logger.info("每个接口每分钟请求数统计向redis发送的信息:" + JsonUtil.getObjectToJson(map));
		//pushDataToRedis(redisQueueName, rowkey, map);

	}

	public static String ipComponent(String server_ip) throws Exception {
		String[] ips = server_ip.split("\\.");
		String ip = "";
		for (int i = 0; i < ips.length; i++) {
			ip += ips[i];
		}
		return ip;
	}

	/***
	 * 
	 * @param tableName
	 * @param rowkey
	 * @param queueName
	 * @throws Exception
	 */
	public static void pushDataToRedis(String queueName, String key, Map value) throws Exception {
		RedisUtil.pushData(queueName, key, JsonUtil.getObjectToJson(value));
	}

}
