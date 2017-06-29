package com.mobilis.testemenu;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothAdapter mBluetoothAdapter = null;

    private ArrayAdapter<String> mResultArrayAdapter;

    private ArrayAdapter<String> mBtResultArrayAdapter;
    private ArrayAdapter<String> mWifiResultArrayAdapter;
    private ArrayAdapter<String> mPasswordArrayAdapter;

    private TextView titleBtResult;
    private TextView titleWifiResult;
    private TextView titlePasswordResult;
    private TextView titleStatus;

    private TextView btResult;
    private TextView wifiResult;
    private TextView passwordResult;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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

    }


    public void searchBT(){
        Log.i(TAG, "searchBT()");
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, String.valueOf(data));

        if(data != null) {
            Intent serverIntent = new Intent(this, WifiListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
        } else {
            titleBtResult.setText("Dispositivo Bluetooth:");
            titleWifiResult.setText("Rede Wifi:");
            titlePasswordResult.setText("Senha da rede:");
            titleStatus.setText("Status:");


            btResult.setTypeface(null, Typeface.BOLD);
            wifiResult.setTypeface(null, Typeface.BOLD);
            passwordResult.setTypeface(null, Typeface.BOLD);
            status.setTypeface(null, Typeface.BOLD);

            btResult.setText(DeviceListActivity.bt_name);
            wifiResult.setText(WifiListActivity.info);
            passwordResult.setText(WifiListActivity.password);
            status.setText("CONECTADO");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.insecure_connect_scan: {

                Log.i(TAG, "clicou no insecure");
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {

                Log.i(TAG, "clicou no discoverable");
               // ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
}
