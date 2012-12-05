import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
/*GAME BOARD PARSER
 * SET URL FOR GETTING XML
 * BROWSE XML CONTENT
 * 	->RETURNS ArrayList<Integer> posP
 * 
*/
public class Parser{
	Document doc, doc2;
	SAXBuilder sx;
	Element racine;
	List<Element> nodes;
	XPathExpression<Element> xpaE;
	XPathFactory xpaF;
	Board plat;
	URL theFile;
	ArrayList<Integer> pos;
	
	/**
	 * @param URL
	 * Create a new parsed URL
	 */
	public Parser(){
		doc = new Document();
		sx = new SAXBuilder();
	}
	/**
	 * @param URL
	 * Create a new file from URL
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws JDomException
	 */
	public void createfilefromURL(String URL){
			try{
				theFile = new URL(URL);
				doc = sx.build(theFile);
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JDOMException e) {
				e.printStackTrace();
			}
			racine = doc.getRootElement();
			xpaF = XPathFactory.instance();
	}	
	/**
	 * Creates a new board from XML
	 */
	public void browseNodes(){
		System.out.println("lol");
		 	pos= new ArrayList<Integer>();
			xpaE = xpaF.compile("board/case", Filters.element());
			nodes = xpaE.evaluate(doc);
			pos.add(0);
			for(Element e: nodes){
				pos.add(Integer.parseInt(e.getValue()));
			}
	}		
	/**
	 * @param i
	 * @return
	 */
	public Integer getVal(int i){
		return pos.get(i);
	}	
}