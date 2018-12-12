package georgearistov.shulte_game.billing.row;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.billingclient.api.SkuDetails;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter {
    private Context context;
    private int textViewResourceId;
    public List<SkuDetails> objects =  new ArrayList<>();
    public static boolean flag = true;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<SkuDetails> objects) {
        super(context, resource, objects);
        this.context = context;
        this.textViewResourceId = resource;
        this.objects = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, textViewResourceId, null);
        if (flag != false) {
            ((TextView) convertView).setText(objects.get(position).getPrice());
        }
        return convertView;
    }
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(objects.get(position).getPrice());

        return label;
    }



}
