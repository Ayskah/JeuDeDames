import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	HashMap<Integer, ArrayList<Integer>> posandMoves;
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
			catch (Exception e) {
				e.printStackTrace();
			}
			racine = doc.getRootElement();
			xpaF = XPathFactory.instance();
			posandMoves = new HashMap<Integer, ArrayList<Integer>>();
	}	
	/**
	 * Creates a new board from XML
	 */
	public void browseNodes(){
			pos = new ArrayList<Integer>();
			xpaE = xpaF.compile("board/case", Filters.element());
			nodes = xpaE.evaluate(doc);
			posandMoves.put(0, new ArrayList<Integer>());
			pos.add(0);
			for(Element e: nodes){
				int val = Integer.parseInt(e.getText());
				ArrayList<Integer> moves = new ArrayList<Integer>(browseMoves(pos.size()));
				posandMoves.put(val, moves);
				pos.add(val);
			}
	}
	/**
	 * @param int Position
	 * @return : ArrayList<Integer> Possible moves
	*/
	public ArrayList<Integer> browseMoves(int id){	
			ArrayList<Integer> moves = new ArrayList<Integer>();
			xpaE = xpaF.compile("board/case["+id+"]//*//cases", Filters.element());
			nodes = xpaE.evaluate(doc);
			for(Element e: nodes){
				moves.add(Integer.parseInt(e.getText()));
			}
	return moves;
	}
	/**
	 * @param int Position
	 * @return int Value pawn
	 */
	public Integer getVal(int i){
		return pos.get(i);
	}	
}