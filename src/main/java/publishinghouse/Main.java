package publishinghouse;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        try {
            boolean reset = (args.length > 0 && "reset".equalsIgnoreCase(args[0]))
                    || "true".equalsIgnoreCase(System.getenv("RESET_DB"));
            DBManager.initialize(reset);

            int port = 8080;
            String portEnv = System.getenv("PORT");
            if (portEnv != null && !portEnv.trim().isEmpty()) {
                try {
                    port = Integer.parseInt(portEnv.trim());
                } catch (NumberFormatException ignored) {
                }
            }

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new HomeHandler());
            server.createContext("/publications", new PublicationsHandler());
            server.createContext("/people", new PeopleHandler());
            server.createContext("/distributors", new DistributorsHandler());
            server.createContext("/reports", new ReportsHandler());

            server.setExecutor(Executors.newCachedThreadPool());
            server.start();

            System.out.println("Web server running at http://localhost:" + port + "/");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                server.stop(1);
                DBManager.close();
            }));

        } catch (Exception e) {
            e.printStackTrace();
            DBManager.close();
        }
    }
}
