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
    public static final String DESCRIPTION_SAVE = "descriptionSave";
    private String type;
    private String message;
    private String message2;


    public EventMessage(String type) {
        this.type = type;
    }

    public EventMessage(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public EventMessage(String type, String message, String message2) {
        this.type = type;
        this.message = message;
        this.message2 = message2;
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

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    @Override
    public String toString() {
        return "type" + type + "\n" +
                "message" + message + "\n" +
                "message2" + message2;



    }
}
