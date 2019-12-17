package br.com.mtanuri.scriptLiferayRobot;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeUtil {

	private static TimeUtil single_instance = null;
	private Calendar calendar;

	private TimeUtil() {
		calendar = new GregorianCalendar();
	}

	public static TimeUtil getInstance() throws IOException {
		if (single_instance == null)
			single_instance = new TimeUtil();

		return single_instance;
	}

	public Long getTimeInMillis() {
		return calendar.getTimeInMillis();
	}

}
