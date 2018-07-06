package com.homeguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.homeguide.utilities.Constants;
import com.homeguide.utilities.Utility;

import java.io.File;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utility.verifyStoragePermissions(this)){
            String root="";
            if(Utility.isExternalStorageAvailable()){
                root = Environment.getExternalStoragePublicDirectory("").toString();
            }
            File myDir = new File(root+"/Home Expense Guide/Database");
            if(!myDir.exists())
            myDir.mkdirs();

            startApp();
        }


    }

    private void startApp(){
        sharedPreferences=getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);

        if(sharedPreferences.getBoolean(Constants.IS_LOGGED_IN,false)){
            intent=new Intent(SplashActivity.this,MainActivity.class);
        }else {
            intent=new Intent(SplashActivity.this,RegisterActivity.class);
        }

        startActivity(intent);
        finish();

       /* new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
                // overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        }, 2000);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String root="";
                    if(Utility.isExternalStorageAvailable()){
                        root = Environment.getExternalStoragePublicDirectory("").toString();
                    }
                    File myDir = new File(root+"/Home Expense Guide/Database");
                    myDir.mkdirs();

                    startApp();
                } else {
                    finish();
                }
                break;
        }
    }

}
