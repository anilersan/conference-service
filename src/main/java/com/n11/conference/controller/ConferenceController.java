package com.n11.conference.controller;

import com.n11.conference.pojo.AllEventsPojo;
import com.n11.conference.service.ConferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConferenceController {
    @Autowired
    private ConferenceService conferenceService;

    @RequestMapping(method = RequestMethod.POST, value = "/createConference", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createConference(@RequestBody AllEventsPojo allEvents) {
        try {
            return new ResponseEntity<>(conferenceService.createConference(allEvents), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getCause() + "" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
