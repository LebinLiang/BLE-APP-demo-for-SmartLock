package activitytest.example.com.smartlock.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import activitytest.example.com.smartlock.R;

import static activitytest.example.com.smartlock.Ble.SelectActivity.mBluetoothLeService;

public class OneFragment extends Fragment {

        private Button lock_btn,unlock_btn;
        private TextView lock_state;
    public String mServiceUUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    public String mCharaUUID_TX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";



    public OneFragment() {
        // Required empty public constructor
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lock_btn = (Button) getActivity().findViewById(R.id.lock_btn);
        unlock_btn =(Button) getActivity().findViewById(R.id.unlock_btn);
        lock_state = (TextView) getActivity().findViewById(R.id.lock_state);

        lock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.writeCustomCharacteristic("L", mServiceUUID, mCharaUUID_TX);
            }
        });

        unlock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.writeCustomCharacteristic("O", mServiceUUID, mCharaUUID_TX);
            }
        });

    }


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_menu, container, false);
    }
}