package me.charghet.tools.requests;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;
import java.util.List;
import java.net.*;
import java.io.*;

/**
 * 发送url请求，能够自动保存请求过程中的cookie信息。
 * @author charghet
 *
 */
public class Session {
    private CookieManager cookieManager = new CookieManager();
    private Properties headers;
    /**
     * 构造一个请求头为空的Session实例。
     */
    public Session() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        headers = new Properties();
    }
    /**
     * 构造一个指定请求头的Session实例。
     * <p> 如果指定的请求头包含 Cookie 字段，则自动将Cookie信息添加到CookieManager中。
     * 
     * @param headers 封装了请求头信息的Properties类，此类在java.util中
     */
    public Session(Properties headers) {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        String cookie = headers.getProperty("Cookie");
        if(cookie != null) {
            cookieManager.add(cookie);
        }
        this.headers = headers;
    }
    /**
     * 获得一个包含默认请求头的Session实例。
     * <p> 默认请求头各字段如下：
     * <p> Accept = *&frasl;*
     * <p> Connection = keep-alive
     * <p> User-Agent = Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36
     * 
     * @return 包含默认请求头的Session实例
     */
    public static Session createDefaultSession() {
        Properties defaultHeaders = new Properties();
        defaultHeaders.setProperty("Accept", "*/*");
        defaultHeaders.setProperty("Connection", "keep-alive");
        defaultHeaders.setProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
        return new Session(defaultHeaders);
    }
    /**
     * 设置此类是否应该自动执行 HTTP 重定向（响应代码为 3xx 的请求）。默认情况下为 true。
     * 
     * @param set 指示是否进行 HTTP 重定向的 boolean 值
     */
    public static void setFollowRedirects(boolean set) {
        HttpURLConnection.setFollowRedirects(set);
    }
    /**
     * 返回指示是否应该自动执行 HTTP 重定向 (3xx) 的 boolean 值。
     * 
     * @return 如果应该自动执行 HTTP 重定向，则返回 true；否则返回 false。
     */
    public static boolean getFollowRedirects() {
        return HttpURLConnection.getFollowRedirects();
    }
    /**
     * 设置请求头。
     * <p> 此操作将替换Session中的请求头为指定的请求头。
     * 
     * @param headers 封装了请求头信息的Properties类，此类在java.util中
     */
    public void setHeaders(Properties headers) {
        this.headers = headers;
    }
    /**
     * 设置请求头。
     * <p> 此操作将替换Session中的请求头为指定的请求头。
     * 
     * @param headers 封装了请求头信息的字符串二维数组
     * @throws FormatException 如果二维数组的大小不为headers[][2]
     */
    public void setHeaders(String[][] headers) throws FormatException {
        Properties properties = new Properties();
        for(String[] header : headers) {
            if(header.length == 2) {
                properties.setProperty(header[0], header[1]);
            }else {
                throw new FormatException("headers[" + headers.length + "].length:" + header.length);
            }
        }
        this.headers = properties;
    }
    /**
     * 添加一条字段到请求头中。
     * <p> 如果请求头中已包含字段名相同的字段，则覆盖已有字段的值。
     * 
     * @param key 字段名
     * @param value 字段值
     */
    public void addHeader(String key, String value) {
        headers.setProperty(key, value);
    }
    /**
     * 获得请求头中指定字段名的值。
     * <p> 如果不存在该字段，则返回null
     * 
     * @param key 字段名
     * @return 字段值 
     */
    public String getHeader(String key) {
        return headers.getProperty(key);
    }
    /**
     * 从请求头中删除一条字段信息。
     * <p> 如果请求头中未包含该字段，则不执行任何操作。
     * @param key 字段名
     * @return 请求头中该字段的值，如果请求头中未包含该字段，则返回null
     */
    public String removeHeader(String key) {
        return (String)headers.remove(key);
    }
    /**
     * 获得请求头信息的二维字符串数组。
     * <p> 该二维字符串数组大小为String[请求头中的字段数][2]
     * @return 二维字符串数组 String[][0]为字段名，String[][1]为字段值
     */
    public String[][] getHeaders() {
        String[][] result = new String[headers.size()][2];
        Enumeration<?> keys = headers.propertyNames();
        for(int i = 0;keys.hasMoreElements();i++) {
            result[i][0] = (String)keys.nextElement();
            result[i][1] = headers.getProperty(result[i][0]);
        }
        return result;
    }
    /**
     * 获得封装了请求头信息的Properties类。
     * 
     * @return Properties java.util中的类
     */
    public Properties getPropertyHeaders() {
        return headers;
    }
    /**
     * 向控制台输出请求头信息。
     * <p> 此方法用于调试。
     */
    public void printHeaders() {
        String[][] headers = getHeaders();
        System.out.println("Request Headers:");
        for(String[] header : headers) {
            System.out.println(header[0] + ": " + header[1]);
        }
    }
    /**
     * 向控制台输出Cookie信息。
     * <p> 此方法用于调试，调用内置的CookieManager.printCookies()
     */
    public void printCookies() {
        cookieManager.printCookies();
    }
    /**
     * 为HttpURLConnection添加Session中的所有请求头信息。
     * 
     * @param connection HttpURLConnection实例
     */
    private void setConnectionHeaders(HttpURLConnection connection) {
        String[][] myheaders = getHeaders();
        for(String[] header : myheaders) {
           connection.addRequestProperty(header[0], header[1]); 
        }
    }
    /**
     * 获得Session内置的CookieManager
     * 
     * @return Session内置的CookieManager
     */
    public CookieManager getCookieManager() {
        return cookieManager;
    }
    /**
     * 替换Session内置的CookieManager为指定的CookieManager
     * 
     * @param cookieManager 指定的CookieManager
     */
    public void setCookieManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }
    /**
     * 添加一条Cookie信息。
     * <p> 通过调用CookieManager.add(String name, String value)实现。
     * <p> 更多添加Cooke的方法，请先用 getCookieManager()获得CookieManager，再用其中的方法进行添加。
     * 
     * @param name cookie的名称
     * @param value cookie的值
     */
    public void addCookie(String name, String value) {
        cookieManager.add(name, value);
    }
    /**
     * 解析"Cookie"字段并添加解析后的一条cookie信息。
     * <p> 只解析成一个Cookie信息。
     * <p> 通过调用内置的CookieManager.add(String cookie)实现。
     * 
     * @param cookie 根据 set-cookie 或 set-cookie2 头字符串
     */
    public void addCookie(String cookie) {
        cookieManager.add(cookie);
    }
    /**
     * 解析"Cookie"字段并添加解析后的多条cookie信息。
     * <p> 能够将"Cookie"字段解析成多个Cookie信息。
     * <p> 通过调用内置的CookieManager.adds(String cookie)实现。
     * 
     * @param cookies Cookie 字段
     */
    public void addCookies(String cookies) {
        cookieManager.adds(cookies);
    }
    /**
     * 获取多个"Set-Cookie"字段值。
     * 
     * @param connection HttpURLConnection实例
     */
    private void setCookies(HttpURLConnection connection) {
        List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
        if(cookies != null) {
            for(String cookie : cookies) {
                cookieManager.add(cookie);
            }
            headers.setProperty("Cookie", cookieManager.getString());
        }
    }
    /**
     * 发送get请求。
     * <p> 注意：
     * <p> 该方法不会自动将url字符串进行urlencode编码，如果url字符串包含中文字符等，请先将url字符串进行urlencode编码。
     * <p> 推荐使用 get(String url, URLParam param)，该方法能够自动将url字符串和url参数进行urlencode编码。
     * 
     * @param url 目标url地址
     * @return Response 响应url请求的类
     * @throws RequestException 如果在请求过程中发生错误
     */
    public Response get(String url) throws RequestException{
        URL myurl;
        HttpURLConnection connection;
        Response response;
            try {
                myurl = new URL(url);
                connection = (HttpURLConnection)myurl.openConnection();
                connection.setRequestMethod("GET");
                setConnectionHeaders(connection);
                connection.setRequestProperty("Cookie", cookieManager.getString());
                connection.connect();
                response = new Response(connection);
                setCookies(connection);
            }catch(Exception e) {
                throw new RequestException(e.toString(),e.getCause());
            }
        return response;
        
    }
    /**
     * 发送get请求。
     * <p> 该方法能够自动将url字符串和url参数进行urlencode编码，请勿将urlencode编码后的参数传入。
     * <p> url地址和url参数应该分开，请勿将url参数附加到url字符串中。
     * 
     * @param url 目标url地址
     * @param param 封装了需要传递的参数的URLParam类
     * @return Response 响应url请求的类
     * @throws RequestException 如果在请求过程中发生错误
     * @throws FormatException 如果对url字符串或url参数进行urlencode编码过程中发生错误
     */
    public Response get(String url, URLParam param) throws RequestException, FormatException{
        url = URLUtil.encodeURL(url) + "?" + param.getEncodeString();
        return get(url);
    }
    /**
     * 发送post请求。
     * <p> 注意：
     * <p> 该方法能够自动将url字符串进行urlencode编码，但不会对url参数进行urlencode编码。
     * <p> 推荐使用post(String url, URLParam param)，该方法能够该方法能够自动将url字符串和url参数进行urlencode编码。
     * 
     * @param url 目标url地址
     * @param param 需要传递的参数，如："name1=vdalue1&amp;name2=value2" 如果包含中文字符等，请先进行urlencode编码
     * @return Response 响应url请求的类
     * @throws FormatException 如果对url字符串进行urlencode编码过程中发生错误
     * @throws RequestException 如果在请求过程中发生错误
     */
    public Response post(String url, String param) throws FormatException, RequestException{
        URL myurl;
        HttpURLConnection connection;
        Response response;
        url = URLUtil.encodeURL(url);
        try {
            myurl = new URL(url);
            connection = (HttpURLConnection)myurl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            setConnectionHeaders(connection);
            connection.setRequestProperty("Cookie", cookieManager.getString());
            connection.connect();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
            bw.write(param);
            bw.flush();
            bw.close();
            response = new Response(connection);
            setCookies(connection);
        }catch(Exception e) {
            throw new RequestException(e.toString(),e.getCause());
        }
        return response;
    }
    /**
     * 发送post请求。
     * <p> 该方法能够自动将url字符串和url参数进行urlencode编码，请勿将urlencode编码后的参数传入。
     * 
     * @param url 目标url地址
     * @param param 封装了url参数的URLParm类
     * @return Response 响应url请求的类
     * @throws FormatException 如果对url字符串和url参数进行urlencode编码过程中发生错误
     * @throws RequestException 如果在请求过程中发生错误
     */
    public Response post(String url, URLParam param) throws FormatException, RequestException{
        return post(url, param.getEncodeString());
    }
    /**
     * 发送post请求。
     * <p> 该方法能够自动将url字符串进行urlencode编码，请勿将urlencode编码后的字符串传入。
     * 
     * @param url 目标url地址
     * @return Response 响应url请求的类
     * @throws FormatException 如果对url字符串进行urlencode编码过程中发生错误
     * @throws RequestException 如果在请求过程中发生错误
     */
    public Response post(String url) throws FormatException, RequestException {
        return post(url, "");
    }
}
