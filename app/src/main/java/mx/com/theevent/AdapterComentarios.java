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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class AdapterComentarios extends ArrayAdapter<String> {
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    private final Activity context;
    private final String[] nombres;
    private final String[] dias;
    private final String[] mensajes;
    public SharedPreferences datosPersistentes;
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
        public TextView fecha;
        public TextView numcomentarios;
        public TextView mensaje;
        public RelativeLayout image;
        public TextView numlikes;
        public ImageView vertblack;
        public ImageView imgperfil;
        public RelativeLayout post;
        public ImageView btnmegusta;
        public ImageView btnmegustafill;
        public ImageView btncomentario;
        public CircularImageView foto1;
        public CircularImageView foto2;
        public TextView txt;
        public CircularImageView img;
        public EditText comentarioTxt;
        public Button comentarioBtn;

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

    public class AsyncComentar extends AsyncTask<String, String, String> {

        public AsyncComentar() {
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
                    case "comentar":
                        res = comentar(params[1],params[2],params[3]);
                        return res.getString("success");
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
            Intent intent = new Intent(context, Comentarios.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_LONG).show();

        }
    }

    private JSONObject comentar(String idusr,String idpost,String Comentario) {
        Log.v("ESTE ES EL IDPOST",idpost);
        Log.v("EST ES EL IDUSR",idusr);
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=comentar&idpost=" + idpost + "&idusr=" + idusr + "&comentario=" + Comentario);

            if (respuesta.getString("success").equals("OK")) {

                return respuesta;

            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
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
                        res = megusta("megusta",params[1],params[2]);
                        return res.getString("success");
                    case "down":
                        res = megusta("yanomegusta",params[1],params[2]);
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

    private JSONObject megusta(String metodo,String id,String idusr) {
        Log.v("ESTE ES EL IDPOST",id);
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {
            if (metodo.equals("megusta")){
                respuesta = conexion.conexionServidor(serverUrl, "action=megusta&idpost=" + id + "&idusr=" + idusr);
            }else{
                respuesta = conexion.conexionServidor(serverUrl, "action=yanomegusta&idpost=" + id + "&idusr=" + idusr);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.comentario_layout, null);


            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.comentarioLayout = (RelativeLayout) rowView.findViewById(R.id.comentarioLayout);
            viewHolder.nombreTxt = (TextView) rowView.findViewById(R.id.nombrePerfilComentario);
            viewHolder.diaTxt = (TextView) rowView.findViewById(R.id.diatxtComentario);
            viewHolder.mensajeTxt = (TextView) rowView.findViewById(R.id.comentario);
            viewHolder.imgperfil = (ImageView) rowView.findViewById(R.id.imgperfilComentario);
            viewHolder.img = (CircularImageView) rowView.findViewById(R.id.imgperfil);
            viewHolder.vertblack = (ImageView) rowView.findViewById(R.id.vertblack);
            viewHolder.post = (RelativeLayout) rowView.findViewById(R.id.timelineHead);
            viewHolder.namePost = (TextView) rowView.findViewById(R.id.nombrePer);
            viewHolder.fecha = (TextView) rowView.findViewById(R.id.diatxt);
            viewHolder.mensaje = (TextView) rowView.findViewById(R.id.mensajeTime);
            viewHolder.numcomentarios = (TextView) rowView.findViewById(R.id.numComentarios);
            viewHolder.numlikes = (TextView) rowView.findViewById(R.id.burbuja);
            viewHolder.btnmegusta = (ImageView) rowView.findViewById(R.id.btnmegusta);
            viewHolder.btnmegustafill = (ImageView) rowView.findViewById(R.id.btnmegustafill);
            viewHolder.btncomentario = (ImageView) rowView.findViewById(R.id.btncomentario);
            viewHolder.foto1 = (CircularImageView) rowView.findViewById(R.id.foto1);
            viewHolder.foto2 = (CircularImageView) rowView.findViewById(R.id.foto2);
            viewHolder.txt = (TextView) rowView.findViewById(R.id.txt);
            viewHolder.comentarioBtn = (Button) rowView.findViewById(R.id.btnComentarioSend);
            viewHolder.comentarioTxt = (EditText) rowView.findViewById(R.id.comentarioTxt);
            rowView.setTag(viewHolder);
        }


        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        if(position == 0){
            datosPersistentes = context.getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
            String id = datosPersistentes.getString("idpostThe3v3nt","");
            Log.v("ESTE ES EL IDPOST",id);


            String nombrePost = datosPersistentes.getString("nombrePost", "");
            String mensajePost = datosPersistentes.getString("mensajePost", "");
            String numlikesPost = datosPersistentes.getString("numlikesPost", "");
            String numcomentariosPost = datosPersistentes.getString("numcomentariosPost", "");
            String fechaPost = datosPersistentes.getString("fechaPost", "");
            String dia;
            String numComentarios = numcomentariosPost + " comentarios";
            String numLikes = "   +" + numlikesPost + "    ";
            String background = datosPersistentes.getString("backgroundPost", "");
            String remotePath = datosPersistentes.getString("fotoperfil","");
            String liked = datosPersistentes.getString("likedPost","");
            String foto1 = datosPersistentes.getString("fotolike1","");
            String foto2 = datosPersistentes.getString("fotolike2","");

            System.out.println("MEGUSTA ConComentarios " + liked);
            if(liked.equals("liked")){
                holder.btnmegustafill.setVisibility(View.VISIBLE);
                holder.btnmegusta.setVisibility(View.GONE);
            }
            else{
                holder.btnmegustafill.setVisibility(View.GONE);
                holder.btnmegusta.setVisibility(View.VISIBLE);
            }

            if (!foto1.equals("")){
                Picasso.with(context.getApplicationContext()).load(foto1).resize(50, 50).into(holder.foto1);
            }
            if (!foto2.equals("")){
                Picasso.with(context.getApplicationContext()).load(foto2).resize(50, 50).into(holder.foto2);
            }

            Picasso.with(context.getApplicationContext()).load(remotePath).resize(50, 50).into(holder.img);
            Picasso.with(context.getApplicationContext()).invalidate(remotePath);
            Picasso.with(context.getApplicationContext()).load(background).into(new Target() {

                @Override
                public void onPrepareLoad(Drawable arg0) {
                    // TODO Auto-generated method stub
                    // Set progressbar message

                }

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                    // TODO Auto-generated method stub
                    holder.post.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable arg0) {
                    // TODO Auto-generated method stub

                }
            });

            switch (fechaPost) {
                case "0":
                    dia = "Hoy";
                    break;
                case "1":
                    dia = "Hace " + fechaPost + " dia";
                    break;
                default:
                    dia = "Hace " + fechaPost + " dias";
                    break;
            }
            holder.namePost.setText(nombrePost);
            holder.fecha.setText(dia);
            holder.mensaje.setText(mensajePost);
            holder.numcomentarios.setText(numComentarios);
            holder.numlikes.setText(numLikes);
        }
        else{
            holder.namePost.setVisibility(View.GONE);
            holder.fecha.setVisibility(View.GONE);
            holder.mensaje.setVisibility(View.GONE);
            holder.numcomentarios.setVisibility(View.GONE);
            holder.numlikes.setVisibility(View.GONE);
            holder.post.setVisibility(View.GONE);
            holder.btnmegusta.setVisibility(View.GONE);
            holder.btnmegustafill.setVisibility(View.GONE);
            holder.btncomentario.setVisibility(View.GONE);
            holder.foto1.setVisibility(View.GONE);
            holder.foto2.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
            holder.txt.setVisibility(View.GONE);
        }


        holder.comentarioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comentario = holder.comentarioTxt.getText().toString();
                if (!comentario.isEmpty()){
                    String idusr = datosPersistentes.getString("idusrThe3v3nt","");
                    String idpost = datosPersistentes.getString("idpostThe3v3nt","");
                    new AsyncComentar().execute("comentar",idusr,idpost,comentario);
                }
            }
        });

        holder.btnmegusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numlikesPost = datosPersistentes.getString("numlikesPost", "");
                String id = datosPersistentes.getString("idpostThe3v3nt","");
                holder.btnmegustafill.setVisibility(View.VISIBLE);
                holder.btnmegusta.setVisibility(View.GONE);
                Integer newLikes=Integer.parseInt(numlikesPost) + 1;
                String newLike = Integer.toString(newLikes);
                String numLikes = "   +" + newLike + "    ";
                holder.numlikes.setText(numLikes);;
                String idusr = datosPersistentes.getString("idusrThe3v3nt","");
                new AsyncMeGusta().execute("up",id,idusr);
            }
        });

        holder.btnmegustafill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numlikesPost = datosPersistentes.getString("numlikesPost", "");
                String id = datosPersistentes.getString("idpostThe3v3nt","");
                holder.btnmegustafill.setVisibility(View.GONE);
                holder.btnmegusta.setVisibility(View.VISIBLE);
                Integer newLikes=Integer.parseInt(numlikesPost);
                String newLike = Integer.toString(newLikes);
                String numLikes = "   +" + newLike + "    ";
                holder.numlikes.setText(numLikes);
                String idusr = datosPersistentes.getString("idusrThe3v3nt","");
                new AsyncMeGusta().execute("down",id,idusr);
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
        Picasso.with(context.getApplicationContext()).load("http://theevent.com.mx/imagenes/usuarios/" + imagenesString[position]).resize(50, 50).into(holder.imgperfil);
        holder.nombreTxt.setText(nombre);
        holder.diaTxt.setText(dia);
        holder.mensajeTxt.setText(mensaje);

        if(position==getCount()- 1){
            holder.comentarioTxt.setVisibility(View.VISIBLE);
            holder.comentarioBtn.setVisibility(View.VISIBLE);
        }

        return rowView;
    }

}
