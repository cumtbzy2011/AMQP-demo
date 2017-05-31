package com.bian.rpc_demo;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Mr.Bi on 2017/5/28.
 */
@Configuration
public class MQConfig {
	//rpc利用用reply-to
	//rabbitmq3.4.0以上支持Direct reply-to; spring amqp 1.4.1以上自动使用
	//此时不用设置reply-to的地址相关属性
	//如果是异步调用则不能用direct reply_to; 必须要reply-to地址

	public static final String QUEUE_NAME = "rpc_send_queue";
	public static final String EXCHANGE_NAME = "rpc_send_exchange";
	public static final String BINDING_NAME = "rpc_send_bindingKey";

	@Autowired
	public void baseDeclaration(ConnectionFactory connectionFactory) {
		RabbitAdmin admin = new RabbitAdmin(connectionFactory);
		Queue rpcQueue = new Queue(QUEUE_NAME, false, false, true);
		DirectExchange exchange = new DirectExchange(EXCHANGE_NAME, false, true);
		Binding binding = BindingBuilder.bind(rpcQueue).to(exchange).with(BINDING_NAME);
		admin.declareQueue(rpcQueue);
		admin.declareExchange(exchange);
		admin.declareBinding(binding);
	}

	@Bean
	public RabbitTemplate declareTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setReplyTimeout(30000L);
		return rabbitTemplate;
	}
}
