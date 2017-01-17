package org.kiwix.kiwixmobile.utils;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.kiwix.kiwixmobile.TableDrawerAdapter;

import static org.kiwix.kiwixmobile.TableDrawerAdapter.*;

public class DocumentParser {

  private String title;
  private SectionsListener listener;
  private List<TableDrawerAdapter.DocumentSection> sections;

  public DocumentParser(SectionsListener listener) {
    this.listener = listener;
  }

  public void initInterface(WebView webView) {
    webView.addJavascriptInterface(new ParserCallback(), "DocumentParser");
  }

  class ParserCallback {

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void parse(final String sectionTitle, final String element, final String id) {
      if (element.equals("H1")) {
        title = sectionTitle;
        return;
      }
      DocumentSection section = new DocumentSection();
      section.title = sectionTitle;
      section.id = id;
      int level;
      try {
        String character = element.substring(element.length() - 1);
        level = Integer.parseInt(character);
      } catch (NumberFormatException e) {
        level = 0;
      }
      section.level = level;
      sections.add(section);
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void start() {
      sections = new ArrayList<>();
      new Handler(Looper.getMainLooper()).post(() -> listener.clearSections());
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void stop() {
      new Handler(Looper.getMainLooper()).post(() -> listener.sectionsLoaded(title, sections));
    }


      @JavascriptInterface
      @SuppressWarnings("unused")
      public void postFeedbackFormData(final String data) {
          new Handler(Looper.getMainLooper()).post(() -> listener.postFeedback(data));
      }

  }

  public interface SectionsListener {
    void sectionsLoaded(String title, List<DocumentSection> sections);

    void clearSections();

    void postFeedback(String data);
  }
}


