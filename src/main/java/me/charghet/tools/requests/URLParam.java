package me.charghet.tools.requests;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用于保存url参数的类。
 * <p> 此类通过成员变量 LinkedHashMap&lt;String, LinkedHashSet&lt;String&gt;&gt; param 来保存url参数的键值对。
 * <p> 其中，String用于保存参数名，LinkedHashSet&lt;String&gt;用于保存同一个参数名的所有参数值。
 * <p> 对于url参数的所有操作都是同步的。
 * @author charghet
 */
public class URLParam {
    private Map<String, Set<String>> param;
    /**
     * 构造一个空的URLParam实例。
     */
    public URLParam() {
        param = new LinkedHashMap<>();
    }
    /**
     * 获得保存url参数的Map集合。
     * 
     * @return 保存url参数的Map集合
     */
    public Map<String, Set<String>> getMap(){
        return param;
    }
    /**
     * 添加一条url参数。
     * 
     * @param key 参数名
     * @param value 参数值
     */
    public synchronized void add(String key, String value) {
        Set<String> set = param.computeIfAbsent(key, k -> new LinkedHashSet<>());
        set.add(value);
    }
    /**
     * 根据参数名删除一条url参数。
     * <p> 此操作将删除与参数名相同的所有url参数。
     * 
     * @param key 参数名
     * @return 如果不存在与参数名相同的url参数，返回false，否则返回true
     */
    public synchronized boolean remove(String key) {
        Set<String> set = param.remove(key);
        return set != null;
    }
    /**
     * 根据参数名和参数值删除一条url参数。
     * <p> 如果含有多条同名url参数，仅删除与参数值相同的url参数。
     * 
     * @param key 参数名
     * @param value 参数值
     * @return 如果不存在与参数名和参数值相同的url参数，返回false，否则返回true
     */
    public synchronized boolean remove(String key, String value) {
        Set<String> set = param.get(key);
        if(set != null) {
            boolean result = set.remove(value);
            if(!result) {
                return false;
            }
            if(set.size() == 0) {
                param.remove(key);
            }
            return true;
        }
        return false;
    }
    /**
     * 获得指定参数名的Set视图。
     * 
     * @param key 参数名
     * @return 指定参数名的Set视图
     */
    public synchronized Set<String> getSet(String key) {
        return param.get(key);
    }
    /**
     * 获得指定参数名的参数值，如果存在多条同名url参数，则只返回匹配的第一条url参数的值。
     * 
     * @param key 参数名
     * @return 匹配的第一条url参数的值，如果未包含与参数名同名的url参数，则返回null
     */
    public synchronized String get(String key) {
        Set<String> set = param.get(key);
        if(set != null && set.size() != 0) {
            return (String)set.toArray()[0];
        }
        return null;
    }
    /**
     * 获得经过urlencode编码后的url参数的字符串。
     * 
     * @return url参数字符串 如果并未包含任何url参数，则返回空字符串
     */
    public synchronized String getEncodeString() {
        StringBuilder result = new StringBuilder();
        Set<String> keySet = param.keySet();
        for(String key : keySet) {
            Set<String> valueSet = param.get(key);
            for(String value : valueSet) {
                try {
                    String s = URLEncoder.encode(key,"utf-8")+"="+URLEncoder.encode(value,"utf-8")+"&";
                    result.append(s);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(result.length() > 0) {
            return result.substring(0,result.length()-1);
        }
        return "";
    }
    /**
     * 获得url参数的字符串。
     * 
     * @return url参数字符串 如果并未包含任何url参数，则返回空字符串
     */
    public synchronized String getString() {
        StringBuilder result = new StringBuilder();
        Set<String> keySet = param.keySet();
        for(String key : keySet) {
            Set<String> valueSet = param.get(key);
            for(String value : valueSet) {
                String s = key+"="+value+"&";
                result.append(s);
            }
        }
        if(result.length() > 0) {
            return result.substring(0,result.length()-1);
        }
        return "";
    }
    /**
     * 向控制台输出url参数。
     * <p> 此方法用于调试。
     * 
     */
    public synchronized void print() {
        System.out.println("Param:");
        Set<String> keySet = param.keySet();
        for(String key : keySet) {
            Set<String> valueSet = param.get(key);
            for(String value : valueSet) {
                System.out.println(key+": "+value);
            }
        }
    }
}
