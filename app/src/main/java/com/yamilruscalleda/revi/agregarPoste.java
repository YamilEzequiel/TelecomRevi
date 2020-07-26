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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static com.android.volley.VolleyLog.TAG;


public class agregarPoste extends Activity {

    TextView texto;
    EditText nombreTer, alturaPoste, terPoste, zonaPoste, Distancia, Comentario, Direccion, Altura,Zona,Ter;
    Button consultarExistencia;
    String TECNICO, CENTRAL, DE, TIPO, TIPOINICIAL, CategoriaText, ValorText,StringImagen;
    StringRequest stringRequest;
    ImageView imagen;
    Spinner SpinnerTipo;
    LinearLayout Scroll;
    ProgressDialog progreso;
    int valores=0,imagenCarga=0;
    String currentPhotoPath;
    File photoFile = null;
    Bitmap imageBitmap;

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
        setContentView(R.layout.agregar_poste);

        //Tomando los valores del usuario
        SharedPreferences preferencessa = Objects.requireNonNull(this).getSharedPreferences("datosUser", MODE_PRIVATE);
        TECNICO = preferencessa.getString("name","Sn");


        texto = findViewById(R.id.texto);
        Altura = findViewById(R.id.Altura);
        nombreTer = findViewById(R.id.nombreTer);
        alturaPoste = findViewById(R.id.alturaPoste);
        zonaPoste = findViewById(R.id.zonaPoste);
        terPoste = findViewById(R.id.terPoste);
        consultarExistencia = findViewById(R.id.consultar);
        SpinnerTipo = findViewById(R.id.SpinnerTipo);
        Distancia = findViewById(R.id.Distancia);
        Comentario = findViewById(R.id.Comentario);
        imagen = findViewById(R.id.imagen);
        Scroll = findViewById(R.id.scroll);
        Direccion = findViewById(R.id.Direccion);
        Zona = findViewById(R.id.Zona);
        Ter = findViewById(R.id.Ter);


        //SPINER VALOR ZONA


        ArrayAdapter<CharSequence> adapters = ArrayAdapter.createFromResource(Objects.requireNonNull(getApplicationContext()), R.array.TipoPoste, android.R.layout.simple_spinner_item);
        adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerTipo.setAdapter(adapters);
        SpinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ValorText = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ValorText = SpinnerTipo.getSelectedItem().toString();





        texto.setText("Busqueda de Poste");

        //Damos el valor al boton en caso de ser 0 el terminal se registra en caso de ser 1 se busca el terminal Existente
        consultarExistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (valores==0) {
                                registrandoTerminal();
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                assert inputMethodManager != null;
                                inputMethodManager.hideSoftInputFromWindow(nombreTer.getWindowToken(), 0);
                                valores = 1;
                    }else if (valores==1){
                        cargarWebService();
                    }

            }
        });

        // Handle editTextSource Click Handler
        Direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("AR").build();
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(autocompleteFilter).build(agregarPoste.this); // notice CrateRide.this
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
                    texto.setText("Agregando Poste");
                    consultarExistencia.setText("Agregar");
                    Direccion.setText(nombreTer.getText().toString());
                    Altura.setText(alturaPoste.getText().toString());
                    Scroll.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Poste ya cargado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(agregarPoste.this, agregarResultadoPoste.class);
                    intent.putExtra("IDPOSTE", response);
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
                parametros.put("tipo", "Poste");
                parametros.put("direccion", nombreTer.getText().toString());
                parametros.put("altura", alturaPoste.getText().toString());

                String Poste = "1",ZonaVolley = "1";

                if (!TextUtils.isEmpty(terPoste.getText().toString())){
                    Poste = terPoste.getText().toString();
                }

                if (!TextUtils.isEmpty(zonaPoste.getText().toString())){
                    ZonaVolley = zonaPoste.getText().toString();
                }

                parametros.put("terminal",Poste);
                parametros.put("zona",ZonaVolley);

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

                    dispatchTakePictureIntent();
                    imagenCarga = 1;

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
            if (requestCode == 1 && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imagen.setImageBitmap(imageBitmap);
            }
        }

        switch (requestCode) {
            case COD_SELECCIONA:
                if (data != null) {
                    Uri miPath = data.getData();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), miPath);
                        imagen.setImageBitmap(imageBitmap);
                        imagenCarga = 1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bitmap = redimensionarImagen(imageBitmap);
                }
                break;
            case COD_FOTO:
                if (data != null){
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    imagen.setImageBitmap(imageBitmap);
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
        progreso.setMessage("Cargando Poste");
        progreso.show();

        String url="http://149.56.192.248/~revi/cargarPoste.php";

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
                    StringImagen=convertirImgString(imageBitmap);
                }

                Map<String,String> parametros=new HashMap<>();
                parametros.put("tipo",SpinnerTipo.getSelectedItem().toString());
                parametros.put("tecnico",TECNICO);
                parametros.put("direccion",Direccion.getText().toString());
                parametros.put("altura",Altura.getText().toString());
                parametros.put("img",StringImagen);
                parametros.put("comentario",Comentario.getText().toString());
                parametros.put("central",Distancia.getText().toString());
                parametros.put("zona",Zona.getText().toString());
                parametros.put("ter",Ter.getText().toString());

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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //Uri photoURI = FileProvider.getUriForFile(this, "com.yamilruscalleda.revi.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, "Vacio");
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }


}