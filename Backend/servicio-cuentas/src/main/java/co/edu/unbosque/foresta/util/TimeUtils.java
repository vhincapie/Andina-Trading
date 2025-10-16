package co.edu.unbosque.foresta.util;

import java.time.*;

public class TimeUtils {

    private static final ZoneId ZONA_NY = ZoneId.of("America/New_York");

    public static boolean isMarketOpenNow() {
        LocalTime now = LocalTime.now(ZONA_NY);
        DayOfWeek day = LocalDate.now(ZONA_NY).getDayOfWeek();
        return (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY)
                && !now.isBefore(LocalTime.of(9, 30))
                && !now.isAfter(LocalTime.of(16, 0));
    }

    public static boolean isAfterMarketHours() {
        LocalTime now = LocalTime.now(ZONA_NY);
        return now.isAfter(LocalTime.of(16, 0)) || now.isBefore(LocalTime.of(9, 30));
    }

    public static boolean isWeekend() {
        DayOfWeek d = LocalDate.now(ZONA_NY).getDayOfWeek();
        return d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY;
    }

    public static LocalDateTime todayStartNY() {
        return LocalDate.now(ZONA_NY).atStartOfDay();
    }

    public static LocalDateTime todayEndNY() {
        return LocalDate.now(ZONA_NY).plusDays(1).atStartOfDay().minusNanos(1);
    }

    public static LocalDateTime nowNY() {
        return LocalDateTime.now(ZONA_NY);
    }
}
