package com.ttmv.datacenter.paycenter.service.facade.tools.jmstool;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * @author Damon_Zs
 * @version 创建时间：2015年7月29日09:27:27
 * @explain :异步队列消息发送器（TB消费统计分析）
 */
public class DamsPcTBConsumeTool {

	private JmsTemplate damsPcTBConsumejmsTemplate;

	public JmsTemplate getDamsPcTBConsumejmsTemplate() {
		return damsPcTBConsumejmsTemplate;
	}
	public void setDamsPcTBConsumejmsTemplate(JmsTemplate damsPcTBConsumejmsTemplate) {
		this.damsPcTBConsumejmsTemplate = damsPcTBConsumejmsTemplate;
	}



	/**
	 * 发送消息
	 * @param msg
	 */
	public void sendMessage(final String msg) {
		damsPcTBConsumejmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message message = session.createTextMessage(msg);
				return message;
			}
		});
	}
}
