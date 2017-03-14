package mx.com.theevent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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


public class Calendario extends AppCompatActivity {
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    public SharedPreferences datosPersistentes;
    ProgressDialog pDialog;
    private String[] descripcion;
    private String[] dias;
    private String[] meses;
    private String[] horas;
    private String[] idcalendarios;
    private String[] years;
    private String[] lugares;
    private String[] latitudes;
    private String[] longitudes;
    private String id;
    JSONObject res;
    final String[] data ={"Mis Eventos","Mi Información","Información del Evento","Notificaciones","Cerrar Sesión",};
    ListView eventContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.timelinemenu, data);

        final ImageView btnDrawerTimeline = (ImageView) findViewById(R.id.btnmenuOpen);
        final ImageView btnDrawerTimelineClose = (ImageView) findViewById(R.id.btnmenuClose);
        final CircularImageView fotoPerfil = (CircularImageView) findViewById(R.id.fotoEncabezado);
        final ImageView iconTimeline = (ImageView) findViewById(R.id.btntimeline);
        final ImageView iconCalendario = (ImageView) findViewById(R.id.btncalendario);
        final ImageView iconPublicacion = (ImageView) findViewById(R.id.btncentro);
        final ImageView iconRuta = (ImageView) findViewById(R.id.btnruta);
//
//        try {
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            options.inSampleSize = calculateInSampleSize(options, 25, 25);
//
//            // Decode bitmap with inSampleSize set
//            options.inJustDecodeBounds = false;
//            Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput("myImage"),null,options);
//
//            fotoPerfil.setImageBitmap(bitmap);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String remotePath = datosPersistentes.getString("fotoperfilThe3v3nt","");
        Picasso.with(getApplicationContext()).load(remotePath).resize(50, 50).into(fotoPerfil);

        new AsyncCalendario().execute("calendario");

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Calendario.this);
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
                        Intent intent = new Intent(Calendario.this, Camara.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                segundoboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Calendario.this, Galeria.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        iconTimeline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Calendario.this, Timeline.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


        iconRuta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Calendario.this, Ubicaciones.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        iconPublicacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Calendario.this, GaleriaPost.class);
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
                        Intent Intent = new Intent(Calendario.this, Evento.class);
                        startActivity(Intent);
                        break;
                    case 2:
                        Intent IntentInfo = new Intent(Calendario.this, MiInformacion.class);
                        startActivity(IntentInfo);
                        break;

                    case 3:
                        Intent IntentInfoEvento = new Intent(Calendario.this, InfoEvento.class);
                        startActivity(IntentInfoEvento);
                        break;

                    case 4:
                        Intent IntentNotificaciones = new Intent(Calendario.this, Notificaciones.class);
                        startActivity(IntentNotificaciones);
                        break;

                    case 5:
                        final Dialog dialog = new Dialog(Calendario.this);
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
                                Intent cerrarSesion = new Intent(Calendario.this, Login.class);
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

    }

    private JSONObject obtenerCalendario() {
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        id = datosPersistentes.getString("idusrThe3v3nt","");
        String idevento = datosPersistentes.getString("ideventoThe3v3nt", "");
        Log.v("ESTE ES EL IDEVENTO",idevento);
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {

            respuesta = conexion.conexionServidor(serverUrl, "action=calendario&idevento=" + idevento);

            if (respuesta.getString("success").equals("OK")) {

                JSONArray calendarios = respuesta.getJSONArray("calendarios");
                descripcion = new String[calendarios.length()];
                dias = new String[calendarios.length()];
                meses = new String[calendarios.length()];
                horas = new String[calendarios.length()];
                idcalendarios = new String[calendarios.length()];
                years = new String[calendarios.length()];
                lugares = new String[calendarios.length()];
                latitudes = new String[calendarios.length()];
                longitudes = new String[calendarios.length()];

                int i = 0;

                while (i < calendarios.length()) {
                    JSONObject calendario = calendarios.getJSONObject(i);
                    descripcion[i] = calendario.getString("descripcion");
                    dias[i] = calendario.getString("dia");
                    meses[i] = calendario.getString("mes");
                    horas[i] = calendario.getString("hora");
                    idcalendarios[i] = calendario.getString("idcalendario");
                    years[i] = calendario.getString("year");
                    lugares[i] = calendario.getString("lugar");
                    latitudes[i] = calendario.getString("latitud");
                    longitudes[i] = calendario.getString("longitud");
                    i++;
                }

            }
            return respuesta;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public class AsyncCalendario extends AsyncTask<String, String, String> {

        public AsyncCalendario() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventContainer = (ListView) findViewById(R.id.eventContainerCalendario);
            pDialog = new ProgressDialog(Calendario.this);
            // Set progressbar message
            pDialog.setMessage("Cargando Actividades...");
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
                    case "calendario":
                        res = obtenerCalendario();
                        return res.getString("success");
                    case "eventimg":
                        String remotePath = params[1];
                        return "OK";
                    case "buscar":
                        res = obtenerCalendario();
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
            if (descripcion != null && descripcion.length > 0) {
                AdapterCalendario adapter = new AdapterCalendario(Calendario.this,descripcion, dias, meses, horas,years, lugares, latitudes, longitudes);
                eventContainer.setAdapter(adapter);
            }
            else{
                final TextView calendarioNull = (TextView) findViewById(R.id.calendarioNull);
                final ImageView img = (ImageView) findViewById(R.id.imgmsg);
                img.setVisibility(View.VISIBLE);
                calendarioNull.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onBackPressed(){
        Intent Intent = new Intent(Calendario.this, Timeline.class);
        Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent);
    }
}

