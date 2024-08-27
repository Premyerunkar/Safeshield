package com.sierratechnologies.safeshield;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterNumberActivity extends AppCompatActivity {

    TextInputEditText number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_number);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        number = findViewById(R.id.numberEdit);

        number = findViewById(R.id.numberEdit);
    }

    public void saveNumber(View view) {
        String numberString = number.getText().toString();
        if(numberString.length()==10){
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("ENUM", numberString);
            myEdit.apply();
            RegisterNumberActivity.this.finish();
        }else {
            Toast.makeText(this, "Enter Valid Number!", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveNumber1(View view) {
        // Show the dialog with indications first
        showIndicationDialog();
    }

    private void showIndicationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Indications")
                .setMessage("1. Ensure your phone has an active internet connection." +
                        "\n2. Keep the app running in the background." +
                        "\n3. Only use the SOS feature in real emergencies. Misuse could cause unnecessary panic among your contacts."+
                        "\n4. Enable location services for accurate tracking."+
                        "\n5. In addition to using the app, practice basic safety measures such as avoiding dark or isolated areas, staying in groups when possible,"+
                        "\n6. While SafeShield is designed to help in emergencies, it is not a substitute for immediate help from authorities. Always contact local emergency services if you are in immediate danger.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // After the user clicks "OK", check if the number is valid
                    String numberString = number.getText().toString();
                    if (numberString.length() == 10) {
                        // Save the number and switch to MainActivity
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("ENUM", numberString);
                        myEdit.apply();
                        Toast.makeText(this, "Number Saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Enter Number to proceed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .show();
    }


}