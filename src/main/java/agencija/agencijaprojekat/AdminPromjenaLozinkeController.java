package agencija.agencijaprojekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

public class AdminPromjenaLozinkeController {
    @FXML
    private PasswordField staraLozinkaField,novaLozinkaField, potvrdaLozinkeField;
    private Admin admin;
    private void prikaziAlert(String poruka, Alert.AlertType tip) {
        Alert alert = new Alert(tip);
        alert.setTitle("Obavještenje");
        alert.setHeaderText(null);
        alert.setContentText(poruka);
        alert.showAndWait();
    }


    @FXML
    private void potvrda_promjene_lozinke(ActionEvent event) {

        this.admin = Controller.Session.getCurrentAdmin();

        if (this.admin == null) {
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
        String korisnickoIme = admin.getKorisnickoIme();
        String korisnickaLozinka = admin.getLozinka();



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
            for(Admin a:Database.listaAdmina()){
                if(a.getKorisnickoIme().equals(admin.getKorisnickoIme()) && a.getLozinka().equals(admin.getLozinka())){
                    a.setLozinka(novaLozinka);
                    Database.promijeniSifruAdmina(a.getKorisnickoIme(),novaLozinka);
                    admin.setLozinka(novaLozinka);
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


    @FXML private PasswordField startNovaLozinka, startPotvrdaLozinke;
    @FXML
    private void startPromjenaLozinke(){
        this.admin = Controller.Session.getCurrentAdmin();
        String novaLozinka = startNovaLozinka.getText();
        String potvrdiLozinku = startPotvrdaLozinke.getText();
        String korisnickaLozinka = admin.getLozinka();
        if (korisnickaLozinka.equals(novaLozinka)) {
            prikaziAlert("Nova lozinka ne može biti ista kao stara.", Alert.AlertType.ERROR);
            startNovaLozinka.requestFocus();
            return;
        }

        if (!novaLozinka.matches(".*\\d.*") || novaLozinka.length() < 3 || novaLozinka.length() > 18) {
            prikaziAlert("Nova lozinka mora sadržavati barem jedan broj i imati između 3 i 18 karaktera.", Alert.AlertType.ERROR);
            startNovaLozinka.requestFocus();
            return;
        }

        if (!novaLozinka.equals(potvrdiLozinku)) {
            prikaziAlert("Nova lozinka i potvrda lozinke se ne podudaraju.", Alert.AlertType.ERROR);
            startPotvrdaLozinke.requestFocus();
            return;
        }
        for(Admin a:Database.listaAdmina()){
            if(a.getKorisnickoIme().equals(admin.getKorisnickoIme()) && a.getLozinka().equals(admin.getLozinka())){
                a.setLozinka(novaLozinka);
                Database.promijeniSifruAdmina(a.getKorisnickoIme(),novaLozinka);
                admin.setLozinka(novaLozinka);
                prikaziAlert("Lozinka uspješno promijenjena.", Alert.AlertType.INFORMATION);
                Stage stage = (Stage) startNovaLozinka.getScene().getWindow();
                stage.close();
            }
        }
    }


}
