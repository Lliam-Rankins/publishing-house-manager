import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static Distributor distributor = null;
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        

        DBManager.initialize();

        boolean running = true;

        while (running) {
            System.out.println("Enter an operation, or 'exit' to exit: ");
            String operation = s.nextLine();
            switch (operation) {
                case "assignEditorToPublication":
                    System.out.println("Enter the editor id:");
                    int editorId = s.nextInt();
                    System.out.println("Enter the publication id:");
                    int pubId = s.nextInt();
                    System.out.println("Enter true if the editor was invited, false otherwise");
                    boolean invited = s.nextBoolean();
                    People.assignEditorToPublication(editorId, pubId, invited);
                    break;
                case "removeEditorFromPublication":
                    System.out.println("Enter the editor id:");
                    editorId = s.nextInt();
                    System.out.println("Enter the publication id:");
                    pubId = s.nextInt();
                    People.removeEditorFromPublication(editorId, pubId);
                    break;
                case "exit":
                    running = false;
                    break;
                
                case "enterPublication":
                    enterPublication();
                    break;
                
                case "addDistributor":{
                    DistributorUI.handleAddDistributor(s,distributor);
                    break;
                }

                case "updateDistributor":{
                    DistributorUI.handleUpdateDistributor(s, distributor);
                    break;
                }
                case "deleteDistributor":{
                    DistributorUI.handleDeleteDistributor(s, distributor);
                    break;
                }
                // Input order
                case "inputOrder":{
                    DistributorUI.handleInputOrder(s, distributor);
                    break;
                }
                //Bill distributor for an order. 
                case "billDistributor":{
                    DistributorUI.handleBillDistributor(s, distributor);
                    break;
                }
                //Receive a payment and change the outstanding balance of a distributor. 
                case "receivePayment":{
                    //thinking about this, maybe we need to first change the status of an order to payed or something
                    DistributorUI.handleReceivePayment(s, distributor);
                    break;
                }
                //Identify distributors whose total billed amount does not match the sum of their recorded payments. 
                case "identifyMismatchedDistributors":{
                    DistributorUI.handleIdentifyMismatchedDistributors(s, distributor);
                    break;
                }
                //List all distributors of a specific type located in a given city. 
                case "listDistributors":{
                    DistributorUI.handleListDistributors(s, distributor);
                    break;
                }
                default:
                    break;
            }
        }

        s.close();
        DBManager.close();
    }
}
