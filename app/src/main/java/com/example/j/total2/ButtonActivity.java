package com.example.j.total2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ButtonActivity extends AppCompatActivity {
    int mPairedDeviceCount = 0;
    static final int REQUEST_ENABLE_BT = 10;

    Set<BluetoothDevice> mDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    Thread mWorkerThread = null;
    Button blop, blcl, wdop, wdcl;
    Button Bbus, Nalssi;
    TextView btname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blop = (Button) findViewById(R.id.blop);
        blcl = (Button) findViewById(R.id.blcl);
        wdop = (Button) findViewById(R.id.wdop);
        wdcl = (Button) findViewById(R.id.wdcl);
        Bbus = (Button) findViewById(R.id.Bbus);
        Nalssi = (Button) findViewById(R.id.Nalssi);
        btname = (TextView) findViewById(R.id.btname);

        blop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mOutputStream.write('3');
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "데이터 전송중 오류", Toast.LENGTH_LONG).show();
                }
            }
        });
        blcl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mOutputStream.write('4');
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "데이터 전송중 오류", Toast.LENGTH_LONG).show();
                }
            }
        });
        wdop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mOutputStream.write('1');
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "데이터 전송중 오류", Toast.LENGTH_LONG).show();
                }
            }
        });
        wdcl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mOutputStream.write('2');
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "데이터 전송중 오류", Toast.LENGTH_LONG).show();
                }
            }
        });
        Bbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),BusActivity.class);
                startActivity(intent);
            }
        });
        Nalssi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),WeatherActivity.class);
                startActivity(intent);
            }
        });

        checkBluetooth();
    }

    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;
        for (BluetoothDevice device : mDevices) {
            if (name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    //  connectToSelectedDevice() : 원격 장치와 연결하는 과정
    //  실제 데이터 송수신을 위해 소켓으로부터 입출력 스트림 얻음, 입출력 스트림 이용
    void connectToSelectedDevice(String selectedDeviceName) {
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {
            //소켓생성, RFCOMM 채널을 통한 연결(?)
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 소켓 생성
            //이 메소드 성공시 폰과 페어링 된 다바이스간 통신 채널에 대흥하는 블루투스 소켓 오브젝트 리턴
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect(); //소켓 생성시 connect() 함수 호출 두 기기 연결 완료

            //스트림 얻기
            //블루투스소켓 오브젝트는 두개의 스트림 제공
            //1. 데이터 보내기 위한 아웃풋 스트림
            //2. 데이트 받기 위한 인풋 스트림
            mOutputStream = mSocket.getOutputStream();
            /*mInputStream = mSocket.getInputStream();*/

            /*// 데이터 수신 준비
            beginListenForData();*/
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결중 오류 발생", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    //블루투스를 지원하며 활성 상태인 경우
    public void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용전 페어링 되어야함
        // getBondedDevices() : 페어링된 장치 목록을 얻어오는 함수
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();

        if (mPairedDeviceCount == 0) { //페어링 된 장치가 없는 경우
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            btname.setText("페어링된 장치가 없습니다.");
            finish();
        }
        //페어링된 장치가 있는경우
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        btname.setText("블루투스 장치 선택");
        builder.setTitle("블루투스 장치 선택");

        //각 디바이스는 서로다른 이름과 주소를 가진다 페어링된 디바이스들을 표시한다.
        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            //device.getName() : 단말기의 Bluetooth Adapter 이름을 반환
            listItems.add(device.getName());
        }
        listItems.add("취소"); // 취소 항목 추가.

        // CharSequence : 변경가능한 문자열
        // toArray : List형태로 넘어온 것 배열로 바꿔서 처리하기 위한 toArray() 함수
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        //toArray 함수를 이용해서 size만큼 배열 생성
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //TODO Auto-generated method stub
                if (item == mPairedDeviceCount) { // 연결할 장치를 선택하지 않고 취소를 누른 경우
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않음", Toast.LENGTH_LONG).show();
                    btname.setText("연결할 장치를 선택하지 않았습니다.");
                    //finish();
                } else { //연결할 장치를 선택한 경우. 선택한 장치와 연결을 시도함
                    connectToSelectedDevice(items[item].toString());
                    btname.setText("연결된 장치 : ");
                    btname.append(items[item].toString());
                }
            }
        });

        builder.setCancelable(false); // 뒤로가기 버튼 사용 금지
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void checkBluetooth() {
        // getDefaultAdapter() : 폰에 블루투스 모듈이 없으면 null 리턴 에러메세지 표시하고 앱종료
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) { // 블루투스 미지원일 경우
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않음", Toast.LENGTH_LONG).show();
            finish();
        } else { // 블루투스 지원할 경우
            // isEnable() : 블루투스 모듈이 활성화 되었는지 확인
            // true : 지원 / false ; 미지원
            if (!mBluetoothAdapter.isEnabled()) { //블루투스 지원하며 비활성화 상태일경우
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성화 상태입니다.", Toast.LENGTH_LONG).show();
                btname.setText("현재 블루투스가 비활성화 상태입니다.");
                Intent enalbeBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // Request_Enalbe_bt : 블루투스 활성상태의 변경 결과를 app 으로 알려줄때 식별자로 사용
                //startActivityForResult 함수 호출후 다이얼로그가 나타남
                // 예를 선택하면 시스템 블루투스 장치를 활성화 시키고
                // 아니오를 선택하면 비활성화상태 유지
                // 선택결과는 onActivityResult 콜백 함수에서 확인가능
                startActivityForResult(enalbeBtIntent, REQUEST_ENABLE_BT);

            } else { // 블루투스 지원하며 활성상태인 경우
                selectDevice();
            }
        }
    }
    // 블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌

    public void onDestroy () {
        try {
            mWorkerThread.interrupt(); //데이터 수신 쓰레드 종료
            mOutputStream.close();
            mSocket.close();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    // onActivityResult : 사용자의 선택결과 확인 (아니오, 예)
    // result_ok : 블루투스 활성화 상태로 변경된 경우 "예"
    // result_canceled : 오류나 사용자의 "아니오" 선택으로 비활성화 상태로 남아있는 경우

    // 사용자가 request를 허가(혹은 거부)하면 안드로이드 앱의 onActivityResult 메소드를 호출해서 request의 허가/거부를 확인할수있다.
    // 첫번째 requestCode : startActivityForResult 에서 사용했던 요청 코드. Request_enable_bt값
    // 두번째 resultcode : 종료된 액태비티가 setResult로 지정한 결과코드. result_ok,canceled 값중 하나 들어감
    // 세번째 data : 종료된 액태비티가 인텐트를 첨부했을 경우, 그 인텐트가 들어있고 첨부하지 않으면 null

    public void onActivityResult ( int requestCode, int resultCode, Intent data){
        //startActivityForResult를 여러번 사용한 땐 switch문 사용하여 어떤 요청인지 구분
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) { //블루투스 활성화 상태
                    selectDevice();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "블루투스를 사용할 수 없어 창문 컨트롤 불가\n(블루투스 데이터 전송을 사용할 경우 앱이 종료됩니다.)", Toast.LENGTH_LONG).show();
                    btname.setText("블루투스를 사용할 수 없어 창문 컨트롤 불가\n 데이터를 전송할 경우 앱이 종료됩니다.");
                    //finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
