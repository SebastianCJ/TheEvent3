package mx.com.theevent;

/**
 * Created by Gatsu on 12/2/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RequestDeeplink;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;


public class AdapterCalendario extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] descripcion;
    private final String[] dias;
    private final String[] meses;
    private final String[] horas;
    private final String[] years;
    private final String[] lugares;
    private final String[] latitudes;
    private final String[] longitudes;


    static class ViewHolder {
        public TextView descripcionTxt;
        public TextView mesTxt;
        public TextView diaTxt;
        public TextView horaTxt;
        public TextView yearTxt;
        public TextView lugarTxt;
        public ImageView vertblack;
        public ImageView ruta;
        public Button comollegarTxt;
        public Button uberbtn;


    }

    public AdapterCalendario(Activity context, String[] descripcion, String[] dias, String[] meses, String[] horas, String[] years, String[] lugares, String[] latitudes, String[] longitudes) {
        super(context, R.layout.activity_comentarios, descripcion);
        this.context = context;
        this.descripcion = descripcion;
        this.dias = dias;
        this.meses = meses;
        this.horas = horas;
        this.years = years;
        this.lugares = lugares;
        this.longitudes = longitudes;
        this.latitudes = latitudes;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.calendario_layout, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.descripcionTxt = (TextView) rowView.findViewById(R.id.nombrePerfilComentario);
            viewHolder.diaTxt = (TextView) rowView.findViewById(R.id.diaTxt);
            viewHolder.mesTxt = (TextView) rowView.findViewById(R.id.mesTxt);
            viewHolder.horaTxt = (TextView) rowView.findViewById(R.id.horaTxt);
            viewHolder.vertblack = (ImageView) rowView.findViewById(R.id.vertblack);
            viewHolder.yearTxt = (TextView) rowView.findViewById(R.id.yearTxt);
            viewHolder.lugarTxt = (TextView) rowView.findViewById(R.id.lugarTxt);
            viewHolder.ruta = (ImageView) rowView.findViewById(R.id.ruta);
            viewHolder.comollegarTxt = (Button) rowView.findViewById(R.id.btncomollegar);
            viewHolder.uberbtn = (Button) rowView.findViewById(R.id.btnuber);
            rowView.setTag(viewHolder);
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        String dia = dias[position] + " de ";
        String nombre = descripcion[position];
        String mes = meses[position] + " de ";
        String hora = horas[position];
        String year = years[position];
        String lugar = lugares[position];
        String latitud = latitudes[position];
        String longitud = longitudes[position];
        if (!latitud.equals("") && !longitud.equals("")){
            holder.lugarTxt.setText(lugar);
            double latitudD = Double.parseDouble(latitud);
            double longitudD = Double.parseDouble(longitud);
            final RideParameters rideParams = new RideParameters.Builder()
                    // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                    .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                    // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                    .setDropoffLocation(latitudD, longitudD, "Mi Ubicacion", " ")
                    // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                    // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location.
                    .setDropoffLocation(latitudD, longitudD, " ", lugar)
                    .build();

            holder.uberbtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    requestRide(context,rideParams);
                }
            });
        }

        holder.descripcionTxt.setText(nombre);
        holder.diaTxt.setText(dia);
        holder.mesTxt.setText(mes);
        holder.horaTxt.setText(hora);
        holder.yearTxt.setText(year);

        if (lugar.equals("")){
            holder.lugarTxt.setVisibility(View.GONE);
            holder.ruta.setVisibility(View.GONE);
            holder.comollegarTxt.setVisibility(View.GONE);
            holder.uberbtn.setVisibility(View.GONE);
        }



        holder.comollegarTxt.setOnClickListener(new View.OnClickListener() {
            String latitud = latitudes[position];
            String longitud = longitudes[position];
            String uri = "http://maps.google.com/maps?daddr=" + latitud + "," + longitud;

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri));
                getContext().startActivity(intent);
            }
        });


        if(position==getCount() - 1){
            holder.vertblack.setVisibility(View.VISIBLE);
        }
        return rowView;
    }

}
