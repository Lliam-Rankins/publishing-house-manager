import java.util.Scanner;

public class PeopleUI {
    private Scanner s;

    public PeopleUI(Scanner s) {
        this.s = s;
    }

    public void handleAssignEditorToPublication() {
        System.out.println("Enter the editor id:");
        int editorId = s.nextInt();
        System.out.println("Enter the publication id:");
        int pubId = s.nextInt();
        System.out.println("Enter true if the editor was invited, false otherwise");
        boolean invited = s.nextBoolean();
        People.assignEditorToPublication(editorId, pubId, invited);
    }

    public void handleRemoveEditorFromPublication() {
        System.out.println("Enter the editor id:");
        int editorId = s.nextInt();
        System.out.println("Enter the publication id:");
        int pubId = s.nextInt();
        People.removeEditorFromPublication(editorId, pubId);
    }

    public void handleEnterPayment() {
        System.out.println("Enter the payment id:");
        int paymentId = s.nextInt();
        System.out.println("Enter the payment amount:");
        float amount = s.nextFloat();
        s.nextLine();
        System.out.println("Enter the payment issue date (YYYY-MM-DD):");
        String dateIssued = s.nextLine();
        System.out.println("Enter the work type:");
        String workType = s.nextLine();
        System.out.println("Enter the date the payment was claimed (Press Enter for NULL):");
        String dateClaimed = s.nextLine();
        if (dateClaimed.equalsIgnoreCase("")) {
            dateClaimed = null;
        }

        System.out.println("Enter the id of the person receiving the payment:");
        int personId = s.nextInt();

        People.enterPayment(paymentId, amount, dateIssued, workType, dateClaimed, personId);
    }

    public void handleClaimPayment() {
        System.out.println("Enter the payment id:");
        int paymentId = s.nextInt();
        s.nextLine();
        System.out.println("Enter the date the payment was claimed (YYYY-MM-DD):");
        String dateClaimed = s.nextLine();

        System.out.println(paymentId);
        People.claimPayment(paymentId, dateClaimed);
    }

    public void handleListUnclaimedPayments() {
        System.out.println("Enter the start date (YYYY-MM-DD):");
        String startDate = s.nextLine();
        System.out.println("Enter the end date (YYYY-MM-DD):");
        String endDate = s.nextLine();

        People.listUnclaimedPayments(startDate, endDate);
    }
}
