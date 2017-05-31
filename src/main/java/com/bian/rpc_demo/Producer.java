package com.bian.rpc_demo;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Mr.Bi on 2017/5/28.
 */
@Component
public class Producer {

	@Autowired
	RabbitTemplate template;

	public String send(String messtr) {
		//超时返回Null
		Object result = template.convertSendAndReceive(MQConfig.EXCHANGE_NAME, MQConfig.BINDING_NAME, messtr);
		return (String) result;
	}
}
