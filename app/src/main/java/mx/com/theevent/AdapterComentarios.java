package mx.com.theevent;

/**
 * Created by Gatsu on 12/2/2016.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class AdapterComentarios extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] nombres;
    private final String[] dias;
    private final String[] mensajes;
    private String[] imagenesString;
    JSONObject res;
    int flag;
    ArrayList<Bitmap> imagenes;

    static class ViewHolder {
        public RelativeLayout comentarioLayout;
        public TextView namePost;
        public TextView nombreTxt;
        public TextView mensajeTxt;
        public TextView diaTxt;
        public TextView comentariosTxt;
        public RelativeLayout image;
        public TextView numlikes;
        public ImageView vertblack;
        public ImageView imgperfil;


    }

    public AdapterComentarios(Activity context, String[] nombres, String[] dias, String[] mensajes, ArrayList<Bitmap> imagenes, String[] imagenesString) {
        super(context, R.layout.activity_comentarios, nombres);
        this.context = context;
        this.nombres = nombres;
        this.dias = dias;
        this.mensajes = mensajes;
        this.imagenes = imagenes;
        this.imagenesString = imagenesString;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.comentario_layout, null);


            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
//            if (flag==1){
//                viewHolder.namePost = (TextView) rowView.findViewById(R.id.nombrePer);
//
//                        TextView namePost = (TextView) findViewById(R.id.nombrePer);
//                TextView fecha = (TextView) findViewById(R.id.diatxt);
//                TextView mensaje = (TextView) findViewById(R.id.mensajeTime);
//                TextView numcomentarios = (TextView) findViewById(R.id.numComentarios);
//                TextView numlikes = (TextView) findViewById(R.id.burbuja);
//            }
            viewHolder.comentarioLayout = (RelativeLayout) rowView.findViewById(R.id.comentarioLayout);
            viewHolder.nombreTxt = (TextView) rowView.findViewById(R.id.nombrePerfilComentario);
            viewHolder.diaTxt = (TextView) rowView.findViewById(R.id.diatxtComentario);
            viewHolder.mensajeTxt = (TextView) rowView.findViewById(R.id.comentario);
            viewHolder.imgperfil = (ImageView) rowView.findViewById(R.id.imgperfilComentario);
            viewHolder.vertblack = (ImageView) rowView.findViewById(R.id.vertblack);

            rowView.setTag(viewHolder);
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        String dia;
        String nombre = nombres[position];
        switch (dias[position]) {
            case "0":
                dia = "Hoy";
                break;
            case "1":
                dia = "Hace " + dias[position] + " dia";
                break;
            default:
                dia = "Hace " + dias[position] + " dias";
                break;
        }
        String mensaje = mensajes[position];

        holder.nombreTxt.setText(nombre);
        holder.diaTxt.setText(dia);
        holder.mensajeTxt.setText(mensaje);
        if(position==getCount() - 1){
            holder.vertblack.setVisibility(View.VISIBLE);
        }
        return rowView;
    }

}
