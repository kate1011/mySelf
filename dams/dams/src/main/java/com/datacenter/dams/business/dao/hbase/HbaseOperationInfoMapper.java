package com.datacenter.dams.business.dao.hbase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.datacenter.dams.input.queue.entity.HbaseOperationInfo;
import com.datacenter.dams.util.JsonUtil;

public class HbaseOperationInfoMapper {

	private final Logger log = LogManager.getLogger(HbaseOperationInfoMapper.class);
	/* 转换时间格式 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private Configuration conf = null;
	private static final String HBASEOPERATIONINFO_TABLENAME = "HbaseOperationInfo";
	private static final String HBASEOPERATIONINFO_COLUMN_DATA = "operation_data";

	HbaseOperationInfoMapper() {
		conf = HBaseConfiguration.create();
		conf.set("ipc.socket.timeout", "3000");
		conf.set("hbase.client.retries.number", "1");
		conf.set("hbase.client.pause", "100");
		conf.set("zookeeper.recovery.retry", "1");
		conf.set("zookeeper.recovery.retry.intervalmill", "100");

	}

	/**
	 * 添加HbaseOperationInfo
	 * 
	 * @param HbaseOperationInfo
	 * @throws Exception
	 */
	public void addHbaseOperationInfo(HbaseOperationInfo hbaseOperationInfo) {

		/* 组装rowkey 的格式：time_01/02_userId */
		String rowKey = null;
		/* 转换数据 */
		String hbaseJson = null;
		try {
			hbaseJson = JsonUtil.getObjectToJson(hbaseOperationInfo);
			log.debug("转换HbaseOperationInfo为Json对象成功！");
		} catch (Exception e) {
			log.error("转换HbaseOperationInfo为Json失败！失败的原因：" + e.getMessage());
		}
		/* 添加表 */
			this.createHTable();
			
		/* 添加数据 */
		try {
			this.addRow(rowKey, hbaseJson);
			//throw new Exception("佣金消费失败，参数错误！");
			log.debug("添加HbaseOperationInfo的Hbase成功！");
		} catch (Exception e) {
			log.error("添加HbaseOperationInfo的Hbase失败！失败的原因：" + e.getMessage());
		}
	}

	/**
	 * 通过UserLoginRecordQuery进行条件查询 ttnum必须存在 startTime endTime 默认的是1个小时
	 * 
	 * @param query
	 */
	public List<Map<String, HbaseOperationInfo>> selectListBySelective(String startTime, String endTime) {
		List<Map<String, HbaseOperationInfo>> list = getListHasConditions(startTime, endTime);
		return list;
	}

	/**
	 * 根据条件查询HbaseOperationInfo 存在分页查询 已确定条件： 存在ttnum， 存在默认时间段：1小时以前的数据 不确定条件：
	 * 登陆或是退出的类型
	 * 
	 * @param ttnum
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 */
	@SuppressWarnings("resource")
	private List<Map<String, HbaseOperationInfo>> getListHasConditions(String startTime, String endTime) {
		List<Map<String, HbaseOperationInfo>> list = new ArrayList<Map<String, HbaseOperationInfo>>();
		HTable htable;
		try {
			htable = new HTable(conf, HBASEOPERATIONINFO_TABLENAME);
			Scan scan = new Scan();
			scan.setStartRow(startTime.getBytes());
			scan.setStopRow(endTime.getBytes());
			ResultScanner rs = htable.getScanner(scan);
			if (rs != null) {
				for (Result r : rs) {
					Map<String, HbaseOperationInfo> map = new HashMap<String, HbaseOperationInfo>();
					String rowKey = new String(r.getRow());
					String json = new String(r.getValue(HBASEOPERATIONINFO_COLUMN_DATA.getBytes(),
							HBASEOPERATIONINFO_COLUMN_DATA.getBytes()));
					HbaseOperationInfo hbase;
					try {
						hbase = this.jsonConvertToHbaseOperationInfo(json);
						map.put(rowKey, hbase);
						list.add(map);
					} catch (Exception e) {
						log.error("json 转换Hbase操作对象失败，失败原因:" + e.getMessage());
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 创建表
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({ "deprecation", "resource" })
	private void createHTable() {
		HBaseAdmin admin;
		try {
			admin = new HBaseAdmin(conf);
			if (admin.tableExists(HBASEOPERATIONINFO_TABLENAME)) {
				log.debug("HbaseOperationInfo表已经存在！");
				return;
			}
			TableName tableName = TableName.valueOf(HBASEOPERATIONINFO_TABLENAME);
			HTableDescriptor htable = new HTableDescriptor(tableName);
			HColumnDescriptor data = new HColumnDescriptor(HBASEOPERATIONINFO_COLUMN_DATA);
			htable.addFamily(data);
			admin.createTable(htable);
			log.debug("创建Hbase的HbaseOperationInfo表成功！");
			admin.close();
		} catch (MasterNotRunningException e) {
			log.error("Hbase Master运行异常，" + e.getMessage());
		} catch (ZooKeeperConnectionException e) {
			log.error("zookeeper连接失败，" + e.getMessage());
		} catch (IOException e) {
			log.error("IOException异常，" + e.getMessage());
		}

	}

	/**
	 * 表添加 row 的数据
	 * 
	 * @param rowKey
	 * @param json
	 */
	private void addRow(String rowKey, String json) {
		HTable table;
		try {
			table = new HTable(conf, HBASEOPERATIONINFO_TABLENAME);
			Put put = new Put(rowKey.getBytes());
			put.add(HBASEOPERATIONINFO_COLUMN_DATA.getBytes(), HBASEOPERATIONINFO_COLUMN_DATA.getBytes(), json.getBytes());
			table.put(put);
		} catch (IOException e) {
			log.error("hbase 添加row数据失败,失败的原因:" + e.getMessage());
		}

	}

	/**
	 * 把json转换成HbaseOperationInfo对象
	 * 
	 * @param json
	 * @return
	 */
	private HbaseOperationInfo jsonConvertToHbaseOperationInfo(String json) throws Exception {
		HbaseOperationInfo hbase = (HbaseOperationInfo) JsonUtil.getObjectFromJson(json, HbaseOperationInfo.class);
		return hbase;
	}
}
