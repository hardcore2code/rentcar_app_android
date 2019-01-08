package com.static4u.netcar.utils.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.static4u.netcar.app.App;
import com.static4u.netcar.utils.CommonUtil;
import com.static4u.netcar.utils.SLog;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.security.KeyStore;

import static com.static4u.netcar.constant.URLConstant.FORCE_LOG;

public class HttpClientUtil {
    private OnHttpListener listener;
    private static final int TIME_CONN = 20000, TIME_SO = 60000;

    public void setListener(OnHttpListener listener) {
        this.listener = listener;
    }

    public interface OnHttpListener {

        /**
         * 请求成功
         *
         * @param responseStr
         */
        void onSuccess(String responseStr);

        /**
         * 请求失败
         */
        void onFailure();
    }

    public HttpClientUtil() {
    }

    public HttpClientUtil(OnHttpListener listener) {
        this.listener = listener;
    }

    private static String cookies;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (listener != null) {
                if (msg.what == 1) {
                    String respStr = (String) msg.obj;
                    listener.onSuccess(respStr);
                } else {
                    listener.onFailure();
                }
            }
        }
    };

    public void postHttpClient(final String url, final String xmlInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpPost post = new HttpPost(url);
                    post.addHeader("Content-Type","text/xml;charset=utf-8");

                    HttpEntity entity = new StringEntity(xmlInfo, "UTF-8");
                    post.setEntity(entity);

                    //setRequestCookies(post);
                    HttpClient client = getNewHttpClient(App.getContext());
                    HttpResponse response = client.execute(post);

                    int code = response.getStatusLine().getStatusCode();
                    if (code == 200) {
                        String respStr = EntityUtils.toString(response.getEntity());

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = respStr;
                        handler.sendMessage(msg);
                        SLog.i("http response : " + respStr);
                    } else {
                        SLog.e("http failed code : " + code);
                        handler.sendEmptyMessage(0);
                    }
                } catch (Exception e) {
                    if (FORCE_LOG) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                    SLog.e("接口请求失败 url : " + url);
                }
            }
        }).start();
    }

    private void setRequestCookies(HttpMessage reqMsg) {
        if (!TextUtils.isEmpty(cookies)) {
            reqMsg.setHeader("Cookie", cookies);
        }
    }

    private void appendCookies(HttpMessage resMsg) {
        Header setCookieHeader = resMsg.getFirstHeader("Set-Cookie");
        if (setCookieHeader != null && !TextUtils.isEmpty(setCookieHeader.getValue())) {
            String setCookie = setCookieHeader.getValue();
            if (TextUtils.isEmpty(cookies)) {
                cookies = setCookie;
            } else {
                cookies = cookies + ";" + setCookie;
            }
        }
    }

    /**
     * 强解析xml标签
     *
     * @param source xml字符串
     * @param key    标签名
     * @return 标签值
     */
    protected String parseValueByKey(String source, String key) {
        try {
            String locA = "<" + key + ">", locB = "</" + key + ">";

            String result = source.substring(source.indexOf(locA) + locA.length(), source.indexOf(locB));
            if (CommonUtil.isEmptyOrNull(result)) {
                return "";
            } else {
                return result;
            }
        } catch (Exception e) {
            if (FORCE_LOG) {
                e.printStackTrace();
            }
            return "";
        }
    }

    private static HttpClient getNewHttpClient(Context context) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(params, TIME_CONN);
            HttpConnectionParams.setSoTimeout(params, TIME_SO);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            // Set the default socket timeout (SO_TIMEOUT) // in
            // milliseconds which is the timeout for waiting for data.
            HttpConnectionParams.setConnectionTimeout(params, TIME_CONN);
            HttpConnectionParams.setSoTimeout(params, TIME_SO);
            HttpClient client = new DefaultHttpClient(ccm, params);
            return client;
        } catch (Exception e) {
            if (FORCE_LOG) {
                e.printStackTrace();
            }
            return new DefaultHttpClient();
        }
    }
}
