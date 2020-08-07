package com.xinlan.mysipdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xinlan.mysipdemo.sip.SipApp;

public class MainActivity extends AppCompatActivity {
    private EditText mAccountText;
    private EditText mPwdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SipApp.getInstance().initSip();

        requestSipPermission();

        mAccountText = findViewById(R.id.account_text);
        mPwdText = findViewById(R.id.password_text);
        //requestSipPermission();
    }

    @Override
    protected void onDestroy() {
        SipApp.getInstance().destory();
        super.onDestroy();
    }

    private void requestSipPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            int request = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            System.out.println("请求权限 " + (request != PackageManager.PERMISSION_GRANTED));
            if (request != PackageManager.PERMISSION_GRANTED)//缺少权限，进行权限申请{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                return;//
            } else {
                //权限同意
            }
        }
    }//end
