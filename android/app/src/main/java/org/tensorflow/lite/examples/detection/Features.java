package org.tensorflow.lite.examples.detection;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.Calling.CallActivity;
import org.tensorflow.lite.examples.detection.Message.MessageReader;
import org.tensorflow.lite.examples.detection.Moneytransfer.Banktransfer;
import org.tensorflow.lite.examples.detection.Moneytransfer.phonetransfer;
import org.tensorflow.lite.examples.detection.ObjectDetection.MainActivity;
import org.tensorflow.lite.examples.detection.Currency.CurrencyDetection;
import org.tensorflow.lite.examples.detection.QRProduct.QRactivity;
import org.tensorflow.lite.examples.detection.Reminder.Reminder;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class Features extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
     private TextView mVoiceInputTv;
    float x1, x2, y1, y2;

    private static TextToSpeech textToSpeech;
    private DBHandler dbHandler;
    static String Readmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        dbHandler = new DBHandler(this);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.forLanguageTag("en-IN-Wavenet-D"));
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.speak(getString(R.string.features)+" swipe right and say what you want", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);


    }


    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {

        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 > x2) {
                    textToSpeech.stop();
                        startVoiceInput();
        }
        break;
    }
        return false;
}


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //mVoiceInputTv.setText(result.get(0));
                String output = result.get(0);

                 if (output.contains("bank transfer")) {
                    Intent i = new Intent(Features.this, Banktransfer.class);
                    startActivity(i);
                }
                else if(output.contains("phone transfer")){
                    Intent i = new Intent(Features.this, phonetransfer.class);
                    startActivity(i);
                }

                else if (output.contains("object")) {
                    Intent i = new Intent(Features.this, MainActivity.class);
                    startActivity(i);
                }
                 else if (output.contains("currency")) {
                     Intent i = new Intent(Features.this, CurrencyDetection.class);
                     startActivity(i);
                 }
                 else if (output.contains("read message")) {
                     Readmessage = "read message";
                     Intent i = new Intent(Features.this, MessageReader.class);
                     i.putExtra("read message", Readmessage);
                     textToSpeech.speak("Getting messages , Please wait", TextToSpeech.QUEUE_FLUSH, null);
                     startActivity(i);

                 } else if (output.contains("unread")) {
                     Readmessage = "unread message";
                     Intent i = new Intent(Features.this, MessageReader.class);
                     i.putExtra("unread message", Readmessage);
                     startActivity(i);

                 } else if (output.contains("call")) {
                     Intent i = new Intent(Features.this, CallActivity.class);
                     startActivity(i);

                 }
                 else if (output.contains("reminder")) {
                     Intent i = new Intent(Features.this, Reminder.class);
                     startActivity(i);
                 }
                 else if (output.contains("Android message")) {
                     Readmessage = "unread message";
                     Intent i = new Intent(Features.this, MessageReader.class);
                     i.putExtra("unread message", Readmessage);
                     startActivity(i);

                 } else if (result.get(0).contains("yesterday")||result.get(0).contains("erday")) {
                     Readmessage = "yesterday message";
                     Intent i = new Intent(Features.this, MessageReader.class);
                     i.putExtra("yesterday message", Readmessage);
                     startActivity(i);

                 }

                 else if (output.contains("scan the QR")||output.contains("QR code")) {
                    Intent i = new Intent(Features.this, QRactivity.class);
                    startActivity(i);
                }
                 else if (output.contains("exit")) {
                   onPause();
                   finishAffinity();
                }

            }
        }
    }
    public void onDestroy(){
            finish();

        super.onDestroy();
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }
    }

