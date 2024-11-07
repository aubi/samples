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
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Deploy command.
 *
 * <pre>
 * curl -i -X POST -u "admin:" -H "Content-Type: multipart/form-data" -H "Accept: application/json" -F "keepState=false" -F "name=clusterjsp" -F "id=/home/aubi/work/payara/server/defects/clusterjsp.war" -H "X-Requested-By: cli" -F "properties=implicitCdiEnabled=true:preserveAppScopedResources=false" -F "force=true" "http://localhost:4848/management/domain/applications/application"
 * </pre>
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
public class DeployCommand extends RemoteCommand {
    private static final String WAR_SUFFIX = ".war";

    private Optional<Boolean> keepState = Optional.of(Boolean.FALSE);
    private String name = null;
    private String id = null;
    private String properties = "implicitCdiEnabled=true:preserveAppScopedResources=false";
    private Optional<Boolean> force = Optional.of(Boolean.FALSE);

    public DeployCommand() {
    }

    @Override
    public String getUrl() {
        return "management/domain/applications/application";
    }

    @Override
    public Verb getVerb() {
        return Verb.POST;
    }

    public DeployCommand id(String id) {
        this.id = id;
        if (name == null) {
            Path pathPath = Path.of(id);
            String fileName = pathPath.getFileName().toString();
            if (fileName.endsWith(WAR_SUFFIX)) {
                name = fileName.substring(0, fileName.length() - WAR_SUFFIX.length());
            }
        }
        return this;
    }

    public DeployCommand keepState(Boolean keepState) {
        this.keepState = Optional.ofNullable(keepState);
        return this;
    }

    public DeployCommand name(String name) {
        this.name = name;
        return this;
    }

    public DeployCommand properties(String properties) {
        this.properties = properties;
        return this;
    }

    public DeployCommand force(Boolean force) {
        this.force = Optional.ofNullable(force);
        return this;
    }

    @Override
    public MultivaluedMap<String, String> createPayload() {
        MultivaluedMap<String, String> payload = new MultivaluedHashMap<>();
        keepState.ifPresent(ks -> payload.add("keepState", ks.toString().toLowerCase()));
        payload.add("name", name /*"clusterjsp"*/);
        payload.add("id", id);
        payload.add("properties", properties);
        force.ifPresent(f -> payload.add("force", f.toString().toLowerCase()));
        return payload;
    }

    @Override
    public void parseResult(RestResponse response) {
    }
}
