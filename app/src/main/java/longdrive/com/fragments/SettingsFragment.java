package longdrive.com.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import longdrive.com.R;
import longdrive.com.user.LoginActivity;
import longdrive.com.user.UserDetails;

public class SettingsFragment extends Fragment {

    SharedPreferences sp;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    TextView phoneNumber,deleteAccount,logout;
    UserDetails userDetails;
    String getUserEmail,getUserMobile,getUserPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_settings, container, false);

        phoneNumber=root.findViewById(R.id.phoneNumber);
        logout=root.findViewById(R.id.logout);
        deleteAccount=root.findViewById(R.id.deleteAccount);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userDetails = new UserDetails();

        sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        String userId = sp.getString("UserMobile", null);

        if (userId == null) {
            phoneNumber.setText("");
        } else {
            firestore.collection("UserDetails").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    getUserMobile = document.getString("userMobile");
                                    getUserEmail = document.getString("userEmail");
                                    getUserPassword = document.getString("userPassword");
                                    phoneNumber.setText(getUserMobile);

                                } else {
                                    Toast.makeText(getActivity(), "User does not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Error getting user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=sp.edit();
                editor.clear();
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(),"Logout",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current user from Firebase Authentication
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    // Prompt the user to re-authenticate before deleting the account
                    // (This is a security measure to ensure the user's identity)
                    AuthCredential credential = EmailAuthProvider.getCredential(getUserEmail, getUserPassword);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Re-authentication successful, now delete the account
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Account deleted successfully
                                                            Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_SHORT).show();
                                                            SharedPreferences.Editor editor=sp.edit();
                                                            editor.clear();
                                                            editor.apply();
                                                            FirebaseAuth.getInstance().signOut();
                                                            startActivity(new Intent(getActivity(), LoginActivity.class));
                                                            getActivity().finish();
                                                        } else {
                                                            // Failed to delete the account
                                                            Toast.makeText(getActivity(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Failed to re-authenticate
                                        Toast.makeText(getActivity(), "Re-authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });



        return root;
    }
}