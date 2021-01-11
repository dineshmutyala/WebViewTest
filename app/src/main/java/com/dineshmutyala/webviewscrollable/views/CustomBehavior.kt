package com.dineshmutyala.webviewscrollable.views

import android.R.attr
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView


class CustomBehavior(context: Context, attributeSet: AttributeSet?) :
    CoordinatorLayout.Behavior<View>(context, attributeSet) {
    private var initialScroll = -1
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is NestedScrollView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        val dependencyLocation = IntArray(2)
        val childLocation = IntArray(2)

        dependency.getLocationInWindow(dependencyLocation)
        child.getLocationInWindow(childLocation)

        if(initialScroll > (dependency as NestedScrollView).scrollY) {
            if(child.scaleY > 0) {
                child.scaleY -= 0.1f
                Log.d("TESTEST", " Scroll Up - ${child.scaleY}")
            }
        } else if (initialScroll < (dependency as NestedScrollView).scrollY){
            if(child.scaleY < 1f) {
                child.scaleY += .1f
                Log.d("TESTEST", " Scroll Down - ${child.scaleY}")
            }
        }
        initialScroll = (dependency as NestedScrollView).scrollY

//        val diff = childLocation[1] - dependencyLocation[1].toFloat()
//        if (diff > 0) {
//            val scale = diff / childLocation[1].toFloat()
//            Log.d("TESTEST", "scale == ${(dependency as NestedScrollView).scrollY}")
//            child.scaleX = 1 + scale
//            child.scaleY = 1 + scale
//        }
        return false
    }
}