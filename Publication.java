/** Assert fields are proper length = titles, ISBN, type etc */
import java.sql.Time;

public static boolean addPublication(int pubID, String title, String type, String pubPeriodicity){

  return false;
}

public static boolean updatePublication(int pubID, String title, String type, String pubPeriodicity){
  return false;
}

public static boolean removePublication(int pubID){
  return false;
}

public static boolean addBookEdition(int pubID, long ISBN, String edition, String editionTitle, java.sql.Date datePublished){
  return false;
}

public static boolean updateBookEdition(int pubID, long ISBN, String edition, String editionTitle, java.sql.Date datePublished){
  return false;
}

public static boolean removeBookEdition(long ISBN){
  return false;
}

public static boolean addIssue(int pubID, String issueTitle, java.sql.Date pubDate, double price){
  return false;
}

public static boolean editIssue(int pubID, String issueTitle, java.sql.Date pubDate, double price){
  return false;
}

public static boolean removeIssue(int pubID, String issueTitle){
  return false;
}



public static boolean addChapterTOC(long ISBN, String chapterTitle){
  return false;
}

public static boolean editChapter(int pubID, long ISBN, String chapterTitle, java.sql.Date date, String text, String topic){
  return false;
}

public static boolean removeChapterTOC(long ISBN, String chapterTitle){
  return false;
}

public static boolean addArticleTOC(int pubID, String issueTitle, String articleTitle){
  return false;
}

public static boolean editArticle(int pubID, String issueTitle, String articleTitle, java.sql.Date dateWritten, String text, String topic){
  return false;
}

public static boolean removeArticleTOC(long pubID, String issueTitle, String articleTitle){
  return false;
}






