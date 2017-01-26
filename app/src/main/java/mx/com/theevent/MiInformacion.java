package mx.com.theevent;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MiInformacion extends AppCompatActivity {

    private String serverUrl = "http://theevent.com.mx/webservices/th33v3nt.php";
    private EditText nombre;
    private EditText contrasena;
    private EditText correo;
    private EditText apaterno;
    private EditText amaterno;
    private EditText telefono;
    private EditText contrasenaNueva;
    String enteredUsername;
    String enteredPassword;
    String enteredCorreo;
    String enteredTelefono;
    String enteredPasswordNew;
    String enteredAmaterno;
    String enteredApaterno;
    JSONObject res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_informacion);

        ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoEncabezado);
        nombre = (EditText) findViewById(R.id.nombreR);
        contrasena = (EditText) findViewById(R.id.passwordTxtR);
        contrasenaNueva = (EditText) findViewById(R.id.newPass);
        correo = (EditText) findViewById(R.id.emailTxtR);
        telefono = (EditText) findViewById(R.id.telefono);

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getApplicationContext());
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
                        Intent intent = new Intent(MiInformacion.this, Camara.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                segundoboton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MiInformacion.this, Galeria.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput("myImage"));
            fotoPerfil.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Button btonCancelar = (Button) findViewById(R.id.cancelar);

        Button btonGuardar = (Button) findViewById(R.id.guardar);

        SharedPreferences datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String nombreguardado = datosPersistentes.getString("nombreThe3v3nt", "");
        String telefonoguardado = datosPersistentes.getString("telefonoThe3v3nt", "");
        String correoguardado = datosPersistentes.getString("correoThe3v3nt", "");

        nombre.setText(nombreguardado);
        correo.setText(correoguardado);
        telefono.setText(telefonoguardado);

        btonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncMisDatos().execute("guardar");
            }

        });

        btonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(getApplicationContext(), Timeline.class);
                startActivity(Intent);
            }

        });

    }

    private JSONObject guardarDatos(){
        SharedPreferences datosPersistentes = getSharedPreferences("The3v3nt", Context.MODE_PRIVATE);
        String id = datosPersistentes.getString("idusrThe3v3nt", "");
        enteredUsername = nombre.getText().toString();
        enteredCorreo = correo.getText().toString();
        enteredTelefono = telefono.getText().toString();
        enteredPassword = contrasena.getText().toString();
        enteredPasswordNew = contrasenaNueva.getText().toString();
        if(!enteredPassword.equals("")) {
            if (enteredPassword.length() <= 1 || enteredPasswordNew.length() <= 1) {
                Toast.makeText(MiInformacion.this, "La longitud de las contraseñas debe ser mayor a 1.", Toast.LENGTH_LONG).show();
                enteredPasswordNew = enteredPassword;
            }
        }

        JSONData conexion = new JSONData();
        JSONObject respuesta = null;

        try {
            respuesta = conexion.conexionServidor(serverUrl, "action=info&idusuario="+ id + "&nombre=" + enteredUsername + "&correo=" + enteredCorreo + "&telefono=" + enteredTelefono + "&pass=" + enteredPassword + "&passNew=" + enteredPasswordNew);
            Log.d("Url: ","action=info&idusuario=" + id + "&nombre=" + enteredUsername + "&correo=" + enteredCorreo + "&telefono=" + enteredTelefono + "&pass=" + enteredPassword + "&passNew=" + enteredPasswordNew );
            if (respuesta.getString("success").equals("OK")) {
                Log.d("Info","OK");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }


    public class AsyncMisDatos extends AsyncTask<String, String, String> {

        public AsyncMisDatos() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            try {
                switch (params[0]) {
                    case "guardar":
                        res = guardarDatos();
                        return res.getString("success");
                }


            } catch (Exception e) {

                System.out.println(e.getMessage());

                return e.getMessage();

            }

            return "Error";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
                if (result.equals("OK")) {
                    Toast.makeText(MiInformacion.this, "Datos Guardados Exitosamente", Toast.LENGTH_LONG).show();

                }
        }
    }

}
