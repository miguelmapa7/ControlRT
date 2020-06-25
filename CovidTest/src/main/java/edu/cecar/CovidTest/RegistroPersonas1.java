package edu.cecar.CovidTest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.controlrt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistroPersonas1 extends AppCompatActivity implements Response.Listener<JSONObject>
        , Response.ErrorListener {

    private EditText cajaIdentificacion, cajaNombre, cajaApellido, cajaDireccion, cajaTelefono;
    private TextView textoRegistar;
    private Button botonRegistarPersona;
    private int validarRegistro = 0;

    RequestQueue rq1;
    JsonRequest jsr1;

    private static String address = null;
    public static String EXTRA_DEVICE_ADDRESS = "";

    /**
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_personas1);

        //Colocar icono al lado del nombre de la APP
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.icono_covid_paginas);

        textoRegistar = (TextView) findViewById(R.id.textViewConsultarPerosna);
        cajaIdentificacion = (EditText) findViewById(R.id.editTextIdentificacion);
        cajaNombre = (EditText) findViewById(R.id.editTextNombre);
        cajaApellido = (EditText) findViewById(R.id.editTextApellido);
        cajaDireccion = (EditText) findViewById(R.id.editTextDireccion);
        cajaTelefono = (EditText) findViewById(R.id.editTextTelefono);
        botonRegistarPersona = (Button) findViewById(R.id.buttonRegistrarPersona);

        //Consigue la direccion MAC desde DeviceListActivity via intent
        //Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        //address = intent.getStringExtra(RegistroPersonas.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->

        rq1 = Volley.newRequestQueue(this);

        cajaIdentificacion.setEnabled(false);
        //cajaIdentificacion.setText(getIntent().getStringExtra("identificacion").toString());
        //address = getIntent().getStringExtra(EXTRA_DEVICE_ADDRESS);

    }

    public void iniciarTest() {
        if(validarRegistro == 1){
            //Con esto vamos a la página para realizar el Test
            Intent pagRealizarTest = new Intent(this, Manual.class);

            //Código para pasar parametros de una activity a otro
            pagRealizarTest.putExtra("identificacion", cajaIdentificacion.getText().toString());
            pagRealizarTest.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(pagRealizarTest);
            finish();
        }else{

        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        validarRegistro = 0;
        Toast.makeText(this
                , "NO se pudo resgistrar la persona... ¡Volver a intentar! " + error.toString()
                , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(this
                ,"Se se Registro la persona con identificación: " + cajaIdentificacion.getText().toString()
                , Toast.LENGTH_LONG).show();
            validarRegistro = 1;

        Toast.makeText(this
                ," debe realizar el Test ", Toast.LENGTH_SHORT).show();

        iniciarTest();

    }

    public void resgistarPersonas(View view){

        String urll = "https://miguelmapa.000webhostapp.com/registrarpersonas1.php?idciudadao_cc="
                +cajaIdentificacion.getText().toString()
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
