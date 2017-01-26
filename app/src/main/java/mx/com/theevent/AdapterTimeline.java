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


public class AdapterTimeline extends ArrayAdapter<String> {
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    public SharedPreferences datosPersistentes;
    private final Activity context;
    private final String[] nombres;
    private final String[] dias;
    private final String[] mensajes;
    private final String[] comentarios;
    private final String[] likes;
    private final String[] idposts;
    private String[] imagenesString;
    JSONObject res;
    ArrayList<Bitmap> imagenes;

    static class ViewHolder {
        public TextView nombreTxt;
        public TextView mensajeTxt;
        public TextView diaTxt;
        public TextView gustaTxt;
        public TextView comentariosTxt;
        public RelativeLayout image;
        public ImageView btnmegusta;
        public ImageView btnmegustafill;
        public ImageView btncomentario;
        public ImageView btnruta;
        public TextView numlikes;
        public ImageView vertblack;
        public RelativeLayout timelinehead;
        public LinearLayout comentariolayout;
        public LinearLayout rutalayout;
        public ImageView imgperfil;
        public ImageView foto1;
        public ImageView foto2;

    }

    public class AsyncMeGusta extends AsyncTask<String, String, String> {

        public AsyncMeGusta() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            //String urlString = params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in = null;
            try {
                switch (params[0]) {
                    case "up":
                        res = megusta("megusta",params[1]);
                        return res.getString("success");
                    case "down":
                        res = megusta("yanomegusta",params[1]);
                        return res.getString("sucess");
                }


            } catch (Exception e) {

                System.out.println(e.getMessage());

                return e.getMessage();

            }

            try {
                return res.getString("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "Error";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


        }
    }

    private JSONObject megusta(String metodo,String id) {
        Log.v("ESTE ES EL IDPOST",id);
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {
            if (metodo.equals("megusta")){
                respuesta = conexion.conexionServidor(serverUrl, "action=megusta&idpost=" + id);
            }else{
                respuesta = conexion.conexionServidor(serverUrl, "action=yanomegusta&idpost=" + id);
            }

            if (respuesta.getString("success").equals("OK")) {

                return respuesta;

            }
            return respuesta;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public AdapterTimeline(Activity context, String[] nombres, String[] dias, String[] mensajes, ArrayList<Bitmap> imagenes,String[] comentarios,String[] likes,String[] idposts,String[] imagenesString) {
        super(context, R.layout.activity_timeline, nombres);
        this.context = context;
        this.nombres = nombres;
        this.dias = dias;
        this.mensajes = mensajes;
        this.imagenes = imagenes;
        this.comentarios = comentarios;
        this.likes = likes;
        this.idposts = idposts;
        this.imagenesString = imagenesString;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        final int posicion = position;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.post_layout, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.timelinehead = (RelativeLayout) rowView.findViewById(R.id.timelineHead);
            viewHolder.nombreTxt = (TextView) rowView.findViewById(R.id.nombrePer);
            viewHolder.diaTxt = (TextView) rowView.findViewById(R.id.diatxt);
            viewHolder.mensajeTxt = (TextView) rowView.findViewById(R.id.mensajeTime);
            viewHolder.gustaTxt = (TextView) rowView.findViewById(R.id.txt);
            viewHolder.comentariosTxt = (TextView) rowView.findViewById(R.id.numComentarios);
            viewHolder.numlikes = (TextView) rowView.findViewById(R.id.burbuja);

            viewHolder.btnmegusta = (ImageView) rowView.findViewById(R.id.btnmegusta);
            viewHolder.btnmegustafill = (ImageView) rowView.findViewById(R.id.btnmegustafill);
            viewHolder.btncomentario = (ImageView) rowView.findViewById(R.id.btncomentario);
            viewHolder.vertblack = (ImageView) rowView.findViewById(R.id.vertblack);


            viewHolder.imgperfil = (ImageView) rowView.findViewById(R.id.imgperfil);
            viewHolder.foto1 = (ImageView) rowView.findViewById(R.id.foto1);
            viewHolder.foto2 = (ImageView) rowView.findViewById(R.id.foto2);

            viewHolder.nombreTxt.setTypeface(CustomFontsLoader.getTypeface(context,CustomFontsLoader.Regular));
            viewHolder.diaTxt.setTypeface(CustomFontsLoader.getTypeface(context,CustomFontsLoader.Light));
            viewHolder.mensajeTxt.setTypeface(CustomFontsLoader.getTypeface(context,CustomFontsLoader.Light));
            viewHolder.gustaTxt.setTypeface(CustomFontsLoader.getTypeface(context,CustomFontsLoader.Light));
            viewHolder.comentariosTxt.setTypeface(CustomFontsLoader.getTypeface(context,CustomFontsLoader.Light));

            viewHolder.image = (RelativeLayout) rowView
                    .findViewById(R.id.timelineHead);
            rowView.setTag(viewHolder);
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.timelinehead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = context.getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
                SharedPreferences.Editor editarDatosPersistentes = settings.edit();
                editarDatosPersistentes.putString("idpostThe3v3nt", idposts[posicion]);
                editarDatosPersistentes.putString("backgroundPost", imagenesString[posicion]);
                editarDatosPersistentes.putString("nombrePost", nombres[posicion]);
                editarDatosPersistentes.putString("mensajePost", mensajes[posicion]);
                editarDatosPersistentes.putString("numlikesPost", likes[posicion]);
                editarDatosPersistentes.putString("numcomentariosPost", comentarios[posicion]);
                editarDatosPersistentes.putString("fechaPost", dias[posicion]);
//                editarDatosPersistentes.putString("idpostThe3v3nt", idposts[posicion]);
//                editarDatosPersistentes.putString("idpostThe3v3nt", idposts[posicion]);
//                editarDatosPersistentes.putString("idpostThe3v3nt", idposts[posicion]);
//                editarDatosPersistentes.putString("idpostThe3v3nt", idposts[posicion]);
                editarDatosPersistentes.apply();
                Intent intent = new Intent(context, Comentarios.class);
                context.startActivity(intent);
            }
        });

