/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import activitytest.example.com.smartlock.BaseActivity;
import activitytest.example.com.smartlock.R;

import static activitytest.example.com.smartlock.ActivityCollector.finishAll;


//对于给定的ble设备，这个activity提供接口去连接，展示数据，service和characteris。
//The Activity communicates with {@code BluetoothLeService}, which in turn interacts with the Bluetooth LE API.
public class DeviceControlActivity extends BaseActivity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView devicd_name2;
    private TextView connect_state;

    private Button cancel_btn;
    private ProgressBar progressBar;

    private String mDeviceName;
    private String mDeviceAddress;
    /*private ExpandableListView mGattServicesList;*/
    private BluetoothLeService mBluetoothLeService;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private String mServiceUUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    private String mCharaUUID_TX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    private String mCharaUUID_RX ="6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    int discount = 0;

    // Code to manage Service lifecycle.
    // Code to manage Service lifecycle.
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
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    //BroadcastReceiver：从service中传回来的参数，从BluetoothLeService中Broadcast中传回来的参数
    //若广播是service状态的改变（连接，未连接，发现，获得data）
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //若广播是service状态的改变（连接，未连接，发现，获得data）
           /* if ((mNotifyCharacteristic == null) || ((0x10 | mNotifyCharacteristic.getProperties()) <= 0)){
                return;
            }*/
           /* DeviceControlActivity.this.mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);*/

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
               updateConnectionState(R.string.connected_state);
               progressBar.setVisibility(View.INVISIBLE);
                cancel_btn.setText("断开连接");

                //TODO 刚加上
              // displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
               updateConnectionState(R.string.disconnected_state);
                progressBar.setVisibility(View.VISIBLE);

                discount++;

                if(discount==1)
                {
                    setTitle("正在进行第一次重连...");
                    mBluetoothLeService.connect(mDeviceAddress);
                }
                if (discount == 2)
                {
                    setTitle("正在进行第二次重连...");
                    mBluetoothLeService.connect(mDeviceAddress);
                }
                if (discount == 3)
                {
                    setTitle("正在进行第三次重连...");
                    mBluetoothLeService.connect(mDeviceAddress);
                }
                if (discount>=4)
                {
                    Toast.makeText(DeviceControlActivity.this,"连接超时，请确认再设备蓝牙范围内",Toast.LENGTH_SHORT).show();
                    finishAll();

                }


             //   invalidateOptionsMenu();
               // clearUI();
            }

            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                mBluetoothLeService.setCharacteristicNotification(mCharaUUID_RX,mServiceUUID,true);

              //  mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);
                Intent intent2 = new Intent(DeviceControlActivity.this, SelectActivity.class);
                intent2.putExtra("DEVICE_NAME",mDeviceName);
                intent2.putExtra("DEVICE_ADDRESS", mDeviceAddress);
                startActivity(intent2);
                //TODO 刚加上
               // displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //TODO在此处修改了，使得发现服务后直接开启获得数据

            }

        }
    };

    //设备未连接时清除界面内容
    private void clearUI() {
       /* mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);*/
      //  mDataField.setText(R.string.no_data);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_layout);

        final Intent intent = getIntent();

        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        // Sets up UI references.
        /*mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);*/
        /*mGattServicesList.setOnChildClickListener(servicesListClickListner);*/
          progressBar = findViewById(R.id.progressBar);
          connect_state = findViewById(R.id.connect_state);
         devicd_name2 = findViewById(R.id.device_name2);
          cancel_btn = findViewById(R.id.cancel_btn);

        connect_state.setText("等待中...");

        devicd_name2.setText(mDeviceName);

        Intent gattServiceIntent = new Intent(DeviceControlActivity.this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);



          cancel_btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (mConnected !=true  ){
            connect_state.setText("连接中...");
            mBluetoothLeService.connect(mDeviceAddress);
        }
        else{

            mBluetoothLeService.disconnect();
            finish();
        }

    }
});





    }

    //重启时、开始时注册广播
    @Override
    protected void onResume() {
        super.onResume();
        //注册广播
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

       if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
           Log.d(TAG, "Connect request result=" + result);
       }


    }

    //停止时，注销广播
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    //关闭activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    //判断是否连接，若未连接就展示没有连接的设备
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
   //     getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
      //      menu.findItem(R.id.menu_connect).setVisible(false);
        //    menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
     //       menu.findItem(R.id.menu_connect).setVisible(true);
        //    menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    //主菜单item被点击之后，intend获取来自deviceScanActivity的address和name从而进行连接
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     /*   switch(item.getItemId()) {
      //      case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
    */    return super.onOptionsItemSelected(item);
    }

    //显示当前连接状态
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect_state.setText(resourceId);
            }
        });
    }

    //显示传来的数据
    private void displayData(String data) {
        if (data != null) {
           /* mDataField.append(data);*/
           // mDataField.setText(data);
        }
    }




    //发送消息
    public void sendMsg(String paramString)
    {
       /* if ((mNotifyCharacteristic == null) || (paramString == null)){
            return;
        }
        if ((0x8 | mNotifyCharacteristic.getProperties()) <= 0){
            return;
        }*/
        byte[] arrayOfByte1 = new byte[20];
        byte[] arrayOfByte2 = new byte[20];
        arrayOfByte2[0] = 0;
        if (paramString.length() > 0){
            arrayOfByte1 = paramString.getBytes();
        }
       mNotifyCharacteristic.setValue(arrayOfByte2[0], 17, 0);
        mNotifyCharacteristic.setValue(arrayOfByte1);
   //     this.mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic);
      /*  byte[] arrayOfByte1 = new byte[20];
        byte[] arrayOfByte2 = new byte[20];
        arrayOfByte2[0] = 0;
        if (paramString.length() > 0)
            arrayOfByte1 = paramString.getBytes();
        //TODO我不懂这是啥意思
        mNotifyCharacteristic.setValue(arrayOfByte2[0], 0x11, 0);
        mNotifyCharacteristic.setValue(arrayOfByte1);
        this.mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic);*/
    }

    //注册广播时定义intent的各种属性
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}


