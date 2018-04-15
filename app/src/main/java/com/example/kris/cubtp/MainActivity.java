package com.example.kris.cubtp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    private boolean mRequestLocationUpdates = false;
    private boolean suporta_gps = false;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;

    private TextView lblLocation;

    private Calendar calendar;
    private SimpleDateFormat simpledateformat;
    private TextView textinstante;
    private Button btnstart;
    private Button btnend;
    private Button btntranf;
    private ImageButton verconteudoficheiro, listarSensores;
    private TextView textgps;
    private TextView textacelerometro;
    private SensorManager sensormanager;
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
    private TextView tituloaceleracaolinear;
    private TextView textoaceleracaolinear;
    private ScrollView scrollView;
    private boolean tem_acelerometro = false , tem_giroscopio = false, tem_proximidade = false, tem_magnetismo = false, tem_linearacel = false;
    private Sensor sensor_acel, sensor_giro, sensor_prox, sensor_magne, sensor_lineacel;
    final private Giroscopio sens_giro = new Giroscopio();
    final private Proximidade sens_prox = new Proximidade();
    final private GeomagnAcel sens_magnAcel = new GeomagnAcel();
    final private LinearAcel sens_lineacel = new LinearAcel();
    private String tempo_inicial ="";
    private String tempo_final ="";
    private Thread t;
    private Dialoge_boxes d_box = new Dialoge_boxes(this);
    private GPS_GOOGLE_API gps_google;
    private LocationManager locationManager;
    private Intent intent;
    private Chronometer simpleChronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //verifica se +e possivel usar o googleapicliente para fazer uso do gps
        if(checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }


        file = new Ficheiro(this);
        file.saveFile("Rosa a poderosa\n");
        //file.readFile();
        //Toast.makeText(getApplicationContext(),file.readFile(),Toast.LENGTH_LONG).show();


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
        verconteudoficheiro = (ImageButton)findViewById(R.id.verconteudofich);
        listarSensores = (ImageButton) findViewById(R.id.listarsen);

        tituloaceleracaolinear = (TextView) findViewById(R.id.tituloaceleracaolinear);
        textoaceleracaolinear = (TextView) findViewById(R.id.textoaceleracaolinear);
        lblLocation = (TextView) findViewById(R.id.textgps);
        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);

       // intent = new Intent(this,gps.class);
        btnend.setBackgroundResource(R.drawable.button_desligado);
        btnend.setEnabled(false);

        InicializaSensores();

        // --- BUTTON INICIAR ATIVIDADE
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (verificaEstadoDo_GPS()) {
                    //GPS
                    LeituraGPS_tracker();


                    // inicia a leitura do sensor
                    RegistaSensores();


                    // OBTEM TEMPO E ATUALIZA TEXTVIEW
                    IniciaContagemDe_Tempo();
                    simpleChronometer.setBase(SystemClock.elapsedRealtime());
                    simpleChronometer.start();
                    //  simpleChronometer.setFormat("Tempo (%s)");



                    btnstart.setEnabled(false);
                    btnstart.setBackgroundResource(R.drawable.button_desligado);
                    btnend.setEnabled(true);
                    btnend.setBackgroundResource(R.drawable.button);
                }

            }
        });

        // --- BUTTON PARAR ATIVIDADE
        btnend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Parar a leitura do acelerometro
                DesregistaSensores();

                //para GPS
                ParaLeitura_GPS();
                t.interrupt();


                simpleChronometer.stop();
                simpleChronometer.setBase(SystemClock.elapsedRealtime());

                limpa_parametros();
                btnstart.setEnabled(true);
                btnstart.setBackgroundResource(R.drawable.button);
                btnend.setEnabled(false);
                btnend.setBackgroundResource(R.drawable.button_desligado);
            }
        });


        // --- BUTTON TANSFERIR / LIMPAR FICHEIRO
        btntranf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //MOSTRAR O CONTEUDO DO FICHEIRO
        verconteudoficheiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mostraficheiro();
            }
        });

        // LISTAR OS SENSORES DO DISPOSITIVO
        listarSensores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListarSensores();
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
                    tituloaceleracaolinear.setVisibility(View.VISIBLE);
                    textoaceleracaolinear.setVisibility(View.VISIBLE);
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
                    tituloaceleracaolinear.setVisibility(View.INVISIBLE);
                    textoaceleracaolinear.setVisibility(View.INVISIBLE);
                    scrollView.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void limpa_parametros(){
        textacelerometro.setText("" , TextView.BufferType.NORMAL);

        if(suporta_gps) {
            textgps.setText("", TextView.BufferType.NORMAL);
        }

        textinstante.setText("PARADO" , TextView.BufferType.NORMAL);
        textgiroscopio.setText("" , TextView.BufferType.NORMAL);
        textproximidade.setText("" , TextView.BufferType.NORMAL);
        textorientation.setText("" , TextView.BufferType.NORMAL);
        textoaceleracaolinear.setText("" , TextView.BufferType.NORMAL);
    }

    public void Mostraficheiro(){
        d_box.Mostraficheiro();
    }

    public void ListarSensores(){
        SensorManager manager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("Lista de Sensores do Dispositivo");
        // Setting Dialog Message
        alertDialog.setMessage(manager.getSensorList(Sensor.TYPE_ALL).toString());

        alertDialog.show();
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

        sensor_lineacel = sensormanager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(sensor_lineacel==null){
            Toast.makeText(getApplicationContext(),"O Dispositivo nao tem acelerapção linear",Toast.LENGTH_LONG).show();
        }else{
            tem_linearacel=true;
        }

    }

    public void RegistaSensores(){
        // inicia a leitura do sensor
        if(tem_giroscopio){
            sensormanager.registerListener(sens_giro,sensor_giro, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(tem_linearacel){
            sensormanager.registerListener(sens_lineacel,sensor_lineacel, SensorManager.SENSOR_DELAY_NORMAL);
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

        if(tem_linearacel) {
            sensormanager.unregisterListener(sens_lineacel);
        }
    }

    public String getDataTempo(){
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

                    tempo_inicial = getDataTempo();

                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String dateString = getDataTempo();
                                textinstante.setText("DATA: "+dateString,TextView.BufferType.NORMAL);
                            }
                        });
                    }

                    tempo_final = getDataTempo();

                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    public void LeituraGPS_tracker(){
       mRequestLocationUpdates = true;
       startLocationUpdates();
    }

    public void ParaLeitura_GPS(){
        mRequestLocationUpdates = false;
        stopLocationUpdates();
            //gps.stopUsingGPS();
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
    class LinearAcel implements SensorEventListener {


        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x ;
            float y ;
            float z ;

            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];

            int acc = sensorEvent.accuracy;

           textoaceleracaolinear.setText("X: "+(int)x+ "   Y: "+(int)y+ "   Z: "+(int)z+"    acc: "+acc, TextView.BufferType.NORMAL);
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







    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLastLocation != null ) {
            double latitude = mLastLocation.getLatitude();
            double longtitude = mLastLocation.getLongitude();
            lblLocation.setText("Latitude: " + latitude + "   Longitude: " + longtitude , TextView.BufferType.NORMAL);
            // lblLocation.setText(latitude + ", " + longtitude);
        } else {

            lblLocation.setText("GPS não esta activado");
            d_box.definicoes_GPS();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private boolean checkPlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                lblLocation.setText("Não suportado", TextView.BufferType.NORMAL);
                //finish();
            }
            suporta_gps = false;
            return false;
        }
        suporta_gps = true;
        return true;
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected boolean verificaEstadoDo_GPS(){


        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        }catch(Exception y){

        }

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (isGPSEnabled) {
            if (locationManager != null) {
                // Register GPSStatus listener for events
                locationManager.addGpsStatusListener(mGPSStatusListener);
                LocationListener  gpslocationListener = new LocationListener() {
                    public void onLocationChanged(Location loc) {}
                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                    public void onProviderEnabled(String provider) {}
                    public void onProviderDisabled(String provider) {}
                };
            }
            return true;
        }else{
            lblLocation.setText("GPS não esta activado");
            d_box.definicoes_GPS();
            return false;
        }
    }

    public GpsStatus.Listener mGPSStatusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch(event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    Toast.makeText(getApplicationContext(), "GPS_SEARCHING", Toast.LENGTH_SHORT).show();
                    System.out.println("TAG - GPS searching: ");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Toast.makeText(getApplicationContext(), "GPS_STOPED", Toast.LENGTH_SHORT).show();
                    System.out.println("TAG - GPS Stopped");
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:


                    Toast.makeText(getApplicationContext(), "GPS_LOCKED", Toast.LENGTH_SHORT).show();




                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    //                 System.out.println("TAG - GPS_EVENT_SATELLITE_STATUS");
                    break;
            }
        }
    };


    @Override
    public void onConnected(Bundle bundle) {
        /*displayLocation();

        if(mRequestLocationUpdates) {
            startLocationUpdates();
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();

        displayLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
        if(mGoogleApiClient.isConnected() && mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }*/


}