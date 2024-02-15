package agencija.agencijaprojekat;

public class Rezervacija {

    private int klijent_id;
    private int aranzman_id;
    private String ukupna_cijena;
    private String placena_cijena;

    public Rezervacija(int klijent,int aranzman_id,String ukupna_cijena,String placena_cijena){
        this.klijent_id=klijent;
        this.aranzman_id=aranzman_id;
        this.ukupna_cijena=ukupna_cijena;
        this.placena_cijena=placena_cijena;
    }

    public int getKlijent() {
        return klijent_id;
    }

    public int getAranzman() {
        return aranzman_id;
    }

    public String getUkupna_cijena() {
        return ukupna_cijena;
    }

    public String getPlacena_cijena() {
        return placena_cijena;
    }

    public void setKlijent(int klijent) {
        this.klijent_id = klijent;
    }

    public void setAranzman_id(int aranzman_id) {
        this.aranzman_id = aranzman_id;
    }

    public void setUkupna_cijena(String ukupna_cijena) {
        this.ukupna_cijena = ukupna_cijena;
    }

    public void setPlacena_cijena(String placena_cijena) {
        this.placena_cijena = placena_cijena;
    }

    @Override
    public String toString() {
        return "Rezervacija{" +
                "klijent=" + klijent_id +
                ", aranzman_id=" + aranzman_id +
                ", ukupna_cijena='" + ukupna_cijena + '\'' +
                ", placena_cijena='" + placena_cijena + '\'' +
                '}';
    }
}
