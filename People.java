import java.sql.ResultSet;
import java.sql.SQLException;

public class People {
    public static boolean assignEditorToPublication(int editorId, int pubId, boolean invited) {
        System.out.println("[assignEditorToPublication]");

        String query = "INSERT INTO Edits VALUES (%d, %d, %b)";
        query = String.format(query, editorId, pubId, invited);

        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't assign editor to publication");
            return false;
        }

        return true;
    }

    public static boolean removeEditorFromPublication(int editorId, int pubId) {
        System.out.println("[removeEditorFromPublication]");

        String query = "DELETE FROM Edits WHERE pID = %d AND pubID = %d";
        query = String.format(query, editorId, pubId);

        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't remove editor from publication");
            return false;
        }

        return true;
    }

    public static boolean enterPayment(int paymentId, float amount, String dateIssued,
            String workType, String dateClaimed, int personId) {
        System.out.println("[enterPayment]");

        DBManager.beginTransaction();

        String query1 = "INSERT INTO Payment VALUES (%d, %f, '%s', '%s', '%s')";
        String query2 = "INSERT INTO Receives VALUES (%d, %d)";
        query1 = String.format(query1, paymentId, amount, dateIssued, workType, dateClaimed);
        query2 = String.format(query2, personId, paymentId);

        if (!DBManager.executeUpdate(query1)) {
            System.out.println("Couldn't add payment to database");
            DBManager.rollbackTransaction();
            return false;
        } else if (!DBManager.executeUpdate(query2)) {
            System.out.println("Couldn't add entry to Receives table");
            DBManager.rollbackTransaction();
            return false;
        }
        DBManager.commitTransaction();

        return true;
    }

    public static boolean claimPayment(int paymentId, String dateClaimed) {
        System.out.println("[claimPayment]");

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
        System.out.println("[listUnclaimedPayments]");

        String query =
                "SELECT * FROM Payment WHERE dateClaimed IS NULL AND dateIssued BETWEEN '%s' AND '%s'";
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
}
