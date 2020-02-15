package com.n11.conference.service;

import com.n11.conference.component.SortedMapGenerator;
import com.n11.conference.model.Track;
import com.n11.conference.pojo.AllEventsPojo;
import com.n11.conference.pojo.EventPojo;
import com.n11.conference.pojo.TrackPojo;
import com.n11.conference.repository.EventRepository;
import com.n11.conference.repository.TrackRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import sun.reflect.generics.tree.Tree;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConferenceServiceTest {

    @Autowired
    private ConferenceService conferenceService;

    @MockBean
    private TrackRepository trackRepository;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private SortedMapGenerator sortedMapGenerator;

    @Test
    public void conferenceService() throws Exception {

        TreeMap<Integer, List<String>> treeMap = new TreeMap<>(Comparator.reverseOrder());
        AllEventsPojo allEventsPojo = new AllEventsPojo();
        List<EventPojo> eventPojos = new ArrayList<>();

        List<String> titles = new ArrayList<>();
        titles.add("title1");
        titles.add("title2");
        titles.add("title3");
        treeMap.put(60, titles);

        titles = new ArrayList<>();
        titles.add("title4");
        treeMap.put(90, titles);

        titles = new ArrayList<>();
        titles.add("title5");
        titles.add("title6");
        treeMap.put(45, titles);

        titles = new ArrayList<>();
        titles.add("title7");
        treeMap.put(5, titles);

        titles = new ArrayList<>();
        titles.add("title8");
        treeMap.put(15, titles);

        Mockito.when(sortedMapGenerator.sortedEventMapper(allEventsPojo)).thenReturn(treeMap);
        List<TrackPojo> trackPojos = conferenceService.createConference(allEventsPojo);

        Assert.assertThat(trackPojos.size(), Matchers.is(1));
        Assert.assertThat(trackPojos.get(0).getMorningEvents().size(), Matchers.is(4));
        Assert.assertThat(trackPojos.get(0).getAfternoonEvents().size(), Matchers.is(5));
    }

    @Test
    public void conferenceServiceAdditionalTracks() throws Exception {

        TreeMap<Integer, List<String>> treeMap = new TreeMap<>(Comparator.reverseOrder());
        AllEventsPojo allEventsPojo = new AllEventsPojo();
        List<EventPojo> eventPojos = new ArrayList<>();

        List<String> titles = new ArrayList<>();
        titles.add("title1");
        titles.add("title2");
        titles.add("title3");
        treeMap.put(60, titles);

        titles = new ArrayList<>();
        titles.add("title4");
        treeMap.put(90, titles);

        titles = new ArrayList<>();
        titles.add("title5");
        titles.add("title6");
        treeMap.put(45, titles);

        titles = new ArrayList<>();
        titles.add("title7");
        treeMap.put(5, titles);

        titles = new ArrayList<>();
        titles.add("title8");
        treeMap.put(15, titles);

        titles = new ArrayList<>();
        titles.add("title9");
        titles.add("title10");
        titles.add("title11");
        titles.add("title12");
        titles.add("title13");
        treeMap.put(30, titles);



        Mockito.when(sortedMapGenerator.sortedEventMapper(allEventsPojo)).thenReturn(treeMap);
        List<TrackPojo> trackPojos = conferenceService.createConference(allEventsPojo);

        Assert.assertThat(trackPojos.size(), Matchers.is(2));
        Assert.assertThat(trackPojos.get(0).getMorningEvents().size(), Matchers.is(3));
        Assert.assertThat(trackPojos.get(0).getAfternoonEvents().size(), Matchers.is(6));
        Assert.assertThat(trackPojos.get(1).getMorningEvents().size(), Matchers.is(5));
    }

}