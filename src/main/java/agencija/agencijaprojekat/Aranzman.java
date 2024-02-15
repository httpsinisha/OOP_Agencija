package agencija.agencijaprojekat;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Aranzman {

    private int id;
    private String nazivPutovanja;
    private String destinacija;
    private String prevoz;
    private LocalDate datumPolaska;
    private LocalDate datumDolaska;
    private double cijenaAranzmana, ukupnaCijena;
    private int brojDana;
    private Smjestaj smjestajId;

    public Aranzman(){}

    public Aranzman(int id, String nazivPutovanja, String destinacija, String prevoz, LocalDate datumPolaska, LocalDate datumDolaska, double cijenaAranzmana, Smjestaj smjestajId) throws SQLException, ClassNotFoundException {
        this.id = id;
        this.nazivPutovanja = nazivPutovanja;
        this.destinacija = destinacija;
        this.prevoz = prevoz;
        this.datumPolaska = datumPolaska;
        this.datumDolaska = datumDolaska;
        this.cijenaAranzmana = cijenaAranzmana;
        this.smjestajId = smjestajId;
        this.brojDana= (int) ChronoUnit.DAYS.between(datumPolaska,datumDolaska);
        if(smjestajId!=null){
            this.ukupnaCijena=brojDana*Double.parseDouble(smjestajId.getCijena_po_nocenju())+cijenaAranzmana;
        }else {
            this.ukupnaCijena=cijenaAranzmana;
        }

    }

    public int getId() {
        return id;
    }

    public String getNazivPutovanja() {
        return nazivPutovanja;
    }

    public String getDestinacija() {
        return destinacija;
    }

    public String getPrevoz() {
        return prevoz;
    }

    public LocalDate getDatumPolaska() {
        return datumPolaska;
    }

    public LocalDate getDatumDolaska() {
        return datumDolaska;
    }

    public double getCijenaAranzmana() {
        return cijenaAranzmana;
    }

    public double getUkupnaCijena() {
        return ukupnaCijena;
    }

    public Smjestaj getSmjestajId() {
        return smjestajId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNazivPutovanja(String nazivPutovanja) {
        this.nazivPutovanja = nazivPutovanja;
    }

    public void setDestinacija(String destinacija) {
        this.destinacija = destinacija;
    }

    public void setPrevoz(String prevoz) {
        this.prevoz = prevoz;
    }

    public void setDatumPolaska(LocalDate datumPolaska) {
        this.datumPolaska = datumPolaska;
    }

    public void setDatumDolaska(LocalDate datumDolaska) {
        this.datumDolaska = datumDolaska;
    }

    public void setCijenaAranzmana(double cijenaAranzmana) {
        this.cijenaAranzmana = cijenaAranzmana;
    }

    public void setSmjestajId(Smjestaj smjestajId) {
        this.smjestajId = smjestajId;
    }

    @Override
    public String toString() {
        if(datumPolaska.equals(datumDolaska)){
            return nazivPutovanja+" | "+destinacija+" | "+ prevoz+" | "+datumPolaska+" | Ukupna cijena: "+ukupnaCijena;
        }else {
            return nazivPutovanja+" | "+destinacija+" | "+ prevoz+" | "+datumPolaska+" | "+ datumDolaska+" | Aranzman: " +cijenaAranzmana+" | Noćenje: "+smjestajId.getCijena_po_nocenju()+" | Ukupna cijena: "+ukupnaCijena;
        }
    }
}
