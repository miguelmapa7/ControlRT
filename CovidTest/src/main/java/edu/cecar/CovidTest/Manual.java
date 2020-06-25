package edu.cecar.CovidTest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.controlrt.R;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class Manual extends AppCompatActivity implements Response.Listener<JSONObject>
        , Response.ErrorListener{
// if(temperatura > 37.5 && temperatura <= 39.5)

    public static String EXTRA_DEVICE_ADDRESS1;

    Button arriba, abajo;
    TextView titulo, grados;
    ImageButton home;
    CheckBox dolorGarganta, tos, congestionNasal, fatiga, dificultaRespiratoria, ninguna;
    EditText identifiacion;

    String a = "";

    String valorDolorGarganta = "", fiebreVariable = "", cogestion_nasal = "", tos_variable = "",
            dificultad_respirar = "", fatigaValriable = "", ninguno = "";
    float gradosVariable = 0;


    //-------------------------------------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;
    //-------------------------------------------

    private static final int RECOGNIZE_SPEECH_ACTIVITY = 1;

    RequestQueue rq2;
    JsonRequest jsr2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);


        //Colocar icono al lado del nombre de la APP
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.icono_covid_paginas);

        titulo = (TextView) findViewById(R.id.textView2Titulo);
        identifiacion = (EditText) findViewById(R.id.editTextIdentificacion);
        dolorGarganta = (CheckBox) findViewById(R.id.checkBoxDGarganta);
        tos = (CheckBox) findViewById(R.id.checkBoxTos);
        congestionNasal = (CheckBox) findViewById(R.id.checkBoxCNasal);
        fatiga = (CheckBox) findViewById(R.id.checkBoxDFatiga);
        dificultaRespiratoria = (CheckBox) findViewById(R.id.checkBoxDRespirar);
        ninguna = (CheckBox) findViewById(R.id.checkBoxNinguno);
        grados = (TextView) findViewById(R.id.textTemperatura);
        arriba = (Button) findViewById(R.id.button3Arriba);
        abajo = (Button) findViewById(R.id.button4Abajo);
        home = (ImageButton) findViewById(R.id.imageButton4Home);

        rq2 = Volley.newRequestQueue(this);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        grados.setText(dataInPrint + "");//<-<- PARTE A MODIFICAR >->->
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };


        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        //VerificarEstadoBT();

    }

    //Método para ir a Inicio
    public void irNuevaPaginaInicio(View view) {
        Intent pagInicio = new Intent(this, InicioCovidTest.class);
        startActivity(pagInicio);
        //finish();
    }

    public void sospechoso() {
        if (btAdapter.isEnabled()) {
            //Datos mandados por BlueTooth
            MyConexionBT.write("a");
            Toast.makeText(this, "¡Eres Sospechoso...!  a ", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "¡Debe Activar el Bluetooth!  e ", Toast.LENGTH_SHORT).show();
        }
    }
    public void no_sospechoso() {
        if (btAdapter.isEnabled()) {
            //Datos mandados por BlueTooth
            MyConexionBT.write("e");
            Toast.makeText(this, "¡NO Eres Sospechoso...!  a ", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "¡Debe Activar el Bluetooth!  e ", Toast.LENGTH_SHORT).show();
        }
    }

    public void abajo(View view) {

        if (btAdapter.isEnabled()) {
        //Datos mandados por BlueTooth
        MyConexionBT.write("t");
        Toast.makeText(this, "¡Se pide la temperatura!  e ", Toast.LENGTH_SHORT).show();
        }else{
        Toast.makeText(this, "¡Debe Activar el Bluetooth!  e ", Toast.LENGTH_SHORT).show();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();

        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(RegistroPersonas.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
        identifiacion.setText(getIntent().getStringExtra("identificacion").toString());
        identifiacion.setEnabled(false);

        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();


    }


    @Override
    public void onPause() {
        super.onPause();
        /*try
        { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();

        } catch (IOException e2) {}*/


        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();


    }


    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this
                , "NO se pudo resgistrar el test... ¡Volver a intentar! " + error.toString()
                , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(this
                ,"Se registro el Test: " , Toast.LENGTH_LONG).show();

        if (valorDolorGarganta.equals("SI")  && fiebreVariable.equals("SI") && cogestion_nasal.equals("SI")
                && tos_variable.equals("SI") && dificultad_respirar.equals("SI") && fatigaValriable.equals("SI")){
            sospechoso();
        }else{
            no_sospechoso();
        }

    }

    public void resgistarTest(View view){

        if(dolorGarganta.isChecked()){
            valorDolorGarganta = "SI";
            System.out.println(valorDolorGarganta);
        }else{
            valorDolorGarganta = "NO";
            System.out.println("valor de la variable Dolor ganta*****************"+valorDolorGarganta);
        }
        gradosVariable= Float.parseFloat(grados.getText().toString());
        if(gradosVariable > 37.5 && gradosVariable <= 39.5 ){
            fiebreVariable = "SI";
        }else{
            fiebreVariable = "NO";
        }
        //fiebreVariable = "NO";
        System.out.println(grados.getText().toString());
        System.out.println();
        if(congestionNasal.isChecked()){
            cogestion_nasal = "SI";
        }else{
            cogestion_nasal = "NO";
        }
        if(tos.isChecked()){
            tos_variable = "SI";
        }else{
            tos_variable = "NO";
        }
        if(dificultaRespiratoria.isChecked()){
            dificultad_respirar = "SI";
        }else{
            dificultad_respirar = "NO";
        }
        if(fatiga.isChecked()){
            fatigaValriable = "SI";
        }else{
            fatigaValriable = "NO";
        }
        if(ninguna.isChecked()){
            ninguno = "SI";
        }else{
            ninguno = "NO";
        }
/*
        if (valorDolorGarganta.equals("SI")  && fiebre.equals("SI") && cogestion_nasal.equals("SI")
                && tos_variable.equals("SI") && dificultad_respirar.equals("SI") && fatigaValriable.equals("SI")){
            sospechoso();
        }else{
            no_sospechoso();
        }
*/

        String url2 = "https://miguelmapa.000webhostapp.com/test.php?id_persona="
                + getIntent().getStringExtra("identificacion").toString()
                +"&fiebre=" +  fiebreVariable
                +"&dolor_garganta=" + valorDolorGarganta
                +"&conges_nasal=" + cogestion_nasal
                +"&tos=" + tos_variable
                +"&dific_respirar=" + dificultad_respirar
                +"&fatiga=" + fatigaValriable
                +"&ninguno=" +  ninguno;

        jsr2 = new JsonObjectRequest(Request.Method.GET
                , url2
                , null
                , this
                , this);
        rq2.add(jsr2);


    }
///////////////////////////////////////////////////////////////////////////////////////////////////
    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //Envio de trama
        public void write(String input) {
            try {
                mmOutStream.write(input.getBytes());
            } catch (IOException e) {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


}
