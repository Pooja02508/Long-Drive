package longdrive.com.user;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import longdrive.com.NavigationDrawer;
import longdrive.com.R;


public class SignUp extends AppCompatActivity {

    SharedPreferences sp;
    TextView login_here, skip;
    EditText userLocation, username, emailId, mobile, pass;
    Button signUp;
    boolean isAllFieldsChecked = false;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    CollectionReference userCollection;
    UserDetails userDetails;
    CountryCodePicker codePicker;
    String currentDate;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        login_here = findViewById(R.id.login_here);
        userLocation = findViewById(R.id.userLocation);
        signUp = findViewById(R.id.signUp);
        emailId = findViewById(R.id.emailId);
        username = findViewById(R.id.username);
        mobile = findViewById(R.id.mobileNumber);
        pass = findViewById(R.id.password);
        codePicker = findViewById(R.id.country_code);
        skip = findViewById(R.id.skip);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userCollection = firebaseFirestore.collection("UserDetails");
        userDetails = new UserDetails();

        sp = getSharedPreferences("login", MODE_PRIVATE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        currentDate = sdf.format(new Date());

        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("login", MODE_PRIVATE);

        if (sp.getBoolean("logged", false)) {
            goToMainActivity();
        }

        login_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllFieldsChecked = CheckAllFields();
                if (isAllFieldsChecked) {
                    registerNewUser();
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NavigationDrawer.class));
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    private void registerNewUser() {
        String email, password;
        email = emailId.getText().toString();
        password = pass.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter email and password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String country_code = codePicker.getSelectedCountryCode();
                            phone = "+" + country_code + mobile.getText().toString();
                            userDetails.setUserName(username.getText().toString());
                            userDetails.setUserEmail(emailId.getText().toString());
                            userDetails.setUserAddress(userLocation.getText().toString());
                            userDetails.setUserMobile(phone);
                            userDetails.setUserPassword(pass.getText().toString());
                            userDetails.setJoiningTime(currentDate);

                            String EMAIL=emailId.getText().toString();

                            userCollection.document(phone).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Toast.makeText(getApplicationContext(), "User already exists.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                userCollection.document(EMAIL).set(userDetails)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                                                sp.edit().putBoolean("logged", true).apply();
                                                                sp.edit().putString("UserMobile", EMAIL).apply();
                                                                sp.edit().putString("UserName", username.getText().toString()).apply();
                                                                goToMainActivity();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(), "Fail to add data " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed!! Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean CheckAllFields() {
        if (username.getText().toString().length() == 0) {
            username.setError("Username is required");
            return false;
        }

        if (emailId.getText().toString().length() == 0) {
            emailId.setError("Email is required");
            return false;
        }

        if (mobile.getText().toString().length() == 0) {
            mobile.setError("Mobile is required");
            return false;
        } else if (mobile.getText().toString().length() < 10) {
            mobile.setError("Enter valid mobile number");
            return false;
        }

        if (pass.getText().toString().length() == 0) {
            pass.setError("Password is required");
            return false;
        } else if (pass.getText().toString().length() < 8) {
            pass.setError("Password must be minimum 8 characters");
            return false;
        }

        return true;
    }

    public void goToMainActivity() {
        Intent i = new Intent(SignUp.this, NavigationDrawer.class);
        startActivity(i);
        finish();
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    userLocation.setText(addresses.get(0).getAddressLine(0));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(getApplicationContext(), "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}


