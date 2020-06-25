package edu.cecar.CovidTest;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.controlrt.R;

import static edu.cecar.CovidTest.DispositivosBT.EXTRA_DEVICE_ADDRESS;


public class InicioCovidTest extends AppCompatActivity {

    private static String address = null;
    public static String EXTRA_DEVICE_ADDRESS1 = "";

    /**
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_covid);

        //Colocar icono al lado del nombre de la APP
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.icono_covid_paginas); // probando

        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = getIntent().getStringExtra(EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->

    }

    /**
     * @param view
     */
    public void irNuevaPaginaConsultarPersonas(View view) {
        Intent pagConMA = new Intent(this, RegistroPersonas.class);
        pagConMA.putExtra(EXTRA_DEVICE_ADDRESS1, address);
        startActivity(pagConMA);
        //finish();
    }

    //Método para ir a Dispositivos Bluetooth
    public void irNuevaPaginaDispositi(View view) {
        Intent pagDisposi = new Intent(this, DispositivosBT.class);
        startActivity(pagDisposi);
        //finish();
    }

}
