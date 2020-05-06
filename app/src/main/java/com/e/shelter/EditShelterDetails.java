package com.e.shelter;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.e.shelter.utilities.InfoWindowData;

public class EditShelterDetails extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_shelter_details);
        show_current_shelter_details();

    }
    public void show_current_shelter_details(){
        final InfoWindowData info = new InfoWindowData();
        EditText editText_name =(EditText) findViewById(R.id.editText_name);
        EditText editText_status =(EditText) findViewById(R.id.editText_status);
        EditText editText_address =(EditText) findViewById(R.id.editText_address);
        EditText editText_capacity =(EditText) findViewById(R.id.editText_capacity);
        editText_name.setText(info.getName());
        editText_address.setText(info.getAddress());
        editText_capacity.setText(info.getCapacity());
        editText_status.setText(info.getStatus());
    }


}
