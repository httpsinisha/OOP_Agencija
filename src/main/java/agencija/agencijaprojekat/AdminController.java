package agencija.agencijaprojekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static agencija.agencijaprojekat.Database.*;

public class AdminController {
    @FXML private Button admin_izadji_dugme;
    @FXML private Label admin_korisnicko_ime, admin_ime_prezime, broj_admina,zarada_agencije;
    @FXML private AnchorPane anchorpane_rezervacije, anchorpane_dodaj_izlet, anchorpane_dodaj_admina;
    @FXML private Button rezervacije, dodaj_izlet, dodaj_admina;


    private static double zarada=listaBankovnihRacuna().getLast().getStanje();

    public static void setZarada(double zarada) {
        AdminController.zarada = zarada;
    }

    private Admin admin = Controller.Session.getCurrentAdmin();


    public void initialize(){
        admin = Controller.Session.getCurrentAdmin();
        if (admin != null) {
            admin_korisnicko_ime.setText(admin.getKorisnickoIme());
            admin_ime_prezime.setText(admin.getIme() + " " + admin.getPrezime());
            postaviBrojAdmina();
            postaviZaraduAgencije();
            if(admin.getLozinka().equals("12345678")){
                startPromjenaLozinke();
            }
            postavi_aranzmane();
            postavi_combo_vrijednosti();
        }
    }

    @FXML
    private void rezervacije_klik(ActionEvent event){
        anchorpane_rezervacije.setVisible(true);
        anchorpane_dodaj_admina.setVisible(false);
        anchorpane_dodaj_izlet.setVisible(false);
        anchorpane_klijenti.setVisible(false);
    }

    @FXML
    private void dodaj_izlet_klik(ActionEvent event){
        anchorpane_dodaj_izlet.setVisible(true);
        anchorpane_rezervacije.setVisible(false);
        anchorpane_dodaj_admina.setVisible(false);
        anchorpane_klijenti.setVisible(false);
    }

    @FXML
    private void dodaj_admina_klik(ActionEvent event){
        anchorpane_dodaj_admina.setVisible(true);
        anchorpane_rezervacije.setVisible(false);
        anchorpane_dodaj_izlet.setVisible(false);
        anchorpane_klijenti.setVisible(false);
    }


    private void postaviBrojAdmina(){
        broj_admina.setText(String.valueOf(Database.listaAdmina().size()));
    }

    private void postaviZaraduAgencije(){
        zarada_agencije.setText(String.valueOf(zarada));
    }




