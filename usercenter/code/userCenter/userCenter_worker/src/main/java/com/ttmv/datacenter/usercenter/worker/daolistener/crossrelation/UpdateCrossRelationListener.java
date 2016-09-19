package com.ttmv.datacenter.usercenter.worker.daolistener.crossrelation;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.ttmv.datacenter.usercenter.dao.implement.constant.SolrConstant;
import com.ttmv.datacenter.usercenter.dao.implement.mapper.bean.usercrossrelation.MysqlUserCrossRelation;
import com.ttmv.datacenter.usercenter.dao.implement.mapper.bean.usercrossrelation.SolrUserCrossRelation;
import com.ttmv.datacenter.usercenter.dao.implement.mapper.usercrossrelation.MysqlUserCrossRelationMapper;
import com.ttmv.datacenter.usercenter.dao.implement.mapper.usercrossrelation.SolrUserCrossRelationMapper;
import com.ttmv.datacenter.usercenter.dao.implement.util.JsonUtil;
import com.ttmv.datacenter.usercenter.domain.data.UserCrossRelation;
import com.ttmv.datacenter.usercenter.worker.daolistener.constant.ListenerConstant;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UpdateCrossRelationListener implements MessageListener{
	/* 日志输出类 */
	private final Logger log = LogManager.getLogger(UpdateCrossRelationListener.class);
	
	private Destination updateCrossDestination;
	private MysqlUserCrossRelationMapper mysqlUserCrossRelationMapper;
	private SolrUserCrossRelationMapper solrUserCrossRelationMapper;
	private Destination ucErrorDestination;
	private JmsTemplate jmsTemplate;
	
	public void onMessage(Message message) {
		ActiveMQTextMessage msg = null;
		String jsonData = null;
		UserCrossRelation userCrossRelation = null;
		Map map = null;
		String reqID = null;
		
		log.debug("[获取updateCrossDestination队列数据！开始。。]");
		if (message instanceof ActiveMQTextMessage) {
			msg = (ActiveMQTextMessage) message;
			try {
				jsonData = msg.getText();
				log.info("=========获取[updateCrossDestination]队列消息======:" + jsonData);
				log.debug("从updateCrossDestination队列获取数据成功！");
			} catch (Exception e) {
				log.error("从updateCrossDestination队列获取数据失败！",e);
				return;//终止此次worker运行
			}
		}
		
		/* json转换成UserInfo 对象 */
		try{
			map = (Map) JsonUtil.getObjectFromJson(jsonData,Map.class);
			String tmp=JsonUtil.getObjectToJson(map.get("data"));
			reqID = map.get("reqID").toString();
			log.debug("[" +reqID+ "]@@" + "[获取reqID]");
			userCrossRelation = (UserCrossRelation)JsonUtil.getObjectFromJson(tmp, UserCrossRelation.class);
			log.debug("[" + reqID+ "]@@" + "[json转换UserCrossRelation对象]");
		}catch(Exception e){
			log.error("[" + reqID+ "]@@" + "[json转换UserCrossRelation对象失败！]",e);
			return ;//终止此次worker运行
		}
		
		/* 修改mysql */
		Integer count = (Integer)map.get("count");
		count ++ ;
		if(count <= 3){
			List<Object> results = null;
			map.put("count", count);
			log.debug("[" + reqID+ "]@@" + "[重新添加计数count]");
			try{
				log.debug("[" + reqID+ "]@@" + "[开始执行mysql修改。。]");
				if (!map.containsKey(ListenerConstant.MYSQL)) {
					SolrUserCrossRelation solr = solrUserCrossRelationMapper.getUserCrossRelationByKey(SolrConstant.SOLR_KEY_ID, userCrossRelation.getId().toString(), SolrConstant.SOLR_START, SolrConstant.SOLR_UNIQUE);
					log.debug("[" + reqID+ "]@@" + "[查询SolrUserCrossRelation成功！]");
					if(solr == null){
						log.debug("[" + reqID+ "]@@" + "[查询SolrUserInfo成功,但是对象为null！id是："+userCrossRelation.getId()+"]");
						return;
					}
					MysqlUserCrossRelation mysql = mysqlUserCrossRelationMapper.getMysqlUserCrossRelationById(new BigInteger(solr.getId().toString()), solr.getDataSourceKey());
					log.debug("[" + reqID+ "]@@" + "[查询MysqlUserCrossRelation成功！]");
					if(mysql == null ){
						log.debug("[" + reqID+ "]@@" + "[查询MysqlUserCrossRelation成功,但是对象为null！id是："+solr.getId()+"]");
						return;
					}
					mysql = mysqlUserCrossRelationMapper.userCrossRelationConvertToMysqlUserCrossRelation(userCrossRelation,mysql);
					log.debug("[" + reqID+ "]@@" + "[MysqlUserCrossRelation新值覆盖旧值]");
					mysqlUserCrossRelationMapper.updateMysqlUserCrossRelation(mysql, solr.getDataSourceKey());
					log.debug("[" + reqID+ "]@@" + "[修改MysqlUserCrossRelation成功！]");
					map.put(ListenerConstant.MYSQL, true);
					log.debug("[" + reqID+ "]@@" + "[设置MysqlUserCrossRelation修改成功标示！]");
				}
			}catch(Exception e){
				log.error("[" + reqID+ "]@@" + "[Mysql修改失败！]",e);
				/* 数据重新加入队列 */
				String newJson = null;
				try {
					newJson = JsonUtil.getObjectToJson(map);
					log.debug("[" + reqID+ "]@@" + "[UserCrossRelation数据对象重新转为Json！]");
					backToMq(newJson);
					log.debug("[" + reqID+ "]@@" + "[数据重新加入队列成功！]");
				} catch (Exception e1) {
					log.error("[" + reqID+ "]@@" + "[注意：修改MysqlUserCrossRelation信息，mysql修改失败，数据重新加入队列失败！]",e1);
					log.error("[" + reqID+ "]@@" + "[失败的数据是："+ newJson);
				}
				return;// 终止此次worker的运行
			}
		}else if(count > 3){
			/* 错误数据放入错误队列中 */
			backToErrorMq(jsonData);
			log.error("[" + reqID+ "]@@" + "[修改UserCrossRelation信息数据woker执行超过3次，数据是："+jsonData);
		}
		log.debug("[操作updateCrossDestination队列数据成功！结束。。]");
	}	

	/**
	 * 如果操作队列中间出现问题，则将队列的数据 重新返回到队列中
	 * 
	 * @param json
	 */
	private void backToMq(final String jsonData) {
		try {
			jmsTemplate.send(updateCrossDestination, new MessageCreator() {
				public Message createMessage(Session session)
						throws JMSException {
					return session.createTextMessage(jsonData);
				}
			});
			log.info("操作修改用户组队列的数据失败，数据重新加入队列成功！");
		} catch (Exception e) {
			log.error("注意，操作修改用户组队列的数据失败，数据重新加入队列失败，请手工把数据库中数据清理掉！失败原因：",e);
		}
	}
	
	/**
	 * 如果数据经过3次重试以后没有正确入库，则直接将数据放入错误的队列中
	 * 
	 * @param json
	 */
	private void backToErrorMq(final String jsonData) {
		try {
			jmsTemplate.send(ucErrorDestination, new MessageCreator() {
				public Message createMessage(Session session)
						throws JMSException {
					return session.createTextMessage(jsonData);
				}
			});
			log.debug("数据放入错误数据队列成功！");
		} catch (Exception e) {
			log.error("数据放入错误数据队列失败，错误的原因是：",e);
			log.error("数据放入错误数据队列失败，错误的数据是：" + jsonData);
		}
	}
	
	public void setUpdateCrossDestination(Destination updateCrossDestination) {
		this.updateCrossDestination = updateCrossDestination;
	}

	public void setMysqlUserCrossRelationMapper(
			MysqlUserCrossRelationMapper mysqlUserCrossRelationMapper) {
		this.mysqlUserCrossRelationMapper = mysqlUserCrossRelationMapper;
	}
	
	public void setSolrUserCrossRelationMapper(
			SolrUserCrossRelationMapper solrUserCrossRelationMapper) {
		this.solrUserCrossRelationMapper = solrUserCrossRelationMapper;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setUcErrorDestination(Destination ucErrorDestination) {
		this.ucErrorDestination = ucErrorDestination;
	}
}
