package me.vitez.grupem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by mitchellvitez on 10/15/2016.
 */

public class ViewActivity extends Activity {

    private ArrayList<String> userEmailList;

    private DatabaseReference mDatabase;
    private DatabaseReference mGroupsRef;
    private TableLayout table;
    private String groupName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupName = getIntent().getStringExtra("groupName");
        userEmailList = new ArrayList<String>();

        setContentView(R.layout.view);
        mAuth = FirebaseAuth.getInstance();

        table = (TableLayout) findViewById(R.id.tableView);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(groupName);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mGroupsRef = mDatabase.child("groups");

        final Button joinButton = (Button) findViewById(R.id.join_button);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userEmailList.contains(mAuth.getCurrentUser().getEmail())) {
                    userEmailList.remove(mAuth.getCurrentUser().getEmail());
                    joinButton.setText("Join Group");
                }
                else {
                    userEmailList.add(mAuth.getCurrentUser().getEmail());
                    joinButton.setText("Leave Group");
                }
                mGroupsRef.child(groupName).setValue(userEmailList);
            }
        });

        mGroupsRef.orderByKey().equalTo(groupName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                table.removeAllViews();
                for(final DataSnapshot usersSnapshot : snapshot.getChildren()) {
                    ArrayList<String> userEmails = (ArrayList<String>) usersSnapshot.getValue();
                    userEmailList = userEmails;
                    boolean inGroup = false;
                    for (String userEmail : userEmails) {
                        if (userEmail.equals(mAuth.getCurrentUser().getEmail())) {
                            inGroup = true;
                        }
                        LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.view_row, null);
                        final TextView userEmailView = (TextView) row.findViewById(R.id.textView);
                        userEmailView.setText(userEmail.toString());
                        table.addView(row);
                    }
                    if (inGroup) {
                        joinButton.setText("Leave Group");
                    } else {
                        joinButton.setText("Join Group");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("database", "the read failed");
            }
        });
    }

}
