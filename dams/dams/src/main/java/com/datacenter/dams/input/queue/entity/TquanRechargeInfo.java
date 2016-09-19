package com.datacenter.dams.input.queue.entity;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TquanRechargeInfo {

	private BigInteger userID;							// 用户ID
	private Integer type;								//充值类型
	private String time;								//充值时间
	public BigInteger TDNumber;						    //如果是T豆结算零头转换，则消耗TD
	private BigDecimal number;								//充值总额
	private String orderId;								//订单编号
	private String clientType;							//设备类型
	private String version;								//版本号
	/* 类型标示 */
	private String dataType = "tq_recharge";
	
	public BigInteger getUserID() {
		return userID;
	}
	public void setUserID(BigInteger userID) {
		this.userID = userID;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public BigInteger getTDNumber() {
		return TDNumber;
	}
	public void setTDNumber(BigInteger TDNumber) {
		this.TDNumber = TDNumber;
	}
	public BigDecimal getNumber() {
		return number;
	}
	public void setNumber(BigDecimal number) {
		this.number = number;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
