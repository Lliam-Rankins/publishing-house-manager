package publishinghouse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DistributorTest {

    private Distributor distributor;

    @BeforeEach
    void setUp() {
        DBManager.initialize(true);
        distributor = new Distributor(DBManager.connection);
    }

    @Test
    @DisplayName("addDistributor: successfully inserts a valid distributor")
    void testAddDistributorSuccess() throws SQLException {
        boolean added = distributor.addDistributor(
                1, 150.0f, "Alice Smith", "919-555-0100", "Wholesale",
                "Apex Books", "100 Main St", "Raleigh", "NC"
        );
        assertTrue(added, "Distributor should be successfully inserted");

        ResultSet rs = DBManager.executeQuery("SELECT name, state FROM Distributor WHERE distribID = 1;");
        assertNotNull(rs);
        assertTrue(rs.next(), "Inserted distributor record must exist");
        assertEquals("Apex Books", rs.getString("name"));
        assertEquals("NC", rs.getString("state"));
        rs.close();
    }

    @Test
    @DisplayName("addDistributor [Edge Case]: rejects state code longer than CHAR(2)")
    void testAddDistributorStateLengthExceeded() throws SQLException {
        boolean added = distributor.addDistributor(
                2, 100.0f, "Bob Jones", "919-555-0101", "Retail",
                "Bob Books", "200 Oak St", "Raleigh", "NORTH_CAROLINA"
        );
        assertFalse(added, "Insert must fail when state exceeds 2 characters");
    }

    @Test
    @DisplayName("addDistributor [Edge Case]: rejects duplicate primary key distribID")
    void testAddDistributorDuplicateId() throws SQLException {
        boolean first = distributor.addDistributor(
                3, 50.0f, "Carol", null, null, "Carol Books", "300 Pine St", "Durham", "NC"
        );
        assertTrue(first);

        boolean duplicate = distributor.addDistributor(
                3, 75.0f, "Carol Again", null, null, "Carol Other", "400 Pine St", "Durham", "NC"
        );
        assertFalse(duplicate, "Insert must fail on duplicate primary key distribID");
    }

    @Test
    @DisplayName("updateDistributor: updates an existing distributor field")
    void testUpdateDistributorSuccess() throws SQLException {
        distributor.addDistributor(
                4, 0.0f, "Dave", "919-555-0102", "Library", "Dave Lib", "500 Elm St", "Cary", "NC"
        );

        distributor.updateDistributor("city", "Apex", 4);

        ResultSet rs = DBManager.executeQuery("SELECT city FROM Distributor WHERE distribID = 4;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("Apex", rs.getString("city"));
        rs.close();
    }

    @Test
    @DisplayName("deleteDistributor: removes distributor from database")
    void testDeleteDistributorSuccess() throws SQLException {
        distributor.addDistributor(
                5, 0.0f, "Eve", null, null, "Eve Books", "600 Cedar St", "Raleigh", "NC"
        );

        distributor.deleteDistributor(5);

        ResultSet rs = DBManager.executeQuery("SELECT * FROM Distributor WHERE distribID = 5;");
        assertNotNull(rs);
        assertFalse(rs.next(), "Deleted distributor should not exist in database");
        rs.close();
    }

    @Test
    @DisplayName("inputOrderISBN: creates order linked to edition and distributor")
    void testInputOrderISBNSuccess() throws SQLException {
        distributor.addDistributor(
                10, 0.0f, "Frank", null, null, "Frank Books", "700 Maple St", "Raleigh", "NC"
        );
        Publication.addEditionPublication(
                100, "Java Guide", "Book", 9780134685991L, 1, "First Edition",
                java.sql.Date.valueOf("2024-01-01"), java.sql.Date.valueOf("2024-02-01"), 49.99
        );

        boolean orderCreated = distributor.inputOrderISBN(
                10, 500, 9780134685991L, "2024-05-01", 15.0f,
                "2024-04-01", "Shipped", "Unpaid", 20
        );
        assertTrue(orderCreated, "Order for ISBN edition must succeed");

        ResultSet rs = DBManager.executeQuery("SELECT copies, deliveryStatus FROM `Order` WHERE oID = 500;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals(20, rs.getInt("copies"));
        assertEquals("Shipped", rs.getString("deliveryStatus"));
        rs.close();
    }

    @Test
    @DisplayName("inputOrderISBN [Edge Case]: rejects negative copies or negative shipping cost")
    void testInputOrderISBNInvalidValues() throws SQLException {
        distributor.addDistributor(
                11, 0.0f, "Grace", null, null, "Grace Books", "800 Ash St", "Raleigh", "NC"
        );

        boolean zeroCopies = distributor.inputOrderISBN(
                11, 501, 9780134685991L, "2024-05-01", 10.0f,
                "2024-04-01", "Pending", "Unpaid", 0
        );
        assertFalse(zeroCopies, "Order with 0 copies must be rejected");

        boolean negativeShipping = distributor.inputOrderISBN(
                11, 502, 9780134685991L, "2024-05-01", -5.0f,
                "2024-04-01", "Pending", "Unpaid", 10
        );
        assertFalse(negativeShipping, "Order with negative shipping cost must be rejected");
    }

    @Test
    @DisplayName("inputOrderIssue: creates order linked to publication issue")
    void testInputOrderIssueSuccess() throws SQLException {
        distributor.addDistributor(
                12, 0.0f, "Heidi", null, null, "Heidi Books", "900 Walnut St", "Raleigh", "NC"
        );
        Publication.addPublication(200, "Science Monthly", "Magazine", "Monthly");
        Publication.addIssue(200, "Vol 1 No 1", java.sql.Date.valueOf("2024-03-01"), 9.99);

        boolean orderCreated = distributor.inputOrderIssue(
                12, 600, 200, "Vol 1 No 1", "2024-04-01", 5.0f,
                "2024-03-15", "Delivered", "Unpaid", 50
        );
        assertTrue(orderCreated, "Order for publication issue must succeed");

        ResultSet rs = DBManager.executeQuery("SELECT * FROM ContainsIssue WHERE oID = 600;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("Vol 1 No 1", rs.getString("issueTitle"));
        rs.close();
    }

    @Test
    @DisplayName("billDistributor: updates order paymentStatus to 'Billed'")
    void testBillDistributorSuccess() throws SQLException {
        distributor.addDistributor(
                13, 0.0f, "Ivan", null, null, "Ivan Books", "101 First St", "Raleigh", "NC"
        );
        Publication.addEditionPublication(
                300, "Algorithms", "Book", 9780262033848L, 3, "Third Edition",
                java.sql.Date.valueOf("2023-01-01"), java.sql.Date.valueOf("2023-06-01"), 89.99
        );
        distributor.inputOrderISBN(
                13, 700, 9780262033848L, "2024-05-01", 10.0f,
                "2024-04-01", "Shipped", "Unpaid", 5
        );

        boolean billed = distributor.billDistributor(700, 13);
        assertTrue(billed, "Unbilled order must be billable");

        ResultSet rs = DBManager.executeQuery("SELECT paymentStatus FROM `Order` WHERE oID = 700;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("Billed", rs.getString("paymentStatus"));
        rs.close();
    }

    @Test
    @DisplayName("billDistributor [Edge Case]: cannot bill an order that belongs to another distributor")
    void testBillDistributorWrongDistributor() throws SQLException {
        distributor.addDistributor(14, 0.0f, "Judy", null, null, "Judy Books", "102 Second St", "Raleigh", "NC");
        distributor.addDistributor(15, 0.0f, "Ken", null, null, "Ken Books", "103 Third St", "Raleigh", "NC");
        Publication.addEditionPublication(
                400, "Clean Code", "Book", 9780132350884L, 1, "First Edition",
                java.sql.Date.valueOf("2020-01-01"), java.sql.Date.valueOf("2020-02-01"), 40.0
        );
        distributor.inputOrderISBN(14, 800, 9780132350884L, "2024-05-01", 5.0f, "2024-04-01", "Pending", "Unpaid", 10);

        boolean billedByKen = distributor.billDistributor(800, 15);
        assertFalse(billedByKen, "Billing must fail when order does not belong to the distributor");
    }

    @Test
    @DisplayName("receivePayment: changes order paymentStatus to 'Paid'")
    void testReceivePaymentSuccess() throws SQLException {
        distributor.addDistributor(16, 500.0f, "Leo", null, null, "Leo Books", "104 Fourth St", "Raleigh", "NC");
        Publication.addEditionPublication(
                500, "Design Patterns", "Book", 9780201633610L, 1, "First Edition",
                java.sql.Date.valueOf("2021-01-01"), java.sql.Date.valueOf("2021-03-01"), 55.0
        );
        distributor.inputOrderISBN(16, 900, 9780201633610L, "2024-05-01", 10.0f, "2024-04-01", "Shipped", "Unpaid", 2);

        boolean paid = distributor.receivePayment(900, 16);
        assertTrue(paid, "Payment should be accepted for valid order");

        ResultSet rs = DBManager.executeQuery("SELECT paymentStatus FROM `Order` WHERE oID = 900;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("Paid", rs.getString("paymentStatus"));
        rs.close();
    }

    @Test
    @DisplayName("receivePayment [Edge Case]: cannot pay an order that is already 'Paid'")
    void testReceivePaymentAlreadyPaid() throws SQLException {
        distributor.addDistributor(17, 200.0f, "Mona", null, null, "Mona Books", "105 Fifth St", "Raleigh", "NC");
        Publication.addEditionPublication(
                600, "Refactoring", "Book", 9780201485677L, 2, "Second Edition",
                java.sql.Date.valueOf("2022-01-01"), java.sql.Date.valueOf("2022-04-01"), 60.0
        );
        distributor.inputOrderISBN(17, 950, 9780201485677L, "2024-05-01", 10.0f, "2024-04-01", "Shipped", "Unpaid", 1);
        distributor.receivePayment(950, 17);

        boolean payAgain = distributor.receivePayment(950, 17);
        assertFalse(payAgain, "Paying an already paid order must be rejected");
    }

    @Test
    @DisplayName("identifyMismatchedDistributors: executes mismatch query without error")
    void testIdentifyMismatchedDistributors() {
        assertDoesNotThrow(() -> distributor.identifyMismatchedDistributors());
    }

    @Test
    @DisplayName("listDistributors: lists distributors by category and city")
    void testListDistributors() throws SQLException {
        distributor.addDistributor(18, 0.0f, "Nina", null, "Retail", "Nina Books", "106 Sixth St", "Durham", "NC");
        assertDoesNotThrow(() -> distributor.listDistributors("Retail", "Durham"));
    }
}
