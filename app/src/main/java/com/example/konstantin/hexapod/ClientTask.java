package com.example.konstantin.hexapod;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Konstantin on 06.03.2018.
 */

public class ClientTask extends AsyncTask<String,Void,Void>
{
    private Socket s=null;
    private String adress;
    private int port;
    private PrintWriter pw=null;
    private BufferedReader bufferedReader=null;
    private String message;

    ClientTask(String adress, int port)
    {
        this.adress=adress;
        this.port=port+1;

    }

    @Override
    protected Void doInBackground(String... params) {
        message = params[0];
       if(pw != null) pw.flush();
        try
        {
            s = new Socket(adress,port);
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
            bufferedReader=new BufferedReader(new InputStreamReader(s.getInputStream()));
            pw.println(message);
            pw.flush();
            pw.close();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
