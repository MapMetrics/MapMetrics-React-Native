package org.maplibre.reactnative;

import android.util.Log;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.maplibre.android.MapLibre;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ReactModule(name = MLRNModule.NAME)
public class MLRNModule extends ReactContextBaseJavaModule {
  public static final String NAME = "MLRNModule";
  private boolean initialized = false;

  public MLRNModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void initialize() {
    if (initialized) {
      return;
    }

    try {
      // Initialize MapLibre with the application context
      MapLibre.getInstance(getReactApplicationContext());
      
      // Set up OkHttp client with cookie handling
      OkHttpClient client = new OkHttpClient.Builder()
        .cookieJar(new CookieJar() {
          private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

          @Override
          public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            Log.d("CookieDebug", "=== Cookie Save ===");
            Log.d("CookieDebug", "URL: " + url.toString());
            Log.d("CookieDebug", "Cookies received: " + cookies.size());
            for (Cookie cookie : cookies) {
              Log.d("CookieDebug", String.format("Cookie: name=%s, value=%s, domain=%s, path=%s, expires=%s, secure=%b, httpOnly=%b",
                cookie.name(), cookie.value(), cookie.domain(), cookie.path(),
                cookie.expiresAt(), cookie.secure(), cookie.httpOnly()));
            }
            cookieStore.put(url.host(), cookies);
            Log.d("CookieDebug", "Current cookie store size: " + cookieStore.size());
            Log.d("CookieDebug", "=== End Cookie Save ===");
          }

          @Override
          public List<Cookie> loadForRequest(HttpUrl url) {
            Log.d("CookieDebug", "=== Cookie Load ===");
            Log.d("CookieDebug", "URL: " + url.toString());
            List<Cookie> cookies = cookieStore.get(url.host());
            if (cookies != null) {
              Log.d("CookieDebug", "Found " + cookies.size() + " cookies for host: " + url.host());
              for (Cookie cookie : cookies) {
                Log.d("CookieDebug", String.format("Sending cookie: name=%s, value=%s, domain=%s",
                  cookie.name(), cookie.value(), cookie.domain()));
              }
            } else {
              Log.d("CookieDebug", "No cookies found for host: " + url.host());
            }
            Log.d("CookieDebug", "=== End Cookie Load ===");
            return cookies != null ? cookies : new ArrayList<>();
          }
        })
        .build();

      initialized = true;
      Log.d("MapLibre", "MapLibre initialized successfully");
    } catch (Exception e) {
      Log.e("MapLibre", "Failed to initialize MapLibre", e);
      throw new RuntimeException("Failed to initialize MapLibre", e);
    }
  }
}