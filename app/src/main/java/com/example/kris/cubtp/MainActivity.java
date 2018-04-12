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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
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
    private Sensor sensor_acel, sensor_giro, sensor_prox, sensor_magne;
    private SensorEventListener sensoreventlistener, sensoreventlistener1;
    private Ficheiro file;
    private TextView textgiroscopio;
    private TextView titulogiroscopio;
    private TextView titulogps;
    private TextView tituloacelerometro;
    private TextView tituloinstante;
    private TextView tituloproximidade;
    private TextView textproximidade;
    private TextView textorientation;
    private TextView tituloorientacao;
    private ScrollView scrollView;
    private boolean tem_acelerometro = false , tem_giroscopio = false, tem_proximidade = false, tem_magnetismo = false;
    final private Giroscopio sens_giro = new Giroscopio();
    final private Proximidade sens_prox = new Proximidade();
    final private GeomagnAcel sens_magnAcel = new GeomagnAcel();
    private String tempo_inicial ="";
    private String tempo_final ="";
    private Thread t;
    private GPS_Tracker gps;

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
        textorientation = (TextView) findViewById(R.id.textoorientation);
        tituloorientacao = (TextView) findViewById(R.id.tituloorientacao);
        scrollView = (ScrollView) findViewById(R.id.scrollview);



        InicializaSensores();

        // --- BUTTON INICIAR ATIVIDADE
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //GPS
                if(LeituraGPS_tracker()){

                    // inicia a leitura do sensor
                    RegistaSensores();

                    // OBTEM TEMPO E ATUALIZA TEXTVIEW

                    tempo_inicial = getTempo();
                    //textinstante.setText("TEMPO: "+tempo_inicial,TextView.BufferType.NORMAL);
                    IniciaContagemDe_Tempo();

                    btnstart.setEnabled(false);
                    btnstart.setBackgroundResource(R.color.colorcinza);
                    btnend.setEnabled(true);
                    btnend.setBackgroundResource(R.color.colorAccent);

                }
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

               // textinstante.setText("PARADO",TextView.BufferType.NORMAL);

                tempo_final = getTempo();
                t.interrupt();
                textinstante.setText("TEMPO: "+tempo_inicial,TextView.BufferType.NORMAL);

                //para GPS
                ParaLeitura_GPS();

                btnstart.setEnabled(true);
                btnstart.setBackgroundResource(R.color.colorAccent);
                btnend.setEnabled(false);
                btnend.setBackgroundResource(R.color.colorcinza);
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
                    tituloorientacao.setVisibility(View.VISIBLE);
                    textorientation.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);

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
                    tituloorientacao.setVisibility(View.INVISIBLE);
                    textorientation.setVisibility(View.INVISIBLE);
                    scrollView.setVisibility(View.INVISIBLE);
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

    public void InicializaSensores(){
        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensor_acel = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor_acel==null){
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

        sensor_magne = sensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(sensor_magne==null){
            Toast.makeText(getApplicationContext(),"O Dispositivo nao tem Magnetic_Field",Toast.LENGTH_LONG).show();
        }else{
            tem_magnetismo = true;
        }


    }

    public void RegistaSensores(){
        // inicia a leitura do sensor
        if(tem_giroscopio){
            sensormanager.registerListener(sens_giro,sensor_giro, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(tem_proximidade) {
            sensormanager.registerListener(sens_prox, sensor_prox, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(tem_magnetismo && tem_acelerometro) {
            sensormanager.registerListener(sens_magnAcel, sensor_acel, SensorManager.SENSOR_DELAY_NORMAL);
            sensormanager.registerListener(sens_magnAcel, sensor_magne, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            if(tem_acelerometro) {
                sensormanager.registerListener(sens_magnAcel, sensor_acel, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    public void DesregistaSensores(){
        // para a leitura do sensor
        if(tem_giroscopio){
            sensormanager.unregisterListener(sens_giro);
        }

        if(tem_proximidade) {
            sensormanager.unregisterListener(sens_prox);
        }

        if(tem_acelerometro) {
            sensormanager.unregisterListener(sens_magnAcel);
        }
    }

    public String getTempo(){
        calendar = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String sDate = (String) simpledateformat.format(calendar.getTime());
        return sDate;
    }


    public void IniciaContagemDe_Tempo(){
        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TextView tdate = (TextView) findViewById(R.id.date);
                                // long date = System.currentTimeMillis();
                                //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy\nhh-mm-ss a");
                                // String dateString = sdf.format(date);
                                String dateString = getTempo();
                                // tdate.setText(dateString);
                                textinstante.setText("TEMPO: "+dateString,TextView.BufferType.NORMAL);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }

            }

        };
        t.start();
    }

    public boolean LeituraGPS_tracker(){

        gps = new GPS_Tracker(this);
        if(gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            double altitude = gps.getAltitude();

            textgps.setText("LAT: "+latitude+"\nLON: "+longitude+"\nALT: "+altitude,TextView.BufferType.NORMAL);
        }else{
            gps.showSettingsAlert();
            return false;
        }
    return true;
    }
public void ParaLeitura_GPS(){
        gps.stopUsingGPS();
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

            textgiroscopio.setText("X: "+(int)x+ "   Y: "+(int)y+ "   Z: "+(int)z+"    acc: "+acc, TextView.BufferType.NORMAL);
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

            textproximidade.setText("X: "+(int)x+" cm "+"    acc: "+acc, TextView.BufferType.NORMAL);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    class GeomagnAcel implements SensorEventListener {

        float[] accelerometerValues = new float[3];
        float[] geomagneticMatrix = new float[3];


        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            switch (sensorEvent.sensor.getType()) {

                case Sensor.TYPE_ACCELEROMETER:
                    accelerometerValues   = sensorEvent.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    geomagneticMatrix = sensorEvent.values.clone();
                    break;
                default:
                    break;
            }


            if (accelerometerValues != null && geomagneticMatrix != null) {


                float[] R = new float[9];
                float[] I = new float[9];

                boolean sucesso = SensorManager.getRotationMatrix(R, null, accelerometerValues, geomagneticMatrix);

                if(sucesso) {
                    float[] actual_orientation = new float[3];
                    SensorManager.getOrientation(R, actual_orientation);

                    float x = accelerometerValues[0];
                    float y = accelerometerValues[1];
                    float z = accelerometerValues[2];

                    textacelerometro.setText("X: " + (int) x + "   Y: " + (int) y + "   Z: " + (int) z + "    acc: ", TextView.BufferType.NORMAL);

                    //floatXTotal += accelerometerValues[0];
                    //tvXTotal.setText(floatXTotal + "");


                    float azimuth_angle = actual_orientation[0];
                    float pitch_angle = actual_orientation[1];
                    float roll_angle = actual_orientation[2];


                    textorientation.setText("Azimuth: " +(int) azimuth_angle + "   pitch: " + (int) pitch_angle + "   roll: " + (int) roll_angle, TextView.BufferType.NORMAL);
                }
            }else{

                if(accelerometerValues != null) {

                    float x;
                    float y;
                    float z;

                    x = sensorEvent.values[0];
                    y = sensorEvent.values[1];
                    z = sensorEvent.values[2];

                    textacelerometro.setText("X: " + (int) x + "   Y: " + (int) y + "   Z: " + (int) z + "    acc: ", TextView.BufferType.NORMAL);
                }
            }








        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }




}