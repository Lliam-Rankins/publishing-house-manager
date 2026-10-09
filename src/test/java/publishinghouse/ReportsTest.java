package publishinghouse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ReportsTest {

    private Distributor distributor;

    @BeforeEach
    void setUp() throws SQLException {
        DBManager.initialize(true);
        distributor = new Distributor(DBManager.connection);

        // 1. Seed Distributors
        distributor.addDistributor(1, 0.0f, "Contact A", "111-222-3333", "Bookstore", "Store A", "10 Main", "Raleigh", "NC");
        distributor.addDistributor(2, 0.0f, "Contact B", "444-555-6666", "Library", "Store B", "20 Park", "Durham", "NC");

        // 2. Seed Book Edition & Issue
        Publication.addEditionPublication(10, "Java Core", "Book", 9780000000001L, 1, "Ed 1", null, null, 50.0);
        Publication.addPublication(20, "Tech Weekly", "Magazine", "Weekly");
        Publication.addIssue(20, "Issue 101", java.sql.Date.valueOf("2024-03-01"), 10.0);

        // 3. Seed Orders
        // Distributor 1 orders 5 copies of book edition (price 50.0 -> revenue 250.0, shipping 15.0)
        distributor.inputOrderISBN(1, 101, 9780000000001L, "2024-04-01", 15.0f, "2024-03-05", "Delivered", "Unpaid", 5);

        // Distributor 1 orders 10 copies of issue (price 10.0 -> revenue 100.0, shipping 5.0)
        distributor.inputOrderIssue(1, 102, 20, "Issue 101", "2024-04-01", 5.0f, "2024-03-06", "Delivered", "Unpaid", 10);

        // 4. Seed Person & Payments (expenses)
        DBManager.executeUpdate("INSERT INTO Person VALUES (1, 'Alice Author');");
        People.enterPayment(201, 500.0f, java.sql.Date.valueOf("2024-03-01"), "book authorship", null, 1);
        People.enterPayment(202, 250.0f, java.sql.Date.valueOf("2024-03-15"), "article authorship", null, 1);
    }

    @Test
    @DisplayName("countDistributors: calculates distributor count")
    void testCountDistributors() {
        assertTrue(Reports.countDistributors(), "countDistributors query must execute successfully");
    }

    @Test
    @DisplayName("countOrdersByDistributor: groups orders placed by each distributor")
    void testCountOrdersByDistributor() {
        assertTrue(Reports.countOrdersByDistributor());
    }

    @Test
    @DisplayName("countEditionsByDistributor and countIssuesByDistributor: counts publication types per distributor")
    void testCountPublicationsByType() {
        assertTrue(Reports.countEditionsByDistributor());
        assertTrue(Reports.countIssuesByDistributor());
    }

    @Test
    @DisplayName("totalCostPerIssuesPerDistributor and totalCostPerEditionPerDistributor: calculates costs per item")
    void testCostPerItemType() {
        assertTrue(Reports.totalCostPerIssuesPerDistributor());
        assertTrue(Reports.totalCostPerEditionPerDistributor());
    }

    @Test
    @DisplayName("Periodic publication counts: per week and per month")
    void testPeriodicPublicationCounts() {
        assertTrue(Reports.countPublicationsPerDistributorPerWeek());
        assertTrue(Reports.countPublicationsPerDistributorPerMonth());
    }

    @Test
    @DisplayName("Periodic cost calculations: per week and per month")
    void testPeriodicCostReports() {
        assertTrue(Reports.totalCostPerDistributorPerWeek());
        assertTrue(Reports.totalCostPerDistributorPerMonth());
    }

    @Test
    @DisplayName("Revenue Reports: by city, by distributor, and aggregate total revenue")
    void testRevenueReports() {
        assertTrue(Reports.totalRevenuePerCity());
        assertTrue(Reports.totalRevenuePerDistributor());
        assertTrue(Reports.totalRevenue());
    }

    @Test
    @DisplayName("Expense and Payroll Reports: total expenses, payments per month, and by work type")
    void testExpenseAndPayrollReports() {
        assertTrue(Reports.totalExpenses());
        assertTrue(Reports.totalPaymentsPerMonth());
        assertTrue(Reports.totalPaymentsPerWorkType());
    }
}
