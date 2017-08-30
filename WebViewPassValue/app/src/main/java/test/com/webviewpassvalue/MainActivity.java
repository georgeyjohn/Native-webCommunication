package test.com.webviewpassvalue;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText customText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webView);
        customText = (EditText) findViewById(R.id.customText);
        webView.getSettings().setJavaScriptEnabled(true);

        // Add the custom WebViewClient class
        webView.setWebViewClient(new CustomWebViewClient());

        // Add the javascript interface
        webView.addJavascriptInterface(new JavaScriptInterface(), "interface");

        // Load the example html file to the WebView
        webView.loadUrl("file:///android_asset/index.html");
    }

    public void onButtonClick(View view) {
        webView.loadUrl("javascript:callFromApp('" + customText.getText() + "');");
    }

    /**
     * Onclick callback method for Button.
     *
     * @param view
     */
    public void onFunctionValue(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("javascript:callFromAppWithReturn();", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "evaluateJavascript call require api level 19 (KitKat)", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * CustomWebViewClient is used to add a custom hook to the url loading.
     */
    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // If the url to be loaded starts with the custom protocol, skip
            // loading and do something else
            if (url.startsWith("cusurl://")) {

                Toast.makeText(MainActivity.this, "URL call from Web Page ", Toast.LENGTH_LONG).show();
                Intent intWebClickActivityCall = new Intent(MainActivity.this, WebClickActivity.class);
                startActivity(intWebClickActivityCall);

                return true;
            }

            return false;
        }
    }

    /**
     * JavaScriptInterface is the interface class for the application code calls. All public methods
     * annotated with {@link android.webkit.JavascriptInterface JavascriptInterface } in this class
     * can be called from JavaScript.
     */
    private class JavaScriptInterface {

        @JavascriptInterface
        public void callFromJS(String text) {
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
        }
    }
}
