package com.example.ecommerce;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AutocompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {
    private static final String TAG = "AutocompleteAdapter";

    private List<AutocompletePrediction> predictions;
    private PlacesClient placesClient;

    public AutocompleteAdapter(Context context, PlacesClient placesClient) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        this.placesClient = placesClient;
        predictions = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return predictions.size();
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return predictions.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Fetch autocomplete predictions from Places API
                    predictions = getAutocompletePredictions(constraint.toString());
                    filterResults.values = predictions;
                    filterResults.count = predictions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private List<AutocompletePrediction> getAutocompletePredictions(String query) {
        List<AutocompletePrediction> predictions = new ArrayList<>();
        if (placesClient != null) {
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .build();

            Task<FindAutocompletePredictionsResponse> task = placesClient.findAutocompletePredictions(request);
            try {
                Tasks.await(task);
                FindAutocompletePredictionsResponse response = task.getResult(ApiException.class);
                if (response != null) {
                    predictions = response.getAutocompletePredictions();
                }
            } catch (ApiException | InterruptedException | ExecutionException e) {
                Log.e(TAG, "Error getting autocomplete predictions: " + e.getMessage());
            }
        }
        return predictions;
    }

}
