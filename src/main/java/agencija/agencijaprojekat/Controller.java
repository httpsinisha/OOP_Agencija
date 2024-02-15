package agencija.agencijaprojekat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static agencija.agencijaprojekat.Database.*;

public class Controller {


    @FXML
    private ImageView pocetna_slika;

    @FXML
    private Button prijava_izadji_dugme, prijava_prijavi_se_dugme,prijava_registruj_se_dugme, registracija_prijavi_se_dugme, registracija_registruj_se_drugme, klijent_izadji_dugme, rezervacije, nova_rezervacija;

    @FXML
    private TextField prijava_korisnicko_ime_textfield, prijava_lozinka_textfield;
    @FXML
    private TextField registracija_ime, registracija_prezime, registracija_broj_telefona, registracija_jmbg, registracija_broj_racuna, registracija_korisnicko_ime;
    @FXML
    private PasswordField registracija_lozinka, registracija_potvrda_lozinke;
    @FXML
    private PasswordField prijava_lozinka_passwordfield;
    @FXML
    private CheckBox prijava_prikazi_lozinku;
    @FXML
    private AnchorPane anchorpane_prijava, anchorpane_registracija;

    @FXML
    private Label prijava_greska_label, registracija_greska_label;


    @FXML
    private void prijava_prikazi_lozinku_klik(ActionEvent event){
        if(prijava_prikazi_lozinku.isSelected()){
            prijava_lozinka_passwordfield.setVisible(false);
            prijava_lozinka_textfield.setVisible(true);
            prijava_lozinka_textfield.setText(prijava_lozinka_passwordfield.getText());
        }else{
            prijava_lozinka_passwordfield.setVisible(true);
            prijava_lozinka_passwordfield.setText(prijava_lozinka_textfield.getText());
            prijava_lozinka_textfield.setVisible(false);
        }
    }

