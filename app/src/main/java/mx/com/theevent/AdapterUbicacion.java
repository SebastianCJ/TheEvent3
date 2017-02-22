package mx.com.theevent;

/**
 * Created by Gatsu on 12/2/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RequestDeeplink;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestActivityBehavior;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.ArrayList;
import java.util.Arrays;


public class AdapterUbicacion extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] nombres;
    private final String[] descripciones;
    private final String[] rangoprecio;
    private final String[] direccion;
    private final String[] titulos;
    private final String[] latitudes;
    private final String[] longitudes;
    private final String[] imagenes;
    private final String[] urls;
    public SharedPreferences datosPersistentes;

    static class ViewHolder {
        public TextView nombreTxt;
        public TextView tituloCat;
        public TextView descripcionTxt;
        public TextView direccionTxt;
        public TextView urlTxt;
        public ImageView vertblack;
        public ImageView image;
        public Button comollegarTxt;
        public Button uberbtn;
        public RelativeLayout layout;


    }

    public AdapterUbicacion(Activity context, String[] nombres, String[] descripciones, String[] rangoprecio, String[] direccion,String[] titulos, String[] imagenes, String[] latitudes, String[] longitudes,String[] urls) {
        super(context, R.layout.activity_ubicaciones, nombres);
        this.context = context;
        this.nombres = nombres;
        this.descripciones = descripciones;
        this.rangoprecio = rangoprecio;
        this.direccion = direccion;
        this.titulos = titulos;
        this.imagenes = imagenes;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        this.urls = urls;

    }

    public void requestRide(@NonNull Context context, @NonNull RideParameters params) {
        SessionConfiguration config = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("4wPUxs5Y2jfiI09e8dGvJ0D6uKgK6TOY")
                // required for enhanced button features
                .setServerToken("UnQu3sTKX8Ww6JRfBr8RHGxNtJy-_ViV0_WVZcG9")
                // required for implicit grant authentication
                .setRedirectUri("https://www.distro.com.mx")
                // required scope for Ride Request Widget features
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                // optional: set Sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();
        UberSdk.initialize(config);
        RequestDeeplink deeplink = new RequestDeeplink.Builder(context)
                .setSessionConfiguration(config)
                .setRideParameters(params).build();
        deeplink.execute();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        final int posicion = position;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.ubicacion_layout, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tituloCat = (TextView) rowView.findViewById(R.id.tituloCat);
            viewHolder.nombreTxt = (TextView) rowView.findViewById(R.id.nombrePerfilComentario);
            viewHolder.descripcionTxt = (TextView) rowView.findViewById(R.id.descripcionTxt);
            //viewHolder.rangoprecioTxt = (TextView) rowView.findViewById(R.id.rangoprecioTxt);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.myImageView);
            viewHolder.direccionTxt = (TextView) rowView.findViewById(R.id.direccionTxt);
            viewHolder.comollegarTxt = (Button) rowView.findViewById(R.id.btncomollegar);
            viewHolder.uberbtn = (Button) rowView.findViewById(R.id.btnuber);
            viewHolder.vertblack = (ImageView) rowView.findViewById(R.id.vertblack);
            viewHolder.urlTxt = (TextView) rowView.findViewById(R.id.urlTxt);
            viewHolder.layout = (RelativeLayout) rowView.findViewById(R.id.desclayoutComentario);

            rowView.setTag(viewHolder);
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        double latitudD = Double.parseDouble(latitudes[posicion]);
        double longitudD = Double.parseDouble(longitudes[posicion]);

        final RideParameters rideParams = new RideParameters.Builder()
                // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                .setDropoffLocation(latitudD, longitudD, "Mi Ubicacion", " ")
                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location.
                .setDropoffLocation(latitudD, longitudD, " ", direccion[posicion])
                .build();
        // set parameters for the RideRequestButton instance
        //requestButton.setRideParameters(rideParams);

        holder.comollegarTxt.setOnClickListener(new View.OnClickListener() {
            String latitud = latitudes[posicion];
            String longitud = longitudes[posicion];
            String uri = "http://maps.google.com/maps?daddr=" + latitud + "," + longitud;

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri));
                getContext().startActivity(intent);
            }
        });

        holder.uberbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestRide(context,rideParams);
            }
        });


        String dia = descripciones[position];
        String nombre = nombres[position];
        //String precio = rangoprecio[position];
        String hora = direccion[position];
        if (imagenes.length >0) {
            String imagen = imagenes[position];
            Picasso.with(context).load(imagen).into(holder.image);
        }
        if(urls.length > 0 && urls[position]!= null) {
            String url = urls[position];
            holder.urlTxt.setText(url);
            holder.urlTxt.setMovementMethod(LinkMovementMethod.getInstance());
        }
        holder.nombreTxt.setText(nombre);
        holder.descripcionTxt.setText(dia);
        // holder.rangoprecioTxt.setText(mes);
        holder.direccionTxt.setText(hora);

        return rowView;
    }

}
