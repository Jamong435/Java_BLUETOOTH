package com.kim9212.ex74bluetooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public class clientactivity extends AppCompatActivity {

    //블루투스 하드웨어 장치에 대한 식별자 UUID
    static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    TextView tv;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;

    DataInputStream dis;
    DataOutputStream dos;

    ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientactivity);
        //제목줄 변경
        getSupportActionBar().setTitle("CLIENT");
        tv = findViewById(R.id.tv);

        //블루투스 관리자 객체 소환
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "이 기기에는 블루투스가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //블루투스가 켜져있는지
        if (bluetoothAdapter.isEnabled()) {
            //서버 블루투스장치를 탐색 및 리스트로 보여주는 화면(액티비티 - 만들어야 함) 실행
            discoveryBluetoothDevices();
        } else {
            //블루투스 장치 On 하는 화면 실행
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 10);
        }

    }//onCreate method...

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "사용불가", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //서버 블루투스 장치 탐색 및 리스트 보는 화면 실행
                    discoveryBluetoothDevices();
                }
                break;

            case 20:
                if (resultCode == RESULT_OK) {
                    // 선택된 BT 디바이스의 Mac주소 얻어오기
                    String address = data.getStringExtra("Address");
                    //이제 선택된 mac주소를 이용해서 socket생성
                    //통신작없은 별도 스레드가..
                    clientThread = new ClientThread(address);
                    clientThread.start();

                }
                break;

        }
    }

    //블루투스 장치 탐색화면(액티비티) 실행 메소드
    void discoveryBluetoothDevices() {
        Intent intent = new Intent(this, BTListactivtivty.class);
        startActivityForResult(intent, 20);
    }

    //inner class
    class ClientThread extends Thread {
        String address;

        public ClientThread(String address) {
            this.address = address;
        }

        @Override
        public void run() {

            //mac주소에 해당하는 블루투스 디바이스객체를 얻어온다.
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            //원격디바이스와 소켓연결작업수행
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(BT_UUID);
                socket.connect();//연결시도!!

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText("서버와연결되었습니다");
                    }
                });

                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                //스트림을 통해 원하는 데이터 주고받기
                dos.writeUTF("안녕하세여");//UTF는 한글도 가능한 문자열 인코딩방식
                dos.writeInt(50);
                dos.flush();
                dos.close();


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}