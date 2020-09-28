package com.example.volleycacherecyclerview;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class DetalleNoticia extends AppCompatActivity {

    TextView txttitulo, txtdetalle;
    ImageView imagenDetalle;
    String urlrelativa = "http://192.168.0.10/webservice/detalle_noticia.php?idnoticia=";
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_noticia);

        txttitulo = findViewById(R.id.titulo_detalle);
        txtdetalle = findViewById(R.id.detalle_noticia);
        imagenDetalle = findViewById(R.id.imagen_detalle);
        queue = Volley.newRequestQueue(this);
        //Poner boton atras en el action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Detalle Noticia");
        //obtener parámetro del intent
        Intent i = getIntent();
        String idnoticia = i.getStringExtra("idnoticia");
        Log.d("idnoticia", idnoticia);
        //pasar idnoticia a la url
        urlrelativa = urlrelativa+idnoticia;
        setDetalle();
    }
    public void setDetalle(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlrelativa, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray mJsonArray = response.getJSONArray("detalle_noticia");
                    for (int i = 0; i < mJsonArray.length(); i++) {
                        JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                        //obtener data
                        String titulo = mJsonObject.getString("titulo");
                        String imagen = mJsonObject.getString("imagen");
                        String detalle = mJsonObject.getString("detalle");
                        Log.d("titulo",titulo);
                        Log.d("imagen",imagen);
                        Log.d("detalle",detalle);
                        txttitulo.setText(titulo);
                        txtdetalle.setText(detalle);

                        Glide.with(getApplicationContext())
                                .load(imagen)
                                .centerCrop()
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(imagenDetalle);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetalleNoticia.this, "Ocurrio un error/Sin conexión: "+error.getMessage(), Toast.LENGTH_LONG).show();
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
                    //Actualizar request en 2 min. No borra el cache solo actualiza si vuelven abrir el app pero luego de 2min.
                    final long cacheHitButRefreshed = 2 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    /*
                    Tiempos:
                    final long cacheExpired = 24 * 60 * 60 * 1000; // En 24 horas el cache expira.
                    final long cacheExpired = 15 * 60 * 1000; // En 15 minutos el cache expira
                    final long cacheExpired = now * 60 * 1000; // En 1 minuto el cache expira
                    */
                    final long cacheExpired = 5 * 60 * 1000; // 5min
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

}