package com.example.chrisantuseze.palcechat.Settings;

import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.R;

public class SettingsNotification extends AppCompatActivity {
    private Button btnNotify, btnIgnore;
    private TextView tvGetNotified, tvDontMiss;
    Toolbar mToolbar;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_notification);

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.main_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        tvName = (TextView)findViewById(R.id.users);
        tvName.setText("Notifications");



        btnIgnore = (Button)findViewById(R.id.no_thanks);
        btnNotify = (Button)findViewById(R.id.notify_me);
        tvGetNotified = (TextView) findViewById(R.id.tv_get_notified);
        tvDontMiss = (TextView)findViewById(R.id.tv_dont_miss);

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        btnIgnore.setTypeface(custom_font1);
        btnNotify.setTypeface(custom_font1);
        tvGetNotified.setTypeface(custom_font1);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        tvDontMiss.setTypeface(custom_font2);
        tvName.setTypeface(custom_font2);

        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        });
        btnIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }
}
