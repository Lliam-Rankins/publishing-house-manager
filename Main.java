import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        DBManager.initialize();

        boolean running = true;

        while (running) {
            System.out.println("Enter an operation, or 'exit' to exit: ");
            String operation = s.nextLine();
            switch (operation) {
                case "assignEditorToPublication":
                    System.out.println("Enter the editor id:");
                    int editorId = s.nextInt();
                    System.out.println("Enter the publication id:");
                    int pubId = s.nextInt();
                    System.out.println("Enter true if the editor was invited, false otherwise");
                    boolean invited = s.nextBoolean();
                    People.assignEditorToPublication(editorId, pubId, invited);
                    break;
                case "removeEditorFromPublication":
                    System.out.println("Enter the editor id:");
                    editorId = s.nextInt();
                    System.out.println("Enter the publication id:");
                    pubId = s.nextInt();
                    People.removeEditorFromPublication(editorId, pubId);
                    break;
                case "exit":
                    running = false;
                    break;
                default:
                    break;
            }
        }

        s.close();
        DBManager.close();
    }
}
