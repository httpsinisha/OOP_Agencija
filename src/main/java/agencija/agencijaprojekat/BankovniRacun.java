package agencija.agencijaprojekat;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BankovniRacun {
    private int id;
    private String brojRacuna;
    private String jmbg;
    private double stanje;

    public BankovniRacun(int id, String brojRacuna, String jmbg, double stanje) {
        this.id = id;
        this.brojRacuna = brojRacuna;
        this.jmbg = jmbg;
        this.stanje = stanje;
    }

    public BankovniRacun(){}

    public int getId() {
        return id;
    }

    public String getBrojRacuna() {
        return brojRacuna;
    }

    public String getJmbg() {
        return jmbg;
    }

    public double getStanje() {
        return stanje;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBrojRacuna(String brojRacuna) {
        this.brojRacuna = brojRacuna;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public void setStanje(double stanje) {
        this.stanje = stanje;
    }

    public static BankovniRacun fromResultSet(ResultSet resultSet) throws SQLException {
        BankovniRacun racun = new BankovniRacun();
        racun.setId(resultSet.getInt("id"));
        racun.setBrojRacuna(resultSet.getString("broj_racuna"));
        racun.setJmbg(resultSet.getString("jmbg"));
        racun.setStanje(resultSet.getDouble("stanje"));
        return racun;
    }

    @Override
    public String toString() {
        return "BankovniRacun{" +
                "id=" + id +
                ", brojRacuna='" + brojRacuna + '\'' +
                ", jmbg='" + jmbg + '\'' +
                ", stanje=" + stanje +
                '}';
    }
}
