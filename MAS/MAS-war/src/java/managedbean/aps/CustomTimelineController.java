/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aps;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.extensions.component.timeline.Timeline;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineGroup;
import org.primefaces.extensions.model.timeline.TimelineModel;

/**
 *
 * @author weiming
 */
@Named(value = "customTimelineController")
@ViewScoped
public class CustomTimelineController implements Serializable {  
  
    private TimelineModel model;  
    private String locale; // current locale as String, java.util.Locale is possible too.  
    private Date start;  
    private Date end;  
  
    @PostConstruct  
    protected void initialize() {  
        TimelineGroup timelineGroup = new TimelineGroup();
        TimelineEvent timeline = new TimelineEvent();
        
        // set initial start / end dates for the axis of the timeline  
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));  
        Date now = new Date();  
  
        cal.setTimeInMillis(now.getTime() - 4 * 60 * 60 * 1000);  
        start = cal.getTime();  
  
        cal.setTimeInMillis(now.getTime() + 8 * 60 * 60 * 1000);  
        end = cal.getTime();  
  
        // groups  
        String[] NAMES = new String[] {"Thomas", "Oleg", "Nilesh", "Mauricio", "Pavol", "Sudheer"};  
  
        // create timeline model  
        model = new TimelineModel();  
  
        for (String name : NAMES) {  
            now = new Date();  
            Date end = new Date(now.getTime() - 12 * 60 * 60 * 1000);  
  
            for (int i = 0; i < 5; i++) {  
                Date start = new Date(end.getTime() + Math.round(Math.random() * 5) * 60 * 60 * 1000);  
                end = new Date(start.getTime() + Math.round(4 + Math.random() * 5) * 60 * 60 * 1000);  
  
                long r = Math.round(Math.random() * 2);  
                String availability = (r == 0 ? "Unavailable" : (r == 1 ? "Available" : "Maybe"));  
  
                // create an event with content, start / end dates, editable flag, group name and custom style class  
                TimelineEvent event = new TimelineEvent(availability, start, end, true, name, availability.toLowerCase());  
                model.add(event);  
            }  
        }  
    }  
  
    public TimelineModel getModel() {  
        return model;  
    }  
  
    public String getLocale() {  
        return locale;  
    }  
  
    public void setLocale(String locale) {  
        this.locale = locale;  
    }  
  
    public Date getStart() {  
        return start;  
    }  
  
    public Date getEnd() {  
        return end;  
    }  
}  