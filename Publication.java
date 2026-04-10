import java.sql.ResultSet;
import java.sql.SQLException;

/** Assert fields are proper length = titles, ISBN, type etc */

// State character limits

// PROMPT USER IF THEY WANT IT TO BE AN EDITION OR NOT
// WHEN ADDING EDITION SEPARATELY< MAKE SURE PUBID IS SAVED FOR IT


/**
 * Testing:
 * 1. DONE Add publication without edition
 * 2. DONE Add publication with edition (check all three tables AND transaction rollback)
 * 3. DONE Add publication and later add edition (check all three tables)
 * 4. DONE Remove publication with edition (check all three tables AND transaction rollback)
 * 5. DONE Remove edition without publicatoin (check edition and link tables)
 * 6. DONE Set author of chapter and article initially
 * 7. DONE Set updates to chapter and article author
 * Check if trying to delete something that doesnt exist works
 *  * 5. Null checking for everything that can have a null
 * 8. EMPTY DATES NEED FIX
 * 8. Probably just everything in general - make sure empty query returns have proper output
 * 
 * 
 */
public class Publication{
  // Adding publications in general (periodic or non periodic without edition link)

  //title, pub periodicity can be null
  public static boolean addPublication(int pubID, String title, String type, String pubPeriodicity){
    String titleValue = (title == null || title.isEmpty()) ? "NULL" : "'" + title + "'";
    String periodicityValue = (pubPeriodicity == null || pubPeriodicity.isEmpty()) ? "NULL" : "'" + pubPeriodicity + "'";
    
    String query = String.format("INSERT INTO Publication VALUES (%d, '%s', %s, %s)", 
        pubID, type, titleValue, periodicityValue);
    
    if(!DBManager.executeUpdate(query)){
        System.out.println("Couldn't add publication to database\n");
        return false;
    }
    System.out.println("Publication added successfully\n");
    return true;
}

