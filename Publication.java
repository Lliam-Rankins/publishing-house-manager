import java.sql.ResultSet;
import java.sql.SQLException;

/** Assert fields are proper length = titles, ISBN, type etc */

//TODO: 
 // NEED WRITESCHAPTER AND WRITES ARTICLE UPDATES
// Update contains(pubID containing an ISBN) in ui and here with transaction
// Null value handling and UI
// do we want add chapter to contain chapter details or not?
// same for articles
// State character limits
// Incorrect date format handling

// PROMPT USER IF THEY WANT IT TO BE AN EDITION OR NOT
// WHEN ADDING EDITION SEPARATELY< MAKE SURE PUBID IS SAVED FOR IT



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
    DBManager.commitTransaction();
    return true;
  }

  // Updating just the publication details
    //title, pub periodicity can be null
  public static boolean updatePublication(int pubID, String title, String type, String pubPeriodicity){
    String query = "UPDATE Publication SET title = '%s', type = '%s', pubPeriodicity = '%s' WHERE pubID = %d";
    query = String.format(query, title, type, pubPeriodicity, pubID);
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
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove publication from database\n");
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
    String query = "SELECT * FROM Publication WHERE pubID = %d";
    query = String.format(query, pubID);
    try {
      if(!DBManager.executeQuery(query).next()){
        System.out.println("Publication not found\n");
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
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove edition-publication link from database\n");
      DBManager.rollbackTransaction();
      return false;
    }
    
    query = "DELETE FROM Edition WHERE ISBN = %d";
    query = String.format(query, ISBN);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove book edition from database\n");
      DBManager.rollbackTransaction();
      return false;
    }

    query = "DELETE FROM Publication WHERE pubID = %d";
    query = String.format(query, pubID);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove publication from database\n");
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
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove issue from database\n");
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
    String query = "UPDATE Chapter SET date = '%s', text = '%s', topic = '%s' WHERE ISBN = %d AND chapterTitle = '%s'";
    query = String.format(query, dateValue, textValue, topicValue, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update chapter in database\n");
      return false;
    }
    System.out.println("Chapter updated successfully\n");
    return true;
  }

  public static boolean removeChapterTOC(long ISBN, String chapterTitle){
    String query = "DELETE FROM Chapter WHERE ISBN = %d AND chapterTitle = '%s'";
    query = String.format(query, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove chapter from database\n");
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
    String query = "UPDATE Article SET dateWritten = '%s', text = '%s', topic = '%s' WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
    query = String.format(query, dateWrittenValue, textValue, topicValue, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update article in database\n");
      return false;
    }
    System.out.println("Article updated successfully\n");
    return true;
  }

  public static boolean removeArticleTOC(long pubID, String issueTitle, String articleTitle){
    String query = "DELETE FROM Article WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
    query = String.format(query, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove article from database\n");
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
      if(table == null){
        System.out.println("Couldn't find books with given topic\n");
        return true;
      }
      else{
        while(table.next()){
          long ISBN = table.getLong("ISBN");
          int edition = table.getInt("edition");
          String editionTitle = table.getString("editionTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          java.sql.Date datePublished = table.getDate("datePublished");
          double price = table.getDouble("price");
          System.out.println("ISBN: " + ISBN + ", Edition: " + edition + ", Edition Title: " + editionTitle + ", Date Written: " + dateWritten + ", Date Published: " + datePublished + ", Price: " + price);
        }

      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;

    }


    System.out.println("Books found successfully\n");
    return true;
  }

    public static boolean findArticlesByTopic(String topic){
    String query = "SELECT * FROM Article WHERE topic = '%s'";
    query = String.format(query, topic);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table == null){
        System.out.println("Couldn't find articles with given topic\n");
        return true;
      }
      else{
        while(table.next()){
          int pubID = table.getInt("pubID");
          String issueTitle = table.getString("issueTitle");
          String articleTitle = table.getString("articleTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          String text = table.getString("text");
          System.out.println("Publication ID: " + pubID + ", Issue Title: " + issueTitle + ", Article Title: " + articleTitle + ", Date Written: " + dateWritten + ", Text: " + text);
        }

      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;

    }


    System.out.println("Articles found successfully\n");
    return true;
    }

  public static boolean findEditionsByDateRange(java.sql.Date startDate, java.sql.Date endDate){
    String query = "SELECT * FROM Edition WHERE datePublished BETWEEN '%s' AND '%s'";
    query = String.format(query, startDate, endDate);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table == null){
        System.out.println("Couldn't find books published in given date range\n");
        return true;
      }
      else{
        while(table.next()){
          long ISBN = table.getLong("ISBN");
          int edition = table.getInt("edition");
          String editionTitle = table.getString("editionTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          java.sql.Date datePublished = table.getDate("datePublished");
          double price = table.getDouble("price");
          System.out.println("ISBN: " + ISBN + ", Edition: " + edition + ", Edition Title: " + editionTitle + ", Date Written: " + dateWritten + ", Date Published: " + datePublished + ", Price: " + price);
        }

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
      if(table == null){
        System.out.println("Couldn't find articles written in given date range\n");
        return true;
      }
      else{
        while(table.next()){
          int pubID = table.getInt("pubID");
          String issueTitle = table.getString("issueTitle");
          String articleTitle = table.getString("articleTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          String text = table.getString("text");
          System.out.println("Publication ID: " + pubID + ", Issue Title: " + issueTitle + ", Article Title: " + articleTitle + ", Date Written: " + dateWritten + ", Text: " + text);
        }

      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;
    }
    return true;
  }

  //NEED to test when dummy data updated
  public static boolean findEditionsByAuthor(String authorName){
    String query = "SELECT * FROM Edition WHERE ISBN IN (SELECT ISBN FROM WritesChapter WHERE pID = (SELECT pID FROM Person WHERE name = '%s'))";
    query = String.format(query, authorName);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table == null){
        System.out.println("Couldn't find books with given author\n");
        return true;
      }
      else{
        while(table.next()){
          long ISBN = table.getLong("ISBN");
          int edition = table.getInt("edition");
          String editionTitle = table.getString("editionTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          java.sql.Date datePublished = table.getDate("datePublished");
          double price = table.getDouble("price");
          System.out.println("ISBN: " + ISBN + ", Edition: " + edition + ", Edition Title: " + editionTitle + ", Date Written: " + dateWritten + ", Date Published: " + datePublished + ", Price: " + price);
        }

      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;

    }
    return true;

  }

  // Need to test when dummy data updated
  public static boolean findArticlesByAuthor(String authorName){
    String query = "SELECT * FROM Article WHERE pubID IN (SELECT pubID FROM WritesArticle WHERE pID = (SELECT pID FROM Person WHERE name = '%s'))";
    query = String.format(query, authorName);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table == null){
        System.out.println("Couldn't find articles with given author\n");
        return true;
      }
      else{
        while(table.next()){
          int pubID = table.getInt("pubID");
          String issueTitle = table.getString("issueTitle");
          String articleTitle = table.getString("articleTitle");
          java.sql.Date dateWritten = table.getDate("dateWritten");
          String text = table.getString("text");
          System.out.println("Publication ID: " + pubID + ", Issue Title: " + issueTitle + ", Article Title: " + articleTitle + ", Date Written: " + dateWritten + ", Text: " + text);
        }

      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;

    }
    return true;

  }

  public static boolean compareIssueArticles(int pubID, String issueTitle1, String issueTitle2){
    String query = "SELECT Article.pubID, Article.issueTitle, Article.articleTitle, Article.topic, Article.dateWritten, Article.text FROM Article WHERE (Article.pubID=%d AND Article.issueTitle = '%s') OR (Article.pubID=%d AND Article.issueTitle = '%s') ORDER BY Article.issueTitle, Article.articleTitle";
    query = String.format(query, pubID, issueTitle1, pubID, issueTitle2);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table == null){
        System.out.println("Couldn't find articles for given issues\n");
        return true;
      }
      else {
        while(table.next()){
        int publicationID = table.getInt("pubID");
        String issueTitle = table.getString("issueTitle");
        String articleTitle = table.getString("articleTitle");
        String topic = table.getString("topic");
        java.sql.Date dateWritten = table.getDate("dateWritten");
        String text = table.getString("text");
        System.out.println("Publication ID: " + publicationID + ", Issue Title: " + issueTitle + ", Article Title: " + articleTitle + ", Topic: " + topic + ", Date Written: " + dateWritten + ", Text: " + text);
    }


      }
    } catch(Exception e){
      System.out.println("Error processing results\n");
      return false;
    }

    return true;
  }

}
