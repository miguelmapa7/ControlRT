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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistroPersonas extends AppCompatActivity implements Response.Listener<JSONObject>
        , Response.ErrorListener {

    private EditText cajaIdentificacion, cajaNombre, cajaApellido, cajaDireccion, cajaTelefono;
    private TextView titulo, textoRegistar;
    private Button botonRegistarPersona, botonConsultar;
    private int validarPagina= 0;

    public static String EXTRA_DEVICE_ADDRESS = "";

    RequestQueue rq;
    JsonRequest jsr;

    private static String address = null;

    /**
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_personas);

        //Colocar icono al lado del nombre de la APP
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.icono_covid_paginas);

        titulo = (TextView) findViewById(R.id.textViewConsultarPerosna);
        textoRegistar = (TextView) findViewById(R.id.textViewConsultarPerosna);
        cajaIdentificacion = (EditText) findViewById(R.id.editTextIdentificacion);
        cajaNombre = (EditText) findViewById(R.id.editTextNombre);
        cajaApellido = (EditText) findViewById(R.id.editTextApellido);
        cajaDireccion = (EditText) findViewById(R.id.editTextDireccion);
        cajaTelefono = (EditText) findViewById(R.id.editTextTelefono);
        botonRegistarPersona = (Button) findViewById(R.id.buttonRegistrarPersona);
        botonConsultar = (Button) findViewById(R.id.buttonConsultarPersona);

        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(InicioCovidTest.EXTRA_DEVICE_ADDRESS1);//<-<- PARTE A MODIFICAR >->->

        rq = Volley.newRequestQueue(this);

        desabilitar();

    }  //>>>>>>>>>> OK

    public void iniciarBusquedaPersona(View view) {

        String url = "https://miguelmapa.000webhostapp.com/sesions_personas.php?idciudadao_cc="
                + cajaIdentificacion.getText().toString();

        jsr = new JsonObjectRequest(Request.Method.GET
                , url
                , null
                , this
                , this);
        rq.add(jsr);
    } //>>>>>>>>>> OK

    public void iniciarTest(View view) {
            //Con esto vamos a la página para realizar el Test >>>> OK
            Intent pagRealizarTest = new Intent(this, Manual.class);
            pagRealizarTest.putExtra(EXTRA_DEVICE_ADDRESS, address);
            pagRealizarTest.putExtra("identificacion", cajaIdentificacion.getText().toString());
            startActivity(pagRealizarTest);
            //finish();
    } //>>>>>>>>>> OK

    public void iniciarResgistrarPersona() {
        //Con esto vamos a la página para registrarPersonas
        Intent pagReagistrar = new Intent(this, MainActivity1.class);

        //Código para pasar parametros de una activity a otro
        pagReagistrar.putExtra("identificacion", cajaIdentificacion.getText().toString());
        //pagReagistrar.putExtra(EXTRA_DEVICE_ADDRESS, address);
        startActivity(pagReagistrar);
        //finish();

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this
                , "NO se encontro la persona... ¡Se debe resgistrar! " + error.toString()
                , Toast.LENGTH_LONG).show();
        iniciarResgistrarPersona();
    }

    @Override
    public void onResponse(JSONObject response) {

        Personas personas = new Personas();

        JSONArray jsonArray = response.optJSONArray("datos_mun");
        JSONObject jsonObject = null;

        try {
            jsonObject = jsonArray.getJSONObject(0);
            personas.setIdentificación(jsonObject.optString("idciudadao_cc"));
            personas.setNombre(jsonObject.optString("ciudadaocol_nombre"));
            personas.setApellido(jsonObject.optString("ciudadaocol_apellido"));
            personas.setDireccion(jsonObject.optString("ciudadaocol_direccion"));
            personas.setTelefono(jsonObject.optString("ciudadaocol_celular"));

            cajaNombre.setText(personas.getNombre());
            cajaApellido.setText(personas.getApellido());
            cajaDireccion.setText(personas.getDireccion());
            cajaTelefono.setText(personas.getTelefono());

            botonRegistarPersona.setText("Realizar Test");
            botonRegistarPersona.setEnabled(true);
            botonConsultar.setEnabled(false);
            cajaIdentificacion.setEnabled(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(this
                , personas.getNombre() + " debe realizar el Test ", Toast.LENGTH_LONG).show();

    } //>>>>>>>>>> OK

    public void desabilitar() {
        cajaNombre.setEnabled(false);
        cajaApellido.setEnabled(false);
        cajaDireccion.setEnabled(false);
        cajaTelefono.setEnabled(false);
        botonRegistarPersona.setEnabled(false);
    } //>>>>>>>>>> OK

}