  // Adding publications and edition with edition link
  // title, edition title, date written, date published, price can be null
  public static boolean addEditionPublication(int pubID, String title, String type, long ISBN, int edition, String editionTitle, java.sql.Date dateWritten, java.sql.Date datePublished, double price){
    String titleValue = (title == null || title.isEmpty()) ? "NULL" : "'" + title + "'";
    String editionTitleValue = (editionTitle == null || editionTitle.isEmpty()) ? "NULL" : "'" + editionTitle + "'";
    String dateWrittenValue = (dateWritten == null || dateWritten.toString().isEmpty()) ? "NULL" : "'" + dateWritten.toString() + "'";
    String datePublishedValue = (datePublished == null || datePublished.toString().isEmpty()) ? "NULL" : "'" + datePublished.toString() + "'";
    DBManager.beginTransaction();
    String query = "INSERT INTO Publication VALUES (%d, '%s', %s, NULL)";
    query = String.format(query, pubID, type, titleValue);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add publication to database\n");
      DBManager.rollbackTransaction();
      return false;
    }
    if(price >= 0){
      query = "INSERT INTO Edition VALUES (%d, %d, %s, %s, %s, %f)";
      query = String.format(query, ISBN, edition, editionTitleValue, dateWrittenValue, datePublishedValue, price);
    }
    else{
      query = "INSERT INTO Edition VALUES (%d, %d, %s, %s, %s, NULL)";
      query = String.format(query, ISBN, edition, editionTitleValue, dateWrittenValue, datePublishedValue);
    }
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add edition to database\n");
      DBManager.rollbackTransaction();
      return false;
    }
    query = "INSERT INTO ISBNPublication VALUES (%d, %d)";
    query = String.format(query, ISBN, pubID);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't link ISBN to publication\n");
      DBManager.rollbackTransaction();
      return false;
    }
    System.out.println("Publication and edition added successfully\n");
    DBManager.commitTransaction();
    return true;
  }

  // Updating just the publication details
    //title, pub periodicity can be null
  public static boolean updatePublication(int pubID, String title, String type, String pubPeriodicity){
    String titleValue = (title == null || title.isEmpty()) ? "NULL" : "'" + title + "'";
    String periodicityValue = (pubPeriodicity == null || pubPeriodicity.isEmpty()) ? "NULL" : "'" + pubPeriodicity + "'";

    String query = "UPDATE Publication SET title = %s, type = '%s', pubPeriodicity = %s WHERE pubID = %d";
    query = String.format(query, titleValue, type, periodicityValue, pubID);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update publication in database\n");
      return false;
    }
    System.out.println("Publication updated successfully\n");
    
    return true;
  }

  // Remove the publication
  public static boolean removePublication(int pubID){
    String query = "DELETE FROM Publication WHERE pubID = %d";
    query = String.format(query, pubID);
    if(DBManager.executeUpdateCount(query) <= 0){
      System.out.println("No publication found with the given ID\n");
      return false;
    }
    System.out.println("Publication removed successfully\n");
    
    return true;
  }

  // Adding edition to an existing publication and linking it with the publication
  // edition title, date written, date published, price can be null
  public static boolean addBookEditionToExistingPub(int pubID, long ISBN, int edition, String editionTitle, java.sql.Date dateWritten, java.sql.Date datePublished, double price){
    String editionTitleValue = (editionTitle == null || editionTitle.isEmpty()) ? "NULL" : "'" + editionTitle + "'";
    String dateWrittenValue = (dateWritten == null || dateWritten.toString().isEmpty()) ? "NULL" : "'" + dateWritten.toString() + "'";
    String datePublishedValue = (datePublished == null || datePublished.toString().isEmpty()) ? "NULL" : "'" + datePublished.toString() + "'";
    String query = "SELECT * FROM Publication WHERE pubID = %d AND pubPeriodicity IS NULL";
    query = String.format(query, pubID);
    try {
      if(!DBManager.executeQuery(query).next()){
        System.out.println("Eligible publication not found\n");
        return false;
      }
    } catch (SQLException e) {
      System.out.println("Error processing results\n");
      return false;
    }
    DBManager.beginTransaction();
    if(price >= 0){
      query = "INSERT INTO Edition VALUES (%d, %d, %s, %s, %s, %f)";
      query = String.format(query, ISBN, edition, editionTitleValue, dateWrittenValue, datePublishedValue, price);
    }
    else{
      query = "INSERT INTO Edition VALUES (%d, %d, %s, %s, %s, NULL)";
      query = String.format(query, ISBN, edition, editionTitleValue, dateWrittenValue, datePublishedValue);
    }
   if(!DBManager.executeUpdate(query)){
     System.out.println("Couldn't add book edition to database\n");
      DBManager.rollbackTransaction();
    return false;
    }
    query = "INSERT INTO ISBNPublication VALUES (%d, %d)";
    query = String.format(query, ISBN, pubID);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't link ISBN to publication\n");
      DBManager.rollbackTransaction();
      return false;
    }
    System.out.println("Book edition added successfully\n");
    DBManager.commitTransaction();
    return true;
  }

  // Updating just the edition details
  // edition title, date written, date published, price can be null
  public static boolean updateBookEdition(long ISBN, int edition, String editionTitle, java.sql.Date dateWritten, java.sql.Date datePublished, double price){
    String editionTitleValue = (editionTitle == null || editionTitle.isEmpty()) ? "NULL" : "'" + editionTitle + "'";
    String dateWrittenValue = (dateWritten == null || dateWritten.toString().isEmpty()) ? "NULL" : "'" + dateWritten.toString() + "'";
    String datePublishedValue = (datePublished == null || datePublished.toString().isEmpty()) ? "NULL" : "'" + datePublished.toString() + "'";
    if(price >= 0){
      String query = "UPDATE Edition SET edition = %d, editionTitle = %s, dateWritten = %s, datePublished = %s, price = %f WHERE ISBN = %d";
      query = String.format(query, edition, editionTitleValue, dateWrittenValue, datePublishedValue, price, ISBN);
      if(!DBManager.executeUpdate(query)){
        System.out.println("Couldn't update book edition in database\n");
        return false;
      }
    }
    else{
      String query = "UPDATE Edition SET edition = %d, editionTitle = %s, dateWritten = %s, datePublished = %s, price = NULL WHERE ISBN = %d";
      query = String.format(query, edition, editionTitleValue, dateWrittenValue, datePublishedValue, ISBN);
      if(!DBManager.executeUpdate(query)){
        System.out.println("Couldn't update book edition in database\n");
        return false;
      }
    }
    System.out.println("Book edition updated successfully\n");
    return true;
  }

  public static boolean removeBookEditionAndPublication(int pubID, long ISBN){
    DBManager.beginTransaction();
    String query = "DELETE FROM ISBNPublication WHERE ISBN = %d";
    query = String.format(query, ISBN);
    if(DBManager.executeUpdateCount(query) <= 0){
      System.out.println("Couldn't remove edition-publication link from database\n");
      DBManager.rollbackTransaction();
      return false;
    }
    
    query = "DELETE FROM Edition WHERE ISBN = %d";
    query = String.format(query, ISBN);
    if(DBManager.executeUpdateCount(query) <= 0){
      System.out.println("Couldn't remove book edition from database\n");
      DBManager.rollbackTransaction();
      return false;
    }

    query = "SELECT * FROM Publication WHERE pubID = %d AND type = 'Book' AND pubPeriodicity IS NULL";
    query = String.format(query, pubID);
    ResultSet table = DBManager.executeQuery(query);
    try {
      if(!table.next()){
        System.out.println("Publication not found or not a book with null periodicity\n");
        DBManager.rollbackTransaction();
        return false;
      }
    } catch (SQLException e) {
      System.out.println("Error processing results\n");
      DBManager.rollbackTransaction();
      return false;
    }

    query = "DELETE FROM Publication WHERE pubID = %d";
    query = String.format(query, pubID);
    if(DBManager.executeUpdateCount(query) <= 0){
      System.out.println("Couldn't remove publication from database\n");
      DBManager.rollbackTransaction();
      return false;
    }

    System.out.println("Book edition removed successfully\n");
    DBManager.commitTransaction();
    return true;
  }

  public static boolean removeBookEditionNotPub(long ISBN){
    DBManager.beginTransaction();
    String query = "DELETE FROM ISBNPublication WHERE ISBN = %d";
    query = String.format(query, ISBN);
    if(DBManager.executeUpdateCount(query) <= 0){
      System.out.println("Couldn't remove edition-publication link from database\n");
      DBManager.rollbackTransaction();
      return false;
    }
    
    query = "DELETE FROM Edition WHERE ISBN = %d";
    query = String.format(query, ISBN);
    if(DBManager.executeUpdateCount(query) <= 0){
      System.out.println("Couldn't remove book edition from database\n");
      DBManager.rollbackTransaction();
      return false;
    }

    System.out.println("Book edition removed successfully\n");
    DBManager.commitTransaction();
    return true;
  }

  //pub date and price can be null
  public static boolean addIssue(int pubID, String issueTitle, java.sql.Date pubDate, double price){
     String pubDateValue = (pubDate == null || pubDate.toString().isEmpty()) ? "NULL" : "'" + pubDate.toString() + "'";
    if(price >= 0){
      String query = "INSERT INTO Issue VALUES (%d, '%s', %s, %f)";
      query = String.format(query, pubID, issueTitle, pubDateValue, price);
      if(!DBManager.executeUpdate(query)){
        System.out.println("Couldn't add issue to database\n");
        return false;
      }
    }
    else{
      String query = "INSERT INTO Issue VALUES (%d, '%s', %s, NULL)";
      query = String.format(query, pubID, issueTitle, pubDateValue);
      if(!DBManager.executeUpdate(query)){
        System.out.println("Couldn't add issue to database\n");
        return false;
      }
    }
    System.out.println("Issue added successfully\n");
    return true;
  }
    

  // pub date and price can be null
  public static boolean editIssue(int pubID, String issueTitle, java.sql.Date pubDate, double price){
    String pubDateValue = (pubDate == null || pubDate.toString().isEmpty()) ? "NULL" : "'" + pubDate.toString() + "'";
    if(price >= 0){
      String query = "UPDATE Issue SET pubDate = %s, price = %f WHERE pubID = %d AND issueTitle = '%s'";
      query = String.format(query, pubDateValue, price, pubID, issueTitle);
      if(!DBManager.executeUpdate(query)){
        System.out.println("Couldn't update issue in database\n");
        return false;
      }
    }
    else{
      String query = "UPDATE Issue SET pubDate = %s, price = NULL WHERE pubID = %d AND issueTitle = '%s'";
      query = String.format(query, pubDateValue, pubID, issueTitle);
      if(!DBManager.executeUpdate(query)){
        System.out.println("Couldn't update issue in database\n");
        return false;
      }
    }
    System.out.println("Issue updated successfully\n");
    return true;
  }

  public static boolean removeIssue(int pubID, String issueTitle){
    String query = "DELETE FROM Issue WHERE pubID = %d AND issueTitle = '%s'";
    query = String.format(query, pubID, issueTitle);
    if(DBManager.executeUpdateCount(query) <= 0){
      System.out.println("No issue found with the given ID and title\n");
      return false;
    }
    System.out.println("Issue removed successfully\n");
    return true;
  }



  public static boolean addChapterTOC(long ISBN, String chapterTitle){
    String query = "INSERT INTO Chapter VALUES (%d, '%s', NULL, NULL, NULL)";
    query = String.format(query, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add chapter to database\n");
      return false;
    }
    System.out.println("Chapter added successfully\n");
    return true;
  }

  // text, date and topic can be null
  public static boolean editChapter(long ISBN, String chapterTitle, java.sql.Date date, String text, String topic){
    String textValue = (text == null || text.isEmpty()) ? "NULL" : "'" + text + "'";
    String dateValue = (date == null || date.toString().isEmpty()) ? "NULL" : "'" + date.toString() + "'";
    String topicValue = (topic == null || topic.isEmpty()) ? "NULL" : "'" + topic + "'";
    String query = "UPDATE Chapter SET date = %s, text = %s, topic = %s WHERE ISBN = %d AND chapterTitle = '%s'";
    query = String.format(query, dateValue, textValue, topicValue, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update chapter in database\n");
      return false;
    }
    System.out.println("Chapter updated successfully\n");
    return true;
  }

  public static boolean removeChapterTOC(long ISBN, String chapterTitle){
    String query = "DELETE FROM WritesChapter WHERE ISBN = %d AND chapterTitle = '%s'";
    query = String.format(query, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove chapter from database\n");
      return false;
    }
    query = "DELETE FROM Chapter WHERE ISBN = %d AND chapterTitle = '%s'";
    query = String.format(query, ISBN, chapterTitle);
    if(DBManager.executeUpdateCount(query) <= 0){
      System.out.println("Chapter does not exist in the database\n");
      return false;
    }
    System.out.println("Chapter removed successfully\n");
    return true;
  }

  public static boolean addArticleTOC(int pubID, String issueTitle, String articleTitle){
    String query = "INSERT INTO Article VALUES (%d, '%s', '%s', NULL, NULL, NULL)";
    query = String.format(query, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add article to database\n");
      return false;
    }
    System.out.println("Article added successfully\n");
    return true;
  }

    // date written, text and topic can be null
  public static boolean editArticle(int pubID, String issueTitle, String articleTitle, java.sql.Date dateWritten, String text, String topic){
    String textValue = (text == null || text.isEmpty()) ? "NULL" : "'" + text + "'";
    String dateWrittenValue = (dateWritten == null || dateWritten.toString().isEmpty()) ? "NULL" : "'" + dateWritten.toString() + "'";
    String topicValue = (topic == null || topic.isEmpty()) ? "NULL" : "'" + topic + "'";
    String query = "UPDATE Article SET dateWritten = %s, text = %s, topic = %s WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
    query = String.format(query, dateWrittenValue, textValue, topicValue, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update article in database\n");
      return false;
    }
    System.out.println("Article updated successfully\n");
    return true;
  }

  public static boolean removeArticleTOC(long pubID, String issueTitle, String articleTitle){
    String query = "DELETE FROM WritesArticle WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
    query = String.format(query, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove article from database\n");
      return false;
    }
    query = "DELETE FROM Article WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
    query = String.format(query, pubID, issueTitle, articleTitle);
    if(DBManager.executeUpdateCount(query) <= 0){
      System.out.println("Article does not exist in the database\n");
      return false;
    }
    System.out.println("Article removed successfully\n");
    return true;
  }

  public static boolean findEditionsByTopic(String topic){
    String query = "SELECT * FROM Edition WHERE ISBN IN (SELECT ISBN FROM Chapter WHERE topic = '%s')";
    query = String.format(query, topic);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table != null && table.next()){
        do{
          long ISBN = table.getLong("ISBN");
          int edition = table.getInt("edition");
          String editionTitle = table.getString("editionTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          java.sql.Date datePublished = table.getDate("datePublished");
          double price = table.getDouble("price");
          System.out.println("ISBN: " + ISBN + ", Edition: " + edition + ", Edition Title: " + editionTitle + ", Date Written: " + dateWritten + ", Date Published: " + datePublished + ", Price: " + price);
        } while(table.next());
        return true;
      }
      else{
        System.out.println("Couldn't find books with given topic\n");
        return true;
      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;

    }
  }

    public static boolean findArticlesByTopic(String topic){
    String query = "SELECT * FROM Article WHERE topic = '%s'";
    query = String.format(query, topic);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table != null && table.next()){
        do{
          int pubID = table.getInt("pubID");
          String issueTitle = table.getString("issueTitle");
          String articleTitle = table.getString("articleTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          String text = table.getString("text");
          System.out.println("Publication ID: " + pubID + ", Issue Title: " + issueTitle + ", Article Title: " + articleTitle + ", Date Written: " + dateWritten + ", Text: " + text);
        }while(table.next());
        return true;
      
      }
      else{
        System.out.println("Couldn't find articles with given topic\n");
        return true;
  

      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;

    }
    }

  public static boolean findEditionsByDateRange(java.sql.Date startDate, java.sql.Date endDate){
    String query = "SELECT * FROM Edition WHERE datePublished BETWEEN '%s' AND '%s'";
    query = String.format(query, startDate, endDate);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table != null && table.next()){
        do {
          long ISBN = table.getLong("ISBN");
          int edition = table.getInt("edition");
          String editionTitle = table.getString("editionTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          java.sql.Date datePublished = table.getDate("datePublished");
          double price = table.getDouble("price");
          System.out.println("ISBN: " + ISBN + ", Edition: " + edition + ", Edition Title: " + editionTitle + ", Date Written: " + dateWritten + ", Date Published: " + datePublished + ", Price: " + price);
        } while(table.next());
      }
      else{
        System.out.println("Couldn't find books published in given date range\n");
        return false;

      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;
    }
    return true;
  }

  public static boolean findArticlesByDateRange(java.sql.Date startDate, java.sql.Date endDate){
    String query = "SELECT * FROM Article WHERE dateWritten BETWEEN '%s' AND '%s'";
    query = String.format(query, startDate, endDate);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table != null && table.next()){
        do{
          int pubID = table.getInt("pubID");
          String issueTitle = table.getString("issueTitle");
          String articleTitle = table.getString("articleTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          String text = table.getString("text");
          System.out.println("Publication ID: " + pubID + ", Issue Title: " + issueTitle + ", Article Title: " + articleTitle + ", Date Written: " + dateWritten + ", Text: " + text);
        }while(table.next());
        return true;
      }
      else{
        System.out.println("Couldn't find articles written in given date range\n");
        return true;

      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;
    }
  }

  //NEED to test when dummy data updated
  public static boolean findEditionsByAuthor(String authorName){
    String query = "SELECT * FROM Edition WHERE ISBN IN (SELECT ISBN FROM WritesChapter WHERE pID = (SELECT pID FROM Person WHERE name = '%s'))";
    query = String.format(query, authorName);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table != null && table.next()){
        do {
          long ISBN = table.getLong("ISBN");
          int edition = table.getInt("edition");
          String editionTitle = table.getString("editionTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          java.sql.Date datePublished = table.getDate("datePublished");
          double price = table.getDouble("price");
          System.out.println("ISBN: " + ISBN + ", Edition: " + edition + ", Edition Title: " + editionTitle + ", Date Written: " + dateWritten + ", Date Published: " + datePublished + ", Price: " + price);
        } while(table.next());
        return true;
      }
      else{
        System.out.println("Couldn't find books with given author\n");
        return true;
      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;

    }

  }

  // Need to test when dummy data updated
  public static boolean findArticlesByAuthor(String authorName){
    String query = "SELECT * FROM Article WHERE pubID IN (SELECT pubID FROM WritesArticle WHERE pID = (SELECT pID FROM Person WHERE name = '%s'))";
    query = String.format(query, authorName);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table != null && table.next()){
        do{
          int pubID = table.getInt("pubID");
          String issueTitle = table.getString("issueTitle");
          String articleTitle = table.getString("articleTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          String text = table.getString("text");
          System.out.println("Publication ID: " + pubID + ", Issue Title: " + issueTitle + ", Article Title: " + articleTitle + ", Date Written: " + dateWritten + ", Text: " + text);
        }while(table.next());
        return true;
      }
      else{
        System.out.println("Couldn't find articles with given author\n");
        return true;
      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;
    }

  }



  public static boolean compareIssueArticles(int pubID, String issueTitle1, String issueTitle2){
    String query = "SELECT Article.pubID, Article.issueTitle, Article.articleTitle, Article.topic, Article.dateWritten, Article.text FROM Article WHERE (Article.pubID=%d AND Article.issueTitle = '%s') OR (Article.pubID=%d AND Article.issueTitle = '%s') ORDER BY Article.issueTitle, Article.articleTitle";
    query = String.format(query, pubID, issueTitle1, pubID, issueTitle2);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table != null && table.next()){
           do{
        int publicationID = table.getInt("pubID");
        String issueTitle = table.getString("issueTitle");
        String articleTitle = table.getString("articleTitle");
        String topic = table.getString("topic");
        java.sql.Date dateWritten = table.getDate("dateWritten");
        String text = table.getString("text");
        System.out.println("Publication ID: " + publicationID + ", Issue Title: " + issueTitle + ", Article Title: " + articleTitle + ", Topic: " + topic + ", Date Written: " + dateWritten + ", Text: " + text);
    } while(table.next());
      return true;
    
  
      }
      else {
        System.out.println("Couldn't find articles for given issues\n");
        return true;
     


      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;
    }

  }

  public static boolean updateChapterAuthor(long ISBN, String chapterTitle, int pID, boolean invited){
    String query = "SELECT * FROM WritesChapter WHERE ISBN = %d AND chapterTitle = '%s'";
    query = String.format(query, ISBN, chapterTitle);
    ResultSet table = DBManager.executeQuery(query);
    try{
      // If chapter hasn't been given an author yet
      if(table == null || !table.next()){
        query = "INSERT INTO WritesChapter VALUES (%d, '%s', %d, %d)";
        query = String.format(query, pID, chapterTitle, ISBN, invited ? 1 : 0);
        if(!DBManager.executeUpdate(query)){
          System.out.println("Couldn't set chapter author in database\n");
          return false;
        }
        System.out.println("Chapter author set successfully\n");
        return true;
      }
      // If chapter has an author already, we must update
      else{
        query = "UPDATE WritesChapter SET pID = %d, invited = %d WHERE ISBN = %d AND chapterTitle = '%s'";
        query = String.format(query, pID, invited ? 1 : 0, ISBN, chapterTitle);
        if(!DBManager.executeUpdate(query)){
          System.out.println("Couldn't update chapter author in database\n");
          return false;
        }
        System.out.println("Chapter author updated successfully\n");
        return true;
      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;
    }
  }

  public static boolean updateArticleAuthor(int pubID, String issueTitle, String articleTitle, int pID, boolean invited){
    String query = "SELECT * FROM WritesArticle WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
    query = String.format(query, pubID, issueTitle, articleTitle);
    ResultSet table = DBManager.executeQuery(query);
    try{
      // If article hasn't been given an author yet
      if(table == null || !table.next()){
        query = "INSERT INTO WritesArticle VALUES (%d, %d, '%s', '%s', %d)";
        query = String.format(query, pID, pubID, articleTitle, issueTitle, invited ? 1 : 0);
        if(!DBManager.executeUpdate(query)){
          System.out.println("Couldn't set article author in database\n");
          return false;
        }
        System.out.println("Article author set successfully\n");
        return true;
      }
      // If article has an author already, we must update
      else{
        query = "UPDATE WritesArticle SET pID = %d, invited = %d WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
        query = String.format(query, pID, invited ? 1 : 0, pubID, issueTitle, articleTitle);
        if(!DBManager.executeUpdate(query)){
          System.out.println("Couldn't update article author in database\n");
          return false;
        }
        System.out.println("Article author updated successfully\n");
        return true;
      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;
    }
  }

}
