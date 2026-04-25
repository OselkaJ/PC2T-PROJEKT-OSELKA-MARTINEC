package database;

import dec.*;
import java.io.*;
import java.util.*;
import java.sql.*;

public class Database {
    private Map<Integer, Zamestnanec> zamestnanci;
    private int nextId;
    private static final String URL = "jdbc:mysql://localhost:3306/techfirm?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public Database() {
        zamestnanci = new HashMap<>();
        nextId = 1;
    }

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

    public void odeberZamestnance(int id) {
    if (zamestnanci.containsKey(id)) {
        zamestnanci.remove(id);
        for (Zamestnanec z : zamestnanci.values()) {
            z.odstranSpolupraci(id);
        }
        System.out.println("Zaměstnanec s ID " + id + " byl odebrán.");
    } else {
        System.out.println("Zaměstnanec s ID " + id + " neexistuje.");
    }
}

    public Zamestnanec najdiPodleId(int id) {
        return zamestnanci.get(id);
    }

    public void pridejSpolupraci(int idZam, int idKolegy, Spoluprace uroven) {
    if (idZam == idKolegy) {
        System.out.println("Chyba: Zaměstnanec nemůže spolupracovat sám se sebou.");
        return;
    }

    Zamestnanec zam = zamestnanci.get(idZam);
    Zamestnanec kolega = zamestnanci.get(idKolegy);

    if (zam != null && kolega != null) {
        zam.pridejSpolupraci(idKolegy, uroven);
        kolega.pridejSpolupraci(idZam, uroven); 
        
        System.out.println("Spolupráce mezi " + zam.getPrijmeni() + " a " + kolega.getPrijmeni() + " byla uložena.");
    } else {
        System.out.println("Chyba: Jeden nebo oba zaměstnanci neexistují (ID: " + idZam + ", " + idKolegy + ").");
    }

}

    public Collection<Zamestnanec> getAllZamestnanci() {
        return zamestnanci.values();
    }

    public List<Zamestnanec> getZamestnanciAbecedne() {
        List<Zamestnanec> seznam = new ArrayList<>(zamestnanci.values());
        seznam.sort(Comparator.comparing(Zamestnanec::getPrijmeni).thenComparing(Zamestnanec::getJmeno));
        return seznam;
    }

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

    public void vypisStatistiky() {
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

    public void spustDovednost(int id) {
        Zamestnanec z = zamestnanci.get(id);
        if (z == null) {
            System.out.println("Zaměstnanec s ID " + id + " neexistuje.");
            return;
        }
        z.getSpecializace().vykonatDovednost(z, this);
    }

   public void ulozDoSql() {
    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
        // Vytvoření tabulek
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS zamestnanci (" +
                "id INT PRIMARY KEY, jmeno VARCHAR(50), prijmeni VARCHAR(50), " +
                "rok INT, skupina VARCHAR(50))");
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS spoluprace (" +
                "id_zam INT, id_kolega INT, uroven VARCHAR(20))");

        conn.createStatement().execute("DELETE FROM spoluprace");
        conn.createStatement().execute("DELETE FROM zamestnanci");

        PreparedStatement psZam = conn.prepareStatement("INSERT INTO zamestnanci VALUES (?, ?, ?, ?, ?)");
        PreparedStatement psSpolu = conn.prepareStatement("INSERT INTO spoluprace VALUES (?, ?, ?)");

        for (Zamestnanec z : zamestnanci.values()) {
            psZam.setInt(1, z.getId());
            psZam.setString(2, z.getJmeno());
            psZam.setString(3, z.getPrijmeni());
            psZam.setInt(4, z.getRokNarozeni());
            psZam.setString(5, (z.getSpecializace() instanceof Datovyanalytik ? "analytik" : "specialista"));
            psZam.executeUpdate();

            for (Map.Entry<Integer, Spoluprace> entry : z.getSpolupracovnici().entrySet()) {
                psSpolu.setInt(1, z.getId());
                psSpolu.setInt(2, entry.getKey());
                psSpolu.setString(3, entry.getValue().name());
                psSpolu.executeUpdate();
            }
        }
        System.out.println("Data byla úspěšně exportována do MySQL databáze.");
    } catch (SQLException e) {
        System.out.println("Chyba při ukládání do SQL: " + e.getMessage());
    }
}

   public void nactiZeSql() {
    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
        Statement st = conn.createStatement();
        
        ResultSet rsZam = st.executeQuery("SELECT * FROM zamestnanci");
        zamestnanci.clear();
        while (rsZam.next()) {
            int id = rsZam.getInt("id");
            String jmeno = rsZam.getString("jmeno");
            String prijmeni = rsZam.getString("prijmeni");
            int rok = rsZam.getInt("rok");
            String skupina = rsZam.getString("skupina");

            Specializace spec = skupina.equals("analytik") ? new Datovyanalytik() : new Bezpecnostnispecialista();
            Zamestnanec z = new Zamestnanec(id, jmeno, prijmeni, rok, spec);
            zamestnanci.put(id, z);
        }

        ResultSet rsSpolu = st.executeQuery("SELECT * FROM spoluprace");
        while (rsSpolu.next()) {
            int id1 = rsSpolu.getInt("id_zam");
            int id2 = rsSpolu.getInt("id_kolega");
            Spoluprace uroven = Spoluprace.valueOf(rsSpolu.getString("uroven"));
            
            if (zamestnanci.containsKey(id1)) {
                zamestnanci.get(id1).pridejSpolupraci(id2, uroven);
            }
        }
        aktualizujNextId();
        System.out.println("Data byla úspěšně načtena z MySQL databáze.");
    } catch (SQLException e) {
        System.out.println("SQL databáze není dostupná nebo je prázdná.");
    }
}

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
    try (PrintWriter writer = new PrintWriter(new FileWriter(soubor))) {
        for (Zamestnanec z : zamestnanci.values()) {
            String skupina = (z.getSpecializace() instanceof Datovyanalytik ? "analytik" : "specialista");
            writer.println(z.getId() + ";" + z.getJmeno() + ";" + z.getPrijmeni() + ";" + z.getRokNarozeni() + ";" + skupina);
        }
        System.out.println("Data byla uložena do textového souboru: " + soubor);
    } catch (IOException e) {
        System.out.println("Chyba při ukládání do souboru: " + e.getMessage());
    }
}
    public void nactiZeSouboru(String soubor) {
    File file = new File(soubor);
    if (!file.exists()) return;

    try (Scanner scanner = new Scanner(file)) {
        zamestnanci.clear();
        while (scanner.hasNextLine()) {
            String radek = scanner.nextLine();
            if (radek.isEmpty()) continue;
            
            String[] casti = radek.split(";");
            int id = Integer.parseInt(casti[0]);
            String jmeno = casti[1];
            String prijmeni = casti[2];
            int rok = Integer.parseInt(casti[3]);
            String skupina = casti[4];

            Specializace spec = skupina.equals("analytik") ? new Datovyanalytik() : new Bezpecnostnispecialista();
            Zamestnanec z = new Zamestnanec(id, jmeno, prijmeni, rok, spec);
            zamestnanci.put(id, z);
        }
        aktualizujNextId();
        System.out.println("Data byla načtena z textového souboru.");
    } catch (Exception e) {
        System.out.println("Chyba při čtení textového souboru: " + e.getMessage());
    }
}

    public void nactiData() {
        nactiZeSouboru("techfirmdata.txt");
    }
}