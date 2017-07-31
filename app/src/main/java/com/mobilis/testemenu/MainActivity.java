package com.mobilis.testemenu;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_SEARCH_BT = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private static final int CONECTADO = 10;
    private static final int DESCONECTADO = 11;

    private BluetoothAdapter mBluetoothAdapter = null;

    private TextView titleBtResult;
    private TextView titleWifiResult;
    private TextView titlePasswordResult;
    private TextView titleStatus;

    private TextView btResult;
    private TextView wifiResult;
    private TextView passwordResult;
    private TextView status;

    private StringBuffer mOutStringBuffer;
    private BluetoothChatService mChatService = null;
    private ProgressDialog progress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }

        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        final Button btn_bt = (Button) findViewById(R.id.button_start);
        btn_bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                searchBT();

            }
        });

        titleBtResult = (TextView) findViewById(R.id.title_bt_result);
        titleWifiResult = (TextView) findViewById(R.id.title_wifi_result);
        titlePasswordResult = (TextView) findViewById(R.id.title_password_result);
        titleStatus = (TextView) findViewById(R.id.title_status);

        btResult = (TextView) findViewById(R.id.bt_result);
        wifiResult = (TextView) findViewById(R.id.wifi_result);
        passwordResult = (TextView) findViewById(R.id.password_result);
        status = (TextView) findViewById(R.id.status);

        mChatService = new BluetoothChatService(this, mHandler);

        mOutStringBuffer = new StringBuffer("");

    }

    public void searchBT(){
       // Log.i(TAG, "searchBT()");
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_SEARCH_BT);

    }

    public void searchWifi(){
       // Log.i(TAG, "searchWifi()");
        Intent serverIntent = new Intent(this, WifiListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        progress = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
        if (resultCode != 0) {
            if (requestCode == REQUEST_SEARCH_BT) {

                connectDevice(data, true);
                progress.setTitle(R.string.connecting);
                progress.setMessage(getResources().getText(R.string.waiting).toString());
                progress.setCancelable(true);
                progress.show();
            } else if (requestCode == REQUEST_CONNECT_DEVICE_SECURE) {

                sendMessage(WifiListActivity.info + "\n" + WifiListActivity.password + "\n \n");
                //setConnectedStatus(CONECTADO);
                progress.setTitle(R.string.configuring);
                progress.setMessage(getResources().getText(R.string.waiting).toString());
                progress.show();

            }
        }

    }

    private void sendMessage(String message) {
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
            mOutStringBuffer.setLength(0);
        }
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            progress.dismiss();
                            searchWifi();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            progress.show();
                            break;
                        case BluetoothChatService.STATE_NONE:
                            progress.dismiss();
                            break;
                    }
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //Log.i(TAG, "----------------mensagem no handler read");
                   // Log.i(TAG, String.valueOf(readMessage));
                    if(readMessage.equals("sucesso")){
                        //Log.i(TAG, "--------------- SUCESOOOO");
                        if (setConnectedStatus(CONECTADO)){
                            progress.dismiss();
                           // mChatService.stop();
                        }

                    } else if (readMessage.equals("falhou")){
                        //Log.i(TAG, "---------------- FALHOOU :(");
                        if (setConnectedStatus(DESCONECTADO)) {
                            progress.dismiss();
                          //  mChatService.stop();
                        }
                    }
                    break;
            }
        }
    };


    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device, secure);
    }

    private boolean setConnectedStatus(int mensagem){

        titleBtResult.setText(R.string.bt_device);
        titleWifiResult.setText(R.string.wifi_name);
        titlePasswordResult.setText(R.string.wifi_password);
        titleStatus.setText(R.string.status);

        btResult.setTypeface(null, Typeface.BOLD);
        wifiResult.setTypeface(null, Typeface.BOLD);
        passwordResult.setTypeface(null, Typeface.BOLD);
        status.setTypeface(null, Typeface.BOLD);

        btResult.setText(DeviceListActivity.bt_name);
        wifiResult.setText(WifiListActivity.info);
        passwordResult.setText(WifiListActivity.password);

        if (mensagem == CONECTADO) {

            status.setText(R.string.connected);
        }
        else if (mensagem == DESCONECTADO){

            status.setText(R.string.error);
        }
        return true;
    }

}
