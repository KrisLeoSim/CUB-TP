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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Calendar calendar;
    SimpleDateFormat simpledateformat;
    TextView textinstante;
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
        final Ficheiro file = new Ficheiro(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- FINDVIEWBYID
        final TextView titulogps = (TextView) findViewById(R.id.titulogps);
        final TextView tituloacelerometro = (TextView) findViewById(R.id.tituloacelerometro);
        final TextView tituloinstante = (TextView) findViewById(R.id.tituloinstante);
        textinstante = (TextView) findViewById(R.id.textinstante);
        Spinner spinner = (Spinner) findViewById(R.id.spinneractividades);
        btnend = (Button) findViewById(R.id.btnend);
        btntranf = (Button) findViewById(R.id.btntranf);
        btnstart = (Button) findViewById(R.id.btnstart);
        textgps = (TextView) findViewById(R.id.textgps);

        InicializaAcelerometro();

        // --- GPS --- BUTTON INICIAR ATIVIDADE
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GPS gps = new GPS(getApplicationContext());
                Location l = gps.getLocation();

                file.saveFile("Teste a ver se guarda bem no ficheiro\n");

                if (l != null){
                    double lat = l.getLatitude();
                    double lon = l.getLongitude();
                    double alt = l.getAltitude();
                    textgps.setText("LAT: "+lat+"\nLON: "+lon+"\nALT: "+alt,TextView.BufferType.NORMAL);
                    //Toast.makeText(getApplicationContext(),"||GPS||\nLAT: "+lat+"\nLON: "+lon+"\nALT: "+alt,Toast.LENGTH_LONG).show();
                }
            }
        });


        // --- ACELEROMETRO INICIAR --- BUTTON PARAR ATIVIDADE
        btnend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inicia a leitura do sensor
                sensormanager.registerListener(sensoreventlistener,sensor,SensorManager.SENSOR_DELAY_NORMAL);

                String testelerfich =  file.readFile();
                Toast.makeText(getApplicationContext(),"Leu: "+testelerfich,Toast.LENGTH_LONG).show();
            }
        });


        // --- ACELEROMETRO TERMINAR --- BUTTON TANSFERIR / LIMPAR FICHEIRO
        btntranf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Parar a leitura do acelerometro
                sensormanager.unregisterListener(sensoreventlistener);
            }
        });


        // --- TRATAMENTO DO SPINNER --- METER NUMA FUNÇAO
        ArrayAdapter<CharSequence> adapterspinner = ArrayAdapter.createFromResource(this,R.array.actividades_list,R.layout.support_simple_spinner_dropdown_item);
        adapterspinner.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapterspinner);
        spinner.setOnItemSelectedListener(this);

        // --- TRATAMENTO DO SWITCH --- METER NUMA FUNÇAO
        Switch escondeMostradores = (Switch) findViewById(R.id.EsconderMostradores);
        escondeMostradores.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    textacelerometro.setVisibility(View.VISIBLE);
                    textgps.setVisibility(View.VISIBLE);
                    textinstante.setVisibility(View.VISIBLE);
                    titulogps.setVisibility(View.VISIBLE);
                    tituloacelerometro.setVisibility(View.VISIBLE);
                    tituloinstante.setVisibility(View.VISIBLE);
                }else{
                    textacelerometro.setVisibility(View.INVISIBLE);
                    textgps.setVisibility(View.INVISIBLE);
                    textinstante.setVisibility(View.INVISIBLE);
                    titulogps.setVisibility(View.INVISIBLE);
                    tituloacelerometro.setVisibility(View.INVISIBLE);
                    tituloinstante.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    // --- SABER QUAL A ACTIVIDADE ESCOLHIDA
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String ActividadeSelecionada = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(),ActividadeSelecionada,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public String getTempo(){
        calendar = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String sDate = (String) simpledateformat.format(calendar.getTime());
        return sDate;
    }

    // --- ACELEROMETRO
    public void InicializaAcelerometro(){
        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor==null){
            Toast.makeText(getApplicationContext(),"O Dispositivo nao tem Acelerometro",Toast.LENGTH_LONG).show();
        }
        textacelerometro = (TextView) findViewById(R.id.textacelerometro);

        sensoreventlistener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                final float alpha = (float) 0.8;
                float gravity[] = new float[3];
                float x;
                float y;
                float z;
                int acc = sensorEvent.accuracy; //isto tera utilidade?
                x = sensorEvent.values[0];
                y = sensorEvent.values[1];
                z = sensorEvent.values[2];
                textacelerometro.setText("X: "+x+"\nY: "+y+"\nZ: "+z+"\nacc: "+acc,TextView.BufferType.NORMAL);

                // OBTEM TEMPO E ATUALIZA TEXTVIEW
                String sDate = getTempo();
                textinstante.setText("TEMPO: "+sDate,TextView.BufferType.NORMAL);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

}