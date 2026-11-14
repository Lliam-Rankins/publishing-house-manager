package publishinghouse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.Date;
import java.util.Map;

public class PeopleHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String result = null;

        if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> params = HttpUtils.parseFormData(exchange);
            String action = HttpUtils.getString(params, "action");

            if ("assignEditorToPublication".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int editorId = HttpUtils.getInt(params, "editorId", 0);
                    int pubId = HttpUtils.getInt(params, "pubId", 0);
                    boolean invited = HttpUtils.getBoolean(params, "invited");
                    boolean success = People.assignEditorToPublication(editorId, pubId, invited);
                    if (success) {
                        System.out.println("Editor assigned to publication successfully.");
                    }
                });
            } else if ("removeEditorFromPublication".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int editorId = HttpUtils.getInt(params, "editorId", 0);
                    int pubId = HttpUtils.getInt(params, "pubId", 0);
                    boolean success = People.removeEditorFromPublication(editorId, pubId);
                    if (success) {
                        System.out.println("Editor removed from publication successfully.");
                    }
                });
            } else if ("enterPayment".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int paymentId = HttpUtils.getInt(params, "paymentId", 0);
                    float amount = HttpUtils.getFloat(params, "amount", 0.0f);
                    Date dateIssued = HttpUtils.getDate(params, "dateIssued");
                    String workType = HttpUtils.getString(params, "workType");
                    Date dateClaimed = HttpUtils.getDate(params, "dateClaimed");
                    int personId = HttpUtils.getInt(params, "personId", 0);
                    boolean success = People.enterPayment(paymentId, amount, dateIssued, workType, dateClaimed, personId);
                    if (success) {
                        System.out.println("Payment entered successfully.");
                    }
                });
            } else if ("claimPayment".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int paymentId = HttpUtils.getInt(params, "paymentId", 0);
                    String dateClaimed = HttpUtils.getString(params, "dateClaimed");
                    boolean success = People.claimPayment(paymentId, dateClaimed);
                    if (success) {
                        System.out.println("Payment claimed successfully.");
                    }
                });
            } else if ("listUnclaimedPayments".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    String startDate = HttpUtils.getString(params, "startDate");
                    String endDate = HttpUtils.getString(params, "endDate");
                    People.listUnclaimedPayments(startDate, endDate);
                });
            } else if ("viewPublicationsByEditor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int editorId = HttpUtils.getInt(params, "editorId", 0);
                    People.viewPublicationsByEditor(editorId);
                });
            }
        }

        StringBuilder html = new StringBuilder();

        // Assign Editor
        html.append("<fieldset>\n<legend>Assign Editor To Publication</legend>\n");
        html.append("<form method=\"POST\" action=\"/people\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"assignEditorToPublication\">\n");
        html.append("  <p><label>Editor ID (pID): <input type=\"number\" name=\"editorId\" required></label></p>\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubId\" required></label></p>\n");
        html.append("  <p><label>Invited: <select name=\"invited\"><option value=\"true\">true</option><option value=\"false\">false</option></select></label></p>\n");
        html.append("  <button type=\"submit\">Assign Editor</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // Remove Editor
        html.append("<fieldset>\n<legend>Remove Editor From Publication</legend>\n");
        html.append("<form method=\"POST\" action=\"/people\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"removeEditorFromPublication\">\n");
        html.append("  <p><label>Editor ID (pID): <input type=\"number\" name=\"editorId\" required></label></p>\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubId\" required></label></p>\n");
        html.append("  <button type=\"submit\">Remove Editor</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // Enter Payment
        html.append("<fieldset>\n<legend>Enter Payment</legend>\n");
        html.append("<form method=\"POST\" action=\"/people\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"enterPayment\">\n");
        html.append("  <p><label>Payment ID: <input type=\"number\" name=\"paymentId\" required></label></p>\n");
        html.append("  <p><label>Person ID (pID): <input type=\"number\" name=\"personId\" required></label></p>\n");
        html.append("  <p><label>Amount: <input type=\"number\" step=\"0.01\" name=\"amount\" required></label></p>\n");
        html.append("  <p><label>Date Issued (YYYY-MM-DD): <input type=\"date\" name=\"dateIssued\" required></label></p>\n");
        html.append("  <p><label>Work Type: <input type=\"text\" name=\"workType\" required></label></p>\n");
        html.append("  <p><label>Date Claimed (YYYY-MM-DD, optional): <input type=\"date\" name=\"dateClaimed\"></label></p>\n");
        html.append("  <button type=\"submit\">Enter Payment</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // Claim Payment
        html.append("<fieldset>\n<legend>Claim Payment</legend>\n");
        html.append("<form method=\"POST\" action=\"/people\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"claimPayment\">\n");
        html.append("  <p><label>Payment ID: <input type=\"number\" name=\"paymentId\" required></label></p>\n");
        html.append("  <p><label>Date Claimed (YYYY-MM-DD): <input type=\"date\" name=\"dateClaimed\" required></label></p>\n");
        html.append("  <button type=\"submit\">Claim Payment</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // List Unclaimed Payments
        html.append("<fieldset>\n<legend>List Unclaimed Payments</legend>\n");
        html.append("<form method=\"POST\" action=\"/people\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"listUnclaimedPayments\">\n");
        html.append("  <p><label>Start Date (YYYY-MM-DD): <input type=\"date\" name=\"startDate\" required></label></p>\n");
        html.append("  <p><label>End Date (YYYY-MM-DD): <input type=\"date\" name=\"endDate\" required></label></p>\n");
        html.append("  <button type=\"submit\">List Unclaimed Payments</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // View Publications by Editor
        html.append("<fieldset>\n<legend>View Publications By Editor</legend>\n");
        html.append("<form method=\"POST\" action=\"/people\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"viewPublicationsByEditor\">\n");
        html.append("  <p><label>Editor ID (pID): <input type=\"number\" name=\"editorId\" required></label></p>\n");
        html.append("  <button type=\"submit\">View Publications</button>\n");
        html.append("</form>\n</fieldset>\n");

        String page = HttpUtils.renderPage("Publishing House Manager - People", result, html.toString());
        HttpUtils.sendResponse(exchange, 200, page);
    }
}
