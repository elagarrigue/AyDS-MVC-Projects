package ayds.dictionary.bravo.fulllogic;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikipediaAPI {

  @GET("api.php?format=xml&action=query&prop=extracts&exlimit=1&explaintext&exintro")
  Call<String> getTerm(@Query("titles") String term);

}
