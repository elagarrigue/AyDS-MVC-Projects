package ayds.dictionary.alpha.fulllogic;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import java.util.List;

import ayds.dictionary.alpha.fulllogic.room.*;

public class DataBase {

  private static ConceptDataBase db;

  public static void createNewDatabase(Context context) {
    db = Room.databaseBuilder(context,
                              ConceptDataBase.class, "dictionary.db").build();
  }

  public static void testDB() {

    List<Concept> concepts = db.termDao().getAll();

    for (Concept concept :
        concepts) {
      Log.e("**", "id =" + concept.getId());
      Log.e("**", "term =" + concept.getTerm());
      Log.e("**", "meaning =" + concept.getMeaning());
      Log.e("**", "source =" + concept.getSource());

    }
  }

  public static void saveTerm(String term, String meaning) {
    Concept concept =  new Concept();
    concept.setTerm(term);
    concept.setMeaning(meaning);
    concept.setSource(1);
    db.termDao().insert(concept);
  }

  public static String getMeaning(String term) {

    Concept concept = db.termDao().findByName(term);

    if (concept != null) {
      return concept.getMeaning();
    }
    return null;
  }

}
