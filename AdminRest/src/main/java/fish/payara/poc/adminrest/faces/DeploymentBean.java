/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package fish.payara.poc.adminrest.faces;

import fish.payara.poc.adminrest.model.ApplicationInfo;
import fish.payara.poc.adminrest.model.restapi.MapElement;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.InputSource;
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

    /**
     * Creates a new instance of ApplicationsBean
     */
    public DeploymentBean() {
    }

    public List<ApplicationInfo> getApplications() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, JAXBException {
        /*
        curl -H "Accept: application/xml" http://localhost:4848/management/domain/applications/list-applications | xmllint --format -
        curl -H "Accept: application/json" http://localhost:4848/management/domain/applications/list-applications | jq
        * this one contains textual data, bad for parsing


    public static final String REST_TOKEN_COOKIE = "gfresttoken";
        WebTarget target = targetWithQueryParams(getJerseyClient().target(address), payload);
        Response resp = target
                .request(RESPONSE_TYPE)
                .cookie(new Cookie(REST_TOKEN_COOKIE, getRestToken()))
                .get(Response.class);
        return RestResponse.getRestResponse(resp);

         */
        HttpURLConnection connection = getConnection("http://localhost:4848/management/domain/applications/list-applications");
        connection.addRequestProperty("Accept", "application/xml");
        Object content = connection.getContent(new Class[]{String.class});
        String type = connection.getContentType();
        String msg = connection.getResponseMessage();
        int ret = connection.getResponseCode();
        StringWriter sw = new StringWriter();

        // Read the input stream from the connection
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder sb = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        // Close the connections
        in.close();
        connection.disconnect();

        // JAXB
        JAXBContext context = JAXBContext.newInstance(MapElement.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        MapElement map = (MapElement) unmarshaller.unmarshal(new InputSource(new StringReader(sb.toString())));
        List<ApplicationInfo> apps = map.findEntry("properties")
                .getFirstMap()
                .getEntries()
                .stream()
                .map(e -> new ApplicationInfo(e.getKey(), e.getValue()))
                .toList();

        // XPath
        /*
        Document doc = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(sb.toString())));
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        XPathExpression xPathExpression = xpath.compile("//map/entry[@key='properties']/map/entry");
        NodeList appNodesList = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
        List<String> apps = new ArrayList<>();
        for (int i = 0; i < appNodesList.getLength(); i++) {
            Element entry = (Element) appNodesList.item(i);
            apps.add(entry.getAttribute("key"));
        }
         */

        return apps;
    }

    private HttpURLConnection getConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setRequestProperty("X-GlassFish-3", "true");
//        connection.setRequestProperty("X-Requested-By", "dummy");
        return connection;
    }

    private int createNode() {
        HttpURLConnection connection = null;
        try {
            String parameters = "name=myConfigNode";
            connection = getConnection("http://localhost:4848/management/domain/nodes/create-node-config");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Language", "en-US");
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();
            return connection.getResponseCode();
        } catch (Exception ex) {
//            fail(ex.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return -1;
    }

}
