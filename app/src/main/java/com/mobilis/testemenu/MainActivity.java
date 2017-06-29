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
    private int i;

    private BluetoothAdapter mBluetoothAdapter = null;

    //////////////////////////////
    WifiManager mainWifi;

    StringBuilder sb = new StringBuilder();
    private final Handler handler = new Handler();

    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

     /*   final Button btn_wifi = (Button) findViewById(R.id.button_wifi);
        btn_wifi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                searchWIFI();
            }
        });
*/
        final Button btn_bt = (Button) findViewById(R.id.button_bt);
        btn_bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                searchBT();

            }
        });

        /////////////////
       // mNewWifiDeviceArrayAdapter = new ArrayAdapter<>()
        /////////////////
     /*   mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, filter);
        if(mainWifi.isWifiEnabled() == false){
            mainWifi.setWifiEnabled(true);
        }
        if (receiverWifi == null){
            receiverWifi = new WifiReceiver();
        }
*/

    }

    //////////////////////////////////////////////////////////////////
    /*class WifiReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> connections = new ArrayList<String>();
            ArrayList<Float> Signal_Strenth = new ArrayList<Float>();

            sb = new StringBuilder();
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            for(int i=0; i < wifiList.size(); i++){
                connections.add(wifiList.get(i).SSID);
               // Log.i(TAG, String.valueOf(connections));
            }
       }
    }*/
    //////////////////////////////////////////////////////////////////

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
        }

    }

  /*  public void searchWIFI(){
        Log.i(TAG, "searchWIFI()");
       // mainWifi.startScan();
        Intent serverIntent = new Intent(this, WifiListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
    }
    */

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
