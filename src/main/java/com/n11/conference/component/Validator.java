package com.n11.conference.component;

import com.n11.conference.pojo.EventPojo;
import org.springframework.stereotype.Component;
/*
All validations for generating a map
 */
@Component
public class Validator {

    public static final Integer maxEventDuration = 240;

    public void validate(EventPojo eventPojo) throws Exception {
        if (eventPojo == null) {
            throw new Exception("Event can not be null");
        } else if (eventPojo.getDuration() == null || eventPojo.getTitle() == null) {
            throw new Exception("Event members can not be null");
        } else if (!"lightning".equalsIgnoreCase(eventPojo.getDuration()) && Integer.valueOf(eventPojo.getDuration()) > maxEventDuration) {
            throw new Exception("Event duration can not be greater than 240 minutes");
        }
    }

}
