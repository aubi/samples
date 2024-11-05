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

import fish.payara.poc.adminrest.service.ServerException;
import fish.payara.poc.adminrest.service.PayaraServer;
import fish.payara.poc.adminrest.service.model.ApplicationInfo;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

/**
 * TODO: Try secure admin
 *
 * @author aubi
 */
@Named(value = "applicationsBean")
@SessionScoped
public class ApplicationsBean implements Serializable {

    @Inject
    private SetupBean setupBean;

    @Inject
    private PayaraServer payaraServer;

    private List<ApplicationInfo> apps;

    /**
     * Creates a new instance of ApplicationsBean
     */
    public ApplicationsBean() {
    }

    public List<ApplicationInfo> getApplications() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, JAXBException {
        return apps;
    }

    @PostConstruct
    public void loadApplications() {
        // curl -H "Accept: application/xml" http://localhost:4848/management/domain/applications/list-applications | xmllint --format -
        // curl -H "Accept: application/json" http://localhost:4848/management/domain/applications/list-applications | jq
        // this one contains textual data, bad for parsing

        try {
            apps = payaraServer.listApplications();
        } catch (ServerException ex) {
            Logger.getLogger(DeploymentBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Server Error", ex.getServerMessage()));
        }
    }

    private HttpURLConnection getConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        return connection;
    }
}
