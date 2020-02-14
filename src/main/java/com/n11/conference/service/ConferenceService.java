package com.n11.conference.service;

import com.n11.conference.component.SortedMapGenerator;
import com.n11.conference.enums.EventPeriod;
import com.n11.conference.enums.EventType;
import com.n11.conference.model.Event;
import com.n11.conference.model.Track;
import com.n11.conference.pojo.AllEventsPojo;
import com.n11.conference.pojo.TrackPojo;
import com.n11.conference.repository.EventRepository;
import com.n11.conference.repository.TrackRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.*;

@Service
public class ConferenceService {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SortedMapGenerator sortedMapGenerator;


    static final LocalTime morningEventsStartTime = LocalTime.of(9, 0);
    static final LocalTime morningEventsEndTime = LocalTime.of(12, 1);
    static final LocalTime afternoonEventsStartTime = LocalTime.of(13, 0);
    static final LocalTime afternoonEventsEndTime = LocalTime.of(17, 1);

    @Transactional
    public void createConference(AllEventsPojo allEventsPojo) throws Exception{
        TreeMap<Integer, List<String>> sortedEventMap = sortedMapGenerator.sortedEventMapper(allEventsPojo);
        List<TrackPojo> tracks = scheduleTracks(sortedEventMap);
    }

    private List<TrackPojo> scheduleTracks(TreeMap<Integer, List<String>> sortedEventMap){

        List<TrackPojo> tracks = new ArrayList<TrackPojo>();
        tracks.add(new TrackPojo());

        Track track = trackRepository.save(new Track());
        Event event;
        while(!sortedEventMap.values().isEmpty()){
            TrackPojo trackPojo = tracks.get(tracks.size() - 1);
            if (trackPojo.getMorningEvents().isEmpty()) {
                event = createEvent(sortedEventMap.get(sortedEventMap.firstKey()).get(0), morningEventsStartTime, sortedEventMap.firstKey());
            } else {
                Event previousEventInTrack = trackPojo.getMorningEvents().get(trackPojo.getMorningEvents().size() - 1);
                LocalTime startTime = previousEventInTrack.getStartTime().plusMinutes(previousEventInTrack.getDuration());
                if (isEventSettableForMorning(startTime, sortedEventMap.firstKey(), sortedEventMap.lastKey())) {
                    event = chooseOptimalEvent(sortedEventMap, startTime, EventPeriod.MORNING);
                } else {
                    if (trackPojo.getAfternoonEvents().isEmpty()) {
                        event = createEvent(sortedEventMap.get(sortedEventMap.firstKey()).get(0), afternoonEventsStartTime, sortedEventMap.firstKey());
                    } else {
                        previousEventInTrack = trackPojo.getAfternoonEvents().get(trackPojo.getAfternoonEvents().size() - 1);
                        startTime = previousEventInTrack.getStartTime().plusMinutes(previousEventInTrack.getDuration());
                        if (isEventSettableForAfternoon(startTime, sortedEventMap.firstKey(), sortedEventMap.lastKey())) {
                            event = chooseOptimalEvent(sortedEventMap, startTime, EventPeriod.AFTERNOON);
                        } else {
                            tracks.add(new TrackPojo());
                            track = trackRepository.save(new Track());
                            continue;
                        }
                    }
                }
            }
            sortedEventMap.get(event.getDuration()).remove(0);
            if (sortedEventMap.get(event.getDuration()).isEmpty()) {
                sortedEventMap.remove(event.getDuration());
            }
            event.setTrack(track);
            eventRepository.save(event);
            if (event.getStartTime().equals(afternoonEventsStartTime) ||  event.getStartTime().isAfter(afternoonEventsStartTime)){
                trackPojo.getAfternoonEvents().add(event);
            }
            else {
                trackPojo.getMorningEvents().add(event);
            }

            System.out.println(event.getTitle() + " " + event.getStartTime() + " " + event.getDuration() + " " + event.getTrack().getId());
        }
        return tracks;
    }

    private boolean isEventSettableForMorning(LocalTime startTime, Integer firstKeyDuration, Integer lastKeyDuration){
        if(startTime.plusMinutes(firstKeyDuration).isBefore(morningEventsEndTime) ||
                startTime.plusMinutes(lastKeyDuration).isBefore(morningEventsEndTime)){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isEventSettableForAfternoon(LocalTime startTime, Integer firstKeyDuration, Integer lastKeyDuration) {
        if ((startTime.plusMinutes(firstKeyDuration).isAfter(afternoonEventsStartTime)
                || startTime.plusMinutes(lastKeyDuration).isAfter(afternoonEventsStartTime))
                && startTime.plusMinutes(lastKeyDuration).isBefore(afternoonEventsEndTime)) {
            return true;
        } else {
            return false;
        }
    }

    private Event chooseOptimalEvent(TreeMap<Integer, List<String>> sortedMap, LocalTime startTime, EventPeriod eventPeriod){
        for(Integer key : sortedMap.keySet()){
            if(EventPeriod.MORNING.equals(eventPeriod) && startTime.plusMinutes(key).isBefore(morningEventsEndTime)) {
                return createEvent(sortedMap.get(key).get(0), startTime, key);
            }
            else if(EventPeriod.AFTERNOON.equals(eventPeriod) && startTime.plusMinutes(key).isBefore(afternoonEventsEndTime)) {
                return createEvent(sortedMap.get(key).get(0), startTime, key);
            }
        }
        return null;
    }

    private Event createEvent(String title, LocalTime startTime, Integer duration){
        Event event = new Event();
        event.setTitle(title);
        event.setStartTime(startTime);
        event.setDuration(duration);
        if(event.getDuration() <= 5){
            event.setEventType(EventType.LIGHTNING);
        }
        else{
            event.setEventType(EventType.PRESENTATION);
        }
        return  event;
    }
}
