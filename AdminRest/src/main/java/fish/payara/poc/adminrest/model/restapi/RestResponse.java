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

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author aubi
 */
@XmlRootElement
public class RestResponse implements Serializable {
    private String message;
    private String command;
    private String exit_code;
    private Map<String, String> properties = new HashMap<>();
    private Map<String, Object> extraProperties = new HashMap<>();
//                subReports

    public RestResponse() {
    }

    public RestResponse(JsonObject restJsonResponse) {
        message = restJsonResponse.getString("message");
        command = restJsonResponse.getString("command");
        exit_code = restJsonResponse.getString("exit_code");
        JsonObject jsonProperties = restJsonResponse.getJsonObject("properties");
        if (jsonProperties != null) {
            properties = jsonProperties
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().toString()));
        }
        JsonObject jsonExtraProperties = restJsonResponse.getJsonObject("extraProperties");
        if (jsonExtraProperties != null) {
            extraProperties = (Map<String, Object>) mapToJava(jsonExtraProperties);
        }
    }

    private Object mapToJava(Object object) {
        if (object instanceof JsonObject) {
            return ((JsonObject) object)
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey(), e -> mapToJava(e.getValue())));
        } else if (object instanceof JsonString) {
            return ((JsonString) object).getString();
        } else if (object instanceof JsonValue) {
            return ((JsonValue) object).toString();
        } else if (object instanceof JsonArray) {
            return ((JsonArray) object)
                    .stream()
                    .map(o -> mapToJava(o))
                    .collect(Collectors.toList());
        } else if (object == null) {
            return null;
        } else {
            throw new IllegalStateException("Unknown type of JSON object: " + object.getClass() + ", '" + object.toString() + "'");
        }
    }

    @Override
    public String toString() {
        return "RestResponse{" + "message='" + message + "', command='" + command + "', exit_code='" + exit_code + "'}";
    }

    public String getMessage() {
        return message;
    }

    public String getCommand() {
        return command;
    }

    public String getExit_code() {
        return exit_code;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Map<String, Object> getExtraProperties() {
        return extraProperties;
    }

}
