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


public class AdapterCalendario extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] descripcion;
    private final String[] dias;
    private final String[] meses;
    private final String[] horas;
    private final String[] years;


    static class ViewHolder {
        public TextView descripcionTxt;
        public TextView mesTxt;
        public TextView diaTxt;
        public TextView horaTxt;
        public TextView yearTxt;
        public ImageView vertblack;



    }

    public AdapterCalendario(Activity context, String[] descripcion, String[] dias, String[] meses, String[] horas, String[] years) {
        super(context, R.layout.activity_comentarios, descripcion);
        this.context = context;
        this.descripcion = descripcion;
        this.dias = dias;
        this.meses = meses;
        this.horas = horas;
        this.years = years;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

            rowView.setTag(viewHolder);
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        String dia = dias[position] + " de ";
        String nombre = descripcion[position];
        String mes = meses[position] + " de ";
        String hora = horas[position];
        String year = years[position];

        holder.descripcionTxt.setText(nombre);
        holder.diaTxt.setText(dia);
        holder.mesTxt.setText(mes);
        holder.horaTxt.setText(hora);
        holder.yearTxt.setText(year);
        if(position==getCount() - 1){
            holder.vertblack.setVisibility(View.VISIBLE);
        }
        return rowView;
    }

}
