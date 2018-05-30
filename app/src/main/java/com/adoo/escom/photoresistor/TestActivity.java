package com.adoo.escom.photoresistor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    private final int GALLERY_INTENT = 1000;
    private final int GALLERY_CODE = 11;
    private Bitmap image = null;
    private ImageView imgView;
    private ImageView imgView2;
    private Uri imgData;
    private File file;
    private ResistorImage img;
    private int portion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button load = findViewById(R.id.btnLoad);
        Button run = findViewById(R.id.btnRun);
//        Button red = findViewById(R.id.btnRed);
//        Button green = findViewById(R.id.btnGreen);
        SeekBar seek = findViewById(R.id.seekBar);
        imgView2 = findViewById(R.id.imageViewTest2);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                portion = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throwIntent(GALLERY_INTENT);
            }
        });

        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ResistorImage r = new ResistorImage(image);
                    Bitmap result = r.getImage();
                    Glide.with(TestActivity.this).load(result).into(imgView2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Integer i = portion;
//                String str = i.toString();
//                Toast.makeText(TestActivity.this, str, Toast.LENGTH_LONG).show();
            }
        });
//
//        green.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Glide.with(TestActivity.this).load(img.getImage()).into(imgView2);
//            }
//        });
//
//        red.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                img.createImageMatrix();
//                img.filterRed(portion);
//                Glide.with(TestActivity.this).load(img.getImage()).into(imgView2);
//            }
//        });
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

    private void throwEditor() {
        file = getTempFile(TestActivity.this);
        UCrop.of(imgData, Uri.fromFile(file)).withAspectRatio(4, 4).withMaxResultSize(300, 300).start(this);
    }


    private File getTempFile(Context context) {
        File file = null;
        try {
            file = File.createTempFile("tempImg", null, context.getCacheDir());
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case GALLERY_CODE:

                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    imgData = data.getData();
                    throwEditor();
//                    imgData = resultUri;
//                    try {
//                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
//                        Glide.with(TestActivity.this).load(resultUri).into(imgView2);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    Toast.makeText(this, "There was an error picking the picture MAIN", Toast.LENGTH_SHORT).show();
                }

                break;

            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK && data != null) {
                    final Uri resultUri = UCrop.getOutput(data);

                    try {
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        Glide.with(TestActivity.this).load(resultUri).into(imgView2);
//                        img = new ResistorImage(image);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                } else if (resultCode == UCrop.RESULT_ERROR) {

                    final Throwable cropError = UCrop.getError(data);
                    Toast.makeText(TestActivity.this, cropError.toString(), Toast.LENGTH_LONG).show();

                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
