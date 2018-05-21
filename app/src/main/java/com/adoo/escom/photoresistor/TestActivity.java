package com.adoo.escom.photoresistor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import java.io.IOException;

import static android.provider.MediaStore.Images.Media;

public class TestActivity extends AppCompatActivity {

    private final int GALLERY_INTENT = 1000;
    private final int GALLERY_CODE = 11;
    private Bitmap image = null;
    private ImageView imgView;
    private ImageView imgView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button load = findViewById(R.id.btnLoad);
        Button run = findViewById(R.id.btnRun);
        imgView2 = findViewById(R.id.imageViewTest2);

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throwIntent(GALLERY_INTENT);
            }
        });

        run.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                if (image != null) {
                    try {
                        ResistorImage img = new ResistorImage(image);
                        image = img.getImage();
                        Glide.with(TestActivity.this).load(image).into(imgView2);
                        Toast.makeText(TestActivity.this, "Everything OK.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    Toast.makeText(TestActivity.this, "You must choose an image.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void throwIntent(int which) {
        switch (which) {

            case GALLERY_INTENT:

                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_CODE);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case GALLERY_CODE:

                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    Uri resultUri = data.getData();
                    try {
                        image = Media.getBitmap(this.getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(this, "There was an error picking the picture MAIN", Toast.LENGTH_SHORT).show();
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
