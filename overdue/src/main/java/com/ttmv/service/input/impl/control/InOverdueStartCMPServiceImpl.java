package com.ttmv.service.input.impl.control;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.ttmv.dao.bean.Cmp;
import com.ttmv.dao.inter.mysql.ICmpExpireInter;
import com.ttmv.service.OverdueService;
import com.ttmv.service.input.ResponseTool;
import com.ttmv.service.tools.constant.ErrorCodeConstant;
import com.ttmv.service.tools.util.DateUtil;
/***
 * 金色弹窗开始提醒
 * @author kate
 *
 */
@SuppressWarnings("rawtypes")
@Service("inOverdueStartCMPServiceImpl")
public class InOverdueStartCMPServiceImpl  extends OverdueService{
	private static Logger logger = LogManager.getLogger(InOverdueStartCMPServiceImpl.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	@Resource(name="icmpExpireInterImpl")
	private ICmpExpireInter  iCmpExpireInterImpl;
	
	@Override
	public Map handler(Map obj) {
		try {
		 	checkData(obj);
			
		} catch (Exception e) {
			logger.error("数据校验失败！！！", e);
			return ResponseTool.returnError(ErrorCodeConstant.SYSTEM_DATAFORMAT_ERROR_CODE);
		}
		Cmp data;
		try {
			data = (Cmp)getDataObject(obj);
		} catch (Exception e) {
			logger.error("数据转换对象失败！！！", e);
			return ResponseTool.returnError(ErrorCodeConstant.SYSTEM_DATAFORMAT_ERROR_CODE);
		}
		try {
			iCmpExpireInterImpl.addOrUpdateCmp(data);
		} catch (Exception e) {
			logger.error("金色弹窗开始提醒数据添加失败！！！", e);
			return ResponseTool.returnError(ErrorCodeConstant.SYSTEM_DATAFORMAT_ERROR_CODE);
		}
		return ResponseTool.returnSuccess();
	}

	@Override
	public Object getDataObject(Map reqMap) throws Exception {
		Cmp cmp=new Cmp();
		cmp.setUserId(new BigInteger(reqMap.get("userID").toString()));
		cmp.setTag(reqMap.get("tag").toString());
		cmp.setWarntime(sdf.format(DateUtil.getDate(reqMap.get("WarnTime").toString())));
		cmp.setType(reqMap.get("type").toString());
		return cmp;
	}

	@Override
	public void checkData(Map reqMap) throws Exception {
		if (reqMap.get("userID") == null || "".equals(reqMap.get("userID"))) {
			throw new Exception("[InOverdueStartCMPServiceImpl[userID] is null...]");
		}
		
		if (reqMap.get("tag") == null || "".equals(reqMap.get("tag"))) {
			throw new Exception("[InOverdueStartCMPServiceImpl[tag] is null...]");
		}
		
		if (reqMap.get("WarnTime") == null || "".equals(reqMap.get("WarnTime"))) {
			throw new Exception("[InOverdueStartCMPServiceImpl[WarnTime] is null...]");
		}
		
		if (reqMap.get("type") == null || "".equals(reqMap.get("type"))) {
			throw new Exception("[InOverdueStartCMPServiceImpl[type] is null...]");
		}
		
	}

}
