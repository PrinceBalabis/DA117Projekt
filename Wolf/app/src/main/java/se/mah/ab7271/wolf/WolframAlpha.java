package se.mah.ab7271.wolf;

import android.app.Activity;
import android.os.AsyncTask;
import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

/**
 *  This class contains the code to run the WolframAlpha query service
 **/
public class WolframAlpha extends AsyncTask<WAQueryResult, Void, WAQueryResult> {
    private WAQueryResult queryResult;
    private String input = "";
    private Callback callerActivity;

    public WolframAlpha(Activity context, String input) {
        callerActivity = (Callback)context;
        this.input = input;
    }

    @Override
    protected WAQueryResult doInBackground(WAQueryResult... urls) {
        WAEngine engine = new WAEngine();
        String appid = "9A682A-TW36J5R7AE";
        engine.setAppID(appid);
        engine.addFormat("plaintext");

        // Create the query.
        WAQuery query = engine.createQuery();
        query.setInput(input);
        queryResult = null;
        try {
            queryResult = engine.performQuery(query);
        } catch (WAException e) {
            e.printStackTrace();
        }
        return queryResult;
    }

    @Override
    protected void onPostExecute(WAQueryResult response) {
        if (queryResult.isError()) {
            System.out.println("Query error");
            System.out.println("  error code: " + queryResult.getErrorCode());
            System.out.println("  error message: " + queryResult.getErrorMessage());

        } else {
            if (!queryResult.isSuccess()) {
                System.out.println("Query was not understood; no results available.");

            } else {

                // Got a result.
                System.out.println("Successful query");
                int i = 0;
                for (WAPod pod : queryResult.getPods()) {
                    if (!pod.isError() && i == 1) {
                        //System.out.println(pod.getTitle());
                        for (WASubpod subpod : pod.getSubpods()) {
                            for (Object element : subpod.getContents()) {
                                if (element instanceof WAPlainText) {
//                                    System.out.println(((WAPlainText) element).getText());
                                    callerActivity.updateDisplays(input, ((WAPlainText) element).getText());
                                }
                            }
                        }
                    }
                    i++;
                }
            }
        }
    }
}
