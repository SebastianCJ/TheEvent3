package mx.com.theevent;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.messaging.FirebaseMessaging;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;

public class Login extends AppCompatActivity  {
    private CallbackManager callbackManager;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "9Qs2Ygm3kvP15XJNJGWKJ5Glp";
    private static final String TWITTER_SECRET = "hWkAQSdw2xwKVsiyljPRgBSb25ugcMRy6yeGUjuVvNMkkvw4d5";
    TwitterAuthClient mTwitterAuthClient;
    TwitterSession session;
    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    private Button btnIniciarSesion;
    private EditText usuario;
    private EditText contrasena;
    public SharedPreferences datosPersistentes;
    public JSONObject respuesta;
    private String id;
    private String correo;
    private String username;
    private Long userID;
    private String UserName;
    JSONObject res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        TextView recuperarContra = (TextView) findViewById(R.id.olvidasteLogin);
        TextView registrarse = (TextView) findViewById(R.id.registrateLogin);
        Button btnlogin = (Button) findViewById(R.id.btniniciar);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), Registro.class);
                startActivity(Intent);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = usuario.getText().toString();
                String enteredPassword = contrasena.getText().toString();

                if (enteredUsername.equals("") || enteredPassword.equals("")) {
                    Toast.makeText(Login.this, "No pueden existir campos vacios", Toast.LENGTH_LONG).show();
                }

                if (enteredUsername.length() <= 1 || enteredPassword.length() <= 1) {
                    Toast.makeText(Login.this, "La longitud debe ser mayor a 1.", Toast.LENGTH_LONG).show();
                }

                //autentificacion con el servidor remoto
                new AsyncLogin().execute("login");
            }
        });

        usuario = (EditText) this.findViewById(R.id.emailTxt);
        contrasena = (EditText) this.findViewById(R.id.passwordTxt);
        contrasena.setOnKeyListener(new EditText.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (!event.isShiftPressed()) {
                        Log.v("AndroidEnterKeyActivity","Enter Key Pressed!");
                        String enteredUsername = usuario.getText().toString();
                        String enteredPassword = contrasena.getText().toString();

                        if (enteredUsername.equals("") || enteredPassword.equals("")) {
                            Toast.makeText(Login.this, "No pueden existir campos vacios", Toast.LENGTH_LONG).show();
                            return true;
                        }

                        if (enteredUsername.length() <= 1 || enteredPassword.length() <= 1) {
                            Toast.makeText(Login.this, "La longitud debe ser mayor a 1.", Toast.LENGTH_LONG).show();
                            return true;
                        }

                        //autentificacion con el servidor remoto
                        new AsyncLogin().execute("login");

                        return true;
                    }

                }
                return false; // pass on to other listeners.
            }
        });
        recuperarContra.setTypeface(CustomFontsLoader.getTypeface(this, CustomFontsLoader.Light));
        registrarse.setTypeface(CustomFontsLoader.getTypeface(this, CustomFontsLoader.Light));

        usuario.setTypeface(CustomFontsLoader.getTypeface(this, CustomFontsLoader.Light));
        contrasena.setTypeface(CustomFontsLoader.getTypeface(this, CustomFontsLoader.Light));


        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("MENSAJE: ", "Key: " + key + " Value: " + value);
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("news");
//        // [END subscribe_topics]
//
//        // Log and toast
        String msg = getString(R.string.msg_subscribed);
        Log.d("TEMA: ", msg);

        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        String msg2 = getString(R.string.msg_token_fmt, token);
        Log.d("TOKEN: ", msg2);


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Log.d("Success", "Login");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("Login Response: ", response.toString());

                                        // Application code
                                        try {
                                            id = object.getString("id");
                                            correo = object.getString("email");
                                            new AsyncLogin().execute("fb");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(Login.this, "Inicio de Sesion Cancelado", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(Login.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

        ImageView btonFB = (ImageView) findViewById(R.id.btn_Fb);


        btonFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile", "email"));
            }

        });
        ImageView btn_Twitter = (ImageView) findViewById(R.id.btn_Twitter);

        mTwitterAuthClient = new TwitterAuthClient();
        btn_Twitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mTwitterAuthClient.authorize(Login.this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        session = twitterSessionResult.data;

                        username = session.getUserName();
                        userID = session.getUserId();
                        System.out.println("USERIDTWIT: " + userID);

                        Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false).enqueue(new Callback<User>() {
                            @Override
                            public void success(Result<User> userResult) {
                                try {
                                    User user = userResult.data;
                                    UserName = user.name;
                                    new AsyncLogin().execute("twit");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failure(TwitterException e) {

                            }

                        });


                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });

            }
        });

        //vemos si tenemos guardado nombre de usuario y/o contraseña, en caso de que esten guardados, los mostramos
        datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String usuarioguardado = datosPersistentes.getString("usrThe3v3nt", "");
        String passguardado = datosPersistentes.getString("passThe3v3nt", "");

        //si ya hay datos guardados, se muestran en pantalla
        if (usuarioguardado.length() > 0 && passguardado.length() > 0) {
            usuario.setText(usuarioguardado);
            contrasena.setText(passguardado);
        }
    }

    private JSONObject conectar(){
        String enteredUsername = usuario.getText().toString();
        String enteredPassword = contrasena.getText().toString();
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;

        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=login&usuario=" + enteredUsername + "&pass=" + enteredPassword);
            Log.d("url: ","action=login&usuario=" + enteredUsername + "&pass=" + enteredPassword);
            if (respuesta.getString("success").equals("OK")) {
                String remotePath;
                //se guardan los datos de manera persistente.
                if (respuesta.getString("imagen").equals("vacio")) {
                    remotePath = "http://theevent.com.mx/imagenes/usuarios/default.png";
                }
                else{
                    remotePath = "http://theevent.com.mx/imagenes/usuarios/" + respuesta.getString("imagen");
                }
                Bitmap myBitMap = getBitmapFromURL(remotePath);
                createImageFromBitmap(myBitMap);
                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                editarDatosPersistentes.putString("usrThe3v3nt", enteredUsername);
                editarDatosPersistentes.putString("passThe3v3nt", enteredPassword);
                editarDatosPersistentes.putString("idusrThe3v3nt", respuesta.getString("idusuario"));
                editarDatosPersistentes.putString("nombreThe3v3nt",respuesta.getString("nombre"));
                editarDatosPersistentes.putString("apaternoThe3v3nt", respuesta.getString("apaterno"));
                editarDatosPersistentes.putString("amaternoThe3v3nt",respuesta.getString("amaterno"));
                editarDatosPersistentes.putString("telefonoThe3v3nt", respuesta.getString("telefono"));
                editarDatosPersistentes.putString("correoThe3v3nt",respuesta.getString("correo"));
                editarDatosPersistentes.apply();

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public JSONObject conectarFB(){
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;
        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=loginfb&idfb=" + id);
            Log.d("url: ","action=loginfb&idfb=" + id);
            System.out.println(respuesta.getString("success"));
            if (respuesta.getString("success").equals("OK")) {

                String remotePath;
                //se guardan los datos de manera persistente.
                if (respuesta.getString("imagen").equals("vacio")) {
                    remotePath = "http://theevent.com.mx/imagenes/usuarios/default";
                }
                else{
                    remotePath = "http://theevent.com.mx/imagenes/usuarios/" + respuesta.getString("imagen");
                }
                Bitmap myBitMap = getBitmapFromURL(remotePath);
                createImageFromBitmap(myBitMap);
                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                editarDatosPersistentes.putString("idusrThe3v3nt", respuesta.getString("idfb"));
                editarDatosPersistentes.putString("nombreThe3v3nt",respuesta.getString("nombre"));
                editarDatosPersistentes.putString("apaternoThe3v3nt", respuesta.getString("apaterno"));
                editarDatosPersistentes.putString("amaternoThe3v3nt",respuesta.getString("amaterno"));
                editarDatosPersistentes.putString("telefonoThe3v3nt", respuesta.getString("telefono"));
                editarDatosPersistentes.putString("correoThe3v3nt",respuesta.getString("correo"));
                editarDatosPersistentes.apply();

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public JSONObject conectarTwit(){
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;

        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=logintwit&id=" + userID);
            Log.d("url: ","action=logintwit&id=" + userID);
            if (respuesta.getString("success").equals("OK")) {
                String remotePath;
                //se guardan los datos de manera persistente.
                if (respuesta.getString("imagen").equals("vacio")) {
                    remotePath = "http://theevent.com.mx/imagenes/usuarios/default";
                }
                else{
                    remotePath = "http://theevent.com.mx/imagenes/usuarios/" + respuesta.getString("imagen");
                }
                Bitmap myBitMap = getBitmapFromURL(remotePath);
                createImageFromBitmap(myBitMap);
                SharedPreferences.Editor editarDatosPersistentes = datosPersistentes.edit();
                editarDatosPersistentes.putString("usrThe3v3nt", UserName);
                editarDatosPersistentes.putString("idusrThe3v3nt", respuesta.getString("id"));
                editarDatosPersistentes.putString("nombreThe3v3nt",respuesta.getString("nombre"));
                editarDatosPersistentes.putString("apaternoThe3v3nt", respuesta.getString("apaterno"));
                editarDatosPersistentes.putString("amaternoThe3v3nt",respuesta.getString("amaterno"));
                editarDatosPersistentes.putString("telefonoThe3v3nt", respuesta.getString("telefono"));
                editarDatosPersistentes.putString("correoThe3v3nt",respuesta.getString("correo"));
                editarDatosPersistentes.apply();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
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

    public class AsyncLogin extends AsyncTask<String, String, String> {

            public AsyncLogin() {
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
                        case "login":
                            res = conectar();
                            return res.getString("success");
                        case "fb":
                            res = conectarFB();
                            return res.getString("success");
                        case "twit":
                            res = conectarTwit();
                            return res.getString("success");
                    }

                } catch (Exception e) {

                    System.out.println(e.getMessage());

                    return e.getMessage();

                }

//            try {
//                resultToDisplay = IOUtils.toString(in, "UTF-8");
//                //to [convert][1] byte stream to a string
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
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
            if (result.equals("OK")) {
                Intent intent = new Intent(Login.this, Evento.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(Login.this, result, Toast.LENGTH_LONG).show();
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
