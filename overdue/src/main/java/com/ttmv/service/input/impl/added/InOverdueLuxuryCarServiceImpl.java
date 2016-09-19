package com.ttmv.service.input.impl.added;

import java.math.BigInteger;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.ttmv.dao.bean.LuxuryExpire;
import com.ttmv.dao.inter.mysql.ILuxuryExpireInter;
import com.ttmv.dao.inter.redis.IRedisLuxuryExpireInter;
import com.ttmv.service.OverdueService;
import com.ttmv.service.callback.http.GetUserInfo;
import com.ttmv.service.input.ResponseTool;
import com.ttmv.service.tools.constant.ErrorCodeConstant;
import com.ttmv.service.tools.util.DateUtil;
import com.ttmv.service.tools.util.UtilBean;

/**
 * @explain 豪车到期（增值服务）
 * @author Damon
 * @Time 2015年11月10日14:23:45
 */
@SuppressWarnings("rawtypes")
@Service("inOverdueLuxuryCarServiceImpl")
public class InOverdueLuxuryCarServiceImpl extends OverdueService {

	private static Logger logger = LogManager.getLogger(InOverdueLuxuryCarServiceImpl.class);
	@Resource(name = "iLuxuryExpireInterImpl")
	private ILuxuryExpireInter iLuxuryExpireInter;
	@Resource(name = "getUserInfo")
	private GetUserInfo getUserInfo;
	@Resource(name = "iRedisLuxuryExpireInterImpl")
	private IRedisLuxuryExpireInter iRedisLuxuryExpireInter;

	@Override
	public Map handler(Map reqMap) {
		logger.info("豪车到期续费。。。。");

		try {
			checkData(reqMap);
			// db对象组装
			LuxuryExpire luxuryExpire = null;
			try {
				luxuryExpire = (LuxuryExpire) this.getDataObject(reqMap);
				// add数据
				LuxuryExpire luExpire = null;
				/*
				 * String user_json = getUserInfo.excute(reqMap.get("userID") +
				 * ""); Map map = (Map) JsonUtil.getObjectFromJson(user_json,
				 * Map.class); Map result_map = (Map) map.get("resData");
				 */
				// if (null != result_map) {
				try {
					luExpire = iLuxuryExpireInter.queryLuxuryExpire(luxuryExpire.getUserId(), luxuryExpire.getCarId());
					if (null == luExpire) {
						iLuxuryExpireInter.addLuxuryExpire(luxuryExpire);
					} else {
						luxuryExpire.setId(luExpire.getId());
						iLuxuryExpireInter.updateLuxuryExpire(luxuryExpire);
						iRedisLuxuryExpireInter.deleteRedisLuxuryExpire(luxuryExpire.getUserId().toString(),
								luxuryExpire.getCarId().toString());
					}
				} catch (Exception e) {
					logger.error("豪车到期数据库操作失败！！！", e);
					return ResponseTool.returnException();
				}
				/*
				 * }else{ logger.error("该用户不存在！！！！"); return
				 * ResponseTool.returnException(); }
				 */

			} catch (Exception e) {
				logger.error("入库对象[LuxuryExpire]组装失败", e);
				return ResponseTool.returnException();
			}
		} catch (Exception e) {
			logger.error("数据校验失败！！！", e);
			return ResponseTool.returnError(ErrorCodeConstant.SYSTEM_DATAFORMAT_ERROR_CODE);
		}
		logger.info("豪车到期续费成功。。。。");
		// 返回处理结果
		return ResponseTool.returnSuccess();
	}

	/**
	 * 组装入库对象
	 * 
	 * @return
	 */

	public Object getDataObject(Map reqMap) throws Exception {
		LuxuryExpire luxuryExpire = new LuxuryExpire();
		luxuryExpire.setUserId(new BigInteger(reqMap.get("userID").toString()));
		luxuryExpire.setCarId(new BigInteger(reqMap.get("carID").toString()));
		// （unix时间戳）long转java date
		luxuryExpire.setEndTime(UtilBean.unixTimeFmt(Long.parseLong(reqMap.get("luxuryCarEndTime").toString())));
		luxuryExpire
				.setRemindTime(DateUtil.getQueryFixedTime(DateUtil.getDate(reqMap.get("luxuryCarEndTime").toString()), 1, -7));
		return luxuryExpire;
	}

	/**
	 * 业务数据校验(数据规格)
	 * 
	 * @param reqMap
	 * @return void
	 * @exception Exception
	 */
	public void checkData(Map reqMap) throws Exception {
		if (reqMap.get("userID") == null || "".equals(reqMap.get("userID"))) {
			throw new Exception("[InOverdueLuxuryCarServiceImpl[userID] is null...]");
		}
		if (reqMap.get("startTime") == null || "".equals(reqMap.get("startTime"))) {
			throw new Exception("[InOverdueLuxuryCarServiceImpl[startTime] is null...]");
		}
		if (reqMap.get("duration") == null || "".equals(reqMap.get("duration"))) {
			throw new Exception("[InOverdueLuxuryCarServiceImpl[duration] is null...]");
		}
		if (reqMap.get("tag") == null || "".equals(reqMap.get("tag"))) {
			throw new Exception("[InOverdueLuxuryCarServiceImpl[tag] is null...]");
		}
		if (reqMap.get("luxuryCarEndTime") == null || "".equals(reqMap.get("luxuryCarEndTime"))) {
			throw new Exception("[InOverdueLuxuryCarServiceImpl[luxuryCarEndTime] is null...]");
		}
		if (reqMap.get("carID") == null || "".equals(reqMap.get("carID"))) {
			throw new Exception("[InOverdueLuxuryCarServiceImpl[carID] is null...]");
		}

	}

}
