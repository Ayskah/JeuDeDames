package xml;
import game.Board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import app.GUI;

/**
 * Parse the XML file made by PHP from an URL
 * @author ayskaw
 */
public class Parser{
	Document doc;
	SAXBuilder sx;
	Element root, node;
	List<Element> nodes;
	XPathExpression<Element> xpaE;
	XPathFactory xpaF;
	URL theFile;
	ArrayList<Integer> pos;
	HashMap<Integer, ArrayList<Integer>> posandMoves;
	
	public static URL url;
	/**
	 * Create new parser
	 */
	public Parser(){
		doc = new Document();
		sx = new SAXBuilder();
	}
	/**
	 * Parse the file taken from URL 
	 * @param URL THE FILE TO BROWSE
	 * @throws SAXBuilderException If file is XMLY incorrect, get the actual XMl from HTML
	 */
	public void createfilefromURL(String URL){
		String info="";
			try {
				theFile = new URL(url+URL);
				doc = sx.build(theFile);
				root = doc.getRootElement();
				xpaF = XPathFactory.instance();
				posandMoves = new HashMap<Integer, ArrayList<Integer>>();
				browseNodes();
			}
			catch (Exception e) {
				try {
					InputStreamReader ipsr = new InputStreamReader(theFile.openStream());
					BufferedReader br = new BufferedReader(ipsr);
					String line = null;
					while ((line = br.readLine()) != null){
						info+=line;
					}
						br.close(); 
						JOptionPane.showMessageDialog(GUI.gui, info, "Choosen", JOptionPane.PLAIN_MESSAGE);
						ipsr.close();
				}
				catch (IOException e1) {
					e.printStackTrace();
				}
				
			}
			
		}
	/**
	 * Browse nodes of the parsed file
	 * @throws InterruptedException 
	 */
	public void browseNodes(){
			pos = new ArrayList<Integer>();
			xpaE = xpaF.compile("board/square", Filters.element());
			nodes = xpaE.evaluate(doc);
			posandMoves.put(0, new ArrayList<Integer>());
			pos.add(0);
			for(Element e: nodes){ //FOR EACH NODE -> browseMoves -> posandMoves(pawn, moves)
				int val = Integer.parseInt(e.getText());
				ArrayList<Integer> moves = new ArrayList<Integer>(browseMoves(pos.size()));
				posandMoves.put(val, moves);
				pos.add(val);
			}
	}	
	/**
	 * @param id The index of the pawn position on the board
	 * @return Possible moves parsed from this index
	 */
	public ArrayList<Integer> browseMoves(int id){	
			ArrayList<Integer> moves = new ArrayList<Integer>();
			xpaE = xpaF.compile("board/square["+id+"]//*//squares", Filters.element()); // /board/square/posMoves/squares
			nodes = xpaE.evaluate(doc);
			for(Element e: nodes){ //ADD MOVES TO moves
				moves.add(Integer.parseInt(e.getText()));
			}
	return moves;
	}
	/**
	 * @param id The index of the pawn position on the board
	 * @return The pawn value associated
	 */
	public Integer getVal(int id){
		return pos.get(id);
	}	
	/**
	 * @param id The player to get (1/2)
	 * @return The nickname for this player
	 */
	public String getPlayers(int id){
		id--;
		xpaE = xpaF.compile("board/players", Filters.element());
		node = xpaE.evaluateFirst(doc);
		String log;
		if(id==0)
		log = node.getAttributeValue("one");
		else log = node.getAttributeValue("two");
		return log;
	}
	/**
	 * @return The actual player
	 */
	public String getTurn(){
		xpaE = xpaF.compile("board/players", Filters.element());
		node = xpaE.evaluateFirst(doc);
		return node.getAttributeValue("turn");
	}
	/**
	 * @param url
	 * @throws MalformedURLException
	 */
	public static void setURL(String url) throws MalformedURLException{
		Parser.url=new URL(url);
	}
}