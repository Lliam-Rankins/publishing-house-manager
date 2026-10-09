package publishinghouse;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class for performing the project operations related to people
 */
public class People {
    public static boolean assignEditorToPublication(int editorId, int pubId, boolean invited) {
        String query = "INSERT INTO Edits VALUES (%d, %d, %b)";
        query = String.format(query, editorId, pubId, invited);

        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't assign editor to publication");
            return false;
        }

        return true;
    }

    public static boolean removeEditorFromPublication(int editorId, int pubId) {
        String query = "DELETE FROM Edits WHERE pID = %d AND pubID = %d";
        query = String.format(query, editorId, pubId);

        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't remove editor from publication");
            return false;
        }

        return true;
    }

    /**
     * Transaction and rollback - entering payments. Ensure that the payment is
     * created first,
     * then linked to the person. One cannot happen without the other.
     * 
     * @param paymentId   id of the payment
     * @param amount      amout of the payment
     * @param dateIssued  date payment was issued
     * @param workType    type of work of the payment
     * @param dateClaimed date payment was claimed
     * @param personId    id of person paid
     * @return true if payment created, false if not
     */
    public static boolean enterPayment(int paymentId, float amount, java.sql.Date dateIssued,
            String workType, java.sql.Date dateClaimed, int personId) {
        DBManager.beginTransaction();

        String query1 = "INSERT INTO Payment VALUES (%d, %f, %s, '%s', %s)";
        String query2 = "INSERT INTO Receives VALUES (%d, %d)";

        // Handle null values for parameters

        String dateClaimedValue = (dateClaimed == null) ? "NULL"
                : "'" + dateClaimed.toString() + "'";
        String dateIssuedValue = "'" + dateIssued.toString() + "'";
        query1 = String.format(query1, paymentId, amount, dateIssuedValue, workType, dateClaimedValue);
        query2 = String.format(query2, personId, paymentId);

        if (!DBManager.executeUpdate(query1)) { // If we fail to add the payment to the Payments
                                                // table
            System.out.println("Couldn't add payment to database");
            DBManager.rollbackTransaction();
            return false;
        } else if (!DBManager.executeUpdate(query2)) { // If we fail to add the payment to the
                                                       // Receives table
            System.out.println("Couldn't add entry to Receives table");
            DBManager.rollbackTransaction();
            return false;
        }
        DBManager.commitTransaction(); // Commit the transaction if all operations are successful

        return true;
    }

    public static boolean claimPayment(int paymentId, String dateClaimed) {
        DBManager.beginTransaction();

        String query = "UPDATE Payment SET dateClaimed = '%s' WHERE paymentID = %d";
        query = String.format(query, dateClaimed, paymentId);
        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't update payment claim date in database");
            DBManager.rollbackTransaction();
            return false;
        }

        DBManager.commitTransaction();
        return true;
    }

    public static boolean listUnclaimedPayments(String startDate, String endDate) {
        String query = "SELECT * FROM Payment WHERE dateClaimed IS NULL AND dateIssued BETWEEN '%s' AND '%s'";
        query = String.format(query, startDate, endDate);

        ResultSet rs = DBManager.executeQuery(query);

        try {
            while (rs.next()) {
                int paymentId = rs.getInt("paymentID");
                float amount = rs.getFloat("amount");
                String dateIssued = rs.getString("dateIssued");
                String workType = rs.getString("workType");
                String dateClaimed = rs.getString("dateClaimed");
                System.out.println("paymentId: " + paymentId + " | amount: " + amount
                        + " | dateIssued: " + dateIssued + " | workType: " + workType
                        + " | dateClaimed: " + dateClaimed);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean viewPublicationsByEditor(int pID) {
        String query = "SELECT * FROM Publication WHERE pubID IN(SELECT pubID from Edits where pID = %d)";
        query = String.format(query, pID);
        ResultSet rs = DBManager.executeQuery(query);
        try {
            while (rs.next()) {
                int pubId = rs.getInt("pubID");
                String type = rs.getString("type");
                String title = rs.getString("title");
                String pubPeriodicity = rs.getString("pubPeriodicity");
                System.out.println("pubID: " + pubId + " | type: " + type
                        + " | title: " + title + " | pubPeriodicity: " + pubPeriodicity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }
}
