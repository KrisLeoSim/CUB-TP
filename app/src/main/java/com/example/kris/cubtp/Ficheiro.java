package com.example.kris.cubtp;



import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Ficheiro {

    Context context;
    String FileName;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public Ficheiro(Context c){
        this.context = c;
        sharedPref= context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor=sharedPref.edit();

        criaFicheiro();
    }

    public String gera_nome(){
        //...
        return FileName;
    }

    private void criaFicheiro(){
        sharedPref.getString("nome_fich",FileName);

        File file = new File(FileName);
        if(file.exists()){
            System.out.println("Ficheiro existe ");


        }else{
            System.out.println("Not find file ");
            // Save your string in SharedPref

            FileName = gera_nome();
            //gera nome e guarda
            editor.putString("nome_fich", FileName);
            editor.commit();
            saveFile(gera_nome());

        }
        


    }

    public void enviar_fich(){


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

    public String getDataTempo(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String sDate = (String) simpledateformat.format(calendar.getTime());
        return sDate;
    }
}
