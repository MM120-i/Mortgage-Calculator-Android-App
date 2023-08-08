package com.example.mcalcpro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import ca.roumani.i2c.*;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener{

     private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.tts = new TextToSpeech(this,this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


    }

    public void onInit(int initStatus){

        if(initStatus == TextToSpeech.SUCCESS) {

            this.tts.setLanguage(Locale.US);

        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1){}

    public void onSensorChanged(SensorEvent event){

        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];

        double a = Math.sqrt(ax * ax + ay * ay + az * az);

        if(a > 10){

            ((EditText) findViewById(R.id.pBox)).setText("");
            ((EditText) findViewById(R.id.aBox)).setText("");
            ((EditText) findViewById(R.id.iBox)).setText("");

            ((TextView) findViewById(R.id.output)).setText("");
        }
    }
    public void buttonClicked(View view) {

        EditText pBox = (EditText) findViewById(R.id.pBox);
        EditText aBox = (EditText) findViewById(R.id.aBox);
        EditText iBox = (EditText) findViewById(R.id.iBox);

        String p = pBox.getText().toString();
        String a = aBox.getText().toString();
        String i = iBox.getText().toString();

        try{

            MPro mp = new MPro();

            mp.setInterest(i);
            mp.setPrinciple(p);
            mp.setAmortization(a);

            String s = "Monthly Payment = " + mp.computePayment("%,.2f");
            String monthlyPayment = s;

            s += "\n\n";
            s +="By making this payments monthly for " + a + " years, the mortgage will be paid in full. "
              + "But if you terminate the mortgage on its nth anniversary, the balance still owing depends on n as shown below:";
            s +="\n\n" + "\n\n";
            s += String.format("%8s", "n") + String.format("%16s","Balance");
            s +="\n\n";
            s += String.format("%8d", 0) + mp.outstandingAfter(0, "%,16.0f");
            s += "\n\n";
            s += String.format("%8d", 1) + mp.outstandingAfter(1, "%,16.0f");
            s += "\n\n";
            s += String.format("%8d", 2) + mp.outstandingAfter(2, "%,16.0f");
            s += "\n\n";
            s += String.format("%8d", 3) + mp.outstandingAfter(3, "%,16.0f");
            s += "\n\n";
            s += String.format("%8d", 4) + mp.outstandingAfter(4, "%,16.0f");
            s += "\n\n";
            s += String.format("%8d", 5) + mp.outstandingAfter(5, "%,16.0f");
            s += "\n\n";
            s += String.format("%8d", 10) + mp.outstandingAfter(10, "%,16.0f");
            s += "\n\n";
            s += String.format("%8d", 15) + mp.outstandingAfter(15, "%,16.0f");
            s += "\n\n";
            s += String.format("%8d", 20) + mp.outstandingAfter(20, "%,16.0f");

            ((TextView) findViewById(R.id.output)).setText(s);
            tts.speak(monthlyPayment, TextToSpeech.QUEUE_FLUSH, null);
        }
        catch(Exception e){

            Toast label = Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT);
            label.show();

        }
    }
}