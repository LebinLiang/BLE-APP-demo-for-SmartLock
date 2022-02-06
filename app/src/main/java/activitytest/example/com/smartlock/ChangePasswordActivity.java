package activitytest.example.com.smartlock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import activitytest.example.com.smartlock.Ble.SelectActivity;

/**
 * Created by pc on 2018/2/21.
 */

public class ChangePasswordActivity extends SelectActivity{

    private EditText cpassword;
    private Button done;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       //去除工具栏
       setContentView(R.layout.layout_change_password);

       setTitle("请输入新访客密码");
       cpassword = findViewById(R.id.cpassword_edit);
       done = findViewById(R.id.sure_button);
       done.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String password = cpassword.getText().toString();

               mBluetoothLeService.writeCustomCharacteristic(password, mServiceUUID, mCharaUUID_TX);
               cpassword.setText(null);
               finish();

           }
       });
       // getSupportActionBar().hide();





       }
    }
