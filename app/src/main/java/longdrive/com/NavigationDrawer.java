package longdrive.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import longdrive.com.databinding.NavigationDrawerBinding;
import longdrive.com.driver_mode.fragmentsDriver.RideRequestFragment;
import longdrive.com.fragments.CityFragment;
import longdrive.com.user.EditProfile;
import longdrive.com.user.LoginActivity;

public class NavigationDrawer extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationDrawerBinding binding;
    SharedPreferences sp;
    BottomNavigationView bottomNavigationView;
    Button driverModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = NavigationDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = getSharedPreferences("login", MODE_PRIVATE);

        setSupportActionBar(binding.appBarNavigationDrawer.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        bottomNavigationView = findViewById(R.id.bottomNavView);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_profile, R.id.nav_city, R.id.nav_outstation, R.id.nav_requestHistory, R.id.nav_safety, R.id.nav_setting, R.id.nav_support, R.id.nav_faq, R.id.nav_freight,
                R.id.bottom_rideRequest, R.id.bottom_myIncome, R.id.bottom_rating)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        driverModeButton = navigationView.findViewById(R.id.driver_mode_button);
        driverModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDriverMode();
                drawer.closeDrawer(navigationView);
            }
        });

        // Display the default fragment when the activity starts
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_content_navigation_drawer, new CityFragment())
                    .commit();

            bottomNavigationView.setVisibility(View.INVISIBLE);
            bottomNavigationView.getLayoutParams().height = 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        if (id == R.id.edit_profile) {
            startActivity(new Intent(getApplicationContext(), EditProfile.class));
        }

        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void openDriverModeFragment() {
        Fragment driverModeFragment = new RideRequestFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_navigation_drawer, driverModeFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        bottomNavigationView.requestLayout();
        driverModeButton.setText("Passenger Mode");
    }

    private void toggleDriverMode() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_navigation_drawer);
        if (currentFragment instanceof RideRequestFragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_content_navigation_drawer, new CityFragment())
                    .commit();
            bottomNavigationView.setVisibility(View.INVISIBLE);
            bottomNavigationView.getLayoutParams().height = 0;
            driverModeButton.setText("Driver Mode");
        } else {
            openDriverModeFragment();
        }
    }
}

