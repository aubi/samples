/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
