package me.charghet.tools.requests;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.script.Invocable;

/**
 * 执行js脚本的工具类，可通过url地址直接执行js文件。
 * 
 * @author charghet
 *
 */
public class JSEngine {
    private ScriptEngine engine;
    /**
     * 构造一个JS引擎。
     */
    public JSEngine() {
        engine = new ScriptEngineManager().getEngineByName("javascript");
    }
    /**
     *设置要解析的js代码。
     * 
     * @param script js代码
     * @throws JSException 如果解析js代码发生错误。
     */
    public void setJS(String script) throws JSException {
        try {
            engine.eval(script);
        }catch(ScriptException e) {
            throw new JSException(e.getMessage(), e.getCause());
        }
    }
    /**
     * 下载js文件并解析。
     * 
     * @param url js文件地址
     * @throws JSException 如果下载或解析过程中发生错误。
     */
    public void setJSURL(String url) throws JSException {
        try {
            String script = URLUtil.get(url).getText();
            engine.eval(script);
        }catch(Exception e) {
            throw new JSException(e.getMessage(), e.getCause());
        }
    }
    /**
     * 读取js文件并解析
     * 
     * @param file 文件名
     * @throws JSException 如果读取或解析过程中发生错误。
     */
    public void setJSFile(String file) throws JSException {
        try {
            String ss;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8));
            StringBuilder s = new StringBuilder();
            String temp;
            while((temp = br.readLine()) != null) {
                temp += "\n";
                s.append(temp);
            }
            br.close();
            if(s.length() > 0) {
                ss = s.substring(0,s.length()-1);
            }else {
                ss = s.toString();
            }
            setJS(ss);
        } catch (IOException e) {
            throw new JSException(e.getMessage(), e.getCause());
        }
    }
    /**
     * 执行js脚本中的函数。
     * 
     * @param name 函数名
     * @param args 函数的参数
     * @return 函数返回值
     * @throws JSException 如果在执行js脚本过程中发生错误。
     */
    public Object runFunction(String name, Object... args) throws JSException {
        try {
            Invocable invokable = (Invocable)engine;
            return invokable.invokeFunction(name, args);
        }catch(Exception e) {
            throw new JSException(e.getMessage(), e.getCause());
        }
    }
}
