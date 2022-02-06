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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import activitytest.example.com.smartlock.LeDeviceListAdapter;
import activitytest.example.com.smartlock.R;

//用来扫描可用设备并将其展示出来的activity
@SuppressLint("NewApi")
public class DeviceScanActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private ListView list;
    private Button Refresh;


    boolean CheckAuto = false;

    List<BluetoothDevice> deviceList = new ArrayList<>();


    private static final int REQUEST_ENABLE_BT = 1;
    // 5秒后停止查找搜索.
    private static final long SCAN_PERIOD = 5000;
    int count = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ble_list);
        //  getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();



        Refresh = (Button)findViewById(R.id.refresh_btn);
        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanLeDevice(true);

            }
        });

        Intent intent = getIntent();
        CheckAuto = intent.getExtras().getBoolean("CheckAuto");
        if (CheckAuto)
        {
            setTitle("正在自动搜索设备");
        }
        else
        {
            setTitle("正在扫描附件BLE设备");
        }

        ListView list =  (ListView) findViewById(R.id.list);
        mLeDeviceListAdapter = new LeDeviceListAdapter(DeviceScanActivity.this);
       // list.setAdapter(mLeDeviceListAdapter);

        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,"设备不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //TODO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(this, "自Android 6.0开始需要打开位置权限才可以搜索到Ble设备", Toast.LENGTH_SHORT).show();
                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }

        Boolean haha =isLocationEnable(this);
        if (!haha){
            setLocationService();
        }
        list.setAdapter(mLeDeviceListAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                System.out.println("==position==" + position);
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null) return;
                final Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                if (mScanning) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                startActivity(intent);
            }
        });
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }
*/
    @Override
    protected void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Initializes list view adapter.

      //  mLeDeviceListAdapter = new LeDeviceListAdapter(DeviceScanActivity.this);
       // list.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true); // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
      //  Toast.makeText(this, "开始扫描", Toast.LENGTH_SHORT).show();

    }

    @Override
    //跳转到devicecontrol进行连接的返回值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 用户没有开启蓝牙

        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (isLocationEnable(this)) {
                //定位已打开的处理
                Toast.makeText(this, "定位已经打开", Toast.LENGTH_SHORT).show();

            } else {
                //定位依然没有打开的处理
                Toast.makeText(this, "定位没有打开", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        /*if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }*/

        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    protected void onPause() {
        super.onPause();
      //  list.setAdapter(mLeDeviceListAdapter);
      //  Toast.makeText(this, "停止扫描", Toast.LENGTH_SHORT).show();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    //主页面的item被点击后，获取address和name发送到deviceControl里面进行连接

    //搜索函数，反馈是mLeScanCallback
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    //BluetoothLeScanner mBluetoothLeScanner = new BluetoothLeScanner();
                    mBluetoothAdapter.stopLeScan(DeviceScanActivity.this.mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(DeviceScanActivity.this.mLeScanCallback);
        } else {

            mScanning = false;
            mBluetoothAdapter.stopLeScan(DeviceScanActivity.this.mLeScanCallback);
        }
        invalidateOptionsMenu();
    }



    // 搜索函数更新到主线程来更新UI界面
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            //TODO 网上说不一样
            if (Looper.myLooper() == Looper.getMainLooper()) {
                // Android 5.0 及以上
              //  Toast.makeText(DeviceScanActivity.this, "搜索到设备", Toast.LENGTH_SHORT).show();

                mLeDeviceListAdapter.addDevice(device);

                mLeDeviceListAdapter.notifyDataSetChanged();
               // list.setAdapter(mLeDeviceListAdapter);不可以在这使用

               if (CheckAuto){
                   String name = device.getName();
                   if (name.equals("SmartLock")) {
                       final Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
                       intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                       intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                       if (mScanning) {
                           mBluetoothAdapter.stopLeScan(mLeScanCallback);
                           mScanning = false;
                       }
                       startActivity(intent);
                   }
                   else
                       {
                           count++;
                           if (count>4) //自行调整，如果N次找不到设备
                           {
                               CheckAuto = false;
                               Toast.makeText(DeviceScanActivity.this, "没有找到设备，请切换手动搜索", Toast.LENGTH_SHORT).show();
                               count =0;
                               finish();

                           }

                       }
               }


            } else {
                // Android 5.0 以下
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLeDeviceListAdapter.addDevice(device);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                });
            }



        }
    };



    //TODO 执行完上面的请求权限后，系统会弹出提示框让用户选择是否允许改权限。选择的结果可以在回到接口中得知：
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户允许改权限，0表示允许，-1表示拒绝 PERMISSION_GRANTED = 0， PERMISSION_DENIED = -1
                //permission was granted, yay! Do the contacts-related task you need to do.
                //这里进行授权被允许的处理
            } else {
                //permission denied, boo! Disable the functionality that depends on this permission.
                //这里进行权限被拒绝的处理
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //判断定位
    public static final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) return true;
        return false;
    }

    private void setLocationService() {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
    }


}