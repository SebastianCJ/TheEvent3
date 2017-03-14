package mx.com.theevent;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GaleriaPost extends AppCompatActivity {
    String fileName;
    String upLoadServerUri = null;
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    private JSONObject res;
    private ProgressDialog pDialog;
    int serverResponseCode = 0;
    File Imagen;
    String remotePath2;
    int flag = 0;
    SharedPreferences datosPersistentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeriapost);
        flag = 0;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final EditText postTxt = (EditText) findViewById(R.id.postTxt);
        Button btnPublicar = (Button) findViewById(R.id.btnPublicar);
        Button btnPickImg = (Button) findViewById(R.id.btnPickImg);
        Button btnCancelar = (Button) findViewById(R.id.btnCancelar);

        final ImageView fotoPost2 = (ImageView) findViewById(R.id.timelineHead);
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String fileNamePostCamera = datosPersistentes.getString("fotoPostThe3v3nt","");
        remotePath2 = datosPersistentes.getString("fotoPostPathThe3v3nt","");
        System.out.println("REMOTEPATH2: " + remotePath2);
        if (!remotePath2.equals("")) {
            flag = 1;
            Picasso.with(getApplicationContext()).load(new File(remotePath2)).fit().centerCrop().into(fotoPost2);
            System.out.println("PREVIEW " + remotePath2);
        }

        String txtPost = datosPersistentes.getString("txtPostThe3v3nt","");
        if (!txtPost.equals("")) {
            postTxt.setText(txtPost);
        }

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1) {
                    if (!postTxt.getText().toString().equals("")) {
                        new Thread(new Runnable() {
                            public void run() {
                                if (remotePath2.equals("")) {
                                    decodeFile(CroperinoFileUtil.getmFileTemp());
                                    uploadFile(CroperinoFileUtil.getmFileTemp().toString());
                                }
                            }
                        }).start();
                        new AsyncPublicar().execute("guardar", postTxt.getText().toString());
                    }
                    else{
                        Toast.makeText(GaleriaPost.this,"La publicacion no puede estar vacia",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(GaleriaPost.this,"Hace falta agregar una imagen",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnPickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(GaleriaPost.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_layout);

                TextView contenido = (TextView) dialog.findViewById(R.id.txtContenidoAlert);
                Button primerboton = (Button) dialog.findViewById(R.id.btnPrimero);
                Button segundoboton = (Button) dialog.findViewById(R.id.btnSegundo);
                Button tercerboton = (Button) dialog.findViewById(R.id.btnTercero);

                datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                editarDatosPersistentes.putString("txtPostThe3v3nt",postTxt.getText().toString());
                editarDatosPersistentes.apply();

                contenido.setText("Porfavor seleccione una opcion.");
                segundoboton.setText("Galeria");
                primerboton.setText("Camara");
                tercerboton.setVisibility(View.GONE);

                primerboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GaleriaPost.this, CamaraPost.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                segundoboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CroperinoFileUtil.verifyStoragePermissions(GaleriaPost.this)) {
                            Croperino.prepareGallery(GaleriaPost.this);
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), Timeline.class);
                Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                editarDatosPersistentes.putString("fotoPostThe3v3nt","");
                editarDatosPersistentes.putString("fotoPostPathThe3v3nt","");
                editarDatosPersistentes.putString("txtPostThe3v3nt","");
                editarDatosPersistentes.apply();
                startActivity(Intent);
            }
        });



        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"TheEvent"); //Creates app specific folder
        if(!path.exists())//check if file already exists
        {
            try {
                if(path.mkdirs()){
                    Log.d("MKDIR:","Folder The Event Creado");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/TheEvent/Pictures", dir);
        CroperinoFileUtil.setupDirectory(GaleriaPost.this);

    }

    // Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=20;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    private void prepareChooser() {
        Croperino.prepareChooser(GaleriaPost.this, "Capture photo...", ContextCompat.getColor(GaleriaPost.this, android.R.color.background_dark));
    }

    private void prepareCamera() {
        Croperino.prepareCamera(GaleriaPost.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("REQUEST: " + requestCode);
        System.out.println("RESULT: " + resultCode);
        final ImageView fotoPost2 = (ImageView) findViewById(R.id.timelineHead);
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String fileNamePostCamera = datosPersistentes.getString("fotoPostThe3v3nt","");
        String remotePath2 = datosPersistentes.getString("fotoPostPathThe3v3nt","");
        String txtPost = datosPersistentes.getString("txtPostThe3v3nt","");
        System.out.println("REMOTEPATH2: " + remotePath2);
        if (!remotePath2.equals("")) {
            flag = 1;
            Picasso.with(getApplicationContext()).load(new File(remotePath2)).fit().centerCrop().into(fotoPost2);
            System.out.println("PREVIEW " + remotePath2);
        }

        final EditText postTxt = (EditText) findViewById(R.id.postTxt);
        if (!txtPost.equals("")) {
            postTxt.setText(txtPost);
        }

        if (resultCode == 0){
            Intent Intent = new Intent(getApplicationContext(), GaleriaPost.class);
            Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent);
        }
        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {

                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), GaleriaPost.this, true, 1, 1, 0, 0);
                    final ImageView fotoPost = (ImageView) findViewById(R.id.timelineHead);
                    Picasso.with(getApplicationContext()).load(new File(CroperinoFileUtil.getmFileTemp().toString())).fit().centerCrop().into(fotoPost);
                    flag = 1;
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                flag = 1;
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, GaleriaPost.this);
                    final ImageView fotoPost = (ImageView) findViewById(R.id.timelineHead);
                    upLoadServerUri = "http://theevent.com.mx/imagenes/uploadPost.php";
                    Uri i = Uri.fromFile(CroperinoFileUtil.getmFileTemp());
                    String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                    Log.d("DIR: ",dir);
                    Imagen = new File(new File(dir), CroperinoFileUtil.getmFileTemp().toString());
                    Log.d("IMAGEN: ",Imagen.toString());
                    String[] separated = Imagen.toString().split("/");
                    fileName = separated[separated.length - 1];
                    Log.d("FILENAME: ", fileName);
                    Log.d("Croperino : ",CroperinoFileUtil.getmFileTemp().toString());

                    datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                    editarDatosPersistentes.putString("fotoPostThe3v3nt",fileName);
                    editarDatosPersistentes.apply();

                    String remotePath = "http://theevent.com.mx/imagenes/timeline/" + fileName;
                    Log.d("REMOTEPATH : ",remotePath);
                    Picasso.with(getApplicationContext()).load(new File(CroperinoFileUtil.getmFileTemp().toString())).fit().centerCrop().into(fotoPost);

                    scanMedia(Imagen.toString());

                }
                break;

        }
    }

    public JSONObject guardarPost(String post){
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String id = datosPersistentes.getString("idusrThe3v3nt","");
        String idevento = datosPersistentes.getString("ideventoThe3v3nt","");
        String imagen = datosPersistentes.getString("fotoPostThe3v3nt","");
        System.out.println("IMAGEN GUARDARPOST: " + imagen);
        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=imgPost&idusuario=" + id + "&imagen=" + imagen + "&post=" + post + "&idevento=" + idevento);
            Log.d("url: ","action=imgPost&idusuario=" + id + "&imagen=" + imagen + "&post=" + post + "&idevento=" + idevento );
            System.out.println(respuesta.getString("success"));
            if (respuesta.getString("success").equals("OK")) {
                System.out.println("SUCCESS GUARDARPOST");

                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                String remotePath = "http://theevent.com.mx/imagenes/timeline/" + respuesta.getString("imagen");
                editarDatosPersistentes.putString("fotoPostThe3v3nt",remotePath);
                editarDatosPersistentes.apply();

                Picasso.with(getApplicationContext()).invalidate(remotePath);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public class AsyncPublicar extends AsyncTask<String, String, String> {

        public AsyncPublicar() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GaleriaPost.this);
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
                    case "guardar":
                        res = guardarPost(params[1]);
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
            if (result.equals("OK")) {
                Toast.makeText(GaleriaPost.this, "Contenido publicado con éxito", Toast.LENGTH_LONG).show();
                datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                editarDatosPersistentes.putString("fotoPostThe3v3nt","");
                editarDatosPersistentes.putString("fotoPostPathThe3v3nt","");
                editarDatosPersistentes.putString("txtPostThe3v3nt","");
                editarDatosPersistentes.apply();
                Intent Intent = new Intent(getApplicationContext(), Timeline.class);
                Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent);
            }
            else{
                Toast.makeText(GaleriaPost.this, "Intentelo nuevamente", Toast.LENGTH_LONG).show();
                System.out.println("RESULT POSTEXECUTE: " + result);
            }
        }
    }

    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String id = datosPersistentes.getString("idusrThe3v3nt","");
        String nombre = datosPersistentes.getString("nombreThe3v3nt","");

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :"+
                    sourceFileUri);
            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;filename="
                        + fileName + "" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.writeBytes(id+nombre);
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    Log.d("filename",fileName);
                    Log.d("Upload Succes:","File Uploaded");
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {


                ex.printStackTrace();


                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {


                e.printStackTrace();

                Log.e("Exception", "Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }
    private void scanMedia(String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        this.sendBroadcast(scanFileIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CroperinoFileUtil.REQUEST_CAMERA) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.CAMERA)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        prepareCamera();
                    }
                }
            }
        } else if (requestCode == CroperinoFileUtil.REQUEST_EXTERNAL_STORAGE) {
            boolean wasReadGranted = false;
            boolean wasWriteGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        wasReadGranted = true;
                    }
                }
                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        wasWriteGranted = true;
                    }
                }
            }

            if (wasReadGranted && wasWriteGranted) {
                prepareChooser();
            }
        }
    }
    @Override
    public void onBackPressed(){
        Intent Intent = new Intent(getApplicationContext(), Timeline.class);
        Intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
        editarDatosPersistentes.putString("fotoPostThe3v3nt","");
        editarDatosPersistentes.putString("fotoPostPathThe3v3nt","");
        editarDatosPersistentes.putString("txtPostThe3v3nt","");
        editarDatosPersistentes.apply();
        startActivity(Intent);
    }
}