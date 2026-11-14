package publishinghouse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.Date;
import java.util.Map;

public class PublicationsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String result = null;

        if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> params = HttpUtils.parseFormData(exchange);
            String action = HttpUtils.getString(params, "action");

            if ("addPublication".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String title = HttpUtils.getString(params, "title");
                    String type = HttpUtils.getString(params, "type");
                    String pubPeriodicity = HttpUtils.getString(params, "pubPeriodicity");
                    Publication.addPublication(pubID, title, type, pubPeriodicity);
                });
            } else if ("addEditionPublication".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String title = HttpUtils.getString(params, "title");
                    String type = HttpUtils.getString(params, "type");
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    int edition = HttpUtils.getInt(params, "edition", 1);
                    String editionTitle = HttpUtils.getString(params, "editionTitle");
                    Date dateWritten = HttpUtils.getDate(params, "dateWritten");
                    Date datePublished = HttpUtils.getDate(params, "datePublished");
                    double price = HttpUtils.getDouble(params, "price", -1.0);
                    Publication.addEditionPublication(pubID, title, type, isbn, edition, editionTitle, dateWritten, datePublished, price);
                });
            } else if ("updatePublication".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String title = HttpUtils.getString(params, "title");
                    String type = HttpUtils.getString(params, "type");
                    String pubPeriodicity = HttpUtils.getString(params, "pubPeriodicity");
                    Publication.updatePublication(pubID, title, type, pubPeriodicity);
                });
            } else if ("removePublication".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    Publication.removePublication(pubID);
                });
            } else if ("addEditionToPub".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    int edition = HttpUtils.getInt(params, "edition", 1);
                    String editionTitle = HttpUtils.getString(params, "editionTitle");
                    Date dateWritten = HttpUtils.getDate(params, "dateWritten");
                    Date datePublished = HttpUtils.getDate(params, "datePublished");
                    double price = HttpUtils.getDouble(params, "price", -1.0);
                    Publication.addBookEditionToExistingPub(pubID, isbn, edition, editionTitle, dateWritten, datePublished, price);
                });
            } else if ("updateEdition".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    int edition = HttpUtils.getInt(params, "edition", 1);
                    String editionTitle = HttpUtils.getString(params, "editionTitle");
                    Date dateWritten = HttpUtils.getDate(params, "dateWritten");
                    Date datePublished = HttpUtils.getDate(params, "datePublished");
                    double price = HttpUtils.getDouble(params, "price", -1.0);
                    Publication.updateBookEdition(isbn, edition, editionTitle, dateWritten, datePublished, price);
                });
            } else if ("removeEditionAndPub".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    Publication.removeBookEditionAndPublication(pubID, isbn);
                });
            } else if ("removeEditionNotPub".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    Publication.removeBookEditionNotPub(isbn);
                });
            } else if ("addIssue".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String issueTitle = HttpUtils.getString(params, "issueTitle");
                    Date pubDate = HttpUtils.getDate(params, "pubDate");
                    double price = HttpUtils.getDouble(params, "price", -1.0);
                    Publication.addIssue(pubID, issueTitle, pubDate, price);
                });
            } else if ("editIssue".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String issueTitle = HttpUtils.getString(params, "issueTitle");
                    Date pubDate = HttpUtils.getDate(params, "pubDate");
                    double price = HttpUtils.getDouble(params, "price", -1.0);
                    Publication.editIssue(pubID, issueTitle, pubDate, price);
                });
            } else if ("removeIssue".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String issueTitle = HttpUtils.getString(params, "issueTitle");
                    Publication.removeIssue(pubID, issueTitle);
                });
            } else if ("addChapter".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    String chapterTitle = HttpUtils.getString(params, "chapterTitle");
                    Publication.addChapterTOC(isbn, chapterTitle);
                });
            } else if ("editChapter".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    String chapterTitle = HttpUtils.getString(params, "chapterTitle");
                    Date date = HttpUtils.getDate(params, "date");
                    String text = HttpUtils.getString(params, "text");
                    String topic = HttpUtils.getString(params, "topic");
                    Publication.editChapter(isbn, chapterTitle, date, text, topic);
                });
            } else if ("editChapterAuthor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    String chapterTitle = HttpUtils.getString(params, "chapterTitle");
                    int pID = HttpUtils.getInt(params, "pID", 0);
                    boolean invited = HttpUtils.getBoolean(params, "invited");
                    Publication.updateChapterAuthor(isbn, chapterTitle, pID, invited);
                });
            } else if ("removeChapterAuthor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    String chapterTitle = HttpUtils.getString(params, "chapterTitle");
                    Publication.removeChapterAuthor(isbn, chapterTitle);
                });
            } else if ("removeChapter".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    long isbn = HttpUtils.getLong(params, "isbn", 0L);
                    String chapterTitle = HttpUtils.getString(params, "chapterTitle");
                    Publication.removeChapterTOC(isbn, chapterTitle);
                });
            } else if ("addArticle".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String issueTitle = HttpUtils.getString(params, "issueTitle");
                    String articleTitle = HttpUtils.getString(params, "articleTitle");
                    Publication.addArticleTOC(pubID, issueTitle, articleTitle);
                });
            } else if ("editArticle".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String issueTitle = HttpUtils.getString(params, "issueTitle");
                    String articleTitle = HttpUtils.getString(params, "articleTitle");
                    Date dateWritten = HttpUtils.getDate(params, "dateWritten");
                    String text = HttpUtils.getString(params, "text");
                    String topic = HttpUtils.getString(params, "topic");
                    Publication.editArticle(pubID, issueTitle, articleTitle, dateWritten, text, topic);
                });
            } else if ("editArticleAuthor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String issueTitle = HttpUtils.getString(params, "issueTitle");
                    String articleTitle = HttpUtils.getString(params, "articleTitle");
                    int pID = HttpUtils.getInt(params, "pID", 0);
                    boolean invited = HttpUtils.getBoolean(params, "invited");
                    Publication.updateArticleAuthor(pubID, issueTitle, articleTitle, pID, invited);
                });
            } else if ("removeArticleAuthor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID = HttpUtils.getInt(params, "pubID", 0);
                    String issueTitle = HttpUtils.getString(params, "issueTitle");
                    String articleTitle = HttpUtils.getString(params, "articleTitle");
                    Publication.removeArticleAuthor(pubID, issueTitle, articleTitle);
                });
            } else if ("removeArticle".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    long pubID = HttpUtils.getLong(params, "pubID", 0L);
                    String issueTitle = HttpUtils.getString(params, "issueTitle");
                    String articleTitle = HttpUtils.getString(params, "articleTitle");
                    Publication.removeArticleTOC(pubID, issueTitle, articleTitle);
                });
            } else if ("findEditionsByTopic".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    String topic = HttpUtils.getString(params, "topic");
                    Publication.findEditionsByTopic(topic);
                });
            } else if ("findArticlesByTopic".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    String topic = HttpUtils.getString(params, "topic");
                    Publication.findArticlesByTopic(topic);
                });
            } else if ("findEditionsByDateRange".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    Date startDate = HttpUtils.getDate(params, "startDate");
                    Date endDate = HttpUtils.getDate(params, "endDate");
                    Publication.findEditionsByDateRange(startDate, endDate);
                });
            } else if ("findArticlesByDateRange".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    Date startDate = HttpUtils.getDate(params, "startDate");
                    Date endDate = HttpUtils.getDate(params, "endDate");
                    Publication.findArticlesByDateRange(startDate, endDate);
                });
            } else if ("findEditionsByAuthor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    String authorName = HttpUtils.getString(params, "authorName");
                    Publication.findEditionsByAuthor(authorName);
                });
            } else if ("findArticlesByAuthor".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    String authorName = HttpUtils.getString(params, "authorName");
                    Publication.findArticlesByAuthor(authorName);
                });
            } else if ("compareIssueArticles".equals(action)) {
                result = HttpUtils.executeWithCapture(() -> {
                    int pubID1 = HttpUtils.getInt(params, "pubID1", 0);
                    String issueTitle1 = HttpUtils.getString(params, "issueTitle1");
                    int pubID2 = HttpUtils.getInt(params, "pubID2", 0);
                    String issueTitle2 = HttpUtils.getString(params, "issueTitle2");
                    Publication.compareIssueArticles(pubID1, issueTitle1, pubID2, issueTitle2);
                });
            }
        }

        StringBuilder html = new StringBuilder();

        // 1. Add Publication
        html.append("<fieldset>\n<legend>Add Publication</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"addPublication\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Title: <input type=\"text\" name=\"title\"></label></p>\n");
        html.append("  <p><label>Type: \n");
        html.append("    <select name=\"type\">\n");
        html.append("      <option value=\"Book\">Book</option>\n");
        html.append("      <option value=\"Magazine\">Magazine</option>\n");
        html.append("      <option value=\"Journal\">Journal</option>\n");
        html.append("    </select>\n");
        html.append("  </label></p>\n");
        html.append("  <p><label>Periodicity (leave empty for books): <input type=\"text\" name=\"pubPeriodicity\"></label></p>\n");
        html.append("  <button type=\"submit\">Add Publication</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 2. Add Publication with Edition
        html.append("<fieldset>\n<legend>Add Publication with Book Edition</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"addEditionPublication\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Title: <input type=\"text\" name=\"title\"></label></p>\n");
        html.append("  <p><label>Type: <input type=\"text\" name=\"type\" value=\"Book\" required></label></p>\n");
        html.append("  <p><label>ISBN (13 digits): <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <p><label>Edition Number: <input type=\"number\" name=\"edition\" required></label></p>\n");
        html.append("  <p><label>Edition Title: <input type=\"text\" name=\"editionTitle\"></label></p>\n");
        html.append("  <p><label>Date Written (YYYY-MM-DD): <input type=\"date\" name=\"dateWritten\"></label></p>\n");
        html.append("  <p><label>Date Published (YYYY-MM-DD): <input type=\"date\" name=\"datePublished\"></label></p>\n");
        html.append("  <p><label>Price (-1 for none): <input type=\"number\" step=\"0.01\" name=\"price\" value=\"-1\"></label></p>\n");
        html.append("  <button type=\"submit\">Add Publication &amp; Edition</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 3. Update Publication
        html.append("<fieldset>\n<legend>Update Publication</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"updatePublication\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Title: <input type=\"text\" name=\"title\"></label></p>\n");
        html.append("  <p><label>Type: \n");
        html.append("    <select name=\"type\">\n");
        html.append("      <option value=\"Book\">Book</option>\n");
        html.append("      <option value=\"Magazine\">Magazine</option>\n");
        html.append("      <option value=\"Journal\">Journal</option>\n");
        html.append("    </select>\n");
        html.append("  </label></p>\n");
        html.append("  <p><label>Periodicity: <input type=\"text\" name=\"pubPeriodicity\"></label></p>\n");
        html.append("  <button type=\"submit\">Update Publication</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 4. Remove Publication
        html.append("<fieldset>\n<legend>Remove Publication</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"removePublication\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <button type=\"submit\">Remove Publication</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 5. Add Edition to Existing Publication
        html.append("<fieldset>\n<legend>Add Book Edition to Existing Publication</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"addEditionToPub\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>ISBN: <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <p><label>Edition Number: <input type=\"number\" name=\"edition\" required></label></p>\n");
        html.append("  <p><label>Edition Title: <input type=\"text\" name=\"editionTitle\"></label></p>\n");
        html.append("  <p><label>Date Written (YYYY-MM-DD): <input type=\"date\" name=\"dateWritten\"></label></p>\n");
        html.append("  <p><label>Date Published (YYYY-MM-DD): <input type=\"date\" name=\"datePublished\"></label></p>\n");
        html.append("  <p><label>Price (-1 for none): <input type=\"number\" step=\"0.01\" name=\"price\" value=\"-1\"></label></p>\n");
        html.append("  <button type=\"submit\">Add Edition to Publication</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 6. Update Edition
        html.append("<fieldset>\n<legend>Update Book Edition</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"updateEdition\">\n");
        html.append("  <p><label>ISBN: <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <p><label>New Edition Number: <input type=\"number\" name=\"edition\" required></label></p>\n");
        html.append("  <p><label>New Edition Title: <input type=\"text\" name=\"editionTitle\"></label></p>\n");
        html.append("  <p><label>New Date Written (YYYY-MM-DD): <input type=\"date\" name=\"dateWritten\"></label></p>\n");
        html.append("  <p><label>New Date Published (YYYY-MM-DD): <input type=\"date\" name=\"datePublished\"></label></p>\n");
        html.append("  <p><label>New Price (-1 for none): <input type=\"number\" step=\"0.01\" name=\"price\" value=\"-1\"></label></p>\n");
        html.append("  <button type=\"submit\">Update Edition</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 7. Remove Edition and Publication
        html.append("<fieldset>\n<legend>Remove Book Edition and Publication</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"removeEditionAndPub\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>ISBN: <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <button type=\"submit\">Remove Edition &amp; Publication</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 8. Remove Edition Only
        html.append("<fieldset>\n<legend>Remove Book Edition Only</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"removeEditionNotPub\">\n");
        html.append("  <p><label>ISBN: <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <button type=\"submit\">Remove Edition</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 9. Add Issue
        html.append("<fieldset>\n<legend>Add Issue</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"addIssue\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Issue Title: <input type=\"text\" name=\"issueTitle\" required></label></p>\n");
        html.append("  <p><label>Publication Date (YYYY-MM-DD): <input type=\"date\" name=\"pubDate\"></label></p>\n");
        html.append("  <p><label>Price (-1 for none): <input type=\"number\" step=\"0.01\" name=\"price\" value=\"-1\"></label></p>\n");
        html.append("  <button type=\"submit\">Add Issue</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 10. Edit Issue
        html.append("<fieldset>\n<legend>Edit Issue</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"editIssue\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Issue Title: <input type=\"text\" name=\"issueTitle\" required></label></p>\n");
        html.append("  <p><label>New Publication Date (YYYY-MM-DD): <input type=\"date\" name=\"pubDate\"></label></p>\n");
        html.append("  <p><label>New Price (-1 for none): <input type=\"number\" step=\"0.01\" name=\"price\" value=\"-1\"></label></p>\n");
        html.append("  <button type=\"submit\">Edit Issue</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 11. Remove Issue
        html.append("<fieldset>\n<legend>Remove Issue</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"removeIssue\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Issue Title: <input type=\"text\" name=\"issueTitle\" required></label></p>\n");
        html.append("  <button type=\"submit\">Remove Issue</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 12. Add Chapter TOC
        html.append("<fieldset>\n<legend>Add Chapter to TOC</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"addChapter\">\n");
        html.append("  <p><label>ISBN: <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <p><label>Chapter Title: <input type=\"text\" name=\"chapterTitle\" required></label></p>\n");
        html.append("  <button type=\"submit\">Add Chapter</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 13. Edit Chapter
        html.append("<fieldset>\n<legend>Edit Chapter</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"editChapter\">\n");
        html.append("  <p><label>ISBN: <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <p><label>Chapter Title: <input type=\"text\" name=\"chapterTitle\" required></label></p>\n");
        html.append("  <p><label>New Date (YYYY-MM-DD): <input type=\"date\" name=\"date\"></label></p>\n");
        html.append("  <p><label>New Text: <textarea name=\"text\" rows=\"3\" cols=\"40\"></textarea></label></p>\n");
        html.append("  <p><label>New Topic: <input type=\"text\" name=\"topic\"></label></p>\n");
        html.append("  <button type=\"submit\">Edit Chapter</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 14. Edit Chapter Author
        html.append("<fieldset>\n<legend>Update Chapter Author</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"editChapterAuthor\">\n");
        html.append("  <p><label>ISBN: <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <p><label>Chapter Title: <input type=\"text\" name=\"chapterTitle\" required></label></p>\n");
        html.append("  <p><label>Author ID (pID): <input type=\"number\" name=\"pID\" required></label></p>\n");
        html.append("  <p><label>Invited: <select name=\"invited\"><option value=\"true\">true</option><option value=\"false\">false</option></select></label></p>\n");
        html.append("  <button type=\"submit\">Update Chapter Author</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 15. Remove Chapter Author
        html.append("<fieldset>\n<legend>Remove Chapter Author</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"removeChapterAuthor\">\n");
        html.append("  <p><label>ISBN: <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <p><label>Chapter Title: <input type=\"text\" name=\"chapterTitle\" required></label></p>\n");
        html.append("  <button type=\"submit\">Remove Chapter Author</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 16. Remove Chapter TOC
        html.append("<fieldset>\n<legend>Remove Chapter from TOC</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"removeChapter\">\n");
        html.append("  <p><label>ISBN: <input type=\"number\" name=\"isbn\" required></label></p>\n");
        html.append("  <p><label>Chapter Title: <input type=\"text\" name=\"chapterTitle\" required></label></p>\n");
        html.append("  <button type=\"submit\">Remove Chapter</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 17. Add Article TOC
        html.append("<fieldset>\n<legend>Add Article to TOC</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"addArticle\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Issue Title: <input type=\"text\" name=\"issueTitle\" required></label></p>\n");
        html.append("  <p><label>Article Title: <input type=\"text\" name=\"articleTitle\" required></label></p>\n");
        html.append("  <button type=\"submit\">Add Article</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 18. Edit Article
        html.append("<fieldset>\n<legend>Edit Article</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"editArticle\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Issue Title: <input type=\"text\" name=\"issueTitle\" required></label></p>\n");
        html.append("  <p><label>Article Title: <input type=\"text\" name=\"articleTitle\" required></label></p>\n");
        html.append("  <p><label>New Date Written (YYYY-MM-DD): <input type=\"date\" name=\"dateWritten\"></label></p>\n");
        html.append("  <p><label>New Text: <textarea name=\"text\" rows=\"3\" cols=\"40\"></textarea></label></p>\n");
        html.append("  <p><label>New Topic: <input type=\"text\" name=\"topic\"></label></p>\n");
        html.append("  <button type=\"submit\">Edit Article</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 19. Edit Article Author
        html.append("<fieldset>\n<legend>Update Article Author</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"editArticleAuthor\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Issue Title: <input type=\"text\" name=\"issueTitle\" required></label></p>\n");
        html.append("  <p><label>Article Title: <input type=\"text\" name=\"articleTitle\" required></label></p>\n");
        html.append("  <p><label>Author ID (pID): <input type=\"number\" name=\"pID\" required></label></p>\n");
        html.append("  <p><label>Invited: <select name=\"invited\"><option value=\"true\">true</option><option value=\"false\">false</option></select></label></p>\n");
        html.append("  <button type=\"submit\">Update Article Author</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 20. Remove Article Author
        html.append("<fieldset>\n<legend>Remove Article Author</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"removeArticleAuthor\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Issue Title: <input type=\"text\" name=\"issueTitle\" required></label></p>\n");
        html.append("  <p><label>Article Title: <input type=\"text\" name=\"articleTitle\" required></label></p>\n");
        html.append("  <button type=\"submit\">Remove Article Author</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 21. Remove Article TOC
        html.append("<fieldset>\n<legend>Remove Article from TOC</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"removeArticle\">\n");
        html.append("  <p><label>Publication ID (pubID): <input type=\"number\" name=\"pubID\" required></label></p>\n");
        html.append("  <p><label>Issue Title: <input type=\"text\" name=\"issueTitle\" required></label></p>\n");
        html.append("  <p><label>Article Title: <input type=\"text\" name=\"articleTitle\" required></label></p>\n");
        html.append("  <button type=\"submit\">Remove Article</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 22. Find Editions by Topic
        html.append("<fieldset>\n<legend>Find Editions by Topic</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"findEditionsByTopic\">\n");
        html.append("  <p><label>Topic: <input type=\"text\" name=\"topic\" required></label></p>\n");
        html.append("  <button type=\"submit\">Search Editions</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 23. Find Articles by Topic
        html.append("<fieldset>\n<legend>Find Articles by Topic</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"findArticlesByTopic\">\n");
        html.append("  <p><label>Topic: <input type=\"text\" name=\"topic\" required></label></p>\n");
        html.append("  <button type=\"submit\">Search Articles</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 24. Find Editions by Date Range
        html.append("<fieldset>\n<legend>Find Editions by Date Range</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"findEditionsByDateRange\">\n");
        html.append("  <p><label>Start Date (YYYY-MM-DD): <input type=\"date\" name=\"startDate\" required></label></p>\n");
        html.append("  <p><label>End Date (YYYY-MM-DD): <input type=\"date\" name=\"endDate\" required></label></p>\n");
        html.append("  <button type=\"submit\">Search Editions</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 25. Find Articles by Date Range
        html.append("<fieldset>\n<legend>Find Articles by Date Range</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"findArticlesByDateRange\">\n");
        html.append("  <p><label>Start Date (YYYY-MM-DD): <input type=\"date\" name=\"startDate\" required></label></p>\n");
        html.append("  <p><label>End Date (YYYY-MM-DD): <input type=\"date\" name=\"endDate\" required></label></p>\n");
        html.append("  <button type=\"submit\">Search Articles</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 26. Find Editions by Author
        html.append("<fieldset>\n<legend>Find Editions by Author Name</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"findEditionsByAuthor\">\n");
        html.append("  <p><label>Author Name: <input type=\"text\" name=\"authorName\" required></label></p>\n");
        html.append("  <button type=\"submit\">Search Editions</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 27. Find Articles by Author
        html.append("<fieldset>\n<legend>Find Articles by Author Name</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"findArticlesByAuthor\">\n");
        html.append("  <p><label>Author Name: <input type=\"text\" name=\"authorName\" required></label></p>\n");
        html.append("  <button type=\"submit\">Search Articles</button>\n");
        html.append("</form>\n</fieldset><br>\n");

        // 28. Compare Issue Articles
        html.append("<fieldset>\n<legend>Compare Issue Articles</legend>\n");
        html.append("<form method=\"POST\" action=\"/publications\">\n");
        html.append("  <input type=\"hidden\" name=\"action\" value=\"compareIssueArticles\">\n");
        html.append("  <p><label>First Publication ID: <input type=\"number\" name=\"pubID1\" required></label></p>\n");
        html.append("  <p><label>First Issue Title: <input type=\"text\" name=\"issueTitle1\" required></label></p>\n");
        html.append("  <p><label>Second Publication ID: <input type=\"number\" name=\"pubID2\" required></label></p>\n");
        html.append("  <p><label>Second Issue Title: <input type=\"text\" name=\"issueTitle2\" required></label></p>\n");
        html.append("  <button type=\"submit\">Compare Articles</button>\n");
        html.append("</form>\n</fieldset>\n");

        String page = HttpUtils.renderPage("Publishing House Manager - Publications", result, html.toString());
        HttpUtils.sendResponse(exchange, 200, page);
    }
}
