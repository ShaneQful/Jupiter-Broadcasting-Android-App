package jupiter.broadcasting.live.tv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;




/*
 * Copyright (c) 2013 Adam Szabo
 *
 * This software is MIT licensed see link for details
 * http://www.opensource.org/licenses/MIT
 *
 * @author Adam Szabo
 *
 */


public class ShowNotesView extends SherlockFragment {

    View v;
    WebView wv;
    String link;
    ProgressBar loadingProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle b = getArguments();
        link = b.getString("Notes");

        v = inflater.inflate(R.layout.shownotes_fragment, null);
        wv = (WebView) v.findViewById(R.id.notesview);
        loadingProgressBar = (ProgressBar) v.findViewById(R.id.progressbar_Horizontal);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setVisibility(View.INVISIBLE);
        wv.setWebChromeClient(new WebChromeClient() {

            // this will be called on page loading progress
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);

                loadingProgressBar.setProgress(newProgress);

                // hide the progress bar if the loading is complete
                if (newProgress == 100) {
                    loadingProgressBar.setVisibility(View.GONE);
                } else {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                }
            }

        });

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                wv.loadUrl("javascript:(function() {var mon = document.getElementsByTagName('div');" +
                        "for (var i = 0; i < mon.length; i++) {" +
                        "if (mon[i].id == 'footer'){mon[i].style.display = 'none';}" +
                        "if (mon[i].id == 'header'){mon[i].style.display = 'none';}" +
                        "if (mon[i].id == 'thevideo'){mon[i].style.display = 'none';}" +
                        "if (mon[i].id == 'sidebar'){mon[i].style.display = 'none';}" +
                        "if (mon[i].id == 'menubar'){mon[i].style.display = 'none';}" +
                        "if (mon[i].id == 'videodets'){mon[i].style.display = 'none';}" +
                        "if (mon[i].id == 'postcomments'){mon[i].style.display = 'none';}" +
                        "}})()");
                wv.loadUrl("javascript: window.CallToAnAndroidFunction.setVisible()");
            }
        });
        wv.addJavascriptInterface(new myJavaScriptInterface(), "CallToAnAndroidFunction");
        wv.loadUrl(link);


        return v;
    }

    public class myJavaScriptInterface {
        @JavascriptInterface
        public void setVisible() {
            getSherlockActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    wv.setVisibility(View.VISIBLE);
                }
            });
        }

    }
}