package com.bian.confirm_demo;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
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
	//1.设置mandatory=true（使returncallback生效）
	//2.CachingConnectionFactory并设置publisherConfirms and publisherReturns properties to 'true'

//	1.当未发送到交换机时，confirm(ack=false, cause !=null) ruturnCallback不回调
//	2.当发送到交换机，但未正确入队时,confirm(ack=true, cause=null) returnCallback回调-->原因
//	3.当发送到交换机，并正确入队时，confirm(ack=true, cause=null) returnCallback不回调

	public static final String QUEUE_NAME = "confirm_send_queue";
	public static final String EXCHANGE_NAME = "confirm_send_exchange";
	public static final String BINDING_NAME = "confirm_send_bindingKey";

	public static final String REPLY_QUEUE_NAME = "confirm_reply_queue";
	public static final String REPLY_EXCHANGE_NAME = "confirm_reply_exchange";
	public static final String REPLY_BINDING_NAME = "confirm_reply_binding";

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("127.0.0.1:5672");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setVirtualHost("/");
		connectionFactory.setPublisherConfirms(true); //必须要设置
		connectionFactory.setPublisherReturns(true);
		return connectionFactory;
	}

	@Autowired
	public void baseDeclaration(ConnectionFactory connectionFactory) {
		RabbitAdmin admin = new RabbitAdmin(connectionFactory);
		Queue confirmQueue = new Queue(QUEUE_NAME, false, false, true);
		DirectExchange exchange = new DirectExchange(EXCHANGE_NAME, false, true);
		Binding binding = BindingBuilder.bind(confirmQueue).to(exchange).with(BINDING_NAME);
		admin.declareQueue(confirmQueue);
		admin.declareExchange(exchange);
		admin.declareBinding(binding);

		Queue replyQueue = new Queue(REPLY_QUEUE_NAME, false, false, true);
		DirectExchange replyExchange = new DirectExchange(REPLY_EXCHANGE_NAME, false, true);
		Binding replyBinding = BindingBuilder.bind(replyQueue).to(replyExchange).with(REPLY_BINDING_NAME);
		admin.declareQueue(replyQueue);
		admin.declareExchange(replyExchange);
		admin.declareBinding(replyBinding);
	}



	@Bean
	public RabbitTemplate declareTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setReplyTimeout(30000L);
		rabbitTemplate.setMandatory(true);
		rabbitTemplate.setReplyAddress(REPLY_QUEUE_NAME);
		rabbitTemplate.setConfirmCallback((correlation, ack, cause) -> {
			System.out.println("correlation: " + correlation.getId());
			System.out.println("ack: " + ack);
			System.out.println("cause: " + cause);
		});
		rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
			System.out.println("return message: " + new String(message.getBody()));
			System.out.println("return replyCode: " + replyCode);
			System.out.println("return replyText: " + replyText);
			System.out.println("return exchange: " + exchange);
			System.out.println("return routingKey: " + routingKey);
		});
		return rabbitTemplate;
	}


}
