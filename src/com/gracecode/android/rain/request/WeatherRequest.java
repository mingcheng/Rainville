package com.gracecode.android.rain.request;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: feelinglucky
 * Date: 15/1/6
 */
public class WeatherRequest extends JsonObjectRequest {
    public static final String WEATHER_API = "http://www.baidu.com/home/xman/data/superload?type=weather";
    private static final String USER_AGENT = "Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko";

    public WeatherRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, WEATHER_API, null, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<>();
        params.put("Referer", "http://www.baidu.com/");
        params.put("User-Agent", USER_AGENT);
        params.put("Cache-Control", "no-cache");
        params.put("Pragma", "no-cache");
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String data = new String(response.data, "UTF-8");
            JSONObject jsonObject = new JSONObject(data);
            JSONObject today = jsonObject.getJSONObject("data").getJSONObject("weather").getJSONObject("content");
            return Response.success(today, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception je) {
            return Response.error(new ParseError(je));
        }
    }
}
