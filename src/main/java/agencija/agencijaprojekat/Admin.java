package agencija.agencijaprojekat;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin {

    private int id;
    private String ime;
    private String prezime;
    private String korisnickoIme;
    private String lozinka;

    public Admin(int id,String ime, String prezime, String korisnickoIme, String lozinka) {
        this.id=id;
        this.ime = ime;
        this.prezime = prezime;
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
    }

    public Admin(){}

    public int getId() { return id; }
    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setId(int id) { this.id = id; }
    public void setIme(String ime) {
        this.ime = ime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public static Admin fromResultSet(ResultSet resultSet) throws SQLException {
        Admin admin = new Admin();
        admin.setIme(resultSet.getString("ime"));
        admin.setPrezime(resultSet.getString("prezime"));
        admin.setKorisnickoIme(resultSet.getString("korisnicko_ime"));
        admin.setLozinka(resultSet.getString("lozinka"));
        return admin;
    }

    @Override
    public String toString() {
        return "Admin{ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", korisnickoIme='" + korisnickoIme + '\'' +
                ", lozinka='" + lozinka + '\'' +
                '}';
    }
}
