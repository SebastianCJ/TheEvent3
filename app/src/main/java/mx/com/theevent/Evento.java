package mx.com.theevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class Evento extends AppCompatActivity {
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    public SharedPreferences datosPersistentes;
    ProgressDialog pDialog;
    private String[] nombres;
    private String[] lugares;
    private String[] descripciones;
    private String[] fechas;
    private String id;
    private String[] ideventos;
    JSONObject res;
    ListView eventContainer;
    ArrayList<Bitmap> imagenes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);


        TextView selectEvento = (TextView) this.findViewById(R.id.seleccionaEvento);
        TextView misEventos = (TextView) this.findViewById(R.id.misEventos);
        ImageView agregarbtn = (ImageView) this.findViewById(R.id.btnagregarimg);
        selectEvento.setTypeface(CustomFontsLoader.getTypeface(this,CustomFontsLoader.Bold));
        misEventos.setTypeface(CustomFontsLoader.getTypeface(this,CustomFontsLoader.Light));

        new AsyncEventos().execute("evento");

        eventContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                editarDatosPersistentes.putString("ideventoThe3v3nt", ideventos[position]);
                editarDatosPersistentes.apply();
                Intent intent = new Intent(Evento.this, Timeline.class);
                startActivity(intent);
            }

        });

//        agregarbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Evento.this, Timeline.class);
//                startActivity(intent);
//            }
//
//        });
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream input = connection.getInputStream();
            options.inSampleSize = calculateInSampleSize(options, 50, 50);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(input,null,options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject obtenerEventos() {
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        id = datosPersistentes.getString("idusrThe3v3nt","");
        Log.v("ESTE ES EL IDUSUARIO",id);
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=eventos&idusuario=" + id);

            if (respuesta.getString("success").equals("OK")) {

                JSONArray eventos = respuesta.getJSONArray("eventos");
                nombres = new String[eventos.length()];
                lugares = new String[eventos.length()];
                descripciones = new String[eventos.length()];
                fechas = new String[eventos.length()];
                ideventos = new String[eventos.length()];
                imagenes.clear();

                int i = 0;

                while (i < eventos.length()) {
                    JSONObject evento = eventos.getJSONObject(i);
                    nombres[i] = evento.getString("nombre");
                    lugares[i] = evento.getString("lugar");
                    descripciones[i] = evento.getString("descripcion");
                    fechas[i] = evento.getString("fecha");
                    ideventos[i] = evento.getString("idevento");
                    String remotePath = "http://theevent.com.mx/imagenes/eventos/" + evento.getString("imagen");
                    Bitmap myBitMap = getBitmapFromURL(remotePath);
                    imagenes.add(myBitMap);
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
            eventContainer = (ListView) findViewById(R.id.eventContainer);
            pDialog = new ProgressDialog(Evento.this);
            // Set progressbar message
            pDialog.setMessage("Cargando Eventos...");
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
                    case "evento":
                        res = obtenerEventos();
                        return res.getString("success");
                    case "eventimg":
                        String remotePath = params[1];
                        return "OK";
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
                AdapterEventos adapter = new AdapterEventos(Evento.this, nombres, lugares, descripciones, fechas, imagenes);
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
    public void onBackPressed(){
        Intent Intent = new Intent(getApplicationContext(), Login.class);
        startActivity(Intent);
    }

    }

