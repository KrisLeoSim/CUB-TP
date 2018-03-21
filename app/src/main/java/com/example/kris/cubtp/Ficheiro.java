package com.example.kris.cubtp;


import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Ficheiro {

    Context context;
    String FileName = "CUBFILE.txt";

    public Ficheiro(Context c){
        context = c;
    }

    public void saveFile(String text){
        try {
            FileOutputStream fos = context.openFileOutput(FileName,Context.MODE_PRIVATE);
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


}
