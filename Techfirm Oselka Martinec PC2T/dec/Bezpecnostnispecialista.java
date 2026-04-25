package dec;

import database.Database;
import java.io.Serializable;
import java.util.Map;

public class Bezpecnostnispecialista implements Specializace, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public void vykonatDovednost(Zamestnanec zamestnanec, Database db) {
        System.out.println("Dovednost Bezpečnostního specialisty - výpočet rizikového skóre.");
        
        Map<Integer, Spoluprace> spolupracovnici = zamestnanec.getSpolupracovnici();
        if (spolupracovnici.isEmpty()) {
            System.out.println("Tento zaměstnanec nemá žádné spolupracovníky. Rizikové skóre: 0");
            return;
        }

        double sumaRizika = 0;
        for (Spoluprace s : spolupracovnici.values()) {
            switch (s) {
                case SPATNA: sumaRizika += 5.0; break;
                case PRUMERNA: sumaRizika += 2.0; break;
                case DOBRA: sumaRizika += 0.5; break;
            }
        }

        double skore = sumaRizika / spolupracovnici.size();
        System.out.println("Počet spolupracovníků: " + spolupracovnici.size());
        System.out.printf("Vypočítané rizikové skóre: %.2f (vyšší číslo = vyšší riziko)\n", skore);
    }
}