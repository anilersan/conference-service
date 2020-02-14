package com.n11.conference.component;

import com.n11.conference.pojo.AllEventsPojo;
import com.n11.conference.pojo.EventPojo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SortedMapGeneratorTest {

    @Autowired
    private SortedMapGenerator sortedMapGenerator;

    @MockBean
    private Validator validator;

    @Test
    public void generatedSortedMap() throws Exception {
        AllEventsPojo allEventsPojo = new AllEventsPojo();
        List<EventPojo> eventPojos = new ArrayList<>();

        EventPojo eventPojo = new EventPojo();
        eventPojo.setDuration("60");
        eventPojo.setTitle("test1");
        eventPojos.add(eventPojo);

        eventPojo = new EventPojo();
        eventPojo.setDuration("90");
        eventPojo.setTitle("test2");
        eventPojos.add(eventPojo);

        eventPojo = new EventPojo();
        eventPojo.setDuration("30");
        eventPojo.setTitle("test3");
        eventPojos.add(eventPojo);

        eventPojo = new EventPojo();
        eventPojo.setDuration("lightning");
        eventPojo.setTitle("test4");
        eventPojos.add(eventPojo);

        allEventsPojo.setEvents(eventPojos);

        TreeMap<Integer, List<String>> sortedTreeMap = sortedMapGenerator.sortedEventMapper(allEventsPojo);

        Assert.assertThat(sortedTreeMap.firstKey(), Matchers.is(90));
        Assert.assertThat(sortedTreeMap.lastKey(), Matchers.is(5));
    }

}