        holder.btnmegusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnmegustafill.setVisibility(View.VISIBLE);
                holder.btnmegusta.setVisibility(View.GONE);
                Integer newLikes=Integer.parseInt(likes[posicion]) + 1;
                String newLike = Integer.toString(newLikes);
                String numLikes = "   +" + newLike + "    ";
                holder.numlikes.setText(numLikes);;
                new AsyncMeGusta().execute("up",idposts[posicion]);
            }
        });

        holder.btnmegustafill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnmegustafill.setVisibility(View.GONE);
                holder.btnmegusta.setVisibility(View.VISIBLE);
                Integer newLikes=Integer.parseInt(likes[posicion]);
                String newLike = Integer.toString(newLikes);
                String numLikes = "   +" + newLike + "    ";
                holder.numlikes.setText(numLikes);
                new AsyncMeGusta().execute("down",idposts[posicion]);
            }
        });

        holder.btncomentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Comentarios.class);
                context.startActivity(intent);;
            }
        });

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
        String numComentarios = comentarios[position]+ " comentarios";
        String numLikes = "   +" + likes[position]+ "    ";
        if (imagenes.size()>0) {
            Bitmap imagen = imagenes.get(position);
            Drawable dr = new BitmapDrawable(imagen);
            holder.image.setBackgroundDrawable(dr);
        }
        holder.nombreTxt.setText(nombre);
        holder.diaTxt.setText(dia);
        holder.mensajeTxt.setText(mensaje);
        holder.comentariosTxt.setText(numComentarios);
        holder.numlikes.setText(numLikes);
        if(position==getCount()- 1){
                holder.vertblack.setVisibility(View.VISIBLE);
        }
        return rowView;
    }

}
