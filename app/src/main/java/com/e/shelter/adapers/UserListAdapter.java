package com.e.shelter.adapers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.e.shelter.R;
import com.e.shelter.utilities.Review;
import com.e.shelter.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<User> {
    private static final String TAG = "CustomListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private UserListAdapter adapter;
    private ArrayList<User> cards;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView Name;
        TextView phone;
        TextView userEmail;
        TextView permession;
        MaterialButton blockedButton;
    }

    /**
     * Default constructor for the PersonListAdapter
     *
     * @param context
     * @param resource
     * @param objects
     */
    public UserListAdapter(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

        adapter = this;
        cards = objects;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final View result;
        final UserListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new UserListAdapter.ViewHolder();
            holder.Name= convertView.findViewById(R.id.userCardName);
            holder.permession = convertView.findViewById(R.id.userCardPermission);
            holder.phone =convertView.findViewById(R.id.userCardPhone);
            holder.userEmail = convertView.findViewById(R.id.userCardEmail);
            holder.blockedButton = convertView.findViewById(R.id.userCardBlockedButton);
            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (UserListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }
//        Animation animation = AnimationUtils.loadAnimation(mContext,
//                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
//        result.startAnimation(animation);
        lastPosition = position;

        final String userPhone = getItem(position).getPhoneNumber();
        final String userN = getItem(position).getName();
        final String userE = getItem(position).getEmail();
        final String userPermession = getItem(position).getPermission();
        holder.Name.setText(userN);
        holder.phone.setText(userPhone);
        holder.userEmail.setText(userE);
        holder.permession.setText(userPermession);
       // checkUserStatus(position,convertView,userE);


        final View finalConvertView = convertView;
        holder.blockedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockedSelectedUser(position, finalConvertView);
                adapter.notifyDataSetChanged();
            }
        });

        return convertView;
    }
    /*public void checkUserStatus(final int position, final View view, final String UserEmail){
        FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if( user.getEmail().equals(UserEmail)) {
                            if(user.getBlocked().equals(false)){
                                MaterialButton blockedBtn= view.findViewById(R.id.userCardBlockedButton);
                                blockedBtn.setText("Block");
                            }
                            else{
                                MaterialButton blockedBtn= view.findViewById(R.id.userCardBlockedButton);
                                blockedBtn.setText("Unblock");
                            }

                        }
                    }
                }
            }
        });



    }*/

    public void blockedSelectedUser(final int position, final View convertView) {

        FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if( user.getEmail().equals(cards.get(position).getEmail())) {
                            if(user.getBlocked().equals(false)){
                                user.setBlocked(true); //Use the setter
                                String id = document.getId();
                                FirebaseFirestore.getInstance().collection("Users").document(id).set(user); //Set student object
                                MaterialButton blockedBtn= convertView.findViewById(R.id.userCardBlockedButton);
                                blockedBtn.setText("Unblock");
                            }
                            else{
                                user.setBlocked(false); //Use the setter
                                String id = document.getId();
                                FirebaseFirestore.getInstance().collection("Users").document(id).set(user); //Set student object
                                MaterialButton blockedBtn= convertView.findViewById(R.id.userCardBlockedButton);
                                blockedBtn.setText("block");
                            }

                        }
                    }
                }
            }
        });



/*
        cards.remove(position);
        Toast.makeText(mContext, "Removed from review list", Toast.LENGTH_LONG).show();*/
    }





}
