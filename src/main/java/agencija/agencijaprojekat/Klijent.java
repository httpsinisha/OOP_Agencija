package agencija.agencijaprojekat;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Klijent {
    private int id;
    private String ime;
    private String prezime;
    private String brojTelefona;
    private String jmbg;
    private String brojRacuna;
    private String korisnickoIme;
    private String lozinka;

    public Klijent(int id, String ime, String prezime, String brojTelefona, String jmbg, String brojRacuna, String korisnickoIme, String lozinka) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.brojTelefona = brojTelefona;
        this.jmbg = jmbg;
        this.brojRacuna = brojRacuna;
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
    }

    public Klijent(){}

    public int getId() {
        return id;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getBrojTelefona() {
        return brojTelefona;
    }

    public String getJmbg() {
        return jmbg;
    }

    public String getBrojRacuna() {
        return brojRacuna;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public void setBrojTelefona(String brojTelefona) {
        this.brojTelefona = brojTelefona;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public void setBrojRacuna(String brojRacuna) {
        this.brojRacuna = brojRacuna;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }


    @Override
    public String toString() {
        return "Klijent{" +
                "id=" + id +
                ", ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", brojTelefona='" + brojTelefona + '\'' +
                ", jmbg='" + jmbg + '\'' +
                ", brojRacuna='" + brojRacuna + '\'' +
                ", korisnickoIme='" + korisnickoIme + '\'' +
                ", lozinka='" + lozinka + '\'' +
                '}';
    }
}
