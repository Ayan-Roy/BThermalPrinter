package com.swosti.bthermalprinter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private MaterialButton btnPrint;
    public static final int PERMISSION_BLUETOOTH = 1;

    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPrint = findViewById(R.id.btn_Print);
        setActionListener();
    }


    private void setActionListener() {
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkBluetoothEnabled())
                    checkBluetoothPermissions();
            }
        });
    }

    private boolean checkBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.d(TAG, "Device does not support Bluetooth");
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!bluetoothAdapter.isEnabled()) {
            Log.d("Bluetooth", "Bluetooth is disabled");
            Toast.makeText(this, "PLease Turn On Bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Log.d("Bluetooth", "Bluetooth is enabled");
            return true;
        }
    }


    public void checkBluetoothPermissions() {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_ADMIN}, MainActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, MainActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, MainActivity.PERMISSION_BLUETOOTH_SCAN);
        } else {
            try {
                printBluetooth();
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case MainActivity.PERMISSION_BLUETOOTH:
                case MainActivity.PERMISSION_BLUETOOTH_ADMIN:
                case MainActivity.PERMISSION_BLUETOOTH_CONNECT:
                case MainActivity.PERMISSION_BLUETOOTH_SCAN:
                    this.checkBluetoothPermissions();
                    break;
            }
        }
    }

    public void printBluetooth() throws Exception {
        EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32);
        printer.printFormattedText(getInvoiceDataAsText(printer));
        Log.e(TAG, "Invoice Printed:");
        Toast.makeText(this, "Invoice Printed", Toast.LENGTH_SHORT).show();

    }

    private String getInvoiceDataAsText(EscPosPrinter printer) {

        String rptData = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.icon_app_store, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                "[L]\n" +
                "[C]<u><font size='big'>Demo Organization</font></u>\n";


        return rptData;
    }


 /*
"[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.icon_app_store, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
            "[L]\n" +
            "[C]<u><font size='big'>Demo Organization</font></u>\n" +
            "[L]\n" +
            "[C]================================\n" +
            "[L]\n" +
            "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
            "[L]  + Size : S\n" +
            "[L]\n" +
            "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
            "[L]  + Size : 57/58\n" +
            "[L]\n" +
            "[C]--------------------------------\n" +
            "[R]TOTAL PRICE :[R]34.98e\n" +
            "[R]TAX :[R]4.23e\n" +
            "[L]\n" +
            "[C]================================\n" +
            "[L]\n" +
            "[L]<font size='tall'>Customer :</font>\n" +
            "[L]Raymond DUPONT\n" +
            "[L]5 rue des girafes\n" +
            "[L]31547 PERPETES\n" +
            "[L]Tel : +33801201456\n" +
            "[L]\n" +
            "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
            "[C]<qrcode size='20'>https://dantsu.com/</qrcode>";*/


}