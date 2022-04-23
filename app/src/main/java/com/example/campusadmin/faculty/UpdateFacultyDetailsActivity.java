package com.example.campusadmin.faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateFacultyDetailsActivity extends AppCompatActivity {

    private CircleImageView civ_faculty_image;
    private EditText et_name,et_email,et_post;
    private Button bt_update,bt_delete;
    private String name,email,post,image,download_url,key,category;
    private final int REQ=1;
    private Bitmap bitmap=null;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty_details);

        name=getIntent().getStringExtra("name");
        email=getIntent().getStringExtra("email");
        post=getIntent().getStringExtra("post");
        image=getIntent().getStringExtra("image");
        key=getIntent().getStringExtra("key");
        category=getIntent().getStringExtra("category");

        civ_faculty_image=findViewById(R.id.civ_update_faculty_image);
        et_name=findViewById(R.id.et_update_faculty_name);
        et_email=findViewById(R.id.et_update_faculty_email);
        et_post=findViewById(R.id.et_update_faculty_post);
        bt_update=findViewById(R.id.bt_update_faculty);
        bt_delete=findViewById(R.id.bt_delete_faculty);
        pd=new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Faculty");

        try {
            Picasso.get().load(image).into(civ_faculty_image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        et_name.setText(name);
        et_email.setText(email);
        et_post.setText(post);

        civ_faculty_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        
        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=et_name.getText().toString();
                email=et_email.getText().toString();
                post=et_post.getText().toString();
                checkValidation();
            }
        });
        
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Please wait..");
                pd.show();
                delete_faculty();
            }
        });
    }

    private void delete_faculty() {
      databaseReference.child(category).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
              pd.dismiss();
              Toast.makeText(UpdateFacultyDetailsActivity.this, "Faculty removed successfully", Toast.LENGTH_SHORT).show();
              Intent intent=new Intent(UpdateFacultyDetailsActivity.this,UpdateFaculty.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(intent);
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              pd.dismiss();
              Toast.makeText(UpdateFacultyDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
          }
      });
    }

    private void checkValidation() {
        if(name.isEmpty())
        {
            et_name.setError("Empty");
            et_name.requestFocus();
        }
        else if(email.isEmpty())
        {
            et_email.setError("Empty");
            et_email.requestFocus();
        }
        else if(post.isEmpty())
        {
            et_post.setError("Empty");
            et_post.requestFocus();
        }
        else if(bitmap==null)
        {
            pd.setMessage("Updating..");
            pd.show();
            uploadData(image);
        }
        else
        {
            pd.setMessage("Updating..");
            pd.show();
            upload_image();
        }
    }

    private void upload_image() {
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte [] final_image=baos.toByteArray();
        final StorageReference image_path;
        image_path=storageReference.child("Faculty").child(final_image+"jpg");
        UploadTask uploadTask=image_path.putBytes(final_image);
        uploadTask.addOnCompleteListener(UpdateFacultyDetailsActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    download_url=String.valueOf(uri);
                                    uploadData(download_url);
                                }
                            });
                        }
                    });
                }
                else
                {
                    pd.dismiss();
                    Toast.makeText(UpdateFacultyDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData(String s) {
        HashMap hm=new HashMap();
        hm.put("name",name);
        hm.put("email",email);
        hm.put("post",post);
        hm.put("download_url",s);


        databaseReference.child(category).child(key).updateChildren(hm).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                pd.dismiss();
                Toast.makeText(UpdateFacultyDetailsActivity.this, "Faculty details Updated", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(UpdateFacultyDetailsActivity.this,UpdateFaculty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               pd.dismiss();
                Toast.makeText(UpdateFacultyDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
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
            civ_faculty_image.setImageBitmap(bitmap);
        }
    }
}