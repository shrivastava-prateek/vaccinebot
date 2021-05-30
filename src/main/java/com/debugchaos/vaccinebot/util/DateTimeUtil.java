package com.debugchaos.vaccinebot.util;

import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtil {
	
	public final static String getFormattedISTCurrentDate() {
		Instant timeStamp= Instant.now();
		ZonedDateTime istTime = timeStamp.atZone(ZoneId.of(ZONEID_INDIA));
		return istTime.format(ddMMyyyyFormatter);
	}
	
	public final static ZonedDateTime getISTZonedDateTime() {
		Instant timeStamp= Instant.now();
		return timeStamp.atZone(ZoneId.of(ZONEID_INDIA));
	}

}
