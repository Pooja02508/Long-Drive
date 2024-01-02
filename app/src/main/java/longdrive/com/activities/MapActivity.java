package longdrive.com.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import longdrive.com.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address=getIntent().getStringExtra("Current Address");

        if(address!=null) {
            try {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address firstAddress = addresses.get(0);
                    LatLng location = new LatLng(firstAddress.getLatitude(), firstAddress.getLongitude());
                    mMap.clear(); // Clear previous markers
                    mMap.addMarker(new MarkerOptions().position(location).title(address));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
                LatLng defaultLocation = new LatLng(21.1458, 79.0882); // San Francisco, CA
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));
        }

        // Move the camera to a default location
//        LatLng defaultLocation = new LatLng(37.7749, -122.4194); // San Francisco, CA
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));

        // Set an onMapClickListener to handle map clicks
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Handle the chosen location (latLng)
                // You can show a marker, save the location, etc.

                // Convert LatLng to address (optional)
                String selectedLocation = getAddressFromLatLng(latLng);

                // Return the selected location to the calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_location", selectedLocation);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // You can customize how the address is formatted based on your needs
                StringBuilder addressStringBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressStringBuilder.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        addressStringBuilder.append(", ");
                    }
                }

                return addressStringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unable to retrieve address";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default:return super.onOptionsItemSelected(item);
        }
    }


}