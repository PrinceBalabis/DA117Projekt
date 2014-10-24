package se.mah.ab7271.wolf;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class HowlingActivity extends Activity {

    private ImageButton btnMicrophone;
    public Spinner spinnerLang;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView tvOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howling);

        // wolframalpha
        String input = "what is 3+3?";
        new WolframAlpha(input).execute();

        spinnerLang = (Spinner) findViewById(R.id.spinnerLang);
        btnMicrophone = (ImageButton) findViewById(R.id.btnMicrophone);
        tvOutput = (TextView) findViewById(R.id.tvOutput);

        btnMicrophone.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        promptSpeechInput();
                    }
                });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvOutput.setText(result.get(0));
                }
                break;
            }

        }
    }
}
