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
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
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
    private Button buttonThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonOne = findViewById(R.id.button1);
        buttonTwo = findViewById(R.id.button2);
        buttonThree = findViewById(R.id.button3);
    }

    public void layoutOneButton(View view) {
        setContentView(R.layout.layout1_scanwifi);
        buttonScan = findViewById(R.id.scanButton);
        buttonScan.setOnClickListener(new MyClass());

        listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled())
        {
            Toast.makeText(this, "Wifi is not enabled on your device.... Please enable it", Toast.LENGTH_LONG).show();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
    }

    public void layoutTwoButton(View view)
    {
        setContentView(R.layout.layout2_mac);
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        TextView firstLine = findViewById(R.id.line1);
        TextView secondLine = findViewById(R.id.line2);
        TextView thirdLine = findViewById(R.id.line3);
        if (wifiManager.isWifiEnabled())
        {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String wifiName = wifiInfo.getSSID();
            String macAddress = wifiInfo.getBSSID();
            firstLine.setText("CURRENT WI-FI :");
            secondLine.setText(wifiName);
            thirdLine.setText("Connected to MAC Address : " + macAddress.toUpperCase());
        }
        else {
            firstLine.setText("CURRENT WI-FI :");
            secondLine.setText("Not connected to WiFi");
            thirdLine.setText("You need to connect to a Wi-Fi network for finding the mac address");
        }
    }
    
    public void layoutThreeButton(View view){
        setContentView(R.layout.layout3_scandevice);

    }

    public class MyClass implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            scanWifi();
        }
    }

    private void scanWifi()
    {
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled())
        {
            Toast.makeText(this, "Wifi is not enabled on your device.... Please enable it", Toast.LENGTH_LONG).show();
        }
        else{
            arrayList.clear();
            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
            Toast.makeText(this, "Scanning Nearby WiFi Networks",Toast.LENGTH_SHORT).show();
        }
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