package com.example.smarttrashapp.userui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;

import com.example.smarttrashapp.Prevalent.Prevalent;
import com.example.smarttrashapp.R;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ScannerActivity extends AppCompatActivity {
    SurfaceView cameraView;
    TextView txtResult, showx;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1;
    private String qrResult;
    private Button incraseBouns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        cameraView = findViewById(R.id.cameraPreview);
        txtResult = findViewById(R.id.showResult);
        incraseBouns = findViewById(R.id.scandone);
        showx = findViewById(R.id.show);
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true)
                .setRequestedPreviewSize(640, 480).build();
        //Now add event to show camera preview
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //Check permissions here first
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScannerActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    //If permission Granted then start Camera
                    cameraSource.start(cameraView.getHolder());
                } catch (Exception e) {
                    Toast.makeText(ScannerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();

            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> sparseArray = detections.getDetectedItems();
                if (sparseArray.size() != 0) {
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(50);//here give the time in MilliSeconds
                    showx.setVisibility(View.GONE);
                    showx.setText("Your Cans Number is : " + sparseArray.valueAt(0).displayValue);
                    txtResult.setText(sparseArray.valueAt(0).displayValue);
                }

            }
        });
        incraseBouns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtResult.getText().toString() == "") {
                    Toast.makeText(ScannerActivity.this, "Scan QRCode First", Toast.LENGTH_SHORT).show();
                } else {
                    qrResult = txtResult.getText().toString();
                    bounsIncrease(qrResult);
                    Toast.makeText(ScannerActivity.this, qrResult, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void bounsIncrease(final String qrResult) {
        final String bouns = qrResult;
        final String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("DD MMM, YYYY");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        String productRandomKey = saveCurrentDate + saveCurrentTime;
        HashMap<String, Object> historymap = new HashMap<>();
        historymap.put("pid", productRandomKey);
        historymap.put("date", saveCurrentDate);
        historymap.put("time", saveCurrentTime);
        historymap.put("totalbouns", bouns);
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineusers.getPhone()).child("History");
               historyRef .child(productRandomKey).updateChildren(historymap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(ScannerActivity.this, HomeActivity.class);
                            startActivity(intent);
                            Toast.makeText(ScannerActivity.this, "Bouns Add Success...", Toast.LENGTH_SHORT).show();
                        } else {
                            String Error = task.getException().toString();
                            Toast.makeText(ScannerActivity.this, "Error : " + Error, Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        //If permission Granted then start Camera
                        cameraSource.start(cameraView.getHolder());
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }
}