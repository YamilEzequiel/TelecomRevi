package com.yamilruscalleda.revi.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

public class fragmentPlantel extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private OnFragmentInteractionListener mListener;
    ProgressDialog dialog;
    RecyclerView recyclerView;
    ArrayList<com.yamilruscalleda.revi.listaGeneral> listaGeneral;
    FloatingActionMenu menuFlotante;
    FloatingActionButton ARM,RIG;
    String TIPO = "Arm",url,CENTRAL = "ARJ2",CALLE,ALTURA;
    int urlInt = 0;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v =  inflater.inflate(R.layout.fragment_plantel, container, false);

        listaGeneral=new ArrayList<>();

        menuFlotante=v.findViewById(R.id.menuFlotante);
        ARM=v.findViewById(R.id.clicArmarios);
        RIG=v.findViewById(R.id.clicRigidos);
        menuFlotante.setClosedOnTouchOutside(true);

        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);


        ARM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaGeneral.clear();
                TIPO = "Arm";
                cargarWebService();
            }
        });

        RIG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaGeneral.clear();
                TIPO = "Rig";
                cargarWebService();
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

        String url="http://149.56.192.248/~revi/consulta.php?tipo=" + TIPO + "&central=" + CENTRAL;
        JsonObjectRequest jsonObjectResquest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectResquest);
    }

    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

}