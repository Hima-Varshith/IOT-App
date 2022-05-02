package com.example.iot_application;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

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
    private CodeScanner qrCodeScanner;
    public Button buttonOne;
    public Button buttonTwo;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> deviceList = new ArrayList<>();
    private ArrayAdapter adapter;
    private ArrayAdapter deviceAdapter;
    public Button buttonThree;
    public Button buttonFour;
    public Button buttonFive;
    public EditText credone;
    public EditText credtwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonOne = findViewById(R.id.button1);
        buttonTwo = findViewById(R.id.button2);
        buttonThree = findViewById(R.id.button3);
        buttonFour = findViewById(R.id.button4);
        buttonFive = findViewById(R.id.button5);
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

    public void layoutFourButton(View view)
    {
        setContentView(R.layout.layout4_get);

    }

    public void layoutFiveButton(View view)
    {
        setContentView(R.layout.layout5_pass);
        credone = findViewById(R.id.editTextSSID);
        credtwo = findViewById(R.id.editTextPassword);
        Button buttonCheck = findViewById(R.id.checkButton);
        buttonCheck.setOnClickListener(new MyClass2());
    }

    public void layoutEightButton(View view){
        setContentView(R.layout.layout8_qr);
        CodeScannerView scannerView = findViewById(R.id.qr_scanner);
        qrCodeScanner = new CodeScanner(this,scannerView);

        qrCodeScanner.setDecodeCallback(new DecodeCallback()
        {
            @Override
            public void onDecoded(@NonNull final Result result)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                qrCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        qrCodeScanner.releaseResources();
        super.onPause();
    }

    public class MyClass implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            scanWifi();
        }
    }

    public class MyClass2 implements View.OnClickListener{
        @Override
        public void onClick(View v) { checkCredentials();}
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

    private void checkCredentials()
    {
        Toast.makeText(this,"Success Response: OK",Toast.LENGTH_LONG).show();
        credone.setText("");
        credtwo.setText("");
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