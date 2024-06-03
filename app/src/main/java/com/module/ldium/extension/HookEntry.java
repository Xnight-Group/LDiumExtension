package com.module.ldium.extension;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final String package_name = lpparam.packageName;

        // Paint
        {
            XposedHelpers.findAndHookConstructor(Paint.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    final Paint self = (Paint) param.thisObject;
                    self.setAntiAlias(false);
                    self.setSubpixelText(false);
                    self.setDither(false);
                    self.setFilterBitmap(false);
                }
            });

            XposedHelpers.findAndHookMethod(Paint.class, "setTypeface", Typeface.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.args[0] = null;
                }
            });
        }

        // TextView
        {
            XposedHelpers.findAndHookMethod(TextView.class, "setEllipsize", TextUtils.TruncateAt.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    final TextUtils.TruncateAt at = (TextUtils.TruncateAt) param.args[0];
                    if (at == TextUtils.TruncateAt.MARQUEE) {
                        param.args[0] = TextUtils.TruncateAt.END;
                    }
                }
            });
        }

        // Drawable
        {
            XposedHelpers.findAndHookMethod(BitmapDrawable.class, "draw", Canvas.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(null);
                }
            });
        }
    }
}
