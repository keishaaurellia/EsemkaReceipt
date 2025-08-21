package com.example.esemkareceipt;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ReceiptActivity extends AppCompatActivity {
    
    RecyclerView recView;
    TextView lblJudulNavbarReceipt;
    EditText txtSearch;
    TextView lblNotFound;
    ImageView imgNotFound;

    int categoryId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receipt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recView = findViewById(R.id.receiptClickRecViewAdapter);
        lblJudulNavbarReceipt = findViewById(R.id.navdetailLblReceipt);
        txtSearch = findViewById(R.id.detailreceiptTxtSearch);
        lblNotFound = findViewById(R.id.receiptLblRecipeNotFound);
        imgNotFound = findViewById(R.id.receiptImgNotFound);

        categoryId = getIntent().getIntExtra("categoryId", 1);
        loadData("categoryId=" + categoryId);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadData("categoryId=" + categoryId + "&search=" + txtSearch.getText());
            }
        });
    }


    private void loadData(String urlParams) {
//        APIHelper helper = new APIHelper("http://10.0.2.2:5000/api/recipes?categoryId=" + categoryId, "GET");
        String fullUrl = "http://10.0.2.2:5000/api/recipes?" + urlParams;
        APIHelper helper = new APIHelper(fullUrl, "GET");

        try {
            String result = helper.execute().get();
            JSONArray dataReceiptClick = new JSONArray(result);

            int totalRecipes = dataReceiptClick.length();
            txtSearch.setHint("Search for " + totalRecipes + " recipes.");

            if (dataReceiptClick.length() == 0){
                lblNotFound.setVisibility(View.VISIBLE);
                imgNotFound.setVisibility(View.VISIBLE);

                String navReceipt = getIntent().getStringExtra("categoryName");
                lblJudulNavbarReceipt.setText(navReceipt);
            }else{
                lblNotFound.setVisibility(View.GONE);
                imgNotFound.setVisibility(View.GONE);

                JSONObject firstItem = dataReceiptClick.getJSONObject(0);
                JSONObject category = firstItem.getJSONObject("category");
                String navReceipt = category.getString("name");

                lblJudulNavbarReceipt.setText(navReceipt);


                recView.setAdapter(new ReceiptRecViewAdapter(dataReceiptClick));
                recView.setLayoutManager(new GridLayoutManager(ReceiptActivity.this, 1));
            }

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }




//            JSONArray dataReceiptClick = new JSONArray(helper.execute().get());


}
}