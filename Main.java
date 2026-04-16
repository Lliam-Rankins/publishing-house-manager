import java.util.Scanner;

public class Main {
    private static Distributor distributor = null;

    public static void main(String[] args) {
        try {
            Scanner s = new Scanner(System.in);
            boolean reset = args.length > 1 && args[0].equals("reset");
            DBManager.initialize(reset);

            boolean running = true;

            PeopleUI peopleUI = new PeopleUI(s);
            Distributor distributor = new Distributor();

            while (running) {
                System.out.println("Enter an operation, or 'exit' to exit: ");
                String operation = s.nextLine();
                switch (operation) {
                    /////////////////
                    // People Operations
                    /////////////////
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
                    case "viewPublicationsByEditor":
                        peopleUI.handleViewPublicationsByEditor();
                        break;

                    //////////////////////
                    // Distributor Operations
                    //////////////////////
                    case "addDistributor": {
                        System.out.print("HELLO");
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
                        // thinking about this, maybe we need to first change the status of an order
                        // to
                        // payed or something
                        DistributorUI.handleReceivePayment(s, distributor);
                        break;
                    }
                    // Identify distributors whose total billed amount does not match the sum of
                    // their
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

                    ////////////////////
                    // Publication Operations
                    /////////////////////
                    case "addPublication": {
                        PublicationUI.handleAddPublication(s);
                        break;
                    }
                    case "updatePublication": {
                        PublicationUI.handleUpdatePublication(s);
                        break;
                    }
                    case "removePublication": {
                        PublicationUI.handleRemovePublication(s);
                        break;
                    }
                    case "addEditionToPub": {
                        PublicationUI.handleAddBookEditionToExistingPub(s);
                        break;
                    }
                    case "updateEdition": {
                        PublicationUI.handleUpdateBookEdition(s);
                        break;
                    }
                    case "removeEditionAndPub": {
                        PublicationUI.handleRemoveBookEditionAndPublication(s);
                        break;
                    }
                    case "removeEditionNotPub": {
                        PublicationUI.handleRemoveBookEditionNotPublication(s);
                        break;
                    }
                    case "addIssue": {
                        PublicationUI.handleAddIssue(s);
                        break;
                    }
                    case "editIssue": {
                        PublicationUI.handleEditIssue(s);
                        break;
                    }
                    case "removeIssue": {
                        PublicationUI.handleRemoveIssue(s);
                        break;
                    }
                    case "addChapter": {
                        PublicationUI.handleAddChapterTOC(s);
                        break;
                    }
                    case "editChapter": {
                        PublicationUI.handleEditChapter(s);
                        break;
                    }
                    case "editChapterAuthor": {
                        PublicationUI.handleUpdateChapterAuthor(s);
                        break;
                    }
                    case "removeChapterAuthor": {
                        PublicationUI.handleRemoveChapterAuthor(s);
                        break;
                    }
                    case "removeChapter": {
                        PublicationUI.handleRemoveChapterTOC(s);
                        break;
                    }
                    case "addArticle": {
                        PublicationUI.handleAddArticleTOC(s);
                        break;
                    }
                    case "editArticle": {
                        PublicationUI.handleEditArticle(s);
                        break;
                    }
                    case "editArticleAuthor": {
                        PublicationUI.handleUpdateArticleAuthor(s);
                        break;
                    }
                    case "removeArticleAuthor": {
                        PublicationUI.handleRemoveArticleAuthor(s);
                        break;
                    }
                    case "removeArticle": {
                        PublicationUI.handleRemoveArticleTOC(s);
                        break;
                    }

                    case "findEditionsByTopic": {
                        PublicationUI.handleFindEditionsByTopic(s);
                        break;
                    }
                    case "findArticlesByTopic": {
                        PublicationUI.handleFindArticlesByTopic(s);
                        break;
                    }
                    case "findEditionsByDateRange": {
                        PublicationUI.handleFindEditionsByDateRange(s);
                        break;
                    }
                    case "findArticlesByDateRange": {
                        PublicationUI.handleFindArticlesByDateRange(s);
                        break;
                    }
                    case "findEditionsByAuthor": {
                        PublicationUI.handleFindEditionsByAuthor(s);
                        break;
                    }
                    case "findArticlesByAuthor": {
                        PublicationUI.handleFindArticlesByAuthor(s);
                        break;
                    }
                    case "compareIssueArticles": {
                        PublicationUI.handleCompareIssueArticles(s);
                        break;
                    }

                    //////////////////
                    // Report Operations
                    //////////////////
                    // Count total number of distributors
                    case "countDistributors": {
                        Reports.countDistributors();
                        break;
                    }
                    // Count total orders by distributor
                    case "countOrdersByDistributor": {
                        Reports.countOrdersByDistributor();
                        break;
                    }
                    // Count total editions by distributor
                    case "countEditionsByDistributor": {
                        Reports.countEditionsByDistributor();
                        break;
                    }
                    // Count total issues by distributor
                    case "countIssuesByDistributor": {
                        Reports.countIssuesByDistributor();
                        break;
                    }
                    // Count total cost per issue per distributor
                    case "costPerIssuePerDistributor": {
                        Reports.totalCostPerIssuesPerDistributor();
                        break;
                    }
                    // Count total cost per edition per distributor
                    case "costPerEditionPerDistributor": {
                        Reports.totalCostPerEditionPerDistributor();
                        break;
                    }
                    // Count publications per distributor per week
                    case "countPublicationsPerDistributorPerWeek": {
                        Reports.countPublicationsPerDistributorPerWeek();
                        break;
                    }
                    // Count publications per distributor per month
                    case "countPublicationsPerDistributorPerMonth": {
                        Reports.countPublicationsPerDistributorPerMonth();
                        break;
                    }
                    // Total cost per distributor per week
                    case "totalCostPerDistributorPerWeek": {
                        Reports.totalCostPerDistributorPerWeek();
                        break;
                    }
                    // Total cost per distributor per month
                    case "totalCostPerDistributorPerMonth": {
                        Reports.totalCostPerDistributorPerMonth();
                        break;
                    }
                    // Total revenue per city
                    case "totalRevenuePerCity": {
                        Reports.totalRevenuePerCity();
                        break;
                    }
                    // Total revenue per distributor
                    case "totalRevenuePerDistributor": {
                        Reports.totalRevenuePerDistributor();
                        break;
                    }
                    // Total revenue
                    case "totalRevenue": {
                        Reports.totalRevenue();
                        break;
                    }
                    // Calculate total expenses
                    case "totalExpenses": {
                        Reports.totalExpenses();
                        break;
                    }
                    case "totalPaymentsPerMonth": {
                        Reports.totalPaymentsPerMonth();
                        break;
                    }
                    case "totalPaymentsPerWorkType": {
                        Reports.totalPaymentsPerWorkType();
                        break;
                    }

                    // Help Case
                    case "help": {
                        // Publication Operations
                        System.out.println("-----Publication Operations-----");
                        System.out.println("addPublication");
                        System.out.println("updatePublication");
                        System.out.println("removePublication");
                        System.out.println("addEditionToPub");
                        System.out.println("updateEdition");
                        System.out.println("removeEditionAndPub");
                        System.out.println("removeEditionNotPub");
                        System.out.println("addIssue");
                        System.out.println("editIssue");
                        System.out.println("removeIssue");
                        System.out.println("addChapter");
                        System.out.println("editChapter");
                        System.out.println("editChapterAuthor");
                        System.out.println("removeChapterAuthor");
                        System.out.println("removeChapter");
                        System.out.println("addArticle");
                        System.out.println("editArticle");
                        System.out.println("editArticleAuthor");
                        System.out.println("removeArticleAuthor");
                        System.out.println("removeArticle");
                        System.out.println("findEditionsByTopic");
                        System.out.println("findArticlesByTopic");
                        System.out.println("findEditionsByDateRange");
                        System.out.println("findArticlesByDateRange");
                        System.out.println("findEditionsByAuthor");
                        System.out.println("findArticlesByAuthor");
                        System.out.println("compareIssueArticles");
                        System.out.println("");
                        // People Operations
                        System.out.println("-----People Operations-----");
                        System.out.println("assignEditorToPublication");
                        System.out.println("removeEditorFromPublication");
                        System.out.println("enterPayment");
                        System.out.println("claimPayment");
                        System.out.println("listUnclaimedPayments");
                        System.out.println("viewPublicationsByEditor");
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
                        System.out.println("countEditionsByDistributor");
                        System.out.println("costPerIssuePerDistributor");
                        System.out.println("costPerEditionPerDistributor");
                        System.out.println("countPublicationsPerDistributorPerWeek");
                        System.out.println("countPublicationsPerDistributorPerMonth");
                        System.out.println("totalCostPerDistributorPerWeek");
                        System.out.println("totalCostPerDistributorPerMonth");
                        System.out.println("totalRevenuePerCity");
                        System.out.println("totalRevenuePerDistributor");
                        System.out.println("totalExpenses");
                        System.out.println("totalRevenue");
                        System.out.println("");
                        break;
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
        } catch (Exception e) {
            DBManager.close();
        }
    }
}
