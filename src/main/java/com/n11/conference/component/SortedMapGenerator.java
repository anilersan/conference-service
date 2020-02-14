package com.n11.conference.component;

import com.n11.conference.pojo.AllEventsPojo;
import com.n11.conference.pojo.EventPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SortedMapGenerator {

    public static final Integer lightningDuration = 5;

    @Autowired
    private Validator validator;

    public TreeMap<Integer, List<String>> sortedEventMapper(AllEventsPojo allEventsPojo) throws Exception{
        TreeMap<Integer, List<String>> sortedEvents = new TreeMap<Integer, List<String>>(Comparator.reverseOrder());
        for(EventPojo eventPojo : allEventsPojo.getEvents()){
            validator.validate(eventPojo);
            Integer duration = durationConverter(eventPojo.getDuration());
            List<String> titles = sortedEvents.getOrDefault(duration, new ArrayList<>());
            titles.add(eventPojo.getTitle());
            sortedEvents.put(duration, titles);
        }
        return sortedEvents;
    }


    private Integer durationConverter(String duration){
        if("lightning".equalsIgnoreCase(duration)){
            return lightningDuration;
        }
        else{
            return Integer.valueOf(duration);
        }
    }
}
