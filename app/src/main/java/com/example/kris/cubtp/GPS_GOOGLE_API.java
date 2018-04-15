package com.example.kris.cubtp;



import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;



public class GPS_GOOGLE_API implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final Context mContext;

    // private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    private boolean mRequestLocationUpdates = false;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;

    private TextView lblLocation;
    private Button btnShowLocation, btnStartLocationUpdates;


    public GPS_GOOGLE_API(Context mContext) {
        this.mContext = mContext;

        lblLocation = (TextView) ((Activity) mContext).findViewById(R.id.textgps);
      //  btnShowLocation = (Button) ((Activity) mContext).findViewById(R.id.buttonShowLocation);
        //btnStartLocationUpdates = (Button) ((Activity) mContext).findViewById(R.id.buttonLocationUpdates);

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        displayLocation();

       // togglePeriodLocationUpdates();
    }



      public void mstart(){
            if(mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }


        public void monresume() {

            checkPlayServices();
            if(mGoogleApiClient.isConnected() && mRequestLocationUpdates) {
                startLocationUpdates();
            }
        }


        public void monStop() {

            if(mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        public void monPause() {
            stopLocationUpdates();
        }

        private void displayLocation() {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                lblLocation.setText("LAT: "+latitude+"\nLON: "+longitude+"\nALT: ",TextView.BufferType.NORMAL);

                //lblLocation.setText(latitude + ", " + longtitude);
            } else {
                lblLocation.setText("Couldn't get the location. Make sure location is enabled on the device");
            }
        }



       /*private void togglePeriodLocationUpdates() {
            if(!mRequestLocationUpdates) {
                //btnStartLocationUpdates.setText(getString(R.string.btn_stop_location_updates));

                mRequestLocationUpdates = true;

                startLocationUpdates();
            } else {
                //btnStartLocationUpdates.setText(getString(R.string.btn_start_location_updates));

                mRequestLocationUpdates = false;

                stopLocationUpdates();
            }
        }*/

        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks( this)
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
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
            if(resultCode != ConnectionResult.SUCCESS) {
                if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, ((Activity) mContext), PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG)
                            .show();
                   // finish();
                }
                return false;
            }
            return true;
        }

        protected void startLocationUpdates() {
            mRequestLocationUpdates = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        protected void stopLocationUpdates() {
            mRequestLocationUpdates = false;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        @Override
        public void onConnected(Bundle bundle) {
            displayLocation();

            if(mRequestLocationUpdates) {
                startLocationUpdates();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;

           // Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();

            displayLocation();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.i("FAILED", "Connection failed: " + connectionResult.getErrorCode());
        }
    }
