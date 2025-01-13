package fish.payara.poc.adminrest.service;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/monitoring-data")
public class MonitoringDataServlet extends HttpServlet {

    Logger logger = Logger.getLogger(MonitoringDataServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String loginUrl = "http://localhost:4848/j_security_check";
        String metricsUrl = "http://localhost:4848/metrics";

        //generate the new sessionID
        String sessionId = performLogin(loginUrl);
        if (sessionId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Failed to log in to Payara server\"}");
            return;
        }

        // Fetch metrics using the session ID
        String metrics = fetchMetrics(metricsUrl, sessionId);

        // Parse metrics to extract values for cpu and memory
        double cpuUsage = parseMetric(metrics, "system_cpu_load");
        double memoryUsage = parseMetric(metrics, "memory_usedHeap_bytes");

        // JSON response
        String jsonResponse = String.format("{\"cpuUsage\": %.2f, \"memoryUsage\": %.2f}", cpuUsage, memoryUsage);
        resp.setContentType("application/json");
        resp.getWriter().write(jsonResponse);
    }

    private String performLogin(String loginUrl) throws IOException {
        String credentials = "j_username=admin&j_password=admin"; //will need to be changed in the future
        HttpURLConnection conn = (HttpURLConnection) new URL(loginUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Send login credentials
        try (OutputStream os = conn.getOutputStream()) {
            os.write(credentials.getBytes(StandardCharsets.UTF_8));
        }

        // Check response and extract session ID
        String sessionId = null;
        String cookieHeader = conn.getHeaderField("Set-Cookie");
        logger.info("Cookie header: " + cookieHeader);
        if (cookieHeader != null) {
            sessionId = extractSessionId(cookieHeader);
        }
        logger.info("Session ID: " + sessionId);
        return sessionId;
    }

    private String extractSessionId(String cookieHeader) {
        // Regex to extract JSESSIONID from Set-Cookie header
        String regex = "JSESSIONID=([^;]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cookieHeader);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String fetchMetrics(String metricsUrl, String sessionId) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(metricsUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
        conn.setInstanceFollowRedirects(true);

        // logging stuff
        logger.info("Requesting metrics with session ID: " + sessionId);

        conn.getRequestProperties().forEach((key, value) ->
                Logger.getLogger("Headers").info(key + ": " + String.join(", ", value))
        );

        // extract result
        StringBuilder metrics = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                metrics.append(line).append("\n");
            }
        }
        logger.info("Raw Metrics:  " + metrics.toString());
        return metrics.toString();
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
