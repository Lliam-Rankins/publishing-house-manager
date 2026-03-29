import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {
    private static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/ambiscoe";

    public static Connection connection = null;
    public static Statement statement = null;
    public static ResultSet result = null;

    public static void initialize() {
        try {
            connectToDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // dropTables();
        // createTables();
    }

    public static void connectToDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");

        String user = "ambiscoe";
        String password = "Summer00";

        connection = DriverManager.getConnection(jdbcURL, user, password);
        statement = connection.createStatement();
    }

    public static boolean execute(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean executeUpdate(String sql) {
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void createTables() {
        try {
            statement.executeUpdate(
                    "CREATE TABLE Distributor ( distribID INTEGER NOT NULL, balance float NOT NULL, contactName VARCHAR(30) NOT NULL, phoneNumber char(12), category VARCHAR(30), name VARCHAR(30), street VARCHAR(30) NOT NULL, city VARCHAR(30) NOT NULL, state CHAR(2) NOT NULL, PRIMARY KEY(distribID)); ");
            statement.executeUpdate(
                    "CREATE TABLE `Order` ( oID INTEGER NOT NULL, dueBy DATE, shippingCost float, datePlaced DATE NOT NULL, deliveryStatus VARCHAR(30) NOT NULL, paymentStatus VARCHAR(30) NOT NULL, copies INTEGER NOT NULL, PRIMARY KEY (oID)); ");
            statement.executeUpdate(
                    "CREATE TABLE Publication ( pubID INTEGER NOT NULL, type VARCHAR(10) NOT NULL, title VARCHAR(150), pubPeriodicity VARCHAR(30), PRIMARY KEY (pubID)); ");
            statement.executeUpdate(
                    "CREATE TABLE Issue ( pubID INTEGER NOT NULL, issueTitle VARCHAR(150) NOT NULL, pubDate DATE, price float, PRIMARY KEY (pubID, issueTitle), FOREIGN KEY (pubID) REFERENCES Publication (pubID)); ");
            statement.executeUpdate(
                    "CREATE TABLE Edition ( ISBN BIGINT NOT NULL, edition INTEGER NOT NULL, editionTitle VARCHAR(150), dateWritten DATE, datePublished DATE, price float, PRIMARY KEY (ISBN)); ");
            statement.executeUpdate(
                    "CREATE TABLE ISBNPublication ( ISBN BIGINT NOT NULL, pubID INTEGER NOT NULL, PRIMARY KEY (ISBN), FOREIGN KEY (ISBN) REFERENCES Edition (ISBN), FOREIGN KEY (pubID) REFERENCES Publication (pubID)); ");
            statement.executeUpdate(
                    "CREATE TABLE ContainsISBN ( oID INTEGER NOT NULL, ISBN BIGINT NOT NULL, PRIMARY KEY (oID), FOREIGN KEY (oID) REFERENCES `Order` (oID), FOREIGN KEY (ISBN) REFERENCES Edition (ISBN)); ");
            statement.executeUpdate(
                    "CREATE TABLE PlacedBy ( distribID INTEGER NOT NULL, oID INTEGER NOT NULL, PRIMARY KEY (distribID,oID), FOREIGN KEY (distribID) REFERENCES Distributor (distribID), FOREIGN KEY (oID) REFERENCES `Order` (oID)); ");
            statement.executeUpdate(
                    "CREATE TABLE Person ( pID INTEGER NOT NULL, name VARCHAR(30), PRIMARY KEY (pID)); ");
            statement.executeUpdate(
                    "CREATE TABLE Payment ( paymentID INTEGER NOT NULL, amount float NOT NULL, dateIssued DATE NOT NULL, workType VARCHAR(30) NOT NULL, dateClaimed DATE, PRIMARY KEY (paymentID)); ");
            statement.executeUpdate(
                    "CREATE TABLE Receives ( pID INTEGER NOT NULL, paymentID INTEGER NOT NULL, PRIMARY KEY (pID,paymentID), FOREIGN KEY (pID) REFERENCES Person (pID), FOREIGN KEY (paymentID) REFERENCES Payment (paymentID)); ");
            statement.executeUpdate(
                    "CREATE TABLE Article ( pubID INTEGER NOT NULL, issueTitle VARCHAR(150) NOT NULL, articleTitle VARCHAR(150) NOT NULL, dateWritten DATE, text text, topic VARCHAR(30), PRIMARY KEY (pubID,issueTitle,articleTitle), FOREIGN KEY (pubID, issueTitle) REFERENCES Issue (pubID, issueTitle)); ");
            statement.executeUpdate(
                    "CREATE TABLE WritesArticle ( pID INTEGER NOT NULL, pubID INTEGER NOT NULL, articleTitle VARCHAR(150) NOT NULL, issueTitle VARCHAR(150) NOT NULL, invited BOOLEAN NOT NULL, PRIMARY KEY (pID,pubID,articleTitle,issueTitle), FOREIGN KEY (pID) REFERENCES Person (pID), FOREIGN KEY (pubID, issueTitle, articleTitle) REFERENCES Article (pubID, issueTitle, articleTitle)); ");
            statement.executeUpdate(
                    "CREATE TABLE Chapter ( ISBN BIGINT NOT NULL, chapterTitle VARCHAR(150) NOT NULL, text text, date DATE, topic VARCHAR(30), PRIMARY KEY (ISBN,chapterTitle), FOREIGN KEY (ISBN) REFERENCES Edition (ISBN)); ");
            statement.executeUpdate(
                    "CREATE TABLE WritesChapter ( pID INTEGER NOT NULL, chapterTitle VARCHAR(150) NOT NULL, ISBN BIGINT NOT NULL, invited BOOLEAN NOT NULL, PRIMARY KEY (pID,chapterTitle,ISBN), FOREIGN KEY (pID) REFERENCES Person (pID), FOREIGN KEY (ISBN, chapterTitle) REFERENCES Chapter (ISBN, chapterTitle)); ");
            statement.executeUpdate(
                    "CREATE TABLE Edits ( pID INTEGER NOT NULL, pubID INTEGER NOT NULL, invited BOOLEAN NOT NULL, PRIMARY KEY (pID,pubID), FOREIGN KEY (pID) REFERENCES Person (pID), FOREIGN KEY (pubID) REFERENCES Publication (pubID)); ");
            statement.executeUpdate(
                    "CREATE TABLE ContainsIssue ( oID INTEGER NOT NULL, pubID INTEGER NOT NULL, issueTitle VARCHAR(150) NOT NULL, PRIMARY KEY (oID,pubID,issueTitle), FOREIGN KEY (oID) REFERENCES `Order` (oID), FOREIGN KEY (pubID, issueTitle) REFERENCES Issue (pubID, issueTitle)); ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropTables() {
        try {
            // Drop tables so we have a fresh slate
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            statement.executeUpdate("DROP TABLE Distributor");
            statement.executeUpdate("DROP TABLE `Order`");
            statement.executeUpdate("DROP TABLE Publication");
            statement.executeUpdate("DROP TABLE Issue");
            statement.executeUpdate("DROP TABLE Edition");
            statement.executeUpdate("DROP TABLE ISBNPublication");
            statement.executeUpdate("DROP TABLE ContainsISBN");
            statement.executeUpdate("DROP TABLE PlacedBy");
            statement.executeUpdate("DROP TABLE Person");
            statement.executeUpdate("DROP TABLE Payment");
            statement.executeUpdate("DROP TABLE Receives");
            statement.executeUpdate("DROP TABLE WritesArticle");
            statement.executeUpdate("DROP TABLE WritesChapter");
            statement.executeUpdate("DROP TABLE Edits");
            statement.executeUpdate("DROP TABLE Article");
            statement.executeUpdate("DROP TABLE Chapter");
            statement.executeUpdate("DROP TABLE ContainsIssue");
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (result != null) {
            try {
                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void beginTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void rollbackTransaction() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void commitTransaction() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
