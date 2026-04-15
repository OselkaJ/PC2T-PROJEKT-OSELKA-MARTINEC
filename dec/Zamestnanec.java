package dec;

//import database.Database;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Zamestnanec implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String jmeno;
    private String prijmeni;
    private int rokNarozeni;
    private Specializace specializace;
    private Map<Integer, Spoluprace> spolupracovnici;

    public Zamestnanec(int id, String jmeno, String prijmeni, int rokNarozeni, Specializace specializace) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.specializace = specializace;
        this.spolupracovnici = new HashMap<>();
    }

    // Gettery a settery
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getJmeno() { return jmeno; }
    public void setJmeno(String jmeno) { this.jmeno = jmeno; }

    public String getPrijmeni() { return prijmeni; }
    public void setPrijmeni(String prijmeni) { this.prijmeni = prijmeni; }

    public int getRokNarozeni() { return rokNarozeni; }
    public void setRokNarozeni(int rokNarozeni) { this.rokNarozeni = rokNarozeni; }

    public Specializace getSpecializace() { return specializace; }
    public void setSpecializace(Specializace specializace) { this.specializace = specializace; }

    public Map<Integer, Spoluprace> getSpolupracovnici() { return spolupracovnici; }

    // Pomocné metody pro spolupráci
    public void pridejSpolupraci(int idKolegy, Spoluprace uroven) {
        spolupracovnici.put(idKolegy, uroven);
    }

    public void odstranSpolupraci(int idKolegy) {
        spolupracovnici.remove(idKolegy);
    }

    public Spoluprace getUrovenSpoluprace(int idKolegy) {
        return spolupracovnici.get(idKolegy);
    }

    public int getPocetSpolupracovniku() {
        return spolupracovnici.size();
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Jméno: %s %s, Rok narození: %d, Skupina: %s",
                id, jmeno, prijmeni, rokNarozeni,
                specializace.getClass().getSimpleName());
    }
}