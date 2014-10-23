package com.ddpclient.spiovesan.ddpclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.spiovesan.ddpclient.DDPClient;

import java.lang.reflect.Field;


public class MainActivity extends Activity {
    private String sMeteorIp = "localhost";
    private Integer sMeteorPort = 3000;
    private DDPClient mDdp;
    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.closeButton = (Button)this.findViewById(R.id.connect);
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mDdp = new DDPClient(sMeteorIp, sMeteorPort);
                    mDdp.connect();
                    Log.w("LogPositionApp", "Connection state:" + mDdp.getState());
                }
                catch(Exception e) {
                    Log.w("LogPositionApp", e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();

            //render the main screen
            String layoutClass = this.getPackageName() + ".R$layout";
            String main = "activity_main";
            Class clazz = Class.forName(layoutClass);
            Field field = clazz.getField(main);
            int screenId = field.getInt(clazz);
            this.setContentView(screenId);

            //StartService button
            Button startService = (Button) findViewById(R.id.start_service);
            startService.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View button) {
                        Intent start = new Intent("com.ddpclient.spiovesan.ddpclient.LogService");
                        MainActivity.this.startService(start);
                    }
                }
            );

            //Stop Service Button
            Button stopService = (Button) findViewById(R.id.stop_service);
            stopService.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View button) {
                       Intent stop = new Intent("com.ddpclient.spiovesan.ddpclient.LogService");
                       MainActivity.this.stopService(stop);
                   }
               }
            );
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

}
