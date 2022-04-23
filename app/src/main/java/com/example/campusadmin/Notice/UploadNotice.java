package com.example.campusadmin.Notice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.campusadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadNotice extends AppCompatActivity {
    CardView cv_select_notice_image;
    private Button bt_upload_notice;
    private EditText et_notice_title;
    private final int REQ=1;
    private Bitmap bitmap;
    private ImageView iv_notice_image_preview;
    private DatabaseReference reference,cur_ref;
    private StorageReference storageReference;
    private String image_url="";
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);
        cv_select_notice_image=findViewById(R.id.cv_select_notice_image);
        iv_notice_image_preview=findViewById(R.id.iv_notice_preview);
        et_notice_title=findViewById(R.id.et_notice_title);
        bt_upload_notice=findViewById(R.id.bt_upload_notice);
        reference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        pd=new ProgressDialog(this);
        cv_select_notice_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_gallery();
            }
        });
        bt_upload_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_notice_title.getText().toString().isEmpty())
                {
                    et_notice_title.setError("Empty");
                    et_notice_title.requestFocus();
                }
                else if(bitmap==null)
                {
                    pd.setMessage("Uploading..");
                    pd.show();
                    upload_data();
                }
                else
                {
                    pd.setMessage("Uploading..");
                    pd.show();
                    upload_image();
                }
            }
        });
    }

    private void upload_image() {
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte [] final_image=baos.toByteArray();
        final StorageReference image_path;
        image_path=storageReference.child("Notice").child(final_image+"jpg");
        UploadTask uploadTask=image_path.putBytes(final_image);
        uploadTask.addOnCompleteListener(UploadNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
              if(task.isSuccessful())
              {
                  task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                      @Override
                      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                             image_url=String.valueOf(uri);
                             upload_data();
                            }
                        });
                      }
                  });
              }
              else
              {
                  pd.dismiss();
                  Toast.makeText(UploadNotice.this, "Something went wrong", Toast.LENGTH_SHORT).show();
              }
            }
        });
    }

    private void upload_data() {
        cur_ref=reference.child("Notice");
        final  String key=cur_ref.push().getKey();
        String title=et_notice_title.getText().toString();

        Calendar cal_for_date=Calendar.getInstance();
        SimpleDateFormat cur_date=new SimpleDateFormat("dd-MM-yy");
        String date=cur_date.format(cal_for_date.getTime());

        Calendar cal_for_time=Calendar.getInstance();
        SimpleDateFormat cur_time=new SimpleDateFormat("hh-mm a");
        String time=cur_time.format(cal_for_time.getTime());

        NoticeData noticeData=new NoticeData(title,image_url,date,time,key);
        cur_ref.child(key).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(UploadNotice.this, "Notice uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadNotice.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void open_gallery() {
        Intent pick_image=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pick_image,REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK)
        {
            Uri uri=data.getData();
            try {
                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            iv_notice_image_preview.setImageBitmap(bitmap);
        }
    }
}