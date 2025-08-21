package com.example.esemkareceipt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class ReceiptRecViewAdapter extends RecyclerView.Adapter<ReceiptRecViewAdapter.ViewHolder> {

    JSONArray dataReceiptKetikaDiClick;

    public ReceiptRecViewAdapter(JSONArray dataReceiptKetikaDiClick) {
        this.dataReceiptKetikaDiClick = dataReceiptKetikaDiClick;
    }

    @NonNull
    @Override
    public ReceiptRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptRecViewAdapter.ViewHolder holder, int position) {
        View layoutReceiptClick = holder.itemView;
        ImageView img = layoutReceiptClick.findViewById(R.id.itemReceiptImg);
        TextView lblTitleReceipt = layoutReceiptClick.findViewById(R.id.itemLbludulReceipt);
        TextView lblDescReceipt = layoutReceiptClick.findViewById(R.id.itemLblDescriptionReceipt);

        try {
            JSONObject receiptClik = dataReceiptKetikaDiClick.getJSONObject(position);
            String titleReceipt = receiptClik.getString("title");
            String descReceipt = receiptClik.getString("description");
            int categoryId = receiptClik.getInt("id");

            String namaIcon = receiptClik.getString("image");

            lblTitleReceipt.setText(titleReceipt);
            lblDescReceipt.setText(descReceipt);


            String urlIcon = "http://10.0.2.2:5000/images/recipes/" + namaIcon;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpURLConnection url = (HttpURLConnection) new URL(urlIcon).openConnection();
                        Bitmap bitmap = BitmapFactory.decodeStream(url.getInputStream());

                        ReceiptActivity receiptActivity = (ReceiptActivity) layoutReceiptClick.getContext();
                        receiptActivity.runOnUiThread(new Runnable() {
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


            CardView cardReceiptClick = layoutReceiptClick.findViewById(R.id.itemcardReceiptClick);
            cardReceiptClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(layoutReceiptClick.getContext(), ReceiptDetailActivity.class);
                    intent.putExtra("item", receiptClik.toString());
                    intent.putExtra("title", titleReceipt);

                    layoutReceiptClick.getContext().startActivity(intent);
                }
            });


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return dataReceiptKetikaDiClick.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
