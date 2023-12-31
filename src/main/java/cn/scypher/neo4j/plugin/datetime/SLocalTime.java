package cn.scypher.neo4j.plugin.datetime;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SLocalTime {
    private final LocalTime localtime;

    public final LocalTime MIN = LocalTime.MIN;
    public final LocalTime MAX = LocalTime.MAX;

    public SLocalTime() {
        this.localtime = LocalTime.now();
    }

    public SLocalTime(LocalTime localTime) {
        this.localtime = localTime;
    }

    public SLocalTime(String localtimeString) {
        if (localtimeString.equalsIgnoreCase("NOW")) {
            this.localtime = LocalTime.MAX;
        } else {
            Pattern localtimePattern = Pattern.compile("(?<hour>\\d{2})(:?(?<minute>\\d{2})((:?(?<second>\\d{2}))((.|,)(?<nanosecond>\\d{1,9}))?)?)?");
            Matcher matcher = localtimePattern.matcher(localtimeString.trim());
            Map<String, Number> localtimeMap = new HashMap<>();
            String[] timeComponents = {"hour", "minute", "second", "nanosecond"};
            if (matcher.find()) {
                for (String component : timeComponents) {
                    if (matcher.group(component) != null) {
                        if (component.equals("nanosecond")) {
                            localtimeMap.put(component, Integer.parseInt(String.format("%-9s", matcher.group(component)).replace(" ", "0")));
                        } else {
                            localtimeMap.put(component, Integer.parseInt(matcher.group(component)));
                        }
                    }
                }
                this.localtime = this.parseLocalTimeMap(localtimeMap);
            } else {
                throw new RuntimeException("The combination of the time components is incorrect.");
            }
        }
    }

    public SLocalTime(Map<String, Number> localtimeMap) {
        // 至少指定hour
        if (!localtimeMap.containsKey("hour")) {
            throw new RuntimeException("The combination of the time components is incorrect.");
        }
        // 不能跨过粗粒度的时间单位指定细粒度的时间单位
        if (((localtimeMap.containsKey("nanosecond") | localtimeMap.containsKey("microsecond") | localtimeMap.containsKey("millisecond")) && !localtimeMap.containsKey("second"))
                | (localtimeMap.containsKey("second") && !localtimeMap.containsKey("minute"))
                | (localtimeMap.containsKey("minute") && !localtimeMap.containsKey("hour"))) {
            throw new RuntimeException("The combination of the time components is incorrect.");
        }
        this.localtime = this.parseLocalTimeMap(localtimeMap);
    }

    public Duration difference(SLocalTime localtime) {
        return Duration.between(this.localtime, localtime.getLocalTime());
    }

    public boolean isBefore(SLocalTime localtime) {
        return this.localtime.isBefore(localtime.getLocalTime());
    }

    public boolean isAfter(SLocalTime localtime) {
        return this.localtime.isAfter(localtime.getLocalTime());
    }


    public LocalTime getLocalTime() {
        return this.localtime;
    }

    private LocalTime parseLocalTimeMap(Map<String, Number> localtimeMap) {
        int hour = localtimeMap.getOrDefault("hour", 0).intValue();
        int minute = localtimeMap.getOrDefault("minute", 0).intValue();
        int second = localtimeMap.getOrDefault("second", 0).intValue();
        int millisecond = localtimeMap.getOrDefault("millisecond", 0).intValue();
        int microsecond = localtimeMap.getOrDefault("microsecond", 0).intValue();
        int nanosecond = localtimeMap.getOrDefault("nanosecond", 0).intValue() + millisecond * 1000000 + microsecond * 1000;
        return LocalTime.of(hour, minute, second, nanosecond);
    }
}
