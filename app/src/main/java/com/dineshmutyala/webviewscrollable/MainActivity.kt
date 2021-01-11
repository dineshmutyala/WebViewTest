package com.dineshmutyala.webviewscrollable

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.dineshmutyala.webviewscrollable.views.ScrollChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.idScrollWebView
import kotlinx.android.synthetic.main.view_bottombar_default.*
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    companion object {
        const val AMAZON_URL = "https://www.amazon.com"
        const val PETSMART_URL = "https://www.petsmart.com"
        const val BESTBUY_URL = "https://www.bestbuy.com"
        const val WALMART_URL = "https://www.walmart.com/grocery"
        const val YOUTUBE_URL = "https://github.com/"
        const val KROGER_URL = "https://kroger.softcoin.com/p/genCardReference?siteId=35140&familyId=1&retailer=Kroger&callback=https://3p-auth.ibotta.com/kroger/callback"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            idScrollWebView.setScrollChangeListener(ChromeCollapseManager(include, idButton))
            idScrollWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    Log.d("URL_TO_LOAD", url ?: "")
                    idScrollWebView.loadUrl(url)
                    return false
                }
            }
            idScrollWebView.loadUrl(intent.getStringExtra("URL") ?: KROGER_URL)
            button_1.setOnClickListener { AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES) }
            button_2.setOnClickListener { AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO) }
            button_3.setOnClickListener { launchActivity2() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        idScrollWebView.loadUrl(intent?.getStringExtra("URL") ?: WALMART_URL)
    }

    private fun launchActivity2() {
        startActivity(Intent(this, MainActivity2::class.java))
    }
}

class ChromeCollapseManager(private val header: View, private val footer: View) : ScrollChangeListener{
    private enum class ScrollDirection {
        NONE, UP, DOWN
    }

    private val headerParams by lazy { this.header.layoutParams as MarginLayoutParams }
    private val footerParams by lazy { this.footer.layoutParams as MarginLayoutParams }
    private val headerHeight by lazy { this.header.height }
    private val footerHeight by lazy { this.footer.height }
    private val initialTopMargin: Int
    private val initialBottomMargin: Int
    private var prevScrollDirection = ScrollDirection.NONE

    init {
        initialTopMargin = headerParams.topMargin
        initialBottomMargin = footerParams.bottomMargin
    }

    override fun onScrollChanged(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val scrolledBy = scrollY - oldScrollY
        val currScrollDir = getScrollDirection(scrollDiff = scrolledBy)
        if (currScrollDir == prevScrollDirection) { // This is to avoid jumping around of the view when Scrolling really slowly
            when (currScrollDir) {
                ScrollDirection.UP -> { followScrollUp(scrolledBy) }
                ScrollDirection.DOWN -> { followScrollDown(scrolledBy) }
            }
            Log.d("Scrolled By", "Header Top: ${headerParams.topMargin}")
            header.layoutParams = headerParams
            footer.layoutParams = footerParams
        }
        prevScrollDirection = currScrollDir
    }

    override fun onTouchUp() {
        when (prevScrollDirection) {
            ScrollDirection.UP -> { if (shouldCollapse()) animateCollapse() }
            ScrollDirection.DOWN -> { if (shouldReveal()) animateReveal() }
        }
    }

    private fun getScrollDirection(scrollDiff: Int) =
        if(scrollDiff <= 0) ScrollDirection.DOWN else ScrollDirection.UP

    private fun followScrollUp (scrolledBy: Int) {
        headerParams.topMargin = max(headerParams.topMargin - scrolledBy, -headerHeight)
        footerParams.bottomMargin = max(footerParams.bottomMargin - scrolledBy, -footerHeight)
    }

    private fun followScrollDown (scrolledBy: Int) {
        headerParams.topMargin = min(headerParams.topMargin - scrolledBy, initialTopMargin)
        footerParams.bottomMargin = min(footerParams.bottomMargin - scrolledBy, initialBottomMargin)
    }

    private fun shouldCollapse() =
        headerParams.topMargin != -headerHeight ||
                footerParams.bottomMargin != -footerHeight

    private fun shouldReveal() =
        headerParams.topMargin != initialTopMargin ||
                footerParams.bottomMargin != initialBottomMargin

    private fun animateCollapse() {
        header.startAnimation(getHeaderCollapseAnimation(headerParams.topMargin))
        footer.startAnimation(getFooterCollapseAnimation(footerParams.bottomMargin))
    }

    private fun animateReveal() {
        header.startAnimation(getHeaderRevealAnimation(headerParams.topMargin))
        footer.startAnimation(getFooterRevealAnimation(footerParams.bottomMargin))
    }

    private fun getHeaderCollapseAnimation(currMargin : Int) = object: Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            headerParams.topMargin =
                max((-headerHeight * interpolatedTime).toInt() + currMargin, -headerHeight)
            header.layoutParams = headerParams
        }
    }.also { applyAnimationProperties(it) }

    private fun getHeaderRevealAnimation(currMargin : Int) = object: Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            headerParams.topMargin = min(
                ((1 - interpolatedTime) * (-headerHeight + initialTopMargin - currMargin)).toInt(),
                initialTopMargin
            )
            header.layoutParams = headerParams
        }
    }.also { applyAnimationProperties(it) }

    private fun getFooterCollapseAnimation(currMargin : Int) = object: Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            footerParams.bottomMargin =
                max((-footerHeight * interpolatedTime).toInt() + currMargin, -footerHeight)
            footer.layoutParams = footerParams
        }
    }.also { applyAnimationProperties(it) }

    private fun getFooterRevealAnimation(currMargin : Int) = object: Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            footerParams.bottomMargin = min(
                ((1 - interpolatedTime) *
                        (-footerHeight + initialBottomMargin - currMargin)
                        ).toInt(),
                initialBottomMargin
            )
            footer.layoutParams = footerParams
        }
    }.also { applyAnimationProperties(it) }

    private fun applyAnimationProperties(animation: Animation) = animation.apply {
        duration = 200
        interpolator = DecelerateInterpolator()
    }
}