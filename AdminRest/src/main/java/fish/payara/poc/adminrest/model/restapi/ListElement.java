/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fish.payara.poc.adminrest.model.restapi;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 *
 * @author aubi
 */
@XmlRootElement(name = "list")
public class ListElement {
    private List<Entry> maps;
    private List<String> strings;

    public ListElement() {
    }

    @XmlElement(name = "map")
    public List<Entry> getMaps() {
        return maps;
    }

    public void setMaps(List<Entry> maps) {
        this.maps = maps;
    }

    @XmlElement(name = "string")
    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

}
