package publishinghouse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class PeopleTest {

    @BeforeEach
    void setUp() {
        DBManager.initialize(true);
        // Seed base Person and Publication records
        DBManager.executeUpdate("INSERT INTO Person VALUES (1, 'Dr. Smith');");
        DBManager.executeUpdate("INSERT INTO Person VALUES (2, 'Prof. Johnson');");
        Publication.addPublication(10, "Journal of Science", "Journal", "Monthly");
    }

    @Test
    @DisplayName("assignEditorToPublication: successfully links editor to publication")
    void testAssignEditorSuccess() throws SQLException {
        boolean assigned = People.assignEditorToPublication(1, 10, true);
        assertTrue(assigned, "Assigning editor to publication should succeed");

        ResultSet rs = DBManager.executeQuery("SELECT invited FROM Edits WHERE pID = 1 AND pubID = 10;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertTrue(rs.getBoolean("invited"));
        rs.close();
    }

    @Test
    @DisplayName("assignEditorToPublication [Edge Case]: rejects duplicate editor assignment")
    void testAssignEditorDuplicate() {
        People.assignEditorToPublication(1, 10, true);
        boolean duplicate = People.assignEditorToPublication(1, 10, false);
        assertFalse(duplicate, "Duplicate (pID, pubID) in Edits should be rejected");
    }

    @Test
    @DisplayName("removeEditorFromPublication: unlinks editor from publication")
    void testRemoveEditorSuccess() throws SQLException {
        People.assignEditorToPublication(1, 10, true);

        boolean removed = People.removeEditorFromPublication(1, 10);
        assertTrue(removed, "Removing assigned editor should succeed");

        ResultSet rs = DBManager.executeQuery("SELECT * FROM Edits WHERE pID = 1 AND pubID = 10;");
        assertNotNull(rs);
        assertFalse(rs.next(), "Record must be deleted from Edits table");
        rs.close();
    }

    @Test
    @DisplayName("enterPayment: enters payment and creates link to person atomically")
    void testEnterPaymentSuccess() throws SQLException {
        boolean entered = People.enterPayment(
                101, 1200.50f, java.sql.Date.valueOf("2024-03-01"),
                "editorial work", null, 1
        );
        assertTrue(entered, "Payment record creation should succeed");

        ResultSet rsPay = DBManager.executeQuery("SELECT amount, workType FROM Payment WHERE paymentID = 101;");
        assertNotNull(rsPay);
        assertTrue(rsPay.next());
        assertEquals(1200.50f, rsPay.getFloat("amount"), 0.001);
        assertEquals("editorial work", rsPay.getString("workType"));
        rsPay.close();

        ResultSet rsRec = DBManager.executeQuery("SELECT pID FROM Receives WHERE paymentID = 101;");
        assertNotNull(rsRec);
        assertTrue(rsRec.next());
        assertEquals(1, rsRec.getInt("pID"));
        rsRec.close();
    }

    @Test
    @DisplayName("enterPayment [Edge Case]: rolls back payment if person does not exist")
    void testEnterPaymentRollbackOnMissingPerson() throws SQLException {
        // Person 999 does not exist in Person table
        boolean entered = People.enterPayment(
                102, 500.0f, java.sql.Date.valueOf("2024-03-01"),
                "article authorship", null, 999
        );
        assertFalse(entered, "Payment must fail due to foreign key constraint on Receives");

        // Verify transaction rollback: Payment table must not contain orphan record
        ResultSet rs = DBManager.executeQuery("SELECT * FROM Payment WHERE paymentID = 102;");
        assertNotNull(rs);
        assertFalse(rs.next(), "No orphan payment should exist after transaction rollback");
        rs.close();
    }

    @Test
    @DisplayName("claimPayment: updates dateClaimed on existing payment")
    void testClaimPaymentSuccess() throws SQLException {
        People.enterPayment(
                103, 750.0f, java.sql.Date.valueOf("2024-03-01"),
                "book authorship", null, 2
        );

        boolean claimed = People.claimPayment(103, "2024-03-15");
        assertTrue(claimed, "Claiming payment should succeed");

        ResultSet rs = DBManager.executeQuery("SELECT dateClaimed FROM Payment WHERE paymentID = 103;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("2024-03-15", rs.getString("dateClaimed"));
        rs.close();
    }

    @Test
    @DisplayName("listUnclaimedPayments: lists unclaimed payments within date range")
    void testListUnclaimedPayments() {
        People.enterPayment(
                104, 300.0f, java.sql.Date.valueOf("2024-02-10"),
                "editorial work", null, 1
        );
        People.enterPayment(
                105, 400.0f, java.sql.Date.valueOf("2024-02-20"),
                "article authorship", java.sql.Date.valueOf("2024-02-25"), 2
        );

        assertDoesNotThrow(() -> People.listUnclaimedPayments("2024-02-01", "2024-02-28"));
    }

    @Test
    @DisplayName("viewPublicationsByEditor: queries publications assigned to an editor")
    void testViewPublicationsByEditor() {
        People.assignEditorToPublication(2, 10, true);
        assertDoesNotThrow(() -> People.viewPublicationsByEditor(2));
    }
}
