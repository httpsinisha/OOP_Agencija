package agencija.agencijaprojekat;

import java.security.spec.ECField;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static String DB_user = "root";
    private static String DB_password = "";
    private static String connectionUrl;
    private static int port = 3306;
    private static String DB_name = "agencija";
    private static Connection connection;

    public static void DBConnect() throws SQLException , ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connectionUrl = "jdbc:mysql://localhost" + ":" + port + "/" + DB_name;
            connection = DriverManager.getConnection(connectionUrl, DB_user, DB_password);
        }
    }


    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if(connection==null || connection.isClosed()){
            DBConnect();
        }
        return connection;
    }

    public static void main(String[] args) {
        try {
            DBConnect();
            System.out.println("Uspjesno ste se konektovali na bazu:" + connectionUrl);
            ResultSet resultSet = null;
            Statement statement = connection.createStatement();
            String SQLQuery = "SELECT * FROM admin";
            resultSet = statement.executeQuery(SQLQuery);


            //String SQLInsert = "INSERT INTO admin VALUES (2, 'Milos', 'milos_milosevic', 'milos123' )";
            //statement.executeUpdate(SQLInsert);

            //String SQLUpdate = "UPDATE admin set lozinka = 'asdasd' WHERE korisnicko_ime='milos_milosevic'";
            //statement.executeUpdate(SQLUpdate);

            System.out.println("--------------------------------------------");
            while (resultSet.next()) {
                String result = resultSet.getString(1) + ", " + resultSet.getString(2)
                        + ", " + resultSet.getString(3) + ", " + resultSet.getString(4)
                        + ", " + resultSet.getString(5);
                System.out.println(result);
                System.out.println("--------------------------------------------");
            }

            //statement.executeUpdate(SQLUpdate);

            statement.close();
            //connection.close();
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        } //catch (ClassNotFoundException e) {
            //throw new RuntimeException(e);
        //}
    }


    public static ArrayList<Klijent> listaKlijenata(){
        ArrayList<Klijent> listaKlijenata = new ArrayList<>();
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLQuery = "SELECT * FROM klijent";

            ResultSet resultSet = statement.executeQuery(SQLQuery);
            resultSet = statement.executeQuery(SQLQuery);

            while (resultSet.next()) {
                Klijent klijent = new Klijent(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),resultSet.getString(6),resultSet.getString(7),resultSet.getString(8));
                listaKlijenata.add(klijent);
            }


            statement.close();
            //connection.close();

        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();

        }
        return listaKlijenata;
    }


    public static ArrayList<Admin> listaAdmina(){
        ArrayList<Admin> listaAdmina = new ArrayList<>();
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLQuery = "SELECT * FROM admin";

            ResultSet resultSet = statement.executeQuery(SQLQuery);

            resultSet = statement.executeQuery(SQLQuery);


            while (resultSet.next()) {
                Admin admin = new Admin(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5));
                listaAdmina.add(admin);
            }


            statement.close();
            //connection.close();

        } catch (SQLException e){
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return listaAdmina;
    }


    public static ArrayList<Smjestaj> listaSmjestaja() {
        ArrayList<Smjestaj> listaSmjestaja = new ArrayList<>();
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLQuery = "SELECT * FROM smjestaj";

            //resultSet = statement.executeQuery(SQLQuery);
            ResultSet resultSet = statement.executeQuery(SQLQuery);

            //System.out.println("--------------------------------------------");
            while (resultSet.next()) {
                Smjestaj smjestaj = new Smjestaj(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5));
                listaSmjestaja.add(smjestaj);
            }
            statement.close();
            //connection.close();
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return listaSmjestaja;
    }





    public static Smjestaj smjestaj_iz_id(int smjestajId) throws SQLException, ClassNotFoundException {
        Smjestaj smjestaj = null;
        String sqlQuery = "SELECT * FROM smjestaj WHERE id = " + smjestajId;
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLQuery = "SELECT * FROM smjestaj";
            ResultSet resultSet = statement.executeQuery(SQLQuery);

            if (resultSet.next()) {
                String naziv = resultSet.getString("naziv");
                String brojZvjezdica = resultSet.getString("broj_zvjezdica");
                String vrstaSobe = resultSet.getString("vrsta_sobe");
                String cijenaPoNocenju = resultSet.getString("cjena_po_nocenju");

                smjestaj = new Smjestaj(smjestajId, naziv, brojZvjezdica, vrstaSobe, cijenaPoNocenju);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return smjestaj;
    }





    public static BankovniRacun racun_iz_klijentId(int klijentId){
        BankovniRacun bankovniRacun = null;
        String sqlQueryAranzman = "SELECT * FROM bankovni_racun WHERE id = '" + klijentId + "'";
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLQuery = "SELECT * FROM bankovni_racun";
            ResultSet resultSet = statement.executeQuery(SQLQuery);
            if (resultSet.next()) {
                String brojRacuna = resultSet.getString("broj_racuna");
                String jmbg = resultSet.getString("jmbg");
                double stanje = resultSet.getDouble("stanje");
                bankovniRacun = new BankovniRacun(klijentId, brojRacuna, jmbg, stanje);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bankovniRacun;
    }







    public static ArrayList<Rezervacija> listaRezervacija() {
        ArrayList<Rezervacija> listaRezervacija = new ArrayList<>();
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLQuery = "SELECT * FROM rezervacija";
            ResultSet resultSet = statement.executeQuery(SQLQuery);
            while (resultSet.next()) {
                Rezervacija r = new Rezervacija(resultSet.getInt(1), resultSet.getInt(2), resultSet.getString(3),resultSet.getString(4));
                listaRezervacija.add(r);
            }
            statement.close();
            //connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return listaRezervacija;
    }


    public static ArrayList<Aranzman> listaAranzmana() {
        ArrayList<Aranzman> listaAranzmana = new ArrayList<>();
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLQuery = "SELECT * FROM aranzman";
            ResultSet resultSet = statement.executeQuery(SQLQuery);
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String nazivPutovanja = resultSet.getString(2);
                String destinacija = resultSet.getString(3);
                String prevoz = resultSet.getString(4);
                LocalDate datumPolaska = resultSet.getDate(5).toLocalDate();
                LocalDate datumDolaska = resultSet.getDate(6).toLocalDate();
                double cijenaAranzmana = resultSet.getDouble(7);
                int smjestajId = resultSet.getInt(8);
                Smjestaj s = smjestaj_iz_id(smjestajId);
                Aranzman a = new Aranzman(id, nazivPutovanja, destinacija, prevoz, datumPolaska, datumDolaska, cijenaAranzmana, s);
                listaAranzmana.add(a);
            }
            statement.close();
            //connection.close();
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return listaAranzmana;
    }


    public static ArrayList<BankovniRacun> listaBankovnihRacuna(){
        ArrayList<BankovniRacun> listaRacuna = new ArrayList<>();
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLQuery = "SELECT * FROM bankovni_racun";
            ResultSet resultSet = statement.executeQuery(SQLQuery);
            while (resultSet.next()) {
                BankovniRacun b = new BankovniRacun(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),Double.parseDouble(resultSet.getString(4)));
                listaRacuna.add(b);
            }
            statement.close();
            //connection.close();
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return listaRacuna;
    }

    public static void dodajKlijenta(int id, String ime, String prezime, String brojTelefona, String jmbg, String brojRacuna, String korisnickoIme, String lozinka){
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLinsert = "INSERT INTO klijent VALUES(" + id + ",'" + ime + "','"+prezime+"','"+ brojTelefona +"','"+jmbg+"','"+brojRacuna+"','"+korisnickoIme+"','"+lozinka+"')";
            statement.executeUpdate(SQLinsert);
            statement.close();
            //connection.close();
            listaKlijenata().add(new Klijent(id, ime, prezime, brojTelefona, jmbg, brojRacuna, korisnickoIme, lozinka));
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void dodajAdmina(int id, String ime,String prezime,String korisnickoIme){
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLinsert = "INSERT INTO admin (id, ime, prezime, korisnicko_ime, lozinka) VALUES ('" + id + "','" + ime + "','" + prezime + "','" + korisnickoIme + "','12345678')";

            statement.executeUpdate(SQLinsert);
            statement.close();
            //connection.close();
            listaAdmina().add(new Admin(id,ime,prezime,korisnickoIme,"12345678"));
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void promijeniSifruKlijenta(int klijentId,String novaLozinka) {
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLupdate = "UPDATE klijent set lozinka  = '"+novaLozinka+"' WHERE id = "+klijentId+"";
            statement.executeUpdate(SQLupdate);
            statement.close();
            //connection.close();
            for (Klijent k:listaKlijenata()) {
                if(k.getId()==klijentId){
                    k.setLozinka(novaLozinka);
                }
            }
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();

        }
    }

    public static void promijeniSifruAdmina(String korisnicko_ime,String novaLozinka) {
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLupdate = "UPDATE admin set lozinka  = '"+novaLozinka+"' WHERE korisnicko_ime = '"+korisnicko_ime+"'";
            statement.executeUpdate(SQLupdate);
            statement.close();
            //connection.close();
            for (Admin a:listaAdmina()) {
                if(a.getKorisnickoIme().equals(korisnicko_ime)){
                    a.setLozinka(novaLozinka);
                }
            }
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }




    public static void dodajRezervaciju(int klijentId,int aranzmanId,String ukupnaCijena,String placenaCijena) {
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLinsert = "INSERT INTO rezervacija VALUES(" + klijentId + ",'" + aranzmanId + "','"+ukupnaCijena+"','"+placenaCijena+"')";
            statement.executeUpdate(SQLinsert);
            statement.close();
            //connection.close();
            listaRezervacija().add(new Rezervacija(klijentId, aranzmanId, ukupnaCijena, placenaCijena));
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();

        }
    }

    public static void otkaziRezervaciju(int klijentId, int aranzmanId) {
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLupdate = "UPDATE rezervacija SET placena_cijena = -1 WHERE klijent_id=" + klijentId + " AND aranzman_id=" + aranzmanId;
            statement.executeUpdate(SQLupdate);
            statement.close();
            //connection.close();
            /*for (Rezervacija rezervacija : listaRezervacija()) {
                if (rezervacija.getKlijent() == klijentId && rezervacija.getAranzman() == aranzmanId) {
                    rezervacija.setPlacena_cijena("-1");
                    break;
                }
            }*/
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void otkaziAranzman(int aranzman_id, int klijent_id) throws SQLException, ClassNotFoundException {
        DBConnect();
        Statement statement = connection.createStatement();
        String SQLupdate = "DELETE FROM rezervacija WHERE klijent_id=" + klijent_id + " AND aranzman_id=" + aranzman_id;
        statement.executeUpdate(SQLupdate);
        statement.close();
        for(Rezervacija r:listaRezervacija()){
            if(r.getAranzman()==aranzman_id && r.getKlijent()==klijent_id)
                listaRezervacija().remove(r);
        }
    }

    public static void obrisiAranzman(int aranzman_id) throws SQLException, ClassNotFoundException {
        DBConnect();
        Statement statement = connection.createStatement();
        String SQLupdate = "DELETE FROM aranzman WHERE id=" + aranzman_id;
        statement.executeUpdate(SQLupdate);
        statement.close();
        for(Aranzman a:listaAranzmana()){
            if(a.getId()==aranzman_id)
                listaAranzmana().remove(a);
        }
    }







    public static void dodajSmjestaj(int id,String naziv,String broj_zvjezdica,String vrsta_sobe,String cijena_po_nocenju){
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLinsert = "INSERT INTO smjestaj VALUES(" + id + ",'" + naziv + "','"+broj_zvjezdica+"','"+vrsta_sobe+"','"+cijena_po_nocenju+"')";
            statement.executeUpdate(SQLinsert);
            statement.close();
            //connection.close();
            listaSmjestaja().add(new Smjestaj(id, naziv, broj_zvjezdica, vrsta_sobe, cijena_po_nocenju));
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();

        }
    }


    public static void dodajPutovanje(int id, String nazivPutovanja, String destinacija, String prevoz, LocalDate datumPolaska, LocalDate datumDolaska, double cijenaAranzmana, Smjestaj smjestajId)
    {
        try {
            DBConnect();
            String SQLinsert = "INSERT INTO aranzman VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLinsert)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, nazivPutovanja);
                preparedStatement.setString(3, destinacija);
                preparedStatement.setString(4, prevoz);
                preparedStatement.setDate(5, Date.valueOf(datumPolaska));
                preparedStatement.setDate(6, Date.valueOf(datumDolaska));
                preparedStatement.setDouble(7, cijenaAranzmana);
                preparedStatement.setInt(8, smjestajId.getId());

                preparedStatement.executeUpdate();
                listaAranzmana().add(new Aranzman(id, nazivPutovanja, destinacija, prevoz, datumPolaska, datumDolaska, cijenaAranzmana, smjestajId));
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //connection.close();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    public static void dodajIzlet(int id, String nazivPutovanja, String destinacija, LocalDate datumPolaska, double cijenaAranzmana) {
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLinsert = "INSERT INTO aranzman VALUES('"+id+"','"+nazivPutovanja+"','"+destinacija+"','Autobus','"+datumPolaska+"','"+datumPolaska+"','"+cijenaAranzmana+"',"+null+")";
            statement.executeUpdate(SQLinsert);
            statement.close();
            //connection.close();
            listaAranzmana().add(new Aranzman(id, nazivPutovanja, destinacija, "Autobus", datumPolaska, datumPolaska, cijenaAranzmana, null));
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();

        }
    }


    public static void azurirajPlacenuCijenuRezervacije(int klijentId, int aranzmanId, double cijena) {
        try {
            DBConnect();

            String updateQuery = "UPDATE rezervacija SET placena_cijena = placena_cijena + ? WHERE Klijent_id = ? AND Aranzman_id = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setDouble(1, cijena);
                updateStatement.setInt(2, klijentId);
                updateStatement.setInt(3, aranzmanId);
                updateStatement.executeUpdate();
            }

            for(Rezervacija r:listaRezervacija()){
                if(r.getKlijent()==klijentId && r.getAranzman()==aranzmanId){
                    r.setPlacena_cijena(String.valueOf(Double.parseDouble(r.getPlacena_cijena())+cijena));
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } /*finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }*/
    }

    public static List<Klijent> listaKlijenataZaAranzman(Aranzman aranzman) {
        List<Klijent> rezervisaniKlijenti = new ArrayList<>();

        for (Rezervacija rezervacija : listaRezervacija()) {
            if (rezervacija.getAranzman() == aranzman.getId()) {
                for(Klijent k: listaKlijenata()){
                    if(k.getId()==rezervacija.getKlijent()){
                        rezervisaniKlijenti.add(k);
                    }
                }
            }
        }

        return rezervisaniKlijenti;
    }


    public static void azurirajStanjeRacuna(int id1,double iznos1, int id2, double iznos2){
        try {
            DBConnect();
            Statement statement = connection.createStatement();
            String SQLupdate = "UPDATE bankovni_racun set stanje  = "+iznos1+" WHERE id = "+id1;
            statement.executeUpdate(SQLupdate);
            String SQLupdate2 = "UPDATE bankovni_racun set stanje  = "+iznos2+" WHERE id = "+id2;
            statement.executeUpdate(SQLupdate2);
            statement.close();
           //connection.close();
            for (BankovniRacun b:listaBankovnihRacuna()) {
                if(b.getId()==id1){
                    b.setStanje(iznos1);
                }
                if(b.getId()==id2){
                    b.setStanje(iznos2);
                }
            }
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }




    public static int pronadiPrviSlobodanIdAranzmana(List<Aranzman> listaAranzmana) {
        int velicinaListe = listaAranzmana.size();
        if (velicinaListe == 0) {
            return 1;
        }
        for (int i = 1; i <= velicinaListe + 1; i++) {
            boolean idZauzet = false;

            for (Aranzman aranzman : listaAranzmana) {
                if (aranzman.getId() == i) {
                    idZauzet = true;
                    break;
                }
            }
            if (!idZauzet) {
                return i;
            }
        }
        return velicinaListe + 1;
    }

    public static int pronadiPrviSlobodanIdSmjestaja(List<Smjestaj> listaSmjestaja) {
        int velicinaListe = listaSmjestaja.size();
        if (velicinaListe == 0) {
            return 1;
        }
        for (int i = 1; i <= velicinaListe + 1; i++) {
            boolean idZauzet = false;
            for (Smjestaj s : listaSmjestaja) {
                if (s.getId() == i) {
                    idZauzet = true;
                    break;
                }
            }
            if (!idZauzet) {
                return i;
            }
        }
        return velicinaListe + 1;
    }


    public static int pronadiPrviSlobodanIdAdmina(List<Admin> listaAdmina) {
        int velicinaListe = listaAdmina.size();
        if (velicinaListe == 0) {
            return 1;
        }
        for (int i = 1; i <= velicinaListe + 1; i++) {
            boolean idZauzet = false;
            for (Admin a : listaAdmina) {
                if (a.getId() == i) {
                    idZauzet = true;
                    break;
                }
            }
            if (!idZauzet) {
                return i;
            }
        }
        return velicinaListe + 1;
    }



}
