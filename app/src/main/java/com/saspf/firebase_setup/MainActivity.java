package com.saspf.firebase_setup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FloatingActionButton add = findViewById(R.id.addNote);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Checking", Toast.LENGTH_SHORT).show();
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_note_dialog,null);
                TextInputEditText titleLayout,contentLayout;
                titleLayout  = view1.findViewById(R.id.titleLayout);
                contentLayout = view1.findViewById(R.id.contentLayout);
                TextInputEditText titleET, contentET;
                titleET = view1.findViewById(R.id.titleET);
                contentET = view1.findViewById(R.id.contentET);
               AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).setTitle("Add").setView(view1).setPositiveButton("add", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       if (Objects.requireNonNull(titleET.getText()).toString().isEmpty()){
                           titleLayout.setError("This field is required!");
                       } else if (Objects.requireNonNull(contentET.getText()).toString().isEmpty()) {
                           contentET.setError("This field is required!");
                       } else {
                           ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                           dialog.setMessage("Storing in Database.");
                           dialog.show();
                           Note note = new Note();
                           note.setTitle(titleET.getText().toString());
                           note.setContent(contentET.getText().toString());
                           database.getReference().child("notes").push().setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void unused) {
                                   dialog.dismiss();
                                   dialogInterface.dismiss();
                                   Toast.makeText(MainActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   dialog.dismiss();
                                   Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                   }
               }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                   }
               }).create();
               alertDialog.show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler);
        TextView empty = findViewById(R.id.empty);
        database.getReference().child("notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Note> arrayList = new ArrayList<>();
                for (DataSnapshot datasnapshot: snapshot.getChildren()){
                    Note note = datasnapshot.getValue(Note.class);
                    Objects.requireNonNull(note).setKey(datasnapshot.getKey());
                    arrayList.add(note);
                }
                if (arrayList.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                NoteAdapter adapter = new NoteAdapter(MainActivity.this,arrayList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Note note) {
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_note_dialog,null);
                        TextInputLayout titleLayout,contentLayout;
                        TextInputEditText titleET,contentET;

                        titleLayout = view.findViewById(R.id.titleLayout);
                        contentLayout = view.findViewById(R.id.contentLayout);
                        titleET = view.findViewById(R.id.titleET);
                        contentET = view.findViewById(R.id.contentET);

                        titleET.setText(note.getTitle());
                        contentET.setText(note.getContent());

                        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Edit")
                                .setView(view)
                                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (Objects.requireNonNull(titleET.getText()).toString().isEmpty()){
                                            titleLayout.setError("This field is required!");
                                        } else if (Objects.requireNonNull(contentET.getText()).toString().isEmpty()) {
                                            contentET.setError("This field is required!");
                                        } else {
                                            progressDialog.setMessage("Saving.");
                                            progressDialog.show();
                                            Note note1 = new Note();
                                            note1.setTitle(titleET.getText().toString());
                                            note1.setContent(contentET.getText().toString());
                                            database.getReference().child("notes").child(note.getKey()).setValue(note1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(MainActivity.this, "Saved Succesfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }).setNeutralButton("close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.setTitle("deleting");
                                        progressDialog.show();
                                        database.getReference().child("notes").child(note.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(MainActivity.this, "deleted Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }).create();
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}