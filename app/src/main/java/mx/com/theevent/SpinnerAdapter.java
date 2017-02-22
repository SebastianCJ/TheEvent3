package mx.com.theevent;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Gatsu on 2/10/2017.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    private int hidingItemIndex;

    public SpinnerAdapter(Context context, int textViewResourceId, String[] objects, int hidingItemIndex) {
        super(context, textViewResourceId, objects);
        this.hidingItemIndex = hidingItemIndex;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = null;
        v = super.getDropDownView(position, null, parent);

        return v;
    }
}
