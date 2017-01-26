package mx.com.theevent;

/**
 * Created by Gatsu on 12/2/2016.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AdapterUbicaciones extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] nombres;
    private final String[] descripciones;
    private final String[] rangoprecio;
    private final String[] direccion;
    private final String[] titulos;


    static class ViewHolder {
        public TextView nombreTxt;
        public TextView tituloCat;
        public TextView rangoprecioTxt;
        public TextView descripcionTxt;
        public TextView direccionTxt;
        public ImageView vertblack;



    }

    public AdapterUbicaciones(Activity context, String[] nombres, String[] descripciones, String[] rangoprecio, String[] direccion,String[] titulos) {
        super(context, R.layout.activity_ubicaciones, nombres);
        this.context = context;
        this.nombres = nombres;
        this.descripciones = descripciones;
        this.rangoprecio = rangoprecio;
        this.direccion = direccion;
        this.titulos = titulos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.ubicaciones_layout, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tituloCat = (TextView) rowView.findViewById(R.id.tituloCat);
            viewHolder.nombreTxt = (TextView) rowView.findViewById(R.id.nombrePerfilComentario);
            viewHolder.descripcionTxt = (TextView) rowView.findViewById(R.id.descripcionTxt);
            //viewHolder.rangoprecioTxt = (TextView) rowView.findViewById(R.id.rangoprecioTxt);
            viewHolder.direccionTxt = (TextView) rowView.findViewById(R.id.direccionTxt);
            viewHolder.vertblack = (ImageView) rowView.findViewById(R.id.vertblack);

            rowView.setTag(viewHolder);
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        if(position == 0){
            holder.tituloCat.setVisibility(View.VISIBLE);
            holder.tituloCat.setText(titulos[position]);
        }
        else{
            holder.tituloCat.setVisibility(View.GONE);
            if (!titulos[position].equals(titulos[position-1])){
                holder.tituloCat.setVisibility(View.VISIBLE);
                holder.tituloCat.setText(titulos[position]);
            }
        }
        String dia = descripciones[position];
        String nombre = nombres[position];
        //String mes = rangoprecio[position];
        String hora = direccion[position];

        holder.nombreTxt.setText(nombre);
        holder.descripcionTxt.setText(dia);
       // holder.rangoprecioTxt.setText(mes);
        holder.direccionTxt.setText(hora);
        if(position==getCount() - 1){
            holder.vertblack.setVisibility(View.VISIBLE);
        }
        return rowView;
    }

}
