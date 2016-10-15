package me.vitez.grupem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mitchellvitez on 10/14/2016.
 */

public class GroupsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference mGroupsRef;
    private TableLayout table;
    private FirebaseAuth mAuth;
    private HashMap<String, ArrayList<String>> groupNames;
    private String newGroupName;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups);

        groupNames = new HashMap<>();

        table = (TableLayout) findViewById(R.id.tableView);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mGroupsRef = mDatabase.child("groups");

        final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupsActivity.this);
                builder.setTitle("New Group");
                final EditText input = new EditText(GroupsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input)
                        .setPositiveButton("Add Group", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                newGroupName = input.getText().toString();
                                groupNames.put(newGroupName, new ArrayList<>(Arrays.asList(mAuth.getCurrentUser().getEmail())));
                                mGroupsRef.setValue(groupNames);
                            }
                        });
                builder.show();
            }
        });

        mGroupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                table.removeAllViews();
                groupNames = new HashMap<>();
                for(final DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    groupNames.put(groupSnapshot.getKey(), (ArrayList<String>) groupSnapshot.getValue());
                    LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.groups_row, null);
                    final TextView groupNameView = (TextView) row.findViewById(R.id.textView);
                    Button button = (Button) row.findViewById(R.id.button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(GroupsActivity.this, ViewActivity.class);
                            intent.putExtra("groupName", groupSnapshot.getKey());
                            startActivity(intent);
                        }
                    });
                    groupNameView.setText(groupSnapshot.getKey());
                    table.addView(row);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("database", "the read failed");
            }
        });
    }
}



