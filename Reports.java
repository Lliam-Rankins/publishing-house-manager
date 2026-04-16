import java.sql.ResultSet;
import java.sql.SQLException;

public class Reports {
    // Class for Reports
    // Generate weekly and monthly reports: number and total price of copies of each
    // publication bought per distributor, per week, and per month; total revenue of
    // the publishing house; total expenses (i.e., shipping costs and salaries).
    // Calculate the total current number of distributors;
    // calculate total revenue (since inception) per city and per distributor.
    // Calculate total payments to the editors and authors, per time period (month)
    // and per work type (book authorship, article authorship, or editorial work).
    // View all publications assigned to a specific editor.

    /**
     * Count the number of distributors
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean countDistributors() {
        // SQL Query
        String query = "SELECT COUNT(*) FROM Distributor;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count distriubutors");
            return false;
        }

        try {
            table.next();
            System.out.println("Total Distributors: " + table.getInt(1));
            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count distriubutors");
            return false;
        }
    }

    /**
     * Calculate how many orders were made by each distributor
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean countOrdersByDistributor() {
        // SQL Query
        String query = "SELECT distribID, COUNT(*) FROM PlacedBy GROUP BY distribID;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count orders per distriubutors");
            return false;
        }

        try {
            System.out.println("Total orders per distributor");
            while (table.next()) {
                System.out.println("DistribID: " + table.getString("distribID") + " | Count: " + table.getInt(2));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count orders per distriubutors");
            return false;
        }
    }

    /**
     * Calculate number of copies of each edition required by each distributor
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean countEditionsByDistributor() {
        // SQL Query
        String query = "SELECT SUM(copies), distribID, ISBN FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsISBN GROUP BY distribID, ISBN;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count editions per distriubutors");
            return false;
        }

        try {
            System.out.println("Total editions per distributor");
            while (table.next()) {
                System.out.println("DistribID: " + table.getString("distribID") + ", ISBN: " + table.getString("ISBN")
                        + " | Copies: " + table.getInt(1));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count editions per distriubutors");
            return false;
        }
    }

    /**
     * Calculate number of copies of each issue required by each distribtor
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean countIssuesByDistributor() {
        // SQL Query
        String query = "SELECT SUM(copies), distribID, issueTitle  FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsIssue GROUP BY distribID, issueTitle;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count issues per distriubutors");
            return false;
        }

        try {
            System.out.println("Total issues per distributor");
            while (table.next()) {
                System.out.println("DistribID: " + table.getString("distribID") + ", IssueTitle: "
                        + table.getString("issueTitle") + " | Copies: " + table.getInt(1));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count issues per distriubutors");
            return false;
        }
    }

    /**
     * Calculate the total amount of money per distributor and per issue
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean totalCostPerIssuesPerDistributor() {
        // SQL Query
        String query = "SELECT SUM(price * copies), distribID, issueTitle FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsIssue NATURAL JOIN Issue GROUP BY distribID, issueTitle;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count cost per issue per distriubutors");
            return false;
        }

        try {
            System.out.println("Total cost per issue per distributor");
            while (table.next()) {
                System.out.println("DistribID: " + table.getString("distribID") + ", IssueTitle: "
                        + table.getString("issueTitle") + " | Cost: " + table.getInt(1));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count cost per issue per distriubutors");
            return false;
        }
    }

    /**
     * Calculate the total amount of money per distributor and per issue
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean totalCostPerEditionPerDistributor() {
        // SQL Query
        String query = "SELECT SUM(price * copies), distribID, editionTitle FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsISBN NATURAL JOIN Edition GROUP BY distribID, editionTitle;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count cost per edition per distriubutors");
            return false;
        }

        try {
            System.out.println("Total cost per edition per distributor");
            while (table.next()) {
                System.out.println("DistribID: " + table.getString("distribID") + ", EditionTitle: "
                        + table.getString("editionTitle") + " | Cost: " + table.getInt(1));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count cost per edition per distriubutors");
            return false;
        }
    }

    /**
     * Calculate the number of copies of publications per distributor per week
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean countPublicationsPerDistributorPerWeek() {
        // SQL Query
        String query = "SELECT DISTINCT distribID, WEEK(datePlaced) AS week ,SUM(copies) AS totalCopies, 'Issue' AS type FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsIssue NATURAL JOIN Issue GROUP BY distribID, WEEK(datePlaced) UNION ALL SELECT distribID, WEEK(datePlaced) AS week , SUM(copies) AS totalCopies, 'Edition' AS type FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsISBN NATURAL JOIN Edition GROUP BY distribID, WEEK(datePlaced) ORDER BY distribID, week;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count publications per distributor per week");
            return false;
        }

        try {
            System.out.println("Total publications per distributor per week");
            while (table.next()) {
                System.out.println("DistribID: " + table.getString("distribID") + ", Week: " + table.getInt("week")
                        + " | Total Publications: " + table.getInt("totalCopies"));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count publications per distributor per week");
            return false;
        }

    }

    /**
     * Calculate the number of copies of publications per distributor per month
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean countPublicationsPerDistributorPerMonth() {
        // SQL Query
        String query = "SELECT DISTINCT distribID, MONTH(datePlaced) AS month ,SUM(copies) AS totalCopies, 'Issue' AS type FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsIssue NATURAL JOIN Issue GROUP BY distribID, MONTH(datePlaced) UNION ALL SELECT distribID, MONTH(datePlaced) AS month , SUM(copies) AS totalCopies , 'Edition' AS type FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsISBN NATURAL JOIN Edition GROUP BY distribID, MONTH(datePlaced) ORDER BY distribID, month;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count publications per distributor per month");
            return false;
        }

        try {
            System.out.println("Total publications per distributor per month");
            while (table.next()) {
                System.out.println("DistribID: " + table.getString("distribID") + ", Month: " + table.getInt("month")
                        + " | Copies: " + table.getInt("totalCopies"));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count publications per distributor per month");
            return false;
        }
    }

    /**
     * Calculate total cost per distributor per week
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean totalCostPerDistributorPerWeek() {
        // SQL Query
        String query = "SELECT distribID, week, SUM(totalPrice) AS totalPrice FROM (SELECT distribID, WEEK(datePlaced) AS week, SUM(price * copies) AS totalPrice FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsIssue NATURAL JOIN Issue GROUP BY distribID, WEEK(datePlaced) UNION ALL SELECT distribID, WEEK(datePlaced) AS week, SUM(price * copies) AS totalPrice FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsISBN NATURAL JOIN Edition GROUP BY distribID, WEEK(datePlaced)) AS combined GROUP BY distribID, week  HAVING totalPrice IS NOT NULL;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count total cost per distributor per week");
            return false;
        }

        try {
            System.out.println("Total cost per distributor per week");
            while (table.next()) {
                System.out.println("Distributor: " + table.getString("distribID") + ", Week: " + table.getInt("week")
                        + " | Cost:" + table.getInt("totalPrice"));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count total cost per distributor per week");
            return false;
        }
    }

    /**
     * Calculate total cost per distributor per month
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean totalCostPerDistributorPerMonth() {
        // SQL Query
        String query = "SELECT distribID, month, SUM(totalPrice) AS totalPrice FROM (SELECT distribID, MONTH(datePlaced) AS month, SUM(price * copies) AS totalPrice FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsIssue NATURAL JOIN Issue GROUP BY distribID, MONTH(datePlaced) UNION ALL SELECT distribID, MONTH(datePlaced) AS month, SUM(price * copies) AS totalPrice FROM `Order` NATURAL JOIN PlacedBy NATURAL JOIN ContainsISBN NATURAL JOIN Edition GROUP BY distribID, MONTH(datePlaced)) AS combined GROUP BY distribID, month HAVING totalPrice IS NOT NULL;\r\n"
                + "";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count total cost per distributor per month");
            return false;
        }

        try {
            System.out.println("Total cost per distributor per month");
            while (table.next()) {
                System.out.println("Distributor: " + table.getString("distribID") + ", Month: " + table.getInt("month")
                        + " | Cost: " + table.getInt("totalPrice"));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count total cost per distributor per month");
            return false;
        }
    }

    /**
     * Calculate total revenue per city
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean totalRevenuePerCity() {
        // SQL Query
        String query = "SELECT city, SUM(revenue) AS totalRevenue FROM (SELECT city, SUM(price * copies) as revenue FROM Distributor NATURAL JOIN PlacedBy NATURAL JOIN `Order` NATURAL JOIN ContainsISBN NATURAL JOIN Edition GROUP BY city UNION ALL SELECT city, SUM(price * copies) as revenue FROM Distributor NATURAL JOIN PlacedBy NATURAL JOIN `Order` NATURAL JOIN ContainsIssue NATURAL JOIN Issue GROUP BY city) AS CombinedResults GROUP BY city;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count total revenue per city");
            return false;
        }

        try {
            System.out.println("Total revenue per city");
            while (table.next()) {
                System.out.println("City: " + table.getString("city") + " | Revenue: " + table.getInt("totalRevenue"));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count total revenue per city");
            return false;
        }
    }

    /**
     * Calcualte total revenue per distributor
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean totalRevenuePerDistributor() {
        // SQL Query
        String query = "SELECT distribID, SUM(revenue) AS totalRevenue FROM (SELECT Distributor.distribID, SUM(price * copies) as revenue FROM Distributor NATURAL JOIN PlacedBy NATURAL JOIN `Order` NATURAL JOIN ContainsISBN NATURAL JOIN Edition GROUP BY Distributor.distribID UNION ALL SELECT Distributor.distribID, SUM(price * copies) as revenue FROM Distributor NATURAL JOIN PlacedBy NATURAL JOIN `Order` NATURAL JOIN ContainsIssue NATURAL JOIN Issue GROUP BY Distributor.distribID) AS CombinedResults GROUP BY distribID;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count total revenue per distributor");
            return false;
        }

        try {
            System.out.println("Total revenue per distributor");
            while (table.next()) {
                System.out.println(
                        "DistribID: " + table.getString("distribID") + " | Revenue: " + table.getInt("totalRevenue"));
            }

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count total revenue per distributor");
            return false;
        }
    }

    /**
     * Calculate total expenses (Shipping cost and payroll)
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean totalExpenses() {
        // SQL Query
        String query = "SELECT SUM(cost) FROM (SELECT shippingCost as cost FROM `Order` UNION ALL SELECT amount as cost FROM Payment) as costTable;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count total expenses");
            return false;
        }

        try {
            table.next();
            System.out.println("Total Expenses: " + table.getInt(1));
            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count total expenses");
            return false;
        }
    }

    /**
     * Calculate total revenue
     *
     * @return true if the query was executed correctly, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean totalRevenue() {
        // SQL Query
        String query = "SELECT SUM(revenue) AS totalRevenue FROM (SELECT Distributor.distribID, SUM(price * copies) as revenue FROM Distributor NATURAL JOIN PlacedBy NATURAL JOIN `Order` NATURAL JOIN ContainsISBN NATURAL JOIN Edition GROUP BY Distributor.distribID UNION ALL SELECT Distributor.distribID, SUM(price * copies) as revenue FROM Distributor NATURAL JOIN PlacedBy NATURAL JOIN `Order` NATURAL JOIN ContainsIssue NATURAL JOIN Issue GROUP BY Distributor.distribID) AS CombinedResults;";

        ResultSet table = DBManager.executeQuery(query);

        if (table == null) {
            System.out.println("Couldn't count total revenue");
            return false;
        }

        try {
            table.next();
            System.out.println("Total Revenue: " + table.getInt(1));
            return true;
        } catch (Exception e) {
            System.out.println("Couldn't count total revenue");
            return false;
        }
    }
}