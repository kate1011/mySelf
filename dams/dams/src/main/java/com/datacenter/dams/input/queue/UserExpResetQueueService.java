package com.datacenter.dams.input.queue;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

import com.google.common.base.Strings;

/**
 * 主播经验清零worker
 * @author wulinli
 */
public class UserExpResetQueueService {

	private static Logger logger=LogManager.getLogger(UserExpResetQueueService.class);
	private static JmsTemplate jmsTemplate;
	private static String userExpResetDestination;
	
	/**
	 * 接收队列的信息
	 * @throws Exception
	 */
	public static List<String> receiveMessage(int sum)throws Exception{
		if(sum != 0){
			List<String> lists = new ArrayList<String>();
			/* 建立连接 */
			ConnectionFactory factory = jmsTemplate.getConnectionFactory();
			Connection connection = null;
			MessageConsumer consumer = null;
			Session session = null;
			try {
				connection = factory.createConnection();
				connection.start();
				/* 获取session */
				session = connection.createSession(true,Session.CLIENT_ACKNOWLEDGE);
				Destination destination = session.createQueue(userExpResetDestination);
				/* 创建消费者 */
				if(consumer == null){			
					consumer = session.createConsumer(destination);
				}
				/* 获取消息 */
				if(consumer != null){
					/* 获取配置数量的消息的信息 */
					for(int i=0;i<sum;i++){
						TextMessage message = (TextMessage) consumer.receive(500);
						if(message != null){
							String data = message.getText();
							logger.info("[DAMS主播经验清零UserExpResetQueueService]uc队列数据出口>>总数是 : "+sum+" , 数据标示数 : "+i+" , 数据是 : message="+data);
							if(Strings.isNullOrEmpty(data)){
								break;
							}
							lists.add(data);
							/* 处理消息 */
							session.commit();
							Thread.sleep(10);
						}else if(message == null){
							break;
						}
					}
					return lists;
				}
			} catch (Exception e) {
				logger.error("[DAMS主播经验清零UserExpResetService#ERROR]worker读取PC队列数据出错.",e);
			}finally{
				try {
					session.close();
					connection.close();
				} catch (JMSException e) {
					logger.error("[DAMS主播经验清零UserExpResetService#ERROR]队列关闭连接出错!",e);
				}
			}
		}
		return null;
	}
	
	public static JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}
	public static void setJmsTemplate(JmsTemplate jmsTemplate) {
		UserExpResetQueueService.jmsTemplate = jmsTemplate;
	}

	public static String getUserExpResetDestination() {
		return userExpResetDestination;
	}

	public static void setUserExpResetDestination(String userExpResetDestination) {
		UserExpResetQueueService.userExpResetDestination = userExpResetDestination;
	}
}
