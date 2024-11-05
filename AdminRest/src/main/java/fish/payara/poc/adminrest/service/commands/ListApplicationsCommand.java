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
import fish.payara.poc.adminrest.service.model.ApplicationInfo;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
public class ListApplicationsCommand implements RemoteCommand {

    private List<ApplicationInfo> applications = new ArrayList<>();

    public ListApplicationsCommand() {
    }

    @Override
    public String getUrl() {
        return "management/domain/applications/list-applications";
    }

    @Override
    public Verb getVerb() {
        return Verb.GET;
    }

    @Override
    public MultivaluedMap<String, String> createPayload() {
        MultivaluedMap<String, String> payload = new MultivaluedHashMap<>();
        return payload;
    }

    @Override
    public void parseResult(RestResponse response) {
        applications = response.getProperties()
                .entrySet()
                .stream()
                .map(e -> new ApplicationInfo(e.getKey(), e.getValue()))
                .toList();
    }

    public List<ApplicationInfo> getApplications() {
        return applications;
    }
}
