package com.huella.acceso;

import android.app.Application;
import org.json.JSONObject;

// Una Application existe mientras vive el proceso de Android.
// Todas las Activities comparten esta misma instancia y su cliente HTTP.
public final class HuellaApp extends Application {
    VetApi api;
    JSONObject user;
}
