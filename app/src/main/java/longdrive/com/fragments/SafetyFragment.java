package longdrive.com.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Objects;

import longdrive.com.R;
import longdrive.com.activities.SafetyTips;

public class SafetyFragment extends Fragment {

    RelativeLayout messageSupport,safetyTips,ambulance,police;
    LinearLayout ll1,ll2;
    Button backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_safety, container, false);

        ambulance=root.findViewById(R.id.ambulance);
        police=root.findViewById(R.id.police);
        safetyTips=root.findViewById(R.id.safetyTips);
        messageSupport=root.findViewById(R.id.messageSupport);
        ll1=root.findViewById(R.id.ll1);
        ll2=root.findViewById(R.id.ll2);
        backBtn=root.findViewById(R.id.backBtn);


        ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialEmergencyNumber("tel:102");
            }
        });
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialEmergencyNumber("tel:100");
            }
        });
        safetyTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.INVISIBLE);
                ll2.setVisibility(View.VISIBLE);

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

    private void dialEmergencyNumber(String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(phoneNumber));

        if (dialIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(dialIntent);
        }
    }
}