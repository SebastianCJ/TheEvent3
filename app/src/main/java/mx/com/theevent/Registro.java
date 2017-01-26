package mx.com.theevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class Registro extends AppCompatActivity implements View.OnKeyListener{
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
    private EditText correoRegistro;
    private String correo;
    private String username;
    private String nombre;
    String enteredUsername;
    String enteredPassword;
    String enteredCorreo;
    private TextView yatienescuenta;
    private Long userID;
    private String UserName;
    String name;
    String lastname;
    JSONObject res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        usuario = (EditText) this.findViewById(R.id.nombreR);
        contrasena = (EditText) this.findViewById(R.id.passwordTxtR);
        correoRegistro = (EditText) this.findViewById(R.id.emailTxtR);
        yatienescuenta = (TextView) this.findViewById(R.id.yatienesCuentaR);
        contrasena.setOnKeyListener(this);

        usuario.setTypeface(CustomFontsLoader.getTypeface(this,CustomFontsLoader.Light));
        contrasena.setTypeface(CustomFontsLoader.getTypeface(this,CustomFontsLoader.Light));
        correoRegistro.setTypeface(CustomFontsLoader.getTypeface(this,CustomFontsLoader.Light));

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
                                            nombre = object.getString("name");
                                            String[] parts = nombre.split("\\s+");
                                            name = parts[0];
                                            lastname = parts[1];
                                            new AsyncRegistro().execute("fb");
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
                        Toast.makeText(Registro.this, "Registro Cancelado", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(Registro.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

        yatienescuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(getApplicationContext(), Login.class);
                startActivity(Intent);
            }

        });

        ImageView btonFB = (ImageView)findViewById(R.id.btn_FbR);

        btonFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Registro.this, Arrays.asList("public_profile","email"));
            }

        });
        ImageView btn_Twitter = (ImageView) findViewById(R.id.btn_TwitterR);

        mTwitterAuthClient= new TwitterAuthClient();
        btn_Twitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mTwitterAuthClient.authorize(Registro.this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        session = twitterSessionResult.data;

                        username = session.getUserName();
                        userID = session.getUserId();
                        System.out.println("USERIDTWIT: " + userID);

                        Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false).enqueue(new Callback<User>()
                        {
                            @Override
                            public void success(Result<User> userResult)
                            {
                                try
                                {
                                    User user = userResult.data;
                                    UserName = user.name;
                                    new AsyncRegistro().execute("twit");
                                } catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failure(TwitterException e)
                            {

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

    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {

        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            if (!event.isShiftPressed()) {
                Log.v("AndroidEnterKeyActivity","Enter Key Pressed!");
                String enteredUsername = usuario.getText().toString();
                String enteredPassword = contrasena.getText().toString();
                String enteredCorreo = correoRegistro.getText().toString();

                if (enteredUsername.equals("") || enteredPassword.equals("") || enteredCorreo.equals("") ) {
                    Toast.makeText(Registro.this, "No pueden existir campos vacios", Toast.LENGTH_LONG).show();
                    return true;
                }

                if (enteredUsername.length() <= 1 || enteredPassword.length() <= 1 || enteredCorreo.length() <= 1) {
                    Toast.makeText(Registro.this, "La longitud debe ser mayor a 1.", Toast.LENGTH_LONG).show();
                    return true;
                }

                //autentificacion con el servidor remoto
                new AsyncRegistro().execute("login");

                return true;
            }

        }
        return false; // pass on to other listeners.

    }

    private JSONObject registrar(String idfb,String idtwit,String nombre,String email, String pass){
        JSONData conexion = new JSONData();
        JSONObject respuesta = null;

        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=registro&idfb=" + idfb + "&nombre=" + nombre + "&idtwit=" + idtwit  + "&correo=" + email + "&pass=" + pass);
            Log.d("Url: ","action=registro&idfb="+ idfb + "&idtwit=" + idtwit + "&nombre="+ nombre + "&correo=" + email + "&pass=" + pass);
            if (respuesta.getString("success").equals("OK")) {
                Log.d("Registro","OK");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public class AsyncRegistro extends AsyncTask<String, String, String> {

        public AsyncRegistro() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            enteredUsername = usuario.getText().toString();
            enteredPassword = contrasena.getText().toString();
            enteredCorreo = correoRegistro.getText().toString();
        }


        @Override
        protected String doInBackground(String... params) {

            //String urlString = params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in = null;
            try {
                switch (params[0]) {
                    case "login":
                        res = registrar(null, null, enteredUsername, enteredCorreo, enteredPassword);
                        return res.getString("success");
                    case "fb":
                        res = registrar(id, null, name, correo, "1");
                        return res.getString("success");
                    case "twit":
                        String idTwit = userID.toString();
                        res = registrar(null, idTwit, UserName, null, "1");
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
            if (result.equals("SI")) {
                Toast.makeText(Registro.this, "Tu Cuenta Ya Esta Registrada", Toast.LENGTH_LONG).show();
            } else {
                if (result.equals("OK")) {
                    Toast.makeText(Registro.this, "Registro Exitoso", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                } else {
                    Toast.makeText(Registro.this, result, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed(){
        Intent Intent = new Intent(getApplicationContext(), Login.class);
        startActivity(Intent);
    }

}
