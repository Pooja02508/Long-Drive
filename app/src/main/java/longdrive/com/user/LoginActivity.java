package longdrive.com.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import longdrive.com.NavigationDrawer;
import longdrive.com.R;


public class LoginActivity extends AppCompatActivity {

    TextView signup_here,skip;
    SharedPreferences sp;
    private FirebaseAuth mAuth;
    TextInputEditText pass,emailid;
    EditText mobile;
    Button login;
    String UserMobile;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference=firebaseDatabase.getInstance().getReference("UserDetails");
    CountryCodePicker codePicker;
    ImageView googleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        signup_here = findViewById(R.id.signup_here);
        pass = findViewById(R.id.password);
        emailid = findViewById(R.id.username);
    //    mobile = findViewById(R.id.mobileNumber);
        login = findViewById(R.id.login);
       // codePicker = findViewById(R.id.country_code);
        skip=findViewById(R.id.skip);

        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("login", MODE_PRIVATE);
        String userId = sp.getString("UserMobile", null);

        if (sp.getBoolean("logged", false)) {
            goToMainActivity();
        }


        signup_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NavigationDrawer.class));
            }
        });

    }
    private void loginUserAccount() {

        String email, password;
        email = emailid.getText().toString();
        password = pass.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
            return;
        }

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
//                                    String country_code=codePicker.getSelectedCountryCode();
//                                    String phone = "+"+country_code+mobile.getText().toString();

                                    Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();
                                    sp.edit().putBoolean("logged",true).apply();
                                    sp.edit().putString("UserMobile",email).apply();
                                    goToMainActivity();
                                    finish();
                                }

                                else {
                                    Toast.makeText(getApplicationContext(), "Login failed!!", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
    }

    public void goToMainActivity() {
        Intent i = new Intent(LoginActivity.this, NavigationDrawer.class);
        startActivity(i);
        finish();
    }


}