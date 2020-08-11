package me.charghet.tools.requests;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 解析url请求结果。
 * <p>解析包含的HttpURLConnection类其中的信息。
 * 
 * @author charghet
 *
 */
public class Response {
    private HttpURLConnection connection;
    private byte[] bytes = null;
    private JSONObject json = null;
    /**
     * 构造一个包含HttpURLConnection的Response类，并读取内容。
     * 
     * @param connection HttpURLConnection
     * @throws IOException 如果在读取过程中发生错误
     */
    public Response(HttpURLConnection connection) throws IOException {
        this.connection = connection;
        readBytes();
    }
    /**
     * 获得Response中的HttpURLConnection类，以调用HttpURLConnection类中的方法。
     * 
     * @return HttpURLConnection
     */
    public HttpURLConnection getConnection() {
        return connection;
    }
    /**
     * 读取HttpURLConnection中的字节流。
     * 
     * @throws IOException 如果发生I/O错误
     */
    private void readBytes() throws IOException{
        if(bytes != null) {
            return;
        }
        BufferedInputStream reader;
        try {
            reader = new BufferedInputStream(connection.getInputStream());
        }catch(IOException e) {
            reader = new BufferedInputStream(connection.getErrorStream());
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while((len = reader.read(b)) != -1) {
            bo.write(b,0,len);
        }
        bytes = bo.toByteArray();
    }
    /**
     * 以指定编码格式获得url请求后的文本信息。
     * 
     * @param encoding 指定的编码格式
     * @return 文本信息
     * @throws UnsupportedEncodingException 如果不支持指定的编码
     */
    public String getText(String encoding) throws UnsupportedEncodingException{
        return new String(bytes, encoding);
    }
    /**
     * 以UTF-8编码格式获得url请求后的文本信息。
     *
     * @return 文本信息
     */
    public String getText() {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 获得url请求结果的字节数组。
     *
     * @return 请求结果的字节数组
     */
    public byte[] getBytes(){
        return bytes;
    }
    /**
     * 获得url请求结果的JSONObject。
     * 
     * @return JSONObject
     */
    public JSONObject getJSON() {
        if(json == null) {
            String text = getText();
            text = text.substring(text.indexOf("{"),text.lastIndexOf("}")+1);
            json = JSON.parseObject(text);
        }
        return json;
    }
    /**
     * 获得url请求结果的JSONObject的一个属性值。
     * 
     * @param key 属性名
     * @return 属性值
     */
    public String getJSONValue(String key) {
        return getJSON().getString(key);
    }
    /**
     * 获得响应头信息的二维字符串数组。
     * <p> 该二维字符串数组大小为String[响应头中的字段数][2]
     * 
     * @return 响应头信息的二维字符串数组
     */
    public String[][] getHeaders(){
        Map<String, List<String>> map = connection.getHeaderFields();
        String[] keySet = map.keySet().toArray(new String[0]);
        List<String> key = new Vector<>();
        List<String> value = new Vector<>();
        for(String name : keySet) {
            List<String> valueList = map.get(name);
            for(String v : valueList) {
                key.add(name);
                value.add(v);
            }
        }
        String[][] headers = new String[key.size()][2];
        for(int i = 0;i < headers.length;i++) {
            headers[i][0] = key.get(i);
            headers[i][1] = value.get(i);
        }
        return headers;
    }
    /**
     * 获得响应头信息的一个属性值。
     * 
     * @param name 属性名
     * @return 属性值
     */
    public String getHeader(String name) {
        return connection.getHeaderField(name);
    }
    /**
     * 将响应url请求后的字节流写入目标文件中。
     * 
     * @param file 目标File对象
     * @throws IOException 如果在写入文件过程中发生I/O错误
     */
    public void writeFile(File file) throws IOException {
        FileOutputStream writer = new FileOutputStream(file);
        writer.write(bytes);
        writer.flush();
        writer.close();
    }
    /**
     * 将响应url请求后的字节流写入目标文件中。
     * 
     * @param file 文件路径字符串
     * @throws IOException 如果在写入文件过程中发生I/O错误
     */
    public void writeFile(String file) throws IOException {
        writeFile(new File(file));
    }
    /**
     * 获得响应状态码。
     * 
     * @return 响应状态码
     */
    public int getStatusCode() {
        try {
            return connection.getResponseCode();
        } catch (IOException e) {
            return -1;
        }
    }
    /**
     * 向控制台输出响应头信息。
     * <p> 此方法用于调试。
     */
    public void printHeaders() {
        String[][] headers = getHeaders();
        System.out.println("Response Headers:");
        for(String[] header:headers) {
            System.out.println(header[0] + ": " + header[1]);
        }
    }
    /**
     * 向控制台输出请求后的文本信息。
     * <p> 此方法用于调试。
     * <p> 实现： System.out.println(getText());
     * 
     */
    public void printText() {
        System.out.println(getText());
    }
}
