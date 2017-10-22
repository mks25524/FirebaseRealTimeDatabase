package com.example.mks.firebaserealtimedatabase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddArtistTrackActivity extends AppCompatActivity {
    TextView tvArtistName;
    EditText etTrackName;
    SeekBar seekbarRating;
    Button btAddTrack;
    ListView listView;
    DatabaseReference databaseReferenceTrack;
    List<Track>trackList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artist_track);

        tvArtistName= (TextView) findViewById(R.id.textViewArtist);
        etTrackName= (EditText) findViewById(R.id.editTrackName);
        seekbarRating= (SeekBar) findViewById(R.id.seekBarRating);
        btAddTrack= (Button) findViewById(R.id.buttonAddTrack);
        listView= (ListView) findViewById(R.id.listViewTracks);

        trackList=new ArrayList<>();

        //for getting intent
        Intent intent=getIntent();
        String id=intent.getStringExtra(MainActivity.ARTIST_ID);
        String name=intent.getStringExtra(MainActivity.ARTIST_NAME);
        tvArtistName.setText(name);
        databaseReferenceTrack= FirebaseDatabase.getInstance().getReference("tracks").child(id);
        btAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrack();

            }
        });
    }
    public void saveTrack(){
        String trackName=etTrackName.getText().toString().trim();
        int rating=seekbarRating.getProgress();
        if(!TextUtils.isEmpty(trackName)){
            String id=databaseReferenceTrack.push().getKey();
            Track track=new Track(id,trackName,rating);
            databaseReferenceTrack.child(id).setValue(track);
            Toast.makeText(this,"Success add track",Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(this,"Add track won't be empty",Toast.LENGTH_SHORT).show();
        }
    }

// for showing listview
    @Override
    protected void onStart() {
        super.onStart();
        databaseReferenceTrack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trackList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Track track=snapshot.getValue(Track.class);
                    trackList.add(track);
                }
                TrackList tarcaklistAdapter=new TrackList(AddArtistTrackActivity.this,trackList);
                listView.setAdapter(tarcaklistAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
