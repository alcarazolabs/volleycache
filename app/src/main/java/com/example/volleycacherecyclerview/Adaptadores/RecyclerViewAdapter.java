package com.example.volleycacherecyclerview.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.volleycacherecyclerview.DetalleNoticia;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.volleycacherecyclerview.Entidades.Noticia;
import com.example.volleycacherecyclerview.R;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    LayoutInflater inflater;
    ArrayList<Noticia> model;

    public RecyclerViewAdapter(Context context, ArrayList<Noticia> model) {
        this.inflater = LayoutInflater.from(context);
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = inflater.inflate(R.layout.item_noticia, parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String titulo = model.get(position).getTitulo();
        String fecha = model.get(position).getFecha();
        String imagen = model.get(position).getImagen();

        holder.txttitulo.setText(titulo);
        holder.txtfecha.setText(fecha);

        Glide.with(holder.itemView.getContext())
                .load(imagen)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imagen);


        /*
        * Mostrar imágen usando GlideApp (Se habilito conforme al modulo creado MyGlideModule (build->build module app) )
        RequestOptions myOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .skipMemoryCache(true)
                .apply(RequestOptions.circleCropTransform())
                ;

        GlideApp.with(holder.itemView.getContext()).
                load(imagen)
                //.apply(RequestOptions.circleCropTransform())
                .apply(myOptions)
                .into(holder.imagen);
    */
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView txttitulo, txtfecha;
        ImageView imagen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(view.getContext(), DetalleNoticia.class);
                    in.putExtra("idnoticia", model.get(getAdapterPosition()).getIdnoticia());
                    //abrir activity
                    view.getContext().startActivity(in);
                }
            });
            txttitulo = itemView.findViewById(R.id.txttitulo);
            txtfecha = itemView.findViewById(R.id.txtfecha);
            imagen = itemView.findViewById(R.id.imagen_noticia);
        }

    }
}
