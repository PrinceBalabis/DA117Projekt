package se.mah.ab7271.wolf;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

/**
 * Created by Prince on 24/10/14.
 */
public class WolframAlpha extends AsyncTask<WAQueryResult, Void, WAQueryResult> {
    private Activity context;
    private static String appid = "9A682A-TW36J5R7AE";
    private WAQueryResult queryResult;
    private String input = "";

    public WolframAlpha(Activity context, String input) {
        this.context = context;
        this.input = input;
    }

    @Override
    protected WAQueryResult doInBackground(WAQueryResult... urls) {
        WAEngine engine = new WAEngine();
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
                System.out.println("Successful query. Pods follow:\n");
                int i = 0;
                for (WAPod pod : queryResult.getPods()) {
                    if (!pod.isError() && (i == 0 || i == 1)) {
                        //System.out.println(pod.getTitle());
                        for (WASubpod subpod : pod.getSubpods()) {
                            for (Object element : subpod.getContents()) {
                                if (element instanceof WAPlainText) {
                                    System.out.println(((WAPlainText) element).getText());
                                    TextView tvAnswer;
                                    tvAnswer = (TextView) context.findViewById(R.id.tvAnswer);
                                    tvAnswer.setText(((WAPlainText) element).getText());
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
