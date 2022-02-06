package activitytest.example.com.smartlock.Login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import activitytest.example.com.smartlock.BaseActivity;
import activitytest.example.com.smartlock.Ble.SelectActivity;
import activitytest.example.com.smartlock.R;


public class Login_Admin_Activity extends SelectActivity {

    private CheckBox rememberPass;


    public SharedPreferences.Editor editor;
    private Button ture_btn;

    private Button back_btn;

    private EditText passwordEdit;

    boolean check= false;

    @Override
       public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__admin_);

        passwordEdit =(EditText)findViewById(R.id.editText_admin);
        rememberPass =(CheckBox)findViewById(R.id.checkBox_admin);
        ture_btn = (Button)findViewById(R.id.true_btn_admin);
        back_btn =(Button)findViewById(R.id.back_btn_admin);
        boolean isRemeber = pref.getBoolean("remember_password",false);

        if (isRemeber){
            String password = pref.getString("password","");
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }
        rememberPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                String password = passwordEdit.getText().toString();
                if(isChecked) {

                    Toast.makeText(Login_Admin_Activity.this, "密码已被记住", Toast.LENGTH_SHORT).show();
                        check = true;

                }
                else{

                        check = false;

                    Toast.makeText(Login_Admin_Activity.this,"密码未被记住",Toast.LENGTH_SHORT).show();
                }

            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = pref.edit();
                String password = passwordEdit.getText().toString();
                int Length = passwordEdit.length();
                if (Length ==6) {

                    if (check)
                    {
                        editor.putBoolean("remember_password", true);
                        editor.putString("password", password);
                    }
                    else
                    {
                        editor.clear();
                    }
                    editor.apply();

                    mBluetoothLeService.writeCustomCharacteristic(password, mServiceUUID, mCharaUUID_TX);

                    finish();
                }
                else{
                    Toast.makeText(Login_Admin_Activity.this,"密码长度错误",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
