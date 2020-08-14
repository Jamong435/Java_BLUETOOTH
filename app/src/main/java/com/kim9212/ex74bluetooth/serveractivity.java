package com.kim9212.ex74bluetooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class serveractivity extends AppCompatActivity {

    //블루투스 하드웨어 장치에 대한 식별자 uuid
    static final UUID BT_UUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    TextView tv;
    BluetoothAdapter bluetoothAdapter;
    BluetoothServerSocket serversocket;
    BluetoothSocket socket;

    //데이터를 주고받기 위한 스트림(자료형 단위로 보낼수있는 stream)
    DataOutputStream dos;
    DataInputStream dis;

    Serverthread serverthread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serveractivity);
        getSupportActionBar().setTitle("server");
        tv = findViewById(R.id.tv);

        //블루투스 관리자객체 소환
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "이기계에는 블루투스기능이없어여", Toast.LENGTH_SHORT).show();
            finish();//이렇게 했다고 바로 끝나지는않는다.....그래서 아래 return을 해야한다..
            return;
        }
        //위에서 리턴되지않았다면블루투스가 있다는것입니다.
        //블루투스 장치가 켜져있는지 체크 및 on하도록 요청해야한다
        if (bluetoothAdapter.isEnabled()) {
            //블루투스가 켜져있다면 서버소켓 생성 작업 실행
            creatserversocket();
        } else {
            //블루투스 장치를 on선택하도록 하는 화면으로 전환
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//물어라 킬건지
            startActivityForResult(intent, 100);//인텐트아저씨한테 켜진걸 받아와야한다
        }


    }//oncreate method..

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_CANCELED) {
                    //블루투스 켰다면 서버소켓 생성작업
                    Toast.makeText(this, "블루투스를 허용하지않음\n앱을종료합니다", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    creatserversocket();
                }
                break;

            case 200:
                if(resultCode==RESULT_CANCELED){
                    Toast.makeText(this,"블루투스탐색허용하지않습니다\n이장치를 찾을수가없습니다",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //서버소켓 생성 작업 메소드
    void creatserversocket() {
        //통신작업은 반드시 별도의 Thread가 해야함.
        serverthread=new Serverthread();
        serverthread.start();

        //이 기기를 다른 장치에서 검색할수 있도록 하는것(액티비티)이 실행 필요함
        Intent intent= new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);//300초간 검색허용

        startActivityForResult(intent,200);

    }
    //서버소켓작업및 통신을 하는 별도 thread클래스:inner class
    class Serverthread extends Thread{
        @Override
        public void run() {
            try {
                serversocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord("SERVER",BT_UUID);//UUID 각 기능에는 아이디가 하나씩있다

                setUI("서버소켓이 생성되었습니다\n");
                //클라이언트의 접속을 기다리기
                socket=serversocket.accept();//커서가 여기서 대기하고있음..
                setUI("client가 접속하였습니다\n");
                //접속된 socket을 이용하여 통신하기위해
                //무지게로드 만들기
                dis=new DataInputStream(socket.getInputStream());
                dos= new DataOutputStream(socket.getOutputStream());
                //스트림을 통해 원하는 데이터 전송하거나 받거나
                String msg=dis.readUTF();
                int num= dis.readInt();

                setUI("클라이언트:"+msg+"--"+num);

                dis.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //ui thread로 메세지 출력하는 기능
        void setUI(final String msg){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.append(msg);
                }
            });
        }
    }

}