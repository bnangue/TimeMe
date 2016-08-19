package com.app.bricenangue.timeme;


import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by bricenangue on 07/03/16.
 */
public class AndroidListAdapter extends ArrayAdapter<CalendarCollection>{

    private final Context context;
    private final ArrayList<CalendarCollection> values;
    private ViewHolder viewHolder;
    private final int resourceId;

    public AndroidListAdapter(Context context, int resourceId,ArrayList<CalendarCollection> values) {
        super(context, resourceId, values);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.values = values;
        this.resourceId = resourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, parent, false);


            viewHolder = new ViewHolder();



            convertView.setTag(viewHolder);


        }else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CalendarCollection list_obj=values.get(position);
        viewHolder.tv_date.setText(list_obj.datetime);
        viewHolder.tv_event.setText(list_obj.title);

        return convertView;
    }





    public class ViewHolder {

        TextView tv_event;
        TextView tv_date;

    }
}
