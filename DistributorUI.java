
import java.sql.SQLException;
import java.util.Scanner;

public class DistributorUI {
    // Colors for printing
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String RESET = "\033[0m";

    public static void handleAddDistributor(Scanner s, Distributor distributor) {
        System.out.println("Enter Distributor ID: ");
        int distribID = Integer.parseInt(s.nextLine());
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
            distributor.addDistributor(distribID, balance, contactName, phoneNumber, category, name,
                    street, city, state);
            System.out.println(GREEN + "Distributor added successfully." + RESET);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void handleUpdateDistributor(Scanner s, Distributor distributor) {
        System.out
                .println("Which field do you want to update? (contactName, category, city, ...): ");
        String field = s.nextLine();
        System.out.println("Enter new value: ");
        String value = s.nextLine();
        System.out.println("Enter distribID:");
        int distribID = Integer.parseInt(s.nextLine());
        try {
            distributor.updateDistributor(field, value, distribID);
            System.out.println(GREEN + "Distributor updated successfully." + RESET);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void handleDeleteDistributor(Scanner s, Distributor distributor) {
        System.out.println("Enter the ID of the distributor you want to delete: ");
        int distribID = Integer.parseInt(s.nextLine());

        try {
            distributor.deleteDistributor(distribID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void handleInputOrder(Scanner s, Distributor distributor) {

        System.out.println("Is this order for bookEdition or issue?: ");
        String distribID = s.nextLine();
        switch (distribID) {
            case "bookEdition": {
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
                    distributor.inputOrderISBN(oID, ISBN, dueBy, shippingCost, datePlaced,
                            deliveryStatus, paymentStatus, copies);
                    System.out.println("Order added successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "issue": {
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
                    distributor.inputOrderIssue(oID, pubID, issueTitle, dueBy, shippingCost,
                            datePlaced, deliveryStatus, paymentStatus, copies);
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

    public static void handleBillDistributor(Scanner s, Distributor distributor) {
        System.out.println("Enter Distributor ID: ");
        int distribID = Integer.parseInt(s.nextLine());
        System.out.println("Enter the order ID;");
        int oID = Integer.parseInt(s.nextLine());
        try {
            distributor.billDistributor(oID, distribID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void handleReceivePayment(Scanner s, Distributor distributor) {
        System.out.print("Enter Distributor ID: ");
        int distribID = Integer.parseInt(s.nextLine());

        boolean hasOrders = Distributor.listUnpaidOrders(distribID);

        if (!hasOrders) {
            System.out.println("No unpaid orders found for this distributor.");
            return;
        }

        System.out.print("Enter the ID of the order you want to pay: ");
        int oID = Integer.parseInt(s.nextLine());

        boolean success = distributor.receivePayment(oID, distribID);

        if (success) {
            System.out.println("Payment received successfully!");
        } else {
            System.out.println("Payment failed. Please check the order ID.");
        }
    }

    public static void handleIdentifyMismatchedDistributors(Scanner s, Distributor distributor) {
        try {
            distributor.identifyMismatchedDistributors();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void handleListDistributors(Scanner s, Distributor distributor) {
        System.out.println("Category: ");
        String category = s.nextLine();
        System.out.println("City: ");
        String city = s.nextLine();
        try {
            distributor.listDistributors(category, city);
            System.out.println("These are all the distributors in that city");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
