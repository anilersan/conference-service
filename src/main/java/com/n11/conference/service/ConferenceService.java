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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
public class ConferenceService {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SortedMapGenerator sortedMapGenerator;

    private static final LocalTime morningEventsStartTime = LocalTime.of(9, 0);
    private static final LocalTime morningEventsEndTime = LocalTime.of(12, 1);
    private static final LocalTime afternoonEventsStartTime = LocalTime.of(13, 0);
    private static final LocalTime afternoonEventsEndTime = LocalTime.of(17, 1);
    private static final LocalTime networkingStartTime = LocalTime.of(16, 0);


    @Transactional
    public List<TrackPojo> createConference(AllEventsPojo allEventsPojo) throws Exception {
        TreeMap<Integer, List<String>> sortedEventMap = sortedMapGenerator.sortedEventMapper(allEventsPojo);
        List<TrackPojo> trackPojos = scheduleTracks(sortedEventMap);
        return addNetworkingEvents(trackPojos);
    }

    private List<TrackPojo> scheduleTracks(TreeMap<Integer, List<String>> sortedEventMap) {

        List<TrackPojo> tracks = new ArrayList<TrackPojo>();
        tracks.add(new TrackPojo());

        Track track = trackRepository.save(new Track());
        Event event;
        while (!sortedEventMap.values().isEmpty()) {
            TrackPojo trackPojo = tracks.get(tracks.size() - 1);
            if (trackPojo.getMorningEvents().isEmpty()) {
                event = createEvent(sortedEventMap.get(sortedEventMap.firstKey()).get(0), morningEventsStartTime, sortedEventMap.firstKey());
                handleEventGeneration(sortedEventMap, event, track, trackPojo);
                continue;
            }

            Event previousEventInTrack = trackPojo.getMorningEvents().get(trackPojo.getMorningEvents().size() - 1);
            LocalTime startTime = previousEventInTrack.getStartTime().plusMinutes(previousEventInTrack.getDuration());
            if (isEventSettableForMorning(startTime, sortedEventMap.firstKey(), sortedEventMap.lastKey())) {
                event = chooseOptimalEvent(sortedEventMap, startTime, EventPeriod.MORNING);
                handleEventGeneration(sortedEventMap, event, track, trackPojo);
                continue;
            }

            if (trackPojo.getAfternoonEvents().isEmpty()) {
                event = createEvent(sortedEventMap.get(sortedEventMap.firstKey()).get(0), afternoonEventsStartTime, sortedEventMap.firstKey());
                handleEventGeneration(sortedEventMap, event, track, trackPojo);
                continue;
            }

            previousEventInTrack = trackPojo.getAfternoonEvents().get(trackPojo.getAfternoonEvents().size() - 1);
            startTime = previousEventInTrack.getStartTime().plusMinutes(previousEventInTrack.getDuration());
            if (isEventSettableForAfternoon(startTime, sortedEventMap.firstKey(), sortedEventMap.lastKey())) {
                event = chooseOptimalEvent(sortedEventMap, startTime, EventPeriod.AFTERNOON);
                handleEventGeneration(sortedEventMap, event, track, trackPojo);
                continue;
            }

            tracks.add(new TrackPojo());
            track = trackRepository.save(new Track());
        }
        return tracks;
    }

    private boolean isEventSettableForMorning(LocalTime startTime, Integer firstKeyDuration, Integer lastKeyDuration) {
        if (startTime.plusMinutes(firstKeyDuration).isBefore(morningEventsEndTime) ||
                startTime.plusMinutes(lastKeyDuration).isBefore(morningEventsEndTime)) {
            return true;
        } else {
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

    private Event chooseOptimalEvent(TreeMap<Integer, List<String>> sortedMap, LocalTime startTime, EventPeriod eventPeriod) {
        for (Integer key : sortedMap.keySet()) {
            if (EventPeriod.MORNING.equals(eventPeriod) && startTime.plusMinutes(key).isBefore(morningEventsEndTime)) {
                return createEvent(sortedMap.get(key).get(0), startTime, key);
            } else if (EventPeriod.AFTERNOON.equals(eventPeriod) && startTime.plusMinutes(key).isBefore(afternoonEventsEndTime)) {
                return createEvent(sortedMap.get(key).get(0), startTime, key);
            }
        }
        return null;
    }

    private Event createEvent(String title, LocalTime startTime, Integer duration) {
        Event event = new Event();
        event.setTitle(title);
        event.setStartTime(startTime);
        event.setDuration(duration);
        if (event.getDuration() <= 5) {
            event.setEventType(EventType.LIGHTNING);
        } else {
            event.setEventType(EventType.PRESENTATION);
        }
        return event;
    }

    private Event createNetworkingEvent(String title, LocalTime startTime) {
        Event event = new Event();
        event.setTitle(title);
        event.setStartTime(startTime);
        event.setEventType(EventType.NETWORKING);
        return event;
    }

    public void removeEventFromMap(TreeMap<Integer, List<String>> sortedMap, Event event) {
        sortedMap.get(event.getDuration()).remove(0);
        if (sortedMap.get(event.getDuration()).isEmpty()) {
            sortedMap.remove(event.getDuration());
        }
    }

    public void handleEventGeneration(TreeMap<Integer, List<String>> sortedMap, Event event, Track track, TrackPojo trackPojo) {
        removeEventFromMap(sortedMap, event);
        event.setTrack(track);
        eventRepository.save(event);
        if (event.getStartTime().equals(afternoonEventsStartTime) || event.getStartTime().isAfter(afternoonEventsStartTime)) {
            trackPojo.getAfternoonEvents().add(event);
        } else {
            trackPojo.getMorningEvents().add(event);
        }
    }

    public List<TrackPojo> addNetworkingEvents(List<TrackPojo> trackPojos) {
        for (TrackPojo trackPojo : trackPojos) {
            if (trackPojo.getAfternoonEvents() == null || trackPojo.getAfternoonEvents().isEmpty()) {
                continue;
            }
            Event lastEvent = trackPojo.getAfternoonEvents().get(trackPojo.getAfternoonEvents().size() - 1);
            LocalTime networkingPossibleStartTime = lastEvent.getStartTime().plusMinutes(lastEvent.getDuration());
            if (networkingPossibleStartTime.isBefore(networkingStartTime)) {
                Event event = createNetworkingEvent("Networking", networkingStartTime);
                event.setTrack(lastEvent.getTrack());
                eventRepository.save(event);
                trackPojo.getAfternoonEvents().add(event);
            } else if (networkingPossibleStartTime.isAfter(networkingStartTime) && networkingPossibleStartTime.isBefore(afternoonEventsEndTime)) {
                Event event = createNetworkingEvent("Networking", networkingPossibleStartTime);
                event.setTrack(lastEvent.getTrack());
                eventRepository.save(event);
                trackPojo.getAfternoonEvents().add(event);
            } else {
                Event event = createNetworkingEvent("Networking", networkingStartTime);
                event.setTrack(lastEvent.getTrack());
                eventRepository.save(event);
                trackPojo.getAfternoonEvents().add(event);
            }

        }

        return trackPojos;
    }
}
