import java.sql.SQLException;
import java.util.Scanner;

public class Main {
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
                case "addDistributor":{
                    System.out.println("Enter Distributor ID: ");
                    String distribID = s.nextLine();
                    System.out.println("Enter balance: ");
                    float balance = Float.parseFloat(s.nextLine());
                    System.out.println("Enter contact name: ");
                    String contactName = s.nextLine();
                    System.out.println("Enter phone number: ");
                    String phoneNumber = s.nextLine();
                    System.out.println("Enter category: ");
                    String category = s.nextLine();
                    System.out.println("Enter name: ");
                    String name = s.nextLine();
                    System.out.println("Enter street: ");
                    String street = s.nextLine();
                    System.out.println("Enter city: ");
                    String city = s.nextLine();
                    System.out.println("Enter state: ");
                    String state = s.nextLine();
                    try {
                        DBManager.distributor.addDistributor(distribID, balance, contactName, phoneNumber, category, name, street, city, state);
                        System.out.println("Distributor added successfully.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case "updateDistributor":{
                    System.out.println("Which field do you want to update? (contactName, category, city, ...): ");
                    String field = s.nextLine();
                    System.out.println("Enter new value: ");
                    String value = s.nextLine();
                    System.out.println("Enter distribID:");
                    int distribID = Integer.parseInt(s.nextLine());
                    try {
                        DBManager.distributor.updateDistributor(field, value, distribID);
                        System.out.println("Distributor updated successfully.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "deleteDistributor":{
                    System.out.println("Enter the ID of the distributor you want to delete: ");
                    int distribID = Integer.parseInt(s.nextLine());
                    
                    try {
                        DBManager.distributor.deleteDistributor(distribID);
                        System.out.println("Distributor deleted successfully.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                // Input order
                case "inputOrder":{
                    System.out.println("Is this order for bookEdition or Issue?: ");
                    String distribID = s.nextLine();
                    switch (distribID) {
                        case "bookEdition":{
                            System.out.println("Enter order ID: ");
                            int oID = Integer.parseInt(s.nextLine());
                            System.out.println("Enter order ISBN: ");
                            long ISBN = Long.parseLong(s.nextLine());
                            System.out.println("Due by (YYYY-MM-DD, or Enter to skip): ");
                            String dueBy = s.nextLine();
                            System.out.println("Shipping cost (or Enter to skip): ");
                            String shippingInput = s.nextLine();
                            Float shippingCost = shippingInput.isEmpty() ? null : Float.parseFloat(shippingInput);
                            System.out.println("Date placed (YYYY-MM-DD): ");
                            String datePlaced = s.nextLine();
                            System.out.println("Delivery status: ");
                            String deliveryStatus = s.nextLine();
                            System.out.println("Payment status: ");
                            String paymentStatus = s.nextLine();
                            System.out.println("Copies: ");
                            int copies = Integer.parseInt(s.nextLine());
                            try {
                                DBManager.distributor.inputOrderISBN(oID, ISBN, dueBy, shippingCost, datePlaced, deliveryStatus, paymentStatus, copies);
                                System.out.println("Order added successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                            case "issue":{
                                System.out.println("Enter order ID: ");
                                int oID = Integer.parseInt(s.nextLine());
                                System.out.println("Enter order publication ID: ");
                                int pubID = Integer.parseInt(s.nextLine());
                                System.out.println("Enter issue Title: ");
                                String issueTitle = s.nextLine();
                                System.out.println("Due by (YYYY-MM-DD, or Enter to skip): ");
                                String dueBy = s.nextLine();
                                System.out.println("Shipping cost (or Enter to skip): ");
                                String shippingInput = s.nextLine();
                                Float shippingCost = shippingInput.isEmpty() ? null : Float.parseFloat(shippingInput);
                                System.out.println("Date placed (YYYY-MM-DD): ");
                                String datePlaced = s.nextLine();
                                System.out.println("Delivery status: ");
                                String deliveryStatus = s.nextLine();
                                System.out.println("Payment status: ");
                                String paymentStatus = s.nextLine();
                                System.out.println("Copies: ");
                                int copies = Integer.parseInt(s.nextLine());
                                try {
                                    DBManager.distributor.inputOrderIssue(oID, pubID, issueTitle, dueBy, shippingCost, datePlaced, deliveryStatus, paymentStatus, copies);
                                    System.out.println("Order added successfully.");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

                            default:
                            break;
                    }
                    
                }
                default:
                    break;
            }
        }

        s.close();
        DBManager.close();
    }
}
