package ayds.dictionary.foxtrot.fulllogic;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainWindow {
  private JTextField textField1;
  private JButton goButton;
  private JPanel contentPane;
  private JTextPane textPane1;

  public MainWindow() {

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://translate.yandex.net/api/v1.5/tr/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build();

    YandexAPI wikiAPI = retrofit.create(YandexAPI.class);

    textPane1.setContentType("text/html");

    goButton.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent e) {

        new Thread(new Runnable() {
          @Override public void run() {

            String text = DataBase.getMeaning(textField1.getText());


            if (text != null) { // exists in db

              text = "[*]" + text;
            } else { // get from service
              Response<String> callResponse;
              try {
                callResponse = wikiAPI.getTerm(textField1.getText()).execute();

                System.out.println("XML " + callResponse.body());


                if (callResponse.body() == null) {
                  text = "No Results";
                } else {

                  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                  DocumentBuilder db = dbf.newDocumentBuilder();
                  Document doc = db.parse(new InputSource(new java.io.StringReader(callResponse.body())));

                  NodeList nodes = doc.getDocumentElement().getElementsByTagName("text");

                  String extract = nodes.item(0).getTextContent();
                  text = extract.replace("\\n", "<br>");
                  text = textToHtml(text, textField1.getText());

                  // save to DB  <o/

                  DataBase.saveTerm(textField1.getText(), text);
                }

              } catch (IOException e1) {
                e1.printStackTrace();
              } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
              } catch (SAXException e1) {
                e1.printStackTrace();
              }
            }

            textPane1.setText(text);
          }
        }).start();

      }
    });

  }

  public static void main(String[] args) {

    JFrame frame = new JFrame("Online Dictionary");
    frame.setContentPane(new MainWindow().contentPane);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);

    DataBase.createNewDatabase();
    DataBase.saveTerm("test", "sarasa");


    System.out.println(DataBase.getMeaning("test"));
    System.out.println(DataBase.getMeaning("nada"));
  }

  public static String textToHtml(String text, String term) {

    StringBuilder builder = new StringBuilder();

    builder.append("<font face=\"arial\">");

    String textWithBold = text
        .replace("'", "`")
        .replace(term, "<b>" + term +"</b>");

    builder.append(textWithBold);

    builder.append("</font>");

    return builder.toString();
  }

}
