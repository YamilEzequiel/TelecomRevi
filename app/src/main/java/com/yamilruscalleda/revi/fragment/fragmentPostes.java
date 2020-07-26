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
import com.yamilruscalleda.revi.agregarPoste;
import com.yamilruscalleda.revi.agregarResultadoPoste;
import com.yamilruscalleda.revi.listaGeneral;
import com.yamilruscalleda.revi.listaPostes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class fragmentPostes extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private OnFragmentInteractionListener mListener;
    ProgressDialog dialog;
    RecyclerView recyclerView;
    ArrayList<com.yamilruscalleda.revi.listaGeneral> listaGeneral;
    FloatingActionMenu menuFlotante;
    FloatingActionButton PEN,CUM,ADD,PAS;
    String TIPO = "Pendiente",url,CENTRAL = "ARJ2",CALLE,ALTURA;
    int urlInt = 0;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v =  inflater.inflate(R.layout.fragment_postes, container, false);

        listaGeneral=new ArrayList<>();

        menuFlotante=v.findViewById(R.id.menuFlotante);
        PEN=v.findViewById(R.id.clicPendientes);
        CUM=v.findViewById(R.id.clicCumplidos);
        ADD=v.findViewById(R.id.clicAgregar);
        PAS=v.findViewById(R.id.clicPasado);
        menuFlotante.setClosedOnTouchOutside(true);

        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);


        PEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaGeneral.clear();
                TIPO = "Pendiente";
                cargarWebService();
            }
        });

        PAS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaGeneral.clear();
                TIPO = "Pasado";
                cargarWebService();
            }
        });

        CUM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaGeneral.clear();
                TIPO = "Cumplido";
                cargarWebService();
            }
        });

        ADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), agregarPoste.class);
                //intent.putExtra("de",DE);
                startActivity(intent);
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

            Toast.makeText(getContext(),TIPO + " : " +json.length(),Toast.LENGTH_SHORT).show();

            for (int i=0;i<json.length();i++){
                listado=new listaGeneral();
                JSONObject jsonObject=null;
                jsonObject=json.getJSONObject(i);

                listado.setIdArm(jsonObject.optString("idPoste"));
                listado.setNombre(jsonObject.optString("Fecha"));
                listado.setDireccion(jsonObject.optString("Direccion" ) + " " + jsonObject.optString("Altura"));
                listado.setDistancia(jsonObject.optString("Central"));
                listado.setValor(jsonObject.optString("Zona"));
                listado.setCables(jsonObject.optString("Comentario"));
                listado.setCategoria(jsonObject.optString("Tipo"));
                listado.setDslam(jsonObject.optString("Tecnico"));
                listado.setImgPerfil(getString(R.string.URL) + jsonObject.optString("Foto"));
                listado.setImgPortada(jsonObject.optString("FotoDos"));
                listado.setComentarioDos(jsonObject.optString("Ter"));
                listaGeneral.add(listado);
            }
            dialog.hide();

            listaPostes adapter=new listaPostes(listaGeneral, getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);


            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog=new ProgressDialog(getContext());
                    dialog.setMessage("Cargando");
                    dialog.show();


                    Intent intent = new Intent(getContext(), agregarResultadoPoste.class);
                    // Toast.makeText(getContext(),listaGeneral.get(recyclerView.getChildAdapterPosition(v)).getIdArm(),Toast.LENGTH_SHORT).show();
                    intent.putExtra("IDPOSTE",listaGeneral.get(recyclerView.getChildAdapterPosition(v)).getIdArm());
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