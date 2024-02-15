package agencija.agencijaprojekat;

import javafx.stage.Stage;

public class WindowManager {
    private static Stage trenutniStage;

    public static void setTrenutniStage(Stage stage) {
        trenutniStage = stage;
    }

    public static Stage getTrenutniStage() {
        return trenutniStage;
    }
}
