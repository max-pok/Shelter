package com.e.shelter;

import com.e.shelter.utilities.StatusValidator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EditShelterTest {

    @Test
    public void StatusValidTest() {
        assertTrue(StatusValidator.isValidStatus("open"));
        assertTrue(StatusValidator.isValidStatus("close"));
    }

    @Test
    public void StatusInvalidTest() {
        assertFalse(StatusValidator.isValidStatus("Test"));
    }

}
