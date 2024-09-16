/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fish.payara.poc.adminrest.model;

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
