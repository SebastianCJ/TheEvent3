package mx.com.theevent;

/**
 * Created by Gatsu on 12/2/2016.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class AdapterEventos extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] nombres;
    private final String[] lugares;
    private final String[] descripciones;
    private final String[] fechas;
    ArrayList<Bitmap> imagenes;

    static class ViewHolder {
        public TextView nombreTxt;
        public TextView descTxt;
        public TextView lugarTxt;
        public TextView fechaTxt;
        public TextView seleccionarTxt;
        public ImageView image;
    }

    public AdapterEventos(Activity context, String[] nombres, String[] lugares, String[] descripciones, String[] fechas, ArrayList<Bitmap> imagenes) {
        super(context, R.layout.eventos_layout, nombres);
        this.context = context;
        this.nombres = nombres;
        this.lugares = lugares;
        this.descripciones = descripciones;
        this.fechas = fechas;
        this.imagenes = imagenes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.eventos_layout, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.nombreTxt = (TextView) rowView.findViewById(R.id.nombretxt);
            viewHolder.lugarTxt = (TextView) rowView.findViewById(R.id.lugartxt);
            viewHolder.descTxt = (TextView) rowView.findViewById(R.id.descripciontxt);
            viewHolder.fechaTxt = (TextView) rowView.findViewById(R.id.fechatxt);
            viewHolder.seleccionarTxt = (TextView) rowView.findViewById(R.id.seleccionar);

            viewHolder.nombreTxt.setTypeface(CustomFontsLoader.getTypeface(context,CustomFontsLoader.Bold));
            viewHolder.lugarTxt.setTypeface(CustomFontsLoader.getTypeface(context,CustomFontsLoader.Bold));
            viewHolder.descTxt.setTypeface(CustomFontsLoader.getTypeface(context,CustomFontsLoader.Light));
            viewHolder.fechaTxt.setTypeface(CustomFontsLoader.getTypeface(context,CustomFontsLoader.Bold));
            viewHolder.seleccionarTxt.setTypeface(CustomFontsLoader.getTypeface(context,CustomFontsLoader.Bold));

            viewHolder.image = (ImageView) rowView
                    .findViewById(R.id.eventimg);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String nombre = nombres[position];
        String lugar = lugares[position];
        String descripcion = descripciones[position];
        String fecha = fechas[position];
        if (imagenes.size()>0) {
            Bitmap imagen = imagenes.get(position);
            holder.image.setImageBitmap(imagen);
        }
        holder.nombreTxt.setText(nombre);
        holder.lugarTxt.setText(lugar);
        holder.descTxt.setText(descripcion);
        holder.fechaTxt.setText(fecha);


        return rowView;
    }
}
