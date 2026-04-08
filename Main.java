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
                //////////////////////
                // People Operations
                //////////////////////
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

                ///////////////////////////
                // Distributor Operations
                ///////////////////////////
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

                case "findEditionsByTopic" : {
                    PublicationUI.handleFindEditionsByTopic(s);
                    break;               
                }
                case "findArticlesByTopic" : {
                    PublicationUI.handleFindArticlesByTopic(s);
                    break;               
                }
                case "findEditionsByDateRange" : {
                    PublicationUI.handleFindEditionsByDateRange(s);
                    break;               
                }
                case "findArticlesByDateRange" : {
                    PublicationUI.handleFindArticlesByDateRange(s);
                    break;               
                }
                case "findEditionsByAuthor" : {
                    PublicationUI.handleFindEditionsByAuthor(s);
                    break;               
                }
                case "findArticlesByAuthor" : {
                    PublicationUI.handleFindArticlesByAuthor(s);
                    break;               
                }
                case "compareIssueArticles" : {
                    PublicationUI.handleCompareIssueArticles(s);
                    break;               
                }


                //////////////////////
                // Report Operations
                //////////////////////
                // Count total number of distributors
                case "countDistributors": {
                    Reports.countDistributors();
                }
                // Count total orders by distributor
                case "countOrdersByDistributor": {
                    Reports.countOrdersByDistributor();
                }
                // Count total editions by distributor
                case "countEditionsByDistributor": {
                    Reports.countEditionsByDistributor();
                }
                // Count total issues by distributor
                case "countIssuesByDistributor": {
                    Reports.countIssuesByDistributor();
                }
                // Count total cost per issue per distributor
                case "costPerIssuePerDistributor": {
                    Reports.totalCostPerIssuesPerDistributor();
                }
                // Count total cost per edition per distributor
                case "costPerEditionPerDistributor": {
                    Reports.totalCostPerEditionPerDistributor();
                }
                // Count publications per distributor per week
                case "countPublicationsPerDistributorPerWeek": {
                    Reports.countPublicationsPerDistributorPerWeek();
                }
                // Count publications per distributor per month
                case "countPublicationsPerDistributorPerMonth": {
                    Reports.countPublicationsPerDistributorPerMonth();
                }
                // Total cost per distributor per week
                case "totalCostPerDistributorPerWeek": {
                    Reports.totalCostPerDistributorPerWeek();
                }
                // Total cost per distributor per month
                case "totalCostPerDistributorPerMonth": {
                    Reports.totalCostPerDistributorPerMonth();
                }
                // Total revenue per city
                case "totalRevenuePerCity": {
                    Reports.totalRevenuePerCity();
                }
                // Total revenue per distributor
                case "totalRevenuePerDistributor": {
                    Reports.totalRevenuePerCity();
                }
                // Calculate total expenses
                case "totalExpenses": {
                    Reports.totalExpenses();
                }
                
                // Help Case
                case "help": {
                    // People Operations
                    System.out.println("-----People Operations-----");
                    System.out.println("assignEditorToPublication");
                    System.out.println("removeEditorFromPublication");
                    System.out.println("enterPayment");
                    System.out.println("claimPayment");
                    System.out.println("listUnclaimedPayments");
                    System.out.println("");

                    // Distributor Operations
                    System.out.println("-----Distributor Operations-----");
                    System.out.println("addDistributor");
                    System.out.println("updateDistributor");
                    System.out.println("deleteDistributor");
                    System.out.println("inputOrder");
                    System.out.println("billDistributor");
                    System.out.println("receivePayment");
                    System.out.println("identifyMismatchedDistributors");
                    System.out.println("listDistributors");
                    System.out.println("");

                    // Report Operations
                    System.out.println("-----Report Operations-----");
                    System.out.println("countDistributors");
                    System.out.println("countOrdersByDistributor");
                    System.out.println("countIssuesByDistributor");
                    System.out.println("costPerIssuePerDistributor");
                    System.out.println("costPerEditionPerDistributor");
                    System.out.println("countPublicationsPerDistributorPerWeek");
                    System.out.println("countPublicationsPerDistributorPerMonth");
                    System.out.println("totalCostPerDistributorPerWeek");
                    System.out.println("totalCostPerDistributorPerMonth");
                    System.out.println("totalRevenuePerCity");
                    System.out.println("totalRevenuePerDistributor");
                    System.out.println("totalExpenses");
                    System.out.println("");
                }

                // Exit Case
                case "exit":
                    running = false;
                    break;

                // Invalid Operation
                default:
                    System.out.println("Invalid operation");
                    break;
            }


        }

        s.close();
        DBManager.close();
    }
}
