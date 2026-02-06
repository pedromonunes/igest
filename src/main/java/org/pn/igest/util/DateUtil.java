package org.pn.igest.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
	
	public DateUtil() {}
	
    public static final String ISO_DATE = "yyyy-MM-dd";
    public static final String TIMESTAMP_FULL = "yyyy-MM-dd HH:mm:ss";
    
    // retorna YYYY-MM-DD
    public static String getTodayIso() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(ISO_DATE));
    }

    // retorna yyyy-MM-dd HH:mm:ss
    public static String nowTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_FULL));
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

}