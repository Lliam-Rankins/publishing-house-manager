import java.util.Scanner;

/**
 * Class for handling user input necessary for completing People operations.
 */
public class PeopleUI {
    /** The Scanner for this UI */
    private Scanner s;

    /**
     * Creates a new PeopleUI handler given a Scanner object.
     * 
     * @param s the Scanner to use
     */
    public PeopleUI(Scanner s) {
        this.s = s;
    }

    /**
     * Handles the assigmnent of editors to a publication Narrative operation: "Assign/Remove
     * editor(s) to/from a publication." Internal name: assignEditorToPublication
     */
    public void handleAssignEditorToPublication() {
        System.out.println("Enter the editor id:");
        int editorId = s.nextInt();
        System.out.println("Enter the publication id:");
        int pubId = s.nextInt();
        System.out.println("Enter true if the editor was invited, false otherwise");
        boolean invited = s.nextBoolean();
        People.assignEditorToPublication(editorId, pubId, invited);
    }

    /**
     * Handles the removal of editors from a publication Narrative operation: "Assign/Remove
     * editor(s) to/from a publication." Internal name: removeEditorFromPublication
     */
    public void handleRemoveEditorFromPublication() {
        System.out.println("Enter the editor id:");
        int editorId = s.nextInt();
        System.out.println("Enter the publication id:");
        int pubId = s.nextInt();
        People.removeEditorFromPublication(editorId, pubId);
    }

    /**
     * Handles entering a payment Narrative operation: "Enter payment for author or editor;"
     * Internal name: enterPayment
     */
    public void handleEnterPayment() {
        // Enter paymentId, amount, dateIssued, workType, dateClaimed for Payment
        int paymentId = s.nextInt();
        System.out.println("Enter the payment amount:");
        float amount = s.nextFloat();
        s.nextLine();
        System.out.println("Enter the payment issue date (YYYY-MM-DD):");
        String dateIssued = s.nextLine();
        System.out.println("Enter the work type:");
        String workType = s.nextLine();

        // dateClaimed may be null, so we must allow for this
        System.out.println("Enter the date the payment was claimed (Press Enter for NULL):");
        String dateClaimed = s.nextLine();
        if (dateClaimed.equalsIgnoreCase("")) {
            dateClaimed = null;
        }

        // Enter pID and reuse paymentId for Receives
        System.out.println("Enter the payment id:");
        System.out.println("Enter the id of the person receiving the payment:");
        int personId = s.nextInt();

        People.enterPayment(paymentId, amount, dateIssued, workType, dateClaimed, personId);
    }

    /**
     * Handles claiming a payment Narrative operation: "Update when each payment was claimed by its
     * addressee" Internal name: claimPayment
     */
    public void handleClaimPayment() {
        System.out.println("Enter the payment id:");
        int paymentId = s.nextInt();
        s.nextLine();
        System.out.println("Enter the date the payment was claimed (YYYY-MM-DD):");
        String dateClaimed = s.nextLine();

        System.out.println(paymentId);
        People.claimPayment(paymentId, dateClaimed);
    }

    /**
     * Handles listing unclaimed payments Narrative operation: "List payments that were issued but
     * not claimed within a specified time window (date range)" Internal name: listUnclaimedPayments
     */
    public void handleListUnclaimedPayments() {
        System.out.println("Enter the start date (YYYY-MM-DD):");
        String startDate = s.nextLine();
        System.out.println("Enter the end date (YYYY-MM-DD):");
        String endDate = s.nextLine();

        People.listUnclaimedPayments(startDate, endDate);
    }
}
