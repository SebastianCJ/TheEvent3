package mx.com.theevent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.mikhaellopez.circularimageview.CircularImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class Notificaciones extends AppCompatActivity {
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    public SharedPreferences datosPersistentes;
    ProgressDialog pDialog;
    private String[] nombres;
    private String[] descripciones;
    private String[] rangoprecio;
    private String[] direccion;
    private String[] idubicaciones;
    private String id;
    private String[] titulos;
    JSONObject res;
    final String[] data ={"Mis Eventos","Mi Informacion","Notificaciones","Cerrar Sesion",};
    ListView eventContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.timelinemenu, data);

        final ImageView btnDrawerTimeline = (ImageView) findViewById(R.id.btnmenuOpen);
        final ImageView btnDrawerTimelineClose = (ImageView) findViewById(R.id.btnmenuClose);
        final CircularImageView fotoPerfil = (CircularImageView) findViewById(R.id.fotoEncabezado);
        final ImageView iconTimeline = (ImageView) findViewById(R.id.btntimeline);
        final ImageView iconCalendario = (ImageView) findViewById(R.id.btncalendario);
        final ImageView iconPublicacion = (ImageView) findViewById(R.id.btncentro);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput("myImage"));
            fotoPerfil.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


       // new AsyncUbicaciones().execute("ubicaciones");
        final TextView ubicacionNull = (TextView) findViewById(R.id.ubicacionNull);
        ubicacionNull.setVisibility(View.VISIBLE);
        final ImageView img = (ImageView) findViewById(R.id.imgmsg2);
        img.setVisibility(View.VISIBLE);

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getApplicationContext());
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
                        Intent intent = new Intent(Notificaciones.this, Camara.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                segundoboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Notificaciones.this, Galeria.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        iconTimeline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Notificaciones.this, Timeline.class);
                startActivity(intent);

            }
        });

        iconCalendario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Notificaciones.this, Calendario.class);
                startActivity(intent);

            }
        });

        iconPublicacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getApplicationContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_layout);

                TextView contenido = (TextView) dialog.findViewById(R.id.txtContenidoAlert);
                Button primerboton = (Button) dialog.findViewById(R.id.btnPrimero);
                Button segundoboton = (Button) dialog.findViewById(R.id.btnSegundo);
                Button tercerboton = (Button) dialog.findViewById(R.id.btnTercero);

                contenido.setText("Las publicaciones estaran disponibles hasta el dia del evento.");
                segundoboton.setText("OK");
                primerboton.setVisibility(View.GONE);
                tercerboton.setVisibility(View.GONE);

                segundoboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

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
                        Intent Intent = new Intent(Notificaciones.this, Evento.class);
                        startActivity(Intent);
                        break;
                    case 2:
                        Intent IntentInfo = new Intent(Notificaciones.this, MiInformacion.class);
                        startActivity(IntentInfo);
                        break;
                    case 3:
                        Intent IntentNotificaciones = new Intent(Notificaciones.this, Notificaciones.class);
                        startActivity(IntentNotificaciones);
                        break;

                    case 4:
                        final Dialog dialog = new Dialog(Notificaciones.this);
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
                                Intent cerrarSesion = new Intent(Notificaciones.this, Login.class);
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
        int dp2 = (int) (.5*scale + 0.1f);
        imageView.setPadding(dp2,dp,0,dp2);
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

    }


    private JSONObject obtenerUbicaciones() {
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        id = datosPersistentes.getString("idusrThe3v3nt","");
        String idevento = datosPersistentes.getString("ideventoThe3v3nt", "");
        Log.v("ESTE ES EL IDEVENTO",idevento);
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {

            respuesta = conexion.conexionServidor(serverUrl, "action=ubicaciones&idevento=" + idevento);

            if (respuesta.getString("success").equals("OK")) {

                JSONArray ubicaciones = respuesta.getJSONArray("ubicaciones");
                nombres = new String[ubicaciones.length()];
                descripciones = new String[ubicaciones.length()];
                rangoprecio = new String[ubicaciones.length()];
                direccion = new String[ubicaciones.length()];
                idubicaciones = new String[ubicaciones.length()];
                titulos = new String[ubicaciones.length()];

                int i = 0;

                while (i < ubicaciones.length()) {
                    JSONObject ubicacion = ubicaciones.getJSONObject(i);
                    nombres[i] = ubicacion.getString("nombre");
                    descripciones[i] = ubicacion.getString("descripcion");
                    rangoprecio[i] = ubicacion.getString("rangoprecio");
                    direccion[i] = ubicacion.getString("direccion");
                    titulos[i] = ubicacion.getString("titulo");
                    idubicaciones[i] = ubicacion.getString("idubicacion");
                    i++;
                }

            }
            return respuesta;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public class AsyncUbicaciones extends AsyncTask<String, String, String> {

        public AsyncUbicaciones() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventContainer = (ListView) findViewById(R.id.eventContainerUbicaciones);
            pDialog = new ProgressDialog(getApplicationContext());
            // Set progressbar message
            pDialog.setMessage("Cargando Ubicaciones...");
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
                    case "ubicaciones":
                        res = obtenerUbicaciones();
                        return res.getString("success");
                    case "eventimg":
                        String remotePath = params[1];
                        return "OK";
                    case "buscar":
                        res = obtenerUbicaciones();
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
                AdapterUbicaciones adapter = new AdapterUbicaciones(Notificaciones.this,nombres, descripciones, rangoprecio, direccion, titulos);
                eventContainer.setAdapter(adapter);
            }
            else{
                final TextView ubicacionNull = (TextView) findViewById(R.id.ubicacionNull);
                final ImageView img = (ImageView) findViewById(R.id.imgmsg2);
                img.setVisibility(View.VISIBLE);
                ubicacionNull.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onBackPressed(){
        Intent Intent = new Intent(Notificaciones.this, Timeline.class);
        startActivity(Intent);
    }


}

