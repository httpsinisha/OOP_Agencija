package agencija.agencijaprojekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KlijentPromjenaLozinkeController {

    @FXML private PasswordField staraLozinkaField;
    @FXML private PasswordField novaLozinkaField;
    @FXML private PasswordField potvrdaLozinkeField;
    private Klijent klijent;
    @FXML
    private void potvrda_promjene_lozinke(ActionEvent event) {

        this.klijent = Controller.Session.getCurrentKlijent();

        if (this.klijent == null) {
            prikaziAlert("Niste prijavljeni!", Alert.AlertType.ERROR);
            return;
        }

        String staraLozinka = staraLozinkaField.getText();
        String novaLozinka = novaLozinkaField.getText();
        String potvrdiLozinku = potvrdaLozinkeField.getText();

        if (staraLozinka.isEmpty() || novaLozinka.isEmpty() || potvrdiLozinku.isEmpty()) {
            prikaziAlert("Molimo Vas, unesite sve podatke!", Alert.AlertType.ERROR);
            return;
        }
        String korisnickoIme = klijent.getKorisnickoIme();
        String korisnickaLozinka = klijent.getLozinka();



        if (!korisnickaLozinka.equals(staraLozinka)) {
            prikaziAlert("Stara lozinka nije ispravna.", Alert.AlertType.ERROR);
            staraLozinkaField.requestFocus();
            return;
        }

        if (korisnickaLozinka.equals(novaLozinka)) {
            prikaziAlert("Nova lozinka ne može biti ista kao stara.", Alert.AlertType.ERROR);
            novaLozinkaField.requestFocus();
            return;
        }

        if (!novaLozinka.matches(".*\\d.*") || novaLozinka.length() < 3 || novaLozinka.length() > 18) {
            prikaziAlert("Nova lozinka mora sadržavati barem jedan broj i imati između 3 i 18 karaktera.", Alert.AlertType.ERROR);
            novaLozinkaField.requestFocus();
            return;
        }

        if (!novaLozinka.equals(potvrdiLozinku)) {
            prikaziAlert("Nova lozinka i potvrda lozinke se ne podudaraju.", Alert.AlertType.ERROR);
            potvrdaLozinkeField.requestFocus();
            return;
        }

        try {
            for(Klijent k:Database.listaKlijenata()){
                if(k.getKorisnickoIme().equals(klijent.getKorisnickoIme()) && k.getLozinka().equals(klijent.getLozinka())){
                    k.setLozinka(novaLozinka);
                    Database.promijeniSifruKlijenta(k.getId(),novaLozinka);
                    klijent.setLozinka(novaLozinka);
                    prikaziAlert("Lozinka uspješno promijenjena.", Alert.AlertType.INFORMATION);
                    Stage stage = (Stage) staraLozinkaField.getScene().getWindow();
                    stage.close();
                }
            }

        } catch (Exception e) {
            prikaziAlert("Došlo je do greške prilikom promjene lozinke.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void prikaziAlert(String poruka, Alert.AlertType tip) {
        Alert alert = new Alert(tip);
        alert.setTitle("Obavještenje");
        alert.setHeaderText(null);
        alert.setContentText(poruka);
        alert.showAndWait();
    }
}
