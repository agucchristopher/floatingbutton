// FloatingWidgetModule.java (Android)
package com.yourapp.floatingwidget;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class FloatingWidgetModule extends ReactContextBaseJavaModule {
    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams params;
    private final ReactApplicationContext reactContext;

    public FloatingWidgetModule(ReactApplicationContext context) {
        super(context);
        this.reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "FloatingWidget";
    }

    @ReactMethod
    public void checkOverlayPermission(Promise promise) {
        if (Settings.canDrawOverlays(reactContext)) {
            promise.resolve(true);
        } else {
            promise.resolve(false);
        }
    }

    @ReactMethod
    public void requestOverlayPermission(Promise promise) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + reactContext.getPackageName()));
        getCurrentActivity().startActivityForResult(intent, 0);
        promise.resolve(null);
    }

    @ReactMethod
    public void showWidget(Promise promise) {
        if (!Settings.canDrawOverlays(reactContext)) {
            promise.reject("PERMISSION_DENIED", "Overlay permission is required");
            return;
        }

        try {
            windowManager = (WindowManager) reactContext.getSystemService(reactContext.WINDOW_SERVICE);
            
            // Inflate the floating view layout
            LayoutInflater inflater = LayoutInflater.from(reactContext);
            floatingView = inflater.inflate(R.layout.floating_widget_layout, null);

            // Set up the WindowManager parameters
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                            : WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    android.graphics.PixelFormat.TRANSLUCENT
            );

            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = 0;
            params.y = 100;

            // Add touch listener for dragging
            setupTouchListener();

            // Add the view to the window
            windowManager.addView(floatingView, params);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void hideWidget(Promise promise) {
        try {
            if (floatingView != null && windowManager != null) {
                windowManager.removeView(floatingView);
                floatingView = null;
            }
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }

    private void setupTouchListener() {
        final int[] initialX = new int[1];
        final int[] initialY = new int[1];
        final float[] initialTouchX = new float[1];
        final float[] initialTouchY = new float[1];

        floatingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX[0] = params.x;
                        initialY[0] = params.y;
                        initialTouchX[0] = event.getRawX();
                        initialTouchY[0] = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX[0] + (int) (event.getRawX() - initialTouchX[0]);
                        params.y = initialY[0] + (int) (event.getRawY() - initialTouchY[0]);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });
    }
}

