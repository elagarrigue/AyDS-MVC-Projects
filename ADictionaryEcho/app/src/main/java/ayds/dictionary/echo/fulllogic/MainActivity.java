package ayds.dictionary.echo.fulllogic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import ayds.dictionary.echo.R;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

  private EditText textField1;
  private Button goButton;
  private TextView textPane1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    init();

    setContentView(R.layout.activity_main);

    textField1 = findViewById(R.id.textField1);
    goButton = findViewById(R.id.goButton);
    textPane1 = findViewById(R.id.textPane1);

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://translate.yandex.net/api/v1.5/tr.json/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build();

    final YandexAPI wikiAPI = retrofit.create(YandexAPI.class);

    goButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        new Thread(new Runnable() {
          public void run() {

            String text = DataBase.getMeaning(textField1.getText().toString());

            if (text != null) { // exists in db

              text = "[*]" + text;
            } else {
              Response<String> callResponse;
              try {
                callResponse = wikiAPI.getTerm(textField1.getText().toString()).execute();

                Log.e("**","JSON " + callResponse.body());


                if (callResponse.body() == null) {
                  text = "No Results";
                } else {


                  Gson gson = new Gson();
                  JsonObject jobj = gson.fromJson(callResponse.body(), JsonObject.class);

                  // nouns
                  String extract = jobj.get("text").getAsString();

                  text = extract.replace("\\n", "<br>");
                  text = textToHtml(text, textField1.getText().toString());

                  // save to DB  <o/
                  DataBase.saveTerm(textField1.getText().toString(), text);
                }

              } catch (IOException e1) {
                e1.printStackTrace();
              }
            }

            final String textToSet = text;
            textPane1.post(new Runnable() {
              public void run() {
                textPane1.setText(Html.fromHtml(textToSet));
              }
            });
          }
        }).start();
      }
    });

  }

  private void init() {

    new Thread(new Runnable() {
      @Override public void run() {
        DataBase.createNewDatabase(getApplicationContext());
        DataBase.saveTerm("test", "sarasa");

        Log.e("**", "" + DataBase.getMeaning("test"));
        Log.e("**", "" + DataBase.getMeaning("nada"));
      }
    }).start();

  }

  public static String textToHtml(String text, String term) {

    StringBuilder builder = new StringBuilder();

    String textWithBold = text.replaceAll("(?i)" + term, "<b>" + term + "</b>");

    builder.append(textWithBold);

    return builder.toString();
  }
}
