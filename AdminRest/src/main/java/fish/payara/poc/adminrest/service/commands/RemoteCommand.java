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
public abstract class RemoteCommand {

    // TODO: further commands:
    // curl -i -u "admin:admin" -H "Accept: application/json"  http://localhost:4848/management/domain/servers/server/server/generate-jvm-report?target=server&type=summary
    public enum Verb {
        GET, POST
    };

    public abstract MultivaluedMap<String, String> createPayload();

    public abstract String getUrl();

    public abstract Verb getVerb();

    public abstract void parseResult(RestResponse response);

    public boolean getUseAuthorization() {
        return true;
    }
}
