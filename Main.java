import java.util.Scanner;

public class Main {
    private static Distributor distributor = null;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        boolean reset = args.length > 1 && args[0].equals("reset");
        DBManager.initialize(reset);

        boolean running = true;

        PeopleUI peopleUI = new PeopleUI(s);

        while (running) {
            System.out.println("Enter an operation, or 'exit' to exit: ");
            String operation = s.nextLine();
            switch (operation) {
                case "assignEditorToPublication":
                    peopleUI.handleAssignEditorToPublication();
                    break;
                case "removeEditorFromPublication":
                    peopleUI.handleRemoveEditorFromPublication();
                    break;
                case "enterPayment":
                    peopleUI.handleEnterPayment();
                    break;
                case "claimPayment":
                    peopleUI.handleClaimPayment();
                    break;
                case "listUnclaimedPayments":
                    peopleUI.handleListUnclaimedPayments();
                    break;
                case "exit":
                    running = false;
                    break;
                case "addDistributor": {
                    DistributorUI.handleAddDistributor(s, distributor);
                    break;
                }

                case "updateDistributor": {
                    DistributorUI.handleUpdateDistributor(s, distributor);
                    break;
                }
                case "deleteDistributor": {
                    DistributorUI.handleDeleteDistributor(s, distributor);
                    break;
                }
                // Input order
                case "inputOrder": {
                    DistributorUI.handleInputOrder(s, distributor);
                    break;
                }
                // Bill distributor for an order.
                case "billDistributor": {
                    DistributorUI.handleBillDistributor(s, distributor);
                    break;
                }
                // Receive a payment and change the outstanding balance of a distributor.
                case "receivePayment": {
                    // thinking about this, maybe we need to first change the status of an order to
                    // payed or something
                    DistributorUI.handleReceivePayment(s, distributor);
                    break;
                }
                // Identify distributors whose total billed amount does not match the sum of their
                // recorded payments.
                case "identifyMismatchedDistributors": {
                    DistributorUI.handleIdentifyMismatchedDistributors(s, distributor);
                    break;
                }
                // List all distributors of a specific type located in a given city.
                case "listDistributors": {
                    DistributorUI.handleListDistributors(s, distributor);
                    break;
                }
                case "addPublication" : {
                    PublicationUI.handleAddPublication(s);
                    break;
                }
                case "updatePublication" : {
                    PublicationUI.handleUpdatePublication(s);
                    break;
                }
                case "removePublication" : {
                    PublicationUI.handleRemovePublication(s);
                    break;
                }
                case "addBookEdition" : {
                    PublicationUI.handleAddBookEdition(s);
                    break;
                }
                case "updateBookEdition" : {
                    PublicationUI.handleUpdateBookEdition(s);
                    break;
                }
                case "removeBookEdition" : {
                    PublicationUI.handleRemoveBookEdition(s);
                    break;
                }
                case "addIssue" : {
                    PublicationUI.handleAddIssue(s);
                    break;
                }
                case "editIssue" : {
                    PublicationUI.handleEditIssue(s);
                    break;
                }
                case "removeIssue" : {
                    PublicationUI.handleRemoveIssue(s);
                    break;
                }
                case "addChapter" : {
                    PublicationUI.handleAddChapterTOC(s);
                    break;               
                }
                case "editChapter" : {
                    PublicationUI.handleEditChapter(s);
                    break;               
                }
                case "removeChapter" : {
                    PublicationUI.handleRemoveChapterTOC(s);
                    break;               
                }
                case "addArticle" : {
                    PublicationUI.handleAddArticleTOC(s);
                    break;               
                }
                case "editArticle" : {
                    PublicationUI.handleEditArticle(s);
                    break;               
                }
                case "removeArticle" : {
                    PublicationUI.handleRemoveArticleTOC(s);
                    break;               
                }
                default:
                    System.out.println("Invalid operation");
                    break;
            }


        }

        s.close();
        DBManager.close();
    }
}
