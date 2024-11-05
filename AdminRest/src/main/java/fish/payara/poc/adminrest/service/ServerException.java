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
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
public class ServerException extends Exception {
    private String serverMessage;
    private Integer status = null;

    public ServerException(String message, String serverMessage) {
        super(message);
        this.serverMessage = serverMessage;
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
        this.serverMessage = cause.getMessage();
    }

    ServerException(String message, String serverMessage, int status) {
        this(message, serverMessage);
        this.status = status;
    }

    public String getServerMessage() {
        return serverMessage;
    }

    public Integer getStatus() {
        return status;
    }
}
