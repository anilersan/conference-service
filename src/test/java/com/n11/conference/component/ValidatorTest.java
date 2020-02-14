package com.n11.conference.component;

import com.n11.conference.model.Event;
import com.n11.conference.pojo.EventPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    public void shouldValidate() throws Exception {
        EventPojo eventPojo = new EventPojo();
        eventPojo.setTitle("Test");
        eventPojo.setDuration("60");
        validator.validate(eventPojo);
    }

    @Test(expected = Exception.class)
    public void validateNullEvent() throws Exception {
        validator.validate(null);
    }

    @Test(expected = Exception.class)
    public void validateNullDuration() throws Exception {
        EventPojo eventPojo = new EventPojo();
        eventPojo.setTitle("Test");
        eventPojo.setDuration(null);
        validator.validate(eventPojo);
    }

    @Test(expected = Exception.class)
    public void validateNullTitle() throws Exception {
        EventPojo eventPojo = new EventPojo();
        eventPojo.setTitle(null);
        eventPojo.setDuration("60");
        validator.validate(eventPojo);
    }

    @Test
    public void validateLightningEvent() throws Exception {
        EventPojo eventPojo = new EventPojo();
        eventPojo.setTitle("Test");
        eventPojo.setDuration("lightning");
        validator.validate(eventPojo);
    }

    @Test(expected = Exception.class)
    public void validateMaxDuration() throws Exception {
        EventPojo eventPojo = new EventPojo();
        eventPojo.setTitle("Test");
        eventPojo.setDuration("250");
        validator.validate(eventPojo);
    }

    @Test(expected = Exception.class)
    public void validateNonNumberDuration() throws Exception {
        EventPojo eventPojo = new EventPojo();
        eventPojo.setTitle("Test");
        eventPojo.setDuration("test");
        validator.validate(eventPojo);
    }

}