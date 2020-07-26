package com.yamilruscalleda.revi;


import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class listaRecycler extends RecyclerView.Adapter<listaRecycler.ViewHolderGeneral> implements View.OnClickListener{

    private ArrayList<listaGeneral> listaGenerals;
    private View.OnClickListener listener;
    private Context context;


    public listaRecycler(ArrayList<listaGeneral> listaGenerals, Context context) {
        this.listaGenerals = listaGenerals;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderGeneral onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_general,null,false);
        view.setOnClickListener(this); //Metodo onClick
        return new ViewHolderGeneral(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolderGeneral holder, int position) {
        holder.Nombre.setText(listaGenerals.get(position).getNombre());
        holder.Direccion.setText(listaGenerals.get(position).getDireccion());
        holder.Distancia.setText(listaGenerals.get(position).getDistancia());
        holder.Valor.setText(listaGenerals.get(position).getValor());
        holder.Categoria.setText(listaGenerals.get(position).getCategoria());
        holder.Dslam.setText(listaGenerals.get(position).getDslam());
        holder.Cables.setText(listaGenerals.get(position).getCables());
        holder.id=listaGenerals.get(position).getIdArm();
        Glide.with(context).load(listaGenerals.get(position).getImgPerfil()).into(holder.idImagen);

    }

    @Override
    public int getItemCount() {
        if (listaGenerals != null)
            return listaGenerals.size();
        return 0;
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }
    }

    //Referenciando los objetos
    class ViewHolderGeneral extends RecyclerView.ViewHolder {

        TextView Nombre,Direccion,Distancia,Valor,Categoria,Dslam,Cables;
        ImageView idImagen;
        String id;

        ViewHolderGeneral(View itemView) {
            super(itemView);
            Nombre = itemView.findViewById(R.id.Nombre);
            Direccion = itemView.findViewById(R.id.Direccion);
            Distancia = itemView.findViewById(R.id.Distancia);
            Valor = itemView.findViewById(R.id.Valor);
            Categoria = itemView.findViewById(R.id.Categoria);
            Dslam = itemView.findViewById(R.id.Dslam);
            Cables = itemView.findViewById(R.id.Cables);
            idImagen = itemView.findViewById(R.id.idImagen);
        }
    }

}

