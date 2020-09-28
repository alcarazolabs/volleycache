package com.example.volleycacherecyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.volleycacherecyclerview.Adaptadores.RecyclerViewAdapter;
import com.example.volleycacherecyclerview.Entidades.Noticia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter adapterRecyclerView;
    ArrayList<Noticia> listnoticias;
    //10.0.2.2
    final String url = "http://192.168.0.10/webservice/noticias.php";
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        listnoticias = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        setRecyclerView();
        setLista();

    }

    public void setLista(){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray mJsonArray = response.getJSONArray("noticias");
                    //limpiar lista
                    listnoticias.clear();
                    for(int i=0;i<mJsonArray.length();i++){
                        JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                        String idnoticia = mJsonObject.getString("idnoticia");
                        String titulo = mJsonObject.getString("titulo");
                        String imagen = mJsonObject.getString("imagen");
                        String fecha = mJsonObject.getString("fecha");
                        //Crear objeto
                        Noticia n = new Noticia();
                        n.setIdnoticia(idnoticia);
                        n.setTitulo(titulo);
                        n.setFecha(fecha);
                        n.setImagen(imagen);
                        //Agregar objeto a la lista
                        listnoticias.add(n);
                        adapterRecyclerView.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Ocurrio un error/Sin conexión: "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    long now = System.currentTimeMillis();

                    final long cacheHitButRefreshed = 1 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    /*
                    Tiempos:
                    final long cacheExpired = 24 * 60 * 60 * 1000; // En 24 horas el cache expira.
                    final long cacheExpired = 15 * 60 * 1000; // En 15 minutos el cache expira
                    final long cacheExpired = now * 60 * 1000; // En 1 minuto el cache expira
                    */

                    final long cacheExpired = 4 * 60 * 1000; // 5min
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString), cacheEntry);
                    //Para StringRequest: return Response.success(new String(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }

        };

        queue.add(request);
    }

    public void setRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterRecyclerView = new RecyclerViewAdapter(this, listnoticias);
        recyclerView.setAdapter(adapterRecyclerView);
    }
}