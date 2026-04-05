/** Assert fields are proper length = titles, ISBN, type etc */

//TODO: Update contains in ui and here
// What to do with true false?
// Can or cant update publication that a book was part of? 
public class Publication{
  public static boolean addPublication(int pubID, String title, String type, String pubPeriodicity){
    String query = "INSERT INTO Publication VALUES (%d, '%s', '%s', '%s')";
    query = String.format(query, pubID, title, type, pubPeriodicity);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add publication to database");
      return false;
    }

  return true;
}

  public static boolean updatePublication(int pubID, String title, String type, String pubPeriodicity){
    String query = "UPDATE Publication SET title = '%s', type = '%s', pubPeriodicity = '%s' WHERE pubID = %d";
    query = String.format(query, title, type, pubPeriodicity, pubID);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update publication in database");
      return false;
    }
    return true;
  }

  public static boolean removePublication(int pubID){
    String query = "DELETE FROM Publication WHERE pubID = %d";
    query = String.format(query, pubID);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove publication from database");
      return false;
    }
    return true;
  }

  public static boolean addBookEdition(int pubID, long ISBN, String edition, String editionTitle, java.sql.Date dateWritten, java.sql.Date datePublished, double price){
   String query = "INSERT INTO Edition VALUES (%d, '%s', '%s', '%s', '%s', %f)";
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
  return true;
}

  public static boolean updateBookEdition(long ISBN, String edition, String editionTitle, java.sql.Date dateWritten, java.sql.Date datePublished, double price){
    String query = "UPDATE Edition SET edition = '%s', editionTitle = '%s', dateWritten = '%s', datePublished = '%s', price = %f WHERE ISBN = %d";
    query = String.format(query, edition, editionTitle, dateWritten, datePublished, price, ISBN);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update book edition in database");
      return false;
    }
    return true;
  }

  public static boolean removeBookEdition(long ISBN){
    String query = "DELETE FROM Edition WHERE ISBN = %d";
    query = String.format(query, ISBN);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove book edition from database");
      return false;
    }
    query = "DELETE FROM ISBNPublication WHERE ISBN = %d";
    query = String.format(query, ISBN);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove edition-publication link from database");
      return false;
    }
    return true;
  }

  public static boolean addIssue(int pubID, String issueTitle, java.sql.Date pubDate, double price){
    String query = "INSERT INTO Issue VALUES (%d, '%s', '%s', %f)";
    query = String.format(query, pubID, issueTitle, pubDate, price);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add issue to database"); 
      return false;
    }
    return true;
  }

  public static boolean editIssue(int pubID, String issueTitle, java.sql.Date pubDate, double price){
    String query = "UPDATE Issue SET pubDate = '%s', price = %f WHERE pubID = %d AND issueTitle = '%s'";
    query = String.format(query, pubDate, price, pubID, issueTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update issue in database");
      return false;
    }
    return true;
  }

  public static boolean removeIssue(int pubID, String issueTitle){
    String query = "DELETE FROM Issue WHERE pubID = %d AND issueTitle = '%s'";
    query = String.format(query, pubID, issueTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove issue from database");
      return false;
    }
    return true;
  }



  public static boolean addChapterTOC(long ISBN, String chapterTitle){
    String query = "INSERT INTO Chapter VALUES (%d, '%s', NULL, NULL, NULL)";
    query = String.format(query, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add chapter to database");
      return false;
    }
    return true;
  }

  public static boolean editChapter(int pubID, long ISBN, String chapterTitle, java.sql.Date date, String text, String topic){
    String query = "UPDATE Chapter SET date = '%s', text = '%s', topic = '%s' WHERE ISBN = %d AND chapterTitle = '%s'";
    query = String.format(query, date, text, topic, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update chapter in database");
      return false;
    }
    return true;
  }

  public static boolean removeChapterTOC(long ISBN, String chapterTitle){
    String query = "DELETE FROM Chapter WHERE ISBN = %d AND chapterTitle = '%s'";
    query = String.format(query, ISBN, chapterTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove chapter from database");
      return false;
    }
    return true;
  }

  public static boolean addArticleTOC(int pubID, String issueTitle, String articleTitle){
    String query = "INSERT INTO Article VALUES (%d, '%s', '%s', NULL, NULL, NULL)";
    query = String.format(query, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't add article to database");
      return false;
    }
    return true;
  }

  public static boolean editArticle(int pubID, String issueTitle, String articleTitle, java.sql.Date dateWritten, String text, String topic){
    String query = "UPDATE Article SET dateWritten = '%s', text = '%s', topic = '%s' WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
    query = String.format(query, dateWritten, text, topic, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't update article in database");
      return false;
    }
    return true;
  }

  public static boolean removeArticleTOC(long pubID, String issueTitle, String articleTitle){
    String query = "DELETE FROM Article WHERE pubID = %d AND issueTitle = '%s' AND articleTitle = '%s'";
    query = String.format(query, pubID, issueTitle, articleTitle);
    if(!DBManager.executeUpdate(query)){
      System.out.println("Couldn't remove article from database");  
      return false;
    }
    return true;
  }







}
