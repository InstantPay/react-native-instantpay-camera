package com.instantpaycamera

import android.graphics.Color
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.InstantpayCameraViewManagerInterface
import com.facebook.react.viewmanagers.InstantpayCameraViewManagerDelegate

@ReactModule(name = InstantpayCameraViewManager.NAME)
class InstantpayCameraViewManager : SimpleViewManager<InstantpayCameraView>(),
  InstantpayCameraViewManagerInterface<InstantpayCameraView> {
  private val mDelegate: ViewManagerDelegate<InstantpayCameraView>

  init {
    mDelegate = InstantpayCameraViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<InstantpayCameraView>? {
    return mDelegate
  }

  override fun getName(): String {
    return NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): InstantpayCameraView {
    return InstantpayCameraView(context)
  }

  @ReactProp(name = "color")
  override fun setColor(view: InstantpayCameraView?, color: Int?) {
    view?.setBackgroundColor(color ?: Color.TRANSPARENT)
  }

  companion object {
    const val NAME = "InstantpayCameraView"
  }
}
