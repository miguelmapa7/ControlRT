package edu.cecar.CovidTest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity1 extends AppCompatActivity implements Response.Listener<JSONObject>
        , Response.ErrorListener{

    private EditText cajaIdentificacion, cajaNombre, cajaApellido, cajaDireccion, cajaTelefono;
    private TextView textoRegistar;
    private Button botonRegistarPersona;
    private int validarRegistro = 0;

    RequestQueue rq1;
    JsonRequest jsr1;

    private static String address = null;
    public static String EXTRA_DEVICE_ADDRESS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        //Colocar icono al lado del nombre de la APP
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.icono_covid_paginas);

        textoRegistar = (TextView) findViewById(R.id.textViewResgistraPersona3);
        cajaIdentificacion = (EditText) findViewById(R.id.editTextIdentificacion3);
        cajaNombre = (EditText) findViewById(R.id.editTextNombre3);
        cajaApellido = (EditText) findViewById(R.id.editTextApellido3);
        cajaDireccion = (EditText) findViewById(R.id.editTextDireccion3);
        cajaTelefono = (EditText) findViewById(R.id.editTextTelefono3);
        botonRegistarPersona = (Button) findViewById(R.id.buttonRegistrarPersona3);

        cajaIdentificacion.setText(getIntent().getStringExtra("identificacion").toString());
        cajaIdentificacion.setEnabled(false);

        rq1 = Volley.newRequestQueue(this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this
                , "NO se pudo resgistrar la persona... ¡Volver a intentar! " + error.toString()
                , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {

        Toast.makeText(this
                ,"Se se Registro la persona con identificación: " + cajaIdentificacion.getText().toString()
                , Toast.LENGTH_LONG).show();
        finish();

    }

    public void resgistarPersonas(View view){

        String urll = "https://miguelmapa.000webhostapp.com/registrarpersonas1.php?idciudadao_cc="
                + cajaIdentificacion.getText().toString()
                +"&ciudadaocol_nombre=" + cajaNombre.getText().toString()
                +"&ciudadaocol_apellido=" + cajaApellido.getText().toString()
                +"&ciudadaocol_direccion=" + cajaDireccion.getText().toString()
                +"&ciudadaocol_celular=" + cajaTelefono.getText().toString();

        jsr1 = new JsonObjectRequest(Request.Method.GET
                , urll
                , null
                , this
                , this);
        rq1.add(jsr1);
    }
}
