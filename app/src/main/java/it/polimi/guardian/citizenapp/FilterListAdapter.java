package it.polimi.guardian.citizenapp;

/**
 * Created by Mirjam on 19/04/2015.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FilterListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] eventType;
    private final String[] eventDate;
    private final String[] eventAddress;
    private final String[] eventDescription;


    public FilterListAdapter(Activity context, String[] eventType, String[] eventDate, String[] eventAddress, String[] eventDescription) {
        super(context, R.layout.filtered_list_item, eventDescription);

        this.context=context;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.eventAddress = eventAddress;
        this.eventDescription = eventDescription;
    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.filtered_list_item, parent,false);

        TextView tv_date = (TextView) rowView.findViewById(R.id.filtered_item_date);
        TextView tv_address = (TextView) rowView.findViewById(R.id.filtered_item_address);
        TextView tv_description = (TextView) rowView.findViewById(R.id.filtered_item_description);
        ImageView iv_icon = (ImageView) rowView.findViewById(R.id.filtered_item_icon);

        tv_date.setText(eventDate[position]);
        tv_address.setText(eventAddress[position]);
        tv_description.setText(eventDescription[position]);

        switch (eventType[position]) {
            case "F":
                iv_icon.setImageResource(R.drawable.flame_gray);
                break;
            case "P":
                iv_icon.setImageResource(R.drawable.police_badge_gray);
                break;
            default:
                iv_icon.setImageResource(R.drawable.ambulance_gray);
                break;
        }

        return rowView;
    }
}