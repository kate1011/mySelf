package com.ttmv.initHbaseData;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String currentTime=args[0];
		String[] times=DataUtil.getStartEndTime(currentTime, 0);
		System.out.println("起始时间:"+times[0]+"结束时间："+times[1]);
		InitData2Hbase.initDatas2Hbase(times[0], times[1],currentTime+" 10:10:10");
		System.out.println("起始时间:"+times[0]+"结束时间："+times[1]+"数据初始化完成");
    
	}

}
