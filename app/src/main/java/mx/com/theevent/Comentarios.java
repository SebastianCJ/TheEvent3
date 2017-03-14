package mx.com.theevent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class Comentarios extends AppCompatActivity {
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    public SharedPreferences datosPersistentes;
    ProgressDialog pDialog;
    private String[] nombres;
    private String[] comentarios;
    private String[] fechas;
    private String[] imagenesString;
    public TextView namePost;
    public TextView fecha;
    public TextView numcomentarios;
    public TextView mensaje;
    public RelativeLayout image;
    public TextView numlikes;
    public ImageView vertblack;
    public RelativeLayout post;
    public ImageView btnmegusta;
    public ImageView btnmegustafill;
    public ImageView btncomentario;
    public EditText comentTxt;
    public Button commentBtn;
    public CircularImageView foto1;
    public CircularImageView foto2;
    public TextView txt;
    public CircularImageView img;
    private Target target;
    private String id;
    JSONObject res;
    final String[] data ={"Mis Eventos","Mi Información","Información del Evento","Notificaciones","Cerrar Sesión",};
    ListView eventContainer;
    RelativeLayout timelineHead;
    RelativeLayout postHead;
    ArrayList<Bitmap> imagenes = new ArrayList<>();
    ArrayList<Bitmap> backgroundArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.timelinemenu, data);

        final ImageView btnDrawerTimeline = (ImageView) findViewById(R.id.btnmenuOpen);
        final ImageView btnDrawerTimelineClose = (ImageView) findViewById(R.id.btnmenuClose);
        final ImageView iconTimeline = (ImageView) findViewById(R.id.btntimeline);
        final ImageView iconCalendario = (ImageView) findViewById(R.id.btncalendario);
        final ImageView iconPublicacion = (ImageView) findViewById(R.id.btncentro);
        final ImageView iconRuta = (ImageView) findViewById(R.id.btnruta);
        final ImageView btnmegusta = (ImageView) findViewById(R.id.btnmegusta);
        final ImageView btncomentario = (ImageView) findViewById(R.id.btncomentario);
        final ImageView btnBack = (ImageView) findViewById(R.id.btnBack);
        comentTxt = (EditText) findViewById(R.id.comentarioTxt);
        commentBtn = (Button) findViewById(R.id.btnComentarioSend);



        new AsyncComentarios().execute("comentarios");

        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Comentarios.this, Timeline.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        iconTimeline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Comentarios.this, Timeline.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        iconCalendario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Comentarios.this, Calendario.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        iconRuta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Comentarios.this, Ubicaciones.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        iconPublicacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Comentarios.this, GaleriaPost.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        final ListView navList = (ListView) findViewById(R.id.drawer);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override @SuppressWarnings("Deprecation")
            public void onItemClick(AdapterView<?> parent, View view, final int pos,long id){
                drawer.setDrawerListener( new DrawerLayout.SimpleDrawerListener(){

                });
                switch(pos){
                    case 1:
                        Intent Intent = new Intent(Comentarios.this, Evento.class);
                        startActivity(Intent);
                        break;
                    case 2:
                        Intent IntentInfo = new Intent(Comentarios.this, MiInformacion.class);
                        startActivity(IntentInfo);
                        break;

                    case 3:
                        Intent IntentInfoEvento = new Intent(Comentarios.this, InfoEvento.class);
                        startActivity(IntentInfoEvento);
                        break;

                    case 4:
                        Intent IntentNotificaciones = new Intent(Comentarios.this, Notificaciones.class);
                        startActivity(IntentNotificaciones);
                        break;

                    case 5:
                        final Dialog dialog = new Dialog(Comentarios.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.dialog_layout);

                        TextView contenido = (TextView) dialog.findViewById(R.id.txtContenidoAlert);
                        Button primerboton = (Button) dialog.findViewById(R.id.btnPrimero);
                        Button segundoboton = (Button) dialog.findViewById(R.id.btnSegundo);
                        Button tercerboton = (Button) dialog.findViewById(R.id.btnTercero);

                        contenido.setText("¿Estas seguro que deseas cerrar sesión?");
                        primerboton.setText("OK");
                        segundoboton.setVisibility(View.GONE);
                        tercerboton.setText("CANCELAR");

                        primerboton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent cerrarSesion = new Intent(Comentarios.this, Login.class);
                                startActivity(cerrarSesion);
                            }
                        });

                        tercerboton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        break;

                }
                //drawer.closeDrawer(navList);

            }
        });

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.logomenu);
        float scale = getResources().getDisplayMetrics().density;
        int dp = (int) (15*scale + 0.5f);
        imageView.setPadding(dp,dp,0,dp);
        navList.addHeaderView(imageView);

        btnDrawerTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
                btnDrawerTimeline.setVisibility(View.GONE);
                btnDrawerTimelineClose.setVisibility(View.VISIBLE);


            }
        });

        btnDrawerTimelineClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(navList);
                btnDrawerTimeline.setVisibility(View.VISIBLE);
                btnDrawerTimelineClose.setVisibility(View.GONE);

            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comentario = comentTxt.getText().toString();
                if (!comentario.isEmpty()){
                    String idusr = datosPersistentes.getString("idusrThe3v3nt","");
                    String idpost = datosPersistentes.getString("idpostThe3v3nt","");
                    new AsyncComentar().execute("comentar",idusr,idpost,comentario);
                }
            }
        });

    }

    private JSONObject obtenerComentarios() {
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        id = datosPersistentes.getString("idusrThe3v3nt","");
        String idpost = datosPersistentes.getString("idpostThe3v3nt","");
        Log.v("ESTE ES EL IDPOST",idpost);
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {

            respuesta = conexion.conexionServidor(serverUrl, "action=comentarios&idpost=" + idpost);

            if (respuesta.getString("success").equals("OK")) {

                JSONArray posts = respuesta.getJSONArray("comentarios");
                nombres = new String[posts.length()];
                fechas = new String[posts.length()];
                comentarios = new String[posts.length()];
                imagenesString = new String[posts.length()];
                int i = 0;

                while (i < posts.length()) {
                    JSONObject post = posts.getJSONObject(i);
                    comentarios[i] = post.getString("comentario");
                    nombres[i] = post.getString("nombre");
                    fechas[i] = post.getString("fecha");
                    imagenesString[i] = post.getString("imagen");
                    i++;
                }

            }
            return respuesta;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    private void obtenerPost() {
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        id = datosPersistentes.getString("idpostThe3v3nt","");
        Log.v("ESTE ES EL IDPOST",id);

        final RelativeLayout timelineHead = (RelativeLayout) findViewById(R.id.timelineHead);

        TextView namePost = (TextView) findViewById(R.id.nombrePer);
        TextView fecha = (TextView) findViewById(R.id.diatxt);
        TextView mensaje = (TextView) findViewById(R.id.mensajeTime);
        TextView numcomentarios = (TextView) findViewById(R.id.numComentarios);
        TextView numlikes = (TextView) findViewById(R.id.burbuja);
        ImageView fotolike1 = (ImageView) findViewById(R.id.foto1);
        ImageView fotolike2 = (ImageView) findViewById(R.id.foto2);

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

        System.out.println("MEGUSTA SinComentarios " + liked);
        if(liked.equals("liked")){
            btnmegustafill.setVisibility(View.VISIBLE);
            btnmegusta.setVisibility(View.GONE);
        }
        else{
            btnmegustafill.setVisibility(View.GONE);
            btnmegusta.setVisibility(View.VISIBLE);
        }

        if (!foto1.equals("")){
            Picasso.with(getApplicationContext()).load(foto1).resize(50, 50).into(fotolike1);
        }
        if (!foto2.equals("")){
            Picasso.with(getApplicationContext()).load(foto2).resize(50, 50).into(fotolike2);
        }

        Picasso.with(getApplicationContext()).load(remotePath).resize(50, 50).into(img);
        Picasso.with(Comentarios.this).invalidate(remotePath);
        target = new Target() {

            @Override
            public void onPrepareLoad(Drawable arg0) {
                // TODO Auto-generated method stub
                // Set progressbar message

            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                // TODO Auto-generated method stub
                timelineHead.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                System.out.println("IMG Loaded Comentarios");
            }

            @Override
            public void onBitmapFailed(Drawable arg0) {
                // TODO Auto-generated method stub
                timelineHead.setBackground( getResources().getDrawable(R.mipmap.imgnot));
                System.out.println("IMG failed Comentarios");

            }
        };
        timelineHead.setTag(target);
        Picasso.with(getApplicationContext()).load(background).error(R.mipmap.imgnot).into((Target) timelineHead.getTag());

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

        namePost.setText(nombrePost);
        fecha.setText(dia);
        mensaje.setText(mensajePost);
        numcomentarios.setText(numComentarios);
        numlikes.setText(numLikes);

    }

    public class AsyncComentarios extends AsyncTask<String, String, String> {

        public AsyncComentarios() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            namePost = (TextView) findViewById(R.id.nombrePer);
            fecha = (TextView) findViewById(R.id.diatxt);
            mensaje = (TextView) findViewById(R.id.mensajeTime);
            numcomentarios = (TextView) findViewById(R.id.numComentarios);
            numlikes = (TextView) findViewById(R.id.burbuja);
            btnmegusta = (ImageView) findViewById(R.id.btnmegusta);
            btnmegustafill = (ImageView) findViewById(R.id.btnmegustafill);
            btncomentario = (ImageView) findViewById(R.id.btncomentario);
            foto1 = (CircularImageView) findViewById(R.id.foto1);
            foto2 = (CircularImageView) findViewById(R.id.foto2);
            img = (CircularImageView) findViewById(R.id.imgperfil);
            txt = (TextView) findViewById(R.id.txt);

            eventContainer = (ListView) findViewById(R.id.eventContainerComentarios);
            pDialog = new ProgressDialog(Comentarios.this);
            // Set progressbar message
            pDialog.setMessage("Cargando Comentarios...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            // Show progressbar
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            //String urlString = params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in = null;
            try {
                switch (params[0]) {
                    case "comentarios":
                        res = obtenerComentarios();
                        return res.getString("success");
                    case "eventimg":
                        String remotePath = params[1];
                        return "OK";
                    case "buscar":
                        //res = obtenerPosts();
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
            pDialog.dismiss();
            if (fechas!=null && fechas.length > 0) {
                AdapterComentarios adapter = new AdapterComentarios(Comentarios.this, nombres, fechas, comentarios, imagenes, imagenesString);
                eventContainer.setAdapter(adapter);
                namePost.setVisibility(View.GONE);
                fecha.setVisibility(View.GONE);
                mensaje.setVisibility(View.GONE);
                numcomentarios.setVisibility(View.GONE);
                numlikes.setVisibility(View.GONE);
                btnmegusta.setVisibility(View.GONE);
                btnmegustafill.setVisibility(View.GONE);
                btncomentario.setVisibility(View.GONE);
                foto1.setVisibility(View.GONE);
                foto2.setVisibility(View.GONE);
                img.setVisibility(View.GONE);
                txt.setVisibility(View.GONE);
                comentTxt.setVisibility(View.GONE);
                commentBtn.setVisibility(View.GONE);
            }
            else{
                eventContainer.setVisibility(View.GONE);
                comentTxt.setVisibility(View.VISIBLE);
                commentBtn.setVisibility(View.VISIBLE);
                obtenerPost();
            }
        }
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
            Intent intent = new Intent(Comentarios.this, Comentarios.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //Toast.makeText(Comentarios.this, "", Toast.LENGTH_LONG).show();

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

    @Override
    public void onBackPressed(){
        Intent Intent = new Intent(getApplicationContext(), Timeline.class);
        Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent);
    }
}
