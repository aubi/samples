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
package fish.payara.poc.adminrest.service;

/**
 * ServerResponse maps Payara Server REST API response fields.
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
public class ServerResponse {
    private String message;
    private String command;
    private String exitCode;
    private String properties;
    private String extraProperties;
    private String subReports;

    public ServerResponse() {
    }

    public ServerResponse(String message, String command, String exitCode, String properties, String extraProperties, String subReports) {
        this.message = message;
        this.command = command;
        this.exitCode = exitCode;
        this.properties = properties;
        this.extraProperties = extraProperties;
        this.subReports = subReports;
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

    public String getExitCode() {
        return exitCode;
    }

    public void setExitCode(String exitCode) {
        this.exitCode = exitCode;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getExtraProperties() {
        return extraProperties;
    }

    public void setExtraProperties(String extraProperties) {
        this.extraProperties = extraProperties;
    }

    public String getSubReports() {
        return subReports;
    }

    public void setSubReports(String subReports) {
        this.subReports = subReports;
    }


}
