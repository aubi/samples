/*
 * Copyright (C) 2024 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
