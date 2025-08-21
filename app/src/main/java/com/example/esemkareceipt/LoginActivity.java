package com.example.esemkareceipt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText txtUsername = findViewById(R.id.logintxtUsername);
        EditText txtPassword = findViewById(R.id.LogintxtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView lblRegister = findViewById(R.id.loginLblRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameUser = txtUsername.getText().toString();
                String usernamePassword = txtPassword.getText().toString();

                JSONObject datalogin = new JSONObject();
                try {
                    datalogin.put("username", usernameUser);
                    datalogin.put("password", usernamePassword);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Log.d("test", datalogin.toString());

                APIHelper apilogin = new APIHelper("http://10.0.2.2:5000/api/sign-in", "POST");

                try {
                    String result = apilogin.execute(datalogin.toString()).get();
                    Integer responseCode = apilogin.connection.getResponseCode();

                    if (responseCode == 200) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                            Toast.makeText(LoginActivity.this, "succes", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "Username atau password salah", Toast.LENGTH_SHORT).show();

                    }
                    Toast.makeText(LoginActivity.this, responseCode.toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        lblRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}