package mx.com.theevent;

import android.app.Dialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import android.widget.TextView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class Timeline extends AppCompatActivity {
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    public SharedPreferences datosPersistentes;
    ProgressDialog pDialog;
    private String[] nombres;
    private String[] dias;
    private String[] mensajes;
    private String[] comentarios;
    private String[] likes;
    private String[] idposts;
    private String[] imagenesString;
    private String[] fotoperfil;
    private String[] fotosLikes;
    private String[] fotosLikes2;
    private String[] megusta;
    private String id;
    JSONObject res;
    private int flag;
    final String[] data ={"Mis Eventos","Mi Información","Información del Evento","Notificaciones","Cerrar Sesión",};
    ListView eventContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.timelinemenu, data);

        final ImageView btnDrawerTimeline = (ImageView) findViewById(R.id.btnmenuOpen);
        final ImageView btnDrawerTimelineClose = (ImageView) findViewById(R.id.btnmenuClose);
        final CircularImageView fotoPerfil = (CircularImageView) findViewById(R.id.fotoEncabezado);
        final ImageView iconCalendario = (ImageView) findViewById(R.id.btncalendario);
        final ImageView iconPublicacion = (ImageView) findViewById(R.id.btncentro);
        final ImageView iconRuta = (ImageView) findViewById(R.id.btnruta);


        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String remotePath = datosPersistentes.getString("fotoperfilThe3v3nt","");
        Picasso.with(getApplicationContext()).load(remotePath).resize(50, 50).into(fotoPerfil);

        flag = 0;
        new AsyncEventos().execute("timeline");

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Timeline.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_layout);

                TextView contenido = (TextView) dialog.findViewById(R.id.txtContenidoAlert);
                Button primerboton = (Button) dialog.findViewById(R.id.btnPrimero);
                Button segundoboton = (Button) dialog.findViewById(R.id.btnSegundo);
                Button tercerboton = (Button) dialog.findViewById(R.id.btnTercero);

                contenido.setText("Para cambiar la foto de perfil, porfavor seleccione una opcion.");
                segundoboton.setText("Galeria");
                primerboton.setText("Camara");
                tercerboton.setVisibility(View.GONE);

                primerboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Timeline.this, Camara.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                segundoboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Timeline.this, Galeria.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });


        iconCalendario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Timeline.this, Calendario.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });

        iconRuta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Timeline.this, Ubicaciones.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });

        iconPublicacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Timeline.this, GaleriaPost.class);
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
                        Intent Intent = new Intent(Timeline.this, Evento.class);
                        Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent);
                        break;
                    case 2:
                        Intent IntentInfo = new Intent(Timeline.this, MiInformacion.class);
                        IntentInfo.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(IntentInfo);
                        break;

                    case 3:
                        Intent IntentInfoEvento = new Intent(Timeline.this, InfoEvento.class);
                        IntentInfoEvento.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(IntentInfoEvento);
                        break;

                    case 4:
                        Intent IntentNotificaciones = new Intent(Timeline.this, Notificaciones.class);
                        IntentNotificaciones.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(IntentNotificaciones);
                        break;

                    case 5:
                        final Dialog dialog = new Dialog(Timeline.this);
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
                                Intent cerrarSesion = new Intent(Timeline.this, Login.class);
                                cerrarSesion.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
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

                System.out.println("OPEN");
            }
        });

        btnDrawerTimelineClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(navList);

                btnDrawerTimeline.setVisibility(View.VISIBLE);
                btnDrawerTimelineClose.setVisibility(View.GONE);
                System.out.println("CLOSE");
            }
        });



    }

    private JSONObject obtenerPosts(String metodo,String busqueda) {
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        id = datosPersistentes.getString("idusrThe3v3nt","");
        String idevento = datosPersistentes.getString("ideventoThe3v3nt", "");
        Log.v("ESTE ES EL IDUSUARIO",id);
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {
            if (metodo.equals("buscar")){
                respuesta = conexion.conexionServidor(serverUrl, "action=timeline&idusuario=" + id + "&idevento=" + idevento + "&buscar=" + busqueda);
            }else{
                respuesta = conexion.conexionServidor(serverUrl, "action=timeline&idusuario=" + id + "&idevento=" + idevento);
            }


            if (respuesta.getString("success").equals("OK")) {

                JSONArray posts = respuesta.getJSONArray("posts");
                nombres = new String[posts.length()];
                dias = new String[posts.length()];
                mensajes = new String[posts.length()];
                comentarios = new String[posts.length()];
                likes = new String[posts.length()];
                idposts = new String[posts.length()];
                fotoperfil = new String[posts.length()];
                imagenesString = new String[posts.length()];
                fotosLikes = new String[posts.length()];
                fotosLikes2 = new String[posts.length()];
                megusta = new String[posts.length()];
                int i = 0;

                while (i < posts.length()) {
                    JSONObject post = posts.getJSONObject(i);
                    nombres[i] = post.getString("nombre");
                    dias[i] = post.getString("fecha");
                    mensajes[i] = post.getString("post");
                    comentarios[i] = post.getString("numcomentarios");
                    likes[i] = post.getString("numlikes");
                    String remotePath = "http://theevent.com.mx/imagenes/timeline/" + post.getString("imagen");
                    String remotePathFoto = "http://theevent.com.mx/imagenes/usuarios/" + post.getString("foto");
                    imagenesString[i] = remotePath;
                    fotoperfil[i] = remotePathFoto;
                    idposts[i] = post.getString("idpost");
                    megusta [i] = post.getString("like");

                    fotosLikes[i] = "http://theevent.com.mx/imagenes/usuarios/" + post.getString("fotolike1");
                    fotosLikes2[i] = "http://theevent.com.mx/imagenes/usuarios/" + post.getString("fotolike2");
                    i++;
                }

            }
            return respuesta;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public class AsyncEventos extends AsyncTask<String, String, String> {

        public AsyncEventos() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventContainer = (ListView) findViewById(R.id.eventContainerTimeline);
            pDialog = new ProgressDialog(Timeline.this);
            // Set progressbar message
            pDialog.setMessage("Cargando Biografia...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            // Show progressbar
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            //String urlString = params[0]; // URL to call

            try {
                switch (params[0]) {
                    case "timeline":
                        res = obtenerPosts("normal"," ");
                        return res.getString("success");
                    case "eventimg":
                        String remotePath = params[1];
                        return "OK";
                    case "buscar":
                        res = obtenerPosts("buscar",params[1]);
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
            if (nombres != null && nombres.length > 0) {
                AdapterTimeline adapter = new AdapterTimeline(Timeline.this, nombres, dias, mensajes, imagenesString, comentarios, likes, idposts,fotoperfil,megusta,fotosLikes,fotosLikes2);
                eventContainer.setAdapter(adapter);
            }
            else{
                final TextView eventoNull = (TextView) findViewById(R.id.eventosNull);
                final ImageView img = (ImageView) findViewById(R.id.imgmsg);
                img.setVisibility(View.VISIBLE);
                eventoNull.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onBackPressed() {
        Intent Intent = new Intent(Timeline.this, Evento.class);
        Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent);
    }
}

