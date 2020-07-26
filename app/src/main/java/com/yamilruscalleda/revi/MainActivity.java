package com.yamilruscalleda.revi;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.fragment.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.view.View;
import com.google.android.material.navigation.NavigationView;
import com.yamilruscalleda.revi.fragment.fragmentAgenda;
import com.yamilruscalleda.revi.fragment.fragmentBusqueda;
import com.yamilruscalleda.revi.fragment.fragmentColores;
import com.yamilruscalleda.revi.fragment.fragmentMiLista;
import com.yamilruscalleda.revi.fragment.fragmentPlantel;
import com.yamilruscalleda.revi.fragment.fragmentPostes;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    TextView legajo,nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeaderView =  navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        nombre = navHeaderView.findViewById(R.id.nombre);
        legajo = navHeaderView.findViewById(R.id.legajo);


        //Tomando los valores del usuario
        SharedPreferences preferencessa = Objects.requireNonNull(this).getSharedPreferences("datosUser", MODE_PRIVATE);
        legajo.setText(preferencessa.getString("name","Sn"));
        nombre.setText(" ");

        //Fragment por defecto
        Fragment fragment = new fragmentPostes();
        getSupportFragmentManager().beginTransaction().add(R.id.content_main,fragment).commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Fragment miFragment = null;
    boolean fragmentSeleccionado = false;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.plantel) {
            miFragment = new fragmentPlantel();
            fragmentSeleccionado = true;
        } else if (id == R.id.postes) {
            miFragment = new fragmentPostes();
            fragmentSeleccionado = true;
        } else if (id == R.id.busqueda) {
            miFragment = new fragmentBusqueda();
            fragmentSeleccionado = true;
        } else if (id == R.id.agenda) {
            miFragment = new fragmentAgenda();
            fragmentSeleccionado = true;
        }else if (id == R.id.miLista){
            miFragment = new fragmentMiLista();
            fragmentSeleccionado = true;
        }else if (id == R.id.speed){
            startActivity(new Intent(MainActivity.this, SpeedTest.class));
        }else if (id == R.id.wire){
            miFragment = new fragmentColores();
            fragmentSeleccionado = true;
        }

        if (fragmentSeleccionado){
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,miFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
