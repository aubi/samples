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

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 *
 * @author aubi
 */
@XmlRootElement(name = "entry")
public class Entry {
    private String key;
    private String value;

    private List<ListElement> lists;
    private List<MapElement> maps;

    public Entry() {
    }

    public MapElement getFirstMap() {
        return maps.get(0);
    }

    @XmlAttribute
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @XmlAttribute
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @XmlElement(name = "list")
    public List<ListElement> getLists() {
        return lists;
    }

    public void setLists(List<ListElement> lists) {
        this.lists = lists;
    }

    @XmlElement(name = "map")
    public List<MapElement> getMaps() {
        return maps;
    }

    public void setMaps(List<MapElement> maps) {
        this.maps = maps;
    }

}
