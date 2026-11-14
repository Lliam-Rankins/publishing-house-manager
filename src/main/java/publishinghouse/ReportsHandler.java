package publishinghouse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class ReportsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String result = null;

        if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> params = HttpUtils.parseFormData(exchange);
            String action = HttpUtils.getString(params, "action");

            if ("countDistributors".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::countDistributors);
            } else if ("countOrdersByDistributor".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::countOrdersByDistributor);
            } else if ("countEditionsByDistributor".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::countEditionsByDistributor);
            } else if ("countIssuesByDistributor".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::countIssuesByDistributor);
            } else if ("costPerIssuePerDistributor".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::totalCostPerIssuesPerDistributor);
            } else if ("costPerEditionPerDistributor".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::totalCostPerEditionPerDistributor);
            } else if ("countPublicationsPerDistributorPerWeek".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::countPublicationsPerDistributorPerWeek);
            } else if ("countPublicationsPerDistributorPerMonth".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::countPublicationsPerDistributorPerMonth);
            } else if ("totalCostPerDistributorPerWeek".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::totalCostPerDistributorPerWeek);
            } else if ("totalCostPerDistributorPerMonth".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::totalCostPerDistributorPerMonth);
            } else if ("totalRevenuePerCity".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::totalRevenuePerCity);
            } else if ("totalRevenuePerDistributor".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::totalRevenuePerDistributor);
            } else if ("totalRevenue".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::totalRevenue);
            } else if ("totalExpenses".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::totalExpenses);
            } else if ("totalPaymentsPerMonth".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::totalPaymentsPerMonth);
            } else if ("totalPaymentsPerWorkType".equals(action)) {
                result = HttpUtils.executeWithCapture(Reports::totalPaymentsPerWorkType);
            }
        }

        StringBuilder html = new StringBuilder();
        html.append("<h2>Generate Reports</h2>\n");

        String[][] reportActions = {
            {"countDistributors", "Total Number of Distributors"},
            {"countOrdersByDistributor", "Total Orders by Distributor"},
            {"countEditionsByDistributor", "Total Editions by Distributor"},
            {"countIssuesByDistributor", "Total Issues by Distributor"},
            {"costPerIssuePerDistributor", "Total Cost Per Issue Per Distributor"},
            {"costPerEditionPerDistributor", "Total Cost Per Edition Per Distributor"},
            {"countPublicationsPerDistributorPerWeek", "Publications Per Distributor Per Week"},
            {"countPublicationsPerDistributorPerMonth", "Publications Per Distributor Per Month"},
            {"totalCostPerDistributorPerWeek", "Total Cost Per Distributor Per Week"},
            {"totalCostPerDistributorPerMonth", "Total Cost Per Distributor Per Month"},
            {"totalRevenuePerCity", "Total Revenue Per City"},
            {"totalRevenuePerDistributor", "Total Revenue Per Distributor"},
            {"totalRevenue", "Total Revenue"},
            {"totalExpenses", "Total Expenses"},
            {"totalPaymentsPerMonth", "Total Payments Per Month"},
            {"totalPaymentsPerWorkType", "Total Payments Per Work Type"}
        };

        for (String[] rep : reportActions) {
            html.append("<p>\n");
            html.append("<form method=\"POST\" action=\"/reports\">\n");
            html.append("  <input type=\"hidden\" name=\"action\" value=\"").append(rep[0]).append("\">\n");
            html.append("  <button type=\"submit\">").append(rep[1]).append("</button>\n");
            html.append("</form>\n");
            html.append("</p>\n");
        }

        String page = HttpUtils.renderPage("Publishing House Manager - Reports", result, html.toString());
        HttpUtils.sendResponse(exchange, 200, page);
    }
}
