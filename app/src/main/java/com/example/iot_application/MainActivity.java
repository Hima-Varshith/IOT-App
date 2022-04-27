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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    public Button buttonOne;
    public Button buttonTwo;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> deviceList = new ArrayList<>();
    private ArrayAdapter adapter;
    private ArrayAdapter deviceAdapter;
    public Button buttonThree;

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
        Button buttonScan = findViewById(R.id.scanButton);
        buttonScan.setOnClickListener(new MyClass());

        ListView listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled())
        {
            Toast.makeText(this, "Wifi is not enabled on your device.... Please enable it", Toast.LENGTH_LONG).show();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
    }

    @SuppressLint("SetTextI18n")
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

    public void layoutThreeButton(View view)
    {
        setContentView(R.layout.layout3_scandevice);
        ListView listView2 = findViewById(R.id.listDevices);
        deviceList.clear();

        BufferedReader bufferedReader = null;
        try
        {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

            String line;

            while ((line = bufferedReader.readLine()) != null)
            {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:.."))
                    {
                        deviceList.add("IP address : " + ip
                                + "                            "
                                + "MAC address : " + mac);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        listView2.setAdapter(deviceAdapter);
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
            List<ScanResult> results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for(ScanResult scanResult : results)
            {
                arrayList.add("Wifi Name : " + scanResult.SSID );
                adapter.notifyDataSetChanged();
            }
        }
    };
}