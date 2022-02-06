package activitytest.example.com.smartlock.Ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import activitytest.example.com.smartlock.R;
import activitytest.example.com.smartlock.ReceiverAdapter;

public class RecordActivity extends AppCompatActivity {

    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    private String mServiceUUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    private String mCharaUUID_TX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    private String mCharaUUID_RX ="6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    private String mDeviceName ,mDeviceAddress;

    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private boolean mConnected = true;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private Button Back;

    private ArrayList<String> messagesList=new ArrayList<>();


    private RecyclerView mRecyclerView;
    private ReceiverAdapter mReceiverAdapter;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);

            // mBluetoothLeService.readCustomDescriptor(mCharaUUID, mServiceUUID);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case BluetoothLeService.ACTION_GATT_CONNECTED:
                    mConnected = true;
                    updateConnectionState(R.string.connected_state);

                    //  invalidateOptionsMenu();
                    break;
                case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                    mConnected = false;
                    updateConnectionState(R.string.disconnected_state);
                    //  invalidateOptionsMenu();
                    break;
                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:

                    break;
                case BluetoothLeService.ACTION_DATA_AVAILABLE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  Toast.makeText(context, "Data Received!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    messagesList.add(0,intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    mReceiverAdapter.notifyItemInserted(0);
                    mRecyclerView.scrollToPosition(0);


                  //  displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    break;
                case BluetoothLeService.ACTION_DESCRIPTOR_AVAILABLE:


                    Log.i("Receiving data", "Broadcast received");
                    displayDescriptor(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list);

        final Intent intent = getIntent();
        Log.i("OnCreate", "Created");
        mDeviceName = intent.getStringExtra("DEVICE_NAME");
        mDeviceAddress = intent.getStringExtra("DEVICE_ADDRESS");

        Back =(Button)findViewById(R.id.back_btn);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mReceiverAdapter =new ReceiverAdapter(this, messagesList));

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //messagesList.remove(0);
                int count = mReceiverAdapter.getItemCount();

                mRecyclerView.removeAllViews();
                messagesList.clear();
                mReceiverAdapter.notifyItemRangeRemoved(0, count) ;
                finish();
            }
        });

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            //mDataField.setText(data);
        }
    }

    private void displayDescriptor(final String data) {
        if( data != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // mCharaDescriptor.setText(mCharaDescriptor.getText().toString() + "\n" + data);
                }
            });
        }
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DESCRIPTOR_AVAILABLE);
        return intentFilter;
    }



}


