package com.example.campusadmin.faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.campusadmin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateFaculty extends AppCompatActivity {
    private FloatingActionButton fab_add_faculty;
    private RecyclerView rv_cs_faculty,rv_mba_faculty,rv_phd_faculty;
    private LinearLayout ll_no_cs_data,ll_no_mba_data,ll_no_phd_data;
    private List<FacultyData> cs_faculty_list,mba_faculty_list,phd_faculty_list;
    private DatabaseReference reference,cur_ref;
    private FacultyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);
        fab_add_faculty=findViewById(R.id.fab_add_faculty);
        rv_cs_faculty=findViewById(R.id.rv_cs_faculty);
        rv_mba_faculty=findViewById(R.id.rv_mba_faculty);
        rv_phd_faculty=findViewById(R.id.rv_phd_faculty);
        ll_no_cs_data=findViewById(R.id.cv_no_cs_faculty_data);
        ll_no_mba_data=findViewById(R.id.cv_no_mba_data);
        ll_no_phd_data=findViewById(R.id.cv_no_phd_faculty);

        reference= FirebaseDatabase.getInstance().getReference().child("Faculty");
        update_cs_rv();
        update_mba_rv();
        update_phd_rv();

        fab_add_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateFaculty.this,AddFaculty.class));
            }
        });
    }

    private void update_cs_rv() {
       cur_ref=reference.child("Computer Science");
       cur_ref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
              cs_faculty_list=new ArrayList<>();
              if(!snapshot.exists())
              {
                  ll_no_cs_data.setVisibility(View.VISIBLE);
                  rv_cs_faculty.setVisibility(View.GONE);

              }
              else
              {
                  ll_no_cs_data.setVisibility(View.GONE);
                  rv_cs_faculty.setVisibility(View.VISIBLE);
                  for(DataSnapshot snapshot1:snapshot.getChildren())
                  {
                    FacultyData data=snapshot1.getValue(FacultyData.class);
                    cs_faculty_list.add(data);
                  }
                  rv_cs_faculty.setHasFixedSize(true);
                  rv_cs_faculty.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                  adapter=new FacultyAdapter(cs_faculty_list,UpdateFaculty.this,"Computer Science");
                  rv_cs_faculty.setAdapter(adapter);
              }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

               Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
    }

    private void update_mba_rv() {
        cur_ref=reference.child("MBA");
        cur_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mba_faculty_list=new ArrayList<>();
                if(!snapshot.exists())
                {
                    ll_no_mba_data.setVisibility(View.VISIBLE);
                    rv_mba_faculty.setVisibility(View.GONE);
                }
                else
                {
                    ll_no_mba_data.setVisibility(View.GONE);
                    rv_mba_faculty.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        FacultyData data=snapshot1.getValue(FacultyData.class);
                        mba_faculty_list.add(data);
                    }
                    rv_mba_faculty.setHasFixedSize(true);
                    rv_mba_faculty.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapter=new FacultyAdapter(mba_faculty_list,UpdateFaculty.this,"MBA");
                    rv_mba_faculty.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update_phd_rv() {
        cur_ref=reference.child("PHD");
        cur_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phd_faculty_list=new ArrayList<>();
                if(!snapshot.exists())
                {
                    ll_no_phd_data.setVisibility(View.VISIBLE);
                    rv_phd_faculty.setVisibility(View.GONE);
                }
                else
                {
                    ll_no_phd_data.setVisibility(View.GONE);
                    rv_phd_faculty.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        FacultyData data=snapshot1.getValue(FacultyData.class);
                        phd_faculty_list.add(data);
                    }
                    rv_phd_faculty.setHasFixedSize(true);
                    rv_phd_faculty.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapter=new FacultyAdapter(phd_faculty_list,UpdateFaculty.this,"PHD");
                    rv_phd_faculty.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}