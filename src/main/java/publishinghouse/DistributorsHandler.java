package publishinghouse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class DistributorsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String result = null;

        if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> params = HttpUtils.parseFormData(exchange);
            String action = HttpUtils.getString(params, "action");
            Distributor distributor = DBManager.distributor != null ? DBManager.distributor : new Distributor(DBManager.connection);

            if ("addDistributor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int distribID = HttpUtils.getInt(params, "distribID", 0);
                    float balance = HttpUtils.getFloat(params, "balance", 0.0f);
                    String contactName = HttpUtils.getString(params, "contactName");
                    String phoneNumber = HttpUtils.getString(params, "phoneNumber");
                    String category = HttpUtils.getString(params, "category");
                    String name = HttpUtils.getString(params, "name");
                    String street = HttpUtils.getString(params, "street");
                    String city = HttpUtils.getString(params, "city");
                    String state = HttpUtils.getString(params, "state");
                    boolean success = distributor.addDistributor(distribID, balance, contactName, phoneNumber, category, name, street, city, state);
                    if (success) {
                        System.out.println("Distributor added successfully.");
                    } else {
                        System.out.println("Failed to add distributor.");
                    }
                });
            } else if ("updateDistributor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int distribID = HttpUtils.getInt(params, "distribID", 0);
                    String field = HttpUtils.getString(params, "field");
                    String value = HttpUtils.getString(params, "value");
                    distributor.updateDistributor(field, value, distribID);
                });
            } else if ("deleteDistributor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int distribID = HttpUtils.getInt(params, "distribID", 0);
                    distributor.deleteDistributor(distribID);
                });
            } else if ("inputOrderBookEdition".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int distribID = HttpUtils.getInt(params, "distribID", 0);
                    int oID = HttpUtils.getInt(params, "oID", 0);
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    String dueBy = HttpUtils.getString(params, "dueBy");
                    Float shippingCost = HttpUtils.getFloatOrNull(params, "shippingCost");
                    String datePlaced = HttpUtils.getString(params, "datePlaced");
                    String deliveryStatus = HttpUtils.getString(params, "deliveryStatus");
                    String paymentStatus = HttpUtils.getString(params, "paymentStatus");
                    int copies = HttpUtils.getInt(params, "copies", 1);
                    boolean success = distributor.inputOrderISBN(distribID, oID, isbn, dueBy, shippingCost, datePlaced, deliveryStatus, paymentStatus, copies);
                    if (success) {
                        System.out.println("Book edition order placed successfully.");
                    } else {
                        System.out.println("Failed to place book edition order.");
                    }
                });
            } else if ("inputOrderIssue".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int distribID = HttpUtils.getInt(params, "distribID", 0);
                    int oID = HttpUtils.getInt(params, "oID", 0);
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String issueTitle = HttpUtils.getString(params, "issueTitle");
                    String dueBy = HttpUtils.getString(params, "dueBy");
                    Float shippingCost = HttpUtils.getFloatOrNull(params, "shippingCost");
                    String datePlaced = HttpUtils.getString(params, "datePlaced");
                    String deliveryStatus = HttpUtils.getString(params, "deliveryStatus");
                    String paymentStatus = HttpUtils.getString(params, "paymentStatus");
                    int copies = HttpUtils.getInt(params, "copies", 1);
                    boolean success = distributor.inputOrderIssue(distribID, oID, pubID, issueTitle, dueBy, shippingCost, datePlaced, deliveryStatus, paymentStatus, copies);
                    if (success) {
                        System.out.println("Issue order placed successfully.");
                    } else {
                        System.out.println("Failed to place issue order.");
                    }
                });
            } else if ("billDistributor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int distribID = HttpUtils.getInt(params, "distribID", 0);
                    int oID = HttpUtils.getInt(params, "oID", 0);
                    boolean success = distributor.billDistributor(oID, distribID);
                    if (success) {
                        System.out.println("Distributor billed successfully.");
                    } else {
                        System.out.println("Billing failed. Please check the order ID.");
                    }
                });
            } else if ("receivePayment".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int distribID = HttpUtils.getInt(params, "distribID", 0);
                    int oID = HttpUtils.getInt(params, "oID", 0);
                    boolean success = distributor.receivePayment(oID, distribID);
                    if (success) {
                        System.out.println("Payment received successfully.");
                    } else {
                        System.out.println("Payment failed. Please check the order ID.");
                    }
                });
            } else if ("identifyMismatchedDistributors".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    distributor.identifyMismatchedDistributors();
                });
            } else if ("listDistributors".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    String category = HttpUtils.getString(params, "category");
                    String city = HttpUtils.getString(params, "city");
                    distributor.listDistributors(category, city);
                });
            }
        }

        StringBuilder html = new StringBuilder();

        // Add Distributor
        html.append("<fieldset>\n<legend>Add Distributor</legend>\n");
        html.append("<form method=\"POST\" action=\"/distributors\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"addDistributor\">\n");
        html.append("  <p><label>Distributor ID: <input type=\"number\" name=\"distribID\" required></label></p>\n");
        html.append("  <p><label>Balance: <input type=\"number\" step=\"0.01\" name=\"balance\" required></label></p>\n");
        html.append("  <p><label>Contact Name: <input type=\"text\" name=\"contactName\" required></label></p>\n");
        html.append("  <p><label>Phone Number (optional): <input type=\"text\" name=\"phoneNumber\"></label></p>\n");
        html.append("  <p><label>Category (optional): <input type=\"text\" name=\"category\"></label></p>\n");
        html.append("  <p><label>Company Name (optional): <input type=\"text\" name=\"name\"></label></p>\n");
        html.append("  <p><label>Street: <input type=\"text\" name=\"street\" required></label></p>\n");
        html.append("  <p><label>City: <input type=\"text\" name=\"city\" required></label></p>\n");
        html.append("  <p><label>State (2 letters): <input type=\"text\" maxlength=\"2\" name=\"state\" required></label></p>\n");
        html.append("  <button type=\"submit\">Add Distributor</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // Update Distributor
        html.append("<fieldset>\n<legend>Update Distributor</legend>\n");
        html.append("<form method=\"POST\" action=\"/distributors\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"updateDistributor\">\n");
        html.append("  <p><label>Distributor ID: <input type=\"number\" name=\"distribID\" required></label></p>\n");
        html.append("  <p><label>Field to update: \n");
        html.append("    <select name=\"field\">\n");
        html.append("      <option value=\"contactName\">contactName</option>\n");
        html.append("      <option value=\"phoneNumber\">phoneNumber</option>\n");
        html.append("      <option value=\"category\">category</option>\n");
        html.append("      <option value=\"name\">name</option>\n");
        html.append("      <option value=\"street\">street</option>\n");
        html.append("      <option value=\"city\">city</option>\n");
        html.append("      <option value=\"state\">state</option>\n");
        html.append("      <option value=\"balance\">balance</option>\n");
        html.append("    </select>\n");
        html.append("  </label></p>\n");
        html.append("  <p><label>New Value: <input type=\"text\" name=\"value\" required></label></p>\n");
        html.append("  <button type=\"submit\">Update Distributor</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // Delete Distributor
        html.append("<fieldset>\n<legend>Delete Distributor</legend>\n");
        html.append("<form method=\"POST\" action=\"/distributors\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"deleteDistributor\">\n");
        html.append("  <p><label>Distributor ID: <input type=\"number\" name=\"distribID\" required></label></p>\n");
        html.append("  <button type=\"submit\">Delete Distributor</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // Input Order for Book Edition
        html.append("<fieldset>\n<legend>Input Order - Book Edition</legend>\n");
        html.append("<form method=\"POST\" action=\"/distributors\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"inputOrderBookEdition\">\n");
        html.append("  <p><label>Distributor ID: <input type=\"number\" name=\"distribID\" required></label></p>\n");
        html.append("  <p><label>Order ID: <input type=\"number\" name=\"oID\" required></label></p>\n");
        html.append("  <p><label>ISBN: <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <p><label>Due By (YYYY-MM-DD, optional): <input type=\"date\" name=\"dueBy\"></label></p>\n");
        html.append("  <p><label>Shipping Cost (optional): <input type=\"number\" step=\"0.01\" name=\"shippingCost\"></label></p>\n");
        html.append("  <p><label>Date Placed (YYYY-MM-DD): <input type=\"date\" name=\"datePlaced\" required></label></p>\n");
        html.append("  <p><label>Delivery Status: <input type=\"text\" name=\"deliveryStatus\" required></label></p>\n");
        html.append("  <p><label>Payment Status: <input type=\"text\" name=\"paymentStatus\" required></label></p>\n");
        html.append("  <p><label>Number of Copies: <input type=\"number\" name=\"copies\" required></label></p>\n");
        html.append("  <button type=\"submit\">Place Book Order</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // Input Order for Issue
        html.append("<fieldset>\n<legend>Input Order - Issue</legend>\n");
        html.append("<form method=\"POST\" action=\"/distributors\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"inputOrderIssue\">\n");
        html.append("  <p><label>Distributor ID: <input type=\"number\" name=\"distribID\" required></label></p>\n");
        html.append("  <p><label>Order ID: <input type=\"number\" name=\"oID\" required></label></p>\n");
        html.append("  <p><label>Publication ID: <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Issue Title: <input type=\"text\" name=\"issueTitle\" required></label></p>\n");
        html.append("  <p><label>Due By (YYYY-MM-DD, optional): <input type=\"date\" name=\"dueBy\"></label></p>\n");
        html.append("  <p><label>Shipping Cost (optional): <input type=\"number\" step=\"0.01\" name=\"shippingCost\"></label></p>\n");
        html.append("  <p><label>Date Placed (YYYY-MM-DD): <input type=\"date\" name=\"datePlaced\" required></label></p>\n");
        html.append("  <p><label>Delivery Status: <input type=\"text\" name=\"deliveryStatus\" required></label></p>\n");
        html.append("  <p><label>Payment Status: <input type=\"text\" name=\"paymentStatus\" required></label></p>\n");
        html.append("  <p><label>Number of Copies: <input type=\"number\" name=\"copies\" required></label></p>\n");
        html.append("  <button type=\"submit\">Place Issue Order</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // Bill Distributor
        html.append("<fieldset>\n<legend>Bill Distributor for Order</legend>\n");
        html.append("<form method=\"POST\" action=\"/distributors\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"billDistributor\">\n");
        html.append("  <p><label>Distributor ID: <input type=\"number\" name=\"distribID\" required></label></p>\n");
        html.append("  <p><label>Order ID: <input type=\"number\" name=\"oID\" required></label></p>\n");
        html.append("  <button type=\"submit\">Bill Distributor</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // Receive Payment
        html.append("<fieldset>\n<legend>Receive Payment from Distributor</legend>\n");
        html.append("<form method=\"POST\" action=\"/distributors\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"receivePayment\">\n");
        html.append("  <p><label>Distributor ID: <input type=\"number\" name=\"distribID\" required></label></p>\n");
        html.append("  <p><label>Order ID: <input type=\"number\" name=\"oID\" required></label></p>\n");
        html.append("  <button type=\"submit\">Receive Payment</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // Identify Mismatched Distributors
        html.append("<fieldset>\n<legend>Identify Mismatched Distributors</legend>\n");
        html.append("<form method=\"POST\" action=\"/distributors\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"identifyMismatchedDistributors\">\n");
        html.append("  <p>Identify distributors whose total billed amount does not match the sum of recorded payments.</p>\n");
        html.append("  <button type=\"submit\">Identify Mismatched</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // List Distributors
        html.append("<fieldset>\n<legend>List Distributors by Category and City</legend>\n");
        html.append("<form method=\"POST\" action=\"/distributors\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"listDistributors\">\n");
        html.append("  <p><label>Category: <input type=\"text\" name=\"category\" required></label></p>\n");
        html.append("  <p><label>City: <input type=\"text\" name=\"city\" required></label></p>\n");
        html.append("  <button type=\"submit\">List Distributors</button>\n");
        html.append("</form>\n</fieldset>\n");

        String page = HttpUtils.renderPage("Publishing House Manager - Distributors", result, html.toString());
        HttpUtils.sendResponse(exchange, 200, page);
    }
}
