/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fish.payara.poc.adminrest.model.restapi;

import jakarta.json.JsonObject;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author aubi
 */
@XmlRootElement
public class RestResponse implements Serializable {
    private String message;
    private String command;
    private String exit_code;
//    private String properties;
//                extraProperties
//                subReports

    public RestResponse() {
    }

    public RestResponse(JsonObject restJsonResponse) {
        message = restJsonResponse.getString("message");
        command = restJsonResponse.getString("command");
        exit_code = restJsonResponse.getString("exit_code");
    }

    @Override
    public String toString() {
        return "RestResponse{" + "message='" + message + "', command='" + command + "', exit_code='" + exit_code + "'}";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getExit_code() {
        return exit_code;
    }

    public void setExit_code(String exit_code) {
        this.exit_code = exit_code;
    }

//    public String getProperties() {
//        return properties;
//    }
//
//    public void setProperties(String properties) {
//        this.properties = properties;
//    }

}
