package publishinghouse;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Publication class handles all of the query building and execution of
 * operations relating to publications, issues,
 * articles, editions and authors for GutenbergDb.
 */
public class Publication {
    /**
     * Adds a publication object to the database.
     * 
     * @param pubID          pubID for the publication
     * @param title          title for the publication (can be null)
     * @param type           type of the publication
     * @param pubPeriodicity periodicity of the publication (can be null if
     *                       publication is an edition)
     */
    public static boolean addPublication(int pubID, String title, String type, String pubPeriodicity) {
        String titleValue = (title == null || title.isEmpty()) ? "NULL" : "'" + title + "'";
        String periodicityValue = (pubPeriodicity == null || pubPeriodicity.isEmpty()) ? "NULL"
                : "'" + pubPeriodicity + "'";

        String query = String.format("INSERT INTO Publication VALUES (%d, '%s', %s, %s)",
                pubID, type, titleValue, periodicityValue);

        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't add publication to database\n");
            return false;
        }
        System.out.println("Publication added successfully\n");
        return true;

    }

    /**
     * Adds a publication object, edition object, and links them together all in one
     * method.
     * Each insert statement is executed separately and controlled with a
     * transaction, ensuring that if
     * any part of the process fails, the database will be left in a consistent
     * state.
     * 
     * @param pubID         pubID for the publication
     * @param title         title of the publication (can be null)
     * @param type          type of the publication
     * @param ISBN          ISBN of the edition
     * @param edition       edition number
     * @param editionTitle  title of the edition (can be null)
     * @param dateWritten   date the edition was written (can be null)
     * @param datePublished date the edition was published (can be null)
     * @param price         price of the publication (can be null)
     * @return true if the publication and edition were added successfully, false
     *         otherwise
     */
    public static boolean addEditionPublication(int pubID, String title, String type, long ISBN, int edition,
            String editionTitle, java.sql.Date dateWritten, java.sql.Date datePublished, double price) {
        try {
            // Handle null checks for all potential null values, set the query input to the
            // appropriate values
            String titleValue = (title == null || title.isEmpty()) ? "NULL" : "'" + title + "'";
            String editionTitleValue = (editionTitle == null || editionTitle.isEmpty()) ? "NULL"
                    : "'" + editionTitle + "'";
            String dateWrittenValue = (dateWritten == null) ? "NULL"
                    : "'" + dateWritten.toString() + "'";
            String datePublishedValue = (datePublished == null) ? "NULL"
                    : "'" + datePublished.toString() + "'";
            // Begin transaction, first needing to insert into the publication relation
            DBManager.beginTransaction();
            String query = "INSERT INTO Publication VALUES (%d, '%s', %s, NULL)";
            query = String.format(query, pubID, type, titleValue);
            // If there is an unsuccessful addition to the Publication relation, rollback
            // the transaction
            if (!DBManager.executeUpdate(query)) {
                System.out.println("Couldn't add publication to database - check for primary key conflict\n");
                DBManager.rollbackTransaction();
                return false;
            }
            // If the price is meant to be null, insert null in the price field
            // This conditional forms the insert statement into the Edition relation
            if (price >= 0) {
                query = "INSERT INTO Edition VALUES (%d, %d, %s, %s, %s, %f)";
                query = String.format(query, ISBN, edition, editionTitleValue, dateWrittenValue, datePublishedValue,
                        price);
            } else {
                query = "INSERT INTO Edition VALUES (%d, %d, %s, %s, %s, NULL)";
                query = String.format(query, ISBN, edition, editionTitleValue, dateWrittenValue, datePublishedValue);
            }
            // If the edition cannot be added, rollback the transaction
            if (!DBManager.executeUpdate(query)) {
                System.out.println("Couldn't add edition to database - check for primary key conflict\n");
                DBManager.rollbackTransaction();
                return false;
            }
            // Lastly, insert the edition and publication link into the ISBNPublication
            // relation
            query = "INSERT INTO ISBNPublication VALUES (%d, %d)";
            query = String.format(query, ISBN, pubID);
            // If the link cannot be added, rollback the transaction
            if (!DBManager.executeUpdate(query)) {
                System.out.println("Couldn't link ISBN to publication - ensure pubID and ISBN are valid\n");
                DBManager.rollbackTransaction();
                return false;
            }
            System.out.println("Publication and edition added successfully\n");
            DBManager.commitTransaction();
            return true;
            /**
             * All three relations must be updated accordingly, or none at all, to ensure
             * that the publication
             * and corresponding edition are added and linked together.
             */

        } catch (Exception e) {
            System.out.println("Process failed. Ensure input is correct.\n");
            DBManager.rollbackTransaction();
            return false;

        }

    }

    /**
     * Updates the details of the publication.
     * 
     * @param pubID          pubID for the publication
     * @param title          title for the publication (can be null)
     * @param type           type of the publication
     * @param pubPeriodicity periodicity of the publication (can be null if
     *                       publication is an edition)
     * @return true if the publication was updated successfully, false otherwise
     * 
     */
    public static boolean updatePublication(int pubID, String title, String type, String pubPeriodicity) {
        // Check for null values where applicable
        String titleValue = (title == null || title.isEmpty()) ? "NULL" : "'" + title + "'";
        String periodicityValue = (pubPeriodicity == null || pubPeriodicity.isEmpty()) ? "NULL"
                : "'" + pubPeriodicity + "'";
        // Assert that publications with editions cannot have a periodicity
        if (!periodicityValue.equals("NULL")) {
            String query = "SELECT * FROM ISBNPublication WHERE pubID = %d";
            query = String.format(query, pubID);
            try {
                if (DBManager.executeQuery(query).next()) {
                    System.out.println("Update failed - Publications with editions cannot have periodicity\n");
                    return false;
                }
            } catch (SQLException e) {
                System.out.println("Error processing results\n");
                return false;
            }
        }

        // If validity checks are good, update the publication at the given pubID
        String query = "UPDATE Publication SET title = %s, type = '%s', pubPeriodicity = %s WHERE pubID = %d";
        query = String.format(query, titleValue, type, periodicityValue, pubID);
        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't update publication in database\n");
            return false;
        }
        System.out.println("Publication updated successfully\n");

        return true;
    }

    /**
     * Removes the publication object from the database.
     * 
     * @param pubID the pubID of the publication
     * @return true if the publication was deleted, false otherwise
     */
    public static boolean removePublication(int pubID) {
        String query = "DELETE FROM Publication WHERE pubID = %d";
        query = String.format(query, pubID);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out
                    .println(
                            "No valid publication found with the given ID (could have issues or editions related to it).\n");
            return false;
        }
        System.out.println("Publication removed successfully\n");

        return true;
    }

    /**
     * Add an edition and its link to an existing publication with a null
     * periodicity.
     * 
     * @param pubID         the pubID of the publication
     * @param ISBN          the ISBN of the edition
     * @param edition       the edition number of the edition
     * @param editionTitle  the title of the edition (can be null)
     * @param dateWritten   the date the edition was written (can be null)
     * @param datePublished the date the edition was published (can be null)
     * @param price         the price of the edition (can be null)
     */
    public static boolean addBookEditionToExistingPub(int pubID, long ISBN, int edition, String editionTitle,
            java.sql.Date dateWritten, java.sql.Date datePublished, double price) {
        String editionTitleValue = (editionTitle == null || editionTitle.isEmpty()) ? "NULL" : "'" + editionTitle + "'";
        String dateWrittenValue = (dateWritten == null) ? "NULL"
                : "'" + dateWritten.toString() + "'";
        String datePublishedValue = (datePublished == null) ? "NULL"
                : "'" + datePublished.toString() + "'";
        String query = "SELECT * FROM Publication WHERE pubID = %d AND pubPeriodicity IS NULL";
        query = String.format(query, pubID);
        // Check to see if there is a publication with a null periodicity with the given
        // pubID
        try {
            if (!DBManager.executeQuery(query).next()) {
                System.out.println("Eligible publication not found\n");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error processing results\n");
            return false;
        }
        // Transaction to enter the edition details and then link the edition to the
        // publication
        DBManager.beginTransaction();
        if (price >= 0) {
            query = "INSERT INTO Edition VALUES (%d, %d, %s, %s, %s, %f)";
            query = String.format(query, ISBN, edition, editionTitleValue, dateWrittenValue, datePublishedValue, price);
        } else {
            query = "INSERT INTO Edition VALUES (%d, %d, %s, %s, %s, NULL)";
            query = String.format(query, ISBN, edition, editionTitleValue, dateWrittenValue, datePublishedValue);
        }
        // If edition details cannot be added, rollback the transaction to avoid having
        // an unlinked publication or partial edition details in the database
        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't add book edition to database\n");
            DBManager.rollbackTransaction();
            return false;
        }
        // If link cannot be added, rollback the transaction
        query = "INSERT INTO ISBNPublication VALUES (%d, %d)";
        query = String.format(query, ISBN, pubID);
        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't link ISBN to publication\n");
            DBManager.rollbackTransaction();
            return false;
        }
        // Commit if all is successful
        DBManager.commitTransaction();
        System.out.println("Book edition added successfully\n");
        return true;
    }

    /**
     * Updates the details of the edition with the given ISBN.
     * 
     * @param ISBN          the ISBN of the edition
     * @param edition       the edition number of the edition
     * @param editionTitle  the title of the edition (can be null)
     * @param dateWritten   the date the edition was written (can be null)
     * @param datePublished the date the edition was published (can be null)
     * @param price         the price of the edition (can be null)
     * @return true if the edition was updated successfully, false otherwise
     */
    public static boolean updateBookEdition(long ISBN, int edition, String editionTitle, java.sql.Date dateWritten,
            java.sql.Date datePublished, double price) {
        String editionTitleValue = (editionTitle == null || editionTitle.isEmpty()) ? "NULL" : "'" + editionTitle + "'";
        String dateWrittenValue = (dateWritten == null) ? "NULL"
                : "'" + dateWritten.toString() + "'";
        String datePublishedValue = (datePublished == null) ? "NULL"
                : "'" + datePublished.toString() + "'";
        // Null checks and check that price isn't supposed to be null
        // If price is meant to be null, insert the value as null in the table
        if (price >= 0) {
            String query = "UPDATE Edition SET edition = %d, editionTitle = %s, dateWritten = %s, datePublished = %s, price = %f WHERE ISBN = %d";
            query = String.format(query, edition, editionTitleValue, dateWrittenValue, datePublishedValue, price, ISBN);
            if (!DBManager.executeUpdate(query)) {
                System.out.println("Couldn't update book edition in database\n");
                return false;
            }
        } else {
            String query = "UPDATE Edition SET edition = %d, editionTitle = %s, dateWritten = %s, datePublished = %s, price = NULL WHERE ISBN = %d";
            query = String.format(query, edition, editionTitleValue, dateWrittenValue, datePublishedValue, ISBN);
            if (!DBManager.executeUpdate(query)) {
                System.out.println("Couldn't update book edition in database\n");
                return false;
            }
        }
        System.out.println("Book edition updated successfully\n");
        return true;
    }

    /**
     * Removes the given edition and its publication from the database.
     * 
     * @param pubID the pubID of the publication
     * @param ISBN  the ISBN of the edition
     * @return true if it could be removed, false otherwise
     */
    public static boolean removeBookEditionAndPublication(int pubID, long ISBN) {
        DBManager.beginTransaction();
        String query = "DELETE FROM ISBNPublication WHERE ISBN = %d AND pubID = %d";
        query = String.format(query, ISBN, pubID);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out.println("Couldn't remove edition-publication link from database\n");
            DBManager.rollbackTransaction();
            return false;
        }

        query = "DELETE FROM Edition WHERE ISBN = %d";
        query = String.format(query, ISBN);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out.println("Couldn't remove book edition from database\n");
            DBManager.rollbackTransaction();
            return false;
        }

        query = "DELETE FROM Publication WHERE pubID = %d";
        query = String.format(query, pubID);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out.println(
                    "Couldn't remove publication from database - potential foreign key constraint violation\n");
            DBManager.rollbackTransaction();
            return false;
        }

        System.out.println("Book edition removed successfully\n");
        DBManager.commitTransaction();
        return true;
    }

    /**
     * Removes the edition and its link, but doesn't remove the associated
     * publication.
     * 
     * @param ISBN the ISBN of the edition
     * @return true if it was deleted, false if not
     */
    public static boolean removeBookEditionNotPub(long ISBN) {
        DBManager.beginTransaction();
        String query = "DELETE FROM ISBNPublication WHERE ISBN = %d";
        query = String.format(query, ISBN);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out.println("Couldn't remove edition-publication link from database\n");
            DBManager.rollbackTransaction();
            return false;
        }

        query = "DELETE FROM Edition WHERE ISBN = %d";
        query = String.format(query, ISBN);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out.println("Couldn't remove book edition from database\n");
            DBManager.rollbackTransaction();
            return false;
        }

        System.out.println("Book edition removed successfully\n");
        DBManager.commitTransaction();
        return true;
    }

    /**
     * Adds an issue to the database
     * 
     * @param pubID      the pubID of the issue
     * @param issueTitle the issue title of the specific issue
     * @param pubDate    the date the issue was published (can be null)
     * @param price      the price of the issue (can be null)
     * @return true if added, false if not
     */
    public static boolean addIssue(int pubID, String issueTitle, java.sql.Date pubDate, double price) {
        String pubDateValue = (pubDate == null) ? "NULL" : "'" + pubDate.toString() + "'";
        if (price >= 0) {
            String query = "INSERT INTO Issue VALUES (%d, '%s', %s, %f)";
            query = String.format(query, pubID, issueTitle, pubDateValue, price);
            if (!DBManager.executeUpdate(query)) {
                System.out.println("Couldn't add issue to database\n");
                return false;
            }
        } else {
            String query = "INSERT INTO Issue VALUES (%d, '%s', %s, NULL)";
            query = String.format(query, pubID, issueTitle, pubDateValue);
            if (!DBManager.executeUpdate(query)) {
                System.out.println("Couldn't add issue to database\n");
                return false;
            }
        }
        System.out.println("Issue added successfully\n");
        return true;
    }

    /**
     * Edits an existing issue in the database
     * 
     * @param pubID      the pubID of the issue
     * @param issueTitle the issue title of the specific issue
     * @param pubDate    the date the issue was published (can be null)
     * @param price      the price of the issue (can be null)
     * @return true if updated, false if not
     */
    public static boolean editIssue(int pubID, String issueTitle, java.sql.Date pubDate, double price) {
        String pubDateValue = (pubDate == null) ? "NULL" : "'" + pubDate.toString() + "'";
        if (price >= 0) {
            String query = "UPDATE Issue SET pubDate = %s, price = %f WHERE pubID = %d AND issueTitle = '%s'";
            query = String.format(query, pubDateValue, price, pubID, issueTitle);
            if (!DBManager.executeUpdate(query)) {
                System.out.println("Couldn't update issue in database\n");
                return false;
            }
        } else {
            String query = "UPDATE Issue SET pubDate = %s, price = NULL WHERE pubID = %d AND issueTitle = '%s'";
            query = String.format(query, pubDateValue, pubID, issueTitle);
            if (!DBManager.executeUpdate(query)) {
                System.out.println("Couldn't update issue in database\n");
                return false;
            }
        }
        System.out.println("Issue updated successfully\n");
        return true;
    }

    /**
     * Removes an issue from the database.
     * 
     * @param pubID      the pubID of the publication
     * @param issueTitle the title of the issue being removed
     * @return true if removed, false if not
     */
    public static boolean removeIssue(int pubID, String issueTitle) {
        String query = "DELETE FROM Issue WHERE pubID = %d AND issueTitle = '%s'";
        query = String.format(query, pubID, issueTitle);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out.println("No issue found with the given ID and title\n");
            return false;
        }
        System.out.println("Issue removed successfully\n");
        return true;
    }

    /**
     * Adds a chapter that will be associated with the given edition, doesn't add
     * the details of the chapter.
     * This is like adding the chapter to the table of contents of the edition.
     * 
     * @param ISBN         the ISBN of the edition
     * @param chapterTitle the title of the chapter being added
     * @return true if added, false if not
     */
    public static boolean addChapterTOC(long ISBN, String chapterTitle) {
        String query = "INSERT INTO Chapter VALUES (%d, '%s', NULL, NULL, NULL)";
        query = String.format(query, ISBN, chapterTitle);
        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't add chapter to database\n");
            return false;
        }
        System.out.println("Chapter added successfully\n");
        return true;
    }

    /**
     * Edits the details of an existing chapter for an edition.
     * 
     * @param ISBN         the ISBN of the edition
     * @param chapterTitle the title of the chapter
     * @param date         the date published (can be null)
     * @param text         the text of the chapter (can be null)
     * @param topic        the topic of the chapter (can be null)
     * @return true if chapter was updated, false if not
     */
    public static boolean editChapter(long ISBN, String chapterTitle, java.sql.Date date, String text, String topic) {
        String textValue = (text == null || text.isEmpty()) ? "NULL" : "'" + text + "'";
        String dateValue = (date == null) ? "NULL" : "'" + date.toString() + "'";
        String topicValue = (topic == null || topic.isEmpty()) ? "NULL" : "'" + topic + "'";
        String query = "UPDATE Chapter SET date = %s, text = %s, topic = %s WHERE ISBN = %d AND chapterTitle = '%s'";
        query = String.format(query, dateValue, textValue, topicValue, ISBN, chapterTitle);
        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't update chapter in database\n");
            return false;
        }
        System.out.println("Chapter updated successfully\n");
        return true;
    }

    /**
     * Updates the author of a chapter, which is separate from the edit chapter
     * application.
     * 
     * @param ISBN         the ISBN of the edition
     * @param chapterTitle the title of the chapter
     * @param pID          the pID of the author
     * @param invited      whether or not the author was invited
     * @return true if updated, false if not
     */
    public static boolean updateChapterAuthor(long ISBN, String chapterTitle, int pID, boolean invited) {
        String query = "SELECT * FROM WritesChapter WHERE ISBN = %d AND chapterTitle = '%s'";
        query = String.format(query, ISBN, chapterTitle);
        ResultSet table = DBManager.executeQuery(query);
        try {
            // If chapter hasn't been given an author yet
            if (table == null || !table.next()) {
                query = "INSERT INTO WritesChapter VALUES (%d, '%s', %d, %d)";
                query = String.format(query, pID, chapterTitle, ISBN, invited ? 1 : 0);
                if (!DBManager.executeUpdate(query)) {
                    System.out.println("Couldn't set chapter author in database\n");
                    return false;
                }
                System.out.println("Chapter author set successfully\n");
                return true;
            }
            // If chapter has an author already, we must update
            else {
                query = "UPDATE WritesChapter SET pID = %d, invited = %d WHERE ISBN = %d AND chapterTitle = '%s'";
                query = String.format(query, pID, invited ? 1 : 0, ISBN, chapterTitle);
                if (!DBManager.executeUpdate(query)) {
                    System.out.println("Couldn't update chapter author in database\n");
                    return false;
                }
                System.out.println("Chapter author updated successfully\n");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error processing results\n");
            return false;
        }
    }

    /**
     * Removes the author for the chapter so there is now none.
     * 
     * @param ISBN         the ISBN of the edition
     * @param chapterTitle the title of the chapter
     * @return true if updated, false if not
     */
    public static boolean removeChapterAuthor(long ISBN, String chapterTitle) {
        String query = "DELETE FROM WritesChapter WHERE ISBN = %d AND chapterTitle = '%s'";
        query = String.format(query, ISBN, chapterTitle);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out.println(
                    "Couldn't remove chapter author from database - chapter may not have an author assigned\n");
            return false;
        }
        System.out.println("Chapter author removed successfully\n");
        return true;
    }

    /**
     * Removes the chapter from the database and removes chapter-edition link.
     * 
     * @param ISBN         the ISBN of the edition
     * @param chapterTitle the title of the chapter
     * @return true if updated, false if not
     */
    public static boolean removeChapterTOC(long ISBN, String chapterTitle) {
        // Delete from WritesChapter iF NECESSARY, not every chapter will have an author
        String query = "DELETE FROM WritesChapter WHERE ISBN = %d AND chapterTitle = '%s'";
        query = String.format(query, ISBN, chapterTitle);
        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't remove chapter from database\n");
            return false;
        }
        query = "DELETE FROM Chapter WHERE ISBN = %d AND chapterTitle = '%s'";
        query = String.format(query, ISBN, chapterTitle);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out.println("Chapter does not exist in the database\n");
            return false;
        }
        System.out.println("Chapter removed successfully\n");
        return true;
    }

    /**
     * Adds an article to the database.
     * 
     * @param pubID        the pubID of the publication
     * @param issueTitle   the title of the issue
     * @param articleTitle the title of the article
     * @return true if added, false if not
     */
    public static boolean addArticleTOC(int pubID, String issueTitle, String articleTitle) {
        String query = "INSERT INTO Article VALUES (%d, '%s', '%s', NULL, NULL, NULL)";
        query = String.format(query, pubID, issueTitle, articleTitle);
        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't add article to database\n");
            return false;
        }
        System.out.println("Article added successfully\n");
        return true;
    }

    /**
     * Edits the article details.
     * 
     * @param pubID        the pubID of the publication
     * @param issueTitle   the title of the issue
     * @param articleTitle the title of the article
     * @param dateWritten  the date that the article was written (can be null)
     * @param text         the text of the article (can be null)
     * @param topic        the topic of the article (can be null)
     * @return true if added, false if not
     */
    public static boolean editArticle(int pubID, String issueTitle, String articleTitle, java.sql.Date dateWritten,
            String text, String topic) {
        String textValue = (text == null || text.isEmpty()) ? "NULL" : "'" + text + "'";
        String dateWrittenValue = (dateWritten == null) ? "NULL"
                : "'" + dateWritten.toString() + "'";
        String topicValue = (topic == null || topic.isEmpty()) ? "NULL" : "'" + topic + "'";
        String query = "UPDATE Article SET dateWritten = %s, text = %s, topic = %s WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
        query = String.format(query, dateWrittenValue, textValue, topicValue, pubID, issueTitle, articleTitle);
        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't update article in database\n");
            return false;
        }
        System.out.println("Article updated successfully\n");
        return true;
    }

    /**
     * Updates the article author.
     * 
     * @param pubID        pubID of the article
     * @param issueTitle   title of the issue
     * @param articleTitle title of the article
     * @param pID          pID of the author
     * @param invited      whether or not the author was invited
     * @return true if updated, false if not
     */
    public static boolean updateArticleAuthor(int pubID, String issueTitle, String articleTitle, int pID,
            boolean invited) {
        String query = "SELECT * FROM WritesArticle WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
        query = String.format(query, pubID, issueTitle, articleTitle);
        ResultSet table = DBManager.executeQuery(query);
        try {
            // If article hasn't been given an author yet
            if (table == null || !table.next()) {
                query = "INSERT INTO WritesArticle VALUES (%d, %d, '%s', '%s', %d)";
                query = String.format(query, pID, pubID, articleTitle, issueTitle, invited ? 1 : 0);
                if (!DBManager.executeUpdate(query)) {
                    System.out.println("Couldn't set article author in database\n");
                    return false;
                }
                System.out.println("Article author set successfully\n");
                return true;
            }
            // If article has an author already, we must update
            else {
                query = "UPDATE WritesArticle SET pID = %d, invited = %d WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
                query = String.format(query, pID, invited ? 1 : 0, pubID, issueTitle, articleTitle);
                if (!DBManager.executeUpdate(query)) {
                    System.out.println("Couldn't update article author in database\n");
                    return false;
                }
                System.out.println("Article author updated successfully\n");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error processing results\n");
            return false;
        }
    }

    /**
     * Removes the article author
     * 
     * @param pubID        pubID of the publication
     * @param issueTitle   title of the issue
     * @param articleTitle title of the article
     * @return true if updated, false if not
     */
    public static boolean removeArticleAuthor(int pubID, String issueTitle, String articleTitle) {
        String query = "DELETE FROM WritesArticle WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
        query = String.format(query, pubID, issueTitle, articleTitle);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out.println(
                    "Couldn't remove article author from database - article may not have an author assigned\n");
            return false;
        }
        System.out.println("Article author removed successfully\n");
        return true;
    }

    /**
     * Removes the article from the database.
     * 
     * @param pubID        pubID of the publication
     * @param issueTitle   title of the issue
     * @param articleTitle title of the article
     * @return true if updated, false if not
     */
    public static boolean removeArticleTOC(long pubID, String issueTitle, String articleTitle) {
        String query = "DELETE FROM WritesArticle WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
        query = String.format(query, pubID, issueTitle, articleTitle);
        if (!DBManager.executeUpdate(query)) {
            System.out.println("Couldn't remove article from database\n");
            return false;
        }
        query = "DELETE FROM Article WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
        query = String.format(query, pubID, issueTitle, articleTitle);
        if (DBManager.executeUpdateCount(query) <= 0) {
            System.out.println("Article does not exist in the database\n");
            return false;
        }
        System.out.println("Article removed successfully\n");
        return true;
    }

    /**
     * Finds editions with a certain topic.
     * 
     * @param topic the topic being searched for
     * @return true if query was processed, false if error
     */
    public static boolean findEditionsByTopic(String topic) {
        String query = "SELECT * FROM Edition WHERE ISBN IN (SELECT ISBN FROM Chapter WHERE topic = '%s')";
        query = String.format(query, topic);
        ResultSet table = DBManager.executeQuery(query);
        try {
            if (table != null && table.next()) {
                do {
                    long ISBN = table.getLong("ISBN");
                    int edition = table.getInt("edition");
                    String editionTitle = table.getString("editionTitle");
                    java.sql.Date dateWritten = table.getDate("dateWritten");
                    java.sql.Date datePublished = table.getDate("datePublished");
                    double price = table.getDouble("price");
                    System.out.println("ISBN: " + ISBN + ", Edition: " + edition + ", Edition Title: " + editionTitle
                            + ", Date Written: " + dateWritten + ", Date Published: " + datePublished + ", Price: "
                            + price);
                } while (table.next());
                System.out.println();
                return true;
            } else {
                System.out.println("Couldn't find books with given topic\n");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error processing results\n");
            return false;

        }
    }

    /**
     * Finds articles with a certain topic.
     * 
     * @param topic topic being searched for
     * @return true if processed, false if error
     */
    public static boolean findArticlesByTopic(String topic) {
        String query = "SELECT * FROM Article WHERE topic = '%s'";
        query = String.format(query, topic);
        ResultSet table = DBManager.executeQuery(query);
        try {
            if (table != null && table.next()) {
                do {
                    int pubID = table.getInt("pubID");
                    String issueTitle = table.getString("issueTitle");
                    String articleTitle = table.getString("articleTitle");
                    java.sql.Date dateWritten = table.getDate("dateWritten");
                    String text = table.getString("text");
                    System.out.println("Publication ID: " + pubID + ", Issue Title: " + issueTitle + ", Article Title: "
                            + articleTitle + ", Date Written: " + dateWritten + ", Text: " + text);
                } while (table.next());
                System.out.println();
                return true;

            } else {
                System.out.println("Couldn't find articles with given topic\n");
                return true;

            }
        } catch (Exception e) {
            System.out.println("Error processing results\n");
            return false;

        }
    }

    /**
     * Finds editions published within a certain date range.
     * 
     * @param startDate beginning date of the range
     * @param endDate   end date of the range
     * @return true if processed, false if error
     */
    public static boolean findEditionsByDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        String query = "SELECT * FROM Edition WHERE datePublished BETWEEN '%s' AND '%s'";
        query = String.format(query, startDate, endDate);
        ResultSet table = DBManager.executeQuery(query);
        try {
            if (table != null && table.next()) {
                do {
                    long ISBN = table.getLong("ISBN");
                    int edition = table.getInt("edition");
                    String editionTitle = table.getString("editionTitle");
                    java.sql.Date dateWritten = table.getDate("dateWritten");
                    java.sql.Date datePublished = table.getDate("datePublished");
                    double price = table.getDouble("price");
                    System.out.println("ISBN: " + ISBN + ", Edition: " + edition + ", Edition Title: " + editionTitle
                            + ", Date Written: " + dateWritten + ", Date Published: " + datePublished + ", Price: "
                            + price);
                } while (table.next());
            } else {
                System.out.println("Couldn't find books published in given date range\n");
                return true;

            }
        } catch (Exception e) {
            System.out.println("Error processing results\n");
            return false;
        }
        return true;
    }

    /**
     * Finds articles within a given date range.
     * 
     * @param startDate date at the beginning of the range
     * @param endDate   date at the end of the range
     * @return true if processed, false if error
     */
    public static boolean findArticlesByDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        String query = "SELECT * FROM Article WHERE dateWritten BETWEEN '%s' AND '%s'";
        query = String.format(query, startDate, endDate);
        ResultSet table = DBManager.executeQuery(query);
        try {
            if (table != null && table.next()) {
                do {
                    int pubID = table.getInt("pubID");
                    String issueTitle = table.getString("issueTitle");
                    String articleTitle = table.getString("articleTitle");
                    java.sql.Date dateWritten = table.getDate("dateWritten");
                    String text = table.getString("text");
                    System.out.println("Publication ID: " + pubID + ", Issue Title: " + issueTitle + ", Article Title: "
                            + articleTitle + ", Date Written: " + dateWritten + ", Text: " + text);
                } while (table.next());
                return true;
            } else {
                System.out.println("Couldn't find articles written in given date range\n");
                return true;

            }
        } catch (Exception e) {
            System.out.println("Error processing results\n");
            return false;
        }
    }

    /**
     * Finds all editions written by an author with the given name.
     * 
     * @param authorName the name of the author
     * @return true if processed, false if error
     */
    public static boolean findEditionsByAuthor(String authorName) {
        String query = "SELECT * FROM Edition WHERE ISBN IN (SELECT ISBN FROM WritesChapter WHERE pID = (SELECT pID FROM Person WHERE name = '%s'))";
        query = String.format(query, authorName);
        ResultSet table = DBManager.executeQuery(query);
        try {
            if (table != null && table.next()) {
                do {
                    long ISBN = table.getLong("ISBN");
                    int edition = table.getInt("edition");
                    String editionTitle = table.getString("editionTitle");
                    java.sql.Date dateWritten = table.getDate("dateWritten");
                    java.sql.Date datePublished = table.getDate("datePublished");
                    double price = table.getDouble("price");
                    System.out.println("ISBN: " + ISBN + ", Edition: " + edition + ", Edition Title: " + editionTitle
                            + ", Date Written: " + dateWritten + ", Date Published: " + datePublished + ", Price: "
                            + price);
                } while (table.next());
                return true;
            } else {
                System.out.println("Couldn't find books with given author\n");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error processing results\n");
            return false;

        }

    }

    /**
     * Finds articles written by an author with the given name.
     * 
     * @param authorName name of the author
     * @return true if processed, false if error
     */
    public static boolean findArticlesByAuthor(String authorName) {
        String query = "SELECT * FROM Article WHERE articleTitle IN (SELECT articleTitle FROM WritesArticle WHERE pID = (SELECT pID FROM Person WHERE name = '%s'))";
        query = String.format(query, authorName);
        ResultSet table = DBManager.executeQuery(query);
        try {
            if (table != null && table.next()) {
                do {
                    int pubID = table.getInt("pubID");
                    String issueTitle = table.getString("issueTitle");
                    String articleTitle = table.getString("articleTitle");
                    java.sql.Date dateWritten = table.getDate("dateWritten");
                    String text = table.getString("text");
                    String topic = table.getString("topic");
                    System.out.println("Publication ID: " + pubID + ", Issue Title: " + issueTitle + ", Article Title: "
                            + articleTitle + ", Date Written: " + dateWritten + ", Text: " + text + ", Topic: "
                            + topic);
                } while (table.next());
                return true;
            } else {
                System.out.println("Couldn't find articles with given author\n");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error processing results\n");
            return false;
        }

    }

    /**
     * Find all the articles of 2 given issues.
     * 
     * @param pubID1      pubID of issue 1
     * @param issueTitle1 issue title of issue 1
     * @param pubID2      pubID of issue 2
     * @param issueTitle2 issue title of issue 2
     * @return true if processed, false if error
     */
    public static boolean compareIssueArticles(int pubID1, String issueTitle1, int pubID2, String issueTitle2) {
        String query = "SELECT * FROM Article WHERE (pubID=%d AND issueTitle = '%s') OR (pubID=%d AND issueTitle = '%s') ORDER BY issueTitle, articleTitle";
        query = String.format(query, pubID1, issueTitle1, pubID2, issueTitle2);
        ResultSet table = DBManager.executeQuery(query);
        try {
            if (table != null && table.next()) {
                do {
                    int publicationID = table.getInt("pubID");
                    String issueTitle = table.getString("issueTitle");
                    String articleTitle = table.getString("articleTitle");
                    String topic = table.getString("topic");
                    java.sql.Date dateWritten = table.getDate("dateWritten");
                    String text = table.getString("text");
                    System.out.println(
                            "Publication ID: " + publicationID + ", Issue Title: " + issueTitle + ", Article Title: "
                                    + articleTitle + ", Date Written: " + dateWritten + ", Text: " + text + ", Topic: "
                                    + topic);
                } while (table.next());
                return true;

            } else {
                System.out.println("Couldn't find articles for given issues\n");
                return true;

            }
        } catch (Exception e) {
            System.out.println("Error processing results\n");
            return false;
        }

    }

}
