package longdrive.com.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import longdrive.com.R;
import longdrive.com.activities.PickupLocation;
import longdrive.com.user.UserDetails;

public class FreightFragment extends Fragment {

    TextView pickup_location, destination,vehicle_size;
    private static final int MAP_REQUEST_CODE = 101;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    UserDetails userDetails;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_freight, container, false);

        pickup_location=root.findViewById(R.id.pickup_location);
        destination=root.findViewById(R.id.destination);
        vehicle_size=root.findViewById(R.id.vehicle_size);


        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userDetails = new UserDetails();

        sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        String userId = sp.getString("UserMobile", null);

        if (userId == null) {
            pickup_location.setText("Pickup Location");

        } else {
            firestore.collection("UserDetails").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String getUserCity = document.getString("userAddress");
                                    pickup_location.setText(getUserCity);

                                } else {
                                    Toast.makeText(getActivity(), "User does not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Error getting user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PickupLocation.class);
                intent.putExtra("location_type", "Destination");
                startActivityForResult(intent, MAP_REQUEST_CODE);
            }
        });

        vehicle_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVehicleDialog();
            }
        });

        return root;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String selectedLocation = data.getStringExtra("selected_location");
            String locationType = data.getStringExtra("location_type");
            if (locationType != null && locationType.equals("Destination")) {
                destination.setText(selectedLocation);
            }
        }
    }

    private void showVehicleDialog() {
        // Create a custom dialog
        final Dialog vehicleDialog = new Dialog(requireContext());
        vehicleDialog.setContentView(R.layout.vehicle_size_layout);

        RelativeLayout rikshaw = vehicleDialog.findViewById(R.id.rikshaw);
        RelativeLayout small_truck = vehicleDialog.findViewById(R.id.small_truck);
        RelativeLayout medium_truck = vehicleDialog.findViewById(R.id.medium_truck);
        ImageView closeButton = vehicleDialog.findViewById(R.id.closeButton);

        rikshaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processVehicle( "Rikshaw (XS)");
                vehicleDialog.dismiss();
            }
        });

        small_truck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processVehicle("Small truck (S)");
                vehicleDialog.dismiss();
            }
        });
        medium_truck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processVehicle( "Medium truck (M)");
                vehicleDialog.dismiss();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicleDialog.dismiss();
            }
        });

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int dialogHeight = screenHeight / 2;
        vehicleDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);

        vehicleDialog.show();
    }

    private void processVehicle(String vehicle) {
        vehicle_size.setText(vehicle);
    }
}