package org.maplibre.reactnative.modules;

import android.os.Handler;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import org.maplibre.android.MapLibre;
import org.maplibre.android.WellKnownTileServer;
import org.maplibre.reactnative.components.camera.constants.CameraMode;
import org.maplibre.reactnative.components.styles.sources.MLRNSource;
import org.maplibre.reactnative.events.constants.EventTypes;
import org.maplibre.reactnative.http.CustomHeadersInterceptor;
import org.maplibre.reactnative.location.UserLocationVerticalAlignment;
import org.maplibre.reactnative.location.UserTrackingMode;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import org.maplibre.android.module.http.HttpRequestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

@ReactModule(name = MLRNModule.REACT_CLASS)
public class MLRNModule extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "MLRNModule";

    public static final String DEFAULT_STYLE_URL = "https://gateway.mapmetrics.org/styles/?fileName=91cf50f5-e3cb-45d3-a1ab-f2f575f6c9b2/urbcalm.json&token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI5MWNmNTBmNS1lM2NiLTQ1ZDMtYTFhYi1mMmY1NzVmNmM5YjIiLCJzY29wZSI6WyJtYXBzIiwiYXV0b2NvbXBsZXRlIiwiZ2VvY29kZSJdLCJpYXQiOjE3NDcxNDE5OTB9.mAtqzwAPCcQhyEr45AgPaCFor4hePZu7tpoUrJUGwGs";

    private static boolean customHeaderInterceptorAdded = false;

    private Handler mUiThreadHandler;
    private ReactApplicationContext mReactContext;

    public MLRNModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        mReactContext = reactApplicationContext;
    }

    @Override
    public void initialize() {
        initializeMapLibreInstance();
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @Nullable
    public Map<String, Object> getConstants() {
        // map style urls
        Map<String, String> styleURLS = new HashMap<>();
        styleURLS.put("Default", DEFAULT_STYLE_URL);

        // events
        Map<String, String> eventTypes = new HashMap<>();
        eventTypes.put("MapClick", EventTypes.MAP_CLICK);
        eventTypes.put("MapLongClick", EventTypes.MAP_LONG_CLICK);
        eventTypes.put("RegionWillChange", EventTypes.REGION_WILL_CHANGE);
        eventTypes.put("RegionIsChanging", EventTypes.REGION_IS_CHANGING);
        eventTypes.put("RegionDidChange", EventTypes.REGION_DID_CHANGE);
        eventTypes.put("UserLocationUpdated", EventTypes.USER_LOCATION_UPDATED);
        eventTypes.put("WillStartLoadingMap", EventTypes.WILL_START_LOADING_MAP);
        eventTypes.put("DidFinishLoadingMap", EventTypes.DID_FINISH_LOADING_MAP);
        eventTypes.put("DidFailLoadingMap", EventTypes.DID_FAIL_LOADING_MAP);
        eventTypes.put("WillStartRenderingFrame", EventTypes.WILL_START_RENDERING_FRAME);
        eventTypes.put("DidFinishRenderingFrame", EventTypes.DID_FINISH_RENDERING_FRAME);
        eventTypes.put("DidFinishRenderingFrameFully", EventTypes.DID_FINISH_RENDERING_FRAME_FULLY);
        eventTypes.put("WillStartRenderingMap", EventTypes.WILL_START_RENDERING_MAP);
        eventTypes.put("DidFinishRenderingMap", EventTypes.DID_FINISH_RENDERING_MAP);
        eventTypes.put("DidFinishRenderingMapFully", EventTypes.DID_FINISH_RENDERING_MAP_FULLY);
        eventTypes.put("DidFinishLoadingStyle", EventTypes.DID_FINISH_LOADING_STYLE);

        // user tracking modes
        Map<String, Integer> userTrackingModes = new HashMap<>();
        userTrackingModes.put("None", UserTrackingMode.NONE);
        userTrackingModes.put("Follow", UserTrackingMode.FOLLOW);
        userTrackingModes.put("FollowWithCourse", UserTrackingMode.FollowWithCourse);
        userTrackingModes.put("FollowWithHeading", UserTrackingMode.FollowWithHeading);

        // user location vertical alignment
        Map<String, Integer> userLocationVerticalAlignment = new HashMap<>();
        userLocationVerticalAlignment.put("Center", UserLocationVerticalAlignment.CENTER);
        userLocationVerticalAlignment.put("Top", UserLocationVerticalAlignment.TOP);
        userLocationVerticalAlignment.put("Bottom", UserLocationVerticalAlignment.BOTTOM);

        // camera modes
        Map<String, Integer> cameraModes = new HashMap<>();
        cameraModes.put("Flight", CameraMode.FLIGHT);
        cameraModes.put("Ease", CameraMode.EASE);
        cameraModes.put("Linear", CameraMode.LINEAR);
        cameraModes.put("None", CameraMode.NONE);

        // style source constants
        Map<String, String> styleSourceConsts = new HashMap<>();
        styleSourceConsts.put("DefaultSourceID", MLRNSource.DEFAULT_ID);

        // offline region download states
        Map<String, Integer> offlinePackDownloadStates = new HashMap<>();
        offlinePackDownloadStates.put("Inactive", MLRNOfflineModule.INACTIVE_REGION_DOWNLOAD_STATE);
        offlinePackDownloadStates.put("Active", MLRNOfflineModule.ACTIVE_REGION_DOWNLOAD_STATE);
        offlinePackDownloadStates.put("Complete", MLRNOfflineModule.COMPLETE_REGION_DOWNLOAD_STATE);

        // offline module callback names
        Map<String, String> offlineModuleCallbackNames = new HashMap<>();
        offlineModuleCallbackNames.put("Error", MLRNOfflineModule.OFFLINE_ERROR);
        offlineModuleCallbackNames.put("Progress", MLRNOfflineModule.OFFLINE_PROGRESS);

        // location module callback names
        Map<String, String> locationModuleCallbackNames = new HashMap<>();
        locationModuleCallbackNames.put("Update", MLRNLocationModule.LOCATION_UPDATE);

        return MapBuilder.<String, Object>builder()
                .put("StyleURL", styleURLS)
                .put("EventTypes", eventTypes)
                .put("UserTrackingModes", userTrackingModes)
                .put("UserLocationVerticalAlignment", userLocationVerticalAlignment)
                .put("CameraModes", cameraModes)
                .put("StyleSource", styleSourceConsts)
                .put("OfflinePackDownloadState", offlinePackDownloadStates)
                .put("OfflineCallbackName", offlineModuleCallbackNames)
                .put("LocationCallbackName", locationModuleCallbackNames)
                .build();
    }

    /**
     * @deprecated This will be removed in the next major version.
     * @see https://github.com/maplibre/maplibre-react-native/issues/25#issuecomment-1382382044
     */
    @Deprecated
    @ReactMethod
    public void setAccessToken(final String accessToken) {
        mReactContext.runOnUiQueueThread(new Runnable() {
            @Override
            public void run() {
                if (accessToken == null) {
                    MapLibre.getInstance(getReactApplicationContext());
                } else {
                    MapLibre.getInstance(getReactApplicationContext(), accessToken, WellKnownTileServer.Mapbox);
                }
            }
        });
    }

    @ReactMethod
    public void removeCustomHeader(final String headerName) {
        mReactContext.runOnUiQueueThread(new Runnable() {
            @Override
            public void run() {
                CustomHeadersInterceptor.INSTANCE.removeHeader(headerName);
            }
        });
    }

    @ReactMethod
    public void addCustomHeader(final String headerName, final String headerValue) {
        mReactContext.runOnUiQueueThread(new Runnable() {
            @Override
            public void run() {
                if (!customHeaderInterceptorAdded) {
                    Log.v("MapLibre-HTTP", "Adding HTTP interceptor");
                    OkHttpClient httpClient = new OkHttpClient.Builder()
                            .addInterceptor(chain -> {
                                Request request = chain.request();
                                String url = request.url().toString();
                                
                                // Log request details
                                Log.v("MapLibre-HTTP", "=== HTTP Request ===");
                                Log.v("MapLibre-HTTP", "URL: " + url);
                                Log.v("MapLibre-HTTP", "Method: " + request.method());
                                Log.v("MapLibre-HTTP", "Headers: " + request.headers());
                                
                                try {
                                    Response response = chain.proceed(request);
                                    
                                    // Log response details
                                    Log.v("MapLibre-HTTP", "=== HTTP Response ===");
                                    Log.v("MapLibre-HTTP", "URL: " + url);
                                    Log.v("MapLibre-HTTP", "Status: " + response.code());
                                    Log.v("MapLibre-HTTP", "Response Headers: " + response.headers());
                                    
                                    return response;
                                } catch (IOException e) {
                                    Log.e("MapLibre-HTTP", "Request failed for URL: " + url, e);
                                    throw e;
                                }
                            })
                            .addInterceptor(CustomHeadersInterceptor.INSTANCE)
                            .dispatcher(getDispatcher())
                            .build();
                    
                    HttpRequestUtil.setOkHttpClient(httpClient);
                    customHeaderInterceptorAdded = true;
                    Log.v("MapLibre-HTTP", "HTTP interceptor added successfully");
                }

                Log.v("MapLibre-HTTP", "Adding custom header: " + headerName + " = " + headerValue);
                CustomHeadersInterceptor.INSTANCE.addHeader(headerName, headerValue);
            }
        });
    }

    /**
     * @deprecated This will be removed in the next major version.
     * @see https://github.com/maplibre/maplibre-react-native/issues/25#issuecomment-1382382044
     */
    @Deprecated
    @ReactMethod
    public void getAccessToken(Promise promise) {
        String token = MapLibre.getApiKey();
        if (token == null) {
            promise.reject("missing_access_token", "No access token has been set");
        } else {
            promise.resolve(token);
        }
    }

    @ReactMethod
    public void setConnected(final boolean connected) {
        mReactContext.runOnUiQueueThread(new Runnable() {
            @Override
            public void run() {
                MapLibre.setConnected(connected);
            }
        });
    }

    private Dispatcher getDispatcher() {
        Dispatcher dispatcher = new Dispatcher();
        // Matches core limit set on
        // https://github.com/mapbox/mapbox-gl-native/blob/master/platform/android/src/http_file_source.cpp#L192
        dispatcher.setMaxRequestsPerHost(20);
        Log.d("MapLibre", "Created dispatcher with max requests per host: 20");
        return dispatcher;
    }

    private void initializeMapLibreInstance() {
        mReactContext.runOnUiQueueThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("MapLibre", "Initializing MapLibre instance");
                    
                    // First initialize MapLibre
                    MapLibre.getInstance(getReactApplicationContext());
                    Log.d("MapLibre", "MapLibre instance initialized successfully");
                    
                    // Then set up the HTTP client with cookie handling
                    try {
                        OkHttpClient httpClient = new OkHttpClient.Builder()
                            .cookieJar(new CookieJar() {
                                private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

                                @Override
                                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                                    Log.d("MapLibre-Cookie", "=== Cookie Save ===");
                                    Log.d("MapLibre-Cookie", "URL: " + url.toString());
                                    Log.d("MapLibre-Cookie", "Cookies received: " + cookies.size());
                                    for (Cookie cookie : cookies) {
                                        Log.d("MapLibre-Cookie", String.format("Cookie: name=%s, domain=%s, path=%s, expires=%s, secure=%b, httpOnly=%b",
                                            cookie.name(), cookie.domain(), cookie.path(),
                                            cookie.expiresAt(), cookie.secure(), cookie.httpOnly()));
                                    }
                                    cookieStore.put(url.host(), cookies);
                                    Log.d("MapLibre-Cookie", "Current cookie store size: " + cookieStore.size());
                                    Log.d("MapLibre-Cookie", "=== End Cookie Save ===");
                                }

                                @Override
                                public List<Cookie> loadForRequest(HttpUrl url) {
                                    Log.d("MapLibre-Cookie", "=== Cookie Load ===");
                                    Log.d("MapLibre-Cookie", "URL: " + url.toString());
                                    List<Cookie> cookies = cookieStore.get(url.host());
                                    if (cookies != null) {
                                        Log.d("MapLibre-Cookie", "Found " + cookies.size() + " cookies for host: " + url.host());
                                        for (Cookie cookie : cookies) {
                                            Log.d("MapLibre-Cookie", String.format("Sending cookie: name=%s, domain=%s",
                                                cookie.name(), cookie.domain()));
                                        }
                                    } else {
                                        Log.d("MapLibre-Cookie", "No cookies found for host: " + url.host());
                                    }
                                    Log.d("MapLibre-Cookie", "=== End Cookie Load ===");
                                    return cookies != null ? cookies : new ArrayList<>();
                                }
                            })
                            .addInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request request = chain.request();
                                    String url = request.url().toString();
                                    
                                    // Log request
                                    Log.d("MapLibre-HTTP", String.format("Request: %s %s", request.method(), url));
                                    Log.d("MapLibre-HTTP", "Headers: " + request.headers());
                                    
                                    try {
                                        Response response = chain.proceed(request);
                                        
                                        // Log response
                                        Log.d("MapLibre-HTTP", String.format("Response: %s %s", response.code(), url));
                                        Log.d("MapLibre-HTTP", "Response Headers: " + response.headers());
                                        
                                        return response;
                                    } catch (IOException e) {
                                        Log.e("MapLibre-HTTP", "Request failed: " + url, e);
                                        throw e;
                                    }
                                }
                            })
                            .dispatcher(getDispatcher())
                            .build();
                        
                        HttpRequestUtil.setOkHttpClient(httpClient);
                        Log.d("MapLibre", "HTTP client configured successfully");
                    } catch (Exception e) {
                        Log.e("MapLibre", "Failed to set up HTTP client", e);
                        // Don't throw here, as MapLibre is already initialized
                    }
                } catch (Exception e) {
                    Log.e("MapLibre", "Failed to initialize MapLibre", e);
                    throw new RuntimeException("Failed to initialize MapLibre", e);
                }
            }
        });
    }
}
