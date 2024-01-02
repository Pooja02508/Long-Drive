package longdrive.com.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import longdrive.com.R;
import longdrive.com.activities.PickupLocation;
import longdrive.com.user.UserDetails;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
public class CityFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView pickup_location, destination, options_comments, offer_your_fare;
    Button findDriver;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    UserDetails userDetails;
    SharedPreferences sp;
    String getUserCity;
    LinearLayout carLayout, autoLayout, outstationLayout;

    private static final int MAP_REQUEST_CODE = 101;

    String cabType = "";
    Bitmap smallBitmap;

    public CityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_city, container, false);

        pickup_location = root.findViewById(R.id.pickup_location);
        options_comments = root.findViewById(R.id.options_comments);
        offer_your_fare = root.findViewById(R.id.offer_your_fare);
        destination = root.findViewById(R.id.destination);
        findDriver = root.findViewById(R.id.findDriver);
        carLayout = root.findViewById(R.id.carLayout);
        autoLayout = root.findViewById(R.id.autoLayout);
        outstationLayout = root.findViewById(R.id.outstationLayout);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userDetails = new UserDetails();


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        carLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carLayout.setBackgroundResource(R.drawable.car_layout_bg);
                autoLayout.setBackgroundResource(R.drawable.auto_layout_bg);
                outstationLayout.setBackgroundResource(R.drawable.auto_layout_bg);

                cabType = "Car";
            }
        });
        autoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoLayout.setBackgroundResource(R.drawable.car_layout_bg);
                carLayout.setBackgroundResource(R.drawable.auto_layout_bg);
                outstationLayout.setBackgroundResource(R.drawable.auto_layout_bg);

                cabType = "Auto";
            }
        });
        outstationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outstationLayout.setBackgroundResource(R.drawable.car_layout_bg);
                carLayout.setBackgroundResource(R.drawable.auto_layout_bg);
                autoLayout.setBackgroundResource(R.drawable.auto_layout_bg);
            }
        });

        pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PickupLocation.class);
                intent.putExtra("location_type", "Pickup");
                intent.putExtra("Current Location", getUserCity);
                startActivityForResult(intent, MAP_REQUEST_CODE);
            }
        });

        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PickupLocation.class);
                intent.putExtra("location_type", "Destination");
                startActivityForResult(intent, MAP_REQUEST_CODE);
            }
        });

        offer_your_fare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFareDialog();
            }
        });

        options_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentsDialog();
            }
        });

        findDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your logic for finding a driver
                // Add route from pickup location to destination
                String pickupLocation = pickup_location.getText().toString();
                String destinationLocation = destination.getText().toString();

                if (!pickupLocation.isEmpty() && !destinationLocation.isEmpty()) {
                    // Call a method to draw the route on the map
                    drawRouteOnMap(pickupLocation, destinationLocation);

                    // Convert pickup location from String to LatLng
                    LatLng pickupLatLng = getLatLngFromLocationName(pickupLocation);

                    if (pickupLatLng != null) {
                        // Call a method to show nearby drivers on the map
                        showNearbyDrivers(pickupLatLng);
                    } else {
                        Toast.makeText(getActivity(), "Failed to convert pickup location to LatLng", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Please set both pickup and destination locations", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }


    private void showFareDialog() {
        // Create a custom dialog
        final Dialog fareDialog = new Dialog(requireContext());
        fareDialog.setContentView(R.layout.fare_dialog);

        // Get views from the dialog layout
        EditText fareAmountEditText = fareDialog.findViewById(R.id.fareAmountEditText);
        Button cashButton = fareDialog.findViewById(R.id.cashButton);
        Button qrButton = fareDialog.findViewById(R.id.qrButton);
        TextView closeButton = fareDialog.findViewById(R.id.closeButton);

        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Cash payment
                // You can add logic to process the fare amount and payment type
                String fareAmount = fareAmountEditText.getText().toString();
                processFare(fareAmount, "Cash");
                fareDialog.dismiss();
            }
        });

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle OQ payment
                // You can add logic to process the fare amount and payment type
                String fareAmount = fareAmountEditText.getText().toString();
                processFare(fareAmount, "QR-Payment");
                fareDialog.dismiss();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fareDialog.dismiss();
            }
        });

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int dialogHeight = screenHeight / 2;
        fareDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);

        fareDialog.show();
    }

    private void showCommentsDialog() {
        // Create a custom dialog
        final Dialog commentDialog = new Dialog(requireContext());
        commentDialog.setContentView(R.layout.comments_dialog);

        // Get views from the dialog layout
        EditText optionsComments = commentDialog.findViewById(R.id.optionsComments);
        Switch passSwitch = commentDialog.findViewById(R.id.passSwitch);
        TextView doneButton = commentDialog.findViewById(R.id.doneButton);
        TextView closeButton = commentDialog.findViewById(R.id.closeButton);
        TextView passenger = commentDialog.findViewById(R.id.passenger);

        if (passSwitch.isChecked()) {
            passenger.setText("More than 4 passengers");
        } else
            passenger.setHint("More than 4 passengers");

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDialog.dismiss();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = optionsComments.getText().toString();
                processComments(comments);
                commentDialog.dismiss();
            }
        });

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int dialogHeight = screenHeight / 2;
        commentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);

        commentDialog.show();
    }

    private void processComments(String comments) {
        options_comments.setText(comments);
    }

    private void processFare(String fareAmount, String payment) {
        offer_your_fare.setText(fareAmount + ", " + payment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String selectedLocation = data.getStringExtra("selected_location");
            String locationType = data.getStringExtra("location_type");
            if (locationType != null && locationType.equals("Pickup")) {
                pickup_location.setText(selectedLocation);
            } else if (locationType != null && locationType.equals("Destination")) {
                destination.setText(selectedLocation);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        String userId = sp.getString("UserMobile", null);

        if (userId == null) {

            LatLng location = new LatLng(37.7749, -122.4194); // Replace with your desired coordinates
            mMap.addMarker(new MarkerOptions().position(location).title("Marker in Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        } else {
            firestore.collection("UserDetails").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    getUserCity = document.getString("userAddress");
                                    pickup_location.setText(getUserCity);
                                    findLocationOnMap(getUserCity);

                                } else {
                                    Toast.makeText(getActivity(), "User does not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Error getting user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    private void findLocationOnMap(String address) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

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

    private void showNearbyDrivers(LatLng pickupLocation) {

        List<LatLng> nearbyDriverLocations = getNearbyDriverLocations(pickupLocation);

        for (LatLng driverLocation : nearbyDriverLocations) {
            // BitmapDescriptor smallIcon = BitmapDescriptorFactory.fromResource(R.drawable.ride_car);

            int iconWidth = 90;
            int iconHeight = 90;

            if (cabType.equals("Car")) {
                smallBitmap = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.ride_car),
                        iconWidth,
                        iconHeight,
                        false
                );
            } else {
                smallBitmap = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.auto),
                        iconWidth,
                        iconHeight,
                        false
                );
            }
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(driverLocation)
                    .title("Driver")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallBitmap));

            mMap.addMarker(markerOptions);

            //  break;
        }

    }

    private List<LatLng> getNearbyDriverLocations(LatLng pickupLocation) {

        int numDrivers = 5;
        double radiusDegrees = 0.005;

        List<LatLng> nearbyDrivers = new ArrayList<>();

        for (int i = 0; i < numDrivers; i++) {
            double randomLatOffset = Math.random() * 2 * radiusDegrees - radiusDegrees;
            double randomLngOffset = Math.random() * 2 * radiusDegrees - radiusDegrees;

            double driverLat = pickupLocation.latitude + randomLatOffset;
            double driverLng = pickupLocation.longitude + randomLngOffset;

            nearbyDrivers.add(new LatLng(driverLat, driverLng));
        }

        return nearbyDrivers;
    }


    private void drawRouteOnMap(String pickupLocation, String destinationLocation) {
        LatLng pickupLatLng = getLatLngFromLocationName(pickupLocation);
        LatLng destinationLatLng = getLatLngFromLocationName(destinationLocation);

        mMap.clear(); // Clear previous markers
        mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup Location"));
        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination Location"));


        drawPolylineOnMap(pickupLatLng, destinationLatLng);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickupLatLng);
        builder.include(destinationLatLng);
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void drawPolylineOnMap(LatLng pickupLatLng, LatLng destinationLatLng) {
        // Use PolylineOptions to draw the route polyline
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(pickupLatLng) // Start point
                .add(destinationLatLng) // End point
                .width(5) // Width of the polyline
                .color(Color.BLUE); // Color of the polyline

        // Add the polyline to the map
        mMap.addPolyline(polylineOptions);
    }

    private LatLng getLatLngFromLocationName(String address) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address firstAddress = addresses.get(0);
                return new LatLng(firstAddress.getLatitude(), firstAddress.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

//    private void drawRouteOnMap(String pickupLocation, String destinationLocation) {
//        LatLng pickupLatLng = getLatLngFromLocationName(pickupLocation);
//        LatLng destinationLatLng = getLatLngFromLocationName(destinationLocation);
//
//        // Use the Directions API to get the route
//        GeoApiContext context = new GeoApiContext.Builder()
//                .apiKey("AIzaSyCMq7OnBf6Fm0xPE8n4EsvEB2b7qHd-TeE")
//                .build();
//
//        DirectionsApi.newRequest(context)
//                .origin(new com.google.maps.model.LatLng(pickupLatLng.latitude, pickupLatLng.longitude))
//                .destination(new com.google.maps.model.LatLng(destinationLatLng.latitude, destinationLatLng.longitude))
//                .mode(TravelMode.DRIVING)
//                .setCallback(new PendingResult.Callback<DirectionsResult>() {
//                    @Override
//                    public void onResult(DirectionsResult result) {
//                        // Log the result to see what's coming back
//                        Log.d("DirectionsAPI", "Result: " + result.toString());
//
//                        // Draw the route on the map
//                        if (result.routes != null && result.routes.length > 0) {
//                            com.google.maps.model.LatLng[] decodedPath = result.routes[0].overviewPolyline.decodePath().toArray(new com.google.maps.model.LatLng[0]);
//
//                            List<LatLng> points = new ArrayList<>();
//                            for (com.google.maps.model.LatLng latLng : decodedPath) {
//                                points.add(new LatLng(latLng.lat, latLng.lng));
//                            }
//
//                            PolylineOptions polylineOptions = new PolylineOptions()
//                                    .addAll(points)
//                                    .width(4)
//                                    .color(Color.BLUE);
//
//                            mMap.addPolyline(polylineOptions);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Throwable e) {
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getActivity(), "Failed to fetch directions", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//
//
//    }

