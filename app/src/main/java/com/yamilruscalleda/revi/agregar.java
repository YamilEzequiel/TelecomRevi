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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static com.android.volley.VolleyLog.TAG;


public class agregar extends Activity {

    TextView texto;
    EditText nombreTer, Distancia, Comentario, Direccion, Altura;
    Button consultarExistencia;
    String TECNICO, CENTRAL, DE, TIPO, TIPOINICIAL, CategoriaText, ValorText,StringImagen;
    StringRequest stringRequest;
    ImageView imagen;
    Spinner Categoria, Valor;
    LinearLayout Scroll;
    ProgressDialog progreso;
    int valores=0,imagenCarga=0;

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
        setContentView(R.layout.agregar);

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
        Scroll = findViewById(R.id.scroll);
        Direccion = findViewById(R.id.Direccion);

        //SPINER CATEGORIA
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
        //SPINER VALOR ZONA
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


        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        assert bundle != null;
        DE = bundle.getString("de");
        CENTRAL = bundle.getString("central");
        TECNICO = bundle.getString("tecnico");
        TIPO = bundle.getString("tipo");
        TIPOINICIAL = bundle.getString("tipoInicial");

        texto.setText("Estas buscando un Ter en " + TIPOINICIAL + " " + DE);

        //Damos el valor al boton en caso de ser 0 el terminal se registra en caso de ser 1 se busca el terminal Existente
        consultarExistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(nombreTer.getText().toString())) {
                    if (valores==0) {
                        registrandoTerminal();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert inputMethodManager != null;
                        inputMethodManager.hideSoftInputFromWindow(nombreTer.getWindowToken(), 0);
                        valores=1;
                    }else if (valores==1){
                        cargarWebService();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Ingrese un nombre de terminal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle editTextSource Click Handler
        Direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("AR").build();
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(autocompleteFilter).build(agregar.this); // notice CrateRide.this
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogOpciones();
            }
        });
    }

    private void registrandoTerminal() {
        String url = "http://149.56.192.248/~revi/registrar.php?";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equalsIgnoreCase("sinDatos")) {
                    Toast.makeText(getApplicationContext(), "Sin datos agregados", Toast.LENGTH_SHORT).show();
                    texto.setText("Estas agregando un Ter a " + TIPOINICIAL + " " + DE);
                    consultarExistencia.setText("Agregar");
                    Scroll.setVisibility(View.VISIBLE);
                } else if (response.trim().equalsIgnoreCase("Existe")) {
                    Toast.makeText(getApplicationContext(), "Este terminal ya esta cargado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(agregar.this, agregarResultado.class);
                    intent.putExtra("central", CENTRAL);
                    intent.putExtra("tipo", "Ter");
                    intent.putExtra("armario", DE);
                    intent.putExtra("nombreTer", nombreTer.getText().toString());
                    intent.putExtra("tipoInicial",TIPOINICIAL);
                    startActivity(intent);
                    Log.i("RESPUESTA: ", "" + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se ha podido conectar", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parametros = new HashMap<>();
                parametros.put("tipo", TIPO);
                parametros.put("central", CENTRAL);
                parametros.put("nombre", nombreTer.getText().toString());
                parametros.put("idArm", DE);

                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
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
                        startActivityForResult(Intent.createChooser(intent,"Seleccione"),COD_SELECCIONA);
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

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                Direccion.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),"x",Toast.LENGTH_SHORT).show();
            }
        }

        switch (requestCode) {
            case COD_SELECCIONA:
                if (data != null) {
                    Uri miPath = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), miPath);
                        imagen.setImageBitmap(bitmap);
                        imagenCarga = 1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bitmap = redimensionarImagen(bitmap);
                }
                break;
            case COD_FOTO:
                if (data != null){
                    bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                assert bitmap != null;
                bitmap = redimensionarImagen(bitmap);
                imagen.setImageBitmap(bitmap);
                imagenCarga = 1;
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


    private void cargarWebService() {

        progreso = new ProgressDialog(this);
        progreso.setMessage("Cargando Terminal");
        progreso.show();

        String url="http://149.56.192.248/~revi/cargar.php";

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

                //String imagen=convertirImgString(bitmap);

                if (imagenCarga==0) {
                    StringImagen = " ";
                }else{
                    StringImagen=convertirImgString(bitmap);
                }

                Map<String,String> parametros=new HashMap<>();
                parametros.put("tipo",TIPO);
                parametros.put("nombre",nombreTer.getText().toString());
                parametros.put("tecnico",TECNICO);
                parametros.put("direccion",Direccion.getText().toString());
                parametros.put("altura",Altura.getText().toString());
                parametros.put("img",StringImagen);
                parametros.put("comentario",Comentario.getText().toString());
                parametros.put("categoria",CategoriaText);
                parametros.put("idArm",DE);
                parametros.put("valor",ValorText);
                parametros.put("distancia",Distancia.getText().toString());
                parametros.put("central",CENTRAL);

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