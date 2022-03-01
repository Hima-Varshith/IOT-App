package com.example.iot_application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listView;
    public Button buttonOne;
    public Button buttonTwo;
    private Button buttonScan;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonOne = (Button) findViewById(R.id.button1);
        buttonTwo = (Button) findViewById(R.id.button2);
    }

    public void layoutOneButton(View view) {
        setContentView(R.layout.layout1_scanwifi);
        buttonScan = (Button) findViewById(R.id.scanButton);
        buttonScan.setOnClickListener(new MyClass());

        listView = (ListView) findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast toast = Toast.makeText(this, "Wifi is not enabled on your device.... We are enabling it", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 50);
            toast.show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
    }

    public void layoutTwoButton(View view) {
        setContentView(R.layout.layout2_mac);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        TextView firstLine = (TextView) findViewById(R.id.line1);
        TextView secondLine = (TextView) findViewById(R.id.line2);
        TextView thirdLine = (TextView) findViewById(R.id.line3);
        if (wifiManager.isWifiEnabled())
        {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String wifiName = wifiInfo.getSSID();
            @SuppressLint("MissingPermission") String macAddress = wifiInfo.getMacAddress();
            firstLine.setText("CURRENT WI-FI :");
            secondLine.setText(wifiName);
            thirdLine.setText("Connected to MAC Address :" + macAddress);
        }
        else {
            firstLine.setText("CURRENT WI-FI :");
            secondLine.setText("Not connected to WiFi");
            thirdLine.setText("You need to connect to a Wi-Fi network for finding the mac address");
        }
    }

    public class MyClass implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            scanWifi();
        }
    }

    private void scanWifi()
    {
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast toast = Toast.makeText(this, "Scanning Nearby WiFi Networks",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 50);
        toast.show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for(ScanResult scanResult : results)
            {
                arrayList.add("Wifi Name : " + scanResult.SSID );
                adapter.notifyDataSetChanged();
            }
        }
    };
}