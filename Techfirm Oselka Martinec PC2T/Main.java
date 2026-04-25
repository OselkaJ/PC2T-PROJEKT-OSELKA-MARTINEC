import database.Database;
import dec.Spoluprace;
import dec.Zamestnanec;
import java.util.Scanner;


public class Main {
    private static final String DATA_FILE = "techfirmdata.txt";
    private static Database db = new Database();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        db.nactiZeSouboru(DATA_FILE);

        boolean konec = false;
        while (!konec) {
            vypisMenu();
            String volba = sc.nextLine().trim();
            switch (volba) {
                case "a": pridejZamestnance(); break;
                case "b": pridejSpolupraci(); break;
                case "c": odeberZamestnance(); break;
                case "d": vyhledejPodleId(); break;
                case "e": spustDovednost(); break;
                case "f": db.vypisAbecednePodleSkupin(); break;
                case "g": db.vypisStatistiky(); break;
                case "h": db.vypisPocetVeSkupinach(); break;
                case "i": db.ulozDoSouboru(DATA_FILE); break;      
                case "j": db.nactiZeSouboru(DATA_FILE); break;     
                case "k": db.ulozDoSql(); break;                   
                case "l": db.nactiZeSql(); break;
                //case "m": vypisVsechnyZamestnance(); break;
                case "x":
                    konec = true;
                    db.ulozDoSouboru(DATA_FILE);
                    db.ulozDoSql();
                    System.out.println("Program ukončen.");
                    break;
                default: System.out.println("Neplatná volba.");
            }
        }
        sc.close();
    }

    private static void vypisMenu() {
    String R = "\u001B[0m"; 
    String B = "\u001B[1m";  

    System.out.println("\n" + B + "┌──────────────────────────────────────────────────────────┐" + R);
    System.out.println(B + "│                T E C H F I R M   2 0 2 6                 │" + R);
    System.out.println(B + "│          By Oselka Jakub & Robert Martinec               │" + R);
    System.out.println(B + "├──────────────────────────────────────────────────────────┤" + R);
    System.out.println(B + "│  SPRÁVA ZAMĚSTNANCŮ                                      │" + R);
    System.out.println(B + "│  a) Přidat zaměstnance        b) Přidat spolupráci       │" + R);
    System.out.println(B + "│  c) Odebrat zaměstnance       d) Vyhledat podle ID       │" + R);
    System.out.println(B + "│  e) Spustit dovednost                                    │" + R);
    System.out.println(B + "├──────────────────────────────────────────────────────────┤" + R);
    System.out.println(B + "│  VÝPISY A STATISTIKY                                     │" + R);
    System.out.println(B + "│  f) Abecední seznam           g) Statistiky vazeb        │" + R);
    System.out.println(B + "│  h) Počty ve skupinách                                   │" + R);
    System.out.println(B + "├──────────────────────────────────────────────────────────┤" + R);
    System.out.println(B + "│  DATA A DATABÁZE                                         │" + R);
    System.out.println(B + "│  i) Uložit do SOUBORU         j) Načíst ze SOUBORU       │" + R);
    System.out.println(B + "│  k) Uložit do SQL             l) Načíst z SQL            │" + R);
    System.out.println(B + "├──────────────────────────────────────────────────────────┤" + R);
    System.out.println(B + "│  x) UKONČIT PROGRAM                                      │" + R);
    System.out.println(B + "└──────────────────────────────────────────────────────────┘" + R);
    
    System.out.print(B + " Zadejte volbu > " + R);
}
   private static void pridejZamestnance() {
        System.out.print("Jméno: ");
        String jmeno = sc.nextLine().trim();
        System.out.print("Příjmení: ");
        String prijmeni = sc.nextLine().trim();
        System.out.print("Rok narození: ");
        int rok = Integer.parseInt(sc.nextLine().trim());

        System.out.println("Vyberte skupinu:");
        System.out.println("  1 / a - analytik");
        System.out.println("  2 / b - specialista");
        System.out.print("Zadejte volbu: ");
        String volba = sc.nextLine().trim().toLowerCase();

        String skupina = null;
        switch (volba) {
            case "1":
            case "a":
                skupina = "analytik";
                break;
            case "2":
            case "b":
                skupina = "specialista";
                break;
            default:
                System.out.println("Neplatná volba.");
                return;
        }    

        try {
            db.pridejZamestnance(jmeno, prijmeni, rok, skupina);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void pridejSpolupraci() {
        System.out.print("ID zaměstnance: ");
        int idZam = Integer.parseInt(sc.nextLine().trim());
        System.out.print("ID kolegy: ");
        int idKolegy = Integer.parseInt(sc.nextLine().trim());
        
        System.out.println("Vyberte úroveň spolupráce:");
        System.out.println("  1 / a - špatná");
        System.out.println("  2 / b - průměrná");
        System.out.println("  3 / c - dobrá");
        System.out.print("Zadejte volbu: ");
        String volba = sc.nextLine().trim().toLowerCase();
        
        Spoluprace uroven = null;
        switch (volba) {
            case "1":
            case "a":
                uroven = Spoluprace.SPATNA;
                break;
            case "2":
            case "b":
                uroven = Spoluprace.PRUMERNA;
                break;
            case "3":
            case "c":
                uroven = Spoluprace.DOBRA;
                break;
            default:
                System.out.println("Neplatná volba.");
                return;
        }
        
        db.pridejSpolupraci(idZam, idKolegy, uroven);
    }

    private static void odeberZamestnance() {
        System.out.print("ID zaměstnance: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        db.odeberZamestnance(id);
    }

    private static void vyhledejPodleId() {
        System.out.print("ID zaměstnance: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Zamestnanec z = db.najdiPodleId(id);
        if (z == null) {
            System.out.println("Zaměstnanec nenalezen.");
        } else {
            System.out.println(z);
            System.out.println("Počet spolupracovníků: " + z.getPocetSpolupracovniku());
            System.out.println("Seznam spolupracovníků:");
            for (var entry : z.getSpolupracovnici().entrySet()) {
                Zamestnanec kolega = db.najdiPodleId(entry.getKey());
                String jmenoKolegy = (kolega != null) ? kolega.getPrijmeni() + " " + kolega.getJmeno() : "ID " + entry.getKey();
                System.out.println("  " + jmenoKolegy + " – " + entry.getValue());
            }
        }
    }

    private static void spustDovednost() {
        System.out.print("ID zaměstnance: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        db.spustDovednost(id);
    }

   /*  private static void vypisVsechnyZamestnance() {
    Collection<Zamestnanec> vsichni = db.getAllZamestnanci();
    if (vsichni.isEmpty()) {
        System.out.println("Žádní zaměstnanci.");
    } else {
        System.out.println("Seznam všech zaměstnanců:");
        for (Zamestnanec z : vsichni) {
            System.out.println(z);
        }
    } */
}
