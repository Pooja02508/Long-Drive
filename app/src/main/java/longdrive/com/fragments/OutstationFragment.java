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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import longdrive.com.R;
import longdrive.com.activities.PickupLocation;
import longdrive.com.user.UserDetails;

public class OutstationFragment extends Fragment {

    TextView pickup_location, destination,fare,comments,passengers;
    private static final int MAP_REQUEST_CODE = 101;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    UserDetails userDetails;
    SharedPreferences sp;
    int np;
    LinearLayout outstationLayout,privateRideLayout,parcelLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_outstation, container, false);

        pickup_location=root.findViewById(R.id.pickup_location);
        destination=root.findViewById(R.id.destination);
        outstationLayout=root.findViewById(R.id.outstationLayout);
        privateRideLayout=root.findViewById(R.id.privateRideLayout);
        parcelLayout=root.findViewById(R.id.parcelLayout);
        fare=root.findViewById(R.id.fare);
        comments=root.findViewById(R.id.comment);
        passengers=root.findViewById(R.id.passengers);

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

        outstationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outstationLayout.setBackgroundResource(R.drawable.car_layout_bg);
            }
        });
        privateRideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateRideLayout.setBackgroundResource(R.drawable.car_layout_bg);
                parcelLayout.setBackgroundResource(R.drawable.auto_layout_bg);
            }
        });
        parcelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parcelLayout.setBackgroundResource(R.drawable.car_layout_bg);
                privateRideLayout.setBackgroundResource(R.drawable.auto_layout_bg);
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

        fare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFareDialog();
            }
        });
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentsDialog();
            }
        });
        passengers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPessangerDialog();
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

    private void processFare(String fareAmount, String payment) {
        fare.setText(fareAmount + ", " + payment);
    }

    private void showCommentsDialog() {
        final Dialog commentDialog = new Dialog(requireContext());
        commentDialog.setContentView(R.layout.comments_dialog);

        EditText optionsComments = commentDialog.findViewById(R.id.optionsComments);
        Switch passSwitch = commentDialog.findViewById(R.id.passSwitch);
        TextView doneButton = commentDialog.findViewById(R.id.doneButton);
        TextView closeButton = commentDialog.findViewById(R.id.closeButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDialog.dismiss();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = optionsComments.getText().toString();
                processComments(comment);
                commentDialog.dismiss();
            }
        });

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int dialogHeight = screenHeight / 2;
        commentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);

        commentDialog.show();
    }

    private void processComments(String comment) {
        comments.setText(comment);
    }

    private void showPessangerDialog() {
        // Create a custom dialog
        final Dialog passengerDialog = new Dialog(requireContext());
        passengerDialog.setContentView(R.layout.passenger_dialog);

        // Get views from the dialog layout
        TextView noPassengers = passengerDialog.findViewById(R.id.noPassenger);
        Button minusButton = passengerDialog.findViewById(R.id.minusButton);
        Button plusButton = passengerDialog.findViewById(R.id.plusButton);
        Button doneButton = passengerDialog.findViewById(R.id.doneButton);

        // Set initial passenger count
        np = Integer.parseInt(noPassengers.getText().toString());

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decrease passenger count
                np--;
                noPassengers.setText(String.valueOf(np));
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increase passenger count
                np++;
                noPassengers.setText(String.valueOf(np));
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPessanger(np);
                passengerDialog.dismiss();
            }
        });

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int dialogHeight = screenHeight / 2;
        passengerDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);

        passengerDialog.show();
    }

    private void processPessanger(int np) {
        passengers.setText(String.valueOf(np));
    }

}