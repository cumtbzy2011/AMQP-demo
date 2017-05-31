package com.bian.confirm_demo;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by Mr.Bi on 2017/5/28.
 */
@Component
public class Consumer {

	@RabbitListener(queues = MQConfig.QUEUE_NAME)
	public String  handlerMessage(Message message) {
		String messtr = new String(message.getBody());
		System.out.println("receive: " + messtr);
		return "reply-" + messtr;
	}

	@RabbitListener(queues = MQConfig.REPLY_QUEUE_NAME)
	public void handlerReply(Message message) {
		System.out.println("handlerReply: " + new String(message.getBody()));
	}
}
