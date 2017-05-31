package com.bian.base_demo;

import com.bian.util.ThreadUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Mr.Bi on 2017/5/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class MQTest {

	@Autowired
	Producer producer;

	@Test
	public void testSend() {
		String message = "hello world!";
		producer.send(message);
		ThreadUtils.sleep(60000);
	}

	@Test
	public void sendToFanout() {
		String message = "hello world!";
		producer.sendToFanoutExchange(message);
		ThreadUtils.sleep(60000);
	}

	@Test
	public void sendToTopic() throws InterruptedException {
		String message = "hello world!";
		for (int i = 0; i < 1000; i++)
			producer.sendToTopicExchange(message + 1, "regex.helloworld");
		new ArrayBlockingQueue<String>(1).take();
	}
}
