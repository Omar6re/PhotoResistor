package com.adoo.escom.photoresistor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {
    private Button manualBtn;
    private Button cameraBtn;
    private Button galleryBtn;
    private final int CAMERA_CODE = 10;
    private final int GALLERY_CODE = 11;
    private final int READ_PERMISSION_CODE = 12;
    private final int WRITE_PERMISSION_CODE = 13;
    private final int GALLERY_INTENT = 1000;
    private final int CAMERA_INTENT = 1001;
    private final int EDIT_IMAGE_INTENT = 1002;
    private final int MANUAL_INPUT_INTENT = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Get all buttons.
        manualBtn = findViewById(R.id.btnManual);
        cameraBtn = findViewById(R.id.btnCamera);
        galleryBtn = findViewById(R.id.btnGallery);

        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throwIntent(MANUAL_INPUT_INTENT);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throwIntent(CAMERA_INTENT);
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throwIntent(GALLERY_INTENT);

            }
        });

        // Check read and write permission.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
        }
    }

    private void throwIntent(int which) {
        switch(which){

            case GALLERY_INTENT:

                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_CODE);

                break;

            case CAMERA_INTENT:

                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMERA_CODE);

                break;

            case MANUAL_INPUT_INTENT:

                Intent manualActivity = new Intent(MainMenu.this, ManualInputActivity.class);
                startActivity(manualActivity);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case CAMERA_CODE:

                if(resultCode == Activity.RESULT_OK && data != null){
                    throwIntent(GALLERY_INTENT);
                }else{
                    Toast.makeText(this, "There was an error taking the picture", Toast.LENGTH_SHORT).show();
                }

                break;

            case GALLERY_CODE:

                if(resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
                        Uri imagePath = data.getData();
                        Intent toEditImage = new Intent(MainMenu.this, EditImageActivity.class);
                        toEditImage.putExtra("image", imagePath);
                        startActivity(toEditImage);
                }else{
                    Toast.makeText(this, "There was an error picking the picture MAIN", Toast.LENGTH_SHORT).show();
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case READ_PERMISSION_CODE:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(MainMenu.this, "You must accept read permission", Toast.LENGTH_SHORT).show();
                break;

            case WRITE_PERMISSION_CODE:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(MainMenu.this, "You must accept write permission", Toast.LENGTH_SHORT).show();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
