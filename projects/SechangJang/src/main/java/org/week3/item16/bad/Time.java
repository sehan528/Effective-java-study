package org.week3.item16.bad;

public class Time {
    // 불변이라도 public 필드는 좋지 않음
    public final int hour;
    public final int minute;

    public Time(int hour, int minute) {
        if (hour < 0 || hour >= 24)
            throw new IllegalArgumentException("시간: " + hour);
        if (minute < 0 || minute >= 60)
            throw new IllegalArgumentException("분: " + minute);
        this.hour = hour;
        this.minute = minute;
    }
}