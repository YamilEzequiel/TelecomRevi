package com.yamilruscalleda.revi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static com.android.volley.VolleyLog.TAG;

public class agregarResultado extends Activity implements Response.ErrorListener, Response.Listener<JSONObject>{

    TextView texto,Tecnico;
    EditText nombreTer,Distancia,Comentario,Direccion,Altura;
    Button consultarExistencia;
    String TECNICO,CENTRAL,DE,TIPO,TIPOINICIAL,CategoriaText,ValorText,NOMBRETER,imagens,imagens2;
    StringRequest stringRequest;
    ImageView imagen;
    ProgressDialog dialog;
    RecyclerView recyclerView;
    ProgressDialog progreso;
    ArrayList<listaGeneral> listaGeneral;
    Spinner Categoria,Valor;
    private RequestQueue request;
    int imgCarga = 0;

    private static final String CARPETA_PRINCIPAL = "misImagenesApp/";//directorio principal
    private static final String CARPETA_IMAGEN = "imagenes";//carpeta donde se guardan las fotos
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios
    private String path;//almacena la ruta de la imagen
    File fileImagen;
    Bitmap bitmap;

    private final int MIS_PERMISOS = 100;
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.agregar_resultado);

        //Tomando los valores del usuario
        SharedPreferences preferencessa = Objects.requireNonNull(this).getSharedPreferences("datosUser", MODE_PRIVATE);
        TECNICO = preferencessa.getString("name","Sn");

        texto = findViewById(R.id.texto);
        Altura = findViewById(R.id.Altura);
        nombreTer = findViewById(R.id.nombreTer);
        consultarExistencia = findViewById(R.id.consultar);
        Categoria = findViewById(R.id.Categoria);
        Valor = findViewById(R.id.Valor);
        Distancia = findViewById(R.id.Distancia);
        Comentario = findViewById(R.id.Comentario);
        imagen = findViewById(R.id.imagen);
        Direccion = findViewById(R.id.Direccion);
        Tecnico = findViewById(R.id.tecnico);

        listaGeneral=new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        assert bundle != null;
        DE = bundle.getString("armario");
        CENTRAL = bundle.getString("central");
        TIPO = bundle.getString("tipo");
        TIPOINICIAL = bundle.getString("tipoInicial");
        NOMBRETER = bundle.getString("nombreTer");

        texto.setText("Datos de " + TIPO + " " + NOMBRETER + " " + TIPOINICIAL + " " + DE);

        //Quitamos el teclado inicial
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(Direccion.getWindowToken(), 0);

        //Actualizando los datos del terminal
        consultarExistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recargandoTerminal();
            }
        });

        // Handle editTextSource Click Handler
        Direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("AR").build();
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(autocompleteFilter).build(agregarResultado.this); // notice CrateRide.this
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });


        //Spinner Categoria
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getApplicationContext()), R.array.categoria, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Categoria.setAdapter(adapter);
        Categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                CategoriaText = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        CategoriaText = Categoria.getSelectedItem().toString();
        //Spiner Valor Zona
        ArrayAdapter<CharSequence> adapters = ArrayAdapter.createFromResource(Objects.requireNonNull(getApplicationContext()), R.array.valorZona, android.R.layout.simple_spinner_item);
        adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Valor.setAdapter(adapters);
        Valor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ValorText = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ValorText = Valor.getSelectedItem().toString();

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogOpciones();
            }
        });


        cargarWebService();

    }

    private void cargarWebService() {
        dialog=new ProgressDialog(this);
        dialog.setMessage("Consultando Terminal");
        dialog.show();

        String url="http://149.56.192.248/~revi/consulta.php?TerTer="+TIPO+"&central="+CENTRAL+"&nombre="+NOMBRETER+"&idArm="+DE;
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

                listado=new listaGeneral();
                JSONObject jsonObject=null;
                jsonObject=json.getJSONObject(0);

                Tecnico.setText("Ultima carga " + jsonObject.optString("tecnico") + " " + jsonObject.optString("fecha"));
                Direccion.setText(jsonObject.optString("direccion"));
                Altura.setText(jsonObject.optString("altura"));
                Comentario.setText(jsonObject.optString("comentario"));
                Distancia.setText(jsonObject.optString("distancia"));

                int valor = 0;
                String valors =  jsonObject.optString("valor");
                if (valors.equals("640k")){valor=0;};
                if (valors.equals("1M")){valor=1;};
                if (valors.equals("3M")){valor=2;};
                if (valors.equals("6M")){valor=3;};
                if (valors.equals("10M")){valor=4;};
                if (valors.equals("15M")){valor=5;};
                if (valors.equals("20M")){valor=6;};
                if (valors.equals("VDSL")){valor=7;};
                if (valors.equals("NGN")){valor=8;};
                Valor.setSelection(valor);

                int valor2 = 0;
                String valors2 =  jsonObject.optString("categoria");
                if (valors2.equals("Poste")){valor2=0;};
                if (valors2.equals("Poste B")){valor2=1;};
                if (valors2.equals("Azotea")){valor2=2;};
                if (valors2.equals("Fachada")){valor2=3;};
                if (valors2.equals("Camara")){valor2=4;};
                if (valors2.equals("interno")){valor2=5;};
                Categoria.setSelection(valor2);

                imagens2 = jsonObject.optString("img");
                Glide.with(getApplicationContext()).load(jsonObject.optString("img")).into(imagen);

            dialog.hide();

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


    //Comienzo de carga de imagen
    private void mostrarDialogOpciones() {
        final CharSequence[] opciones={"Tomar Foto","Elegir de Galeria","Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elige una Opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,COD_FOTO);
                }else{
                    if (opciones[i].equals("Elegir de Galeria")){
                        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Activity result de google place
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                Direccion.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        //Activity result de camara
        switch (requestCode) {
            case COD_SELECCIONA:
                if (data != null) {
                    Uri miPath = data.getData();
                    imgCarga = 1;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), miPath);
                        imagen.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bitmap = redimensionarImagen(bitmap);
                }
                break;
            case COD_FOTO:
                if (data != null) {
                    imgCarga = 1;
                    bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    assert bitmap != null;
                    bitmap = redimensionarImagen(bitmap);
                    imagen.setImageBitmap(bitmap);
                }
                break;
        }
    }

    private Bitmap redimensionarImagen(Bitmap bitmap) {

        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        if (ancho > (float) 600 || alto > (float) 800) {
            float escalaAncho = (float) 600 / ancho;
            float escalaAlto = (float) 800 / alto;

            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho, escalaAlto);

            return Bitmap.createBitmap(bitmap, 0, 0, ancho, alto, matrix, false);

        } else {
            return bitmap;
        }

    }


    private void recargandoTerminal() {

        progreso = new ProgressDialog(this);
        progreso.setMessage("Cargando Datos");
        progreso.show();

        String url="http://149.56.192.248/~revi/recargar.php";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progreso.hide();

                if (response.trim().equalsIgnoreCase("registra")){
                    Toast.makeText(getApplicationContext(),"Se ha registrado con exito",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"No se ha registrado ",Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ",""+response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                progreso.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (imgCarga==0) {
                    imagens = imagens2;
                }else{
                    imagens = convertirImgString(bitmap);
                }

                Map<String,String> parametros=new HashMap<>();

                parametros.put("tipo",TIPO);
                parametros.put("nombre",NOMBRETER);
                parametros.put("tecnico",TECNICO);
                parametros.put("direccion",Direccion.getText().toString());
                parametros.put("altura",Altura.getText().toString());
                parametros.put("img",imagens);
                parametros.put("comentario",Comentario.getText().toString());
                parametros.put("categoria",CategoriaText);
                parametros.put("idArm",DE);
                parametros.put("valor",ValorText);
                parametros.put("distancia",Distancia.getText().toString());
                parametros.put("central",CENTRAL);
                parametros.put("imgCarga", String.valueOf(imgCarga));

                return parametros;
            }
        };
        //request.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(this).addToRequestQueue(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap) {
        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte=array.toByteArray();
        String imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);
        return imagenString;
    }

}
