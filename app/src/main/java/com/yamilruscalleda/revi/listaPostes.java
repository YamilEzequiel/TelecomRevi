package com.yamilruscalleda.revi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class listaPostes extends RecyclerView.Adapter<listaPostes.ViewHolder> implements View.OnClickListener{

    private ArrayList<listaGeneral> mListaGenerals;
    private View.OnClickListener listener;
    private Context mContext;


    public listaPostes(ArrayList<listaGeneral> listaGenerals, Context context) {
        mListaGenerals = listaGenerals;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_postes,null,false);
        view.setOnClickListener(this);

        return new listaPostes.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Nombre.setText(mListaGenerals.get(position).getNombre());
        holder.Ter.setText(mListaGenerals.get(position).getComentarioDos());
        holder.Direccion.setText(mListaGenerals.get(position).getDireccion());
        holder.Distancia.setText(mListaGenerals.get(position).getDistancia());
        holder.Valor.setText(mListaGenerals.get(position).getValor());
        holder.Categoria.setText(mListaGenerals.get(position).getCategoria());
        holder.Dslam.setText(mListaGenerals.get(position).getDslam());
        holder.Cables.setText(mListaGenerals.get(position).getCables());
        holder.id=mListaGenerals.get(position).getIdArm();
        Glide.with(mContext).load(mListaGenerals.get(position).getImgPerfil()).into(holder.idImagen);
    }

    @Override
    public int getItemCount() {
        return mListaGenerals.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Nombre,Direccion,Distancia,Valor,Categoria,Dslam,Cables,Ter;
        ImageView idImagen;
        String id;

        ViewHolder(View itemView) {
            super(itemView);
            Nombre = itemView.findViewById(R.id.Nombre);
            Direccion = itemView.findViewById(R.id.Direccion);
            Distancia = itemView.findViewById(R.id.Distancia);
            Valor = itemView.findViewById(R.id.Valor);
            Categoria = itemView.findViewById(R.id.Categoria);
            Dslam = itemView.findViewById(R.id.Dslam);
            Cables = itemView.findViewById(R.id.Cables);
            idImagen = itemView.findViewById(R.id.idImagen);
            Ter = itemView.findViewById(R.id.Ter);
        }
    }




}
