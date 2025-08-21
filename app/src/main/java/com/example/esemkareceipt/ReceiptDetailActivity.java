package com.example.esemkareceipt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReceiptDetailActivity extends AppCompatActivity {

    public JSONObject selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receipt_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgProduct = findViewById(R.id.receiptDetailImg);
        TextView lblJudul = findViewById(R.id.receiptDetailLblJudul);
        TextView lblCookingTime = findViewById(R.id.receiptDetailLblcookngEstimate);
        TextView lblPriceEstimate = findViewById(R.id.receiptDetailLblpriceEstimate);
        TextView lblCategory = findViewById(R.id.receiptDetailLblCategory);
        TextView lblDescription = findViewById(R.id.receiptDetailLblDescription);
        TextView lblIngredients = findViewById(R.id.receiptDetailLblIngredients);
        TextView lblSteps = findViewById(R.id.receiptDetailLblSteps);
        ImageView imgCategory = findViewById(R.id.receiptDetailImgCategory);
        ImageView imgLike = findViewById(R.id.receiptdetailImgLikeDefault);
        TextView lblNavReceipt = findViewById(R.id.detailReceiptLblNavbar);

        imgLike.setTag(0);

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String recipedId = selected.getString("id");
                    APIHelper likeHelper = new APIHelper("http://10.0.2.2:5000/api/recipes/like-recipe?recipeId=" + recipedId, "GET");

                    likeHelper.execute();

                    int currentStatus = (int) imgLike.getTag();

                    if (currentStatus == 0){
                        imgLike.setImageResource(R.drawable.like);
                        imgLike.setTag(1);
                        selected.put("isLike", true);
                    }else {
                        imgLike.setImageResource(R.drawable.likekosong);
                        imgLike.setTag(0);
                        selected.put("isLike", false);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        String jsonString = getIntent().getStringExtra("item");
        try {
            selected = new JSONObject(jsonString);

            boolean isLiked = selected.getBoolean("isLike");

            if (isLiked){
                imgLike.setImageResource(R.drawable.like);
                imgLike.setTag(1);
            }else {
                imgLike.setImageResource(R.drawable.likekosong);
                imgLike.setTag(0);
            }

            String selectedId  = selected.getString("id");
            APIHelper helper = new APIHelper("http://10.0.2.2:5000/api/recipes/detail/" + selectedId, "GET");

            try {
                String dataDetail = helper.execute().get();
                JSONObject jsonObject = new JSONObject(dataDetail);

                String receiptTitle = jsonObject.getString("title");
                String receiptDescription = jsonObject.getString("description");
                String receiptpriceEstimate = "Price Estimate: " + jsonObject.getString( "priceEstimate");
                String receiptcookingTimeEstimate = "Cooking Time Estimate:" + jsonObject.getString( "cookingTimeEstimate");
                String receiptimage = jsonObject.getString("image");

//                JSONArray ingredientsArray = jsonObject.getJSONArray("ingredients");
//
//                StringBuilder ingredientsBuilder = new StringBuilder();
//                for (int i = 0; i < ingredientsArray.length(); i++) {
//                    ingredientsBuilder.append(ingredientsArray.getString(i)).append("\n");
//                }
//                String receiptingredients = ingredientsBuilder.toString();

                JSONArray ingredientsArray = jsonObject.getJSONArray("ingredients");
                List<String> ingredientList = new ArrayList<>();
                for (int i = 0; i < ingredientsArray.length(); i++) {
                    ingredientList.add(ingredientsArray.getString(i));
                }
                String receiptingredients = TextUtils.join("\n\n", ingredientList);

                JSONArray stepsArray = jsonObject.getJSONArray("steps");
               List<String> stepsList = new ArrayList<>();
                for (int i = 0; i < stepsArray.length(); i++) {
                    stepsList.add(stepsArray.getString(i));
                }
                String receiptsteps = TextUtils.join("\n\n", stepsList);

                //Object Category
                JSONObject categoryObject = jsonObject.getJSONObject("category");
                String receiptCategory = categoryObject.getString("name");
                String receiptImgCategory = categoryObject.getString("icon");

                lblJudul.setText(receiptTitle);
                lblCookingTime.setText(receiptcookingTimeEstimate);
                lblPriceEstimate.setText(receiptpriceEstimate);
                lblCategory.setText(receiptCategory);
                lblDescription.setText(receiptDescription);
                lblIngredients.setText(receiptingredients);
                lblSteps.setText(receiptsteps);
                lblNavReceipt.setText(receiptTitle);

                String imgUrl = "http://10.0.2.2:5000/images/recipes/"+ receiptimage;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection connection = (HttpURLConnection) new URL(imgUrl).openConnection();
                            connection.setDoInput(true);
                            connection.connect();

                            Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgProduct.setImageBitmap(bitmap);
                                }
                            });

                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();

                String imgCategoryUrl = "http://10.0.2.2:5000/images/categories/" + receiptImgCategory;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection connection = (HttpURLConnection) new URL(imgCategoryUrl).openConnection();
                            connection.setDoInput(true);
                            connection.connect();

                            Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgCategory.setImageBitmap(bitmap);
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();

            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}