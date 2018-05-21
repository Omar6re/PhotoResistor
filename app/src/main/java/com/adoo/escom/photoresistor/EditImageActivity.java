package com.adoo.escom.photoresistor;

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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class EditImageActivity extends AppCompatActivity {

    private ImageView imgView;
    private File image;
    private Bundle data;
    private RadioGroup opcNumColors;
    Bitmap img;

    private int numberColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        imgView = findViewById(R.id.imageView);
        Button continueResult = findViewById(R.id.btnContinueResult);
        Button editImage = findViewById(R.id.btnEditImage);
        opcNumColors = findViewById(R.id.radioGroupNumberColorsEdit);
        numberColors = 4; // Set default selection.


        // Initialize buttons click listener.
        continueResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resistor r = null;
                try {
                    r = new Resistor(img, numberColors);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent results = new Intent(EditImageActivity.this, ResultsActivity.class);
                results.putExtra("RESISTOR", (Serializable) r);
                startActivity(results);
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throwEditor();
            }
        });

        opcNumColors.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){

                    case R.id.radioBtnC4:

                        numberColors = 4;

                        break;

                    case R.id.radioBtnC5:

                        numberColors = 5;

                        break;

                    case R.id.radioBtnC6:

                        numberColors = 6;

                        break;
                }
            }
        });

        // Get picture.
        data = getIntent().getExtras();

        throwEditor();
    }

    private void throwEditor() {
        if(data != null && !data.isEmpty()){
            // Get image path info.
            Uri uri = (Uri) data.get("image");

            if (uri != null)
            {
                image = getTempFile(EditImageActivity.this);
                UCrop.of(uri, Uri.fromFile(image)).withAspectRatio(4, 4).withMaxResultSize(300, 300).start(this);
            }
            else
                Toast.makeText(this, "There was an error loading the image", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "There was an ERROR", Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            final Uri resultUri = UCrop.getOutput(data);
            try
            {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            Glide.with(EditImageActivity.this).load(resultUri).into(imgView);

        } else if (resultCode == UCrop.RESULT_ERROR) {

            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(EditImageActivity.this, cropError.toString(), Toast.LENGTH_LONG).show();

        }
    }
}
