package com.example.mks.firebaserealtimedatabase;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public  static  final String ARTIST_ID="artist id";
    public  static  final String ARTIST_NAME="artist Name";
    EditText editTextName;
    Spinner spinnerGenre;
    Button buttonAddArtist;
    ListView listViewArtists;
    DatabaseReference databaseArtists;
    List<Artist> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = (EditText) findViewById(R.id.editTextName);
        spinnerGenre = (Spinner) findViewById(R.id.spinnerGenres);
      listViewArtists = (ListView) findViewById(R.id.listViewArtists);
        databaseArtists= FirebaseDatabase.getInstance().getReference("artists");
        artistList=new ArrayList<>();

        buttonAddArtist = (Button) findViewById(R.id.buttonAddArtist);
        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArtist();

            }
        });
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist=artistList.get(i);
                Intent intent=new Intent(getApplicationContext(),AddArtistTrackActivity.class);
                intent.putExtra(ARTIST_ID,artist.getArtistId());
                intent.putExtra(ARTIST_NAME,artist.getArtistName());
                startActivity(intent);
            }
        });
        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist=artistList.get(i);
                showUpdateDialog(artist.getArtistId(),artist.getArtistName());
                return false;
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                artistList.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Artist artist = postSnapshot.getValue(Artist.class);
                    //adding artist to the list
                    artistList.add(artist);
                }

                //creating adapter
                ArtistList artistAdapter = new ArtistList(MainActivity.this, artistList);
                //attaching adapter to the listview
                listViewArtists.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void showUpdateDialog(final String artistId, String artistName){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater layoutInflater= getLayoutInflater();
        final View dialogView=layoutInflater.inflate(R.layout.update_dialogue,null);
        builder.setView(dialogView);
        final EditText editTextName=(EditText)dialogView.findViewById(R.id.editTextName);
       // final TextView textViewName=(TextView)dialogView.findViewById(R.id.tvName);
        final Button buttonUpdate=(Button)dialogView.findViewById(R.id.buttonUpdateArtist);
        final Spinner spinner=(Spinner)dialogView.findViewById(R.id.spinnerGenress);
        builder.setTitle("Update Artist"+artistName);
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String name=editTextName.getText().toString().trim();
               String genre=spinner.getSelectedItem().toString();
               if(TextUtils.isEmpty(name)){
                   editTextName.setError("Name required");
                   return;
               }
               updateArtist(artistId,name,genre);
               alertDialog.dismiss();


           }
       });





    }
    private boolean updateArtist(String id,String name,String genre){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("artists").child(id);
        Artist artist= new Artist(id,name,genre);
        databaseReference.setValue(artist);
        Toast.makeText(this,"Update successfully",Toast.LENGTH_LONG).show();
        return true;
    }
    private void addArtist() {
        //getting the values to save
        String name = editTextName.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();

        //checking if the value is provided
        if (!TextUtils.isEmpty(name)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Artist
            String id = databaseArtists.push().getKey();

            //creating an Artist Object
            Artist artist = new Artist(id, name, genre);

            //Saving the Artist
            databaseArtists.child(id).setValue(artist);

            //setting edittext to blank again
            editTextName.setText("");

            //displaying a success toast
            Toast.makeText(this, "Artist added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }
}
