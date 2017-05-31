package com.bian.util;

/**
 * Created by Mr.Bi on 2017/5/28.
 */
public class ThreadUtils {

	public static final void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
