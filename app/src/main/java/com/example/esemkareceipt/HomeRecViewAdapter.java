package com.example.esemkareceipt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeRecViewAdapter extends RecyclerView.Adapter<HomeRecViewAdapter.ViewHolder> {
    JSONArray dataReceipt;

    public HomeRecViewAdapter(JSONArray dataReceipt) {
        this.dataReceipt = dataReceipt;
    }


    @NonNull
    @Override
    public HomeRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecViewAdapter.ViewHolder holder, int position) {
        View layoutHome = holder.itemView;
        ImageView img = layoutHome.findViewById(R.id.itemCategoriesImg);
        TextView lblName = layoutHome.findViewById(R.id.itemCategoriesJudul);


        try {
            JSONObject receipt = dataReceipt.getJSONObject(position);
            String namaReceipt = receipt.getString("name");
            String namaIcon = receipt.getString("icon");
            int categoryId = receipt.getInt("id");


            lblName.setText(namaReceipt);
            String urlIcon = "http://10.0.2.2:5000/images/categories/" + namaIcon;

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        HttpURLConnection url = (HttpURLConnection) new URL(urlIcon).openConnection();
                        Bitmap bitmap = BitmapFactory.decodeStream(url.getInputStream());

                        HomeActivity homeActivity = (HomeActivity) layoutHome.getContext();
                        homeActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                img.setImageBitmap(bitmap);
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();


            CardView cardReceipt = layoutHome.findViewById(R.id.cardReceipt);
            cardReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(layoutHome.getContext(), ReceiptActivity.class);
                    intent.putExtra("categoryId", categoryId);
                    intent.putExtra("categoryName", namaReceipt);

                    holder.itemView.getContext().startActivity(intent);
                }
            });


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return dataReceipt.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
