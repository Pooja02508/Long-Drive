package longdrive.com.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import longdrive.com.R;
import longdrive.com.activities.UserProfileActivity;

public class SupportFragment extends Fragment {

    RelativeLayout app_issues,about,question;
    RelativeLayout city,city_to_city,delivery,freight,myProfile;
    TextView title;
    LinearLayout ll1,ll2;
    Button backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_support, container, false);

        city=root.findViewById(R.id.city);
        city_to_city=root.findViewById(R.id.city_to_city);
        delivery=root.findViewById(R.id.delivery);
        freight=root.findViewById(R.id.freight);
        myProfile=root.findViewById(R.id.myProfile);
        title=root.findViewById(R.id.title);
        ll1=root.findViewById(R.id.ll1);
        ll2=root.findViewById(R.id.ll2);
        backBtn=root.findViewById(R.id.backBtn);

        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.INVISIBLE);
                ll2.setVisibility(View.VISIBLE);
                title.setText("City");
            }
        });
        city_to_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.INVISIBLE);
                ll2.setVisibility(View.VISIBLE);
                title.setText("City to city");
            }
        });
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.INVISIBLE);
                ll2.setVisibility(View.VISIBLE);
                title.setText("Delivery");
            }
        });
        freight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.INVISIBLE);
                ll2.setVisibility(View.VISIBLE);
                title.setText("Freight");
            }
        });
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserProfileActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll2.setVisibility(View.INVISIBLE);
                ll1.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }
}