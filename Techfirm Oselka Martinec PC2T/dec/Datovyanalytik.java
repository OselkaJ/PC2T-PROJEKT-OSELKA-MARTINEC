package dec;

import database.Database;
import java.util.*;

public class Datovyanalytik extends Specializace{
    private static final long serialVersionUID = 1L;
    
    @Override
    public void vykonatDovednost(Zamestnanec zamestnanec, Database db) {
        System.out.println("Dovednost Datového analytika - hledání nejvíce společných spolupracovníků.");
        Map<Integer, Zamestnanec> vsichni = db.getZamestnanciMap(); 
        Set<Integer> spolupracovniciZam = zamestnanec.getSpolupracovnici().keySet();
        if (spolupracovniciZam.isEmpty()) {
            System.out.println("Tento zaměstnanec nemá žádné spolupracovníky.");
            return;
        }

        Map<Integer, Integer> spolecniCount = new HashMap<>();
        for (int idKolegy : spolupracovniciZam) {
            Zamestnanec kolega = vsichni.get(idKolegy);
            if (kolega == null) continue;
            
            Set<Integer> kolegoviSpolupracovnici = kolega.getSpolupracovnici().keySet();
            int pocetSpolecnych = 0;
            
            for (int id : kolegoviSpolupracovnici) {
                if (id != zamestnanec.getId() && spolupracovniciZam.contains(id)) {
                    pocetSpolecnych++;
                }
            }
            spolecniCount.put(idKolegy, pocetSpolecnych);
        }

        int max = -1;
        List<Integer> nejlepsiKolegove = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : spolecniCount.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                nejlepsiKolegove.clear();
                nejlepsiKolegove.add(entry.getKey());
            } else if (entry.getValue() == max && max != -1) {
                nejlepsiKolegove.add(entry.getKey());
            }
        }

        if (max <= 0) {
            System.out.println("Žádné společné vazby nenalezeny.");
        } else {
            System.out.println("Nejvíce společných spolupracovníků (počet: " + max + "):");
            for (int id : nejlepsiKolegove) {
                Zamestnanec kolega = vsichni.get(id);
                if (kolega != null) {
                    System.out.println("  " + kolega.getPrijmeni() + " " + kolega.getJmeno() + " (ID: " + id + ")");
                }
            }
        }
    }
}