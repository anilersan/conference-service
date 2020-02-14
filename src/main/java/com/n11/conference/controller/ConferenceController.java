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

import java.awt.*;

@RestController
public class ConferenceController {
    @Autowired
    private ConferenceService conferenceService;

    @RequestMapping(method = RequestMethod.POST, value = "/createConference", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createConference(@RequestBody AllEventsPojo allEvents){
        try {
            conferenceService.createConference(allEvents);
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getCause() + "" +  e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
