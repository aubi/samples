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
package fish.payara.poc.adminrest.faces;

import fish.payara.poc.adminrest.service.PayaraServer;
import fish.payara.poc.adminrest.service.ServerException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aubi
 */
@Named("setupBean")
@SessionScoped
public class SetupBean implements Serializable {

    @Inject
    private PayaraServer payaraServer;

    private String url = "http://localhost:4848";
    private String adminUsername = "admin";
    private String adminPassword = "";
    private Boolean anonymousUserEnabled = null;

    public SetupBean() {
    }

    @PostConstruct
    public void init() {
        url = payaraServer.getUrl();
        adminUsername = payaraServer.getAdminUsername();
        adminPassword = payaraServer.getAdminPassword();
    }

    public void checkSetup() {
        String urlBackup = payaraServer.getUrl();
        String adminUsernameBackup = payaraServer.getAdminUsername();
        String adminPasswordBackup = payaraServer.getAdminPassword();
        try {
            payaraServer.setUrl(url);
            payaraServer.setAdminUsername(adminUsername);
            payaraServer.setAdminPassword(adminPassword);

            anonymousUserEnabled = payaraServer.checkAnonymousUserEnabled();
        } catch (ServerException ex) {
            Logger.getLogger(DeploymentBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Server Error", ex.getServerMessage()));
        } finally {
            payaraServer.setUrl(urlBackup);
            payaraServer.setAdminUsername(adminUsernameBackup);
            payaraServer.setAdminPassword(adminPasswordBackup);
        }
    }

    public String save() {
        payaraServer.setUrl(url);
        payaraServer.setAdminUsername(adminUsername);
        payaraServer.setAdminPassword(adminPassword);
        return "index?faces-redirect=true";
    }

    public void setAdminAdmin() {
        adminUsername = "admin";
        adminPassword = "admin";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public Boolean getAnonymousUserEnabled() {
        return anonymousUserEnabled;
    }

    public void setAnonymousUserEnabled(Boolean anonymousUserEnabled) {
        this.anonymousUserEnabled = anonymousUserEnabled;
    }

}
