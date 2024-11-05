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
package fish.payara.poc.adminrest.service.model;

import java.util.List;

/**
 *
 * @author aubi
 */
public class ApplicationInfo {
    private String name;
    private List<String> services;

    public ApplicationInfo() {
    }

    public ApplicationInfo(String name, String services) {
        this.name = name;
        if (services.startsWith("\"") && services.endsWith("\"")) {
            services = services.substring(1, services.length() - 1);
        }
        this.services = List.of(services.split(", "));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

}
