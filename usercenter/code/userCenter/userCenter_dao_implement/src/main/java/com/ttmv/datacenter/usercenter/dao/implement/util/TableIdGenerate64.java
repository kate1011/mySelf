package com.ttmv.datacenter.usercenter.dao.implement.util;


import java.math.BigInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ttmv.datacenter.generator.GUID.GUIDGenerator;


/**
 * 
 * @author Scarlett.zhou
 * @date 2015年2月5日
 */
public class TableIdGenerate64 {

	private static final Logger logger = LogManager
			.getLogger(TableIdGenerate64.class);;
	private GUIDGenerator generator;

	public TableIdGenerate64(GUIDGenerator generator) {
		this.generator = generator;
	}

	public BigInteger getTableId(String tableName) {
		BigInteger id;
		try {
			id = new BigInteger(String.valueOf(generator.guid(tableName)));
		} catch (Exception e) {
			logger.error("生成 table id 异常!", e);
			return null;
		}
		return id;
	}
	
}
