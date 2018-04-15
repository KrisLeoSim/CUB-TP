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

    private String cabecalho = "lat,log,alt,timestamp,x_acc,y_acc,z_acc,x_gyro,y_gyro,z_gyro,x_pro,azi_ori,pit_ori,roll_ori,x_accl,y_accl,z_accl,activity";
    private Context context;
    private String FileName;
    final String chave = "nome_fich";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int cria = 0;

    public Ficheiro(Context c){
        this.context = c;


        //cria ficheiro e uma verificaçao ele verifica se ha algum ficheiro em memoria
        //se nao houver cria.o caso esteja apenas recebe o nome do ficheiro que esta a ser alterado
        criaFicheiro();
    }

    private void criaFicheiro(){

        sharedPref= context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor=sharedPref.edit();

        //faz get nome em memoria para ele se voltar a relembrar do ficheiro caso se volte a aplicaçao
        FileName = sharedPref.getString(chave,FileName);

        //verifica se da para aceder ao ficheiro

        if(FileName != null) {
            File file = new File(FileName);
            if (file.exists()) {
                System.out.println("Ficheiro existe " + FileName);
                cria = 0;
            } else {
                System.out.println("Ficheiro : " + FileName + " nao encontrado ");
                cria = 1;
            }
        }else{
            cria = 1;
        }

        if(cria == 1){
            System.out.println("Ficheiro : " + FileName + " nao encontrado ");
            // Save your string in SharedPref


            //gera nome e guarda na memoria
            FileName = GetNomeFicheiro();
            editor.putString(chave, FileName);
            editor.commit();

            //add o cabeça-lho
            saveFile(cabecalho);
            System.out.println("Criei o ficheiro: " + FileName);

        }
    }

    public void enviar_fich(){

        //envia ficheiro

        //apaga e faz um novo
        apaga_ficheiro();
    }

    public void apaga_ficheiro(){
        //apaga ficheiro da memoria interna
        context.deleteFile(FileName);

        //apaga ficheiro da memoria partilhada
        editor.remove(chave);
        editor.apply();

        //abre um novo para estar preparado a receber escrita
        criaFicheiro();
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
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd_MM_yyyyHH_mm_ss");
        String sDate = (String) simpledateformat.format(calendar.getTime());
        return sDate;
    }

    public String GetNomeFicheiro(){
        String nomeUnico;

            nomeUnico = "CUB_KS_" + getDataTempo() + ".csv";
            System.out.println("Gerei o nome: "+ nomeUnico);
            //Toast.makeText(context,nomeUnico,Toast.LENGTH_LONG).show();
        return nomeUnico ;
    }



}
