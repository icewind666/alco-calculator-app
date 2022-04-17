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
public class DadataRegionAdapter extends ArrayAdapter<DadataRegion> {

    private Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((DadataRegion)resultValue).name;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<DadataRegion> suggestions = new ArrayList<>();
                DadataApi api = new DadataApi();
                api.context = getContext();
                JSONArray result = api.searchRegionsSync(constraint.toString());

                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject current = (JSONObject) result.get(i);
                        if(current != null) {
                            DadataRegion regionSugg = new DadataRegion();
                            regionSugg.name = current.getString("value");
                            regionSugg.sourceObj = current;
                            suggestions.add(regionSugg);
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
                addAll((ArrayList<DadataRegion>) results.values);

            }
            notifyDataSetChanged();
        }
    };

    /**
     *
     * @param context
     * @param textViewResourceId
     */
    public DadataRegionAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }
}