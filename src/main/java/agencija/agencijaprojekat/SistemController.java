package agencija.agencijaprojekat;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static agencija.agencijaprojekat.Database.*;

public class SistemController {
    @FXML private Button klijent_izadji_dugme;
    @FXML private AnchorPane anchorpane_rezervacije, anchorpane_nova_rezervacija, center;
    @FXML private Label sistem_korisnicko_ime,sistem_ime_prezime,stanje_novca;


    private Klijent klijent= Controller.Session.getCurrentKlijent();

    public void setKlijent(Klijent klijent) {
        this.klijent = klijent;
    }

    public void initialize() throws SQLException, ClassNotFoundException {
        klijent = Controller.Session.getCurrentKlijent();
        if (klijent != null) {
            sistem_korisnicko_ime.setText(klijent.getKorisnickoIme());
            sistem_ime_prezime.setText(klijent.getIme() + " " + klijent.getPrezime());
            postaviStanjeNovca();
            postavi_combo_vrijednosti();
            postavi_novac_klijenta();
            postavi_rezervacije();
            postavi_aranzmane();
        }
    }


    public double dohvatiStanjeNovca(String brojRacuna) {
        double stanje = 0.0;
        for(BankovniRacun b:listaBankovnihRacuna()){
            if(b.getBrojRacuna().equals(brojRacuna)){
                stanje=b.getStanje();
            }
        }
        return stanje;
    }

    public void postaviStanjeNovca() {
        Klijent trenutniKlijent = Controller.Session.getCurrentKlijent();
        if (trenutniKlijent != null) {
            double stanje = dohvatiStanjeNovca(trenutniKlijent.getBrojRacuna());
            stanje_novca.setText(String.format("%.2f", stanje));
        }
    }


    @FXML
    private void rezervacije_klik(ActionEvent event){
        anchorpane_rezervacije.setVisible(true);
        anchorpane_nova_rezervacija.setVisible(false);
    }

    @FXML
    private void nova_rezervacija_klik(ActionEvent event){
        anchorpane_rezervacije.setVisible(false);
        anchorpane_nova_rezervacija.setVisible(true);
    }



    @FXML private TabPane rezervacijeTable,izletiTable;
    @FXML
    private Tab aktivne,protekle,otkazane, jednodnevna, visednevna;
    @FXML
    private TextField iznos_doplate;
    @FXML
    private PasswordField potvrda_lozinke;
    @FXML
    private Label potroseno_aranzmani, potrebno_doplatiti;


    @FXML
    private ListView<String> jednodnevna_lista, visednevna_lista, moje_aktivne, moje_protekle, moje_otkazane;
    private ListView<Aranzman> jednoAranzman= new ListView<>();
    private ListView<Aranzman> viseAranzman= new ListView<>();
    private ListView<Aranzman> a_moje_aktivne= new ListView<>();
    private ListView<Aranzman> a_moje_protekle= new ListView<>();
    private ListView<Aranzman> a_moje_otkazane= new ListView<>();

