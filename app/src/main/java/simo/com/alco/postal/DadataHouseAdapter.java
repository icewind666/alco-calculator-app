package simo.com.alco.postal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 *
 */
public class DadataHouseAdapter extends ArrayAdapter<DadataHouse> {
    public String currentStreet;

    private Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((DadataHouse)resultValue).name;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<DadataHouse> suggestions = new ArrayList<>();
                DadataApi api = new DadataApi();
                api.context = getContext();
                JSONArray result = api.searchHousesSync(constraint.toString(), currentStreet);

                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject current = (JSONObject) result.get(i);
                        if(current != null) {
                            DadataHouse sugg = new DadataHouse();
                            sugg.name = current.getString("value");
                            sugg.sourceObj = current;
                            suggestions.add(sugg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();

            if (results != null && results.count > 0) {
                addAll((ArrayList<DadataHouse>) results.values);

            }
            notifyDataSetChanged();
        }
    };

    /**
     *
     * @param context
     * @param textViewResourceId
     */
    public DadataHouseAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }
}