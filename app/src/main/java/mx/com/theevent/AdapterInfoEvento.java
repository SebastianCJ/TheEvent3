package mx.com.theevent;

/**
 * Created by Gatsu on 12/2/2016.
 */
import android.app.Activity;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class AdapterInfoEvento extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] nombres;
    private final String[] descripciones;


    static class ViewHolder {
        public TextView nombreTxt;
        public EditText descripcionTxt;
        public ImageView vertblack;



    }

    public AdapterInfoEvento(Activity context, String[] nombres, String[] descripciones) {
        super(context, R.layout.activity_ubicaciones, nombres);
        this.context = context;
        this.nombres = nombres;
        this.descripciones = descripciones;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.infoevento_layout, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.nombreTxt = (TextView) rowView.findViewById(R.id.nombreTxt);
            viewHolder.descripcionTxt = (EditText) rowView.findViewById(R.id.descripcionTxt);
            viewHolder.vertblack = (ImageView) rowView.findViewById(R.id.vertblack);

            rowView.setTag(viewHolder);
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        String descripcion = descripciones[position];
        String nombre = nombres[position];
        holder.nombreTxt.setText(nombre);
        holder.descripcionTxt.setText(descripcion);
        holder.descripcionTxt.setMovementMethod(LinkMovementMethod.getInstance());
        holder.descripcionTxt.setTextIsSelectable(true);

        return rowView;
    }

}
