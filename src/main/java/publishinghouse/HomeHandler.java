package publishinghouse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class HomeHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String result = null;

        if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> params = HttpUtils.parseFormData(exchange);
            String action = HttpUtils.getString(params, "action");
            if ("resetDatabase".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    DBManager.initialize(true);
                    System.out.println("Database tables reset successfully.");
                });
            } else if ("seedSampleData".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    Distributor distributor = DBManager.distributor != null ? DBManager.distributor : new Distributor(DBManager.connection);
                    distributor.addDistributor(1, 0.0f, "Contact A", "111-222-3333", "Bookstore", "Store A", "10 Main", "Raleigh", "NC");
                    distributor.addDistributor(2, 0.0f, "Contact B", "444-555-6666", "Library", "Store B", "20 Park", "Durham", "NC");
                    Publication.addEditionPublication(10, "Java Core", "Book", 9780000000001L, 1, "Ed 1", null, null, 50.0);
                    Publication.addPublication(20, "Tech Weekly", "Magazine", "Weekly");
                    Publication.addIssue(20, "Issue 101", java.sql.Date.valueOf("2024-03-01"), 10.0);
                    distributor.inputOrderISBN(1, 101, 9780000000001L, "2024-04-01", 15.0f, "2024-03-05", "Delivered", "Unpaid", 5);
                    distributor.inputOrderIssue(1, 102, 20, "Issue 101", "2024-04-01", 5.0f, "2024-03-06", "Delivered", "Unpaid", 10);
                    DBManager.executeUpdate("INSERT INTO Person VALUES (1, 'Alice Author')");
                    People.enterPayment(201, 500.0f, java.sql.Date.valueOf("2024-03-01"), "book authorship", null, 1);
                    People.enterPayment(202, 250.0f, java.sql.Date.valueOf("2024-03-15"), "article authorship", null, 1);
                    System.out.println("Sample data loaded successfully.");
                });
            }
        }

        StringBuilder html = new StringBuilder();
        html.append("<p>Welcome to the Publishing House Manager.</p>\n");
        html.append("<h2>Sections</h2>\n");
        html.append("<ul>\n");
        html.append("  <li><a href=\"/publications\">Publications</a>: Manage publications, editions, issues, chapters, articles, and searches.</li>\n");
        html.append("  <li><a href=\"/people\">People</a>: Manage editors, payments, and view publications by editor.</li>\n");
        html.append("  <li><a href=\"/distributors\">Distributors</a>: Manage distributors, orders, billing, and payments.</li>\n");
        html.append("  <li><a href=\"/reports\">Reports</a>: Run financial and statistical reports.</li>\n");
        html.append("</ul>\n");

        html.append("<hr>\n");
        html.append("<h2>Database Management</h2>\n");
        html.append("<form method=\"POST\" action=\"/\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"resetDatabase\">\n");
        html.append("  <p>Warning: This drops and recreates all database tables.</p>\n");
        html.append("  <button type=\"submit\">Reset Database</button>\n");
        html.append("</form>\n");

        html.append("<form method=\"POST\" action=\"/\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"seedSampleData\">\n");
        html.append("  <p>Insert sample data for testing reports and operations.</p>\n");
        html.append("  <button type=\"submit\">Load Sample Data</button>\n");
        html.append("</form>\n");

        String page = HttpUtils.renderPage("Publishing House Manager - Home", result, html.toString());
        HttpUtils.sendResponse(exchange, 200, page);
    }
}
