/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.util.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author weiming
 */
public class DateHelper {

    public static void setToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
    }

    public static void setToEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    public static Date convertStringToDate(String date){
        try {
            return new SimpleDateFormat("EE, dd MMM yyyy").parse(date);
        } catch (ParseException ex) {
            return new Date();
        }
    }
    
    public static String convertDateTime(Date date) {
        return new SimpleDateFormat("EE, dd MMM yyyy").format(date);
    }

    public static long calcDateDiff(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
    }

    public static String convertMSToHourMinute(long ms) {
        int hours = (int) ((ms / (1000 * 60 * 60)) % 24);
        int minutes = (int) (((ms - hours * 1000 * 60 * 60) / (1000 * 60)) % 60);
        return hours + "h" + minutes + "m";
    }
}
