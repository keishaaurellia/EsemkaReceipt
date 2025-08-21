package com.example.esemkareceipt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class CategoriesFragment extends Fragment {

    RecyclerView recView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.tab_categories, container, false);

        recView = fragmentView.findViewById(R.id.homeRecView);
        loadData();
        return fragmentView;
    }

    private void loadData() {
        APIHelper helper = new APIHelper("http://10.0.2.2:5000/api/categories", "GET");

        try {
            JSONArray dataReceiptdariAPI = new JSONArray(helper.execute().get());
            recView.setAdapter(new HomeRecViewAdapter(dataReceiptdariAPI));
            recView.setLayoutManager(new GridLayoutManager(getActivity(), 2)); // Using getActivity() here

        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
