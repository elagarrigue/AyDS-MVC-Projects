package ayds.dictionary.alpha.fulllogic.room;

import android.arch.persistence.room.*;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ConceptDao {

  @Query("SELECT * FROM Concept")
  List<Concept> getAll();

  @Query("SELECT * FROM Concept WHERE term LIKE :term LIMIT 1")
  Concept findByName(String term);

  @Insert(onConflict = REPLACE)
  void insert(Concept concept);
}
