package me.charghet.tools.requests;

import java.net.HttpCookie;
import java.util.*;

/**
 * 用于保存cookie信息的类。
 * @author charghet
 *
 */
public class CookieManager {
    private List<HttpCookie> list = new Vector<>();
    /**
     * 添加一条cookie信息。
     * 
     * @param cookie HttpCookie
     */
    public void add(HttpCookie cookie) {
        int i;
        for(i = 0;i < list.size();i++) {
            if(list.get(i).getName().equals(cookie.getName())) {
                list.set(i, cookie);
                break;
            }
        }
        if(i == list.size()) {
            list.add(cookie);
        }
    }
    /**
     * 添加一条cookie信息。
     * 
     * @param name cookie的名称
     * @param value cookie的值
     */
    public void add(String name, String value) {
        add(new HttpCookie(name,value));
    }
    /**
     * 添加多条cookie信息。
     * 
     * @param list 包含多条cookie信息的列表
     */
    public void add(List<HttpCookie> list) {
        for(HttpCookie cookie: list) {
            add(cookie);
        }
    }
    /**
     * 解析"Cookie"字段并添加解析后的多条cookie信息。
     * <p> 能够将"Cookie"字段解析成多个Cookie信息。
     * 
     * @param cookies Cookie 字段
     */
    public void adds(String cookies) {
        cookies = cookies.replaceAll(" ","");
        String[] cookie = cookies.split(";");
        for(String c : cookie) {
            add(HttpCookie.parse(c));
        }
    }
    /**
     * 解析"Cookie"字段并添加解析后的一条cookie信息。
     * <p> 只解析成一个Cookie信息。
     * 
     * @param cookie Cookie 字段
     */
    public void add(String cookie) {
        add(HttpCookie.parse(cookie));
    }
    /**
     * 获得包含所有cookie信息的列表。
     * 
     * @return 包含所有cookie信息的列表
     */
    public List<HttpCookie> getList(){
        return list;
    }
    /**
     * 获得包含所有cookie信息的字符串，此字符串格式为请求头中"Cookie"字段的格式。
     * <p> 格式如下：
     * <p> "name1=value1; name2=value2"
     * 
     * @return 包含所有cookie信息的字符串
     */
    public String getString() {
        if(list.size() == 0) {
            return "";
        }
        StringBuilder cookies = new StringBuilder();
        for(HttpCookie cookie: list) {
            String s = cookie.getName() + "=" + cookie.getValue() + "; ";
            cookies.append(s);
        }
        return cookies.substring(0, cookies.length()-2); 
    }
    /**
     * 获得Cookie的值。如果有多个同名Cookie，只返回第一个匹配的值。
     * 
     * @param name Cookie的名称
     * @return Cookie的值
     */
    public String getValue(String name) {
        for(HttpCookie cookie : list) {
            if(cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }
    /**
     * 获得一个cookie信息的字符串。
     * <p> 格式如下：
     * <p> "name=value"
     *
     * @param name cookie名
     * @return cookie信息的字符串
     */
    public String getString(String name) {
        for(HttpCookie cookie : list) {
            if(cookie.getName().equals(name)) {
                return cookie.getName() + "=" + cookie.getValue();
            }
        }
        return null;
    }
    /**
     * 向控制台输出Cookie信息。
     * <p> 此方法用于调试。
     */
    public void printCookies() {
        System.out.println("Cookies:");
        if(list.size() > 0) {
            for(HttpCookie cookie:list) {
                System.out.println(cookie.getName() + ": " + cookie.getValue());
            }
        }
    }
}
