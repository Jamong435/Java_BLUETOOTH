package com.kim9212.ex74bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BTListactivtivty extends AppCompatActivity {

    ArrayList<String> deviceList = new ArrayList<>();
    ListView listView;
    ArrayAdapter adapter;

    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> devices;

    DiscoveryResultReceiver discoveryResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_t_listactivtivty);

        listView = findViewById(R.id.listview);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(adapter);

        //블루투스 아답터 소환
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //이미 페어링 되어있는 디바이스들을 리스트에 추가
        devices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            String name = device.getName();
            String address = device.getAddress();

            deviceList.add(name + "\n" + address);
        }

        //새로운 장치를 찾은 결과를 운영체제에서 Broadcast를 함
        //그러므로 이를 들으려면 Broadcast Receiver 가 필요함
        //Bluetooth의 장치검색 결과는 동적(Java언어 에서 등록한) Receiver만 가능함
        discoveryResultReceiver = new DiscoveryResultReceiver();

        //장치를 찾았다는 방송듣는 필터
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryResultReceiver, filter);//Manifest.xml말고 자바에서 리시버 등록

        //탐색이 종료되었다는 방송듣는 필터
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryResultReceiver, filter2);

        // 탐색 시작!!
        bluetoothAdapter.startDiscovery();


        //다이얼로그스타일 일때.
        //아웃사이드 터치했을때 cancel되지 않도록.
        setFinishOnTouchOutside(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = deviceList.get(position);
                //s문자열에 저장된 name과 mac주소중에 주소만 분리
                String[] ss = s.split("\n");
                String address = ss[1];

                //얻어온 address를 이 액티비티를 실행했던
                //clientactivitiy에 전달( 이액티비티에 실행했던 택배기사(intent)객체소환
                Intent intent = getIntent();
                //이택배기사에게 갖고 돌아갈 데이터를 추가시킨다
                intent.putExtra("Address", address);
                //이게 이 액티비티의 결과가 끝났다
                setResult(-1,intent);
                finish();
            }
        });

    }//onCreate method..

    //inner class..
    class DiscoveryResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                //장치를 찾은 상황
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device.getName() + "\n" + device.getAddress());
                adapter.notifyDataSetChanged();
                //장치들을 중복되지 않게 가지고 있는 Set객체
//                boolean isAdded= devices.add(device); //중복된 디바이스가 없다면 true리턴
//                if(isAdded){//새로운 장치라는 것임
//                    String name= device.getName();
//                    String address= device.getAddress();
//
//                    //리스트뷰에 보여줄 데이터에 추가
//                    deviceList.add(name+"\n"+address);
//                }

            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Toast.makeText(context, "블루투스 탐색을 완료했습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }


    //액티비티가 화면에서 안보일때 리시버 등록 해제
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(discoveryResultReceiver);
    }
}//class
