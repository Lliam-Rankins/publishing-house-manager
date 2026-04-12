import java.util.Scanner;

//TODO: Null handling
public class PublicationUI {
  public static void handleAddPublication(Scanner s) {
    int pubID = 0;
    String title = "";
    String type = "";
    String pubPeriodicity = "";
    boolean pubIDValid = false;
    boolean ISBNValid = false;
    boolean dwValid = false;
    boolean dpValid = false;
    boolean priceValid = false;
    boolean typeValid = false;
    boolean editionValid = false;

    System.out.println("Enter Publication ID (integer): ");
    while (!pubIDValid) {
      try {
        pubID = s.nextInt();
        pubIDValid = true;
      } catch (Exception e) {
        System.out.println("Invalid input for Publication ID. Please enter an integer.\n");
        s.nextLine();
      }
    }
    s.nextLine();
    System.out.println("Enter title (press enter if there is none): ");
    title = s.nextLine();
    System.out.println("Enter type: ");
    while (!typeValid) {
      type = s.nextLine();
      if (type.equalsIgnoreCase("book") || type.equalsIgnoreCase("magazine") || type.equalsIgnoreCase("journal")) {
        typeValid = true;
      } else {
        System.out.println("Invalid input for type. Please enter 'Book', 'Magazine', or 'Journal'.\n");
      }
    }
    System.out.println("Enter periodicity (press enter if publication is a book): ");
    pubPeriodicity = s.nextLine();
    if (pubPeriodicity.equalsIgnoreCase("")) {
      pubPeriodicity = null;
      System.out.println("Would you like to add this publication as an edition of a book? y/n");
      String response = s.nextLine();
      if (response.equalsIgnoreCase("y")) {
        long ISBN = 0;
        while (!ISBNValid) {
          System.out.println("Enter ISBN (long integer, 13 digits): ");
          try {
            ISBN = s.nextLong();
            if (String.valueOf(ISBN).length() != 13) {
              System.out.println("Invalid input for ISBN. Please enter a long integer with 13 digits.\n");
            } else {
              ISBNValid = true;
            }
          } catch (Exception e) {
            System.out.println("Invalid input for ISBN. Please enter a long integer.\n");
            s.nextLine();
          }
        }
        s.nextLine();
        System.out.println("Enter edition number: ");
        int edition = 0;
        while (!editionValid) {
          try {
            edition = s.nextInt();
            editionValid = true;
          } catch (Exception e) {
            System.out.println("Invalid input for edition number. Please enter an integer.\n");
            s.nextLine();
          }
        }
        s.nextLine();
        System.out.println("Enter edition title (enter if there is none): ");
        String editionTitle = s.nextLine();
        System.out.println("Enter date written (YYYY-MM-DD) (enter if there is none): ");
        String dateWrittenStr = s.nextLine();
        java.sql.Date dateWritten = null;
        while (!dwValid) {
          if (dateWrittenStr.isEmpty()) {
            dateWritten = null;
            dwValid = true;
          } else {
            try {
              dateWritten = java.sql.Date.valueOf(dateWrittenStr);
              dwValid = true;
            } catch (Exception e) {
              System.out.println(
                  "Invalid input for date written. Please enter a date in the format YYYY-MM-DD or press enter if there is none.\n");
              dateWrittenStr = s.nextLine();
            }
          }
        }
        System.out.println("Enter date published (YYYY-MM-DD) (enter if there is none): ");
        String datePublishedStr = s.nextLine();
        java.sql.Date datePublished = null;
        while (!dpValid) {
          if (datePublishedStr.isEmpty()) {
            datePublished = null;
            dpValid = true;
          } else {
            try {
              datePublished = java.sql.Date.valueOf(datePublishedStr);
              dpValid = true;
            } catch (Exception e) {
              System.out.println(
                  "Invalid input for date published. Please enter a date in the format YYYY-MM-DD or press enter if there is none.\n");
              datePublishedStr = s.nextLine();
            }
          }
        }
        System.out.println("Enter price (use -1 if there is none) : ");
        double price = 0;
        while (!priceValid) {
          try {
            price = s.nextDouble();
            if (price < 0 && price != -1) {
              System.out
                  .println("Invalid input for price. Please enter a non-negative number or -1 if there is none.\n");
              priceValid = false;
            } else {
              priceValid = true;
            }
          } catch (Exception e) {
            System.out.println("Invalid input for price. Please enter a double or -1 if there is none.\n");
            s.nextLine();
          }

        }

        s.nextLine();
        Publication.addEditionPublication(pubID, title, type, ISBN, edition, editionTitle, dateWritten, datePublished,
            price);
      } else {
        Publication.addPublication(pubID, title, type, pubPeriodicity);

      }
    } else {
      Publication.addPublication(pubID, title, type, pubPeriodicity);
    }
  }

