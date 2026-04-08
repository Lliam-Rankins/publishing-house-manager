import java.sql.ResultSet;

/** Assert fields are proper length = titles, ISBN, type etc */

//TODO: 
 // NEED WRITESCHAPTER AND WRITES ARTICLE UPDATES
// Update contains(pubID containing an ISBN) in ui and here with transaction
// Null value handling
// do we want add chapter to contain chapter details or not?
// same for articles
// State character limits
// EXCEPTION HANDLING
// Incorrect date format handling



public class Publication{
  public static boolean addPublication(int pubID, String title, String type, String pubPeriodicity){
    String query = "INSERT INTO Publication VALUES (%d, '%s', '%s', '%s')";
    query = String.format(query, pubID, type, title, pubPeriodicity);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add publication to database");
      return false;
    }
    System.out.println("Publication added successfully");
    return true;
  }

  public static boolean updatePublication(int pubID, String title, String type, String pubPeriodicity){
    String query = "UPDATE Publication SET title = '%s', type = '%s', pubPeriodicity = '%s' WHERE pubID = %d";
    query = String.format(query, title, type, pubPeriodicity, pubID);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update publication in database");
      return false;
    }
    System.out.println("Publication updated successfully");
    
    return true;
  }

  public static boolean removePublication(int pubID){
    String query = "DELETE FROM Publication WHERE pubID = %d";
    query = String.format(query, pubID);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove publication from database");
      return false;
    }
    System.out.println("Publication removed successfully");
    
    return true;
  }

  public static boolean addBookEdition(int pubID, long ISBN, int edition, String editionTitle, java.sql.Date dateWritten, java.sql.Date datePublished, double price){
   String query = "INSERT INTO Edition VALUES (%d, %d, '%s', '%s', '%s', %f)";
   query = String.format(query, ISBN, edition, editionTitle, dateWritten, datePublished, price);
   if(!DBManager.executeUpdate(query)){
     System.out.println("Couldn't add book edition to database");
    return false;
    }
    query = "INSERT INTO ISBNPublication VALUES (%d, %d)";
    query = String.format(query, ISBN, pubID);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't link ISBN to publication");
      return false;
    }
    System.out.println("Book edition added successfully");
  
   return true;
}

  public static boolean updateBookEdition(long ISBN, int edition, String editionTitle, java.sql.Date dateWritten, java.sql.Date datePublished, double price){
    String query = "UPDATE Edition SET edition = %d, editionTitle = '%s', dateWritten = '%s', datePublished = '%s', price = %f WHERE ISBN = %d";
    query = String.format(query, edition, editionTitle, dateWritten, datePublished, price, ISBN);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update book edition in database");
      return false;
    }
    System.out.println("Book edition updated successfully");
    return true;
  }

  public static boolean removeBookEdition(long ISBN){
    String query = "DELETE FROM ISBNPublication WHERE ISBN = %d";
    query = String.format(query, ISBN);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove edition-publication link from database");
      return false;
    }
    
    query = "DELETE FROM Edition WHERE ISBN = %d";
    query = String.format(query, ISBN);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove book edition from database");
      return false;
    }

    System.out.println("Book edition removed successfully");
    return true;
  }

  public static boolean addIssue(int pubID, String issueTitle, java.sql.Date pubDate, double price){
    String query = "INSERT INTO Issue VALUES (%d, '%s', '%s', %f)";
    query = String.format(query, pubID, issueTitle, pubDate, price);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add issue to database"); 
      return false;
    }
    System.out.println("Issue added successfully");
    return true;
  }

  public static boolean editIssue(int pubID, String issueTitle, java.sql.Date pubDate, double price){
    String query = "UPDATE Issue SET pubDate = '%s', price = %f WHERE pubID = %d AND issueTitle = '%s'";
    query = String.format(query, pubDate, price, pubID, issueTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update issue in database");
      return false;
    }
    System.out.println("Issue updated successfully");
    return true;
  }

  public static boolean removeIssue(int pubID, String issueTitle){
    String query = "DELETE FROM Issue WHERE pubID = %d AND issueTitle = '%s'";
    query = String.format(query, pubID, issueTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove issue from database");
      return false;
    }
    System.out.println("Issue removed successfully");
    return true;
  }



  public static boolean addChapterTOC(long ISBN, String chapterTitle){
    String query = "INSERT INTO Chapter VALUES (%d, '%s', NULL, NULL, NULL)";
    query = String.format(query, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add chapter to database");
      return false;
    }
    System.out.println("Chapter added successfully");
    return true;
  }

  public static boolean editChapter(long ISBN, String chapterTitle, java.sql.Date date, String text, String topic){
    String query = "UPDATE Chapter SET date = '%s', text = '%s', topic = '%s' WHERE ISBN = %d AND chapterTitle = '%s'";
    query = String.format(query, date, text, topic, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update chapter in database");
      return false;
    }
    System.out.println("Chapter updated successfully");
    return true;
  }

  public static boolean removeChapterTOC(long ISBN, String chapterTitle){
    String query = "DELETE FROM Chapter WHERE ISBN = %d AND chapterTitle = '%s'";
    query = String.format(query, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove chapter from database");
      return false;
    }
    System.out.println("Chapter removed successfully");
    return true;
  }

  public static boolean addArticleTOC(int pubID, String issueTitle, String articleTitle){
    String query = "INSERT INTO Article VALUES (%d, '%s', '%s', NULL, NULL, NULL)";
    query = String.format(query, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add article to database");
      return false;
    }
    System.out.println("Article added successfully");
    return true;
  }

  public static boolean editArticle(int pubID, String issueTitle, String articleTitle, java.sql.Date dateWritten, String text, String topic){
    String query = "UPDATE Article SET dateWritten = '%s', text = '%s', topic = '%s' WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
    query = String.format(query, dateWritten, text, topic, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update article in database");
      return false;
    }
    System.out.println("Article updated successfully");
    return true;
  }

  public static boolean removeArticleTOC(long pubID, String issueTitle, String articleTitle){
    String query = "DELETE FROM Article WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
    query = String.format(query, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove article from database");  
      return false;
    }
    System.out.println("Article removed successfully");
    return true;
  }

  public static boolean findEditionsByTopic(String topic){
    String query = "SELECT * FROM Edition WHERE ISBN IN (SELECT ISBN FROM Chapter WHERE topic = '%s')";
    query = String.format(query, topic);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table == null){
        System.out.println("Couldn't find books with given topic");
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
      System.out.println("Error processing results");
      return false;

    }


    System.out.println("Books found successfully");
    return true;
  }

    public static boolean findArticlesByTopic(String topic){
    String query = "SELECT * FROM Article WHERE topic = '%s'";
    query = String.format(query, topic);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table == null){
        System.out.println("Couldn't find articles with given topic");
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
      System.out.println("Error processing results");
      return false;

    }


    System.out.println("Articles found successfully");
    return true;
    }

  public static boolean findEditionsByDateRange(java.sql.Date startDate, java.sql.Date endDate){
    String query = "SELECT * FROM Edition WHERE datePublished BETWEEN '%s' AND '%s'";
    query = String.format(query, startDate, endDate);
    ResultSet table = DBManager.executeQuery(query);
    try{
      if(table == null){
        System.out.println("Couldn't find books published in given date range");
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
      System.out.println("Error processing results");
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
        System.out.println("Couldn't find articles written in given date range");
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
      System.out.println("Error processing results");
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
        System.out.println("Couldn't find books with given author");
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
      System.out.println("Error processing results");
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
        System.out.println("Couldn't find articles with given author");
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
      System.out.println("Error processing results");
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
        System.out.println("Couldn't find articles for given issues");
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
      System.out.println("Error processing results");
      return false;
    }

    return true;
  }

}
