package virtualassistant.ai;

import java.util.List;
import java.util.Set;

import java.io.IOException;
import java.text.ParseException;

//The AI section
public interface ILearningAgent {

  // Take tokenized string and patternized string and compare one against the other
  public void analyzeInput(List<String> tokenized, List<String> patternized);

  //?
  public Favourites<String, Integer> getFavouriteStocks();
  //Lets the AI know the user has bookmarked the stock
  public void bookmarkStock(String ticker);

  public String[] suggestQueries(int count);

  //For notifications (when the data gets updated):

  //Constantly search for stock anomalies
  public String searchForStockEvent();
  //Constantly search for news anomalies
  public String searchForNewsEvent() throws IOException, ParseException;
}
