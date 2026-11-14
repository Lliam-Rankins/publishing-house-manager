package publishinghouse;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    @FunctionalInterface
    public interface DatabaseAction {
        void execute() throws Exception;
    }

    public static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        InputStream inputStream = exchange.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        String body = buffer.toString(StandardCharsets.UTF_8);
        if (body.isEmpty()) {
            return parameters;
        }

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
            parameters.put(key, value);
        }
        return parameters;
    }

    public static String getString(Map<String, String> params, String name) {
        String val = params.get(name);
        if (val == null) {
            return null;
        }
        String trimmed = val.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static int getInt(Map<String, String> params, String name, int defaultValue) {
        String val = getString(params, name);
        if (val == null) {
            return defaultValue;
        }
        return Integer.parseInt(val);
    }

    public static long getLong(Map<String, String> params, String name, long defaultValue) {
        String val = getString(params, name);
        if (val == null) {
            return defaultValue;
        }
        return Long.parseLong(val);
    }

    public static float getFloat(Map<String, String> params, String name, float defaultValue) {
        String val = getString(params, name);
        if (val == null) {
            return defaultValue;
        }
        return Float.parseFloat(val);
    }

    public static Float getFloatOrNull(Map<String, String> params, String name) {
        String val = getString(params, name);
        if (val == null) {
            return null;
        }
        return Float.parseFloat(val);
    }

    public static double getDouble(Map<String, String> params, String name, double defaultValue) {
        String val = getString(params, name);
        if (val == null) {
            return defaultValue;
        }
        return Double.parseDouble(val);
    }

    public static Date getDate(Map<String, String> params, String name) {
        String val = getString(params, name);
        if (val == null) {
            return null;
        }
        return Date.valueOf(val);
    }

    public static boolean getBoolean(Map<String, String> params, String name) {
        String val = getString(params, name);
        if (val == null) {
            return false;
        }
        return Boolean.parseBoolean(val) || val.equalsIgnoreCase("yes") || val.equals("1");
    }

    public static synchronized String executeWithCapture(DatabaseAction action) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        try {
            System.setOut(printStream);
            System.setErr(printStream);
            action.execute();
        } catch (Exception e) {
            printStream.println("Error: " + e.getMessage());
            e.printStackTrace(printStream);
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        String output = outputStream.toString(StandardCharsets.UTF_8).trim();
        return output.isEmpty() ? "Operation completed with no console output." : output;
    }

    public static void sendResponse(HttpExchange exchange, int statusCode, String htmlContent) throws IOException {
        byte[] bytes = htmlContent.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '&':
                    builder.append("&amp;");
                    break;
                case '<':
                    builder.append("&lt;");
                    break;
                case '>':
                    builder.append("&gt;");
                    break;
                case '"':
                    builder.append("&quot;");
                    break;
                case '\'':
                    builder.append("&#39;");
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }

    public static String renderPage(String title, String resultOutput, String bodyContent) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n<head>\n<title>").append(escapeHtml(title)).append("</title>\n</head>\n<body>\n");
        sb.append("<h1>").append(escapeHtml(title)).append("</h1>\n");
        sb.append("<nav>\n");
        sb.append("  <a href=\"/\">Home</a> |\n");
        sb.append("  <a href=\"/publications\">Publications</a> |\n");
        sb.append("  <a href=\"/people\">People</a> |\n");
        sb.append("  <a href=\"/distributors\">Distributors</a> |\n");
        sb.append("  <a href=\"/reports\">Reports</a>\n");
        sb.append("</nav>\n<hr>\n");

        if (resultOutput != null && !resultOutput.isEmpty()) {
            sb.append("<h2>Execution Output</h2>\n");
            sb.append("<pre>").append(escapeHtml(resultOutput)).append("</pre>\n<hr>\n");
        }

        sb.append(bodyContent);
        sb.append("</body>\n</html>\n");
        return sb.toString();
    }
}
