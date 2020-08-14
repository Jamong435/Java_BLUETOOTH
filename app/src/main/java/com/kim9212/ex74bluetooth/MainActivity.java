package com.kim9212.ex74bluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Location에 대한 동적 퍼미션

        String permisson= Manifest.permission.ACCESS_FINE_LOCATION;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(checkSelfPermission(permisson)== PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{permisson},10);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 10:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED);
                Toast.makeText(this, "클라이언트에게 새로운장치를 검색하는 기능이 제한됩니다.\n 기존에 페일된장치는 접속가능합니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void clickserver(View view) {
        Intent intent= new Intent(this,serveractivity.class);
        startActivity(intent);




    }

    public void clickclient(View view) {
        Intent intent= new Intent(this,clientactivity.class);
        startActivity(intent);

    }
}