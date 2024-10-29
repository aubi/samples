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

import fish.payara.poc.adminrest.model.restapi.RestResponse;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
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

    private static final String WAR_SUFFIX = ".war";

    @Inject
    private SetupBean setupBean;

    private String path;
    private Boolean isForce = Boolean.TRUE;

    private String result = null;

    /**
     * Creates a new instance of ApplicationsBean
     */
    public DeploymentBean() {
    }

    public void deploy() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, JAXBException {
        /*
        curl -i -X POST -u "admin:" -H "Content-Type: multipart/form-data" -H "Accept: application/json" -F "keepState=false" -F "name=clusterjsp" -F "id=/home/aubi/work/payara/server/defects/clusterjsp.war" -H "X-Requested-By: cli" -F "properties=implicitCdiEnabled=true:preserveAppScopedResources=false" -F "force=true" "http://localhost:4848/management/domain/applications/application"
         */
        Path pathPath = Path.of(path);
        String name = pathPath.getFileName().toString();
        if (name.endsWith(WAR_SUFFIX)) {
            name = name.substring(0, name.length() - WAR_SUFFIX.length());
        }

        MultivaluedMap<String, String> payload = new MultivaluedHashMap<>();
        payload.add("keepState", "false");
        payload.add("name", name /*"clusterjsp"*/);
        payload.add("id", pathPath.toString());
        payload.add("properties", "implicitCdiEnabled=true:preserveAppScopedResources=false");
        payload.add("force", isForce.toString().toLowerCase());

        try (Client client = ClientBuilder.newClient()) {
            String targetUrl = "http://localhost:4848/management/domain/applications/application";
            Response response = client.target(targetUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .header("X-Requested-By", "cli")
                    .header(HttpHeaders.AUTHORIZATION, constructBasicAuthentication(
                            setupBean.getAdminUsername(), // "admin"
                            setupBean.getAdminPassword())) // ""
                    .post(Entity.form(payload));
            if (response.getStatus() == 200) {
//                result = "Success: " + response.readEntity(String.class);
//                RestResponse restResponse = response.readEntity(RestResponse.class);
                JsonObject restJsonResponse = response.readEntity(JsonObject.class);
                RestResponse restResponse = new RestResponse(restJsonResponse);
                result = "Success: " + restResponse;
                System.out.println(result);
                // how to get results, JSON?
            } else {
                JsonObject restJsonResponse = response.readEntity(JsonObject.class);
                String msg = restJsonResponse.getString("message");
                result = "Failed with HTTP code: " + response.getStatus() + ", message: " + msg;
                System.out.println(result);
            }
        }
    }

    public static String constructBasicAuthentication(String username, String password) {
        String userAndPassword = username + ":" + password;
        byte[] userAndPasswordBytes = userAndPassword.getBytes(StandardCharsets.UTF_8);
        return "Basic " + Base64.getEncoder().encodeToString(userAndPasswordBytes);
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
