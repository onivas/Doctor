package com.savinoordine.example;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.savinoordine.doctor.Doctor;
import com.savinoordine.doctor.log.LogHandler;

public class MainActivity extends AppCompatActivity {

    private Doctor mDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        // setup the Doctor
        mDoctor = new Doctor(this)
                .setLogPriority(LogHandler.PriorityType.IWEF)
                .start();

        Button d = (Button) findViewById(R.id.d);
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DDDD", "debug debug debug");
            }
        });

        Button i = (Button) findViewById(R.id.i);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("III", "info info info");
            }
        });

        Button e = (Button) findViewById(R.id.e);
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("EEE", "error error error");
            }
        });
    }

    @Override
    protected void onStop() {
        // remember to stop the Doctor
        mDoctor.stop();
        super.onStop();
    }
}
