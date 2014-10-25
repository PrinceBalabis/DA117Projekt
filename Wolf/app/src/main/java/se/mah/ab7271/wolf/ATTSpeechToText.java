package se.mah.ab7271.wolf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import com.att.android.speech.ATTSpeechError;
import com.att.android.speech.ATTSpeechError.ErrorType;
import com.att.android.speech.ATTSpeechErrorListener;
import com.att.android.speech.ATTSpeechResult;
import com.att.android.speech.ATTSpeechResultListener;
import com.att.android.speech.ATTSpeechService;

import org.apache.http.entity.HttpEntityWrapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public class ATTSpeechToText {

    private ATTSpeechService speechService;
    private String oauthToken = null;
    private Callback callerActivity;

    public ATTSpeechToText(Activity context, ATTSpeechService speechService){
        this.callerActivity = (Callback)context;
        this.speechService = speechService;

        // Fetch the OAuth credentials.
        validateOAuth();
    }


    /**
     * Called by the Speak button in the sample activity.
     * Starts the SpeechKit service that listens to the microphone and returns
     * the recognized text.
     **/
    public void startSpeechService() {

        // Register for the success and error callbacks.
        speechService.setSpeechResultListener(new ResultListener());
        speechService.setSpeechErrorListener(new ErrorListener());
        // Next, we'll put in some basic parameters.
        // First is the Request URL.  This is the URL of the speech recognition
        // service that you were given during onboarding.
        try {
            speechService.setRecognitionURL(new URI(SpeechConfig.serviceUrl()));
        }
        catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }

        // Specify the speech context for this app.
        speechService.setSpeechContext("Generic");

        // Set the OAuth token that was fetched in the background.
        speechService.setBearerAuthToken(oauthToken);

        // Add extra arguments for speech recognition.
        // The parameter is the name of the current screen within this app.
        speechService.setXArgs(
                Collections.singletonMap("ClientScreen", "main"));

        // Finally we have all the information needed to start the speech service.
        speechService.startListening();
        Log.v("SimpleSpeech", "Starting speech interaction");
    }

    /**
     * This callback object will get all the speech success notifications.
     **/
    private class ResultListener implements ATTSpeechResultListener {
        public void onResult(ATTSpeechResult result) {
            Log.v("SimpleSpeech", "YESYESYESY.");

            // The hypothetical recognition matches are returned as a list of strings.
            List<String> textList = result.getTextStrings();
            String resultText = null;
            if (textList != null && textList.size() > 0) {
                // There may be multiple results, but this example will only use
                // the first one, which is the most likely.
                resultText = textList.get(0);
            }
            if (resultText != null && resultText.length() > 0) {
                // This is where your app will process the recognized text.
                Log.v("SimpleSpeech", "Recognized "+textList.size()+" hypotheses.");
                handleRecognition(resultText);
            }
            else {
                // The speech service did not recognize what was spoken.
                Log.v("SimpleSpeech", "Recognized no hypotheses.");
//                alert("Didn't recognize speech", "Please try again.");
            }
        }
    }

    /** Make use of the recognition text in this app. **/
    private void handleRecognition(String resultText) {
        System.out.println(resultText);
       callerActivity.makeQuery(resultText);
    }

    /**
     * This callback object will get all the speech error notifications.
     **/
    private class ErrorListener implements ATTSpeechErrorListener {
        public void onError(ATTSpeechError error) {
            ErrorType resultCode = error.getType();
            if (resultCode == ErrorType.USER_CANCELED) {
                // The user canceled the speech interaction.
                // This can happen through several mechanisms:
                // pressing a cancel button in the speech UI;
                // pressing the back button; starting another activity;
                // or locking the screen.

                // In all these situations, the user was instrumental
                // in canceling, so there is no need to put up a UI alerting
                // the user to the fact.
                Log.v("SimpleSpeech", "User canceled.");
            }
            else {
                // Any other value for the result code means an error has occurred.
                // The argument includes a message to help the programmer
                // diagnose the issue.
                String errorMessage = error.getMessage();
                Log.v("SimpleSpeech", "Recognition error #"+resultCode+": "+errorMessage);

//                alert("Speech Error", "Please try again later.");
            }
        }
    }

    /**
     * Start an asynchronous OAuth credential check.
     * Disables the Speak button until the check is complete.
     **/
    private void validateOAuth() {
        SpeechAuth auth =
                SpeechAuth.forService(SpeechConfig.oauthUrl(), SpeechConfig.oauthScope(),
                        SpeechConfig.oauthKey(), SpeechConfig.oauthSecret());
        auth.fetchTo(new OAuthResponseListener());
    }

    /**
     * Handle the result of an asynchronous OAuth check.
     **/
    private class OAuthResponseListener implements SpeechAuth.Client {
        public void
        handleResponse(String token, Exception error)
        {
            if (token != null) {
                oauthToken = token;
                callerActivity.enableSpeechToText();
            }
            else {
                Log.v("SimpleSpeech", "OAuth error: "+error);
                // There was either a network error or authentication error.
                // Show alert for the latter.
//                alert("Speech Unavailable",
//                        "This app was rejected by the speech service.  Contact the developer for an update.");
            }
        }
    }

//    private void alert(String header, String message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage(message)
//                .setTitle(header)
//                .setCancelable(true)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
}
