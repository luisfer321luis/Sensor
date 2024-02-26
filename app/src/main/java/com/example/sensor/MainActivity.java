package com.example.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.example.sensor.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityMainBinding binding;
    private SensorManager sensorManager;
    private Sensor magnetometro;
    private Sensor acelerometro;
    private float[] valoresAcelerometro = new float[3];
    private float[] valoresMagnetometro = new float[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == acelerometro) {
            valoresAcelerometro = event.values;
        } else if (event.sensor == magnetometro) {
            valoresMagnetometro = event.values;
        }

        float[] matrizRotacion = new float[9];
        float[] orientacion = new float[3];

        SensorManager.getRotationMatrix(matrizRotacion, null, valoresAcelerometro, valoresMagnetometro);
        SensorManager.getOrientation(matrizRotacion, orientacion);

        float grados = (float) Math.toDegrees(orientacion[0]);

        String direccion = getDirectionString(grados);
        binding.direccion.setText("Direccion: " + direccion);
        float gradosAux = grados;
        if(grados < 0){
            gradosAux = 360 + grados;
        }
        binding.grados.setText("Grados: " + Math.abs(Math.round(gradosAux)) + "º");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private String getDirectionString(float grados) {
        String[] direcciones = {"Norte", "Noreste", "Este", "Sureste", "Sur", "Suroeste", "Oeste", "Noroeste", "Norte"};
        float gradosAux = grados;
        if(grados < 0){
            gradosAux = 360 + grados;
        }
        int pos = Math.abs((int)Math.floor((gradosAux+22.5f) / 45) % 8);
        return direcciones[Math.abs(pos)];
    }
}