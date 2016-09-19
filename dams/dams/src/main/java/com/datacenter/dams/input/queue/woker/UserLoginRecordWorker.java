package com.datacenter.dams.input.queue.woker;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;

import com.datacenter.dams.business.center.MessageHandlerCenter;
import com.datacenter.dams.input.queue.UserLoginQueueService;
import com.datacenter.dams.util.JsonUtil;
import com.datacenter.worker.worker.QuartzWorker;
import com.google.common.base.Strings;

/**
 * 用户登录记录worker，从uc系统取消息
 * @author wulinli
 */
@DisallowConcurrentExecution
public class UserLoginRecordWorker extends QuartzWorker<String>{
	
	private static Logger logger = LogManager.getLogger(UserLoginRecordWorker.class);
	
	@Override
	protected List<String> getData(int sum) {
		try {
			List<String> listStrings = UserLoginQueueService.receiveMessage(sum);
			if(listStrings != null && listStrings.size() > 0){
				logger.info("[DAMS用户登录记录UserLoginWorker]从UC队列取消息："+JsonUtil.getObjectToJson(listStrings));
				return listStrings;
			}
		} catch (Exception e) {
			logger.error("[DAMS用户登录记录UserLoginWorker]从UC队列取消息出错。",e);
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected void process(String message) throws Exception {
		if(!Strings.isNullOrEmpty(message)){
			Map info = (Map) JsonUtil.getObjectFromJson(message, Map.class);
			MessageHandlerCenter.userLoginCenter.handler(info);
		}
	}

	@Override
	protected String toLog(String log) {
		return null;
	}
}
