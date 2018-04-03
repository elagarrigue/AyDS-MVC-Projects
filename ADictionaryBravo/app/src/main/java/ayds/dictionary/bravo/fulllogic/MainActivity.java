package ayds.dictionary.bravo.fulllogic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ayds.dictionary.bravo.R;
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
        .baseUrl("https://en.wikipedia.org/w/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build();

    final WikipediaAPI wikiAPI = retrofit.create(WikipediaAPI.class);

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

                Log.e("**", "XML: " + callResponse.body());

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(new java.io.StringReader(callResponse.body())));


                NodeList nodes = doc.getDocumentElement().getElementsByTagName("extract");

                Node extract = nodes.item(0);

                Log.e("**", "XML " + extract);

                if (extract == null) {
                  text = "No Results";
                } else {
                  text = extract.getTextContent().replace("\\n", "\n");
                  text = textToHtml(text, textField1.getText().toString());

                  // save to DB  <o/
                  DataBase.saveTerm(textField1.getText().toString(), text);
                }

              } catch (IOException e1) {
                e1.printStackTrace();
              } catch (ParserConfigurationException e) {
                e.printStackTrace();
              } catch (SAXException e) {
                e.printStackTrace();
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
