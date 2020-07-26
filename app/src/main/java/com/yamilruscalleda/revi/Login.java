package com.yamilruscalleda.revi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login extends Activity {

    EditText Nombre,Pass;
    Button Login;
    String loginS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);

        Nombre = findViewById(R.id.name);
        Pass = findViewById(R.id.pass);
        Login = findViewById(R.id.ingresar);


        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getApplicationContext()).getSharedPreferences("datosUser", getApplicationContext().MODE_PRIVATE);
        Nombre.setText(sharedPreferences.getString("name",""));
        Pass.setText(sharedPreferences.getString("pass",""));
        loginS = sharedPreferences.getString("login","false");

        if(loginS.equals("true")){
            LoginUsuario();
        }


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(Nombre.getText().toString())){
                    if (!TextUtils.isEmpty(Pass.getText().toString())) {
                        LoginUsuario();
                    }else{
                        Toast.makeText(Login.this,"Ingrese su contraseña",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Login.this,"Ingrese su usuario",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void LoginUsuario(){
        String url="http://149.56.192.248/~revi/login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response.trim().equalsIgnoreCase("loginOk")) {

                    ProgressDialog dialog = new ProgressDialog(Login.this);
                    dialog.setMessage("Cargando datos");
                    dialog.show();

                    SharedPreferences sharedPreferences = getSharedPreferences("datosUser", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("name",Nombre.getText().toString());
                    editor.putString("pass",Pass.getText().toString());
                    editor.putString("login","true");
                    editor.apply();

                    Intent ok = new Intent(Login.this, MainActivity.class);
                    startActivity(ok);

                    dialog.hide();

                } else if (response.trim().equalsIgnoreCase("userError")) {
                    Toast.makeText(getApplicationContext(), "Usuario invalido", Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ", "" + response);
                } else if (response.trim().equalsIgnoreCase("passError")) {
                    Toast.makeText(getApplicationContext(), "Contraseña invalida", Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ", "" + response);
                } else {
                    Toast.makeText(getApplicationContext(), "Error al iniciar!", Toast.LENGTH_SHORT).show();
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
            protected Map<String, String> getParams() {
                String nombre = Nombre.getText().toString();
                String contrasena = Pass.getText().toString();

                Map<String, String> parametros = new HashMap<>();
                parametros.put("login", "login");
                parametros.put("usuario", nombre);
                parametros.put("pass", contrasena);

                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
    }


}
