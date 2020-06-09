package com.e.shelter.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.e.shelter.R;
import com.e.shelter.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<User> {
    /**
     * class UserListAdapter fields
     */
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
        TextView permissionTextView;
        MaterialButton blockedButton;
        MaterialButtonToggleGroup permissionToggle;
        int currentCheckedPermission;
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

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return View
     */
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final View result;
        final UserListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new UserListAdapter.ViewHolder();
            holder.Name = convertView.findViewById(R.id.userCardName);
            holder.permissionTextView = convertView.findViewById(R.id.userCardPermission);
            holder.phone = convertView.findViewById(R.id.userCardPhone);
            holder.userEmail = convertView.findViewById(R.id.userCardEmail);
            holder.blockedButton = convertView.findViewById(R.id.userCardBlockedButton);
            holder.permissionToggle = convertView.findViewById(R.id.permission_toggle_content_users);
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
        holder.permissionTextView.setText(userPermession);

        //Setting the permission toggle
        if (holder.permissionTextView.getText().equals("user")) {
            holder.currentCheckedPermission = R.id.user_permission_button_content_users;
            holder.permissionToggle.check(holder.currentCheckedPermission);
        } else {
            holder.currentCheckedPermission = R.id.admin_permission_button_content_users;
            holder.permissionToggle.check(holder.currentCheckedPermission);
        }

        if (getItem(position).getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            holder.permissionToggle.setVisibility(View.INVISIBLE);
            holder.blockedButton.setVisibility(View.INVISIBLE);
        }

        final View finalConvertView = convertView;
        holder.blockedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockedSelectedUser(position, finalConvertView);
                adapter.notifyDataSetChanged();
            }
        });
        holder.permissionToggle.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (holder.currentCheckedPermission != checkedId)
                        switch (group.getCheckedButtonId()) {
                            case R.id.user_permission_button_content_users:
                                changePermission(holder, position, "user");
                                return;
                            case R.id.admin_permission_button_content_users:
                                changePermission(holder, position, "admin");
                                return;
                            default:
                                return;
                        }
                }  else {
                    if (-1 == group.getCheckedButtonId()) {
                        //All buttons are unselected
                        //So now we will select the button which was unselected right now
                        group.check(checkedId);
                    }
                }
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

    /**
     * function that blocks user and updates the database
     * @param position
     * @param convertView
     */
    public void blockedSelectedUser(final int position, final View convertView) {

        FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (user.getEmail().equals(cards.get(position).getEmail())) {
                            if (user.getBlocked().equals(false)) {
                                user.setBlocked(true); //Use the setter
                                String id = document.getId();
                                FirebaseFirestore.getInstance().collection("Users").document(id).set(user); //Set student object
                                MaterialButton blockedBtn = convertView.findViewById(R.id.userCardBlockedButton);
                                blockedBtn.setText("Unblock");
                            } else {
                                user.setBlocked(false); //Use the setter
                                String id = document.getId();
                                FirebaseFirestore.getInstance().collection("Users").document(id).set(user); //Set student object
                                MaterialButton blockedBtn = convertView.findViewById(R.id.userCardBlockedButton);
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

    public void changePermission(ViewHolder holder, final int position, final String permission) {
        FirebaseFirestore.getInstance().collection("Users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.get("email").equals(getItem(position).getEmail())) {
                            FirebaseFirestore.getInstance().collection("Users").document(documentSnapshot.getId()).update("permission", permission);
                            return;
                        }
                    }
                }
            }
        });
        if (permission.equals("user")) holder.currentCheckedPermission = R.id.user_permission_button_content_users;
        else holder.currentCheckedPermission = R.id.admin_permission_button_content_users;
    }
}