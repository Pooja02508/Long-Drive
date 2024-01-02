package longdrive.com.user;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import longdrive.com.R;

public class EditProfile extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    DocumentReference documentReference;

    EditText userName, userEmail, userLocation, userPassword;
    Button save, btnUpload;
    String getUserName, getUserEmail, getUserPassword, userId, getUserCity, getUserImage;
    SharedPreferences sp;
    CircleImageView profile_image;
    ImageView edit_profile;
    FirebaseStorage storage;
    ProgressBar progressBar;
    private Uri imageUri;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userLocation = findViewById(R.id.userLocation);
        userPassword = findViewById(R.id.userPassword);
        save = findViewById(R.id.save);
        progressBar = findViewById(R.id.progressBar);

        edit_profile = findViewById(R.id.edit_profile);
        profile_image = findViewById(R.id.profile_image);
        btnUpload = findViewById(R.id.uploadImage);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        userId = sp.getString("UserMobile", null);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("UserDetails").document(userId);

        if (userId == null) {
        } else {
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        getUserName = documentSnapshot.getString("userName");
                        getUserEmail = documentSnapshot.getString("userEmail");
                        getUserPassword = documentSnapshot.getString("userPassword");
                        getUserCity = documentSnapshot.getString("userAddress");
                        getUserImage = documentSnapshot.getString("ProfileImage");

                        userName.setText(getUserName);
                        userEmail.setText(getUserEmail);
                        userPassword.setText(getUserPassword);
                        userLocation.setText(getUserCity);

                        if (getUserImage != null) {
                            Picasso.with(EditProfile.this).load(getUserImage).fit().into(profile_image);
                        } else {
                            profile_image.setImageResource(R.drawable.ic_profile);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(EditProfile.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFirestoreData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profile_image.setImageURI(imageUri);
        }
    }

    private void uploadToFirebase(Uri uri) {
        final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        documentReference.update("ProfileImage", uri.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(EditProfile.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        profile_image.setImageURI(uri);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(EditProfile.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    private void updateFirestoreData() {
        if (isNameChanged()) {
            Toast.makeText(EditProfile.this, "User Name Changed", Toast.LENGTH_SHORT).show();
        } else if (isEmailChanged()) {
            Toast.makeText(EditProfile.this, "Email Changed", Toast.LENGTH_SHORT).show();
        } else if (isPasswordChanged()) {
            Toast.makeText(EditProfile.this, "Password Changed", Toast.LENGTH_SHORT).show();
        } else if (isAddressChanged()) {
            Toast.makeText(EditProfile.this, "Address Changed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EditProfile.this, "No Changes Found", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNameChanged() {
        if (!getUserName.equals(userName.getText().toString())) {
            documentReference.update("userName", userName.getText().toString());
            getUserName = userName.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isEmailChanged() {
        if (!getUserEmail.equals(userEmail.getText().toString())) {
            documentReference.update("userEmail", userEmail.getText().toString());
            getUserEmail = userEmail.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isPasswordChanged() {
        if (!getUserPassword.equals(userPassword.getText().toString())) {
            documentReference.update("userPassword", userPassword.getText().toString());
            getUserPassword = userPassword.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isAddressChanged() {
        if (!getUserCity.equals(userLocation.getText().toString())) {
            documentReference.update("userAddress", userLocation.getText().toString());
            getUserCity = userLocation.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

