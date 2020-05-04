package com.e.shelter;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import static com.e.shelter.R.layout.edit_shelter_details;

public class EditShelterDetails extends AppCompatActivity {
    public static String name;
    public static String status;
    public static String address;
    public static String capacity;
    public EditText editText_name =(EditText) findViewById(R.id.editText_name);
    public EditText editText_status =(EditText) findViewById(R.id.editText_status);
    public EditText editText_address =(EditText) findViewById(R.id.editText_status);
    public EditText editText_capacity =(EditText) findViewById(R.id.editText_capacity);

    EditShelterDetails(String name,String address,String status,String capacity){
        this.name = name;
        this.address=address;
        this.status=status;
        this.capacity=capacity;

    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(edit_shelter_details);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //show_current_shelter_details();

    }
    public void show_current_shelter_details(){
        editText_name.setText(name);
        editText_address.setText(address);
        editText_capacity.setText(capacity);
        editText_status.setText(status);
    }


}
