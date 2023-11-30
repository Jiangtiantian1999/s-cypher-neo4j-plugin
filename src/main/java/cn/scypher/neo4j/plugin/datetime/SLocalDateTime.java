package cn.scypher.neo4j.plugin.datetime;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public class SLocalDateTime {
    private final LocalDateTime localdatetime;

    public SLocalDateTime() {
        this.localdatetime = LocalDateTime.now();
    }

    public SLocalDateTime(LocalDateTime localDateTime) {
        this.localdatetime = localDateTime;
    }

    public SLocalDateTime(String localdatetimeString) {
        if (localdatetimeString.equalsIgnoreCase("NOW")) {
            this.localdatetime = LocalDateTime.MAX;
        } else {
            String[] localdatetimeStringList = localdatetimeString.split("T");
            SDate date = new SDate(localdatetimeStringList[0]);
            if (localdatetimeStringList.length == 2) {
                SLocalTime time = new SLocalTime(localdatetimeStringList[1]);
                this.localdatetime = LocalDateTime.of(date.getDate(), time.getLocalTime());
            } else {
                this.localdatetime = LocalDateTime.of(date.getDate(), LocalTime.MIN);
            }
        }
    }

    public SLocalDateTime(Map<String, Integer> datetimeMap) {
        SDate date = new SDate(datetimeMap);
        SLocalTime localtime = new SLocalTime(datetimeMap);
        this.localdatetime = LocalDateTime.of(date.getDate(), localtime.getLocalTime());
    }

    public Duration difference(SLocalDateTime localdatetime) {
        return Duration.between(this.localdatetime, localdatetime.getLocalDateTime());
    }

    public boolean isBefore(SLocalDateTime localdatetime) {
        return this.localdatetime.isBefore(localdatetime.getLocalDateTime());
    }

    public boolean isAfter(SLocalDateTime localdatetime) {
        return this.localdatetime.isAfter(localdatetime.getLocalDateTime());
    }

    public LocalDateTime getLocalDateTime() {
        return this.localdatetime;
    }
}
