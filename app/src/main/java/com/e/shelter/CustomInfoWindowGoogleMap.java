package com.e.shelter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.shelter.utilities.InfoWindowData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import static androidx.core.content.ContextCompat.startActivity;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    public Context context;

    public Context getCtx(){
        return this.context;
    }

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        final View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.info_window, null);

        TextView name_tv = view.findViewById(R.id.name);
        TextView address_tv = view.findViewById(R.id.address);

        TextView status_tv = view.findViewById(R.id.status);
        TextView capacity_tv = view.findViewById(R.id.capacity);
        TextView rating_tv = view.findViewById(R.id.rating);

        name_tv.setText(marker.getTitle());
        address_tv.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        status_tv.setText(infoWindowData.getStatus());
        capacity_tv.setText(infoWindowData.getCapacity());
        rating_tv.setText(infoWindowData.getRating());
        Button edit = (Button) view.findViewById(R.id.admin_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("click");
            }

        });
                return view;
    }

    public void show_edit_details(){

    }

}
