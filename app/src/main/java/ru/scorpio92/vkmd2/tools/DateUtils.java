package ru.scorpio92.vkmd2.tools;

import java.util.concurrent.TimeUnit;


public class DateUtils {

    public static String getHumanTimeFromMilliseconds(int milliseconds) {
        String s = "";
        try {
            long hours, min, sec;
            if (milliseconds >= 3600 * 1000) {
                hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
                min = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
                sec = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
                if(hours < 10)
                    s += "0" + Long.toString(hours) + ":";
                else
                    s += Long.toString(hours) + ":";
                if(min < 10)
                    s += "0" + Long.toString(min) + ":";
                else
                    s += Long.toString(min) + ":";
                if(sec < 10)
                    s += "0" + Long.toString(sec);
                else
                    s += Long.toString(sec);
            } else {
                min = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
                sec = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
                s += "00:";
                if(min < 10)
                    s += "0" + Long.toString(min) + ":";
                else
                    s += Long.toString(min) + ":";
                if(sec < 10)
                    s += "0" + Long.toString(sec);
                else
                    s += Long.toString(sec);
            }
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "00:00:00";
    }


}
