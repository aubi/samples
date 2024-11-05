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
package fish.payara.poc.adminrest.service.commands;

import fish.payara.poc.adminrest.model.restapi.RestResponse;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
public interface RemoteCommand {

    public enum Verb {
        GET, POST
    };

    MultivaluedMap<String, String> createPayload();

    String getUrl();

    public Verb getVerb();

    void parseResult(RestResponse response);
}
