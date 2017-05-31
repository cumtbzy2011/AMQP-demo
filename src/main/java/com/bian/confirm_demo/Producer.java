package com.bian.confirm_demo;

import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by Mr.Bi on 2017/5/28.
 */
@Component
public class Producer {

	@Autowired
	RabbitTemplate rabbitTemplate;

	//回调returnback时，是不保证顺序的，所以一般加个Correlation来判断发送和返回的对应关系
	public void asyncSend(String messtr) {
		MessageProperties props = new MessageProperties();
		String correlationId = "this is test corrId: " + UUID.randomUUID().toString();
		props.setCorrelationId(correlationId.getBytes());
		props.setReplyToAddress(new Address(MQConfig.REPLY_EXCHANGE_NAME, MQConfig.REPLY_BINDING_NAME));

		Message message = new Message(messtr.getBytes(), props);
		rabbitTemplate.send(
			MQConfig.EXCHANGE_NAME,
			MQConfig.BINDING_NAME,
			message,
			new CorrelationData("this is uuid: " + UUID.randomUUID().toString())
		);
	}
}
