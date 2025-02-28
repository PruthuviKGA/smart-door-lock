
package com.example.smartdoorlockmyapp;

        import android.Manifest;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Handler;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;

        import java.io.IOException;
        import java.io.InputStream;
        import java.sql.SQLOutput;
        import java.util.Set;
        import java.util.UUID;

public class BluetoothActivityFirst extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final UUID HC06_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private TextView receivedDataText;
    private ListView pairedDevicesList;
    private Button disconnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        receivedDataText = findViewById(R.id.receivedDataText);
        pairedDevicesList = findViewById(R.id.pairedDevicesList);
        disconnectButton = findViewById(R.id.disconnectButton);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
            finish();
        }

        // Request permissions before proceeding with Bluetooth operations
        requestBluetoothPermissions();

        // Enable Bluetooth if not already enabled
        enableBluetooth();

        // Show paired devices if permissions are granted
        showPairedDevices();

        // Set up item click listener for paired devices
        pairedDevicesList.setOnItemClickListener((parent, view, position, id) -> {
            String deviceInfo = (String) parent.getItemAtPosition(position);
            String deviceAddress = deviceInfo.substring(deviceInfo.length() - 17);
            connectToDevice(deviceAddress);
        });

        // Get the text from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("FINGERPRINT_TEXT")) {
            String fingerprintText = intent.getStringExtra("FINGERPRINT_TEXT");
            sendData(fingerprintText); // Send data via Bluetooth
        }
        // Set up disconnect button
        disconnectButton.setOnClickListener(v -> disconnectBluetooth());
    }

    private void requestBluetoothPermissions() {
        // Check if Bluetooth permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request necessary permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            // Check if Bluetooth permissions are granted before enabling Bluetooth
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        PERMISSION_REQUEST_CODE);
                return;
            }

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    private void showPairedDevices() {
        // Check if Bluetooth permissions are granted before accessing paired devices
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceAddress = device.getAddress(); // MAC address
                adapter.add(deviceName + "\n" + deviceAddress);
            }
            pairedDevicesList.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No paired Bluetooth devices found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with Bluetooth actions
                enableBluetooth();
                showPairedDevices();
            } else {
                Toast.makeText(this, "Bluetooth permissions are required for this app to function", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void connectToDevice(String deviceAddress) {
        try {
            // Check if Bluetooth permissions are granted before connecting
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        PERMISSION_REQUEST_CODE);
                return;
            }

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            bluetoothSocket = device.createRfcommSocketToServiceRecord(HC06_UUID);
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            Toast.makeText(this, "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
            sendData("A");
            startListeningForDataFirst();
        } catch (IOException | SecurityException e) {
            // Handle SecurityException or IOException
            Log.e("Bluetooth", "Connection failed: " + e.getMessage());
            Toast.makeText(this, "Connection failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void startListeningForDataFirst() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;
            try {
                while ((bytes = inputStream.read(buffer)) > 0) {
                    String receivedData = new String(buffer, 0, bytes);
                    Intent intent = new Intent(BluetoothActivityFirst.this, RegistrationActivity.class);
                            intent.putExtra("FINGERPRINT_ID", receivedData); // Send fId to ViewLogsActivity
                            startActivity(intent);
////
////
////                            Log.d("Bluetooth Data", "Fingerprint ID: " + fId);
                    //runOnUiThread(() -> Toast.makeText(this, "Data: " + receivedData, Toast.LENGTH_SHORT).show());

//                    // Split received data into lines (if multiple lines are received at once)
//                    String[] lines = receivedData.split("\\r?\\n");

//                    for (String line : lines) {
//                        if (line.trim().matches("^FingerprintId:\\s*\\d+$")) {
//                            // Extract the number from the line
//                            int fId = Integer.parseInt(line.replaceAll("[^0-9]", ""));
//
//
//                            Log.d("Bluetooth Data", "Fingerprint ID: " + fId);
//
//                            // Update UI with the received fingerprint ID
//
//
//
//
////                            Intent intent = new Intent(BluetoothActivityFirst.this, ViewLogsActivity.class);
////                            intent.putExtra("FINGERPRINT_ID", fId); // Send fId to ViewLogsActivity
////                            startActivity(intent);
////
////
////                            Log.d("Bluetooth Data", "Fingerprint ID: " + fId);
//
//
//                            // Pass fingerprintId to ViewLogsActivity
////                            openViewLogsActivity(fId);
////                            ViewLogsActivity viewLogsActivity= new ViewLogsActivity();
////                            viewLogsActivity.verifyFingerprint(fId);
//
//
//
//
////                            openViewLogsActivity(fId);
//
//
//
//
//                        }
//                    }
                }
            } catch (IOException e) {
                Log.e("Bluetooth", "Error reading data: " + e.getMessage());
            }
        }).start();
    }



//    private void startListeningForDataFirst() {
//        new Thread(() -> {
//            byte[] buffer = new byte[1024];
//            int bytes;
//            try {
//                while ((bytes = inputStream.read(buffer)) > 0) {
//                    String receivedData = new String(buffer, 0, bytes);
//
//                    // Split received data into lines (if multiple lines are received at once)
//                    String[] lines = receivedData.split("\\r?\\n");
//
//                    for (String line : lines) {
//                        if (line.trim().matches("^Initial:\\s*\\d+$")) {
//                            // Extract the number from the line
//                            int fId = Integer.parseInt(line.replaceAll("[^0-9]", ""));
//
//
//                            Log.d("Bluetooth Data", "Inital Fingerprint ID: " + fId);
//
//                            // Update UI with the received fingerprint ID
//                            runOnUiThread(() -> receivedDataText.setText(String.valueOf(fId)));
//
//
//
//                            Intent intent = new Intent(BluetoothActivityFirst.this, RegistrationActivity.class);
//                            intent.putExtra("Inital_FINGERPRINT_ID", fId); // Send fId to ViewLogsActivity
//                            startActivity(intent);
//
//
//                            Log.d("Bluetooth Data", "Initial Fingerprint ID: " + fId);
//
//
//                            // Pass fingerprintId to ViewLogsActivity
////                            openViewLogsActivity(fId);
////                            ViewLogsActivity viewLogsActivity= new ViewLogsActivity();
////                            viewLogsActivity.verifyFingerprint(fId);
//
//
//
//
////                            openViewLogsActivity(fId);
//
//
//
//
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                Log.e("Bluetooth", "Error reading data: " + e.getMessage());
//            }
//        }).start();
//    }
    // Method to send data via Bluetooth
    private void sendData(String message) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.getOutputStream().write(message.getBytes());
                Toast.makeText(this, "Data Sent: " + message, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("Bluetooth", "Error sending data: " + e.getMessage());
                Toast.makeText(this, "Failed to send data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bluetooth is not connected", Toast.LENGTH_SHORT).show();
        }
    }



//    private void openViewLogsActivity(int fingerprintId) {
//        Intent intent = new Intent(this, ViewLogsActivity.class);
//        intent.putExtra("FINGERPRINT_ID", fingerprintId);
//        startActivity(intent);
//    }






    private void disconnectBluetooth() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error disconnecting", Toast.LENGTH_SHORT).show();
        }
    }
}
