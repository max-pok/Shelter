package com.e.shelter;
import android.content.Context;
import android.widget.EditText;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)

public class LoginTest {
    LoginActivity mActivity = new LoginActivity();
    private EditText username;
    private EditText password;


    public void testPreconditions() {
        username = (EditText) mActivity.findViewById(R.id.emailInput);
        password = (EditText) mActivity.findViewById(R.id.passowrdInput);
        assertNotNull(username);
        assertNotNull(password);
    }


    public void testText() {
        assertEquals("",username.getText());
        assertEquals("", password.getText());
    }
}