package com.example.kris.cubtp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class GPS implements LocationListener {

    Context context;

    public GPS(Context c){
        context = c;
    }


    public Location getLocation(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context,"Permissão nao aprovada",Toast.LENGTH_SHORT).show();
            return null; // Caso nao se tiver a  permissao FINE_LOCATION
        }

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Boolean isGPSEnable = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(isGPSEnable){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,6000,10,this);
            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return l;
        }else{
            Toast.makeText(context,"Falta Ativar o GPS",Toast.LENGTH_LONG).show();
        }

        return null; // caso tenha permissao mas o GPS nao esteja ligado

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
