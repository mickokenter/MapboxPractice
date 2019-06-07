package com.matthewqiu.mapboxpractice;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.matthewqiu.mapboxnavilib.navigation.MapboxNavigation;
import com.matthewqiu.mapboxnavilib.routeprogress.ProgressChangeListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseActivity extends Activity {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    private static final String JSON_EXTENSION = ".json";

    private MapboxNavigation navigation;
    private String filename;
    private ProgressChangeListener progressHistoryListener = (location, routeProgress) -> executeStoreHistoryTask();

    public void addNavigationForHistory(@NonNull MapboxNavigation navigation) {
        if (navigation == null) {
            throw new IllegalArgumentException("MapboxNavigation cannot be null");
        }
        this.navigation = navigation;
        navigation.addProgressChangeListener(progressHistoryListener);
        navigation.toggleHistory(true);
        filename = buildFileName();
    }

    protected void executeStoreHistoryTask() {
//        new StoreHistoryTask(navigation, filename).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigation.toggleHistory(false);
    }

    private String buildFileName() {
        StringBuilder sb = new StringBuilder();
        sb.append(obtainCurrentTimeStamp());
        sb.append(JSON_EXTENSION);
        return sb.toString();
    }

    private String obtainCurrentTimeStamp() {
        Date now = new Date();
        String strDate = DATE_FORMAT.format(now);
        return strDate;
    }
}
