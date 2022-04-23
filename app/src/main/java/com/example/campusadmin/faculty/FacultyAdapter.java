package com.example.campusadmin.faculty;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusadmin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewAdapter> {

    private String category;
    private List<FacultyData> list;
    private Context context;

    public FacultyAdapter(List<FacultyData> list, Context context,String category) {
        this.list = list;
        this.context = context;
        this.category=category;
    }

    @NonNull
    @Override
    public FacultyViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.faculty_item_layout,parent,false);
        return new FacultyViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewAdapter holder, int position) {
        FacultyData cur_faculty=list.get(position);
        holder.tv_name.setText(cur_faculty.getName());
        holder.tv_post.setText(cur_faculty.getPost());
        holder.tv_email.setText(cur_faculty.getEmail());

        try {
            Picasso.get().load(cur_faculty.getDownload_url()).into(holder.iv_faculty_image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.bt_update_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,UpdateFacultyDetailsActivity.class);

                intent.putExtra("name",cur_faculty.getName());
                intent.putExtra("email",cur_faculty.getEmail());
                intent.putExtra("post",cur_faculty.getPost());
                intent.putExtra("image",cur_faculty.getDownload_url());
                intent.putExtra("key",cur_faculty.getUnique_key());
                intent.putExtra("category",category);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FacultyViewAdapter extends RecyclerView.ViewHolder {

        private  TextView tv_name,tv_post,tv_email;
        private Button bt_update_faculty;
        private ImageView iv_faculty_image;
        public FacultyViewAdapter(@NonNull View itemView) {
            super(itemView);
            tv_name=itemView.findViewById(R.id.tv_faculty_name);
            tv_post=itemView.findViewById(R.id.tv_faculty_post);
            tv_email=itemView.findViewById(R.id.tv_faculty_email);
            bt_update_faculty=itemView.findViewById(R.id.bt_update_faculty_info);
            iv_faculty_image=itemView.findViewById(R.id.iv_faculty_image);
        }
    }
}
