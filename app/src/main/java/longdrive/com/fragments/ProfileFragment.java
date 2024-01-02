package longdrive.com.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import longdrive.com.R;
import longdrive.com.user.EditProfile;
import longdrive.com.user.UserDetails;

public class ProfileFragment extends Fragment {

    ImageView edit_profile2;
    CircleImageView profile_image;
    Button edit_profile1;
    SharedPreferences sp;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    TextView user_name, user_email, user_city, user_mobile;
    UserDetails userDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        profile_image = root.findViewById(R.id.profile_image);

        sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        String userId = sp.getString("UserMobile", null);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userDetails = new UserDetails();

        user_name = root.findViewById(R.id.user_name);
        user_email = root.findViewById(R.id.user_email);
        user_city = root.findViewById(R.id.user_city);
        user_mobile = root.findViewById(R.id.user_mobile);
        edit_profile1 = root.findViewById(R.id.edit_profile1);
        edit_profile2 = root.findViewById(R.id.edit_profile2);

        edit_profile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });
        edit_profile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });

        if (userId == null) {
        } else {
            firestore.collection("UserDetails").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String getUserFirstName = document.getString("userName");
                                    String getUserMobile = document.getString("userMobile");
                                    String getUserEmail = document.getString("userEmail");
                                    String getUserCity = document.getString("userAddress");
                                    String getUserImage = document.getString("ProfileImage");

                                    user_name.setText(getUserFirstName);
                                    user_mobile.setText(getUserMobile);
                                    user_email.setText(getUserEmail);
                                    user_city.setText(getUserCity);

                                    if (getUserImage != null) {
                                        Picasso.with(getActivity()).load(getUserImage).fit().into(profile_image);
                                    } else {
                                        profile_image.setImageResource(R.drawable.long_drive_logo);
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "User does not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Error getting user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        return root;
    }
}