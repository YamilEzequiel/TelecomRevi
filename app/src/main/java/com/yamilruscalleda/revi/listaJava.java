package com.yamilruscalleda.revi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;

public class listaJava extends Activity implements Response.ErrorListener,Response.Listener<JSONObject> {

    ProgressDialog dialog;
    RecyclerView recyclerView;
    ArrayList<listaGeneral> listaGeneral;
    String TIPO = "Ter";
    String TIPOINICIAL = "";
    String CENTRAL = "ARJ2";
    String DE = " ";
    String TECNICO = "";
    FloatingActionButton Agregar;
    String CALLE,ALTURA,url,NOMBRE,ARMORIG,TERr;
    int urlInt = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.lista_java);


        //Tomando los valores del usuario
        SharedPreferences preferencessa = Objects.requireNonNull(this).getSharedPreferences("datosUser", MODE_PRIVATE);
        TECNICO = preferencessa.getString("name","Sn");

        listaGeneral=new ArrayList<>();

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        Agregar = findViewById(R.id.clicAgregar);


        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        assert bundle != null;
        DE = bundle.getString("de");
        CENTRAL = bundle.getString("central");
        TIPOINICIAL = bundle.getString("tipoInicial");
        urlInt=bundle.getInt("urlInt");
        CALLE=bundle.getString("calle");
        ALTURA=bundle.getString("altura");
        NOMBRE=bundle.getString("nombre");
        TIPO=bundle.getString("tipo");
        TERr=bundle.getString("ter");


        if (urlInt==0){ url = "http://149.56.192.248/~revi/consulta.php?tipo=" + TIPO + "&central=" + CENTRAL; }
        if (urlInt==1){ url = "http://149.56.192.248/~revi/consulta.php?central="+CENTRAL+"&calle="+CALLE+"&altura="+ALTURA; Agregar.setEnabled(false);}
        if (urlInt==3){ url = "http://149.56.192.248/~revi/consulta.php?central="+CENTRAL+"&consultarArm=Ter&nombre="+TERr+"&terminal="+NOMBRE; Agregar.setEnabled(false);}
        if (urlInt==4){ url = "http://149.56.192.248/~revi/consulta.php?tipo="+ TIPO +"&central="+ CENTRAL + "&de=" + DE;}

        Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),agregar.class);
                intent.putExtra("de",DE);
                intent.putExtra("central",CENTRAL);
                intent.putExtra("tecnico",TECNICO);
                intent.putExtra("tipo",TIPO);
                intent.putExtra("tipoInicial",TIPOINICIAL);
                startActivity(intent);
            }
        });

        cargarWebService();
    }

    private void cargarWebService() {
        dialog=new ProgressDialog(this);
        dialog.setMessage("Consultando lista");
        dialog.show();

       // String url="http://149.56.192.248/~revi/consulta.php?tipo="+ TIPO +"&central="+ CENTRAL + "&de=" + DE;
        JsonObjectRequest jsonObjectResquest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectResquest);
    }

    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
        Log.i("ERROR",error.toString());

    }

    public void onResponse(JSONObject response) {
        listaGeneral listado=null;

        JSONArray json=response.optJSONArray("plantel");

        try {

            for (int i=0;i<json.length();i++){
                listado=new listaGeneral();
                JSONObject jsonObject=null;
                jsonObject=json.getJSONObject(i);

                listado.setIdArm(jsonObject.optString("idArm"));
                listado.setNombre(jsonObject.optString("nombre"));
                listado.setDireccion(jsonObject.optString("direccion") + " " + jsonObject.optString("altura"));
                listado.setDistancia(jsonObject.optString("distancia"));
                listado.setValor(jsonObject.optString("valor"));
                listado.setCables(jsonObject.optString("cables"));
                listado.setCategoria(jsonObject.optString("idArm"));
                listado.setDslam(jsonObject.optString("categoria"));
                listado.setImgPerfil(jsonObject.optString("img"));
                listado.setImgPortada(jsonObject.optString("imgDos"));
                listaGeneral.add(listado);
            }
            dialog.hide();

            listaRecycler adapter=new listaRecycler(listaGeneral, getApplicationContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(adapter);


            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog=new ProgressDialog(listaJava.this);
                    dialog.setMessage("Cargando");
                    dialog.show();


                    Intent intent = new Intent(listaJava.this,agregarResultado.class);
                    intent.putExtra("armario",listaGeneral.get(recyclerView.getChildAdapterPosition(v)).getIdArm());
                    intent.putExtra("central",CENTRAL);
                    intent.putExtra("tipo","Ter");
                    intent.putExtra("tipoIncial",TIPOINICIAL);
                    intent.putExtra("nombreTer",listaGeneral.get(recyclerView.getChildAdapterPosition(v)).getNombre());
                    startActivity(intent);


                    dialog.hide();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

}
