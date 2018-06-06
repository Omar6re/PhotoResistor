package com.adoo.escom.photoresistor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ImageAnalysis.Resistor;
import ImageAnalysis.ResistorImage;

public class EditImageActivity extends AppCompatActivity {

    private ImageView imgView;
    private File image;
    private Bundle data;
    private RadioGroup opcNumColors;
    private Spinner stripe;
    private int[] stripesCoord = {0, 0, 0, 0, 0, 0};
    private int coord;
    private ResistorImage img;
    private SeekBar stripePos;
    private int selectedStripe = 1;

    private int numberColors;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        imgView = findViewById(R.id.imageView);
        Button continueResult = findViewById(R.id.btnContinueResult);
        Button editImage = findViewById(R.id.btnEditImage);
        opcNumColors = findViewById(R.id.radioGroupNumberColorsEdit);
        stripe = findViewById(R.id.spinnerStripe);
        stripePos = findViewById(R.id.stripePos);
        RadioButton def = findViewById(R.id.radioBtnC4);

        numberColors = 4; // Set default selection.
        def.toggle();
        setSpinnerContent(4);
        stripe.setGravity(Spinner.TEXT_ALIGNMENT_CENTER);
        stripe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStripe = Integer.valueOf(stripe.getSelectedItem().toString());
                stripePos.setProgress(stripesCoord[selectedStripe - 1]);
                img.setStripeLine(stripesCoord[selectedStripe - 1]);
                Glide.with(EditImageActivity.this).load(img.getImage()).into(imgView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedStripe = 1;
            }
        });

        stripePos.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                coord = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                img.setStripeLine(stripesCoord[selectedStripe - 1]);
                Toast.makeText(EditImageActivity.this, "Mueve la línea hasta que este sobre la banda.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                stripesCoord[selectedStripe - 1] = coord;
                img.setStripeLine(coord);
                Glide.with(EditImageActivity.this).load(img.getImage()).into(imgView);
            }
        });


        // Initialize buttons click listener.
        continueResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Resistor r = new Resistor(img, numberColors, stripesCoord);
                    Integer value = r.getValue();
                    System.out.println(Arrays.toString(r.getColors()));
                    System.out.println(Arrays.toString(stripesCoord));
//                    Toast.makeText(EditImageActivity.this, value.toString(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Intent results = new Intent(EditImageActivity.this, ResultsActivity.class);
//                results.putExtra("RESISTOR", (Serializable) r);
//                startActivity(results);
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throwEditor();
            }
        });

        opcNumColors.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.radioBtnC4:

                        numberColors = 4;
                        setSpinnerContent(4);

                        break;

                    case R.id.radioBtnC5:

                        numberColors = 5;
                        setSpinnerContent(5);

                        break;

                    case R.id.radioBtnC6:

                        numberColors = 6;
                        setSpinnerContent(6);

                        break;
                }
            }
        });

        // Get picture.
        data = getIntent().getExtras();

        throwEditor();
    }

    private void setSpinnerContent(int i) {
        ArrayAdapter<CharSequence> adapter = null;

        switch (i) {
            case 4:
                adapter = ArrayAdapter.createFromResource(this, R.array.stripes4, android.R.layout.simple_spinner_item);
                break;

            case 5:
                adapter = ArrayAdapter.createFromResource(this, R.array.stripes5, android.R.layout.simple_spinner_item);
                break;

            case 6:
                adapter = ArrayAdapter.createFromResource(this, R.array.stripes6, android.R.layout.simple_spinner_item);
                break;
        }

        if (adapter != null) {
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stripe.setAdapter(adapter);
        }
    }

    private void throwEditor() {
        if (data != null && !data.isEmpty()) {
            // Get image path info.
            Uri uri = (Uri) data.get("image");

            if (uri != null) {
                image = getTempFile(EditImageActivity.this);
                UCrop.of(uri, Uri.fromFile(image)).withAspectRatio(4, 4).withMaxResultSize(300, 300).start(this);
            } else
                Toast.makeText(this, "There was an error loading the image", Toast.LENGTH_SHORT).show();
        } else
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
            try {
                Bitmap resultImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                img = new ResistorImage(resultImg);
                stripePos.setMax(img.getWidth() - 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Glide.with(EditImageActivity.this).load(img.getImage()).into(imgView);

        } else if (resultCode == UCrop.RESULT_ERROR) {

            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(EditImageActivity.this, cropError.toString(), Toast.LENGTH_LONG).show();

        }
    }
}
