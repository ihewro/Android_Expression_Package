package com.ihewro.android_expression_package.bean;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EventMessage {

    public static final String DATABASE = "database";
    private String type;
    private String message;


    public EventMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
