package com.example.chrisantuseze.palcechat.Settings;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chrisantuseze.palcechat.R;

public class SettingsHelp extends AppCompatActivity {
    private LinearLayout linearInfo, linearContact;
    private TextView tvInfo, tvContact;
    Toolbar mToolbar;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_help);

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.main_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        linearInfo = (LinearLayout)findViewById(R.id.app_info_linear);
        linearContact = (LinearLayout)findViewById(R.id.contact_linear);

        tvInfo = (TextView)findViewById(R.id.info);
        tvContact = (TextView)findViewById(R.id.contact);

        tvName = (TextView)findViewById(R.id.users);
        tvName.setText("Help");

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        tvName.setTypeface(custom_font2);
        tvInfo.setTypeface(custom_font1);
        tvContact.setTypeface(custom_font1);

        linearInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsHelp.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        linearContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsHelp.this, "Clicked", Toast.LENGTH_SHORT).show();
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
