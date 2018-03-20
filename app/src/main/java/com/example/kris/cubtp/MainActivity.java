package com.example.kris.cubtp;

import android.Manifest;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnstart;
    Button btnend;
    Button btntranf;
    TextView textgps;
    TextView textacelerometro;
    SensorManager sensormanager;
    Sensor sensor;
    SensorEventListener sensoreventlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // --- ACELEROMETRO
        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor==null){
            Toast.makeText(getApplicationContext(),"O Dispositivo nao tem Acelerometro",Toast.LENGTH_LONG).show();
        }
        textacelerometro = (TextView) findViewById(R.id.textacelerometro);

        sensoreventlistener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                textacelerometro.setText("X: "+x+"\nY: "+y+"\nZ: "+z,TextView.BufferType.NORMAL);
                //Toast.makeText(getApplicationContext(),"X: "+x+"\nY: "+y+"\nZ: "+z,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        btnend = (Button) findViewById(R.id.btnend);
        //ver se é necessario permissao para isto ou nao
        btnend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inicia a leitura do sensor
                textacelerometro.setVisibility(View.VISIBLE);
                sensormanager.registerListener(sensoreventlistener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

        btntranf = (Button) findViewById(R.id.btntranf);
        btntranf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Parar a leitura do acelerometro
                sensormanager.unregisterListener(sensoreventlistener);
            }
        });

        // --- GPS
        btnstart = (Button) findViewById(R.id.btnstart);
        textgps = (TextView) findViewById(R.id.textgps);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GPS gps = new GPS(getApplicationContext());
                Location l = gps.getLocation();
                textgps.setVisibility(View.VISIBLE);

                if (l != null){
                    double lat = l.getLatitude();
                    double lon = l.getLongitude();
                    double alt = l.getAltitude();
                    textgps.setText("LAT: "+lat+"\nLON: "+lon+"\nALT: "+alt,TextView.BufferType.NORMAL);
                    //Toast.makeText(getApplicationContext(),"||GPS||\nLAT: "+lat+"\nLON: "+lon+"\nALT: "+alt,Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
