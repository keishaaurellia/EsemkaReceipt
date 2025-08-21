package com.example.esemkareceipt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class profileRecViewAdapter extends RecyclerView.Adapter<profileRecViewAdapter.ViewHolder> {

    JSONArray dataReceiptProfile;

    public profileRecViewAdapter (JSONArray dataProfile) {
        this.dataReceiptProfile = dataProfile;
    }

    @NonNull
    @Override
    public profileRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull profileRecViewAdapter.ViewHolder holder, int position) {
        View layoutProfile = holder.itemView;
        ImageView img =  layoutProfile.findViewById(R.id.itemProfileReceiptImmg);

        try {
            JSONObject profileReceipt = dataReceiptProfile.getJSONObject(position);
            String namaImg = profileReceipt.getString("image");

            String urlImg =  " http://10.0.2.2:5000/images/recipes/" + namaImg;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpURLConnection url = (HttpURLConnection) new URL(urlImg).openConnection();
                        Bitmap bitmap = BitmapFactory.decodeStream(url.getInputStream());

                        HomeActivity homeActivity = (HomeActivity) layoutProfile.getContext();
                        homeActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                img.setImageBitmap(bitmap);
                            }
                        });
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            CardView cardReceiptClick = layoutProfile.findViewById(R.id.itemCardProfileReceipt);
            cardReceiptClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(layoutProfile.getContext(), ReceiptDetailActivity.class);
                    intent.putExtra("item", profileReceipt.toString());

                    layoutProfile.getContext().startActivity(intent);
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return dataReceiptProfile.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
