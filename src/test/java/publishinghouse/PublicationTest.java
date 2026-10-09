package publishinghouse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class PublicationTest {

    @BeforeEach
    void setUp() {
        DBManager.initialize(true);
        // Seed author
        DBManager.executeUpdate("INSERT INTO Person VALUES (10, 'Robert Martin');");
    }

    @Test
    @DisplayName("addPublication: successfully inserts publication")
    void testAddPublicationSuccess() throws SQLException {
        boolean added = Publication.addPublication(1, "Database Systems", "Book", null);
        assertTrue(added);

        ResultSet rs = DBManager.executeQuery("SELECT title, type FROM Publication WHERE pubID = 1;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("Database Systems", rs.getString("title"));
        assertEquals("Book", rs.getString("type"));
        rs.close();
    }

    @Test
    @DisplayName("addPublication [Edge Case]: rejects duplicate pubID")
    void testAddPublicationDuplicateId() {
        Publication.addPublication(1, "First Book", "Book", null);
        boolean duplicate = Publication.addPublication(1, "Duplicate Book", "Book", null);
        assertFalse(duplicate, "Primary key collision on pubID must fail");
    }

    @Test
    @DisplayName("updatePublication: updates title and type of publication")
    void testUpdatePublicationSuccess() throws SQLException {
        Publication.addPublication(2, "Old Title", "Book", null);
        boolean updated = Publication.updatePublication(2, "New Title", "Book", null);
        assertTrue(updated);

        ResultSet rs = DBManager.executeQuery("SELECT title FROM Publication WHERE pubID = 2;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("New Title", rs.getString("title"));
        rs.close();
    }

    @Test
    @DisplayName("updatePublication [Edge Case]: cannot set periodicity on book with edition")
    void testUpdatePublicationPeriodicityConflict() {
        Publication.addEditionPublication(
                3, "Book with Edition", "Book", 9781111111111L, 1, "Ed 1",
                java.sql.Date.valueOf("2020-01-01"), java.sql.Date.valueOf("2020-02-01"), 29.99
        );

        // Publication linked to ISBN cannot have periodicity
        boolean updated = Publication.updatePublication(3, "Book with Edition", "Magazine", "Weekly");
        assertFalse(updated, "Cannot add periodicity to a publication linked to an edition");
    }

    @Test
    @DisplayName("removePublication: removes standalone publication")
    void testRemovePublicationSuccess() throws SQLException {
        Publication.addPublication(4, "Standalone Book", "Book", null);
        boolean removed = Publication.removePublication(4);
        assertTrue(removed);

        ResultSet rs = DBManager.executeQuery("SELECT * FROM Publication WHERE pubID = 4;");
        assertNotNull(rs);
        assertFalse(rs.next());
        rs.close();
    }

    @Test
    @DisplayName("addEditionPublication: creates publication, edition, and link atomically")
    void testAddEditionPublicationSuccess() throws SQLException {
        boolean added = Publication.addEditionPublication(
                5, "Operating Systems", "Book", 9780133591620L, 10, "Tenth Edition",
                java.sql.Date.valueOf("2018-01-01"), java.sql.Date.valueOf("2018-05-01"), 75.00
        );
        assertTrue(added);

        ResultSet rsEd = DBManager.executeQuery("SELECT price FROM Edition WHERE ISBN = 9780133591620;");
        assertNotNull(rsEd);
        assertTrue(rsEd.next());
        assertEquals(75.00, rsEd.getDouble("price"), 0.01);
        rsEd.close();

        ResultSet rsLink = DBManager.executeQuery("SELECT pubID FROM ISBNPublication WHERE ISBN = 9780133591620;");
        assertNotNull(rsLink);
        assertTrue(rsLink.next());
        assertEquals(5, rsLink.getInt("pubID"));
        rsLink.close();
    }

    @Test
    @DisplayName("addBookEditionToExistingPub: adds second edition to existing book")
    void testAddBookEditionToExistingPubSuccess() throws SQLException {
        Publication.addPublication(6, "Computer Networks", "Book", null);

        boolean added = Publication.addBookEditionToExistingPub(
                6, 9780132126953L, 5, "Fifth Edition",
                java.sql.Date.valueOf("2010-01-01"), java.sql.Date.valueOf("2010-06-01"), 85.0
        );
        assertTrue(added);

        ResultSet rs = DBManager.executeQuery("SELECT editionTitle FROM Edition WHERE ISBN = 9780132126953;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("Fifth Edition", rs.getString("editionTitle"));
        rs.close();
    }

    @Test
    @DisplayName("updateBookEdition: updates price and details of an edition")
    void testUpdateBookEditionSuccess() throws SQLException {
        Publication.addEditionPublication(
                7, "Compilers", "Book", 9780321486813L, 2, "Second Edition",
                java.sql.Date.valueOf("2006-01-01"), java.sql.Date.valueOf("2006-08-01"), 90.0
        );

        boolean updated = Publication.updateBookEdition(
                9780321486813L, 2, "Second Edition (Revised)",
                java.sql.Date.valueOf("2006-01-01"), java.sql.Date.valueOf("2007-01-01"), 95.0
        );
        assertTrue(updated);

        ResultSet rs = DBManager.executeQuery("SELECT price, editionTitle FROM Edition WHERE ISBN = 9780321486813;");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals(95.0, rs.getDouble("price"), 0.01);
        assertEquals("Second Edition (Revised)", rs.getString("editionTitle"));
        rs.close();
    }

    @Test
    @DisplayName("removeBookEditionAndPublication: deletes edition and its parent publication")
    void testRemoveBookEditionAndPublication() throws SQLException {
        Publication.addEditionPublication(
                8, "Distributed Systems", "Book", 9781543057386L, 3, "Third Edition",
                null, null, 45.0
        );

        boolean removed = Publication.removeBookEditionAndPublication(8, 9781543057386L);
        assertTrue(removed);

        ResultSet rsPub = DBManager.executeQuery("SELECT * FROM Publication WHERE pubID = 8;");
        assertNotNull(rsPub);
        assertFalse(rsPub.next());
        rsPub.close();

        ResultSet rsEd = DBManager.executeQuery("SELECT * FROM Edition WHERE ISBN = 9781543057386;");
        assertNotNull(rsEd);
        assertFalse(rsEd.next());
        rsEd.close();
    }

    @Test
    @DisplayName("removeBookEditionNotPub: removes edition while preserving parent publication")
    void testRemoveBookEditionNotPub() throws SQLException {
        Publication.addEditionPublication(
                9, "Artificial Intelligence", "Book", 9780136042594L, 3, "Third Edition",
                null, null, 110.0
        );

        boolean removed = Publication.removeBookEditionNotPub(9780136042594L);
        assertTrue(removed);

        ResultSet rsEd = DBManager.executeQuery("SELECT * FROM Edition WHERE ISBN = 9780136042594;");
        assertNotNull(rsEd);
        assertFalse(rsEd.next(), "Edition must be deleted");
        rsEd.close();

        ResultSet rsPub = DBManager.executeQuery("SELECT * FROM Publication WHERE pubID = 9;");
        assertNotNull(rsPub);
        assertTrue(rsPub.next(), "Parent publication must still exist");
        rsPub.close();
    }

    @Test
    @DisplayName("addIssue, editIssue, and removeIssue: lifecycle of a periodic issue")
    void testIssueLifecycle() throws SQLException {
        Publication.addPublication(20, "Tech Digest", "Magazine", "Monthly");

        // 1. Add Issue
        boolean added = Publication.addIssue(20, "Spring 2024", java.sql.Date.valueOf("2024-03-01"), 12.50);
        assertTrue(added);

        // 2. Edit Issue
        boolean edited = Publication.editIssue(20, "Spring 2024", java.sql.Date.valueOf("2024-03-01"), 14.00);
        assertTrue(edited);

        ResultSet rs = DBManager.executeQuery("SELECT price FROM Issue WHERE pubID = 20 AND issueTitle = 'Spring 2024';");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals(14.00, rs.getDouble("price"), 0.01);
        rs.close();

        // 3. Remove Issue
        boolean removed = Publication.removeIssue(20, "Spring 2024");
        assertTrue(removed);

        ResultSet rsEmpty = DBManager.executeQuery("SELECT * FROM Issue WHERE pubID = 20 AND issueTitle = 'Spring 2024';");
        assertNotNull(rsEmpty);
        assertFalse(rsEmpty.next());
        rsEmpty.close();
    }

    @Test
    @DisplayName("Chapter Operations: addChapter, editChapter, author management, and removeChapter")
    void testChapterOperations() throws SQLException {
        long isbn = 9780132350884L;
        Publication.addEditionPublication(30, "Clean Code", "Book", isbn, 1, "First Edition", null, null, 40.0);

        // 1. Add Chapter TOC
        boolean added = Publication.addChapterTOC(isbn, "Meaningful Names");
        assertTrue(added);

        // 2. Edit Chapter
        boolean edited = Publication.editChapter(
                isbn, "Meaningful Names", java.sql.Date.valueOf("2023-01-01"),
                "Names should reveal intent", "Software Quality"
        );
        assertTrue(edited);

        // 3. Set Author
        boolean authorSet = Publication.updateChapterAuthor(isbn, "Meaningful Names", 10, true);
        assertTrue(authorSet);

        // 4. Remove Author
        boolean authorRemoved = Publication.removeChapterAuthor(isbn, "Meaningful Names");
        assertTrue(authorRemoved);

        // 5. Remove Chapter TOC
        boolean chapterRemoved = Publication.removeChapterTOC(isbn, "Meaningful Names");
        assertTrue(chapterRemoved);

        ResultSet rs = DBManager.executeQuery("SELECT * FROM Chapter WHERE ISBN = " + isbn + ";");
        assertNotNull(rs);
        assertFalse(rs.next());
        rs.close();
    }

    @Test
    @DisplayName("Article Operations: addArticle, editArticle, author management, and removeArticle")
    void testArticleOperations() throws SQLException {
        Publication.addPublication(40, "Nature Insights", "Journal", "Monthly");
        Publication.addIssue(40, "Vol 100", java.sql.Date.valueOf("2024-01-01"), 20.0);

        // 1. Add Article TOC
        boolean added = Publication.addArticleTOC(40, "Vol 100", "Quantum Computing");
        assertTrue(added);

        // 2. Edit Article
        boolean edited = Publication.editArticle(
                40, "Vol 100", "Quantum Computing", java.sql.Date.valueOf("2024-01-10"),
                "Quantum qubits explanation", "Physics"
        );
        assertTrue(edited);

        // 3. Update Article Author
        boolean authorSet = Publication.updateArticleAuthor(40, "Vol 100", "Quantum Computing", 10, true);
        assertTrue(authorSet);

        // 4. Remove Article Author
        boolean authorRemoved = Publication.removeArticleAuthor(40, "Vol 100", "Quantum Computing");
        assertTrue(authorRemoved);

        // 5. Remove Article TOC
        boolean articleRemoved = Publication.removeArticleTOC(40, "Vol 100", "Quantum Computing");
        assertTrue(articleRemoved);
    }

    @Test
    @DisplayName("Search & Compare Queries: verifies search operations execute cleanly")
    void testSearchAndCompareQueries() {
        long isbn = 9780131103627L;
        Publication.addEditionPublication(50, "C Programming", "Book", isbn, 2, "Second Edition",
                java.sql.Date.valueOf("1988-01-01"), java.sql.Date.valueOf("1988-04-01"), 35.0);
        Publication.addChapterTOC(isbn, "Pointers and Arrays");
        Publication.editChapter(isbn, "Pointers and Arrays", java.sql.Date.valueOf("1988-01-01"), "Pointers are...", "Memory");
        Publication.updateChapterAuthor(isbn, "Pointers and Arrays", 10, true);

        Publication.addPublication(60, "Byte Magazine", "Magazine", "Monthly");
        Publication.addIssue(60, "Issue 1", java.sql.Date.valueOf("2023-01-01"), 5.0);
        Publication.addIssue(60, "Issue 2", java.sql.Date.valueOf("2023-02-01"), 5.0);
        Publication.addArticleTOC(60, "Issue 1", "Memory Management");
        Publication.editArticle(60, "Issue 1", "Memory Management", java.sql.Date.valueOf("2023-01-05"), "RAM overview", "Memory");

        assertDoesNotThrow(() -> Publication.findEditionsByTopic("Memory"));
        assertDoesNotThrow(() -> Publication.findArticlesByTopic("Memory"));
        assertDoesNotThrow(() -> Publication.findEditionsByDateRange(java.sql.Date.valueOf("1980-01-01"), java.sql.Date.valueOf("2000-01-01")));
        assertDoesNotThrow(() -> Publication.findArticlesByDateRange(java.sql.Date.valueOf("2022-01-01"), java.sql.Date.valueOf("2024-01-01")));
        assertDoesNotThrow(() -> Publication.findEditionsByAuthor("Robert Martin"));
        assertDoesNotThrow(() -> Publication.compareIssueArticles(60, "Issue 1", 60, "Issue 2"));
    }
}
