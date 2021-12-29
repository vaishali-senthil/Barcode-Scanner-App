package com.example.barcodescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



//-------------------------
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousChannelGroup;
//------------------------


import com.airbnb.lottie.LottieAnimationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    String mine,me;
    Button scanBtn;
    LottieAnimationView anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);

        anim= (LottieAnimationView) findViewById(R.id.animationview);
        anim.setAnimation(R.raw.trial);
    }

    @Override
    public void onClick(View view)
    {

        scanCode();
    }

    public void scanCode()
    {
        IntentIntegrator integrator =  new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning  Code");
        integrator.initiateScan();
    }

    public void finish()
    {
        String message = me;
        BackgroundTask b1 = new BackgroundTask();
        b1.execute(message);
        anim.playAnimation();


        Toast.makeText(this, "Done successfully", Toast.LENGTH_SHORT).show();
    }

    IntentResult result;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

       result  = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()!=null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                me = result.getContents();
                mine = (builder.setMessage(result.getContents()).toString());
                builder.setTitle("Scanning Result");
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        scanCode();
                    }
                }).setNegativeButton("finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       finish();

                    }
                });



                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                Toast.makeText(this, "No Result", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
    class BackgroundTask extends AsyncTask<String,Void,Void>
    {
        Socket s;
        PrintWriter writer;
        @Override
        protected Void doInBackground(String... voids) {
            try{
                String message = voids[0];
                s = new Socket("192.168.1.9",6000);
                writer = new PrintWriter(s.getOutputStream());
                writer.write(message);
                writer.flush();
                writer.close();
                s.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }




}