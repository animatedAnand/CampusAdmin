package com.example.campusadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadPDF extends AppCompatActivity {
    private CardView cv_select_file;
    private Button bt_upload_file;
    private EditText et_file_title;
    private final int REQ=1;
    private String file_name;
    private Uri file_uri;
    private TextView tv_selected_file_name_status;
    private DatabaseReference database_reference,cur_ref;
    private StorageReference storageReference;
    private String file_url="";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        cv_select_file=findViewById(R.id.cv_select_file);
        et_file_title=findViewById(R.id.et_file_title);
        bt_upload_file=findViewById(R.id.bt_upload_file);
        tv_selected_file_name_status=findViewById(R.id.tv_file_selected_status_name);
        database_reference= FirebaseDatabase.getInstance().getReference().child("Files");
        storageReference= FirebaseStorage.getInstance().getReference().child("Files");
        pd=new ProgressDialog(this);
        cv_select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_gallery();
            }
        });

        bt_upload_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_file_title.getText().toString().isEmpty())
                {
                    et_file_title.setError("Empty");
                    et_file_title.requestFocus();
                }
                else if(file_uri==null)
                {
                    Toast.makeText(UploadPDF.this, "Please select a file to upload", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    upload_pdf();
                }
            }
        });
    }

    private void upload_pdf() {
        pd.setTitle("Please wait");
        pd.setMessage("Uploading..");
        pd.show();
        StorageReference cur_ref=storageReference.child("pdf/"+file_name+System.currentTimeMillis()+".pdf");
        cur_ref.putFile(file_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri>task_uri= taskSnapshot.getStorage().getDownloadUrl();
                while(!task_uri.isComplete());
                Uri uri=task_uri.getResult();
                upload_data(String.valueOf(uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPDF.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void upload_data(String download_url) {
        String unique_key;
        unique_key=database_reference.push().getKey();
        HashMap data=new HashMap();
        data.put("Title",et_file_title.getText().toString());
        data.put("URL",download_url);
        database_reference.child(unique_key).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(UploadPDF.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                tv_selected_file_name_status.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPDF.this, "File failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void open_gallery() {
        Intent intent=new Intent();
        intent.setType("pdf/ppt/docs");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select file to upload"),REQ);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK)
        {
            file_uri=data.getData();
            if(file_uri.toString().startsWith("content://"))
            {
                Cursor cursor;
                try {
                    cursor=UploadPDF.this.getContentResolver().query(file_uri,null,null,null,null);
                    if(cursor!=null && cursor.moveToFirst())
                    {
                        file_name=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(file_uri.toString().startsWith("file://"))
            {
                file_name=new File(file_uri.toString()).getName();
            }
            tv_selected_file_name_status.setText(file_name);
        }
    }
}