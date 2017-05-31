package com.bian.confirm_demo;

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
	public void testConfirm() throws InterruptedException {
		String senderMessage = "hello world";
		producer.asyncSend(senderMessage);
		new ArrayBlockingQueue(1).take();
	}
}
