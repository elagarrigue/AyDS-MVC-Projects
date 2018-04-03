package ayds.dictionary.charlie.fulllogic;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

// https://words.bighugelabs.com/api.php
public interface WordsBighugelabsAPI {

  @GET("ba10a6bc725bde1d3b3dfc78f9b92ab1/{word}/json")
  Call<String> getTerm(@Path("word") String word);

}