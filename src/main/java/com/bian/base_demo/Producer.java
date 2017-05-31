package com.bian.base_demo;

import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by Mr.Bi on 2017/5/28.
 */
@Component
public class Producer {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public String send(String messtr) {
		MessageProperties props = new MessageProperties();
		props.setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
		props.setCorrelationId(UUID.randomUUID().toString().getBytes());
		props.setReplyTo(MQConfig.REPLY_ADDRESS);
		Message message = new Message(messtr.getBytes(), props);
		rabbitTemplate.send(MQConfig.DIRECT_EXCHANGE_NAME, MQConfig.BINDING_NAME, message);
		return null;
	}

	//消息持久化要求：队列是持久化队列+消息是持久化消息
	public String sendDurable(String messtr) {
		MessageProperties props = new MessageProperties();
		props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		props.setCorrelationId(UUID.randomUUID().toString().getBytes());
		props.setReplyToAddress(new Address(MQConfig.REPLY_EXCHANGE, MQConfig.REPLY_ROUNTING));
		Message message = new Message(messtr.getBytes(), props);
		rabbitTemplate.send(MQConfig.DIRECT_EXCHANGE_NAME, MQConfig.DURABLE_BINDING_NAME, message);
		return null;
	}

	public String sendToFanoutExchange(String messtr) {
		MessageProperties props = new MessageProperties();
		props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		props.setCorrelationId(UUID.randomUUID().toString().getBytes());
		props.setReplyToAddress(new Address(MQConfig.REPLY_EXCHANGE, MQConfig.REPLY_ROUNTING));
		Message message = new Message(messtr.getBytes(), props);
		rabbitTemplate.send(MQConfig.FANOUT_EXCHANGE, "", message);
		return null;
	}

	public String sendToTopicExchange(String messtr, String bindingKey) {
		MessageProperties props = new MessageProperties();
		props.setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
		props.setCorrelationId(UUID.randomUUID().toString().getBytes());
		props.setReplyToAddress(new Address(MQConfig.REPLY_EXCHANGE, MQConfig.REPLY_ROUNTING));
		Message message = new Message(messtr.getBytes(), props);
		rabbitTemplate.send(MQConfig.TOPIC_EXCHANGE, bindingKey, message);
		return null;
	}
}
