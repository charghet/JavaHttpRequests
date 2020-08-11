package me.charghet.tools.requests;

import java.util.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 该类是通过http请求调用百度文字识别接口对图片进行OCR识别，需要应用的API Key和Secret Key。
 * <p> 关于如何获得应用的API Key和Secret Key
 * <p> 具体参阅：<a href=https://ai.baidu.com/ai-doc/OCR/dk3iqnq51>文字识别</a>
 * <p> 只须参阅第一步中的获取应用的API Key和Secret Key即可。
 * @author charghet
 *
 */
public class OCR {
    private String id;
    private String secret;
    private String token;
    /**
     * 通过应用的API Key和Secret Key构造一个OCR实例。
     * <p> 在构造过程中会通过http请求对应用的API Key和Secret Key进行验证。
     * 
     * @param id 应用的API Key
     * @param secret 应用的Secret Key
     * @throws OCRException 如果在OCR识别过程中发生错误
     */
    public OCR(String id, String secret) throws OCRException {
        this.id = id;
        this.secret = secret;
        token = getToken();
    }
    /**
     * 通过应用的API Key和Secret Key获得access_token。
     * 
     * @return access_token
     * @throws OCRException 如果在OCR识别过程中发生错误
     */
    private String getToken() throws OCRException {
        Session session = new Session();
        String tokenurl = "https://aip.baidubce.com/oauth/2.0/token";
        URLParam data = new URLParam();
        data.add("grant_type", "client_credentials");
        data.add("client_id", id);
        data.add("client_secret", secret);
        try {
            Response rs = session.post(tokenurl, data);
            String result = rs.getText();
            JSONObject json = JSON.parseObject(result);
            String token = json.getString("access_token");
            if(token == null) {
                throw new OCRException(result);
            }
            return token;
        }catch(Exception e) {
            throw new OCRException(e.getMessage(), e.getCause());
        }
    }
    /**
     * 对图片的字节数组进行OCR识别，返回识别后的字符串数组。
     * 
     * @param bytes 图片的字节数组
     * @return  识别后的字符串数组
     * @throws OCRException 如果在OCR识别过程中发生错误
     */
    public String[] discernToArray(byte[] bytes) throws OCRException {
        String image = Base64.getEncoder().encodeToString(bytes);
        String dicernurl = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        URLParam data = new URLParam();
        data.add("access_token", token);
        data.add("image", image);
        Session session = new Session();
        try {
            Response rs = session.post(dicernurl, data);
            String text = rs.getText();
            JSONObject json = JSON.parseObject(text);
            JSONArray array = json.getJSONArray("words_result");
            String[] result = new String[array.size()];
            for(int i = 0;i < array.size();i++) {
                result[i] = array.getJSONObject(i).getString("words");
            }
            return result;
        }catch(Exception e) {
            throw new OCRException(e.toString(),e.getCause());
        }
        
    }
    /**
     * 对图片的字节数组进行OCR识别，返回识别后的字符串。
     * 
     * @param bytes 图片的字节数组
     * @return 识别后的字符串
     * @throws OCRException 如果在OCR识别过程中发生错误
     */
    public String discern(byte[] bytes) throws OCRException {
        String[] array = discernToArray(bytes);
        StringBuilder result = new StringBuilder();
        for(String s : array) {
            s += '\n';
            result.append(s);
        }
        return result.toString();
    }
}
