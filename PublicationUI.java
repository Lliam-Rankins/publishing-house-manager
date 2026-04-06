import java.util.Scanner;

public class PublicationUI {
  public static void handleAddPublication(Scanner s){
    int pubID;String title; String type; String pubPeriodicity;
    System.out.println("Enter Publication ID: ");
    pubID = s.nextInt();
    System.out.println("Enter title: ");
    title = s.nextLine();
    System.out.println("Enter type: ");
    type = s.nextLine();
    System.out.println("Enter periodicity: ");
    pubPeriodicity = s.nextLine();
    Publication.addPublication(pubID, title, type, pubPeriodicity);
    

  }

  public static void handleUpdatePublication(Scanner s){

  int pubID; String title; String type; String pubPeriodicity;
  System.out.println("Enter Publication ID: ");
  pubID = s.nextInt();
  System.out.println("Enter new title: ");
  title = s.nextLine();
  System.out.println("Enter new type: ");
  type = s.nextLine();
  System.out.println("Enter new periodicity: ");
  pubPeriodicity = s.nextLine();
  Publication.updatePublication(pubID, title, type, pubPeriodicity);
}

  public static void handleRemovePublication(Scanner s){
    int pubID;
    System.out.println("Enter Publication ID: ");
    pubID = s.nextInt();
    Publication.removePublication(pubID);
  }


  public static void handleAddBookEdition(Scanner s){
    int pubID;
    long ISBN; 
    String edition; 
    String editionTitle; 
    java.sql.Date dateWritten;
    java.sql.Date datePublished;
    double price;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    System.out.println("Enter edition: ");
    edition = s.nextLine();
    System.out.println("Enter edition title: ");
    editionTitle = s.nextLine();
    System.out.println("Enter date written (YYYY-MM-DD): ");
    dateWritten = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter date published (YYYY-MM-DD): ");
    datePublished = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter price: ");
    price = s.nextDouble();
    Publication.addBookEdition(pubID, ISBN, edition, editionTitle, dateWritten, datePublished, price);    
  }

  public static void handleUpdateBookEdition(Scanner s){
    long ISBN; String edition; String editionTitle; java.sql.Date dateWritten; java.sql.Date datePublished; double price;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();      
    System.out.println("Enter new edition: ");
    edition = s.nextLine();
    System.out.println("Enter new edition title: ");
    editionTitle = s.nextLine();
    System.out.println("Enter new date written (YYYY-MM-DD): ");
    dateWritten = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter new date published (YYYY-MM-DD): ");
    datePublished = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter new price: ");
    price = s.nextDouble();
    Publication.updateBookEdition(ISBN, edition, editionTitle, dateWritten, datePublished, price);
  }

  public static void handleRemoveBookEdition(Scanner s){
    long ISBN;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    Publication.removeBookEdition(ISBN);
  }

  public static void handleAddIssue(Scanner s){
    int pubID; String issueTitle; java.sql.Date pubDate; double price;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter publication date (YYYY-MM-DD): ");
    pubDate = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter price: ");
    price = s.nextDouble();
    Publication.addIssue(pubID, issueTitle, pubDate, price);
  }

  public static void handleEditIssue(Scanner s){
    int pubID; String issueTitle; java.sql.Date pubDate; double price;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter new publication date (YYYY-MM-DD): ");
    pubDate = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter new price: ");
    price = s.nextDouble();
    Publication.editIssue(pubID, issueTitle, pubDate, price);
    
  }

  public static void handleRemoveIssue(Scanner s){
    int pubID;String issueTitle;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    Publication.removeIssue(pubID, issueTitle);
    
  }



  public static void handleAddChapterTOC(Scanner s){
    long ISBN; String chapterTitle;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    System.out.println("Enter chapter title: ");
    chapterTitle = s.nextLine();
    Publication.addChapterTOC(ISBN, chapterTitle);
    
  }

  public static void handleEditChapter(Scanner s){
    int pubID; long ISBN; String chapterTitle; java.sql.Date date; String text; String topic;
      System.out.println("Enter publication ID: ");
      pubID = s.nextInt();
      System.out.println("Enter ISBN: ");
      ISBN = s.nextLong();
      System.out.println("Enter chapter title: ");
      chapterTitle = s.nextLine();
      System.out.println("Enter new date (YYYY-MM-DD): ");
      date = java.sql.Date.valueOf(s.nextLine());
      System.out.println("Enter new text: ");
      text = s.nextLine();
      System.out.println("Enter new topic: ");
      topic = s.nextLine();
      Publication.editChapter(pubID, ISBN, chapterTitle, date, text, topic);
    
  }

  public static void handleRemoveChapterTOC(Scanner s){
    long ISBN; String chapterTitle;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    System.out.println("Enter chapter title: ");
    chapterTitle = s.nextLine();
    Publication.removeChapterTOC(ISBN, chapterTitle);
    
  }

  public static void handleAddArticleTOC(Scanner s){
    int pubID; String issueTitle; String articleTitle;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter article title: ");
    articleTitle = s.nextLine();
    Publication.addArticleTOC(pubID, issueTitle, articleTitle); 
    
  }

  public static void handleEditArticle(Scanner s){
    int pubID; String issueTitle; String articleTitle; java.sql.Date dateWritten; String text; String topic;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    System.out.println("Enter issue title: ");    
    issueTitle = s.nextLine();  
    System.out.println("Enter article title: ");
    articleTitle = s.nextLine();
    System.out.println("Enter new date written (YYYY-MM-DD): ");
    dateWritten = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter new text: ");
    text = s.nextLine();
    System.out.println("Enter new topic: ");
    topic = s.nextLine();
    Publication.editArticle(pubID, issueTitle, articleTitle, dateWritten, text, topic);
    
  }

  public static void handleRemoveArticleTOC(Scanner s){
    long pubID; String issueTitle; String articleTitle;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter article title: ");
    articleTitle = s.nextLine();
    Publication.removeArticleTOC(pubID, issueTitle, articleTitle);
    
  }
  
}
