package longdrive.com.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import longdrive.com.R;
import longdrive.com.user.UserDetails;

public class PickupLocation extends AppCompatActivity {

    EditText location;
    TextView choose_on_map;
    ImageView cancel;
    private static final int MAP_REQUEST_CODE = 101;
    String selectedLocation,Location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        location=findViewById(R.id.location);
        choose_on_map=findViewById(R.id.choose_on_map);
        cancel=findViewById(R.id.cancel);

        Location=getIntent().getStringExtra("location_type");
        String CurrentLocation=getIntent().getStringExtra("Current Location");


        if(Location.equals("Pickup")){
            if(CurrentLocation!=null)
                location.setText(CurrentLocation);
            else
                location.setHint("Pickup Location");
        }
        else
            location.setHint("Destination");


        choose_on_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                mapIntent.putExtra("Current Address",CurrentLocation);
                startActivityForResult(mapIntent, MAP_REQUEST_CODE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText("");
                location.setHint("Pickup Location");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_location", selectedLocation);
                resultIntent.putExtra("location_type", Location);
                setResult(RESULT_OK, resultIntent);
                finish();
                return true;

            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected location and address from the MapActivity
            selectedLocation = data.getStringExtra("selected_location");
            location.setText(selectedLocation);
        }
    }

}