package WebCrawler.Crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
 
public class App {
	public static DB dataBase = new DB();
	private static int counter = 1;
	public static void main(String[] args) throws SQLException, IOException {

		dataBase.runSql2("TRUNCATE Record;");
		Scanner input = new Scanner(System.in);
		System.out.println("Input the seed link: ");
		String URL = input.nextLine();
		System.out.println("Input the url keyword you want to crawl: ");
		String keywrod = input.nextLine();
		processPage(URL,keywrod);

	}
 
	public static void processPage(String URL, String keyword) throws SQLException, IOException{
		/*
		 * Check if the URL is already in the database.
		 */
		String sql = "select * from Record where URL = '" + URL + "'";
		ResultSet result = dataBase.runSql(sql);
		if(result.next()){
 
		}
		else{
			/*
			 * Store the URL in the database to avoid repetitive parsing.
			 */
			sql = "INSERT INTO  `Crawler`.`Record` " + "(`URL`) VALUES " + "(?);";
			PreparedStatement statement = dataBase.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, URL);
			statement.execute();
 
			/*
			 * Get information.
			 */
			Document document = Jsoup.connect(URL.replaceAll(" ", "%20")).ignoreContentType(true).ignoreHttpErrors(true).get();
		
			System.out.print(counter + ".");
			System.out.println(URL);
			counter++;
 
			/*
			 * 	Get all links and recursively call 
			 * 	the processPage method.
			 */
			Elements elements = document.select("a[href]");
			for(Element link: elements){
				if(link.attr("href").contains(keyword))
					processPage(link.attr("abs:href"),keyword);
			}
		}
	}
}