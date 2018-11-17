package alchemist.fit.uom.alchemists.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import alchemist.fit.uom.alchemists.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomListAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] web;
    private final String[] time;
    private final Integer[] imageId;
    public CustomListAdapter(Activity context, String[] time,
                             String[] web, Integer[] imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.time = time;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = rowView.findViewById(R.id.txt);
        TextView txtDate = rowView.findViewById(R.id.txtNew);
        CircleImageView imageView = rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);
        txtDate.setText(time[position]);
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}