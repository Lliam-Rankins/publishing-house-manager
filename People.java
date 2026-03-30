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

    public static boolean createPayment() {
        System.out.println("[createPayment]");
        return false;
    }

    public static boolean claimPayment() {
        System.out.println("[claimPayment]");
        return false;
    }

    public static boolean listUnclaimedPayments() {
        System.out.println("[listUnclaimedPayments]");
        return false;
    }
}
