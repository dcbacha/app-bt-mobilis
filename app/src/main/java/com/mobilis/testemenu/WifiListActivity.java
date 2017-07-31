package com.mobilis.testemenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

//import org.w3c.dom.Text;

//import java.util.ArrayList;
import java.util.List;


public class WifiListActivity extends Activity {

    private static final String TAG = "WifiListActivity";

    WifiManager mainWifi;
    private ArrayAdapter<String> mNewWifiDevicesArrayAdapter;
    private ArrayAdapter<String> mNetwork;
    public static String info;
    public static String password;

    //private final EditText pw = null;



    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device_list);
        this.setFinishOnTouchOutside(false);
        setTitle(R.string.select_network);

        TextView divider = (TextView) findViewById(R.id.title_new_devices);
        divider.setText(R.string.available_network);


        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        setResult(Activity.RESULT_CANCELED);

        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setText(R.string.search_network);
        scanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                doDiscovery();
                view.setVisibility(View.GONE);
            }
        });

        mNetwork = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewWifiDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        ListView newWifiDevicesListView = (ListView) findViewById(R.id.new_devices);
        newWifiDevicesListView.setAdapter(mNewWifiDevicesArrayAdapter);
        newWifiDevicesListView.setOnItemClickListener(mWifiClickListener);

        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        ArrayAdapter<String> pairedArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        pairedListView.setAdapter(pairedArrayAdapter);
        pairedArrayAdapter.add(" ");

        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(WifiReceiver, filter);
        if(!mainWifi.isWifiEnabled()){
            mainWifi.setWifiEnabled(true);
        }

    }

    private void doDiscovery(){
        Log.d(TAG, "doDiscovery() - Wifi");
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.searching_network);
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        mainWifi.startScan();
    }

    private void doConfiguration(){
       // Log.i(TAG, "doConfiguration()");
       // Log.i(TAG, info);
       // Log.i(TAG, password);
        if(DeviceListActivity.bt_name != null){
            Log.i(TAG, DeviceListActivity.bt_name);
        }else{
            Log.i(TAG, "conecte um bt");
        }

        finish();
    }

    private AdapterView.OnItemClickListener mWifiClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            info = ((TextView) view).getText().toString();
            Log.i(TAG, "clicou na rede");
           // Log.i(TAG, info);

            setResult(Activity.RESULT_OK);

            setProgressBarIndeterminateVisibility(false);
            setTitle(R.string.insert_password);
            setContentView(R.layout.activity_password);

            final EditText pw = (EditText) findViewById(R.id.pw);
            pw.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(pw, InputMethodManager.SHOW_IMPLICIT);

            Button sendButton = (Button) findViewById(R.id.button_password);
            sendButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //final EditText pw = (EditText) findViewById(R.id.pw);
                    password = pw.getText().toString();
                    doConfiguration();
                    view.setVisibility(View.GONE);
                }
            });

            ListView networkListView = (ListView) findViewById(R.id.network);
            networkListView.setAdapter(mNetwork);
            mNetwork.add(info);
        }
    };

    private final BroadcastReceiver WifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){
                List<ScanResult> wifiList;
                wifiList = mainWifi.getScanResults();
                for(int i=0; i < wifiList.size();i++){
                    mNewWifiDevicesArrayAdapter.add(wifiList.get(i).SSID);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(WifiReceiver);
    }
}
