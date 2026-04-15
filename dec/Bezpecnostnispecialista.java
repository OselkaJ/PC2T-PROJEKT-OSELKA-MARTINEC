package dec;

import database.Database;
import java.io.Serializable;
//import java.util.*;

public class Bezpecnostnispecialista implements Specializace, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public void vykonatDovednost(Zamestnanec zamestnanec, Database db) {
        System.out.println("Dovednost Bezpečnostního specialisty – výpočet rizikového skóre.");

        int pocetSpolupracovniku = zamestnanec.getPocetSpolupracovniku();
        if (pocetSpolupracovniku == 0) {
            System.out.println("Zaměstnanec nemá žádné spolupracovníky, rizikové skóre = 0.");
            return;
        }

        // Průměrná kvalita spolupráce: převedeme enum na číselnou hodnotu (1=špatná, 2=průměrná, 3=dobrá)
        double sumKvalita = 0;
        for (Spoluprace u : zamestnanec.getSpolupracovnici().values()) {
            switch (u) {
                case SPATNA: sumKvalita += 1; break;
                case PRUMERNA: sumKvalita += 2; break;
                case DOBRA: sumKvalita += 3; break;
            }
        }
        double prumernaKvalita = sumKvalita / pocetSpolupracovniku;

        // Vlastní algoritmus: riziko = počet spolupracovníků * (4 - průměrná kvalita)
        // Čím vyšší riziko, tím horší.
        double riziko = pocetSpolupracovniku * (4 - prumernaKvalita);
        System.out.printf("Rizikové skóre: %.2f (počet spolupracovníků: %d, průměrná kvalita: %.2f)%n",
                riziko, pocetSpolupracovniku, prumernaKvalita);
    }
}