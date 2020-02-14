package com.n11.conference.controller;


import com.n11.conference.model.Track;
import com.n11.conference.repository.EventRepository;
import com.n11.conference.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/conferenceSchedule")
public class ScheduleController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TrackRepository trackRepository;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Track> getSchedules() {
        List<Track> tracks = new ArrayList<>();
        for (Track track : trackRepository.findAll()) {
            tracks.add(track);
        }
        return tracks;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Track getScheduleById(@PathVariable Long id) {
        return trackRepository.findById(id).orElse(new Track());
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteAllTracks() {
        eventRepository.deleteAll();
        trackRepository.deleteAll();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteTrackById(@PathVariable Long id) throws Exception {
        Track track = trackRepository.findById(id).orElse(null);
        if (track == null) {
            throw new Exception("Track with the given id does not exist");
        }
        eventRepository.deleteAll(track.getEvents());
        trackRepository.deleteById(id);
    }
}
