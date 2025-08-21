package com.example.esemkareceipt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ProfileFragment extends Fragment {

    RecyclerView recView;
    ImageView imgProfile;
    TextView lblTidakAdaLike;
    TextView lblDescriptionTidakAdaLike;
    TextView lblprofileUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.tab_profile, container, false);

        recView = fragmentView.findViewById(R.id.profileRecView);
        imgProfile = fragmentView.findViewById(R.id.profileImg);
        lblTidakAdaLike = fragmentView.findViewById(R.id.itemProfileLblNothing);
        lblprofileUsername = fragmentView.findViewById(R.id.profilelblUser);
        lblDescriptionTidakAdaLike = fragmentView.findViewById(R.id.itemProfileLblDescriptionNothing);

        loadData();
        loadDataProfileImg();
        return fragmentView;
    }

    private void loadDataProfileImg() {
        APIHelper profileHelper = new APIHelper("http://10.0.2.2:5000/api/me", "GET");

        new Thread(() -> {
            try {
                String result = profileHelper.execute().get();
                JSONObject userObj = new JSONObject(result);
                String namaImg = userObj.getString("image");
                String nameUser = userObj.getString("username");

                lblprofileUsername.setText("Hello, " + nameUser);


                String urlImg = "http://10.0.2.2:5000/images/profiles/" + namaImg;

                HttpURLConnection urlConnection = (HttpURLConnection) new URL(urlImg).openConnection();
                Bitmap bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());

                requireActivity().runOnUiThread(() -> {
                    imgProfile.setImageBitmap(bitmap);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void loadData() {
        APIHelper helper = new APIHelper("http://10.0.2.2:5000/api/me/liked-recipes", "GET");

        try {
            JSONArray dataReceiptApi = new JSONArray(helper.execute().get());

            if (dataReceiptApi.length() == 0) {
                lblTidakAdaLike.setVisibility(View.VISIBLE);
                lblDescriptionTidakAdaLike.setVisibility(View.VISIBLE);

            }else {
                lblTidakAdaLike.setVisibility(View.GONE);
                lblDescriptionTidakAdaLike.setVisibility(View.GONE);
                recView.setAdapter(new profileRecViewAdapter(dataReceiptApi));
                recView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
