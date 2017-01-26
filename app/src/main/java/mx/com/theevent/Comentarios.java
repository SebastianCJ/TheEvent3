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

import com.mikhaellopez.circularimageview.CircularImageView;

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
    private String id;
    JSONObject res;
    final String[] data ={"Mis Eventos","Mi Información","Notificaciones","Cerrar Sesión",};
    ListView eventContainer;
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
        final CircularImageView fotoPerfil = (CircularImageView) findViewById(R.id.fotoEncabezado);
        final ImageView iconTimeline = (ImageView) findViewById(R.id.btntimeline);
        final ImageView iconCalendario = (ImageView) findViewById(R.id.btncalendario);
        final ImageView iconPublicacion = (ImageView) findViewById(R.id.btncentro);
        final ImageView iconRuta = (ImageView) findViewById(R.id.btnruta);
        final ImageView btnmegusta = (ImageView) findViewById(R.id.btnmegusta);
        final ImageView btncomentario = (ImageView) findViewById(R.id.btncomentario);


        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput("myImage"));
            fotoPerfil.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        obtenerPost();

        iconTimeline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Comentarios.this, Timeline.class);
                startActivity(intent);

            }
        });

        iconCalendario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Comentarios.this, Calendario.class);
                startActivity(intent);

            }
        });
//
//        iconRuta.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(Timeline.this, Ruta.class);
//                startActivity(intent);
//
//            }
//        });
//
        iconPublicacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Comentarios.this);
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

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Comentarios.this, Camara.class);
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
                        Intent Intent = new Intent(getApplicationContext(), Evento.class);
                        startActivity(Intent);
                        break;
                    case 2:
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
                                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                                editarDatosPersistentes.putString("usrThe3v3nt", "");
                                editarDatosPersistentes.putString("passThe3v3nt", "");
                                editarDatosPersistentes.apply();
                                Intent cerrarSesion = new Intent(getApplicationContext(), Login.class);
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
                //imagenes.clear();
                //imagenesString = new String[posts.length()];

                int i = 0;

                while (i < posts.length()) {
                    JSONObject post = posts.getJSONObject(i);
                    comentarios[i] = post.getString("comentario");
                    nombres[i] = post.getString("nombre");
                    fechas[i] = post.getString("fecha");
                    //imagenesString[i] = post.getString("imagen");
                    //String remotePath = "http://theevent.com.mx/imagenes/timeline/" + post.getString("imagen");
                    //Bitmap myBitMap = getBitmapFromURL(remotePath);
                    //imagenes.add(myBitMap);
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

        TextView namePost = (TextView) findViewById(R.id.nombrePer);
        TextView fecha = (TextView) findViewById(R.id.diatxt);
        TextView mensaje = (TextView) findViewById(R.id.mensajeTime);
        TextView numcomentarios = (TextView) findViewById(R.id.numComentarios);
        TextView numlikes = (TextView) findViewById(R.id.burbuja);

        String nombrePost = datosPersistentes.getString("nombrePost", "");
        String mensajePost = datosPersistentes.getString("mensajePost", "");
        String numlikesPost = datosPersistentes.getString("numlikesPost", "");
        String numcomentariosPost = datosPersistentes.getString("numcomentariosPost", "");
        String fechaPost = datosPersistentes.getString("fechaPost", "");
        String dia;
        String numComentarios = numcomentariosPost + " comentarios";
        String numLikes = "   +" + numlikesPost + "    ";
        new AsyncImagenes().execute("background");

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

    public class AsyncImagenes extends AsyncTask<String, String, String> {

        public AsyncImagenes() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventContainer = (ListView) findViewById(R.id.eventContainerTimeline);
            pDialog = new ProgressDialog(Comentarios.this);
            // Set progressbar message
            pDialog.setMessage("Cargando Post...");
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
                    case "background":
                        String background = datosPersistentes.getString("backgroundPost", "");
                        String remotePath = "http://theevent.com.mx/imagenes/timeline/" + background;
                        Bitmap myBitMap = getBitmapFromURL(remotePath);
                        backgroundArray.add(myBitMap);
                        return "success";
                    case "eventimg":

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
            Drawable dr = new BitmapDrawable(backgroundArray.get(0));
            RelativeLayout postHead = (RelativeLayout) findViewById(R.id.timelineHead);
            postHead.setBackgroundDrawable(dr);
            new AsyncComentarios().execute("comentarios");
        }
    }

    public class AsyncComentarios extends AsyncTask<String, String, String> {

        public AsyncComentarios() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            }
            else{
                final TextView comentarioNull = (TextView) findViewById(R.id.comentariosNull);
                comentarioNull.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed(){
        Intent Intent = new Intent(getApplicationContext(), Timeline.class);
        startActivity(Intent);
    }
}