    @FXML
    private void promijeniLozinku(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PromjenaLozinkeAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Promjena lozinke");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)event.getSource()).getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startPromjenaLozinke() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("startPromjenaLozinkeAdmina.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Promjena lozinke");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(WindowManager.getTrenutniStage());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML private TextField ime, prezime,korisnicko_ime;
    @FXML
    private void dodaj_admina_potvrda(ActionEvent event){
        String admin_ime = ime.getText();
        String admin_prezime = prezime.getText();
        String admin_korisnicko_ime = korisnicko_ime.getText();
        if (!admin_ime.matches("^[a-zA-ZšđčćžŠĐČĆŽ]+$")) {
            prikaziAlert("Ime mora sadržavati samo slova.", Alert.AlertType.ERROR);
            ime.requestFocus();
            return;
        }

        if (!admin_prezime.matches("^[a-zA-ZšđčćžŠĐČĆŽ]+$")) {
            prikaziAlert("Prezime mora sadržavati samo slova.", Alert.AlertType.ERROR);
            prezime.requestFocus();
            return;
        }
        if (admin_korisnicko_ime.length() < 3 || admin_korisnicko_ime.length() > 18) {
            prikaziAlert("Korisnicko ime mora imati između 3 i 19 karaktera.", Alert.AlertType.ERROR);
            korisnicko_ime.requestFocus();
            return;
        }

        for(Admin a:Database.listaAdmina()){
            if(a.getKorisnickoIme().equals(admin_korisnicko_ime)){
                prikaziAlert("Korisnicko ime već postoji.", Alert.AlertType.ERROR);
                korisnicko_ime.requestFocus();
                return;
            }
        }
        int id = pronadiPrviSlobodanIdAdmina(listaAdmina());
        Admin novi_admin = new Admin(id,admin_ime,admin_prezime,admin_korisnicko_ime,"12345678");
        Database.listaAdmina().add(novi_admin);
        Database.dodajAdmina(id,admin_ime,admin_prezime,admin_korisnicko_ime);
        prikaziAlert("Dodadavanje admina uspješno.", Alert.AlertType.CONFIRMATION);
        ime.setText("");
        prezime.setText("");
        korisnicko_ime.setText("");
        anchorpane_dodaj_admina.setVisible(false);
        anchorpane_rezervacije.setVisible(true);
        postaviBrojAdmina();

    }

    @FXML
    private ListView<String> jednodnevna_lista, visednevna_lista;
    private ListView<Aranzman> jednoAranzman= new ListView<>();
    private ListView<Aranzman> viseAranzman= new ListView<>();



    private void postavi_aranzmane(){
        jednodnevna_lista.getItems().clear();
        jednoAranzman.getItems().clear();
        visednevna_lista.getItems().clear();
        viseAranzman.getItems().clear();
        for (Aranzman a:listaAranzmana()) {
            if(a.getDatumPolaska().equals(a.getDatumDolaska())){
                String izlet = a.toString();
                jednodnevna_lista.getItems().add(izlet);
                jednoAranzman.getItems().add(a);
            }else {
                for(Smjestaj s:listaSmjestaja()){
                    if(s.getId()==a.getSmjestajId().getId()){
                        String putovanje = a.toString()+" | "+s.getNaziv()+" | "+s.getVrsta_sobe()+" | "+s.getBroj_zvjezdica()+"☆";
                        visednevna_lista.getItems().add(putovanje);
                        viseAranzman.getItems().add(a);
                    }
                }
            }
        }
    }



    @FXML private TextField izlet_naziv, izlet_destinacija, izlet_cijena;
    @FXML private DatePicker izlet_datum;

    @FXML
    private void dodaj_izlet(){
        String naziv  = izlet_naziv.getText();
        String destinacija = izlet_destinacija.getText();
        String cijena = izlet_cijena.getText();
        LocalDate datum = izlet_datum.getValue();

        if(!naziv.matches("^[a-zA-ZšđčćžŠĐČĆŽ\\s]+$")){
            prikaziAlert("Naziv nije validan.", Alert.AlertType.ERROR);
            izlet_naziv.requestFocus();
            return;
        }
        if(!destinacija.matches("^[a-zA-ZšđčćžŠĐČĆŽ\\s]+$")){
            prikaziAlert("Destinacija nije validna.", Alert.AlertType.ERROR);
            izlet_destinacija.requestFocus();
            return;
        }
        if(datum==null || datum.isBefore(LocalDate.now().plusDays(15))){
            prikaziAlert("Datum nije validan.", Alert.AlertType.ERROR);
            return;
        }
        try {
            double c = Double.parseDouble(cijena);
            String izlet = naziv+" | "+destinacija+" | Autobus | "+datum+" | Ukupna cijena: "+cijena;
            int idd = pronadiPrviSlobodanIdAranzmana(listaAranzmana());
            Aranzman aranzman = new Aranzman(idd,naziv,destinacija,"Autobus",datum,datum,Double.parseDouble(cijena),null);
            jednodnevna_lista.getItems().add(izlet);
            jednoAranzman.getItems().add(aranzman);
            prikaziAlert("Uspješno dodan izlet", Alert.AlertType.CONFIRMATION);
            izlet_naziv.setText("");
            izlet_destinacija.setText("");
            izlet_cijena.setText("");
            izlet_datum.setValue(null);
            anchorpane_dodaj_izlet.setVisible(false);
            anchorpane_rezervacije.setVisible(true);

            int id = Database.pronadiPrviSlobodanIdAranzmana(listaAranzmana());
            Database.dodajIzlet(id,naziv,destinacija,datum,c);

        }catch (NumberFormatException e){
            prikaziAlert("Cijena nije validna.", Alert.AlertType.ERROR);
            izlet_cijena.requestFocus();
            return;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    @FXML
    private ComboBox<String> putovanje_sobe, putovanje_prevoz, putovanje_zvjezdice;
    private String[] vrste_soba = {"Jednokrevetna", "Dvokrevetna", "Trokrevetna", "Apartman"};
    private String[] nacin_prevoza = {"Avion", "Autobus", "Samostalan"};
    private String[] broj_zvjezdica = {"1", "2", "3", "4", "5"};

    private void postavi_combo_vrijednosti(){
        putovanje_sobe.getItems().addAll(vrste_soba);
        putovanje_prevoz.getItems().addAll(nacin_prevoza);
        putovanje_zvjezdice.getItems().addAll(broj_zvjezdica);
    }

    @FXML private TextField putovanje_naziv, putovanje_destinacija, putovanje_cijena, putovanje_smjestaj, putovanje_nocenje;
    @FXML private DatePicker putovanje_polazak, putovanje_dolazak;

    @FXML
    public void dodaj_putovanje(){
        String naziv = putovanje_naziv.getText();
        String destinacija = putovanje_destinacija.getText();
        String cijena = putovanje_cijena.getText();
        String smjestaj = putovanje_smjestaj.getText();
        String nocenje = putovanje_nocenje.getText();
        LocalDate polazak = putovanje_polazak.getValue();
        LocalDate dolazak = putovanje_dolazak.getValue();
        String soba = putovanje_sobe.getValue();
        String prevoz = putovanje_prevoz.getValue();
        String zvjezdice = putovanje_zvjezdice.getValue();
        int id = pronadiPrviSlobodanIdAranzmana(listaAranzmana());
        int broj_dana = 1;


        if(!naziv.matches("^[a-zA-ZšđčćžŠĐČĆŽ\\s]+$")){
            prikaziAlert("Naziv nije validan.", Alert.AlertType.ERROR);
            putovanje_naziv.requestFocus();
            return;
        }
        if(!destinacija.matches("^[a-zA-ZšđčćžŠĐČĆŽ\\s]+$")){
            prikaziAlert("Destinacija nije validna.", Alert.AlertType.ERROR);
            putovanje_destinacija.requestFocus();
            return;
        }
        try{
            double c = Double.parseDouble(cijena);
            if(!smjestaj.matches("^[a-zA-ZšđčćžŠĐČĆŽ\\s]+$")){
                prikaziAlert("Smjestaj nije validan.", Alert.AlertType.ERROR);
                putovanje_smjestaj.requestFocus();
                return;
            }
            try {
                double cijena_n = Double.parseDouble(nocenje);
                if(polazak==null || dolazak==null || polazak.isAfter(dolazak)){
                    prikaziAlert("Datumi nisu validni.", Alert.AlertType.ERROR);
                    return;
                }

                try{
                    broj_dana = (int) ChronoUnit.DAYS.between(polazak,dolazak);
                }catch (Exception e){
                    prikaziAlert("Datumi nisu validni.", Alert.AlertType.ERROR);
                    return;
                }
                if(putovanje_sobe.getValue()==null){
                    prikaziAlert("Izaberite vrstu sobe.", Alert.AlertType.ERROR);
                    return;
                }
                if(putovanje_prevoz.getValue()==null){
                    prikaziAlert("Izaberite tip prevoza.", Alert.AlertType.ERROR);
                    return;
                }
                if(putovanje_zvjezdice.getValue()==null){
                    prikaziAlert("Izaberite broj zvjezdica", Alert.AlertType.ERROR);
                    return;
                }

                if(polazak.isAfter(dolazak) || polazak.isBefore(LocalDate.now().plusDays(15))){
                    prikaziAlert("Datumi nisu validni.", Alert.AlertType.ERROR);
                    return;
                }

                String putovanje =naziv+" | "+destinacija+" | "+ prevoz+" | "+polazak+" | "+ dolazak+" | Aranzman: " +c+" | Noćenje: "+cijena_n+" | Ukupna cijena: "+(c+cijena_n*broj_dana)+" | "+smjestaj+" | "+soba+" | "+zvjezdice+"☆";
                int idd = pronadiPrviSlobodanIdAranzmana(listaAranzmana());
                visednevna_lista.getItems().add(putovanje);
                prikaziAlert("Uspješno dodano putovanje", Alert.AlertType.CONFIRMATION);
                int id_smjestaj = pronadiPrviSlobodanIdSmjestaja(listaSmjestaja());
                Smjestaj s = new Smjestaj(id_smjestaj,smjestaj,zvjezdice,soba,String.valueOf(cijena_n));
                Aranzman aranzman = new Aranzman(idd,naziv,destinacija,"Autobus",polazak,dolazak,Double.parseDouble(cijena),s);
                viseAranzman.getItems().add(aranzman);
                Database.dodajSmjestaj(id_smjestaj,naziv,zvjezdice,soba,String.valueOf(cijena_n));
                Database.dodajPutovanje(id,naziv,destinacija,prevoz,polazak,dolazak,c,s);
                putovanje_naziv.setText("");
                putovanje_destinacija.setText("");
                putovanje_cijena.setText("");
                putovanje_smjestaj.setText("");
                putovanje_nocenje.setText("");
                putovanje_polazak.setValue(null);
                putovanje_dolazak.setValue(null);
                putovanje_sobe.setValue(null);
                putovanje_prevoz.setValue(null);
                putovanje_zvjezdice.setValue(null);
                anchorpane_dodaj_izlet.setVisible(false);
                anchorpane_rezervacije.setVisible(true);

            }catch (Exception e){
                prikaziAlert("Cijena noćenja nije validna", Alert.AlertType.ERROR);
                putovanje_nocenje.requestFocus();
            }


        }catch (Exception e){
            prikaziAlert("Cijena nije validna", Alert.AlertType.ERROR);
            putovanje_cijena.requestFocus();
        }
    }



    @FXML private AnchorPane anchorpane_klijenti;
    @FXML private Label naziv_aranzmana;
    @FXML private ListView<String> lista_klijenata;
    private ListView<Klijent> a_lista_klijenata = new ListView<>();

    @FXML
    private void prikazi_klijent_listu_izlet(MouseEvent event){
        lista_klijenata.getItems().clear();
        a_lista_klijenata.getItems().clear();
        int indeks = jednodnevna_lista.getSelectionModel().getSelectedIndex();
        if(indeks!=-1){
            Aranzman izabrani = jednoAranzman.getItems().get(indeks);
            for(Rezervacija r:listaRezervacija()){
                if(r.getAranzman()==izabrani.getId()){
                    int klijent_id = r.getKlijent();
                    for(Klijent k:listaKlijenata()){
                        if(k.getId()==klijent_id){
                            naziv_aranzmana.setText(izabrani.getNazivPutovanja());
                            if(r.getPlacena_cijena().equals("-1")){
                                lista_klijenata.getItems().add(k.getIme()+" "+k.getPrezime()+" "+k.getBrojTelefona() +" Otkazao je!");
                            } else if (r.getPlacena_cijena().equals("-2")) {
                                lista_klijenata.getItems().add(k.getIme()+" "+k.getPrezime()+" "+k.getBrojTelefona() +" Protekla!");
                            } else {
                                lista_klijenata.getItems().add(k.getIme()+" "+k.getPrezime()+" "+k.getBrojTelefona() +" Platio: "+r.getPlacena_cijena()+" Preostalo: "+ (Double.parseDouble(r.getUkupna_cijena()) - Double.parseDouble(r.getPlacena_cijena())));
                            }
                            a_lista_klijenata.getItems().add(k);
                            anchorpane_rezervacije.setVisible(false);
                            anchorpane_klijenti.setVisible(true);
                        }
                    }
                }
            }
            if(lista_klijenata.getItems().isEmpty()){
                prikaziAlert("Niko nije rezervisao "+izabrani.getNazivPutovanja(), Alert.AlertType.INFORMATION);
            }
        }else {
            prikaziAlert("Izaberite aranzman.", Alert.AlertType.INFORMATION);
        }

    }

    @FXML
    private void prikazi_klijent_listu_putovanje(MouseEvent event){
        lista_klijenata.getItems().clear();
        a_lista_klijenata.getItems().clear();
        int indeks = visednevna_lista.getSelectionModel().getSelectedIndex();
        if(indeks!=-1){
            Aranzman izabrani = viseAranzman.getItems().get(indeks);
            for(Rezervacija r:listaRezervacija()){
                if(r.getAranzman()==izabrani.getId()){
                    int klijent_id = r.getKlijent();
                    for(Klijent k:listaKlijenata()){
                        if(k.getId()==klijent_id){
                            naziv_aranzmana.setText(izabrani.getNazivPutovanja());
                            if(r.getPlacena_cijena().equals("-1")){
                                lista_klijenata.getItems().add(k.getIme()+" "+k.getPrezime()+" "+k.getBrojTelefona() +" Otkazao je!");
                            }else {
                                lista_klijenata.getItems().add(k.getIme()+" "+k.getPrezime()+" "+k.getBrojTelefona() +" Platio: "+r.getPlacena_cijena()+" Preostalo: "+ (Double.parseDouble(r.getUkupna_cijena()) - Double.parseDouble(r.getPlacena_cijena())));
                            }
                            a_lista_klijenata.getItems().add(k);
                            anchorpane_rezervacije.setVisible(false);
                            anchorpane_klijenti.setVisible(true);
                        }
                    }
                }
            }
            if(lista_klijenata.getItems().isEmpty()){
                prikaziAlert("Niko nije rezervisao "+izabrani.getNazivPutovanja(), Alert.AlertType.INFORMATION);
            }
        }else {
            prikaziAlert("Izaberite aranzman.", Alert.AlertType.INFORMATION);
        }
    }


    @FXML Tab jednodnevna,visednevna;

    @FXML
    private void otkazi_aranzman(ActionEvent event) throws SQLException, ClassNotFoundException {
        int indeks;
        Aranzman izabrani = new Aranzman();

        if (jednodnevna.isSelected()) {
            indeks = jednodnevna_lista.getSelectionModel().getSelectedIndex();
            if (indeks != -1) {
                izabrani = jednoAranzman.getItems().get(indeks);
            }

            Alert potvrda = new Alert(Alert.AlertType.CONFIRMATION);
            potvrda.setTitle("Potvrda brisanja");
            potvrda.setHeaderText("Jeste li sigurni da želite otkazati aranžman?");
            Optional<ButtonType> rezultat = potvrda.showAndWait();

            if (rezultat.isPresent() && rezultat.get() == ButtonType.OK) {
                List<Klijent> rezervisani_klijenti = Database.listaKlijenataZaAranzman(izabrani);
                double placeno = 0;
                for (Klijent k : rezervisani_klijenti) {
                    for(Rezervacija r:listaRezervacija()){
                        if(r.getKlijent()==k.getId() && r.getAranzman()== izabrani.getId()){
                            placeno= Double.parseDouble(r.getPlacena_cijena());
                            break;
                        }
                    }
                    otkaziAranzman(izabrani.getId(), k.getId());
                    Database.azurirajStanjeRacuna(k.getId(),placeno,11,racun_iz_klijentId(11).getStanje()-izabrani.getUkupnaCijena());
                }
                obrisiAranzman(izabrani.getId());
                jednoAranzman.getItems().remove(indeks);
                jednodnevna_lista.getItems().remove(indeks);
                postavi_aranzmane();
                anchorpane_klijenti.setVisible(false);
                anchorpane_rezervacije.setVisible(true);
            }
        } else {
            indeks = visednevna_lista.getSelectionModel().getSelectedIndex();
            if (indeks != -1) {
                izabrani = viseAranzman.getItems().get(indeks);
            }

            Alert potvrda = new Alert(Alert.AlertType.CONFIRMATION);
            potvrda.setTitle("Potvrda brisanja");
            potvrda.setHeaderText("Jeste li sigurni da želite otkazati aranžman?");
            Optional<ButtonType> rezultat = potvrda.showAndWait();

            if (rezultat.isPresent() && rezultat.get() == ButtonType.OK) {
                List<Klijent> rezervisani_klijenti = Database.listaKlijenataZaAranzman(izabrani);
                double placeno = 0;
                for (Klijent k : rezervisani_klijenti) {
                    for(Rezervacija r:listaRezervacija()){
                        if(r.getKlijent()==k.getId() && r.getAranzman()== izabrani.getId()){
                            placeno= Double.parseDouble(r.getPlacena_cijena());
                            break;
                        }
                    }
                    otkaziAranzman(izabrani.getId(), k.getId());
                    Database.azurirajStanjeRacuna(k.getId(),placeno,11,racun_iz_klijentId(11).getStanje()-izabrani.getUkupnaCijena());
                }
                obrisiAranzman(izabrani.getId());
                viseAranzman.getItems().remove(indeks);
                visednevna_lista.getItems().remove(indeks);
                postavi_aranzmane();
                anchorpane_klijenti.setVisible(false);
                anchorpane_rezervacije.setVisible(true);
            }
        }
    }




    @FXML
    private void izadji_iz_liste_klijenata(ActionEvent event){
        anchorpane_klijenti.setVisible(false);
        anchorpane_rezervacije.setVisible(true);
    }




    private void prikaziAlert(String poruka, Alert.AlertType tip) {
        Alert alert = new Alert(tip);
        alert.setTitle("Obavještenje");
        alert.setHeaderText(null);
        alert.setContentText(poruka);
        alert.showAndWait();
    }

    private double x=0, y=0;
    @FXML
    private void odjavi_se_klik(ActionEvent e) throws IOException {
        Alert potvrda = new Alert(Alert.AlertType.CONFIRMATION);
        potvrda.setTitle("Potvrda odjave");
        potvrda.setHeaderText("Da li ste sigurni da se želite odjaviti?");

        ButtonType daButton = new ButtonType("Da", ButtonBar.ButtonData.YES);
        ButtonType neButton = new ButtonType("Ne", ButtonBar.ButtonData.NO);

        potvrda.getButtonTypes().setAll(daButton, neButton);

        Optional<ButtonType> rezultat = potvrda.showAndWait();

        if (rezultat.isPresent() && rezultat.get() == daButton) {

            Stage trenutniStage = (Stage) admin_ime_prezime.getScene().getWindow();

            trenutniStage.close();
            admin = null;
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
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

        } else {
            potvrda.close();
        }
    }

    @FXML
    private void admin_izadji_dugme_klik(ActionEvent event){
        Stage stage = (Stage) admin_izadji_dugme.getScene().getWindow();
        stage.close();
    }
}
