package me.charghet.tools.requests;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 包含url请求常用方法的工具类.
 * 
 * @author charghet
 *
 */
public class URLUtil {
    /**
     * 将url地址进行urlencode编码，请勿在url地址中附加参数。
     * 
     * @param url 目标url地址
     * @return 编码后的url地址
     * @throws FormatException 如果在编码过程中发生错误
     */
    public static String encodeURL(String url) throws FormatException {
        try {
            String end = url.substring(url.indexOf(":")+3);
            String[] words = end.split("/");
            for(String word : words) {
                url = url.replaceFirst(word,URLEncoder.encode(word,"utf-8"));
            }
            return url;
        }catch(Exception e) {
            throw new FormatException("url格式错误！");
        }
    }
    /**
     * 将url转换为 application/x-www-form-urlencoded 格式。
     * 
     * @param url url字符串，可以附带参数 如："https://www.charghet.com?name=vale"
     * @return 转换后的字符串
     * @throws FormatException 如果字符串格式错误
     */
    public static String encodeGetURL(String url) throws FormatException {
        int index = url.indexOf("?");
        if(index != -1) {
            StringBuilder sb = new StringBuilder();
            sb.append(encodeURL(url.substring(0, index))).append("?");
            String param = url.substring(index+1);
            String[] properties = param.split("&");
            for(String s : properties) {
                String[] ss = s.split("=");
                try {
                    if(ss.length == 2) {
                        sb.append(URLEncoder.encode(ss[0], "utf-8")).append("=").append(URLEncoder.encode(ss[1], "utf-8")).append("&");
                    }else {
                        sb.append(URLEncoder.encode(ss[0], "utf-8")).append("=&");
                    }
                }catch(Exception e) {
                    throw new FormatException(e.toString(), e.getCause());
                }
            }
            return sb.substring(0, sb.length()-1);
        }else {
            return encodeURL(url);
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
    public static Response get(String url) throws RequestException{
        URL myurl;
        HttpURLConnection connection;
        Response response;
            try {
                myurl = new URL(url);
                connection = (HttpURLConnection)myurl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                response = new Response(connection);
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
    public static Response get(String url, URLParam param) throws RequestException, FormatException{
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
     * @throws FormatException 如果将url字符串进行urlencode编码过程中发生错误
     * @throws RequestException 如果在请求过程中发生错误
     */
    public static Response post(String url, String param) throws FormatException, RequestException{
        URL myurl;
        HttpURLConnection connection;
        Response response;
        url = URLUtil.encodeURL(url);
        try {
            myurl = new URL(url);
            connection = (HttpURLConnection)myurl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.connect();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),StandardCharsets.UTF_8));
            bw.write(param);
            bw.flush();
            bw.close();
            response = new Response(connection);
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
     * @throws FormatException 如果将url字符串和url参数进行urlencode编码过程中发生错误
     * @throws RequestException 如果在请求过程中发生错误
     */
    public static Response post(String url, URLParam param) throws FormatException, RequestException{
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
