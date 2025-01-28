package fish.payara.poc.adminrest.service;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/monitoring-data")
public class MonitoringService {

    private static final String LOGIN_URL = "http://localhost:4848/j_security_check";
    private static final String METRICS_URL = "http://localhost:4848/metrics";

    private final CookieManager cookieManager = new CookieManager();

    public MonitoringService() {
        // Set up the CookieManager to handle cookies automatically
        java.net.CookieHandler.setDefault(cookieManager);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonitoringData() {
        try {
            performLogin();

            String metricsData = fetchMetrics();

            double cpuUsage = parseMetric(metricsData, "system_cpu_load");
            double memoryUsage = parseMetric(metricsData, "memory_usedHeap_bytes");


            Map<String, Object> responseData = new HashMap<>();
            responseData.put("cpuUsage", cpuUsage);
            responseData.put("memoryUsage", memoryUsage);

            return Response.ok(responseData).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving monitoring data: " + e.getMessage())
                    .build();
        }
    }

    private void performLogin() throws IOException, InterruptedException {
        // Create the HttpClient with cookie handling
        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        // Prepare login data
        String postData = "j_username=admin&j_password=admin";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .POST(HttpRequest.BodyPublishers.ofString(postData))
                .build();

        // Send the login request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Login failed: HTTP " + response.statusCode());
        }

        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.get(URI.create(LOGIN_URL));

        // Ensure the session cookie JSESSIONID is present
        boolean hasSessionCookie = cookies.stream().anyMatch(cookie -> cookie.getName().equals("JSESSIONID"));
        if (!hasSessionCookie) {
            throw new IOException("Failed to obtain JSESSIONID cookie");
        }
    }

    private String fetchMetrics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(METRICS_URL))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch metrics: HTTP " + response.statusCode());
        }

        return response.body();
    }

    private double parseMetric(String metrics, String metricName) {
        // Regex to find the metric value
        String regex = metricName + "\\{.*?\\} ([0-9.E+-]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(metrics);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0.0; // Default if metric not found
    }
}
