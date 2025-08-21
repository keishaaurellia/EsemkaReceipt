package com.example.esemkareceipt;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    int tahundipilih = 2025;
    int bulanDipilih = Calendar.APRIL;
    int tangggalDipilih = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rgeister);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText txtUsername = findViewById(R.id.registerTxtUsername);
        EditText txtFullName = findViewById(R.id.registerTxtFullName);
        EditText txtDateOfBirth = findViewById(R.id.registerTxtDate);
        txtDateOfBirth.setOnKeyListener(null);
        EditText txtPassword = findViewById(R.id.registerTxtPassword);
        EditText txtConfirmPassword = findViewById(R.id.registerTxtConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        txtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tahundipilih = year;
                        bulanDipilih = month + 1;
                        tangggalDipilih = day;

                        // Format bulan menjadi dua digit
                        String formattedMonth = String.format("%02d", bulanDipilih);

                        // Format tanggal dan tahun
                        txtDateOfBirth.setText(year + "-" + formattedMonth + "-" + day);
                    }

                }, tahundipilih, bulanDipilih - 1, tangggalDipilih);
                dialog.show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString().trim();
                String fullName = txtFullName.getText().toString().trim();
                String dateOfBirth = txtDateOfBirth.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String confirmpassword = txtConfirmPassword.getText().toString().trim();

                if (username.isEmpty() || fullName.isEmpty()|| dateOfBirth.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Harus diisi semua", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmpassword)) {
                    Toast.makeText(RegisterActivity.this, "Password tidak sama", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject regsiterData = new JSONObject();
                try {
                    regsiterData.put("username", username);
                    regsiterData.put("fullName", fullName);
                    regsiterData.put("password", password);
                    regsiterData.put("dateOfBirth", dateOfBirth);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Terjadi kesalahan saat membuat data registrasi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("testRegis", regsiterData.toString());

                APIHelper apiRegister = new APIHelper("http://10.0.2.2:5000/api/sign-up", "POST");

                try {
                    String result = apiRegister.execute(regsiterData.toString()).get();

                    int responseCode = apiRegister.connection.getResponseCode();

                    if (responseCode == 201) {
                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else if (responseCode == 400) {
                        Toast.makeText(RegisterActivity.this, "Username sudah dipakai!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registrasi gagal: " + result, Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }



        });

    }
}