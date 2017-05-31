package com.bian.base_demo;

import com.bian.util.ThreadUtils;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Mr.Bi on 2017/5/28.
 */
@Component
public class Consumer {

	@RabbitListener(queues = {MQConfig.DURABLE_QUEUE_NAME, MQConfig.QUEUE_NAME})
//	@SendTo("spel") 手动指定reply地址,优先级低
	@SendTo(MQConfig.REPLY_ADDRESS)
	public String receive(Message message, Channel channel) throws IOException {
		//此方法结束后才会自动ack,抛出异常不ack-重新投递,抛出AmqpRejectAndDontRequeueException有ack不重投
		//可以手动调用底层的ack方法，提前ack
		MessageProperties props = message.getMessageProperties();
//		channel.basicAck(props.getDeliveryTag(), false);
//		channel.close();
		String messtr = new String(message.getBody());
		System.out.println(messtr);
		return "reply " + messtr;
		//当此方法有返回值时，将封装成Message并reply给props中指定的reply-Address(高)
		// 或者reply给@sendTo指定的Address-("exchangeName/routingKey")(低)
	}

	@RabbitListener(queues = MQConfig.REPLY_QUEUE)
	public void receiveReply(Message message) {
		String messtr = new String(message.getBody());
		System.out.println(messtr);
	}

	@RabbitListener(queues = MQConfig.FANOUT_QUEUE)
	public void receiveFanoutTestQueue(Message message) {
		String messtr = new String(message.getBody());
		System.out.println("fanout: " + messtr);
	}

	public static void topicMessageComsumer(Message message) {
		String messtr = new String(message.getBody());
		System.out.println("topic: " + messtr +"," + Thread.currentThread().getName());
		ThreadUtils.sleep(5000L);
	}

}
