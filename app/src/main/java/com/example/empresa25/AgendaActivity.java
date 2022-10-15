package com.example.empresa25;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AgendaActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{
    EditText jetid,jettitulo,jetnota,jethora;
    CheckBox jcbacti;
    RequestQueue rq;
    JsonRequest jrq;
    String id,titulo,nota,hora;
    byte sw;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        getSupportActionBar().hide();
        jetid=findViewById(R.id.etId);
        jettitulo=findViewById(R.id.etTitulo);
        jetnota=findViewById(R.id.etNota);
        jethora=findViewById(R.id.etHora);
        jettitulo=findViewById(R.id.etTitulo);
        jcbacti=findViewById(R.id.cbact);
        rq= Volley.newRequestQueue(this);
        sw=0;
    }
    public void Guardar(View view){
        id=jetid.getText().toString();
        titulo=jettitulo.getText().toString();
        nota=jetnota.getText().toString();
        hora=jethora.getText().toString();
        if (id.isEmpty() || titulo.isEmpty() || nota.isEmpty() || hora.isEmpty()){
            Toast.makeText(this, "Todos los datos son regueridos", Toast.LENGTH_SHORT).show();
            jetid.requestFocus();
        }else {
            if (sw == 0)
                    url = "http://172.16.60.24:8080/webserver12/agenda/registrocorreo.php";
            else {
                url = "http://172.16.60.24:8080/webserver12/agenda/actualizar.php";
                sw=0;
            }
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            Limpiar_campos();
                            Toast.makeText(getApplicationContext(), "agenda de usuario realizado!", Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "agenda de usuario incorrecto!", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ID",jetid.getText().toString().trim());
                    params.put("TITULO", jettitulo.getText().toString().trim());
                    params.put("NOTA",jetnota.getText().toString().trim());
                    params.put("HORA",jethora.getText().toString().trim());
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);

        }
    }
    public void ConsultarAgenda (View view){
        id=jetid.getText().toString();
        if (id.isEmpty()){
            Toast.makeText(this, "Usuario es requerido para la busqueda", Toast.LENGTH_SHORT).show();
            jetid.requestFocus();
        }else {
            url = "http://172.16.60.24:8080/webserver12/agenda/consultar.php?id="+id;
            jrq = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
            rq.add(jrq);
        }
    }
    public void Limpiar(View view){
        Limpiar_campos();
    }

    public void Regresar(View view){
        Intent intmain=new Intent(this,MainActivity.class);
        startActivity(intmain);
    }

    private void Limpiar_campos(){
        sw=0;
        jetid.setText("");
        jettitulo.setText("");
        jetnota.setText("");
        jethora.setText("");
        jcbacti.setChecked(false);
        jetid.requestFocus();

    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "id no registrado", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResponse(JSONObject response) {
        sw=1;
        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject = null;
        Toast.makeText(this, "registrado", Toast.LENGTH_SHORT).show();
        try {
            jsonObject=jsonArray.getJSONObject(0); //Posicion 0 del arreglo...
            jettitulo.setText(jsonObject.optString("TITULO"));
            jetnota.setText(jsonObject.optString("NOTA"));
            jethora.setText(jsonObject.optString("HORA"));
            if (jsonObject.optString("ACTIVO").equals("si"))
                jcbacti.setChecked(true);
            else
                jcbacti.setChecked(false);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void Anular(View view) {
        id = jetid.getText().toString();
        if (id.isEmpty()) {
            Toast.makeText(this, "El usuario es reguerido", Toast.LENGTH_SHORT).show();
            jetid.requestFocus();
        } else {

            url = "http://172.16.60.24:8080/webserver12/agenda/anular.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Limpiar_campos();
                            Toast.makeText(getApplicationContext(), "Registro de usuario anulado!", Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Registro de usuario no anulado!", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ID", jetid.getText().toString().trim());
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);

        }
    }
}