package activitytest.example.com.smartlock.Ble;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import activitytest.example.com.smartlock.BaseActivity;
import activitytest.example.com.smartlock.Login.Login_Admin_Activity;
import activitytest.example.com.smartlock.Login.Login_Visitor_Activity;
import activitytest.example.com.smartlock.R;


public class SelectActivity extends BaseActivity {
    private ImageButton admin,visitor;

    public SharedPreferences pref;

    public SharedPreferences.Editor editor;



    public String mServiceUUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    public String mCharaUUID_TX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    public String mCharaUUID_RX ="6e400003-b5a3-f393-e0a9-e50e24dcca9e";



    public static BluetoothLeService mBluetoothLeService;




    String send_str = "";

    public final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
          //  mBluetoothLeService.connect(mDeviceAddress);

            // mBluetoothLeService.readCustomDescriptor(mCharaUUID, mServiceUUID);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
  public final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case BluetoothLeService.ACTION_GATT_CONNECTED:
                 //   mConnected = true;
                  //  updateConnectionState(R.string.connected_state);

                    //  invalidateOptionsMenu();
                    break;
                case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                  //  mConnected = false;
                   // updateConnectionState(R.string.disconnected_state);
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

                //    Date date = new Date(System.currentTimeMillis());
                //    String timecheck = "";
               //     timecheck =simpleDateFormat.format(date);
                        String return_str ="";
                        return_str = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                        if (return_str.equals("N") || return_str.equals("N\r\n"))
                        {
                            setTitle("正在验证");
                          admin.setEnabled(false);
                          visitor.setEnabled(false);

                            if ( send_str.equals("A"))
                            {
                                Intent intent2 = new Intent(SelectActivity.this,Login_Admin_Activity.class);
                                startActivity(intent2);
                            }
                            else
                            {
                                Intent intent3 = new Intent(SelectActivity.this,Login_Visitor_Activity.class);
                                startActivity(intent3);
                            }

                        }
                        else if (return_str.equals("A") || return_str.equals("A\r\n"))
                        {
                            Toast.makeText(SelectActivity.this, "管理员身份验证成功", Toast.LENGTH_SHORT).show();
                            Intent intent5 = new Intent(SelectActivity.this, MenuActivity.class);
                            intent5.putExtra("send_str", "A");
                            startActivity(intent5);
                        }
                    else if (return_str.equals("V") || return_str.equals("V\r\n"))
                        {
                            Toast.makeText(SelectActivity.this, "访客身份验证成功", Toast.LENGTH_SHORT).show();

                            Intent intent4 = new Intent(SelectActivity.this, MenuActivity.class);
                            intent4.putExtra("send_str", "V");//验证成功，将信息传递到menu然后设置设置按钮能否显示。
                            startActivity(intent4);
                        }

                        else if (return_str.equals("5") || return_str.equals("5\r\n"))
                        {

                            Toast.makeText(SelectActivity.this, "密码错误过多", Toast.LENGTH_SHORT).show();
                            admin.setEnabled(true);
                            visitor.setEnabled(true);
                        }
                        else if(return_str.equals("6") || return_str.equals("6\r\n"))
                        {
                            Toast.makeText(SelectActivity.this, "密码错误过多，退出登陆", Toast.LENGTH_SHORT).show();
                        }
                    break;
                case BluetoothLeService.ACTION_DESCRIPTOR_AVAILABLE:


                    Log.i("Receiving data", "Broadcast received");

                default:
                    break;
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.select_layout);
       admin = findViewById(R.id.button_admin);
        visitor = findViewById(R.id.button_visitor);
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        admin.setEnabled(true);
        visitor.setEnabled(true);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              send_str = "A";

                mBluetoothLeService.writeCustomCharacteristic(send_str, mServiceUUID, mCharaUUID_TX);

          ;
            }
        });

        visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                send_str = "V";

                mBluetoothLeService.writeCustomCharacteristic(send_str, mServiceUUID, mCharaUUID_TX);

            }
        });
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }




    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());


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



    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DESCRIPTOR_AVAILABLE);
        return intentFilter;
    }



}
