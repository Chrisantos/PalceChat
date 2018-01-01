package com.example.chrisantuseze.palcechat.Settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.R;
import com.example.chrisantuseze.palcechat.ViewPicture;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsAccount extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;
    private TextView tvName, tvStatus, tvEmail, tvPhoneNumber, tvLocation, tvProfileName;
    private RelativeLayout etEditEmail, etEditPhoneNumber, etEditStatus, etEditLocation;
    private LinearLayout profilEdit;
    private AVLoadingIndicatorView mAVL;

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private FirebaseUser currentUser;

    private CircleImageView circleImageView, profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabase.keepSynced(true);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mDatabase.keepSynced(true);

        mAVL = (AVLoadingIndicatorView)findViewById(R.id.avi);
        mAVL.setVisibility(View.INVISIBLE);

        profilEdit = (LinearLayout)findViewById(R.id.editPic);
        tvName = (TextView)findViewById(R.id.name);
        tvStatus = (TextView)findViewById(R.id.status);
        tvEmail = (TextView)findViewById(R.id.email);
        tvPhoneNumber = (TextView)findViewById(R.id.phone_number);
        tvLocation = (TextView)findViewById(R.id.location);

        tvProfileName = (TextView)findViewById(R.id.profile);

        circleImageView = (CircleImageView)findViewById(R.id.circleView);
        profilePhoto = (CircleImageView)findViewById(R.id.profile_pic);
        //btnName = (Button)findViewById(R.id.changeImage);
        etEditEmail = (RelativeLayout) findViewById(R.id.email_relative);
        etEditPhoneNumber = (RelativeLayout) findViewById(R.id.phone_number_relative);
        etEditStatus = (RelativeLayout) findViewById(R.id.status_relative);
        etEditLocation = (RelativeLayout) findViewById(R.id.location_relative);


        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        tvName.setTypeface(custom_font1);
        tvProfileName.setTypeface(custom_font1);
//        btnName.setTypeface(custom_font1);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        tvStatus.setTypeface(custom_font2);
        tvEmail.setTypeface(custom_font2);
        tvPhoneNumber.setTypeface(custom_font2);
        tvLocation.setTypeface(custom_font2);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsAccount.this, ViewPicture.class));
            }
        });

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = tvName.getText().toString();
                Intent intent = new Intent(SettingsAccount.this, NameActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
        etEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = tvStatus.getText().toString();
                Intent intent = new Intent(SettingsAccount.this, StatusActivity.class);
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });
        etEditPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone_number = tvPhoneNumber.getText().toString();
                Intent intent = new Intent(SettingsAccount.this, PhoneActivity.class);
                intent.putExtra("phone", phone_number);
                startActivity(intent);
            }
        });
        etEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = tvLocation.getText().toString();
                Intent intent = new Intent(SettingsAccount.this, LocationActivity.class);
                intent.putExtra("location", location);
                startActivity(intent);
            }
        });
        etEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = tvEmail.getText().toString();
                Intent intent = new Intent(SettingsAccount.this, EmailActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String location = dataSnapshot.child("location").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                if (!image.equals("default")){

                    Picasso.with(SettingsAccount.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.user_avatar1)
                            .into(circleImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    Picasso.with(SettingsAccount.this).load(thumb_image)
                                            .placeholder(R.drawable.user_avatar1)
                                            .into(circleImageView);

                                }
                            });
                    Picasso.with(SettingsAccount.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.user_avatar1)
                            .into(profilePhoto, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    Picasso.with(SettingsAccount.this).load(thumb_image)
                                            .placeholder(R.drawable.user_avatar1)
                                            .into(profilePhoto);

                                }
                            });

                }
                tvName.setText(name);
                tvProfileName.setText(name);
                tvStatus.setText(status);
                tvEmail.setText(email);

                tvPhoneNumber.setText(phone);
                tvLocation.setText(location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profilEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // start picker to get image for cropping and then use the image in cropping activity
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsAccount.this);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Profile Picture"), GALLERY_PICK);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressBar progressBar;

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).start(SettingsAccount.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                mAVL.setVisibility(View.VISIBLE);
                mAVL.show();

//                dialog = new ProgressDialog(SettingsAccount.this);
//                dialog.setTitle("Uploading Image");
//                dialog.setMessage("Please wait while image is been uploaded");
//                dialog.show();
//                dialog.setCanceledOnTouchOutside(false);

                progressBar = (ProgressBar)findViewById(R.id.progressBar2);
                progressBar.setVisibility(View.VISIBLE);
                StyleableToast.makeText(this, "Uploading image, please wait...", R.style.success).show();

                Uri resultUri = result.getUri();
                //Getting the file
                File thumb_filePath = new File(resultUri.getPath());
                String currentUserID = mCurrentUser.getUid();

                //Compressing the image
                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(400)
                        .setMaxHeight(350)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                //To store byte array into firebase
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filepath = mImageStorage.child("profile_image").child(currentUserID
                        + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_image")
                        .child("thumb").child(currentUserID + ".jpg");



                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            //The task gets the values that are provided in firebase

                            //we get the download url and store it the the database
                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloaadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                    if (thumb_task.isSuccessful()){

//                                        Map<String, Object> update_map = new HashMap();
                                        Map update_map = new HashMap();
                                        update_map.put("image", download_url);
                                        update_map.put("thumb_image", thumb_downloaadUrl);

                                        mDatabase.updateChildren(update_map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                            //dialog.dismiss();
                                            new StyleableToast.Builder(getApplicationContext())
                                                    .text("Uploaded successfully").textColor(getColor
                                                    (R.color.colorPrimaryDark))
                                                    .backgroundColor(getColor(R.color.milk)).show();
                                            mAVL.hide();
                                            mAVL.setVisibility(View.INVISIBLE);
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                            }
                                        });
                                    }else{
                                       // dialog.dismiss();
                                        new StyleableToast.Builder(getApplicationContext())
                                                .text("Error occurred").textColor(Color.WHITE)
                                                .backgroundColor(getColor(R.color.red)).show();
                                    }
                                }
                            });

                            mDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                       // dialog.dismiss();
                                    }else{

                                    }
                                }
                            });
                        }else{

                            new StyleableToast.Builder(getApplicationContext())
                                    .text("Signing in failed!").textColor(Color.WHITE)
                                    .backgroundColor(getColor(R.color.red)).show();
                           // dialog.dismiss();
                        }
                    }
                });
                mAVL.hide();
                mAVL.setVisibility(View.INVISIBLE);

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception  error = result.getError();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        mUserRef.child("online").setValue("0");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
