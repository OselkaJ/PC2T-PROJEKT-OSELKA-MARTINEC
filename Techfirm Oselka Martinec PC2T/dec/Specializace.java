package dec;

import java.io.Serializable;
import database.Database;

public abstract class Specializace implements Serializable {
    private static final long serialVersionUID = 1L;
    public abstract void vykonatDovednost(Zamestnanec zamestnanec, Database db);
}