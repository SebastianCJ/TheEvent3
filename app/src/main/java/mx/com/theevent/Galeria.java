package mx.com.theevent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Galeria extends AppCompatActivity {
    String fileName;
    String upLoadServerUri = null;
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    private JSONObject res;
    int serverResponseCode = 0;
    File Imagen;
    SharedPreferences datosPersistentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CroperinoFileUtil.verifyStoragePermissions(Galeria.this)) {
            Croperino.prepareGallery(Galeria.this);
        }
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
        CroperinoFileUtil.setupDirectory(Galeria.this);

    }

    private void prepareChooser() {
        Croperino.prepareChooser(Galeria.this, "Capture photo...", ContextCompat.getColor(Galeria.this, android.R.color.background_dark));
    }

    private void prepareCamera() {
        Croperino.prepareCamera(Galeria.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("REQUEST: " + requestCode);
        System.out.println("RESULT: " + resultCode);
        if (resultCode == 0){
            Intent Intent = new Intent(getApplicationContext(), Timeline.class);
            startActivity(Intent);
        }
        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {

                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), Galeria.this, true, 1, 1, 0, 0);

                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, Galeria.this);
                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), Galeria.this, true, 1, 1, 0, 0);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    upLoadServerUri = "http://theevent.com.mx/imagenes/upload.php";
                    Uri i = Uri.fromFile(CroperinoFileUtil.getmFileTemp());
                    String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                    Log.d("DIR: ",dir);
                    Imagen = new File(new File(dir), CroperinoFileUtil.getmFileTemp().toString());
                    Log.d("IMAGEN: ",Imagen.toString());
                    String[] separated = Imagen.toString().split("/");
                    fileName = separated[8];
                    Log.d("FILENAME: ", fileName);
                    Log.d("Croperino : ",CroperinoFileUtil.getmFileTemp().toString());
                    scanMedia(Imagen.toString());

                    new Thread(new Runnable() {
                        public void run() {
                            uploadFile(CroperinoFileUtil.getmFileTemp().toString());
                            new AsyncImagenPerfil().execute("guardar");
                        }
                    }).start();

                    super.onBackPressed();
                    //Do saving / uploading of photo method here.
                }
                break;
            default:
                Intent Intent = new Intent(getApplicationContext(), Timeline.class);
                startActivity(Intent);
                break;
        }
    }


    public JSONObject guardarImagenPerfil(){
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String id = datosPersistentes.getString("idusrThe3v3nt","");

        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=imgPerfil&idusuario=" + id + "&img=" + fileName);
            Log.d("url: ","action=imgPerfil&idusuario=" + id + "&img=" + fileName);
            System.out.println(respuesta.getString("success"));
            if (respuesta.getString("success").equals("OK")) {
                System.out.println("SUCCESS IMGPERFIL");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public class AsyncImagenPerfil extends AsyncTask<String, String, String> {

        public AsyncImagenPerfil() {
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
                    case "guardar":
                        res = guardarImagenPerfil();
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
        startActivity(Intent);
    }
}