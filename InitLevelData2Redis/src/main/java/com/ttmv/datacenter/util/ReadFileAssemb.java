package com.ttmv.datacenter.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ReadFileAssemb {

	//{"sum":20,"spendId":20,"spendToId":10,"time":1234567890}
	
	public static String flowerTypeAssemb(String data) throws Exception{
		String[] datas=data.split("\\s+");
		if (datas.length!=2) {
			return null;
		}
		BigDecimal sum=new BigDecimal(datas[1]);
		BigInteger spendToId=new BigInteger("-1");
		BigInteger spendId=new BigInteger(datas[0]);
		Long currentTime=new Date().getTime();
		String time=String.valueOf(currentTime).substring(0,10);
		Map resultMap=new HashMap();
		resultMap.put("sum", sum);
		resultMap.put("spendId", spendId);
		resultMap.put("spendToId", spendToId);
		resultMap.put("time", time);
		return JsonUtil.getObjectToJson(resultMap);
	}
	
	
	//{"sum":20,"spendId":20,"spendToId":10,"time":1234567890}
	public static String heartTypeAssemb(String data) throws Exception{
		String[] datas=data.split("\\s+");
		if (datas.length!=2) {
			return null;
		}
		BigDecimal sum=new BigDecimal(datas[1]);
		BigInteger spendToId=new BigInteger("-1");
		BigInteger spendId=new BigInteger(datas[0]);
		Long currentTime=new Date().getTime();
		String time=String.valueOf(currentTime).substring(0,10);
		Map resultMap=new HashMap();
		resultMap.put("sum", sum);
		resultMap.put("spendId", spendId);
		resultMap.put("spendToId", spendToId);
		resultMap.put("time", time);
		return JsonUtil.getObjectToJson(resultMap);
	}
	
	//TQ : {"tq":20,"spendId":20,"spendToId":10,"time":1234567890}
	public static String tdouTypeAssemb(String data) throws Exception{
		String[] datas=data.split("\\s+");
		if (datas.length!=2) {
			return null;
		}
		BigDecimal tq=new BigDecimal(datas[1]);
		BigInteger spendToId=new BigInteger("-1");
		BigInteger spendId=new BigInteger(datas[0]);
		Long currentTime=new Date().getTime();
		String time=String.valueOf(currentTime).substring(0,10);
		Map resultMap=new HashMap();
		resultMap.put("tq", tq);
		resultMap.put("spendId", spendId);
		resultMap.put("spendToId", spendToId);
		resultMap.put("time", time);
		return JsonUtil.getObjectToJson(resultMap);
	}
	
	
	
	
}
