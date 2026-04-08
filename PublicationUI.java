import java.util.Scanner;

//TODO: Null handling
public class PublicationUI {
  public static void handleAddPublication(Scanner s){
    int pubID;String title; String type; String pubPeriodicity;
    System.out.println("Enter Publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter title (null if there is none): ");
    title = s.nextLine();
    System.out.println("Enter type: ");
    type = s.nextLine();
    System.out.println("Enter periodicity (null if NA): ");
    pubPeriodicity = s.nextLine();
    if(pubPeriodicity.equalsIgnoreCase("null")){
      pubPeriodicity = null;
    }
    Publication.addPublication(pubID, title, type, pubPeriodicity);
    

  }

  public static void handleUpdatePublication(Scanner s){

  int pubID; String title; String type; String pubPeriodicity;
  System.out.println("Enter Publication ID: ");
  pubID = s.nextInt();
  s.nextLine();
  System.out.println("Enter new title (null if there is none): ");
  title = s.nextLine();
  System.out.println("Enter new type: ");
  type = s.nextLine();
  System.out.println("Enter new periodicity (null if NA): ");
  pubPeriodicity = s.nextLine();
  Publication.updatePublication(pubID, title, type, pubPeriodicity);
}

  public static void handleRemovePublication(Scanner s){
    int pubID;
    System.out.println("Enter Publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    Publication.removePublication(pubID);
  }


  public static void handleAddBookEdition(Scanner s){
    int pubID;
    long ISBN; 
    int edition; 
    String editionTitle; 
    java.sql.Date dateWritten;
    java.sql.Date datePublished;
    double price;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    System.out.println("Enter edition number: ");
    edition = s.nextInt();
    s.nextLine();
    System.out.println("Enter edition title (null if there is none): ");
    editionTitle = s.nextLine();
    System.out.println("Enter date written (YYYY-MM-DD): ");
    dateWritten = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter date published (YYYY-MM-DD): ");
    datePublished = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter price: ");
    price = s.nextDouble();
    s.nextLine();
    Publication.addBookEdition(pubID, ISBN, edition, editionTitle, dateWritten, datePublished, price);    
  }

  public static void handleUpdateBookEdition(Scanner s){
    long ISBN; int edition; String editionTitle; java.sql.Date dateWritten; java.sql.Date datePublished; double price;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();  
    s.nextLine();    
    System.out.println("Enter new edition number: ");
    edition = s.nextInt();
    s.nextLine();
    System.out.println("Enter new edition title: ");
    editionTitle = s.nextLine();
    System.out.println("Enter new date written (YYYY-MM-DD): ");
    dateWritten = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter new date published (YYYY-MM-DD): ");
    datePublished = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter new price: ");
    price = s.nextDouble();
    s.nextLine();
    Publication.updateBookEdition(ISBN, edition, editionTitle, dateWritten, datePublished, price);
  }

  public static void handleRemoveBookEdition(Scanner s){
    long ISBN;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    Publication.removeBookEdition(ISBN);
  }

  public static void handleAddIssue(Scanner s){
    int pubID; String issueTitle; java.sql.Date pubDate; double price;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter publication date (YYYY-MM-DD): ");
    pubDate = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter price: ");
    price = s.nextDouble();
    s.nextLine();
    Publication.addIssue(pubID, issueTitle, pubDate, price);

  }

  public static void handleEditIssue(Scanner s){
    int pubID; String issueTitle; java.sql.Date pubDate; double price;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter new publication date (YYYY-MM-DD): ");
    pubDate = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter new price: ");
    price = s.nextDouble();
    s.nextLine();
    Publication.editIssue(pubID, issueTitle, pubDate, price);
    
  }

  public static void handleRemoveIssue(Scanner s){
    int pubID;String issueTitle;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    Publication.removeIssue(pubID, issueTitle);
    
  }



  public static void handleAddChapterTOC(Scanner s){
    long ISBN; String chapterTitle;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    System.out.println("Enter chapter title: ");
    chapterTitle = s.nextLine();
    Publication.addChapterTOC(ISBN, chapterTitle);
    
  }

  public static void handleEditChapter(Scanner s){
      long ISBN; String chapterTitle; java.sql.Date date; String text; String topic;
      System.out.println("Enter ISBN: ");
      ISBN = s.nextLong();
      s.nextLine();
      System.out.println("Enter chapter title: ");
      chapterTitle = s.nextLine();
      System.out.println("Enter new date (YYYY-MM-DD): ");
      date = java.sql.Date.valueOf(s.nextLine());
      System.out.println("Enter new text: ");
      text = s.nextLine();
      System.out.println("Enter new topic: ");
      topic = s.nextLine();
      Publication.editChapter(ISBN, chapterTitle, date, text, topic);
    
  }

  public static void handleRemoveChapterTOC(Scanner s){
    long ISBN; String chapterTitle;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    System.out.println("Enter chapter title: ");
    chapterTitle = s.nextLine();
    Publication.removeChapterTOC(ISBN, chapterTitle);
    
  }

  public static void handleAddArticleTOC(Scanner s){
    int pubID; String issueTitle; String articleTitle;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
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
    s.nextLine();
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
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter article title: ");
    articleTitle = s.nextLine();
    Publication.removeArticleTOC(pubID, issueTitle, articleTitle);
    
  }

  public static void handleFindEditionsByTopic(Scanner s){
    String topic;
    System.out.println("Enter topic: ");
    topic = s.nextLine();
    Publication.findEditionsByTopic(topic);
  }

  public static void handleFindArticlesByTopic(Scanner s){
    String topic;
    System.out.println("Enter topic: ");
    topic = s.nextLine();
    Publication.findArticlesByTopic(topic);
  }

  public static void handleFindEditionsByDateRange(Scanner s){
    java.sql.Date startDate; java.sql.Date endDate;
    System.out.println("Enter start date (YYYY-MM-DD): ");
    startDate = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter end date (YYYY-MM-DD): ");
    endDate = java.sql.Date.valueOf(s.nextLine());
    Publication.findEditionsByDateRange(startDate, endDate);
  }

  public static void handleFindArticlesByDateRange(Scanner s){
    java.sql.Date startDate; java.sql.Date endDate;
    System.out.println("Enter start date (YYYY-MM-DD): ");
    startDate = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter end date (YYYY-MM-DD): ");
    endDate = java.sql.Date.valueOf(s.nextLine());
    Publication.findArticlesByDateRange(startDate, endDate);
  }

  public static void handleFindEditionsByAuthor(Scanner s){
    String authorName;
    System.out.println("Enter author name: ");
    authorName = s.nextLine();
    Publication.findEditionsByAuthor(authorName);
  }

  public static void handleFindArticlesByAuthor(Scanner s){
    String authorName;
    System.out.println("Enter author name: ");
    authorName = s.nextLine();
    Publication.findArticlesByAuthor(authorName);
  }

  public static void handleCompareIssueArticles(Scanner s){
    int pubID; String issueTitle1; String issueTitle2;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter first issue title: ");
    issueTitle1 = s.nextLine();
    System.out.println("Enter second issue title: ");
    issueTitle2 = s.nextLine();
    Publication.compareIssueArticles(pubID, issueTitle1, issueTitle2);
  }
  
}
