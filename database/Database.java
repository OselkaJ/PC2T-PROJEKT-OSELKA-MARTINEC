package database;

import dec.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class Database {
    private Map<Integer, Zamestnanec> zamestnanci;
    private int nextId;

    public Database() {
        zamestnanci = new HashMap<>();
        nextId = 1;
    }

    // ---------- Přidání zaměstnance ----------
    public void pridejZamestnance(String jmeno, String prijmeni, int rokNarozeni, String skupina) {
        Specializace spec;
        if ("analytik".equalsIgnoreCase(skupina)) {
            spec = new Datovyanalytik();
        } else if ("specialista".equalsIgnoreCase(skupina)) {
            spec = new Bezpecnostnispecialista();
        } else {
            throw new IllegalArgumentException("Neznámá skupina: " + skupina + ". Použijte 'analytik' nebo 'specialista'.");
        }

        Zamestnanec novy = new Zamestnanec(nextId++, jmeno, prijmeni, rokNarozeni, spec);
        zamestnanci.put(novy.getId(), novy);
        System.out.println("Zaměstnanec přidán s ID: " + novy.getId());
    }

    // ---------- Odebrání zaměstnance ----------
    public void odeberZamestnance(int id) {
        if (!zamestnanci.containsKey(id)) {
            System.out.println("Zaměstnanec s ID " + id + " neexistuje.");
            return;
        }
        zamestnanci.remove(id);
        // Odstranit ze seznamů spolupracovníků ostatních
        for (Zamestnanec z : zamestnanci.values()) {
            z.odstranSpolupraci(id);
        }
        System.out.println("Zaměstnanec s ID " + id + " byl odebrán.");
    }

    // ---------- Vyhledání podle ID ----------
    public Zamestnanec najdiPodleId(int id) {
        return zamestnanci.get(id);
    }

    // ---------- Přidání spolupráce (jednostranné) ----------
    public void pridejSpolupraci(int idZam, int idKolegy, Spoluprace uroven) {
        Zamestnanec zam = zamestnanci.get(idZam);
        Zamestnanec kolega = zamestnanci.get(idKolegy);
        if (zam == null) {
            System.out.println("Zaměstnanec s ID " + idZam + " neexistuje.");
            return;
        }
        if (kolega == null) {
            System.out.println("Zaměstnanec s ID " + idKolegy + " neexistuje.");
            return;
        }
        zam.pridejSpolupraci(idKolegy, uroven);
        System.out.println("Spolupráce přidána.");
    }

    // ---------- Všechny zaměstnanci ----------
    public Collection<Zamestnanec> getAllZamestnanci() {
        return zamestnanci.values();
    }

    // ---------- Abecední seznam podle příjmení ----------
    public List<Zamestnanec> getZamestnanciAbecedne() {
        List<Zamestnanec> seznam = new ArrayList<>(zamestnanci.values());
        seznam.sort(Comparator.comparing(Zamestnanec::getPrijmeni).thenComparing(Zamestnanec::getJmeno));
        return seznam;
    }

    // ---------- Výpis podle skupin (abecedně) ----------
    public void vypisAbecednePodleSkupin() {
        List<Zamestnanec> analytici = new ArrayList<>();
        List<Zamestnanec> specialisti = new ArrayList<>();

        for (Zamestnanec z : zamestnanci.values()) {
            if (z.getSpecializace() instanceof Datovyanalytik) {
                analytici.add(z);
            } else {
                specialisti.add(z);
            }
        }

        analytici.sort(Comparator.comparing(Zamestnanec::getPrijmeni).thenComparing(Zamestnanec::getJmeno));
        specialisti.sort(Comparator.comparing(Zamestnanec::getPrijmeni).thenComparing(Zamestnanec::getJmeno));

        System.out.println("\n--- Analytici ---");
        for (Zamestnanec z : analytici) {
            System.out.println(z.getPrijmeni() + " " + z.getJmeno() + " (ID: " + z.getId() + ")");
        }
        System.out.println("\n--- Specialisté ---");
        for (Zamestnanec z : specialisti) {
            System.out.println(z.getPrijmeni() + " " + z.getJmeno() + " (ID: " + z.getId() + ")");
        }
    }

    // ---------- Počet zaměstnanců ve skupinách ----------
    public void vypisPocetVeSkupinach() {
        int pocetAnalytiku = 0;
        int pocetSpecialistu = 0;
        for (Zamestnanec z : zamestnanci.values()) {
            if (z.getSpecializace() instanceof Datovyanalytik) {
                pocetAnalytiku++;
            } else {
                pocetSpecialistu++;
            }
        }
        System.out.println("Počet analytiků: " + pocetAnalytiku);
        System.out.println("Počet specialistů: " + pocetSpecialistu);
    }

    // ---------- Statistiky ----------
    public void vypisStatistiky() {
        // Zaměstnanec s nejvíce vazbami
        Zamestnanec nejviceVazeb = null;
        int maxVazeb = -1;
        for (Zamestnanec z : zamestnanci.values()) {
            int pocet = z.getPocetSpolupracovniku();
            if (pocet > maxVazeb) {
                maxVazeb = pocet;
                nejviceVazeb = z;
            }
        }
        if (nejviceVazeb != null) {
            System.out.println("Zaměstnanec s nejvíce vazbami: " + nejviceVazeb.getPrijmeni() + " " + nejviceVazeb.getJmeno() +
                    " (ID: " + nejviceVazeb.getId() + ", počet vazeb: " + maxVazeb + ")");
        } else {
            System.out.println("Žádní zaměstnanci.");
        }

        // Převažující kvalita spolupráce (napříč všemi vazbami)
        int spatna = 0, prumerna = 0, dobra = 0;
        for (Zamestnanec z : zamestnanci.values()) {
            for (Spoluprace u : z.getSpolupracovnici().values()) {
                switch (u) {
                    case SPATNA: spatna++; break;
                    case PRUMERNA: prumerna++; break;
                    case DOBRA: dobra++; break;
                }
            }
        }
        int total = spatna + prumerna + dobra;
        if (total == 0) {
            System.out.println("Žádné spolupráce nejsou evidovány.");
        } else {
            System.out.print("Převažující kvalita spolupráce: ");
            if (spatna >= prumerna && spatna >= dobra) System.out.println("špatná");
            else if (prumerna >= spatna && prumerna >= dobra) System.out.println("průměrná");
            else System.out.println("dobrá");
            System.out.printf("  (špatná: %d, průměrná: %d, dobrá: %d)%n", spatna, prumerna, dobra);
        }
    }

    // ---------- Spuštění dovednosti ----------
    public void spustDovednost(int id) {
        Zamestnanec z = zamestnanci.get(id);
        if (z == null) {
            System.out.println("Zaměstnanec s ID " + id + " neexistuje.");
            return;
        }
        z.getSpecializace().vykonatDovednost(z, this);
    }

    public void ulozDoSouboru() {
        System.out.println("Ulozeno Do Souboru.");
    }

    public void nactiZeSouboru() {
        System.out.println("Načítání ze souboru zatím není implementováno.");
    }

    public void ulozDoSql() {
        System.out.println("Ukládání do SQL zatím není implementováno.");
    }

    public void nactiZeSql() {
        System.out.println("Načítání z SQL zatím není implementováno.");
    }

    // Getter a setter pro serializaci (pro pozdější použití)
    public Map<Integer, Zamestnanec> getZamestnanciMap() {
        return zamestnanci;
    }

    public void setZamestnanciMap(Map<Integer, Zamestnanec> zamestnanci) {
        this.zamestnanci = zamestnanci;
        aktualizujNextId();
    }

    private void aktualizujNextId() {
        int max = 0;
        for (int id : zamestnanci.keySet()) {
            if (id > max) max = id;
        }
        nextId = max + 1;
    }

    public void ulozDoSouboru(String soubor) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(soubor))) {
            oos.writeObject(zamestnanci);
            System.out.println("Data byla uložena do souboru: " + soubor);
        } catch (IOException e) {
            System.out.println("Chyba při ukládání do souboru: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void nactiZeSouboru(String soubor) {
        File file = new File(soubor);
        if (!file.exists()) {
            System.out.println("Soubor neexistuje: " + soubor);
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                zamestnanci = (Map<Integer, Zamestnanec>) obj;
                aktualizujNextId();
                System.out.println("Data byla načtena ze souboru: " + soubor);
            } else {
                System.out.println("Soubor neobsahuje platná data.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Chyba při načítání ze souboru: " + e.getMessage());
        }
    }

    // Upravená metoda nactiData() – bude volat načtení ze souboru při startu
    public void nactiData() {
        nactiZeSouboru("data.ser");  // pokusíme se načíst výchozí soubor
    }

}