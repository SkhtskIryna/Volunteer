package eu.tutorials.volunteerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {

    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val checkoutUrl = intent.getStringExtra("CHECKOUT_URL") ?: run {
            Toast.makeText(this, "Checkout URL not provided", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let {
                        when {
                            it.contains("success") -> {
                                safeToast("Оплата успішна")
                                finish()
                            }
                            it.contains("failure") -> {
                                safeToast("Помилка при оплаті")
                                finish()
                            }
                            it.contains("cancel") -> {
                                safeToast("Оплату скасовано")
                                finish()
                            }
                        }
                    }
                }
            }
            loadUrl(checkoutUrl)
        }

        setContentView(webView)
    }

    private fun safeToast(message: String) {
        if (!isFinishing) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webView?.apply {
            loadUrl("about:blank")
            removeAllViews()
            destroy()
        }
        webView = null
    }
}