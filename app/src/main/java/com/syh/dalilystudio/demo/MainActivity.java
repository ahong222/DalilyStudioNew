package com.syh.dalilystudio.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.syh.dalilystudio.GlobalAppData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalAppData.init(this, BuildConfig.DEBUG);

        TextView textView = new TextView(this);
        textView.setText("hello world");
        setContentView(textView);
    }
}
