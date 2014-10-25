package se.mah.ab7271.wolf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.att.android.speech.ATTSpeechService;

import java.util.ArrayList;
import java.util.Locale;


public class HowlingActivity extends Activity implements Callback {

    private ProgressDialog pdQuery;
    private final Context context = this;
    private ImageButton btnAsk;
    private TextView tvQuestion;
    private TextView tvAnswer;
    private ImageButton btnMicrophone;
    // The ATTSpeechKit uses a singleton object to interface with the
    // speech server.
    private ATTSpeechToText attSpeechToText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howling);

        btnMicrophone = (ImageButton) findViewById(R.id.btnMicrophone);
        tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        tvAnswer = (TextView) findViewById(R.id.tvAnswer);
        btnAsk = (ImageButton) findViewById(R.id.btnAsk);
        ATTSpeechService speechService = ATTSpeechService.getSpeechService(this);
        attSpeechToText = new ATTSpeechToText(HowlingActivity.this, speechService);
    }

    public void updateDisplays(String question, String answer){
        pdQuery.dismiss(); // Hide progressdialog
        tvAnswer.setText(answer);
        tvQuestion.setText(question);
    }

    public void makeQuery(String question){
        pdQuery = ProgressDialog.show(HowlingActivity.this,
                "", "Asking WolframAlpha...", true);
        new WolframAlpha(HowlingActivity.this, question).execute();
    }

    public void enableSpeechToText(){
        btnListener_init();
    }

    private void btnListener_init() {
        btnMicrophone.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
//                        new SpeechToText(HowlingActivity.this).promptSpeechInput();
                        // MIC ACTION
                        attSpeechToText.startSpeechService();
                    }
                });


        btnAsk.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // get prompts.xml view
                        LayoutInflater li = LayoutInflater.from(context);
                        View promptsView = li.inflate(R.layout.popup, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // Show keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final EditText userInput = (EditText) promptsView.findViewById(R.id.etQuestionWrite);

                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                makeQuery(userInput.getText().toString());
                                                // Hide keyboard
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                // Hide keyboard
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                            }
                                        });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                    }
                });
    }
}
