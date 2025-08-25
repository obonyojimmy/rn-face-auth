package com.rnfaceauth

import android.graphics.Color
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.RnFaceAuthViewManagerInterface
import com.facebook.react.viewmanagers.RnFaceAuthViewManagerDelegate

@ReactModule(name = RnFaceAuthViewManager.NAME)
class RnFaceAuthViewManager : SimpleViewManager<RnFaceAuthView>(),
  RnFaceAuthViewManagerInterface<RnFaceAuthView> {
  private val mDelegate: ViewManagerDelegate<RnFaceAuthView>

  init {
    mDelegate = RnFaceAuthViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<RnFaceAuthView>? {
    return mDelegate
  }

  override fun getName(): String {
    return NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): RnFaceAuthView {
    return RnFaceAuthView(context)
  }

  @ReactProp(name = "color")
  override fun setColor(view: RnFaceAuthView?, color: String?) {
    view?.setBackgroundColor(Color.parseColor(color))
  }

  companion object {
    const val NAME = "RnFaceAuthView"
  }
}
