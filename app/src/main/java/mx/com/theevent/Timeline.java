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
    private String id;
    JSONObject res;
    private int flag;
    final String[] data ={"Mis Eventos","Mi Información","Notificaciones","Cerrar Sesión",};
    ListView eventContainer;
    ArrayList<Bitmap> imagenes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.timelinemenu, data);

        final ImageView btnDrawerTimeline = (ImageView) findViewById(R.id.btnmenuOpen);
        final Button timelineTitulo = (Button) findViewById(R.id.btnDrawerTimeline);
        final ImageView btnDrawerTimelineClose = (ImageView) findViewById(R.id.btnmenuClose);
        final CircularImageView fotoPerfil = (CircularImageView) findViewById(R.id.fotoEncabezado);
        final ImageView btnBuscar = (ImageView) findViewById(R.id.buscarTimeline);
        final EditText buscarTxt = (EditText) findViewById(R.id.buscarTxt);
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
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                segundoboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Timeline.this, Galeria.class);
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
                startActivity(intent);

            }
        });

        iconRuta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Timeline.this, Ubicaciones.class);
                startActivity(intent);

            }
        });

        iconPublicacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Timeline.this);
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

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timelineTitulo.setVisibility(View.INVISIBLE);
                buscarTxt.setVisibility(View.VISIBLE);
                buscarTxt.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(buscarTxt, InputMethodManager.SHOW_IMPLICIT);


            }
        });
        buscarTxt.setOnKeyListener(new EditText.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (!event.isShiftPressed()) {
                        timelineTitulo.setVisibility(View.VISIBLE);
                        buscarTxt.setVisibility(View.INVISIBLE);
                        buscarTxt.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        new AsyncEventos().execute("buscar",buscarTxt.getText().toString());
                        return true;
                    }

                }
                return false; // pass on to other listeners.
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
                        Intent IntentInfo = new Intent(getApplicationContext(), MiInformacion.class);
                        startActivity(IntentInfo);
                        break;
                    case 3:
                        Intent IntentNotificaciones = new Intent(getApplicationContext(), Notificaciones.class);
                        startActivity(IntentNotificaciones);
                        break;

                    case 4:
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
//                iconTimeline.setVisibility(View.GONE);
//                iconCalendario.setVisibility(View.GONE);
//                iconPublicacion.setVisibility(View.GONE);
//                iconRuta.setVisibility(View.GONE);
                btnDrawerTimeline.setVisibility(View.GONE);
                btnDrawerTimelineClose.setVisibility(View.VISIBLE);

                System.out.println("OPEN");
            }
        });

        btnDrawerTimelineClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(navList);
//                iconTimeline.setVisibility(View.VISIBLE);
//                iconCalendario.setVisibility(View.VISIBLE);
//                iconPublicacion.setVisibility(View.VISIBLE);
//                iconRuta.setVisibility(View.VISIBLE);
                btnDrawerTimeline.setVisibility(View.VISIBLE);
                btnDrawerTimelineClose.setVisibility(View.GONE);
                System.out.println("CLOSE");
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
                imagenes.clear();
                imagenesString = new String[posts.length()];

                int i = 0;

                while (i < posts.length()) {
                    JSONObject post = posts.getJSONObject(i);
                    nombres[i] = post.getString("nombre");
                    dias[i] = post.getString("fecha");
                    mensajes[i] = post.getString("post");
                    comentarios[i] = post.getString("numcomentarios");
                    likes[i] = post.getString("numlikes");
                    imagenesString[i] = post.getString("imagen");
                    String remotePath = "http://theevent.com.mx/imagenes/timeline/" + post.getString("imagen");
                    Bitmap myBitMap = getBitmapFromURL(remotePath);
                    imagenes.add(myBitMap);
                    idposts[i] = post.getString("idpost");
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

            String resultToDisplay = "";

            InputStream in = null;
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
                AdapterTimeline adapter = new AdapterTimeline(Timeline.this, nombres, dias, mensajes, imagenes, comentarios, likes, idposts, imagenesString);
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
        Intent Intent = new Intent(getApplicationContext(), Evento.class);
        startActivity(Intent);
    }


}

