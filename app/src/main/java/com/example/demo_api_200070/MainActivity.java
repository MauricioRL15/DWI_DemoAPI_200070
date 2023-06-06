package com.example.demo_api_200070;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btnGuardar;
    private Button btnBuscar;
    private Button btnActualizar;
    private Button btnEliminar;
    private EditText etCodigoBaras;
    private EditText etDescripcion;
    private EditText etMarca;
    private EditText etPrecioCompra;
    private EditText etPrecioVenta;
    private EditText etExistencia;
    private ListView lvProductos;
    private RequestQueue colaPeticiones;
    private JsonArrayRequest jsonArrayRequest;
    private ArrayList<String> origenDatos = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private String url = "http://10.10.62.17:3300";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEliminar = findViewById(R.id.btnEliminar);
        etCodigoBaras = findViewById(R.id.etCodigoBaras);
        etDescripcion = findViewById(R.id.etDescripcion);
        etMarca = findViewById(R.id.etMarca);
        etPrecioCompra = findViewById(R.id.etPrecioCompra);
        etPrecioVenta = findViewById(R.id.etPrecioVenta);
        etExistencia = findViewById(R.id.etExistencia);
        lvProductos = findViewById(R.id.lvProductos);
        colaPeticiones = Volley.newRequestQueue(this);
        listarProductos();

        //Button update
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject newproduct = new JSONObject();
                try {
                    newproduct.put("codigobarras",etCodigoBaras.getText().toString());
                    newproduct.put("descripcion",etDescripcion.getText().toString());
                    newproduct.put("marca",etMarca.getText().toString());
                    newproduct.put("preciocompra",Float.parseFloat(etPrecioCompra.getText().toString()));
                    newproduct.put("precioventa",Float.parseFloat(etPrecioVenta.getText().toString()));
                    newproduct.put("existencias",Float.parseFloat(etExistencia.getText().toString()))
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url+"/actualizar/"+etCodigoBaras.getText().toString(),
                        newproduct,

                );*/
            }
        });

        //Button Search:
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest peticion = new JsonObjectRequest(
                        Request.Method.GET,
                        url + "/" + etCodigoBaras.getText().toString(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.has("status"))
                                    Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                else {
                                    try {
                                        etDescripcion.setText(response.getString("descripcion"));
                                        etMarca.setText(response.getString("marca"));
                                        etPrecioCompra.setText(String.valueOf(response.getInt("preciocompra")));
                                        etPrecioVenta.setText(String.valueOf(response.getInt("precioventa")));
                                        etExistencia.setText(String.valueOf(response.getInt("existencias")));
                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(peticion);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject producto = new JSONObject();
                try {
                    producto.put("codigobarras",etCodigoBaras.getText().toString());
                    producto.put("descripcion",etDescripcion.getText().toString());
                    producto.put("marca",etMarca.getText().toString());
                    producto.put("preciocompra",Float.parseFloat(etPrecioCompra.getText().toString()));
                    producto.put("precioventa",Float.parseFloat(etPrecioVenta.getText().toString()));
                    producto.put("existencias",Float.parseFloat(etExistencia.getText().toString()));
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url +"/insert",
                        producto,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("Prodcuto insertado"))
                                        Toast.makeText(MainActivity.this, "¨Producto insertado con EXITO!", Toast.LENGTH_SHORT).show();
                                }catch (JSONException e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(jsonObjectRequest);
            }
        });
    }
    protected void listarProductos(){

        jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0 ; i<response.length();i++){
                            try {
                                String codigobarras = response.getJSONObject(i).getString("codigobarras");
                                String descripcion = response.getJSONObject(i).getString("descripcion");
                                String marca = response.getJSONObject(i).getString("marca");
                                origenDatos.add(codigobarras+" -> "+descripcion+" -> "+marca);
                            } catch (JSONException e) {

                            }
                        }
                        adapter = new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, origenDatos);
                        lvProductos.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        colaPeticiones.add(jsonArrayRequest);
    }
}