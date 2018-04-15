package com.project.app.quickquery.adapter;

//import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.project.app.quickquery.R;
import com.project.app.quickquery.activities.PlaceSpeechActivity;
import com.project.app.quickquery.models.PlaceAutoComplete;
import java.util.List;
import java.util.StringTokenizer;


public class AutoCompleteAdapter extends ArrayAdapter<PlaceAutoComplete> {
    //ViewHolder holder;
    private Context context;
    private List<PlaceAutoComplete> Places;
    //private Activity mActivity;

    public AutoCompleteAdapter(Context context, List<PlaceAutoComplete> modelsArrayList, PlaceSpeechActivity placeSpeechActivity) {
        super(context, R.layout.autocomplete_row, modelsArrayList);
        this.context = context;
        this.Places = modelsArrayList;
        //this.mActivity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            rowView = inflater.inflate(R.layout.autocomplete_row, parent, false);
            holder = new ViewHolder();
            holder.name = rowView.findViewById(R.id.place_name);
            holder.location =  rowView.findViewById(R.id.place_detail);
            rowView.setTag(holder);

        } else
            holder = (ViewHolder) rowView.getTag();
        holder.Place = Places.get(position);
        StringTokenizer st=new StringTokenizer(holder.Place.getPlaceDesc(), ",");

        holder.name.setText(st.nextToken());
        StringBuilder desc_detail= new StringBuilder();
        for(int i=1; i<st.countTokens(); i++) {
            if(i==st.countTokens()-1){
                desc_detail.append(st.nextToken());
            }else {
                desc_detail.append(st.nextToken()).append(",");
            }
        }
        holder.location.setText(desc_detail.toString());
        return rowView;
    }

    class ViewHolder {
        PlaceAutoComplete Place;
        TextView name, location;
    }

    @Override
    public int getCount(){
        return Places.size();
    }
}