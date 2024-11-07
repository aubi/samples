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
package fish.payara.poc.adminrest.service;

import fish.payara.poc.adminrest.model.restapi.RestResponse;
import fish.payara.poc.adminrest.service.commands.AnonymousUserEnabledCommand;
import fish.payara.poc.adminrest.service.commands.ListApplicationsCommand;
import fish.payara.poc.adminrest.service.commands.RemoteCommand;
import fish.payara.poc.adminrest.service.model.ApplicationInfo;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Payara Server API. Provides easy-to-use services, internally calls server
 * REST API.
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
@Named(value = "payaraServer")
@SessionScoped
public class PayaraServer implements Serializable {
    private String url = "http://localhost:4848";
    private String adminUsername = "admin";
    private String adminPassword = "";

    /**
     * Creates a new instance of PayaraServer
     */
    public PayaraServer() {
    }

    public List<ApplicationInfo> listApplications() throws ServerException {
        ListApplicationsCommand listApplicationsCommand = new ListApplicationsCommand();
        RestResponse response = executeRestCall(listApplicationsCommand);
        return listApplicationsCommand.getApplications();
    }

    public boolean checkAnonymousUserEnabled() throws ServerException {
        AnonymousUserEnabledCommand anonymousUserEnabledCommand = new AnonymousUserEnabledCommand();
        try {
            /*RestResponse response =*/ executeRestCall(anonymousUserEnabledCommand);
            return anonymousUserEnabledCommand.isAllowed();
        } catch (ServerException ex) {
            // HTTP code 401 is ok, meaning that secure admin is switched on,
            // e.g. anonymous user is NOT allowed
            if (ex.getStatus() != null && ex.getStatus() == 401) {
                return false;
            } else {
                throw ex;
            }
        }
    }

    public RestResponse executeRestCall(RemoteCommand command) throws ServerException {
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            Logger.getLogger(PayaraServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (Client client = ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build()) {
            String targetUrl = url
                    + (url.endsWith("/") ? "" : "/")
                    + command.getUrl();
            MultivaluedMap<String, String> payload = command.createPayload();
            Invocation.Builder invocationBuilder = client.target(targetUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .header("X-Requested-By", "cli");
            if (command.getUseAuthorization() && !(adminPassword == null || adminPassword.isEmpty())) {
                invocationBuilder.header(HttpHeaders.AUTHORIZATION, constructBasicAuthentication(
                        adminUsername, // "admin"
                        adminPassword)); // "";
            }

            Response response;
            switch (command.getVerb()) {
                case GET:
                    response = invocationBuilder.get();
                    break;
                case POST:
                    response = invocationBuilder.post(Entity.form(payload));
                    break;
                default:
                    throw new IllegalStateException("command.getVerb() must return either GET or POST");
            }

            if (response.getStatus() == 200) {
                JsonObject restJsonResponse = response.readEntity(JsonObject.class);
                RestResponse restResponse = new RestResponse(restJsonResponse);
                command.parseResult(restResponse);
                return restResponse;
            } else {
                JsonObject restJsonResponse = response.readEntity(JsonObject.class);
                String msg;
                if (restJsonResponse == null) {
                    msg = response.getStatus() + ", " + response.getStatusInfo();
                } else {
                    msg = restJsonResponse.getString("message");
                }
                throw new ServerException("Server call failing with a message '" + msg + "'", msg, response.getStatus());
            }
        } catch (Exception ex) {
            if (ex instanceof ServerException) {
                throw ex; // keep existing ServerException
            } else {
                throw new ServerException("Server call failing with a message '" + ex.getMessage() + "'", ex);
            }
        }

    }
    public static String constructBasicAuthentication(String username, String password) {
        String userAndPassword = username + ":" + password;
        byte[] userAndPasswordBytes = userAndPassword.getBytes(StandardCharsets.UTF_8);
        return "Basic " + Base64.getEncoder().encodeToString(userAndPasswordBytes);
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
}

// Additional commands:
//    private int createNode() {
//        HttpURLConnection connection = null;
//        try {
//            String parameters = "name=myConfigNode";
//            connection = getConnection("http://localhost:4848/management/domain/nodes/create-node-config");
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
//            connection.setUseCaches(false);
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setRequestProperty("Content-Language", "en-US");
//            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
//            wr.writeBytes(parameters);
//            wr.flush();
//            wr.close();
//            return connection.getResponseCode();
//        } catch (Exception ex) {
////            fail(ex.getMessage());
//        } finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
//        }
//
//        return -1;
//    }
