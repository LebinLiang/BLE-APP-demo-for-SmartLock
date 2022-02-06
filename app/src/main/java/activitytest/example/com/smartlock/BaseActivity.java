package activitytest.example.com.smartlock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by pc on 2018/1/24.
 */



public class BaseActivity extends AppCompatActivity{
   public SharedPreferences pref;

    public SharedPreferences.Editor editor;
    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
      //  Log.d("BaseActivity",getClass().getSimpleName());
        ActivityCollector.addActivity(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
