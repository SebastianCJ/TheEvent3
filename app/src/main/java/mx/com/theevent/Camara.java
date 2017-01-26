package mx.com.theevent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.FrameLayout;
import android.hardware.Camera;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Camara extends Activity {
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String fileName;
    String upLoadServerUri = null;
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    SharedPreferences datosPersistentes;
    int serverResponseCode = 0;
    private JSONObject res;
    private File mFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setContentView(R.layout.activity_arcamera);
            dispatchTakePictureIntent();

//            mCamera = getCameraInstance();
//            mCameraPreview = new CameraPreview(this, mCamera);
//            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
//            preview.addView(mCameraPreview);
        }
        else {

            if (null == savedInstanceState) {
                setContentView(R.layout.activity_camera);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, Camera2BasicFragment.newInstance())
                        .commit();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("REQUEST: " + requestCode);
        System.out.println("RESULT: " + resultCode);
        if (resultCode == 0){
            Intent Intent = new Intent(getApplicationContext(), Timeline.class);
            startActivity(Intent);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            createImageFromBitmap(imageBitmap);
            datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
            String id = datosPersistentes.getString("idusrThe3v3nt","");
            String nombre = datosPersistentes.getString("nombreThe3v3nt","");
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"TheEvent");
            mFile = new File(path, id+nombre+"_pic.jpg");
            fileName = id+nombre+"_pic.jpg";
            Croperino.runCropImage(mFile, Camara.this, true, 1, 1, 0, 0);
            scanMedia(mFile.toString());

            new Thread(new Runnable() {
                public void run() {
//                            File file = new File(path);
//                            Uri uri = Uri.fromFile(file);
                    upLoadServerUri = "http://theevent.com.mx/imagenes/upload.php";
                    decodeFile(mFile);
                    uploadFile(mFile.toString());

                    new AsyncImagenPerfil().execute("guardar");

                }
            }).start();
            Intent intent = new Intent(Camara.this, Timeline.class);
            startActivity(intent);
        }
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    private void scanMedia(String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        Camara.this.sendBroadcast(scanFileIntent);
    }

    // Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=70;

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

    public JSONObject guardarImagenPerfil(){
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
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
            Intent intent = new Intent(Camara.this, Timeline.class);
            startActivity(intent);
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

    /**
     * Helper method to access the camera returns null if it cannot get the
     * camera or does not exist
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

}