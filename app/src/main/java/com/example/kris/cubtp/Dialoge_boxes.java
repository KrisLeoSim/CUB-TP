package com.example.kris.cubtp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

public class Dialoge_boxes {
    private final Context mContext;
    private final Ficheiro ficheiro;

    public Dialoge_boxes(Context mContext, Ficheiro fich) {
        this.mContext = mContext;
        this.ficheiro = fich;
    }

    public void definicoes_GPS(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS definições");

        // Setting Dialog Message
        alertDialog.setMessage("GPS não está activado. Pretende activa-lo?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void Mostraficheiro(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        // Setting Dialog Title
        alertDialog.setTitle("Ficheiro CUBTP");
        // Setting Dialog Message

        alertDialog.setMessage(ficheiro.readFile());

        alertDialog.show();
    }
}
