package longdrive.com.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import longdrive.com.NavigationDrawer;
import longdrive.com.R;
import longdrive.com.user.LoginActivity;

public class OnboardScreen extends AppCompatActivity {


    Button continueBtn, laterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_screen);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        continueBtn = findViewById(R.id.continueBtn);
        laterBtn = findViewById(R.id.laterBtn);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allow notification permission
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }


}
