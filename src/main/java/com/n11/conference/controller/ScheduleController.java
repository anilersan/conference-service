package com.n11.conference.controller;


import com.n11.conference.model.Track;
import com.n11.conference.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ScheduleController {

    @Autowired
    private TrackRepository trackRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/conferenceSchedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Track> getSchedule() {
        List<Track> tracks = new ArrayList<>();
        trackRepository.findAll().forEach(tracks::add);
        return tracks;
    }
}
