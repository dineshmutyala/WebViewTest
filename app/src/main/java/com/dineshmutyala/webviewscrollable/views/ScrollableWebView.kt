package com.dineshmutyala.webviewscrollable.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.webkit.WebView
import android.R
import android.annotation.SuppressLint
import android.util.Log
//import androidx.webkit.WebSettingsCompat
//import androidx.webkit.WebViewFeature


class ScrollableWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.webViewStyle
): WebView(context, attrs, defStyleAttr) {

    init {
        initView(context)
    }

    private var isTouching: Boolean = false
    var listener: ScrollChangeListener? = null

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView(context: Context) {
        this.settings.javaScriptEnabled = true
        this.settings.useWideViewPort = true
        this.settings.loadWithOverviewMode = true
        this.settings.domStorageEnabled = true
//        settings.userAgentString = getDesktopVersionUserAgent(settings.userAgentString)
//        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
//            WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_AUTO)
//        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (isTouching) listener?.onScrollChanged(l, t, oldl, oldt)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_UP -> {
                isTouching = false
                listener?.onTouchUp()
            }
            MotionEvent.ACTION_DOWN -> {
                isTouching = true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setScrollChangeListener(listener: ScrollChangeListener) {
        this.listener = listener
    }
//    private fun getDesktopVersionUserAgent(userAgent: String): String {
//        val start = userAgent.indexOf('(')
//        val end = userAgent.indexOf(')', startIndex = start)
//        return userAgent.replaceRange(start..end, "(X11; Linux x86_64)")
//    }
}

interface ScrollChangeListener {
    fun onScrollChanged(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)
    fun onTouchUp()
}