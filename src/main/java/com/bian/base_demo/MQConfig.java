package com.bian.base_demo;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * Created by Mr.Bi on 2017/5/28.
 */
@Configuration
public class MQConfig implements RabbitListenerConfigurer {
	public static final String QUEUE_NAME = "my_queue_name";
	public static final String DURABLE_QUEUE_NAME = "my_queue_name_durable";
	public static final String DIRECT_EXCHANGE_NAME = "my_direct_exchange";
	public static final String BINDING_NAME = "binding_name";
	public static final String DURABLE_BINDING_NAME = "durable_binding_name";

	public static final String REPLY_QUEUE = "reply_queue_name";
	public static final String REPLY_EXCHANGE = "reply_direct_exchange_name";
	public static final String REPLY_ROUNTING = "reply_reply_routing_name";
	public static final String REPLY_ADDRESS = REPLY_EXCHANGE + "/" + REPLY_ROUNTING;

	public static final String FANOUT_QUEUE = "test_queue_name_fanout";
	public static final String FANOUT_EXCHANGE = "fanout_exchange_name";


	public static final String TOPIC_EXCHANGE = "topic_exchange_name";
	public static final String TOPIC_BINDING = "regex.#";
	//topic支持模糊匹配：
	//必须以 . 分隔的字符串
	//# 代表words
	//* one word

	@Bean(name = TOPIC_EXCHANGE)
	public TopicExchange declareTopic() {
		TopicExchange exchange = new TopicExchange(TOPIC_EXCHANGE, false, true);
		return exchange;
	}


	@Bean(name = FANOUT_QUEUE)
	public Queue declareFanoutTestQueue() {
		Queue fanoutTestQueue = new Queue(FANOUT_QUEUE, false, false, true);
		return fanoutTestQueue;
	}

	@Bean(name = FANOUT_EXCHANGE)
	public FanoutExchange declareFanoutExchange() {
		FanoutExchange exchange = new FanoutExchange(FANOUT_EXCHANGE, false, true);
		return exchange;
	}

	@Bean
	public Binding declareFanoutBinding1(@Qualifier(FANOUT_QUEUE) Queue queue,
										@Qualifier(FANOUT_EXCHANGE) FanoutExchange exchange) {
		Binding binding = BindingBuilder.bind(queue).to(exchange);
		return binding;
	}

	@Bean
	public Binding declareFanoutBinding2(@Qualifier(QUEUE_NAME) Queue queue,
										@Qualifier(FANOUT_EXCHANGE) FanoutExchange exchange) {
		Binding binding = BindingBuilder.bind(queue).to(exchange);
		return binding;
	}

	@Bean
	public Binding declareFanoutBinding3(@Qualifier(DURABLE_QUEUE_NAME) Queue queue,
										@Qualifier(FANOUT_EXCHANGE) FanoutExchange exchange) {
		Binding binding = BindingBuilder.bind(queue).to(exchange);
		return binding;
	}

	@Bean(name = REPLY_QUEUE)
	public Queue declareReplyQueue() {
		Queue replyQueue = new Queue(REPLY_QUEUE, false, false, true);
		return replyQueue;
	}

	@Bean(name = REPLY_EXCHANGE)
	public DirectExchange declareReplyExchange() {
		DirectExchange replyExchange = new DirectExchange(REPLY_EXCHANGE, false, true);
		return replyExchange;
	}

	@Bean(name = REPLY_ROUNTING)
	public Binding bindReplyQueue(@Qualifier(REPLY_QUEUE) Queue queue, @Qualifier(REPLY_EXCHANGE) DirectExchange exchange) {
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(REPLY_ROUNTING);
		return binding;
	}


	@Bean(name = QUEUE_NAME)
	public Queue declareQueue() {
		//(name,durable,exclusive,autodelete,args)
		Queue directQueue = new Queue(QUEUE_NAME, false, false, true);
		return directQueue;
	}

	@Bean(name = DURABLE_QUEUE_NAME)
	public Queue declareDurableQueue() {
		Queue durableDirectQueue = new Queue(DURABLE_QUEUE_NAME, true, false, false);
		return durableDirectQueue;
	}

	@Bean(name = DIRECT_EXCHANGE_NAME)
	public DirectExchange declareDirectExchange() {
		DirectExchange directExchange = new DirectExchange(DIRECT_EXCHANGE_NAME);
		return directExchange;
	}

	@Bean(name = BINDING_NAME)
	public Binding bindQueue(@Qualifier(QUEUE_NAME) Queue queue, @Qualifier(DIRECT_EXCHANGE_NAME) DirectExchange exchange) {
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(BINDING_NAME);
		return binding;
	}

	@Bean(name = DURABLE_BINDING_NAME)
	public Binding bindDurableQueue(@Qualifier(DURABLE_QUEUE_NAME) Queue queue, @Qualifier(DIRECT_EXCHANGE_NAME) DirectExchange exchange) {
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(DURABLE_BINDING_NAME);
		return binding;
	}



	@Autowired
	ConnectionFactory factory;

	//动态注册一个topic监听者
	@Override
	public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
		RabbitAdmin admin = new RabbitAdmin(factory);
		Queue randomQueue = admin.declareQueue();
		TopicExchange exchange = new TopicExchange(MQConfig.TOPIC_EXCHANGE, false, true);
		admin.declareExchange(exchange);
		Binding topicBinding = BindingBuilder.bind(randomQueue).to(exchange).with(TOPIC_BINDING);
		admin.declareBinding(topicBinding);

		SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
		endpoint.setQueues(randomQueue);
		endpoint.setMessageListener(Consumer::topicMessageComsumer);
		endpoint.setId(UUID.randomUUID().toString());
		registrar.registerEndpoint(endpoint);
	}

	//和rabbitmq交互的细节由ListenerContainer决定
	//1.设置了并发接收信息的线程
	//2.设置了序列化Message的方式-生产者和消费者要一致
	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
		SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
		containerFactory.setConnectionFactory(factory);
		containerFactory.setConcurrentConsumers(3);
		containerFactory.setMaxConcurrentConsumers(10);
		containerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
		return containerFactory;
	}
}
