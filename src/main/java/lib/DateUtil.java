package lib;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;

public abstract class DateUtil {
	
	public static ZonedDateTime zoned(LocalDateTime time) {
		return ZonedDateTime.of(time, ZoneOffset.systemDefault());
	}
	
	public static <D extends ChronoLocalDate> long millis(ChronoLocalDateTime<D> time) {
		return time.toEpochSecond(ZoneOffset.UTC);
	}
	
	public static <D extends ChronoLocalDate> long millis(ChronoZonedDateTime<D> zoned) {
		return zoned.toEpochSecond();
	}
	
	public static void main(String[] args) {
		System.out.println(millis(zoned(LocalDateTime.now())));
	}
}
