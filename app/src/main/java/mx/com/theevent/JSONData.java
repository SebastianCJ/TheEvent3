package mx.com.theevent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by alfredotoquero on 20/05/16.
 */
public class JSONData{
    protected JSONObject conexion (String myurl, String parametros) throws IOException  {
        InputStream is = null;
        int length = 2048*200;
        int len = 2048*2000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15*1000);
            conn.setConnectTimeout(15*1000);
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            if(!parametros.equals("")){
                //se sacan todos los parametros que vienen en forma de cadena
                String dataPost = "";
                int cont = 0;
                String[] parameters = parametros.split("&");
                for(String p : parameters){
                    if(cont>0){
                        dataPost += "&";
                    }
                    String[] parametro = p.split("=");
                    dataPost += parametro[0] + "=" + URLEncoder.encode(parametro[1], "UTF-8");
                    cont++;
                }

                // Tamaño previamente conocido
                conn.setFixedLengthStreamingMode(dataPost.getBytes().length);

                len = dataPost.getBytes().length;
                // Establecer application/x-www-form-urlencoded debido a la simplicidad de los datos
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.setRequestProperty("Accept-Encoding", "" );
                OutputStream out = new BufferedOutputStream(conn.getOutputStream());

                out.write(dataPost.getBytes());
                out.flush();
                out.close();
            }

            conn.connect();

            try{
                int response = conn.getResponseCode();
                System.out.println(response);
            }
            catch(EOFException e){
                conexionServidor(myurl, parametros);
            }
//            int response = conn.getResponseCode();

            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = convertInputStreamToString(is, len);
            System.out.println("CONTENTASTRING"+contentAsString);
            JSONObject respuestaJSON = null;


            try {
                //se devuelve el valor en JSON
                respuestaJSON = new JSONObject(contentAsString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return respuestaJSON;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    protected JSONObject conexionServidor(String myurl, String parametros) throws IOException {
        JSONObject respuesta = null;
        try {
            respuesta = conexion(myurl, parametros);
            System.out.println(respuesta);
        }
        catch(EOFException e)
        {
            respuesta = conexion(myurl, parametros);
        }
        return respuesta;
    }



    protected String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        int nextCharacter;
        String responseData = "";
        while(true){ // Infinite loop, can only be stopped by a "break" statement
            nextCharacter = reader.read(); // read() without parameters returns one character
            if(nextCharacter == -1) // A return value of -1 means that we reached the end
                break;
            responseData += (char) nextCharacter; // The += operator appends the character to the end of the string
        }
        return responseData;
    }

}