    @FXML
    public void initialize() {
               prijava_korisnicko_ime_textfield.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                if (!prijava_prikazi_lozinku.isSelected()) {
                    event.consume();
                    prijava_lozinka_passwordfield.requestFocus();
                }
            }
        });
    }



    @FXML
    private void prijava_izadji_dugme_klik(ActionEvent event){
            Stage stage = (Stage) prijava_izadji_dugme.getScene().getWindow();
            stage.close();
    }



    @FXML
    private void prijava_registruj_se_dugme_klik(ActionEvent event){
        prijava_korisnicko_ime_textfield.clear();
        prijava_lozinka_passwordfield.clear();
        prijava_lozinka_textfield.clear();
        prijava_greska_label.setText("");
        registracija_ime.clear();
        registracija_prezime.clear();
        registracija_broj_telefona.clear();
        registracija_broj_racuna.clear();
        registracija_jmbg.clear();
        registracija_broj_racuna.clear();
        registracija_korisnicko_ime.clear();
        registracija_lozinka.clear();
        registracija_potvrda_lozinke.clear();
        registracija_greska_label.setText("");
        anchorpane_prijava.setVisible(false);
        anchorpane_registracija.setVisible(true);
        registracija_ime.requestFocus();
    }

    @FXML
    private void registracija_prijavi_se_dugme_klik(ActionEvent event){
        registracija_ime.clear();
        registracija_prezime.clear();
        registracija_broj_telefona.clear();
        registracija_broj_racuna.clear();
        registracija_jmbg.clear();
        registracija_broj_racuna.clear();
        registracija_korisnicko_ime.clear();
        registracija_lozinka.clear();
        registracija_potvrda_lozinke.clear();
        registracija_greska_label.setText("");
        prijava_korisnicko_ime_textfield.clear();
        prijava_lozinka_passwordfield.clear();
        prijava_lozinka_textfield.clear();
        prijava_greska_label.setText("");
        anchorpane_prijava.setVisible(true);
        anchorpane_registracija.setVisible(false);
    }


    @FXML
    private void prijava_prijavi_se_dugme_klik(ActionEvent event) throws SQLException, ClassNotFoundException {
        validacija_prijave();
    }


    public static class Session {
        private static Klijent currentKlijent = null;
        private static Admin currentAdmin = null;

        public static Klijent getCurrentKlijent() {
            return currentKlijent;
        }

        public static void setCurrentKlijent(Klijent klijent) {
            currentKlijent = klijent;
        }

        public static Admin getCurrentAdmin() {
            return currentAdmin;
        }

        public static void setCurrentAdmin(Admin currentAdmin) {
            Session.currentAdmin = currentAdmin;
        }
    }

    private Klijent klijent;
    private Admin admin = new Admin();

    private void validacija_prijave() {
        String korisnickoIme = prijava_korisnicko_ime_textfield.getText();
        String lozinkaTekst = prijava_prikazi_lozinku.isSelected() ? prijava_lozinka_textfield.getText() : prijava_lozinka_passwordfield.getText();

        if (korisnickoIme.isEmpty() || lozinkaTekst.isEmpty()) {
            prijava_greska_label.setTextFill(Color.RED);
            prijava_greska_label.setText("Molimo Vas, unesite sve podatke!");
        } else {
            int indikator=0;
            for (Admin a:listaAdmina()) {
                if(a.getKorisnickoIme().equals(korisnickoIme) && a.getLozinka().equals(lozinkaTekst)){
                    admin = a;
                    Session.setCurrentAdmin(admin);
                    prijava_greska_label.setTextFill(Color.GREEN);
                    prijava_greska_label.setText("Dobrodošli (admin)");
                    prikaziAlert("Prijava uspješna. Dobrodošli!", Alert.AlertType.INFORMATION);
                    sistem_prikaz_admin();
                    return;
                }
            }
            for(Klijent k:listaKlijenata()){
                if(k.getKorisnickoIme().equals(korisnickoIme) && k.getLozinka().equals(lozinkaTekst)){
                    klijent =k;
                    Session.setCurrentKlijent(klijent);
                    prijava_greska_label.setTextFill(Color.GREEN);
                    prijava_greska_label.setText("Dobrodošli (klijent)");
                    prikaziAlert("Prijava uspješna. Dobrodošli!", Alert.AlertType.INFORMATION);
                    sistem_prikaz_klijent();
                    indikator++;
                }
            }
            if(indikator==0){
                prijava_greska_label.setTextFill(Color.RED);
                prijava_greska_label.setText("Nevažeći podaci. Molimo Vas, pokušajte ponovo.");
            }
        }
    }


    @FXML
    private void registracija_registruj_se_drugme_klik(ActionEvent event) throws SQLException, ClassNotFoundException {
        validacija_registracije();
    }



    private int dohvatiIDBankovnogRacuna(String brojRacuna) throws SQLException, ClassNotFoundException {
        int broj=-1;
        for(BankovniRacun b: listaBankovnihRacuna()){
            if(b.getBrojRacuna().equals(brojRacuna)){
                broj = b.getId();
            }
        }
        return broj;
    }


    private void validacija_registracije() throws SQLException, ClassNotFoundException {
        String ime = registracija_ime.getText();
        String prezime = registracija_prezime.getText();
        String brojTelefona = registracija_broj_telefona.getText();
        String jmbg = registracija_jmbg.getText();
        String brojRacuna = registracija_broj_racuna.getText();
        String korisnickoIme = registracija_korisnicko_ime.getText();
        String lozinka = registracija_lozinka.getText();
        String potvrdaLozinke = registracija_potvrda_lozinke.getText();

        if(ime.isEmpty() || prezime.isEmpty() || brojTelefona.isEmpty() || jmbg.isEmpty() || brojRacuna.isEmpty() || korisnickoIme.isEmpty() || lozinka.isEmpty() || potvrdaLozinke.isEmpty()){
            registracija_greska_label.setText("Popunite sva polja.");
            registracija_greska_label.setTextFill(Color.RED);
            return;
        }

        if (!ime.matches("^[a-zA-ZšđčćžŠĐČĆŽ]+$")) {
            registracija_greska_label.setText("Ime mora sadržavati samo slova.");
            registracija_greska_label.setTextFill(Color.RED);
            registracija_ime.requestFocus();
            return;
        }

        if (!prezime.matches("^[a-zA-ZšđčćžŠĐČĆŽ]+$")) {
            registracija_greska_label.setText("Prezime mora sadržavati samo slova.");
            registracija_greska_label.setTextFill(Color.RED);
            registracija_prezime.requestFocus();
            return;
        }

        if (!brojTelefona.matches("^\\d+$")) {
            registracija_greska_label.setText("Broj telefona mora sadržavati samo brojeve.");
            registracija_greska_label.setTextFill(Color.RED);
            registracija_broj_telefona.requestFocus();
            return;
        }

        if (korisnickoIme.length() < 3 || korisnickoIme.length() > 18) {
            registracija_greska_label.setText("Korisničko ime mora imati između 3 i 18 karaktera.");
            registracija_greska_label.setTextFill(Color.RED);
            registracija_korisnicko_ime.requestFocus();
            return;
        }

        if (lozinka.length() < 3 || lozinka.length() > 18 || !lozinka.matches(".*\\d.*")) {
            registracija_greska_label.setText("Lozinka mora imati između 3 i 18 karaktera i sadržavati barem jedan broj.");
            registracija_greska_label.setTextFill(Color.RED);
            registracija_lozinka.requestFocus();
            return;
        }

        if (!lozinka.equals(potvrdaLozinke)) {
            registracija_greska_label.setText("Lozinka i potvrda lozinke se ne podudaraju.");
            registracija_greska_label.setTextFill(Color.RED);
            registracija_potvrda_lozinke.requestFocus();
            return;
        }

        for(Admin a:listaAdmina()){
            if(a.getKorisnickoIme().equals(korisnickoIme)){
                registracija_greska_label.setText("Korisničko ime već postoji u tabeli admin.");
                registracija_greska_label.setTextFill(Color.RED);
                registracija_korisnicko_ime.requestFocus();
                return;
            }
        }

        for (Klijent k:listaKlijenata()) {
            if(k.getKorisnickoIme().equals(korisnickoIme)){
                registracija_greska_label.setText("Korisničko ime već postoji u tabeli klijent.");
                registracija_greska_label.setTextFill(Color.RED);
                registracija_korisnicko_ime.requestFocus();
                return;
            }
            if(k.getBrojRacuna().equals(brojRacuna)){
                registracija_greska_label.setText("Broj računa već koristi drugi klijent. Molimo izaberite drugi.");
                registracija_greska_label.setTextFill(Color.RED);
                registracija_broj_racuna.requestFocus();
                return;
            }
            if(k.getJmbg().equals(jmbg)){
                registracija_greska_label.setText("JMBG već koristi drugi klijent. Molimo izaberite drugi.");
                registracija_greska_label.setTextFill(Color.RED);
                registracija_jmbg.requestFocus();
                return;
            }
        }

        int indikator=0;
        for(BankovniRacun b:listaBankovnihRacuna()){
            if(b.getBrojRacuna().equals(brojRacuna) && b.getJmbg().equals(jmbg))
                indikator++;
        }

        if(indikator>0){
            int bankovniRacunID = dohvatiIDBankovnogRacuna(brojRacuna);
            listaKlijenata().add(new Klijent(bankovniRacunID,ime,prezime,brojTelefona,jmbg,brojRacuna,korisnickoIme,lozinka));
            Database.dodajKlijenta(bankovniRacunID,ime,prezime,brojTelefona,jmbg,brojRacuna,korisnickoIme,lozinka);
            registracija_greska_label.setText("Registracija uspješna. Dobrodošli!");
            registracija_greska_label.setTextFill(Color.GREEN);
            prikaziAlert("Registracija uspješna. Dobrodošli!", Alert.AlertType.CONFIRMATION);
            anchorpane_registracija.setVisible(false);
            anchorpane_prijava.setVisible(true);
        }else {
            registracija_greska_label.setText("Broj računa i JMBG ne odgovaraju u banci.");
            registracija_greska_label.setTextFill(Color.RED);
            registracija_jmbg.requestFocus();
            return;
        }

    }


    private void prikaziAlert(String poruka, Alert.AlertType tip) {
        Alert alert = new Alert(tip);
        alert.setTitle("Obavještenje");
        alert.setHeaderText(null);
        alert.setContentText(poruka);
        alert.showAndWait();
    }




    private double x=0, y=0;

    private void sistem_prikaz_klijent() {
        try {
            Stage trenutniStage = (Stage) prijava_izadji_dugme.getScene().getWindow();
            trenutniStage.close();

            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("klijentScena.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();


            scene.setOnMousePressed(mouseEvent ->{
                x = mouseEvent.getSceneX();
                y = mouseEvent.getSceneY();
            });

            scene.setOnMouseDragged(mouseEvent -> {
                stage.setX(mouseEvent.getScreenX() - x);
                stage.setY(mouseEvent.getScreenY() - y);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void sistem_prikaz_admin() {
        try {
            Stage trenutniStage = (Stage) prijava_izadji_dugme.getScene().getWindow();
            trenutniStage.close();
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("adminScena.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();


            scene.setOnMousePressed(mouseEvent ->{
                x = mouseEvent.getSceneX();
                y = mouseEvent.getSceneY();
            });

            scene.setOnMouseDragged(mouseEvent -> {
                stage.setX(mouseEvent.getScreenX() - x);
                stage.setY(mouseEvent.getScreenY() - y);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}