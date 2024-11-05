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
import fish.payara.poc.adminrest.model.restapi.RestResponse;
import fish.payara.poc.adminrest.service.PayaraServer;
import fish.payara.poc.adminrest.service.commands.DeployCommand;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

/**
 * Back-end Faces bean for application deployment.
 *
 * Starting points: DeployCommand
 *
 * @author aubi
 */
@Named(value = "deploymentBean")
@RequestScoped
public class DeploymentBean {

    @Inject
    private SetupBean setupBean;

    @Inject
    private PayaraServer payaraServer;

    private String path;
    private Boolean isForce = Boolean.TRUE;

//    private String result = null;

    /**
     * Creates a new instance of ApplicationsBean
     */
    public DeploymentBean() {
    }

    public void deploy() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, JAXBException {
        /*
        curl -i -X POST -u "admin:" -H "Content-Type: multipart/form-data" -H "Accept: application/json" -F "keepState=false" -F "name=clusterjsp" -F "id=/home/aubi/work/payara/server/defects/clusterjsp.war" -H "X-Requested-By: cli" -F "properties=implicitCdiEnabled=true:preserveAppScopedResources=false" -F "force=true" "http://localhost:4848/management/domain/applications/application"
         */
        DeployCommand deployCommand = new DeployCommand()
                .id(path)
                //.name("simpleName") // not necessary, done by id()
                .keepState(false)
                .properties("implicitCdiEnabled=true:preserveAppScopedResources=false")
                .force(isForce);

        try {
            RestResponse response = payaraServer.executeRestCall(deployCommand);
            String result = response.getMessage();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", result));
            // TODO: return more!!! Eventually all fields. Create custom facelet component?
        } catch (ServerException ex) {
            Logger.getLogger(DeploymentBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Server Error", ex.getServerMessage()));
//            result = ex.getMessage();
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getIsForce() {
        return isForce;
    }

    public void setIsForce(Boolean isForce) {
        this.isForce = isForce;
    }

//    public String getResult() {
//        return result;
//    }
//
//    public void setResult(String result) {
//        this.result = result;
//    }

}
