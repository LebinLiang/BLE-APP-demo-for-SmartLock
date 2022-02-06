package activitytest.example.com.smartlock.Ble;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingFormatArgumentException;

import activitytest.example.com.smartlock.Ble.BluetoothLeService;
import activitytest.example.com.smartlock.Ble.SelectActivity;
import activitytest.example.com.smartlock.ChangePasswordActivity;
import activitytest.example.com.smartlock.Fragment.OneFragment;
import activitytest.example.com.smartlock.Fragment.ThreeFragment;
import activitytest.example.com.smartlock.Fragment.TwoFragment;
import activitytest.example.com.smartlock.Login.Login_Admin_Activity;
import activitytest.example.com.smartlock.Login.Login_Visitor_Activity;
import activitytest.example.com.smartlock.R;

public class MenuActivity extends SelectActivity {
    private TextView item_menu, item_setting, item_about;
    private ViewPager vp;
    private OneFragment oneFragment;
    private TwoFragment twoFragment;
    private ThreeFragment threeFragment;
    private SimpleDateFormat simpleDateFormat;

    private TextView lock_state;

    public String mServiceUUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    public String mCharaUUID_TX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    public String mCharaUUID_RX ="6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;

    String return_str = "";

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
                    String return_str ="";
                    return_str = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);

                    if(return_str.equals("O") || return_str.equals("O\r\n"))
                    {
                        setTitle("当前状态：已开锁");
                    }
                    else if(return_str.equals("L") || return_str.equals("L\r\n"))
                    {
                        setTitle("当前状态：已上锁");
                    }
                    else if(return_str.equals("1") || return_str.equals("1\r\n"))
                    {
                        setTitle("请旋转到开锁位置，然后点击确定");
                    }
                    else if(return_str.equals("2") || return_str.equals("2\r\n"))
                    {
                        setTitle("开锁位置设置成功");
                    }
                    else if (return_str.equals("3") || return_str.equals("3\r\n"))
                    {
                        setTitle("请旋转到上锁位置，然后点击确定");
                    }
                    else if (return_str.equals("4") || return_str.equals("4\r\n"))
                    {
                        setTitle("上锁位置设置成功");
                    }
                    else if (return_str.equals("7") || return_str.equals("7\r\n"))  //一代原型机功能
                    {
                        setTitle("请旋转至上锁尽头，然后点击确定");
                    }
                    else if (return_str.equals("8") || return_str.equals("8\r\n")) //一代原型机功能
                    {
                        setTitle("上锁尽头设置成功");
                    }
                    else if (return_str.equals("P") || return_str.equals("P\r\n"))
                    {
                        //setTitle("请输入新访客密码");
                        Intent intent7 = new Intent(MenuActivity.this, ChangePasswordActivity.class);
                        startActivity(intent7);
                    }
                   else if (return_str.equals("E"))
                    {
                        setTitle("出现未知错误，请重启设备并旋转至开锁位置");
                    }
                    else if (return_str.equals("R") || return_str.equals("R\r\n"))
                    {
                        Intent intent6 = new Intent(MenuActivity.this,RecordActivity.class);
                        startActivity(intent6);
                    }
                    else if(return_str.equals("F") || return_str.equals("F\r\n"))
                    {
                        setTitle("串口调试");
                        Intent intent7 = new Intent(MenuActivity.this,RxTxActivity.class);
                        startActivity(intent7);
                    }
                    else if ( return_str.equals("0")|| return_str.equals("0\r\n"))//0123
                    {
                        setTitle("新访客密码设置成功");
                    }
                    else if (return_str.equals("U"))
                    {
                        setTitle("开启自动上锁模式");
                    }
                    else if (return_str.equals("Q"))
                    {
                        setTitle("关闭自动上锁模式");
                    }
                    else if (return_str.equals("K"))
                    {
                        setTitle("设备电量不足，请及时充电");
                    }
                    else if (return_str.equals("T"))
                    {
                        Date date = new Date(System.currentTimeMillis());
                        String timeupdata = "";
                        timeupdata =simpleDateFormat.format(date);

                        mBluetoothLeService.writeCustomCharacteristic(timeupdata, mServiceUUID, mCharaUUID_TX);
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
        //去除工具栏
        // getSupportActionBar().hide();
        setContentView(R.layout.activity_menu);

        initViews();
        Intent intent = getIntent();

            return_str = intent.getStringExtra("send_str");

            if (return_str.equals("A"))
            {
                Bundle bundle = new Bundle();
                bundle.putString("Iden","A");//这里的values就是我们要传的值
                twoFragment.setArguments(bundle);
            }
            else {
                Bundle bundle = new Bundle();
                bundle.putString("Iden", "V");//这里的values就是我们要传的值
                twoFragment.setArguments(bundle);
            }

        simpleDateFormat = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");// HH:mm:ss


        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);

        vp.setOffscreenPageLimit(4);//ViewPager的缓存为4帧
        vp.setAdapter(mFragmentAdapter);
        vp.setCurrentItem(0);//初始设置ViewPager选中第一帧
        item_menu.setTextColor(Color.parseColor("#66CDAA"));

        //ViewPager的监听事件
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*此方法在页面被选中时调用*/
                //   title.setText(titles[position]);
                changeTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                /*此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。
                arg0 ==1的时辰默示正在滑动，
                arg0==2的时辰默示滑动完毕了，
                arg0==0的时辰默示什么都没做。*/
            }
        });

        //getFragmentManager().findFragmentById(id).getView().findViewById(id)


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    /**
     * 初始化布局View
     */
    private void initViews() {
        //  title = (TextView) findViewById(R.id.title);
        item_menu = findViewById(R.id.item_menu);
        item_setting = findViewById(R.id.item_setting);
        item_about = findViewById(R.id.item_about);


        vp = (ViewPager) findViewById(R.id.mainViewPager);
        oneFragment = new OneFragment();
        twoFragment = new TwoFragment();
        threeFragment = new ThreeFragment();

        //给FragmentList添加数据
        mFragmentList.add(oneFragment);
        mFragmentList.add(twoFragment);
        mFragmentList.add(threeFragment);

        item_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(0, true);
            }
        });
        item_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(1, true);
            }
        });
        item_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(2, true);
            }
        });


    }

    /**
     * 点击底部Text 动态修改ViewPager的内容
     */

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_menu:
                vp.setCurrentItem(0, true);
                break;
            case R.id.item_setting:
                vp.setCurrentItem(1, true);
                break;
            case R.id.item_about:
                vp.setCurrentItem(2, true);
                break;

        }
    }


    public class FragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<Fragment>();

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

    /*
     *由ViewPager的滑动修改底部导航Text的颜色
     */
    private void changeTextColor(int position) {
        if (position == 0) {
            item_menu.setTextColor(Color.parseColor("#66CDAA"));
            item_setting.setTextColor(Color.parseColor("#000000"));
            item_about.setTextColor(Color.parseColor("#000000"));

        } else if (position == 1) {
            item_setting.setTextColor(Color.parseColor("#66CDAA"));
            item_menu.setTextColor(Color.parseColor("#000000"));
            item_about.setTextColor(Color.parseColor("#000000"));

        } else if (position == 2) {
            item_about.setTextColor(Color.parseColor("#66CDAA"));
            item_menu.setTextColor(Color.parseColor("#000000"));
            item_setting.setTextColor(Color.parseColor("#000000"));

        }

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
