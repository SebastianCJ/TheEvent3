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
import android.widget.Spinner;
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
import java.util.Arrays;


public class Ubicaciones extends AppCompatActivity {
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    public SharedPreferences datosPersistentes;
    ProgressDialog pDialog;
    private String[] nombres;
    private String[] descripciones;
    private String[] rangoprecio;
    private String[] direccion;
    private String[] idubicaciones;
    private String[] latitudes;
    private String[] longitudes;
    private String[] categorias;
    private String[] idcategorias;
    private String[] subcategorias;
    private String[] idsubcategorias;
    private String[] imgPaths;
    private String[] idcategoria;
    private String id;
    Spinner dropdown;
    Spinner dropdownSub;
    private String[] titulos;
    CircularImageView fotoPerfil;
    JSONObject res;
    final String[] data ={"Mis Eventos","Mi Información","Información del Evento","Notificaciones","Cerrar Sesión",};
    ListView eventContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicaciones);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.timelinemenu, data);

        final ImageView btnDrawerTimeline = (ImageView) findViewById(R.id.btnmenuOpen);
        final ImageView btnDrawerTimelineClose = (ImageView) findViewById(R.id.btnmenuClose);
        fotoPerfil = (CircularImageView) findViewById(R.id.fotoEncabezado);
        final ImageView iconTimeline = (ImageView) findViewById(R.id.btntimeline);
        final ImageView iconCalendario = (ImageView) findViewById(R.id.btncalendario);
        final ImageView iconPublicacion = (ImageView) findViewById(R.id.btncentro);
        dropdownSub = (Spinner)findViewById(R.id.spinnerSub);


        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String remotePath = datosPersistentes.getString("fotoperfilThe3v3nt","");
        Picasso.with(getApplicationContext()).load(remotePath).resize(50, 50).into(fotoPerfil);

        new AsyncCategorias().execute("categorias");

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Ubicaciones.this);
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
                        Intent intent = new Intent(Ubicaciones.this, Camara.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                segundoboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Ubicaciones.this, Galeria.class);
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
                Intent intent = new Intent(Ubicaciones.this, Timeline.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        iconCalendario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Ubicaciones.this, Calendario.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });



        iconPublicacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Ubicaciones.this, GaleriaPost.class);
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
                        Intent Intent = new Intent(Ubicaciones.this, Evento.class);
                        Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent);
                        break;
                    case 2:
                        Intent IntentInfo = new Intent(Ubicaciones.this, MiInformacion.class);
                        IntentInfo.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(IntentInfo);
                        break;

                    case 3:
                        Intent IntentInfoEvento = new Intent(Ubicaciones.this, InfoEvento.class);
                        IntentInfoEvento.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(IntentInfoEvento);
                        break;

                    case 4:
                        Intent IntentNotificaciones = new Intent(Ubicaciones.this, Notificaciones.class);
                        IntentNotificaciones.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(IntentNotificaciones);
                        break;

                    case 5:
                        final Dialog dialog = new Dialog(Ubicaciones.this);
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
                                Intent cerrarSesion = new Intent(Ubicaciones.this, Login.class);
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

    private JSONObject obtenerCategorias() {
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        id = datosPersistentes.getString("idusrThe3v3nt","");
        String idevento = datosPersistentes.getString("ideventoThe3v3nt", "");
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {
            Log.d("UBICACIONES: ", "action=categorias&idevento=" + idevento);
            respuesta = conexion.conexionServidor(serverUrl, "action=categorias&idevento=" + idevento);
            if (respuesta.getString("success").equals("OK")) {

                JSONArray ubicaciones = respuesta.getJSONArray("categorias");
                categorias = new String[ubicaciones.length()];
                idcategorias = new String[ubicaciones.length()];
                int i = 0;

                while (i < ubicaciones.length()) {
                    JSONObject ubicacion = ubicaciones.getJSONObject(i);
                    categorias[i] = ubicacion.getString("nombre");
                    idcategorias[i] = ubicacion.getString("idcategoria");
                    i++;
                }

            }
            return respuesta;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    private JSONObject obtenerSubCategorias(String idcategoria) {
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        id = datosPersistentes.getString("idusrThe3v3nt","");
        String idevento = datosPersistentes.getString("ideventoThe3v3nt", "");
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {
            Log.d("UBICACIONES: ", "action=subcategorias&idevento=" + idevento + "&idcategoria=" + idcategoria);
            respuesta = conexion.conexionServidor(serverUrl, "action=subcategorias&idevento=" + idevento + "&idcategoria=" + idcategoria);
            if (respuesta.getString("success").equals("OK")) {

                JSONArray ubicaciones = respuesta.getJSONArray("subcategorias");
                subcategorias = new String[ubicaciones.length()];
                idsubcategorias = new String[ubicaciones.length()];
                int i = 0;

                while (i < ubicaciones.length()) {
                    JSONObject ubicacion = ubicaciones.getJSONObject(i);
                    subcategorias[i] = ubicacion.getString("nombre");
                    idsubcategorias[i] = ubicacion.getString("idsubcategoria");
                    i++;
                }

            }
            else{
                subcategorias = new String[0];
                idsubcategorias = new String[0];
            }
            return respuesta;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    private JSONObject obtenerUbicaciones(String idcategoria) {
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        id = datosPersistentes.getString("idusrThe3v3nt","");
        String idevento = datosPersistentes.getString("ideventoThe3v3nt", "");
        Log.v("ESTE ES EL IDEVENTO",idevento);
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {
            Log.d("UBICACIONES: ", "action=ubicaciones&idevento=" + idevento + "&idcategoria=" + idcategoria);
            respuesta = conexion.conexionServidor(serverUrl, "action=ubicaciones&idevento=" + idevento + "&idcategoria=" + idcategoria);
            if (respuesta.getString("success").equals("OK")) {

                JSONArray ubicaciones = respuesta.getJSONArray("ubicaciones");
                nombres = new String[ubicaciones.length()];
                descripciones = new String[ubicaciones.length()];
                rangoprecio = new String[ubicaciones.length()];
                direccion = new String[ubicaciones.length()];
                idubicaciones = new String[ubicaciones.length()];
                titulos = new String[ubicaciones.length()];
                latitudes = new String[ubicaciones.length()];
                longitudes = new String[ubicaciones.length()];
                imgPaths = new String[ubicaciones.length()];

                int i = 0;

                while (i < ubicaciones.length()) {
                    JSONObject ubicacion = ubicaciones.getJSONObject(i);
                    nombres[i] = ubicacion.getString("nombre");
                    descripciones[i] = ubicacion.getString("descripcion");
                    rangoprecio[i] = ubicacion.getString("rangoprecio");
                    direccion[i] = ubicacion.getString("direccion");
                    titulos[i] = ubicacion.getString("titulo");
                    idubicaciones[i] = ubicacion.getString("idubicacion");
                    latitudes[i] = ubicacion.getString("latitud");
                    longitudes[i] = ubicacion.getString("longitud");
                    String remotePath = "http://theevent.com.mx/imagenes/ubicaciones/" + idubicaciones[i] + ".jpg";
                    imgPaths[i] = remotePath;
                    i++;
                }

            }
            return respuesta;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    private JSONObject obtenerUbicacionesSubcategorias(String idsubcategoria) {
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        id = datosPersistentes.getString("idusrThe3v3nt","");
        String idevento = datosPersistentes.getString("ideventoThe3v3nt", "");
        Log.v("ESTE ES EL IDEVENTO",idevento);
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {
            Log.d("UBICACIONES: ", "action=ubicacionesSubCategorias&idevento=" + idevento + "&idsubcategoria=" + idsubcategoria);
            respuesta = conexion.conexionServidor(serverUrl, "action=ubicacionesSubCategorias&idevento=" + idevento + "&idsubcategoria=" + idsubcategoria);
            if (respuesta.getString("success").equals("OK")) {

                JSONArray ubicaciones = respuesta.getJSONArray("ubicaciones");
                nombres = new String[ubicaciones.length()];
                descripciones = new String[ubicaciones.length()];
                rangoprecio = new String[ubicaciones.length()];
                direccion = new String[ubicaciones.length()];
                idubicaciones = new String[ubicaciones.length()];
                titulos = new String[ubicaciones.length()];
                latitudes = new String[ubicaciones.length()];
                longitudes = new String[ubicaciones.length()];
                imgPaths = new String[ubicaciones.length()];

                int i = 0;

                while (i < ubicaciones.length()) {
                    JSONObject ubicacion = ubicaciones.getJSONObject(i);
                    nombres[i] = ubicacion.getString("nombre");
                    descripciones[i] = ubicacion.getString("descripcion");
                    rangoprecio[i] = ubicacion.getString("rangoprecio");
                    direccion[i] = ubicacion.getString("direccion");
                    titulos[i] = ubicacion.getString("titulo");
                    idubicaciones[i] = ubicacion.getString("idubicacion");
                    latitudes[i] = ubicacion.getString("latitud");
                    longitudes[i] = ubicacion.getString("longitud");
                    String remotePath = "http://theevent.com.mx/imagenes/ubicaciones/" + idubicaciones[i] + ".jpg";
                    imgPaths[i] = remotePath;
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
            pDialog = new ProgressDialog(Ubicaciones.this);
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
                        res = obtenerUbicaciones(params[1]);
                        return res.getString("success");
                    case "subcategorias":
                        res = obtenerUbicacionesSubcategorias(params[1]);
                        return res.getString("success");
                    case "buscar":
                        res = obtenerUbicaciones(params[1]);
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
                AdapterUbicaciones adapter = new AdapterUbicaciones(Ubicaciones.this,nombres, descripciones, rangoprecio, direccion, titulos, imgPaths, latitudes, longitudes,idubicaciones);
                eventContainer.setAdapter(adapter);
            }
            else{
                final TextView ubicacionNull = (TextView) findViewById(R.id.ubicacionNull);
                final ImageView img = (ImageView) findViewById(R.id.imgmsg);
                img.setVisibility(View.VISIBLE);
                ubicacionNull.setVisibility(View.VISIBLE);
            }
        }
    }


    public class AsyncCategorias extends AsyncTask<String, String, String> {

        public AsyncCategorias() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventContainer = (ListView) findViewById(R.id.eventContainerUbicaciones);

        }


        @Override
        protected String doInBackground(String... params) {

            //String urlString = params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in = null;
            try {
                switch (params[0]) {
                    case "categorias":
                        res = obtenerCategorias();
                        return res.getString("success");
                    case "subcategorias":
                        res = obtenerSubCategorias(params[1]);
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

            dropdown = (Spinner)findViewById(R.id.spinner);
            dropdownSub = (Spinner)findViewById(R.id.spinnerSub);
            final TextView noCats = (TextView) findViewById(R.id.ubicacionNullCat);
            final ImageView msg = (ImageView) findViewById(R.id.imgmsg);
            int hidingItemIndex = 0;
            if (categorias != null && categorias.length > 0) {
                SpinnerAdapter dataAdapterCategorias = new SpinnerAdapter(getApplicationContext(), R.layout.spinner_item, categorias, hidingItemIndex);
                dataAdapterCategorias.setDropDownViewResource(R.layout.spinner_item);
                dropdown.setAdapter(dataAdapterCategorias);

                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        System.out.println(dropdown.getSelectedItem().toString());
                        System.out.println(idcategorias[position]);
                        if (!idcategorias[position].equals("0")) {
                            noCats.setVisibility(View.GONE);
                            msg.setVisibility(View.GONE);
                            subcategorias = new String[0];
                            idsubcategorias = new String[0];
                            new AsyncSubCategorias().execute("subcategorias", idcategorias[position]);
//                            if (!(subcategorias != null && subcategorias.length > 0)) {
//                                new AsyncUbicaciones().execute("ubicaciones", idcategorias[position]);
//                            }
                            SharedPreferences settings = getApplicationContext().getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editarDatosPersistentes = settings.edit();
                            editarDatosPersistentes.putString("selectedUbicationThe3v3nt", idcategorias[position]);
                            editarDatosPersistentes.apply();
                        }
                        else{
                            String selectedUbication = datosPersistentes.getString("selectedUbicationThe3v3nt","");
                            String selectedSubUbication = datosPersistentes.getString("selectedSubUbicationThe3v3nt","");
                            if (!selectedUbication.equals("") && !selectedSubUbication.equals("")) {
                                int myNum = Integer.parseInt(selectedUbication);
                                dropdown.setSelection(myNum);
                                int myNumSub = Integer.parseInt(selectedSubUbication);
                                dropdownSub.setSelection(myNumSub);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

            }

        }
    }

    public class AsyncSubCategorias extends AsyncTask<String, String, String> {

        public AsyncSubCategorias() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventContainer = (ListView) findViewById(R.id.eventContainerUbicaciones);
        }


        @Override
        protected String doInBackground(String... params) {

            //String urlString = params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in = null;
            try {
                switch (params[0]) {
                    case "subcategorias":
                        res = obtenerSubCategorias(params[1]);
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
            int hidingItemIndex = 0;
                if (subcategorias != null && subcategorias.length > 0) {
                    SpinnerAdapter dataAdapterSubCategorias = new SpinnerAdapter(getApplicationContext(), R.layout.spinner_item, subcategorias, hidingItemIndex);
                    dataAdapterSubCategorias.setDropDownViewResource(R.layout.spinner_item);
                    dropdownSub.setAdapter(dataAdapterSubCategorias);
                    dropdownSub.setVisibility(View.VISIBLE);

                    dropdownSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            System.out.println(dropdownSub.getSelectedItem().toString());
                            System.out.println(idsubcategorias[position]);
                            if (!idsubcategorias[position].equals("0")) {
                                new AsyncUbicaciones().execute("subcategorias", idsubcategorias[position]);
                                SharedPreferences settings = getApplicationContext().getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editarDatosPersistentes = settings.edit();
                                editarDatosPersistentes.putString("selectedSubUbicationThe3v3nt", idsubcategorias[position]);
                                editarDatosPersistentes.apply();
                            }

                            if (position==0){
                                String posicion = datosPersistentes.getString("selectedUbicationThe3v3nt","");
                                new AsyncUbicaciones().execute("ubicaciones", posicion);
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });
                }
        }
    }

    @Override
    public void onBackPressed(){
        Intent Intent = new Intent(Ubicaciones.this, Timeline.class);
        Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        SharedPreferences settings = getApplicationContext().getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        SharedPreferences.Editor editarDatosPersistentes = settings.edit();
        editarDatosPersistentes.putString("selectedUbicationThe3v3nt", "");
        editarDatosPersistentes.putString("selectedSubUbicationThe3v3nt", "");
        editarDatosPersistentes.apply();
        startActivity(Intent);
    }



}

