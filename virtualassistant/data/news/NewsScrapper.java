import java.io.IOException;  
import org.jsoup.Jsoup;  
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;


public class NewsScrapper{

	private ArrayList<newsObj> articlesObjs = new ArrayList<newsObj>(); // arraylist of news articles objects
	private DateFormat toFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");

	private class newsObj{  // possibly in another file ??
		private String DateTime;
		private String title;
		private String impact;
		private String url;
		private String source;

		public newsObj(String time, String title, String impact, String url,String source){ // for Rns news
			this.DateTime = time;
			this.title = title;
			this.impact = impact;
			this.url = url;
			this.source = source;
		}

		public newsObj(String time, String title, String url,String source){ // for other types of news articles
			this.DateTime = time;
			this.title = title;
			this.url = url;
			this.source = source;
		}

		public String getDateTime(){
			return this.DateTime;
		}
		public String getTitle(){
			return this.title;
		}
		public String getImpact(){
			return this.impact;
		}
		public String getUrl(){
			return this.url;
		}
		public String getSource(){
			return this.source;
		}
	}

	public ArrayList<newsObj> getRnsNews(String company) throws IOException, ParseException {
		for(int i=1;i<=6;i++){ // loop to go through each page and get the isin number of the company wanted.
			final Document doc = Jsoup.connect("http://www.londonstockexchange.com/exchange/prices-and-markets/stocks/indices/constituents-indices.html?index=UKX&industrySector=&page=" + i).get();
				for(Element li: doc.select(".table_dati tr")){ // going through each list item and adding the article into the arraylist
					if(li.select(".name:eq(0)").text().equals(company)){
						String kk = li.select(".name:eq(1)").first().select(".name a").attr("href");
						rnsNewsArticleGetter(isinNumberGetter(kk)); // We've got the isin number, now get all the news from it
						i=6; // break out of loop once the company has been found.
					}
				}
			}
			return articlesObjs;
		}

		private void rnsNewsArticleGetter(String webpage) throws IOException, ParseException {
			DateFormat fromFormat = new SimpleDateFormat("HH:mm dd-MMM-yyyy"); // Date needs to be formatted.
			fromFormat.setLenient(false);
			toFormat.setLenient(false);

			final Document doc = Jsoup.connect(webpage).get(); // all the articles from lse new analysis is gotten. 
			for(Element row: doc.select(".table_datinews tr")){ // loop of articles in the table row is done
				final String title = row.select(".text-left a").text();
				final String time = toFormat.format(fromFormat.parse(row.select(".datetime.nowrap").text())); // with the date formatting
				final String source = row.select(".nowrap.text-left").get(1).text();
				final String impact = row.select(".nowrap.text-left").get(2).text();
				final String relHref = row.select(".text-left a").first().attr("href");
				final String absurl=javascriptLinkToUrl(relHref); // html link is a javascript script so need to use regex to get the link out.
				articlesObjs.add(newsArrayAdder(time,title,impact,absurl,source)); // news article object added to arraylist
				System.out.println(time + "     " + title + "   " + impact + "  " + absurl + "   " + source);  // FOR TESTING ONLY WILL BE REMOVED

			}	
		}

	private String isinNumberGetter(String webPageLink){	 // used to get the isin Number of the company and return the webpage for that company	
		final Pattern pattern = Pattern.compile("/summary/company-summary/(.*?).html"); // regex used to get the part of link needed (the isin number).
		final Matcher matcher = pattern.matcher(webPageLink);
		String absoluteUrl = "";
		while(matcher.find()){
			absoluteUrl = "http://www.londonstockexchange.com/exchange/prices-and-markets/stocks/exchange-insight/news-analysis.html?fourWayKey=" + matcher.group(1);
		}
		return absoluteUrl; // returns the url needed
	}

	private String javascriptLinkToUrl(String jsUrl){	 // used to convert a javascript link into an actual usable url
		final Pattern pattern = Pattern.compile("'/exchange/news/(.*?).html"); // regex used to get the part of link needed.
		final Matcher matcher = pattern.matcher(jsUrl);
		String absurl = "";
		while(matcher.find()){
			absurl = "http://www.londonstockexchange.com/exchange/news/" + matcher.group(1) +".html";
		}
		return absurl;
	}

