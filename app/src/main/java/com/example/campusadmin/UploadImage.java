package com.example.campusadmin;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class UploadImage extends AppCompatActivity {

    private CardView cv_select_image;
    private Spinner sp_image_category;
    private Button bt_upload_gallery_image;
    private ImageView iv_image_preview;
    private final int REQ=1;
    private Bitmap bitmap;
    private String category;
    private ProgressDialog pd;
    private DatabaseReference refernce,cur_ref;
    private StorageReference storageReference;
    private String image_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        cv_select_image=findViewById(R.id.cv_select_gallery_image);
        sp_image_category=findViewById(R.id.sp_select_image_category);
        bt_upload_gallery_image=findViewById(R.id.bt_upload_gallery_image);
        iv_image_preview=findViewById(R.id.iv_gallery_image_preview);
        pd=new ProgressDialog(this);
        refernce= FirebaseDatabase.getInstance().getReference().child("Gallery");
        storageReference= FirebaseStorage.getInstance().getReference().child("Gallery");

        String[] category_items=new String[] {"Select Category","Group Photos","Screenshots","Others"};
        sp_image_category.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,category_items));
        sp_image_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category=sp_image_category.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cv_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        bt_upload_gallery_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap==null)
                {
                    Toast.makeText(UploadImage.this, "Please select image", Toast.LENGTH_SHORT).show();
                }
                else if(category=="Select Category")
                {
                    Toast.makeText(UploadImage.this, "Please select image category", Toast.LENGTH_SHORT).show();
                }
                else
                {
                 pd.setMessage("Uploading..");
                 pd.show();
                 uploadImage();
                }
            }
        });
    }

    private void uploadImage() {
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte [] final_image=baos.toByteArray();
        final StorageReference image_path;
        image_path=storageReference.child(final_image+"jpg");
        UploadTask uploadTask=image_path.putBytes(final_image);
        uploadTask.addOnCompleteListener(UploadImage.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                    Toast.makeText(UploadImage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void upload_data() {
        cur_ref=refernce.child(category);
        String unique_key=cur_ref.push().getKey();
        cur_ref.child(unique_key).setValue(image_url).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
              pd.dismiss();
                Toast.makeText(UploadImage.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               pd.dismiss();
                Toast.makeText(UploadImage.this, "Someting went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent pick_image=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pick_image,REQ);
    }
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
            iv_image_preview.setImageBitmap(bitmap);
        }
    }
}