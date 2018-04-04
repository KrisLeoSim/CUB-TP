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

    private Calendar calendar;
    private SimpleDateFormat simpledateformat;
    private TextView textinstante;
    private Button btnstart;
    private Button btnend;
    private Button btntranf;
    private TextView textgps;
    private TextView textacelerometro;
    private SensorManager sensormanager,sensormanager_giro;
    private Sensor sensor, sensor_giro, sensor_prox;
    private SensorEventListener sensoreventlistener, sensoreventlistener1;
    private Ficheiro file;
    private TextView textgiroscopio;
    private TextView titulogiroscopio;
    private TextView titulogps;
    private TextView tituloacelerometro;
    private TextView tituloinstante;
    private TextView tituloproximidade;
    private TextView textproximidade;
    private boolean tem_acelerometro = false , tem_giroscopio = false, tem_proximidade = false;
    final private Giroscopio sens_giro = new Giroscopio();
    final private Acelerometro sens_acel = new Acelerometro();
    final private Proximidade sens_prox = new Proximidade();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = new Ficheiro(getApplicationContext());


        // --- FINDVIEWBYID
        titulogps = (TextView) findViewById(R.id.titulogps);
        tituloacelerometro = (TextView) findViewById(R.id.tituloacelerometro);
        textacelerometro = (TextView) findViewById(R.id.textacelerometro);
        tituloinstante = (TextView) findViewById(R.id.tituloinstante);
        textinstante = (TextView) findViewById(R.id.textinstante);
        Spinner spinner = (Spinner) findViewById(R.id.spinneractividades);
        btnend = (Button) findViewById(R.id.btnend);
        btntranf = (Button) findViewById(R.id.btntranf);
        btnstart = (Button) findViewById(R.id.btnstart);
        textgps = (TextView) findViewById(R.id.textgps);
        textgiroscopio = (TextView) findViewById(R.id.textgiroscopio);
        titulogiroscopio = (TextView) findViewById(R.id.titulogiroscopio);
        textproximidade = (TextView) findViewById(R.id.textproximidade);
        tituloproximidade = (TextView) findViewById(R.id.tituloproximidade);



        InicializaSensores();

        // --- BUTTON INICIAR ATIVIDADE
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inicia a leitura do sensor
                RegistaSensores();

                // OBTEM TEMPO E ATUALIZA TEXTVIEW
                String sDate = getTempo();
                textinstante.setText("TEMPO: "+sDate,TextView.BufferType.NORMAL);

                //GPS
                LeituraGPS();

                //String testelerfich =  file.readFile();
                //Toast.makeText(getApplicationContext(),"Leu: "+testelerfich,Toast.LENGTH_LONG).show();
            }
        });



        // --- BUTTON PARAR ATIVIDADE
        btnend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Parar a leitura do acelerometro
                DesregistaSensores();

                textinstante.setText("PARADO",TextView.BufferType.NORMAL);
            }
        });


        // --- BUTTON TANSFERIR / LIMPAR FICHEIRO
        btntranf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                if(isChecked == true) {
                    textacelerometro.setVisibility(View.VISIBLE);
                    textgps.setVisibility(View.VISIBLE);
                    textinstante.setVisibility(View.VISIBLE);
                    textgiroscopio.setVisibility(View.VISIBLE);
                    titulogps.setVisibility(View.VISIBLE);
                    tituloacelerometro.setVisibility(View.VISIBLE);
                    tituloinstante.setVisibility(View.VISIBLE);
                    titulogiroscopio.setVisibility(View.VISIBLE);
                    textproximidade.setVisibility(View.VISIBLE);
                    tituloproximidade.setVisibility(View.VISIBLE);
                }else{
                    textacelerometro.setVisibility(View.INVISIBLE);
                    textgps.setVisibility(View.INVISIBLE);
                    textinstante.setVisibility(View.INVISIBLE);
                    textgiroscopio.setVisibility(View.INVISIBLE);
                    titulogps.setVisibility(View.INVISIBLE);
                    tituloacelerometro.setVisibility(View.INVISIBLE);
                    tituloinstante.setVisibility(View.INVISIBLE);
                    titulogiroscopio.setVisibility(View.INVISIBLE);
                    textproximidade.setVisibility(View.INVISIBLE);
                    tituloproximidade.setVisibility(View.INVISIBLE);
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

    // --- GPS
    public void LeituraGPS(){
        GPS gps = new GPS(getApplicationContext());
        Location l = gps.getLocation();

        //file.saveFile("Teste a ver se guarda bem no ficheiro\n");

        if (l != null){
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            double alt = l.getAltitude();
            textgps.setText("LAT: "+lat+"\nLON: "+lon+"\nALT: "+alt,TextView.BufferType.NORMAL);
            //Toast.makeText(getApplicationContext(),"||GPS||\nLAT: "+lat+"\nLON: "+lon+"\nALT: "+alt,Toast.LENGTH_LONG).show();
        }
    }

    public void InicializaSensores(){
        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensor = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor==null){
            Toast.makeText(getApplicationContext(),"O Dispositivo nao tem Acelerometro",Toast.LENGTH_LONG).show();
        }else{
            tem_acelerometro=true;
        }

        sensor_giro = sensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensor_giro==null){
            Toast.makeText(getApplicationContext(),"O Dispositivo nao tem Giroscopio",Toast.LENGTH_LONG).show();
        }else{
            tem_giroscopio=true;
        }

        sensor_prox = sensormanager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(sensor_prox==null){
            Toast.makeText(getApplicationContext(),"O Dispositivo nao tem Proximidade",Toast.LENGTH_LONG).show();
        }else{
            tem_proximidade=true;
        }
    }

    public void RegistaSensores(){
        // inicia a leitura do sensor
        if(tem_giroscopio){
        sensormanager.registerListener(sens_giro,sensor_giro, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(tem_acelerometro) {
            sensormanager.registerListener(sens_acel, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(tem_proximidade) {
            sensormanager.registerListener(sens_prox, sensor_prox, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void DesregistaSensores(){
        // para a leitura do sensor
        if(tem_giroscopio){
            sensormanager.unregisterListener(sens_giro);
        }

        if(tem_acelerometro) {
            sensormanager.unregisterListener(sens_acel);
        }

        if(tem_proximidade) {
            sensormanager.unregisterListener(sens_prox);
        }
    }

   class Giroscopio implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            float x ;
            float y ;
            float z ;

            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];

            int acc = sensorEvent.accuracy;

            textgiroscopio.setText("X: "+(int)x+ "   Y: "+(int)y+ "   Z: "+(int)z+"\nacc: "+acc, TextView.BufferType.NORMAL);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    class Acelerometro implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            float x= sensorEvent.values[0];
            float y= sensorEvent.values[1];
            float z= sensorEvent.values[2];

            int acc = sensorEvent.accuracy;

            textacelerometro.setText("X: "+(int)x+"   Y: "+(int)y+ "   Z: "+(int)z+"\nacc: "+acc, TextView.BufferType.NORMAL);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    class Proximidade implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            float x= sensorEvent.values[0];
            //float val =  sensorEvent.sensor.getMaximumRange();


            int acc = sensorEvent.accuracy;

            textproximidade.setText("X: "+(int)x+"\ncm "+"\nacc: "+acc, TextView.BufferType.NORMAL);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}