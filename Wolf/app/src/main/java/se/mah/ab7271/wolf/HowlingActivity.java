package se.mah.ab7271.wolf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.att.android.speech.ATTSpeechService;
import static android.view.LayoutInflater.*;
import static se.mah.ab7271.wolf.R.layout.dialog_nocomprende;
import static se.mah.ab7271.wolf.R.layout.dialog_nointernet;

/**
 *  This is the main class which sets up the GUI and initiates the APIs
 *  @author Prince, Stefan, Tequamnesh
 **/
public class HowlingActivity extends Activity implements Callback {

    private ProgressDialog pdQuery;
    private final Context context = this;
    private ImageButton btnAsk;
    private TextView tvQuestion;
    private TextView tvAnswer;
    private ImageButton btnMicrophone;
    private ATTSpeechToText attSpeechToText;
    AlertDialog alertDialogNoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howling);

        // Setup GUI Widgets
        tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        tvAnswer = (TextView) findViewById(R.id.tvAnswer);
        btnAsk = (ImageButton) findViewById(R.id.btnAsk);
        btnMicrophone = (ImageButton) findViewById(R.id.btnMicrophone);

        speechToText_init(); // initiate the speech2Text service
        networkCheck(); // Check internet connection and setup internet-required services
        btnListener_init();
    }

    /**
     * onResume method runs every time app closes after have been paused
     **/
    @Override
    protected void onResume() {
        super.onResume();
        networkCheck();
    }

    /**
     * Method which is a callback executed by WolframAlpha API
     * to pass on the question and answer
     *
     * @param question
     * @param answer
     **/
        public void updateDisplays(String question, String answer){
        pdQuery.dismiss(); // Hide progressdialog
        tvAnswer.setText(answer);
        tvQuestion.setText(question);
    }

    /**
     * Method is executed to make a WolframAlpha query. This method is a callback
     * from Speech2Text class or local from manual keyboard entry
     *
     * @param question
     **/
    public void makeQuery(String question){
        // Show a progressdialog while waiting for WolframAlpha query
        pdQuery = ProgressDialog.show(HowlingActivity.this,
                "", "Asking WolframAlpha...", true);
        // Start the WolframAlpha query
        new WolframAlpha(HowlingActivity.this, question).execute();
    }

    /**
     * Method is executed whenever WolframAlpha didn't understand the questin.
     * This method is a callback from WolframAlpha API
     **/
     public void queryWasNotUnderstood(String question){
        pdQuery.dismiss(); // Hide progressdialog
        LayoutInflater li = from(context);
        View promptsView = li.inflate(dialog_nocomprende, null);
        TextView tvNoComprende = (TextView) promptsView.findViewById(R.id.tvNoComprende);
        tvNoComprende.setText("WolframAlpha did not understand: '"+question+"'");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set dialog_nointernet.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((HowlingActivity) context).onResume();
                            }
                        });


        // create alert dialog
        alertDialogNoInternet = alertDialogBuilder.create();

        // show it
        alertDialogNoInternet.show();

    }

    /**
     * This method checks if the phone is connected to the
     * internet and sets up the internet-required services
     **/
    private boolean networkCheck(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()){
            attSpeechToText.validate(); // (re)validate Speech2Text service
            return true;
        } else{
            showNoInternetDialog(); // Show an alert for missing internet-connection
            return false;
        }
    }

    /**
     * Sets up and displays a dialog which alert user of missing internet-connection
     **/
    private void showNoInternetDialog(){
        System.out.println("No internet!");
        // get dialog_nointernet.xml view
        LayoutInflater li = from(context);
        View promptsView = li.inflate(dialog_nointernet, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set dialog_nointernet.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Try again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((HowlingActivity) context).onResume();
                            }
                        });
        // create alert dialog
        alertDialogNoInternet = alertDialogBuilder.create();

        // show it
        alertDialogNoInternet.show();
    }

    /**
     * Toggles(Shows/hides) the keyboard depending on previous state
     **/
    private void toggleKeyboard(){
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Initiates Speech2Text service
     **/
    private void speechToText_init(){
        ATTSpeechService speechService = ATTSpeechService.getSpeechService(this);
        attSpeechToText = new ATTSpeechToText(HowlingActivity.this, speechService);
    }

    /**
     * Sets up button listener for keyboard and microphone buttons
     **/
    private void btnListener_init() {
        // Setup microphone button
        btnMicrophone.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //Check internet connection
                        if(networkCheck()){
                            // Start SpeechToText conversion
                            attSpeechToText.startSpeechService();
                        }
                    }
                });

        // Setup keyboard button
        btnAsk.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //Check internet connection
                        if(networkCheck()){
                            // get dialog_keyboard.xml view
                            LayoutInflater li = from(context);
                            View promptsView = li.inflate(R.layout.dialog_keyboard, null);

                            AlertDialog.Builder alertDialogBuilder =
                                    new AlertDialog.Builder(context);

                            // Show keyboard
                            toggleKeyboard();

                            // set dialog_keyboard.xml to alertdialog builder
                            alertDialogBuilder.setView(promptsView);

                            final EditText userInput =
                                    (EditText) promptsView.findViewById(R.id.etQuestionWrite);

                            // set dialog message
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {
                                                    // Make a WolframAlpha Query
                                                    makeQuery(userInput.getText().toString());
                                                    // Hide keyboard
                                                    toggleKeyboard();
                                                }
                                            })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {
                                                    // hide dialog
                                                    dialog.cancel();
                                                    // Hide keyboard
                                                    toggleKeyboard();
                                                }
                                            });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();
                        }
                    }
                });
    }
}
