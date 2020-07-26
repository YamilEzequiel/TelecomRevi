package com.yamilruscalleda.revi.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.yamilruscalleda.revi.R;
import com.yamilruscalleda.revi.VolleySingleton;
import com.yamilruscalleda.revi.listaGeneral;
import com.yamilruscalleda.revi.listaJava;
import com.yamilruscalleda.revi.listaRecycler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class fragmentMiLista extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private fragmentBusqueda.OnFragmentInteractionListener mListener;
    ProgressDialog dialog;
    Dialog dialogComentar;
    RecyclerView recyclerView;
    ArrayList<com.yamilruscalleda.revi.listaGeneral> listaGeneral;
    FloatingActionMenu menuFlotante;
    FloatingActionButton ARM;
    String TIPO = "Arm",url,CENTRAL = "ARJ2",TECNICO;
    EditText numeroArm;
    Button agregarArm;
    int urlInt = 0;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v =  inflater.inflate(R.layout.fragment_mi_lista, container, false);

        //Tomando los valores del usuario
        SharedPreferences preferencessa = Objects.requireNonNull(getActivity()).getSharedPreferences("datosUser", MODE_PRIVATE);
        TECNICO = preferencessa.getString("name","Sn");

        listaGeneral=new ArrayList<>();

        menuFlotante=v.findViewById(R.id.menuFlotante);
        ARM=v.findViewById(R.id.clicArmarios);
        menuFlotante.setClosedOnTouchOutside(true);

        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);


        ARM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogComentar = new Dialog(Objects.requireNonNull(getContext()));
                dialogComentar.setContentView(R.layout.dialog_fav);
                numeroArm = dialogComentar.findViewById(R.id.numero);
                agregarArm = dialogComentar.findViewById(R.id.agregar);

                dialogComentar.show();

                agregarArm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(numeroArm.getText().toString())) {
                                agregar();
                        }else{
                            Toast.makeText(getContext(),"Ingrese un armario",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        cargarWebService();

        return v;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private void cargarWebService() {
        dialog=new ProgressDialog(getContext());
        dialog.setMessage("Consultando lista");
        dialog.show();

        String url="http://149.56.192.248/~revi/favoritos.php?tecnico="+ TECNICO +"&tipo=idArm";
        JsonObjectRequest jsonObjectResquest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectResquest);
    }

    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
        Log.i("ERROR",error.toString());

    }

    public void onResponse(JSONObject response) {
        listaGeneral listado=null;

        JSONArray json=response.optJSONArray("favoritos");

        try {

            for (int i=0;i<json.length();i++){
                listado=new listaGeneral();
                JSONObject jsonObject=null;
                jsonObject=json.getJSONObject(i);

                listado.setIdArm(jsonObject.optString("idArm"));
                listado.setNombre(jsonObject.optString("nombre"));
                listado.setDireccion(jsonObject.optString("direccion"));
                listado.setDistancia(jsonObject.optString("distancia"));
                listado.setValor(jsonObject.optString("valor"));
                listado.setCables(jsonObject.optString("cables"));
                listado.setCategoria(jsonObject.optString("cat"));
                listado.setDslam(jsonObject.optString("dslam"));
                listado.setImgPerfil(jsonObject.optString("img"));
                listado.setImgPortada(jsonObject.optString("imgDos"));
                listaGeneral.add(listado);
            }
            dialog.hide();

            listaRecycler adapter=new listaRecycler(listaGeneral, getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);


            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog=new ProgressDialog(getContext());
                    dialog.setMessage("Cargando");
                    dialog.show();


                    Intent intent = new Intent(getContext(), listaJava.class);
                    // Toast.makeText(getContext(),listaGeneral.get(recyclerView.getChildAdapterPosition(v)).getIdArm(),Toast.LENGTH_SHORT).show();
                    intent.putExtra("de",listaGeneral.get(recyclerView.getChildAdapterPosition(v)).getNombre());
                    intent.putExtra("central",CENTRAL);
                    intent.putExtra("tipo","Ter");
                    intent.putExtra("tipoInicial",TIPO);
                    intent.putExtra("urlInt",4);
                    startActivity(intent);

                    dialog.hide();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


    //Metodo de Comentar
    private void agregar(){
        String url="http://149.56.192.248/~revi/favoritos.php?";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.trim().equalsIgnoreCase("registra")) {
                    Toast.makeText(getContext(),"Agregado",Toast.LENGTH_SHORT).show();
                    dialogComentar.cancel();
                } else {
                    Toast.makeText(getContext(), "No se ha registrado ", Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ", "" + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No se ha podido conectar", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> parametros=new HashMap<>();
                parametros.put("agregar",TECNICO);
                parametros.put("idarm",numeroArm.getText().toString());
                parametros.put("idTer","0");
                parametros.put("tipo","idArm");
                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }




    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

}