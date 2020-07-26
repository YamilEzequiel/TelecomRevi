package com.yamilruscalleda.revi.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.yamilruscalleda.revi.R;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


public class fragmentAgenda extends Fragment {

    Button ngnOne,ngntwo,mesa,gsm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);

        ngnOne = view.findViewById(R.id.ngnone);
        ngntwo = view.findViewById(R.id.ngntwo);
        mesa = view.findViewById(R.id.mesa);
        gsm = view.findViewById(R.id.gsm);

        ngnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ngntwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:08005556534")));
            }
        });

        mesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:08004440610")));
            }
        });

        gsm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:08004445414")));
            }
        });


        return view;
    }





    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
