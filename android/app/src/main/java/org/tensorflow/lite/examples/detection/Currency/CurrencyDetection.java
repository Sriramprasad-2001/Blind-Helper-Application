package org.tensorflow.lite.examples.detection.Currency;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageProxy;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.R;


import org.tensorflow.lite.examples.detection.ml.ModelUnquant;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;


public class CurrencyDetection extends AppCompatActivity implements SurfaceHolder.Callback, TextToSpeech.OnUtteranceCompletedListener {

    TextView result, confidence;
     Camera camera1;
    SurfaceView surfaceView;
    TextView textttt;
    private static TextToSpeech textToSpeech;
    SurfaceHolder surfaceHolder;
    public static boolean previewing = false;
    ImageView imageView;
     int imageSize = 224;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvitycurren);
        imageView = findViewById(R.id.image);
         textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.9f);
                    textToSpeech.setOnUtteranceCompletedListener(CurrencyDetection.this);
                     textToSpeech.speak("tap on the screen to detect the currency",TextToSpeech.QUEUE_ADD,null);
                }
            }
        });

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
         getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = new SurfaceView(this);
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        if(!previewing){

            camera1 = Camera.open();
            if (camera1 != null){
                try {
                    camera1.setDisplayOrientation(90);
                    camera1.setPreviewDisplay(surfaceHolder);
                    camera1.startPreview();
                    Camera.Parameters params = camera1.getParameters();
                    if (params.getSupportedFocusModes().contains(
                            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    }
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                     camera1.setParameters(params);

                     previewing = true;
                 } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        surfaceView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                camera1.startPreview();
                if(camera1 != null)
                {
                    camera1.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);

                }
            }
        });

        surfaceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getApplicationContext(), "returning to main menu ", Toast.LENGTH_SHORT).show();
                finish();
                Intent i = new Intent(CurrencyDetection.this, Home.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            }
        });

   
//
//
//        picture.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onClick(View view) {
//                // Launch camera if we have permission
//                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, 1);
//                } else {
//                    //Request camera permission if we don't have it.
//                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
//                }
//            }
//        });
    }
    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback(){

        public void onShutter() {
            // TODO Auto-generated method stub
        }};

    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback(){

        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub
        }};

    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback(){


        public void onPictureTaken(byte[] arg0, Camera arg1) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(arg0, 0, arg0.length,options);

            Bitmap correctBmp = Bitmap.createBitmap(bitmapPicture, 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), null, true);
            int dimension = Math.min(correctBmp.getWidth(), correctBmp.getHeight());
            correctBmp = ThumbnailUtils.extractThumbnail(correctBmp, dimension, dimension);

            correctBmp = Bitmap.createScaledBitmap(correctBmp, imageSize, imageSize, false);
             imageView.setImageBitmap(correctBmp);
            classifyImage(correctBmp);


        }};


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        if(previewing){
            camera1.stopPreview();
            previewing = false;
        }

        if (camera1 != null){
            try {
                camera1.setPreviewDisplay(surfaceHolder);
                camera1.startPreview();
                Camera.Parameters params = camera1.getParameters();
                if (params.getSupportedFocusModes().contains(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                camera1.setParameters(params);
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

        camera1.stopPreview();
        camera1.release();
        camera1 = null;
        previewing = false;

    }

    public void ReadTextFromImage(Image image, ImageProxy imageProxy){

    }
     public void classifyImage(Bitmap image){
        try {

            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
//            500
//            100
//            10

//

              String[] classes = {"five hundred rupees","hundred rupees", "ten rupees",  };
      //      String[] classes = {"ten rupees","twenty rupees","fifty rupees","hundred rupees","five hundred rupees"};

            result.setText(classes[maxPos]);

            String s = "";
            for(int i = 0; i < classes.length; i++){
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
            confidence.setText(s);
            textToSpeech.speak(result.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            textToSpeech.speak("tap to detect the currency ",TextToSpeech.QUEUE_ADD,null);

            // Releases model resources if no longer used.
            model.close();

                camera1 = Camera.open();
                if (camera1 != null){
                    try {
                        camera1.setDisplayOrientation(90);
                        camera1.setPreviewDisplay(surfaceHolder);
                        camera1.startPreview();
                        Camera.Parameters params = camera1.getParameters();
                        if (params.getSupportedFocusModes().contains(
                                Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        }
                        camera1.setParameters(params);
                        previewing = true;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

        } catch (IOException e) {
            // TODO Handle the exception
        }




     }



    @Override
    public void onResume() {
        super.onResume();
        camera1 = Camera.open();
        if (camera1 != null) {
            try {
                camera1.setDisplayOrientation(90);
                camera1.setPreviewDisplay(surfaceHolder);
                camera1.startPreview();
                Camera.Parameters params = camera1.getParameters();
                if (params.getSupportedFocusModes().contains(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                camera1.setParameters(params);
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (textToSpeech != null) {
            textToSpeech.stop();
        }

    }

    @Override
    public void onUtteranceCompleted(String s) {
        textToSpeech.speak("tap on the screen to detect the currency",TextToSpeech.QUEUE_FLUSH,null);

    }
}