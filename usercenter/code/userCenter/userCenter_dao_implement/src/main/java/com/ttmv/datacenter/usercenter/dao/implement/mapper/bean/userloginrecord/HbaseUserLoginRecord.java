package com.ttmv.datacenter.usercenter.dao.implement.mapper.bean.userloginrecord;

import java.math.BigInteger;
import java.util.Date;

public class HbaseUserLoginRecord {

	private BigInteger id;
    private BigInteger TTnum;
    private Date time;
    private Integer type;
    private Integer clientType;
    private String address;
    private String ip;
    private String mac;
    private String disksn;
	public BigInteger getId()
	{
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public BigInteger getTTnum() {
		return TTnum;
	}
	public void setTTnum(BigInteger tTnum) {
		TTnum = tTnum;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getClientType() {
		return clientType;
	}
	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getDisksn() {
		return disksn;
	}
	public void setDisksn(String disksn) {
		this.disksn = disksn;
	}
}
