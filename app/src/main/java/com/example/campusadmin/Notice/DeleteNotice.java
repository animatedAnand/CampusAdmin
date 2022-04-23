package com.example.campusadmin.Notice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.campusadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteNotice extends AppCompatActivity {
    private RecyclerView rv_delete_notice;
    private ProgressBar pb;
    private ArrayList<NoticeData> list;
    private NoticeAdapter adapter;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);

        rv_delete_notice=findViewById(R.id.rv_delete_notice);
        pb=findViewById(R.id.pb_delete_notice);
        rv_delete_notice.setLayoutManager(new LinearLayoutManager(this));
        rv_delete_notice.setHasFixedSize(true);
        ref= FirebaseDatabase.getInstance().getReference().child("Notice");
        getNotice();
    }

    private void getNotice() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list =new ArrayList<NoticeData>();
                for(DataSnapshot cur_ss:snapshot.getChildren())
                {
                    NoticeData data=cur_ss.getValue(NoticeData.class);
                    list.add(data);
                }
                pb.setVisibility(View.GONE);
                adapter=new NoticeAdapter(DeleteNotice.this,list);
                adapter.notifyDataSetChanged();
                rv_delete_notice.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                pb.setVisibility(View.GONE);
                Toast.makeText(DeleteNotice.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}