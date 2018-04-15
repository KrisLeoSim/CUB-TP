package com.example.kris.cubtp;



import android.content.Context;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Ficheiro {
    String cabecalho = "lat,log,alt,timestamp,x_acc,y_acc,z_acc,x_gyro,y_gyro,z_gyro,x_pro,azi_ori,pit_ori,roll_ori,x_accl,y_accl,z_accl,activity";
    Context context;
    String FileName = "CUBFILE.csv";

    public Ficheiro(Context c){
        this.context = c;
    }

    public void saveFile(String text){
        try {
            FileOutputStream fos = context.openFileOutput(FileName,Context.MODE_APPEND);
            fos.write(text.getBytes());
            fos.close();
            Toast.makeText(context,"Saved!",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"Erro a salvar o ficheiro",Toast.LENGTH_LONG).show();
        }
    }

    public String readFile(){
        String text = "";

        try{
            FileInputStream fis = context.openFileInput(FileName);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            text = new String(buffer);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"Erro a ler o ficheiro",Toast.LENGTH_LONG).show();
        }

        return text;
    }

    public String GetNomeFicheiro(){
        String nomeUnico;

            nomeUnico = "CUB_KS_" + "FALTA POR O GETTIME" + ".csv";
            //Toast.makeText(context,nomeUnico,Toast.LENGTH_LONG).show();
        return nomeUnico ;
    }


}
