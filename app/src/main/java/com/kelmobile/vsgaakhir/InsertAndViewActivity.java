package com.kelmobile.vsgaakhir;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class InsertAndViewActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_STORAGE = 100;
    private int eventID = 0;
    private EditText etFileName;
    private EditText etContent;
    //boolean isEditable = false;
    private String tempCatatan = "";
    //set directory path for saving file
    private String pathDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/MyNotes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_and_view);

        //views to object
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etFileName = findViewById(R.id.etFilename);
        etContent = findViewById(R.id.etContent);
        Button btSimpan = findViewById(R.id.btSimpan);

        String fileName;

        //event handler
        btSimpan.setOnClickListener(v -> {
            eventID = 2;
            if (!tempCatatan.equals(etContent.getText().toString())) {
                checkPermissionsAndExecute(this::tampilSaveDialog);
            }
        });

        //check bundle dari intent
        if (getIntent().getExtras() != null && !getIntent().getExtras().isEmpty()) {
            fileName = getIntent().getExtras().getString("filename");
            etFileName.setText(fileName);
            getSupportActionBar().setTitle("Ubah Catatan");
        } else {
            getSupportActionBar().setTitle("Tambah Catatan");
        }


        eventID = 1;
        if (Build.VERSION.SDK_INT >= 23) {
            if (periksaIzinPenyimpanan()) {
                bacaFile();
            }
        } else {
            bacaFile();
        }
    }

    public boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE);

                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (eventID == 1)
                bacaFile();
            else
                tampilSaveDialog();
    }


    void bacaFile() {
        File file = new File(pathDir, etFileName.getText().toString());
        if (file.exists()) {

            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line = br.readLine();

                while (line != null) {
                    text.append(line);
                    line = br.readLine();
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            tempCatatan = text.toString();
            etContent.setText(text.toString());
        }
    }

    void buatDanUbah() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }

        File parent = new File(pathDir);
        if (!parent.exists() && !parent.mkdir()) {
            return;
        }
        File file = new File(pathDir, etFileName.getText().toString());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(etContent.getText().toString().getBytes());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        onBackPressed();
    }

    void tampilSaveDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Simpan Catatan")
                .setMessage("Apakah Anda yakin ingin menyimpan Catatan ini?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES", (dialog, whichButton) -> buatDanUbah())
                .setNegativeButton("NO", null).show();
    }

    @Override
    public void onBackPressed() {
        if (!tempCatatan.equals(etContent.getText().toString())) {
            tampilSaveDialog();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPermissionsAndExecute(Runnable action) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (periksaIzinPenyimpanan()) {
                action.run();
            }
        } else {
            action.run();
        }
    }
}