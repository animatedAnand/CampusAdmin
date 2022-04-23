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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFaculty extends AppCompatActivity {
    private CircleImageView civ_faculty_image;
    private EditText et_faculty_name,et_faculty_email,et_faculty_post;
    private Spinner sp_faculty_category;
    private Button bt_add_faculty;
    private final int REQ=1;
    private Bitmap bitmap=null;
    private String faculty_name,faculty_email,faculty_post,download_url,faculty_dept;
    private StorageReference storageReference;
    private DatabaseReference databaseReference,cur_ref;
    private ProgressDialog pd;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);

        download_url="https://firebasestorage.googleapis.com/v0/b/campus-admin-49bd9.app" +
                "spot.com/o/Faculty%2Fdefault.png?alt=media&token=eacc3acc-eaf2-4ea3-920b-3745ea051abe";
        civ_faculty_image=findViewById(R.id.civ_add_faculty_image);
        et_faculty_name=findViewById(R.id.et_faculty_name);
        et_faculty_email=findViewById(R.id.et_faculty_email);
        et_faculty_post=findViewById(R.id.et_faculty_post);
        sp_faculty_category=findViewById(R.id.sp_select_faculty_category);
        bt_add_faculty=findViewById(R.id.bt_add_faculty);
        pd=new ProgressDialog(this);

        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Faculty");

        String[] category_items=new String[] {"Select Category","Computer Science","MBA","PHD"};
        sp_faculty_category.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,category_items));
        sp_faculty_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                faculty_dept=sp_faculty_category.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        civ_faculty_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        
        bt_add_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_validation();
            }
        });
    }

    private void check_validation() {
        faculty_name=et_faculty_name.getText().toString();
        faculty_email=et_faculty_email.getText().toString();
        faculty_post=et_faculty_post.getText().toString();
        if(faculty_name.isEmpty())
        {
            et_faculty_name.setError("Empty");
            et_faculty_name.requestFocus();
        }
        else if(faculty_email.isEmpty())
        {
            et_faculty_email.setError("Empty");
            et_faculty_email.requestFocus();
        }
        else if(faculty_post.isEmpty())
        {
            et_faculty_post.setError("Empty");
            et_faculty_post.requestFocus();
        }
        else if(faculty_dept.equals("Select Category"))
        {
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
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

    private void upload_image() {
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte [] final_image=baos.toByteArray();
        final StorageReference image_path;
        image_path=storageReference.child("Faculty").child(final_image+"jpg");
        UploadTask uploadTask=image_path.putBytes(final_image);
        uploadTask.addOnCompleteListener(AddFaculty.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    upload_data();
                                }
                            });
                        }
                    });
                }
                else
                {
                    pd.dismiss();
                    Toast.makeText(AddFaculty.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void upload_data() {
        cur_ref=databaseReference.child(faculty_dept);
        final  String key=cur_ref.push().getKey();

        FacultyData facultyData=new FacultyData(faculty_name,faculty_email,faculty_post,download_url,key);
        cur_ref.child(key).setValue(facultyData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(AddFaculty.this, "Added successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AddFaculty.this,UpdateFaculty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddFaculty.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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