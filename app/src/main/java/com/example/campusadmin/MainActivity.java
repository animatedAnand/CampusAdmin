package com.example.campusadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.campusadmin.Notice.DeleteNotice;
import com.example.campusadmin.Notice.UploadNotice;
import com.example.campusadmin.faculty.UpdateFaculty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    CardView cv_upload_notice,cv_upload_image,cv_upload_pdf,cv_update_faculty,cv_delete_notice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cv_upload_notice=findViewById(R.id.cv_upload_notice);
        cv_upload_image=findViewById(R.id.cv_upload_image);
        cv_upload_pdf=findViewById(R.id.cv_upload_pdf);
        cv_update_faculty=findViewById(R.id.cv_update_faculty);
        cv_delete_notice=findViewById(R.id.cv_delete_notice);

        cv_upload_notice.setOnClickListener(this);
        cv_upload_image.setOnClickListener(this);
        cv_upload_pdf.setOnClickListener(this);
        cv_update_faculty.setOnClickListener(this);
        cv_delete_notice.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
    switch (view.getId())
    {
        case R.id.cv_upload_notice:
        {
            startActivity(new Intent(MainActivity.this, UploadNotice.class));
            break;
        }
        case R.id.cv_upload_image:
        {
            startActivity(new Intent(MainActivity.this,UploadImage.class));
            break;
        }
        case R.id.cv_upload_pdf:
        {
            startActivity(new Intent(MainActivity.this,UploadPDF.class));
            break;
        }
        case R.id.cv_update_faculty:
        {
            startActivity(new Intent(MainActivity.this, UpdateFaculty.class));
            break;
        }
        case R.id.cv_delete_notice:
        {
            startActivity(new Intent(MainActivity.this, DeleteNotice.class));
            break;
        }
    }
    }
}