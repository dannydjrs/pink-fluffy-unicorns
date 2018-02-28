package virtualassistant.data.stocks;

import java.util.Calendar;
import java.util.Map;

public class Company implements ICompany {

  private String ticker;
  private String name;
  private String sector;

  private double currentPrice;
  private double currentChange;
  private double currentPercentChange;

  private double yearHigh = 0.0;
  private double yearLow = 0.0;
  private double averageClose = 0.0;
  private double averageVolume = 0.0;

  private HistoricalData pastData;

  public Company(String ticker, String name, String sector) {
    this.ticker = ticker;
    this.name = name;
    this.sector = sector;
    try {
      pastData = Scrapper.getPastData(ticker);

      //Do something with the data
    } catch (Exception e) {
      System.out.println(ticker);
      System.out.println(e);
    }

    this.updateCalculatedData();
  }

  public Company(String ticker, String name, String sector, HistoricalData pastData) {

    this.ticker = ticker;
    this.name = name;
    this.sector = sector;

    this.pastData = pastData;

    this.updateCalculatedData();

  }

  public String getName() {
    return name;
  }

  public String getTicker() {
    return ticker;
  }

  public String getSector() {
    return sector;
  }

  //-------------------- Current data -----------------

  public void updatePrice(double price) {
    currentPrice = price;
  }

  public void updatePercentageChange(double change) {
    currentChange = change;
  }

  public void updateChange(double change) {
    currentPercentChange = change;
  }

  public double getCurrentPrice() {
    return currentPrice;
  }
  public double getChange() {
    return currentChange;
  }
  public double getPercentageChange() {
    return currentPercentChange;
  }

  public double getOpen() {
    return getOpenPriceOnDate(Calendar.getInstance());
  }

  public double getHigh() {
    return getHighPriceOnDate(Calendar.getInstance());
  }

  public double getLow() {
    return getLowPriceOnDate(Calendar.getInstance());
  }

  public double getVolume() {
    return getVolumeOnDate(Calendar.getInstance());
  }

  public double getClose() {
    Calendar date = Calendar.getInstance();
    date.add(Calendar.DAY_OF_YEAR, -1);
    return getClosePriceOnDate(date);
  }

  //----------------- Past data ----------------

  private void updateCalculatedData() {

    double high = -1.0;
    double low = 10000000.0;
    double totalPrice = 0.0;
    double totalVolume = 0;

    for (Map.Entry<Calendar, HistoricalData.Record> date : pastData.entrySet()) {

      totalPrice += date.getValue().close;
      totalVolume += date.getValue().volume;

      if (high < date.getValue().high) {
        high = date.getValue().high;
      }

      if (low > date.getValue().low) {
        low = date.getValue().low;
      }

    }

    this.yearHigh = high;
    this.yearLow = low;
    this.averageClose = totalPrice / pastData.size();
    this.averageVolume = totalVolume / pastData.size();

  }

  public HistoricalData getPastData() {
    return pastData;
  }

  public double yearHigh() {
    return yearHigh;
  }

  public double yearLow() {
    return yearLow;
  }

  public double yearAverageClose() {
    return averageClose;
  }

  public double yearAverageVolume() {
    return averageVolume;
  }

  public double getClosePriceOnDate(Calendar day) {

    day = resetTime(day);

    if (pastData.containsKey(day))
      return pastData.get(day).close;

    return -1.0;
  }

  public double getOpenPriceOnDate(Calendar day) {
    day = resetTime(day);

    if (pastData.containsKey(day))
      return pastData.get(day).open;

    return -1.0;
  }

  public double getHighPriceOnDate(Calendar day) {
    day = resetTime(day);

    if (pastData.containsKey(day))
      return pastData.get(day).high;

    return -1.0;
  }

  public double getLowPriceOnDate(Calendar day) {
    day = resetTime(day);

    if (pastData.containsKey(day))
      return pastData.get(day).low;

    return -1.0;
  }

  public int getVolumeOnDate(Calendar day) {
    day = resetTime(day);

    if (pastData.containsKey(day))
      return pastData.get(day).volume;

    return -1;
  }

  public static Calendar resetTime(Calendar day) {
    day.set(Calendar.HOUR_OF_DAY, 12);
    day.set(Calendar.MINUTE, 0);
    day.set(Calendar.SECOND, 0);
    day.set(Calendar.MILLISECOND, 0);

    return day;
  }


}