	public ArrayList<newsObj> getAllianceNews(String company) throws IOException, ParseException { // COMPANY OFFICIAL NAME E.G BARCLAYS = BARC MUST BE IN CAPITALS LETTERS
		Date date = new Date();
		String todaysDate= new SimpleDateFormat("dd MMM yyyy").format(date);

		final Document doc = Jsoup.connect("http://www.londonstockexchange.com/exchange/news/alliance-news/company-news.html?tidm=" + company).get();

			for(Element li: doc.select("ul li")){ // going through each list item and adding the article into the arraylist
				final String title = (li.select("a").text());
				final String url = "http://www.londonstockexchange.com" + li.select("a").first().attr("href");
				final String dateGrabbed = li.select(".hour").text();
				final String datetime = dateGrabbed.length() == 5 ? todaysDate + " " + dateGrabbed : dateGrabbed; // at times the article may be posted today, which will only contain the time need to then add the date to it
				articlesObjs.add(newsArrayAdder(datetime,title,url,"London stock exchange - Alliance News"));  //articles are added to the array list as objects.
				System.out.println(datetime + "               " + title + "                " + url);  // FOR TESTING ONLY WILL BE REMOVED

			}
			return articlesObjs;
		}

		public ArrayList<newsObj> getYahooNews(String comapny) throws IOException, ParseException {			
			DateFormat fromFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z"); // Date needs to be formatted.
			fromFormat.setLenient(false);
			toFormat.setLenient(false);

			String companyName = "";
		if(comapny.charAt(comapny.length() -1) == '.'){ // This is needed as the company name must end with .l e.g. barc = barc.l
			companyName = comapny + "l";		
		}else{
			companyName = comapny + ".l";	
		}

			Document doc = Jsoup.parse(new URL("https://feeds.finance.yahoo.com/rss/2.0/headline?s="+companyName+"&region=US&lang=en-US").openStream(), "UTF-8", "", Parser.xmlParser()); // xml retrieved to be parsed
			for(Element li: doc.select("item")){
				String date = toFormat.format(fromFormat.parse(li.select("pubDate").text()));
				final String title = li.select("title").text();
				final String url = li.select("link").text();
				articlesObjs.add(newsArrayAdder(date,title,url,"Yahoo Fianace News"));
				System.out.println(date + "               " + title + "                " + url);  // FOR TESTING ONLY WILL BE REMOVED
			}
			return articlesObjs;	
		}

		public ArrayList<newsObj> sectorNews(String sector) throws IOException, ParseException {
			/* Sectors allowed here ::::    mining  oil-gas  utilities  banks  insurance  property  financial-services  health-care  pharmaceuticals  aerospace-defence
			   automobiles  basic-resources  chemicals  construction  industrial-goods  support-services  accounting-consulting-services  legal-services  recruitment-services
			   food-beverage  luxury-goods  personal-goods (<---contains household goods too)  retail  tobacco  travel-leisure  airlines  shipping  rail

			   General sectors allowed :::: energy  financials  health  industrials  media  professional-services  retail-consumer  telecoms  transport  technology
			*/			
			DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // Date needs to be formatted.
			fromFormat.setLenient(false);
			toFormat.setLenient(false);

			final Document doc = Jsoup.connect("https://www.ft.com/companies/" + sector).get();

			for(Element li: doc.select("#stream li")){ // going through each list item and adding the article into the arraylist
				if(li.select(".stream-card__date .o-date.o-teaser__timestamp").text().length() >0){  // needed to prevent empty spaces from being added due to ads on the website
					String headLine = li.select(".js-teaser-heading-link").text();
					String dateTime = toFormat.format(fromFormat.parse(li.select(".o-teaser__timestamp").first().attr("datetime"))); // the datetime of the article.
					final String relHref = "https://www.ft.com" + li.select(".js-teaser-heading-link").first().attr("href"); // url of the article
					articlesObjs.add(newsArrayAdder(dateTime,headLine,relHref,"The Financial Times"));
					System.out.println(headLine + "  " + dateTime + "   " + relHref);

				}
			}
			return articlesObjs;
		}

	private newsObj newsArrayAdder(String time, String title, String url,String source){ // used to make objects of news articles. 
		newsObj c = new newsObj(time,title,url,source);
		return c;
	}

	private newsObj newsArrayAdder(String time, String title, String impact, String url,String source){ // creates objects for each rns news article
		newsObj c = new newsObj(time,title,impact,url,source);
		return c;
	}
}
