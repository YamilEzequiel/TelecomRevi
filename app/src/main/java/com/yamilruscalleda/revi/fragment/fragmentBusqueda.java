package com.yamilruscalleda.revi.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.yamilruscalleda.revi.R;
import com.yamilruscalleda.revi.listaJava;

import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.android.volley.VolleyLog.TAG;


public class fragmentBusqueda extends Fragment implements View.OnClickListener{

    Button Buscar,Arm,Calle;
    LinearLayout ArmRig,Calles;
    EditText Direccion,Altura,numeroArmRig,numeroTer;
    int buscar=1;
    String armorig;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_busqueda, container, false);

        Buscar = view.findViewById(R.id.Buscar);
        Arm = view.findViewById(R.id.Arm);
        Calle = view.findViewById(R.id.Calle);
        ArmRig = view.findViewById(R.id.ArmRig);
        Calles = view.findViewById(R.id.Calles);
        Direccion = view.findViewById(R.id.laCalle);
        Altura = view.findViewById(R.id.laAltura);
        numeroArmRig = view.findViewById(R.id.numeroArmRig);
        numeroTer = view.findViewById(R.id.numeroTer);

        Arm.setOnClickListener(this);
        Calle.setOnClickListener(this);
        Buscar.setOnClickListener(this);
        Buscar.setEnabled(false);

        // Handle editTextSource Click Handler
        Direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("AR").build();
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(autocompleteFilter).build(Objects.requireNonNull(getActivity())); // notice CrateRide.this
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        return view;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //implement the onClick method here
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.Arm:
                ArmRig.setVisibility(View.VISIBLE);
                Calles.setVisibility(View.INVISIBLE);
                buscar=3;
                Buscar.setEnabled(true);
                armorig = "Arm";
                break;
            case R.id.Calle:
                Calles.setVisibility(View.VISIBLE);
                ArmRig.setVisibility(View.INVISIBLE);
                Buscar.setEnabled(true);
                buscar=1;
                break;
            case R.id.Buscar:
                Intent intent=new Intent(getContext(), listaJava.class);
                intent.putExtra("urlInt",buscar);
                intent.putExtra("calle",Direccion.getText().toString());
                intent.putExtra("altura",Altura.getText().toString());
                intent.putExtra("central","ARJ2");
                intent.putExtra("ter",numeroTer.getText().toString());
                intent.putExtra("nombre",numeroArmRig.getText().toString());
                intent.putExtra("armorig",armorig);
                startActivity(intent);

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Activity result de google place
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(Objects.requireNonNull(getContext()), data);
                Log.i(TAG, "Place: " + place.getName());
                Direccion.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(Objects.requireNonNull(getContext()), data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }



}
