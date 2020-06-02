package com.e.shelter;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.e.shelter.adapers.UserListAdapter;
import com.e.shelter.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowUsersActivity extends MainActivity {
    private ListView userListView;
    private ArrayList<User> userArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_user_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>User List</font>"));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        userListView = findViewById(R.id.user_list_view);
        retrieveShowUsers();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void retrieveShowUsers() {
        firebaseFirestore.collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userArrayList.add(user);
                            }
                        } else {
                            Log.d("Show Users Class", "Error getting documents: ", task.getException());
                        }
                        UserListAdapter adapter = new UserListAdapter(getBaseContext(), R.layout.content_users, userArrayList);
                        userListView.setAdapter(adapter);
                    }
                });
    }


}