    private void postavi_aranzmane(){
        jednodnevna_lista.getItems().clear();
        visednevna_lista.getItems().clear();
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

    @FXML
    public void rezervacija_izleta(MouseEvent event) {
        String aranzman_string = jednodnevna_lista.getSelectionModel().getSelectedItem();
        int indeks = jednodnevna_lista.getSelectionModel().getSelectedIndex();

        if(indeks!=-1){
            Aranzman aranzman = jednoAranzman.getItems().get(indeks);
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Potvrda rezervacije");
            dialog.setHeaderText("Unesite lozinku za potvrdu rezervacije:");
            dialog.setContentText("Lozinka:");

            dialog.showAndWait().ifPresent(lozinka -> {
                if (lozinka.equals(klijent.getLozinka())) {
                    int indikator=0;
                    for(Aranzman a:a_moje_aktivne.getItems()){
                        if(a.getId()==aranzman.getId()){
                            indikator++;
                        }
                    }
                    if(indikator==0){
                        if(aranzman.getDatumPolaska().isBefore(LocalDate.now().plusDays(15))){
                            prikaziAlert("Kasno za rezervaciju.", Alert.AlertType.INFORMATION);
                            return;
                        }
                        double potroseno_trenutni = aranzman.getCijenaAranzmana()/2;
                        double potroseno = Double.parseDouble(potroseno_aranzmani.getText())+(aranzman.getCijenaAranzmana()/2);
                        int indikator2=0;
                        for(Aranzman a:a_moje_otkazane.getItems()){
                            if(a.getId()==aranzman.getId()){
                                indikator2++;
                            }
                        }
                        int indikator3=0;
                        for(Aranzman a: a_moje_protekle.getItems()){
                            if(a.getId()==aranzman.getId()){
                                indikator3++;
                            }
                        }
                        if (indikator3>0) {
                            int i = 0;
                            for (Aranzman a:a_moje_protekle.getItems()){
                                if(a.getId()==aranzman.getId())
                                    break;
                                i++;
                            }
                            a_moje_protekle.getItems().remove(i);
                            moje_protekle.getItems().remove(i);
                            a_moje_aktivne.getItems().add(aranzman);
                            moje_aktivne.getItems().add(aranzman_string+" | Plaćeno: "+Double.parseDouble(String.valueOf(potroseno_trenutni+2))+ " | Preostalo: "+Double.parseDouble(String.valueOf(potroseno_trenutni-1)));
                            azurirajPlacenuCijenuRezervacije(klijent.getId(),aranzman.getId(),potroseno_trenutni+2);
                            prikaziAlert("Uspjesno rezervisano!", Alert.AlertType.CONFIRMATION);
                            potroseno_aranzmani.setText(String.valueOf(potroseno));
                            if(Double.parseDouble(potrebno_doplatiti.getText())>0)
                                potrebno_doplatiti.setText(String.valueOf(Double.parseDouble(potrebno_doplatiti.getText())-aranzman.getUkupnaCijena()/2));
                            else
                                potrebno_doplatiti.setText(String.valueOf(potroseno));
                            stanje_novca.setText(String.valueOf(Double.parseDouble(stanje_novca.getText())-potroseno_trenutni));
                            AdminController.setZarada(listaBankovnihRacuna().getLast().getStanje()+potroseno_trenutni);
                            BankovniRacun banka = listaBankovnihRacuna().getLast();
                            azurirajStanjeRacuna(klijent.getId(),dohvatiStanjeNovca(klijent.getBrojRacuna())-potroseno_trenutni, banka.getId(), banka.getStanje()+potroseno_trenutni);
                            for(Rezervacija r:listaRezervacija()){
                                if(r.getAranzman()==aranzman.getId()){
                                    r.setPlacena_cijena(String.valueOf(potroseno_trenutni));
                                    break;
                                }
                            }
                        }
                        else if(indikator2==0){
                            a_moje_aktivne.getItems().add(aranzman);
                            moje_aktivne.getItems().add(aranzman_string+" | Plaćeno: "+potroseno_trenutni+ " | Preostalo: "+potroseno_trenutni);
                            prikaziAlert("Uspjesno rezervisano!", Alert.AlertType.CONFIRMATION);
                            potroseno_aranzmani.setText(String.valueOf(potroseno));
                            potrebno_doplatiti.setText(String.valueOf(potroseno));
                            stanje_novca.setText(String.valueOf(Double.parseDouble(stanje_novca.getText())-potroseno_trenutni));
                            AdminController.setZarada(listaBankovnihRacuna().getLast().getStanje()+potroseno_trenutni);
                            BankovniRacun banka = listaBankovnihRacuna().getLast();
                            azurirajStanjeRacuna(klijent.getId(),dohvatiStanjeNovca(klijent.getBrojRacuna())-potroseno_trenutni, banka.getId(), banka.getStanje()+potroseno_trenutni);
                            Database.dodajRezervaciju(klijent.getId(),aranzman.getId(),String.valueOf(potroseno_trenutni*2),String.valueOf(potroseno_trenutni));
                        } else {
                            int i = 0;
                            for (Aranzman a:a_moje_otkazane.getItems()){
                                if(a.getId()==aranzman.getId())
                                    break;
                                i++;
                            }
                            a_moje_otkazane.getItems().remove(i);
                            moje_otkazane.getItems().remove(i);
                            a_moje_aktivne.getItems().add(aranzman);
                            moje_aktivne.getItems().add(aranzman_string+" | Plaćeno: "+ (potroseno_trenutni - 1) + " | Preostalo: "+(potroseno_trenutni+1));
                            azurirajPlacenuCijenuRezervacije(klijent.getId(),aranzman.getId(),potroseno_trenutni+1);
                            prikaziAlert("Uspjesno rezervisano!", Alert.AlertType.CONFIRMATION);
                            potroseno_aranzmani.setText(String.valueOf(potroseno));
                            if(Double.parseDouble(potrebno_doplatiti.getText())>0)
                                potrebno_doplatiti.setText(String.valueOf(Double.parseDouble(potrebno_doplatiti.getText())+aranzman.getUkupnaCijena()/2));
                            else
                                potrebno_doplatiti.setText(String.valueOf(potroseno));
                            stanje_novca.setText(String.valueOf(Double.parseDouble(stanje_novca.getText())-potroseno_trenutni));
                            AdminController.setZarada(listaBankovnihRacuna().getLast().getStanje()+potroseno_trenutni);
                            BankovniRacun banka = listaBankovnihRacuna().getLast();
                            azurirajStanjeRacuna(klijent.getId(),dohvatiStanjeNovca(klijent.getBrojRacuna())-potroseno_trenutni, banka.getId(), banka.getStanje()+potroseno_trenutni);
                            for(Rezervacija r:listaRezervacija()){
                                if(r.getAranzman()==aranzman.getId()){
                                    r.setPlacena_cijena(String.valueOf(potroseno_trenutni));
                                    break;
                                }
                            }
                            postavi_rezervacije();
                        }

                    }else {
                        prikaziAlert("Vec imate ovu rezervaciju", Alert.AlertType.ERROR);
                    }

                }else {
                    prikaziAlert("Lozinka nije tacna!", Alert.AlertType.ERROR);
                }

            });
        }else {
            prikaziAlert("Izaberite aranzman.", Alert.AlertType.INFORMATION);
        }





    }

    @FXML
    public void rezervacija_putovanja(MouseEvent event) {
        String aranzman_string = visednevna_lista.getSelectionModel().getSelectedItem();
        int indeks = visednevna_lista.getSelectionModel().getSelectedIndex();
        if(indeks!=-1){
            Aranzman aranzman = viseAranzman.getItems().get(indeks);
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Potvrda rezervacije");
            dialog.setHeaderText("Unesite lozinku za potvrdu rezervacije:");
            dialog.setContentText("Lozinka:");

            dialog.showAndWait().ifPresent(lozinka -> {
                if (lozinka.equals(klijent.getLozinka())) {
                    int indikator=0;
                    for(Aranzman a:a_moje_aktivne.getItems()){
                        if(a==aranzman){
                            indikator++;
                        }
                    }
                    if(indikator==0){
                        double potroseno_trenutni = aranzman.getUkupnaCijena()/2;
                        double potroseno = Double.parseDouble(potroseno_aranzmani.getText())+aranzman.getUkupnaCijena()/2;
                        int indikator2=0;
                        for(Aranzman a:a_moje_otkazane.getItems()){
                            if(a.getId()==aranzman.getId()){
                                indikator2++;
                            }
                        }
                        if(indikator2==0){
                            a_moje_aktivne.getItems().add(aranzman);
                            moje_aktivne.getItems().add(aranzman_string+" | Plaćeno: "+potroseno_trenutni+ " | Preostalo: "+potroseno_trenutni);
                            prikaziAlert("Uspjesno rezervisano!", Alert.AlertType.CONFIRMATION);
                            potroseno_aranzmani.setText(String.valueOf(potroseno));
                            potrebno_doplatiti.setText(String.valueOf(potroseno));
                            stanje_novca.setText(String.valueOf(Double.parseDouble(stanje_novca.getText())-potroseno_trenutni));
                            AdminController.setZarada(listaBankovnihRacuna().getLast().getStanje()+potroseno_trenutni);
                            BankovniRacun banka = listaBankovnihRacuna().getLast();
                            azurirajStanjeRacuna(klijent.getId(),dohvatiStanjeNovca(klijent.getBrojRacuna())-potroseno_trenutni, banka.getId(), banka.getStanje()+potroseno_trenutni);
                            Database.dodajRezervaciju(klijent.getId(),aranzman.getId(),String.valueOf(aranzman.getUkupnaCijena()),String.valueOf(aranzman.getUkupnaCijena()/2));
                        }else {
                            int i = 0;
                            for (Aranzman a:a_moje_otkazane.getItems()){
                                if(a.getId()==aranzman.getId())
                                    break;
                                i++;
                            }
                            a_moje_otkazane.getItems().remove(i);
                            moje_otkazane.getItems().remove(i);
                            a_moje_aktivne.getItems().add(aranzman);
                            moje_aktivne.getItems().add(aranzman_string+" | Plaćeno: "+String.valueOf(potroseno_trenutni+1)+ " | Preostalo: "+String.valueOf(potroseno_trenutni-1));
                            azurirajPlacenuCijenuRezervacije(klijent.getId(),aranzman.getId(),potroseno_trenutni+1);
                            prikaziAlert("Uspjesno rezervisano!", Alert.AlertType.CONFIRMATION);
                            potroseno_aranzmani.setText(String.valueOf(potroseno));
                            potrebno_doplatiti.setText(String.valueOf(potroseno));
                            stanje_novca.setText(String.valueOf(Double.parseDouble(stanje_novca.getText())-potroseno_trenutni));
                            AdminController.setZarada(listaBankovnihRacuna().getLast().getStanje()+potroseno_trenutni);
                            BankovniRacun banka = listaBankovnihRacuna().getLast();
                            azurirajStanjeRacuna(klijent.getId(),dohvatiStanjeNovca(klijent.getBrojRacuna())-potroseno_trenutni, banka.getId(), banka.getStanje()+potroseno_trenutni);
                            for(Rezervacija r:listaRezervacija()){
                                if(r.getAranzman()==aranzman.getId()){
                                    r.setPlacena_cijena(String.valueOf(potroseno_trenutni));
                                    break;
                                }
                            }
                        }

                    }else {
                        prikaziAlert("Vec imate ovu rezervaciju", Alert.AlertType.ERROR);
                    }

                }else {
                    prikaziAlert("Lozinka nije tacna!", Alert.AlertType.ERROR);
                }

            });
        }else {
            prikaziAlert("Izaberite aranzman.", Alert.AlertType.INFORMATION);
        }

    }

    private void postavi_rezervacije(){
        moje_aktivne.getItems().clear();
        moje_otkazane.getItems().clear();
        moje_protekle.getItems().clear();
        a_moje_aktivne.getItems().clear();
        a_moje_otkazane.getItems().clear();
        a_moje_protekle.getItems().clear();

        for (Rezervacija r:listaRezervacija()){
            if(klijent.getId()==r.getKlijent()){
                int id_rezervacije = r.getAranzman();
                for (Aranzman a:listaAranzmana()){
                    if(a.getId()==id_rezervacije){
                        if(Double.parseDouble(r.getPlacena_cijena())>0){
                            if(!a.getDatumPolaska().isBefore(LocalDate.now().plusDays(15))){

                                if(a.getDatumPolaska().isBefore(LocalDate.now().plusDays(18))){
                                    a_moje_aktivne.getItems().add(a);
                                    moje_aktivne.getItems().add("\uD83D\uDCB8 "+ a.toString()+" | Plaćeno: "+ (Double.parseDouble(r.getPlacena_cijena())) +" | Preostalo: "+ (Double.parseDouble(r.getUkupna_cijena()) - Double.parseDouble(r.getPlacena_cijena())));
                                    //int indeks = a_moje_aktivne.getItems().indexOf(a);


                                }else {
                                    a_moje_aktivne.getItems().add(a);
                                    moje_aktivne.getItems().add(a.toString()+" | Plaćeno: "+(Double.parseDouble(r.getPlacena_cijena()))+" | Preostalo: "+ (Double.parseDouble(r.getUkupna_cijena()) - Double.parseDouble(r.getPlacena_cijena())));
                                }
                            }else {
                                a_moje_protekle.getItems().add(a);
                                moje_protekle.getItems().add(a.toString());
                                azurirajPlacenuCijenuRezervacije(klijent.getId(),a.getId(),-Double.parseDouble(r.getPlacena_cijena())-2);
                            }

                        }else if(Double.parseDouble(r.getPlacena_cijena())==-1){
                            a_moje_otkazane.getItems().add(a);
                            moje_otkazane.getItems().add(a.toString());
                        }else if(Double.parseDouble(r.getPlacena_cijena())==-2){
                            a_moje_protekle.getItems().add(a);
                            moje_protekle.getItems().add(a.toString());
                        }

                    }
                }

            }

        }
        if(a_moje_aktivne.getItems().isEmpty()){
            potrebno_doplatiti.setText("0.0");
            potroseno_aranzmani.setText("0.0");
        }
    }




    private void postavi_novac_klijenta(){
        double potroseno=0,potrebno=0;
        for(Rezervacija r:listaRezervacija()){
            if(r.getKlijent()==klijent.getId()){
                potroseno+=Double.parseDouble(r.getPlacena_cijena());
                potrebno+=Double.parseDouble(r.getUkupna_cijena())-Double.parseDouble(r.getPlacena_cijena());
            }
        }
        potroseno_aranzmani.setText(String.valueOf(potroseno));
        potrebno_doplatiti.setText(String.valueOf(potrebno));
    }



    @FXML
    private ComboBox<String> combo_vrsta_sobe, combo_prevoz, combo_broj_zvjezdica;
    private String[] vrste_soba = {"Jednokrevetna", "Dvokrevetna", "Trokrevetna", "Apartman"};
    private String[] nacin_prevoza = {"Avion", "Autobus", "Samostalan"};
    private String[] broj_zvjezdica = {"1", "2", "3", "4", "5"};

    private void postavi_combo_vrijednosti(){
        combo_vrsta_sobe.getItems().addAll(vrste_soba);
        combo_prevoz.getItems().addAll(nacin_prevoza);
        combo_broj_zvjezdica.getItems().addAll(broj_zvjezdica);
    }


    @FXML private DatePicker datePicker_polazak, datePicker_dolazak;
    @FXML private Slider slider_max_cijena;
    @FXML private TextField izabrana_destinacija;



    @FXML
    private void filtriraj_klik(){
        jednodnevna_lista.getItems().clear();
        jednoAranzman.getItems().clear();
        visednevna_lista.getItems().clear();
        viseAranzman.getItems().clear();
        postavi_aranzmane();
        LocalDate izabran_polazak = datePicker_polazak.getValue();
        LocalDate izabran_dolazak = datePicker_dolazak.getValue();
        String izabrana_soba = combo_vrsta_sobe.getValue();
        String izabran_prevoz = combo_prevoz.getValue();
        String izabrana_destinacijaa = izabrana_destinacija.getText();
        String izabrane_zvjezdice = combo_broj_zvjezdica.getValue();
        double izabrana_cijena = slider_max_cijena.getValue();
        List<Aranzman> jednoAranzmanList = jednoAranzman.getItems();
        List<Aranzman> viseAranzmanList = viseAranzman.getItems();
        List<Aranzman> filtriraniJednoAranzmani = jednoAranzmanList.stream()
                .filter(a -> izabran_polazak == null || a.getDatumPolaska().equals(izabran_polazak))
                .filter(a -> izabran_dolazak == null || a.getDatumDolaska().equals(izabran_dolazak))
                .filter(a -> izabran_prevoz == null || izabran_prevoz.isEmpty() || a.getPrevoz().equalsIgnoreCase(izabran_prevoz))
                .filter(a -> izabrana_destinacijaa == null || izabrana_destinacijaa.isEmpty() || a.getDestinacija().equalsIgnoreCase(izabrana_destinacijaa))
                .filter(a -> a.getUkupnaCijena() <= izabrana_cijena)
                .collect(Collectors.toList());
        List<Aranzman> filtriraniViseAranzmani = viseAranzmanList.stream()
                .filter(a -> izabran_polazak == null || a.getDatumPolaska().equals(izabran_polazak))
                .filter(a -> izabran_dolazak == null || a.getDatumDolaska().equals(izabran_dolazak))
                .filter(a -> izabran_prevoz == null || izabran_prevoz.isEmpty() || a.getPrevoz().equalsIgnoreCase(izabran_prevoz))
                .filter(a -> izabrana_destinacijaa == null || izabrana_destinacijaa.isEmpty() || a.getDestinacija().equalsIgnoreCase(izabrana_destinacijaa))
                .filter(a -> a.getUkupnaCijena() <= izabrana_cijena)
                .filter(a -> {
                    Smjestaj smjestaj = listaSmjestaja().stream()
                            .filter(s -> s.getId() == a.getSmjestajId().getId())
                            .findFirst()
                            .orElse(null);
                    return smjestaj != null &&
                            (izabrana_soba == null || izabrana_soba.isEmpty() || smjestaj.getVrsta_sobe().equalsIgnoreCase(izabrana_soba)) &&
                            (izabrane_zvjezdice == null || izabrane_zvjezdice.isEmpty() || smjestaj.getBroj_zvjezdica().equalsIgnoreCase(izabrane_zvjezdice));
                })
                .collect(Collectors.toList());

        jednoAranzman.getItems().clear();
        jednodnevna_lista.getItems().clear();
        for(Aranzman a:filtriraniJednoAranzmani){
            jednoAranzman.getItems().add(a);
            jednodnevna_lista.getItems().add(a.toString());
        }
        viseAranzman.getItems().clear();
        visednevna_lista.getItems().clear();
        for(Aranzman a:filtriraniViseAranzmani){
            for(Smjestaj s:listaSmjestaja()){
                if(s.getId()==a.getSmjestajId().getId()){
                    String putovanje = a.toString()+" | "+s.getNaziv()+" | "+s.getVrsta_sobe()+" | "+s.getBroj_zvjezdica()+"☆";
                    viseAranzman.getItems().add(a);
                    visednevna_lista.getItems().add(putovanje);
                }
            }
        }
    }



    @FXML
    private void ukloni_filtere_klik(){
        if(jednodnevna.isSelected()){
            postavi_aranzmane();
            datePicker_polazak.setValue(null);
            datePicker_dolazak.setValue(null);
            combo_vrsta_sobe.setValue(null);
            combo_prevoz.setValue(null);
            combo_broj_zvjezdica.setValue(null);
            slider_max_cijena.setValue(slider_max_cijena.getMax());
            izabrana_destinacija.setText("");
        }
        if(visednevna.isSelected()){
            postavi_aranzmane();
            datePicker_polazak.setValue(null);
            datePicker_dolazak.setValue(null);
            combo_vrsta_sobe.setValue(null);
            combo_prevoz.setValue(null);
            combo_broj_zvjezdica.setValue(null);
            slider_max_cijena.setValue(slider_max_cijena.getMax());
            izabrana_destinacija.setText("");
        }

    }



    @FXML
    private void doplati_klik() {
        int indeks = moje_aktivne.getSelectionModel().getSelectedIndex();
        if (indeks != -1) {
            Aranzman aranzman = a_moje_aktivne.getItems().get(indeks);
            String iznosDoplate = iznos_doplate.getText();
            String potvrdaLozinke = potvrda_lozinke.getText();

            if (aranzman == null) {
                prikaziAlert("Izaberite rezervaciju", Alert.AlertType.ERROR);
                return;
            }

            if (!potvrdaLozinke.equals(klijent.getLozinka())) {
                potvrda_lozinke.requestFocus();
                prikaziAlert("Lozinka nije ispravna", Alert.AlertType.ERROR);
                return;
            }

            try {
                double iznos = Double.parseDouble(iznosDoplate);

                for (Rezervacija r : listaRezervacija()) {
                    if (klijent.getId() == r.getKlijent() && aranzman.getId() == r.getAranzman()) {
                        double preostaliIznos = Double.parseDouble(r.getUkupna_cijena()) - Double.parseDouble(r.getPlacena_cijena());

                        if (iznos <= preostaliIznos) {
                            for (BankovniRacun b : listaBankovnihRacuna()) {
                                if (klijent.getId() == b.getId()) {
                                    if (iznos <= b.getStanje()) {
                                        iznos_doplate.setText("");
                                        potvrda_lozinke.setText("");
                                        BankovniRacun banka = listaBankovnihRacuna().getLast();
                                        stanje_novca.setText(String.valueOf(Double.parseDouble(stanje_novca.getText()) - iznos));
                                        potroseno_aranzmani.setText(String.valueOf(Double.parseDouble(potroseno_aranzmani.getText()) + iznos));
                                        potrebno_doplatiti.setText(String.valueOf(Double.parseDouble(potrebno_doplatiti.getText()) - iznos));
                                        azurirajPlacenuCijenuRezervacije(klijent.getId(), aranzman.getId(), iznos);
                                        azurirajStanjeRacuna(klijent.getId(), b.getStanje()-iznos,banka.getId(),banka.getStanje()+iznos);
                                        String aranzman_string = aranzman.toString() + " | Plaćeno: " + r.getPlacena_cijena() + " | Preostalo: " + (preostaliIznos - iznos);
                                        moje_aktivne.getItems().remove(indeks);
                                        moje_aktivne.getItems().add(indeks, aranzman_string);
                                        postavi_rezervacije();
                                        prikaziAlert("Doplaćeno " + iznos + " za " + aranzman.getNazivPutovanja()+"\nVaše trenutno stanje na računu je: "+stanje_novca.getText(), Alert.AlertType.CONFIRMATION);
                                        return;
                                    } else {
                                        prikaziAlert("Nemate dovoljno novca na računu!", Alert.AlertType.ERROR);
                                        return;
                                    }
                                }
                            }
                        } else {
                            prikaziAlert("Iznos za uplatu je veći nego što je preostalo za plaćanje.", Alert.AlertType.ERROR);
                            iznos_doplate.requestFocus();
                            return;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                prikaziAlert("Iznos nije validan", Alert.AlertType.ERROR);
                iznos_doplate.requestFocus();
            }
        } else {
            prikaziAlert("Izaberite rezervaciju.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void otkazi_klik() {
        int indeks = moje_aktivne.getSelectionModel().getSelectedIndex();
        if (indeks != -1) {
            Aranzman aranzman = a_moje_aktivne.getItems().get(indeks);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrda otkazivanja");
            alert.setHeaderText("Da li ste sigurni da želite otkazati rezervaciju?");
            alert.setContentText("Otkazivanjem rezervacije novac će biti vraćen na vaš račun.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                for (Rezervacija r : listaRezervacija()) {
                    if (klijent.getId() == r.getKlijent() && aranzman.getId() == r.getAranzman()) {
                        double uplaceniIznos = Double.parseDouble(r.getPlacena_cijena());
                        BankovniRacun banka = listaBankovnihRacuna().getLast();
                        BankovniRacun racun_klijent = racun_iz_klijentId(klijent.getId());
                        azurirajStanjeRacuna(klijent.getId(), Double.parseDouble(stanje_novca.getText()) + uplaceniIznos, banka.getId(), banka.getStanje()-uplaceniIznos);
                        racun_klijent.setStanje(Double.parseDouble(stanje_novca.getText()) + uplaceniIznos);
                        stanje_novca.setText(String.valueOf(racun_klijent.getStanje()));
                        if(uplaceniIznos!=-1){
                            potrebno_doplatiti.setText(String.valueOf((Double.parseDouble(potrebno_doplatiti.getText()) - (Double.parseDouble(r.getUkupna_cijena())-Double.parseDouble(r.getPlacena_cijena())))));
                        }else {
                            potrebno_doplatiti.setText(String.valueOf((Double.parseDouble(potrebno_doplatiti.getText()) - Double.parseDouble(r.getUkupna_cijena()))));
                        }

                        moje_aktivne.getItems().remove(indeks);
                        a_moje_aktivne.getItems().remove(indeks);
                        otkaziRezervaciju(klijent.getId(),aranzman.getId());
                        a_moje_otkazane.getItems().add(aranzman);
                        moje_otkazane.getItems().add(aranzman.toString());
                        postavi_rezervacije();
                        prikaziAlert("Rezervacija je uspešno otkazana, novac je vraćen na račun.\nVaše trenutno stanje je: "+stanje_novca.getText(), Alert.AlertType.INFORMATION);
                        return;
                    }
                }
            }
        } else {
            prikaziAlert("Izaberite rezervaciju za otkazivanje.", Alert.AlertType.ERROR);
        }
    }





    private void prikaziAlert(String poruka, Alert.AlertType tip) {
        Alert alert = new Alert(tip);
        alert.setTitle("Obavještenje");
        alert.setHeaderText(null);
        alert.setContentText(poruka);
        alert.showAndWait();
    }



    @FXML
    private void promijeniLozinku(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PromjenaLozinkeKlijent.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Promjena lozinke");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node)event.getSource()).getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            Stage trenutniStage = (Stage) sistem_ime_prezime.getScene().getWindow();

            trenutniStage.close();
            klijent = null;
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
    private void klijent_izadji_dugme_klik(ActionEvent event) {
        center.setOpacity(1);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000),center);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            Stage stage = (Stage) center.getScene().getWindow();
            stage.close();
        });

        fadeOut.play();
    }
}
