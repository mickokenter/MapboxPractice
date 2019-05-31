package com.matthewqiu.mapboxpractice;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

public class MirroredMapView extends MapView {

    public MirroredMapView(@NonNull Context context) {
        super(context);
    }

    public MirroredMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MirroredMapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MirroredMapView(@NonNull Context context, @Nullable MapboxMapOptions options) {
        super(context, options);
    }

//    @Override
//    protected void dispatchDraw(Canvas canvas){
//        canvas.translate(getWidth(), 0);
//        canvas.scale(-1, 1);
//        super.dispatchDraw(canvas);
//    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.translate(getWidth(), 0);
        canvas.scale(-1, 1);
        super.onDraw(canvas);
    }
}
