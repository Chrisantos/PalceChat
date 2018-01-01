package com.example.chrisantuseze.palcechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import id.zelory.compressor.Compressor;

public class ViewPicture extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView tvName;
    private ImageView mChangePic;
    private ImageView mProfilePhoto;

    private AVLoadingIndicatorView mAVL;
    private DatabaseReference mDatabase;
    private StorageReference mImageStorage;
    private static final int GALLERY_PICK = 1;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        mToolbar = (Toolbar)findViewById(R.id.view_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.view_picture_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabase.keepSynced(true);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        tvName = (TextView)findViewById(R.id.name);
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        tvName.setTypeface(custom_font1);

        mProfilePhoto = (ImageView)findViewById(R.id.profile_pic);

        mChangePic = (ImageView)findViewById(R.id.change_pic);
        mChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Profile Picture"), GALLERY_PICK);
            }
        });

        mAVL = (AVLoadingIndicatorView)findViewById(R.id.avi);
        mAVL.setVisibility(View.INVISIBLE);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String image = dataSnapshot.child("image").getValue().toString();
                final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                if (!image.equals("default")){

                    Picasso.with(ViewPicture.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.user_avatar1)
                            .into(mProfilePhoto, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    Picasso.with(ViewPicture.this).load(thumb_image)
                                            .placeholder(R.drawable.user_avatar1)
                                            .into(mProfilePhoto);

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changeStatusBarColor();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressBar progressBar;

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).start(ViewPicture.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                mAVL.setVisibility(View.VISIBLE);
                mAVL.show();
                StyleableToast.makeText(this, "Uploading image, please wait...", R.style.success).show();
                progressBar = (ProgressBar)findViewById(R.id.progressBar2);
                progressBar.setVisibility(View.VISIBLE);


                Uri resultUri = result.getUri();
                //Getting the file
                File thumb_filePath = new File(resultUri.getPath());

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


            StorageReference filepath = mImageStorage.child("profile_image").child(uid + ".jpg");
            final StorageReference thumb_filepath = mImageStorage.child("profile_image")
                    .child("thumb").child(uid + ".jpg");



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
                        }else{

                        }
                    }
                });
                }else{

                    new StyleableToast.Builder(getApplicationContext())
                            .text("Signing in failed!").textColor(Color.WHITE)
                            .backgroundColor(getColor(R.color.red)).show();
                }
            }
        });
        mAVL.hide();
        mAVL.setVisibility(View.INVISIBLE);

    }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
        Exception  error = result.getError();
    }
}}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.milk));
        }
    }
}
