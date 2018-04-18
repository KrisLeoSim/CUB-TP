package com.example.kris.cubtp;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Ficheiro {

    private String delimitador = ";";
    private String cabecalho = "lat"+delimitador+"log"+delimitador+"alt"+delimitador+"timestamp"+delimitador+"x_acc"+delimitador+"y_acc"+delimitador+"z_acc"+delimitador+"x_gyro"+delimitador+"y_gyro"+delimitador+"z_gyro"+delimitador+"x_pro"+delimitador+"azi_ori"+delimitador+"pit_ori"+delimitador+"roll_ori"+delimitador+"x_accl"+delimitador+"y_accl"+delimitador+"z_accl"+delimitador+"activity\n";

    private Context context;
    private String FileName;
    final String chave = "nome_fich";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int cria = 0;
    private int nlinhas;

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

    public String numeroRegistos(){
        int cAndar = 0,cConduzir = 0, cSaltar = 0, cSubir = 0, cDescer = 0;
        String resultaro = "";
        try{
            FileInputStream fis = context.openFileInput(FileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            while ( br.readLine() != null){
                if ( br.readLine().contains("WALKING")){
                    cAndar++;
                }else{
                    if( br.readLine().contains("DRIVING")){
                        cConduzir++;
                    }else{
                        if(br.readLine().contains("JUMP")){
                            cSaltar++;
                        }else{
                            if(br.readLine().contains("GO_UPSTAIRS")){
                                cSubir++;
                            }
                            else{ // igual a descer escadas
                                if(br.readLine().contains("GO_DOWNSTAIRS")) {
                                    cDescer++;
                                }else{
                                    // é o cabeçalho
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(context,"Erro a ler o ficheiro",Toast.LENGTH_LONG).show();
        }

        resultaro = "  Andar (" + cAndar + ")" + "\n" + "  Conduzir (" + cConduzir + ")" + "\n" + "  Saltar (" + cSaltar + ")" + "\n" + "  Subir Escadas (" + cSubir + ")" + "\n" + "  Descer Escadas (" + cDescer + ")";
        return resultaro;
    }


    public void saveFile(String text){

        try {
            FileOutputStream fos = context.openFileOutput(FileName,Context.MODE_APPEND);
            fos.write(text.getBytes());
            fos.close();
            //Toast.makeText(context,"Saved!",Toast.LENGTH_LONG).show();
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

    public void transfer_fich(){
        new LongOperation().execute();
    }


    private class LongOperation extends AsyncTask<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                JSch ssh = new JSch();
                Session session = ssh.getSession("cubistudent", "urbysense.dei.uc.pt", 22);
              // Remember that this is just for testing and we need a quick access, you can add an identity and known_hosts file to prevent
                // Man In the Middle attacks
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.setPassword("mis_cubi_2018");

                session.connect();
                Channel channel = session.openChannel("sftp");
                channel.connect();

                ChannelSftp sftp = (ChannelSftp) channel;

                sftp.cd("/home/cubistudent/a21230192_a21230463");
                // If you need to display the progress of the upload, read how to do it in the end of the article
            // use the put method , if you are using android remember to remove "file://" and use only the relative path
                sftp.put(context.getFileStreamPath(FileName).getPath(), FileName);


                channel.disconnect();
                session.disconnect();



            } catch (JSchException e) {
                System.out.println(e.getMessage().toString());
                e.printStackTrace();
            } catch (SftpException e) {
                System.out.println(e.getMessage().toString());
                e.printStackTrace();
            }

            //System.out.println("AQUIIII" + context.getFileStreamPath(FileName).getPath());
            return "Terminado";
        }


        @Override
        protected void onPostExecute(String result) {
            Log.d("PostExecuted", result);

            apaga_ficheiro();
            Toast.makeText(context.getApplicationContext(), "Ficheiro transferido", Toast.LENGTH_SHORT).show();
        }
    }

}
