package activitytest.example.com.smartlock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import activitytest.example.com.smartlock.Ble.DeviceScanActivity;
import activitytest.example.com.smartlock.Login.Login_Admin_Activity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button saomiao;
    private CheckBox rememberaddress;
    boolean CheckAuto = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saomiao = (Button) findViewById(R.id.saomiao);
        rememberaddress = (CheckBox)findViewById(R.id.remember_address);
        boolean isRemeber = pref.getBoolean("remember_address",false);


        if (isRemeber){

            rememberaddress.setChecked(true);
        }

        rememberaddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                editor = pref.edit();
                if(isChecked) {

                    CheckAuto = true ;
                    Toast.makeText(MainActivity.this, "App将自动连接控制设备", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("remember_address", true);


                }
                else{
                    CheckAuto = false;
                    Toast.makeText(MainActivity.this,"切换到手动寻找设备",Toast.LENGTH_SHORT).show();
                    editor.putBoolean("remember_address", false);
                }
                editor.apply();

            }
        });


        saomiao.setOnClickListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.about_item:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("·关于：");
                dialog.setMessage("APP名称;SmartLock\n"+"制作者：Michael_B\n"+"Wechat:13610140855\n"+"是否需要BLE设备:是\n"+"这是一个测试使用的APP，任何商业行为请联系制作者\n"+"BOULKOO 团队所有\n"+"更新时间：31/8/2018(兼容原型机）");
                dialog.setCancelable(true);
                dialog.show();
                break;
                default:

        }
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saomiao:


                Intent intent = new Intent(MainActivity.this,DeviceScanActivity.class);
               intent.putExtra("CheckAuto",CheckAuto);
                startActivity(intent);
                break;
            default:
                break;

        }
    }

}



