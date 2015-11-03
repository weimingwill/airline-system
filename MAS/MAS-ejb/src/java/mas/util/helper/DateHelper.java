/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.util.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    
    public static String convertDateTime(Date date) {
        return new SimpleDateFormat("EE, dd MMM yyyy").format(date);
    }
}