  public static void handleUpdatePublication(Scanner s) {

    int pubID;
    String title;
    String type;
    String pubPeriodicity;
    System.out.println("Enter Publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter new title (enter if there is none): ");
    title = s.nextLine();
    System.out.println("Enter new type: ");
    type = s.nextLine();
    System.out.println("Enter new periodicity (enter if there is none): ");
    pubPeriodicity = s.nextLine();
    Publication.updatePublication(pubID, title, type, pubPeriodicity);
  }

  public static void handleRemovePublication(Scanner s) {
    int pubID;
    System.out.println("Enter Publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    Publication.removePublication(pubID);
  }

  public static void handleAddBookEditionToExistingPub(Scanner s) {
    int pubID;
    long ISBN;
    int edition;
    String editionTitle;
    String dateWrittenStr;
    String datePublishedStr;
    java.sql.Date dateWritten;
    java.sql.Date datePublished;
    double price;
    System.out.println("Enter publication ID (must have null periodicity): ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    System.out.println("Enter edition number: ");
    edition = s.nextInt();
    s.nextLine();
    System.out.println("Enter edition title (enter if there is none): ");
    editionTitle = s.nextLine();
    System.out.println("Enter date written (YYYY-MM-DD) (enter if there is none): ");
    dateWrittenStr = s.nextLine();
    if (!dateWrittenStr.isEmpty()) {
      dateWritten = java.sql.Date.valueOf(dateWrittenStr);
    } else {
      dateWritten = null;
    }
    System.out.println("Enter date published (YYYY-MM-DD) (enter if there is none): ");
    datePublishedStr = s.nextLine();
    if (!datePublishedStr.isEmpty()) {
      datePublished = java.sql.Date.valueOf(datePublishedStr);
    } else {
      datePublished = null;
    }
    System.out.println("Enter price (use -1 if there is none): ");
    price = s.nextDouble();
    s.nextLine();
    Publication.addBookEditionToExistingPub(pubID, ISBN, edition, editionTitle, dateWritten, datePublished, price);
  }

  public static void handleUpdateBookEdition(Scanner s) {
    long ISBN;
    int edition;
    String editionTitle;
    java.sql.Date dateWritten;
    java.sql.Date datePublished;
    String dateWrittenStr;
    String datePublishedStr;
    double price;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    System.out.println("Enter new edition number: ");
    edition = s.nextInt();
    s.nextLine();
    System.out.println("Enter new edition title (enter if there is none): ");
    editionTitle = s.nextLine();
    System.out.println("Enter new date written (YYYY-MM-DD) (enter if there is none): ");
    dateWrittenStr = s.nextLine();
    if (!dateWrittenStr.isEmpty()) {
      dateWritten = java.sql.Date.valueOf(dateWrittenStr);
    } else {
      dateWritten = null;
    }
    System.out.println("Enter new date published (YYYY-MM-DD) (enter if there is none): ");
    datePublishedStr = s.nextLine();
    if (!datePublishedStr.isEmpty()) {
      datePublished = java.sql.Date.valueOf(datePublishedStr);
    } else {
      datePublished = null;
    }
    System.out.println("Enter new price (use -1 if there is none): ");
    price = s.nextDouble();
    s.nextLine();
    Publication.updateBookEdition(ISBN, edition, editionTitle, dateWritten, datePublished, price);
  }

  public static void handleRemoveBookEditionAndPublication(Scanner s) {
    int pubID;
    long ISBN;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    Publication.removeBookEditionAndPublication(pubID, ISBN);
  }

  public static void handleRemoveBookEditionNotPublication(Scanner s) {
    long ISBN;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    Publication.removeBookEditionNotPub(ISBN);
  }

  public static void handleAddIssue(Scanner s) {
    int pubID;
    String issueTitle;
    java.sql.Date pubDate;
    String pubDateStr;
    double price;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter publication date (YYYY-MM-DD) (enter if there is none): ");
    pubDateStr = s.nextLine();
    if (!pubDateStr.isEmpty()) {
      pubDate = java.sql.Date.valueOf(pubDateStr);
    } else {
      pubDate = null;
    }
    System.out.println("Enter price (use -1 if there is none): ");
    price = s.nextDouble();
    s.nextLine();
    Publication.addIssue(pubID, issueTitle, pubDate, price);

  }

  public static void handleEditIssue(Scanner s) {
    int pubID;
    String issueTitle;
    java.sql.Date pubDate;
    String pubDateStr;
    double price;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter new publication date (YYYY-MM-DD) (enter if there is none): ");
    pubDateStr = s.nextLine();
    if (!pubDateStr.isEmpty()) {
      pubDate = java.sql.Date.valueOf(pubDateStr);
    } else {
      pubDate = null;
    }
    System.out.println("Enter new price (use -1 if there is none): ");
    price = s.nextDouble();
    s.nextLine();
    Publication.editIssue(pubID, issueTitle, pubDate, price);

  }

  public static void handleRemoveIssue(Scanner s) {
    int pubID;
    String issueTitle;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    Publication.removeIssue(pubID, issueTitle);

  }

  public static void handleAddChapterTOC(Scanner s) {
    long ISBN;
    String chapterTitle;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    System.out.println("Enter chapter title: ");
    chapterTitle = s.nextLine();
    Publication.addChapterTOC(ISBN, chapterTitle);

  }

  public static void handleEditChapter(Scanner s) {
    long ISBN;
    String chapterTitle;
    java.sql.Date date;
    String dateStr;
    String text;
    String topic;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    System.out.println("Enter chapter title: ");
    chapterTitle = s.nextLine();
    System.out.println("Enter new date (YYYY-MM-DD) (enter if there is none): ");
    dateStr = s.nextLine();
    if (!dateStr.isEmpty()) {
      date = java.sql.Date.valueOf(dateStr);
    } else {
      date = null;
    }
    System.out.println("Enter new text (enter if there is none): ");
    text = s.nextLine();
    System.out.println("Enter new topic (enter if there is none): ");
    topic = s.nextLine();
    Publication.editChapter(ISBN, chapterTitle, date, text, topic);

  }

  public static void handleRemoveChapterTOC(Scanner s) {
    long ISBN;
    String chapterTitle;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    System.out.println("Enter chapter title: ");
    chapterTitle = s.nextLine();
    Publication.removeChapterTOC(ISBN, chapterTitle);

  }

  public static void handleAddArticleTOC(Scanner s) {
    int pubID;
    String issueTitle;
    String articleTitle;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter article title: ");
    articleTitle = s.nextLine();
    Publication.addArticleTOC(pubID, issueTitle, articleTitle);

  }

  public static void handleEditArticle(Scanner s) {
    int pubID;
    String issueTitle;
    String articleTitle;
    java.sql.Date dateWritten;
    String dateWrittenStr;
    String text;
    String topic;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter article title: ");
    articleTitle = s.nextLine();
    System.out.println("Enter new date written (YYYY-MM-DD) (enter if there is none): ");
    dateWrittenStr = s.nextLine();
    if (!dateWrittenStr.isEmpty()) {
      dateWritten = java.sql.Date.valueOf(dateWrittenStr);
    } else {
      dateWritten = null;
    }
    System.out.println("Enter new text (enter if there is none): ");
    text = s.nextLine();
    System.out.println("Enter new topic (enter if there is none): ");
    topic = s.nextLine();
    Publication.editArticle(pubID, issueTitle, articleTitle, dateWritten, text, topic);

  }

  public static void handleRemoveArticleTOC(Scanner s) {
    long pubID;
    String issueTitle;
    String articleTitle;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter article title: ");
    articleTitle = s.nextLine();
    Publication.removeArticleTOC(pubID, issueTitle, articleTitle);

  }

  public static void handleFindEditionsByTopic(Scanner s) {
    String topic;
    System.out.println("Enter topic: ");
    topic = s.nextLine();
    Publication.findEditionsByTopic(topic);
  }

  public static void handleFindArticlesByTopic(Scanner s) {
    String topic;
    System.out.println("Enter topic: ");
    topic = s.nextLine();
    Publication.findArticlesByTopic(topic);
  }

  public static void handleFindEditionsByDateRange(Scanner s) {
    java.sql.Date startDate;
    java.sql.Date endDate;
    System.out.println("Enter start date (YYYY-MM-DD): ");
    startDate = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter end date (YYYY-MM-DD): ");
    endDate = java.sql.Date.valueOf(s.nextLine());
    Publication.findEditionsByDateRange(startDate, endDate);
  }

  public static void handleFindArticlesByDateRange(Scanner s) {
    java.sql.Date startDate;
    java.sql.Date endDate;
    System.out.println("Enter start date (YYYY-MM-DD): ");
    startDate = java.sql.Date.valueOf(s.nextLine());
    System.out.println("Enter end date (YYYY-MM-DD): ");
    endDate = java.sql.Date.valueOf(s.nextLine());
    Publication.findArticlesByDateRange(startDate, endDate);
  }

  public static void handleFindEditionsByAuthor(Scanner s) {
    String authorName;
    System.out.println("Enter author name: ");
    authorName = s.nextLine();
    Publication.findEditionsByAuthor(authorName);
  }

  public static void handleFindArticlesByAuthor(Scanner s) {
    String authorName;
    System.out.println("Enter author name: ");
    authorName = s.nextLine();
    Publication.findArticlesByAuthor(authorName);
  }

  public static void handleCompareIssueArticles(Scanner s) {
    int pubID1;
    String issueTitle1;
    int pubID2;
    String issueTitle2;
    System.out.println("Enter first publication ID: ");
    pubID1 = s.nextInt();
    s.nextLine();
    System.out.println("Enter first issue title: ");
    issueTitle1 = s.nextLine();
    System.out.println("Enter second publication ID: ");
    pubID2 = s.nextInt();
    s.nextLine();
    System.out.println("Enter second issue title: ");
    issueTitle2 = s.nextLine();
    Publication.compareIssueArticles(pubID1, issueTitle1, pubID2, issueTitle2);
  }

  public static void handleUpdateChapterAuthor(Scanner s) {
    long ISBN;
    String chapterTitle;
    int pID;
    boolean invited;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    System.out.println("Enter chapter title: ");
    chapterTitle = s.nextLine();
    System.out.println("Enter author pID: ");
    pID = s.nextInt();
    s.nextLine();
    System.out.println("Is the author invited? (true/false): ");
    invited = s.nextBoolean();
    s.nextLine();
    Publication.updateChapterAuthor(ISBN, chapterTitle, pID, invited);
  }

  public static void handleRemoveChapterAuthor(Scanner s) {
    long ISBN;
    String chapterTitle;
    System.out.println("Enter ISBN: ");
    ISBN = s.nextLong();
    s.nextLine();
    System.out.println("Enter chapter title: ");
    chapterTitle = s.nextLine();
    Publication.removeChapterAuthor(ISBN, chapterTitle);
  }

  public static void handleUpdateArticleAuthor(Scanner s) {
    int pubID;
    String issueTitle;
    String articleTitle;
    int pID;
    boolean invited;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter article title: ");
    articleTitle = s.nextLine();
    System.out.println("Enter author pID: ");
    pID = s.nextInt();
    s.nextLine();
    System.out.println("Is the author invited? (true/false): ");
    invited = s.nextBoolean();
    s.nextLine();
    Publication.updateArticleAuthor(pubID, issueTitle, articleTitle, pID, invited);

  }

  public static void handleRemoveArticleAuthor(Scanner s) {
    int pubID;
    String issueTitle;
    String articleTitle;
    System.out.println("Enter publication ID: ");
    pubID = s.nextInt();
    s.nextLine();
    System.out.println("Enter issue title: ");
    issueTitle = s.nextLine();
    System.out.println("Enter article title: ");
    articleTitle = s.nextLine();
    Publication.removeArticleAuthor(pubID, issueTitle, articleTitle);
  }
}
