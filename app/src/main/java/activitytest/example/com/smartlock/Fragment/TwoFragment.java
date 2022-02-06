package activitytest.example.com.smartlock.Fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import activitytest.example.com.smartlock.R;


import static activitytest.example.com.smartlock.Ble.SelectActivity.mBluetoothLeService;

public class TwoFragment extends Fragment {


    public String mServiceUUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    public String mCharaUUID_TX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    Button Setpassword,Setspace,Record,Freelock;

    CheckBox Autolock;

    String return_str;

    public TwoFragment() {

        // Required empty public constructor
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

          Setpassword = (Button) getActivity().findViewById(R.id.set_password_btn);// Inflate the layout for this fragment
           Setspace = (Button) getActivity().findViewById(R.id.set_space_btn);
           Record = (Button) getActivity().findViewById(R.id.record_btn);
           Freelock = (Button) getActivity().findViewById(R.id.unlock_warning_btn);
           Autolock = (CheckBox) getActivity().findViewById(R.id.auto_lock);

           if(return_str.equals("A"))
           {
               Setspace.setVisibility(View.VISIBLE);
               Setpassword.setVisibility(View.VISIBLE);
               Record.setVisibility(View.VISIBLE);
               Freelock.setVisibility(View.VISIBLE);
           }
           else
           {
               Setspace.setVisibility(View.INVISIBLE);
               Setpassword.setVisibility(View.INVISIBLE);
               Record.setVisibility(View.INVISIBLE);
               Freelock.setVisibility(View.INVISIBLE);

           }



        Setpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(return_str.equals("A") ) {
                    mBluetoothLeService.writeCustomCharacteristic("P", mServiceUUID, mCharaUUID_TX);
                }

            }
        });

        Setspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(return_str.equals("A")) {
                    mBluetoothLeService.writeCustomCharacteristic("W", mServiceUUID, mCharaUUID_TX);
                }
            }
        });
        Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(return_str.equals("A")) {
                    mBluetoothLeService.writeCustomCharacteristic("R", mServiceUUID, mCharaUUID_TX);
                }
            }
        });
        Freelock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    mBluetoothLeService.writeCustomCharacteristic("F", mServiceUUID, mCharaUUID_TX);
                }
            }
        });


        Autolock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    mBluetoothLeService.writeCustomCharacteristic("U", mServiceUUID, mCharaUUID_TX);
                }
                else
                {
                    mBluetoothLeService.writeCustomCharacteristic("Q", mServiceUUID, mCharaUUID_TX);
                }


            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (isAdded()) {//判断Fragment已经依附Activity
            return_str = getArguments().getString("Iden");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return_str = getArguments().getString("Iden");
        return inflater.inflate(R.layout.fragment_setting, container, false);




    }
}