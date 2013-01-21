package app;
import game.Board; 
import game.PanelJeu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import xml.Parser;

/**
 * @author ayskaw
 * Singleton panels, menus, buttons maker
 */
@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener{
	/*LOOK*/
	public static GUI gui;
	Dimension dimBaseWindow = new Dimension(510, 700);
	LineBorder lineBorder = new LineBorder(Color.BLACK);
	TitledBorder titledBorderHisto = new TitledBorder("Historique de la partie");
	String log, logVS, nick, nickvs;
	
	/*PANS+COMP*/
	JMenuBar menuBar;
	JPanel panGlobal;
	JButton jb, rafraichir=new JButton("Rafraichir");
	
	PanelJeu panPlat;
	JScrollPane panHisto;
	public static JTextArea history;
	
	/*MENU ITEMS*/
	JMenu menu1 = new JMenu("Partie");
	JMenuItem nouvellePartie = new JMenuItem("Nouvelle Partie");
	JMenuItem chargerPartie = new JMenuItem("Charger Partie");
	JMenuItem rafraichirPartie = new JMenuItem("Rafraichir Partie");
	JMenuItem supprimerPartie = new JMenuItem("Supprimer Partie");
	JMenuItem quitterPartie = new JMenuItem("Quitter Partie");
	JMenuItem aide = new JMenuItem("Aide");
	/**
	 * Make the GUI, Menu, Panels
	 */
	public GUI() {
		String ip = (String)JOptionPane.showInputDialog(this, "IP du serveur (x.x.x.x)", "IP", JOptionPane.PLAIN_MESSAGE, null, null,null);
		try {
			Parser.setURL("http://"+ip);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		rafraichir = new JButton("Rafraichir");
		rafraichir.setBackground(new Color(115, 164, 209));
		makeMe();
		makePans();
		makeMenu();
		getContentPane().add(panGlobal);
		
		rafraichirPartie.addActionListener(this);
		nouvellePartie.addActionListener(this);
		chargerPartie.addActionListener(this);
		supprimerPartie.addActionListener(this);
		
		pack();
		setVisible(true);
		
		
	}	
	private void makePans(){
		/*PANGLOB*/
		panGlobal = new JPanel(new BorderLayout());
		panGlobal.setBorder(new LineBorder(Color.BLACK));
		panGlobal.setBackground(Color.WHITE);
			/*CONTENU*/
				/*NORTH*/
					/*PLATEAU*/
					Image imagePlateau = new ImageIcon(getClass().getResource("/res/plat.png")).getImage();
					panPlat = new PanelJeu(imagePlateau, this);
			/*SOUTH*/
				/*history*/
				history = new JTextArea(650, 30);
				history.setBorder(titledBorderHisto);
				history.setEditable(false);
				history.setPreferredSize(new Dimension(100, 400));
				panHisto = new JScrollPane(history);
				panHisto.setBorder(null);
				history.setBackground(Color.GRAY);
			
		/*CONTENU->PAN*/
		panGlobal.add(panPlat, BorderLayout.NORTH);
		panGlobal.add(rafraichir);
		panGlobal.add(panHisto);	
	}	
	private void makeMenu(){
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(menu1);
		menu1.add(nouvellePartie);
		menu1.add(chargerPartie);
		menu1.add(supprimerPartie);
		menu1.add(rafraichirPartie);
	}	
	private void makeMe(){
		setTitle("Jeu de Dames");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(dimBaseWindow);
		setResizable(false);
		for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(laf.getName())) {
				try {
					UIManager.setLookAndFeel(laf.getClassName());
				} catch (Exception e) {
					System.out.println("Erreur LNF - Default used");
				}
			}
		}
	}
	public void actionPerformed(ActionEvent e){
		/**
		 * New game -> log vs logVS (both required)
		 */
		try{
		if(e.getSource()==nouvellePartie){
			history.removeAll();
			log = (String)JOptionPane.showInputDialog(this, "Pseudo", "Pseudo", JOptionPane.PLAIN_MESSAGE);
			logVS = (String)JOptionPane.showInputDialog(this, "VS", "Pseudo VS", JOptionPane.PLAIN_MESSAGE);
			nickvs = logVS;
			if(log.isEmpty()&&logVS.isEmpty()){
				JOptionPane.showMessageDialog(this, "Pas de pseudo, pas de partie.");
			}
			/**
			 * log vs logVS entered, nick setting, PHP-> newGame
			 */
			else{
				Object[] possibleNicks = {log, logVS};
				nick = (String)JOptionPane.showInputDialog(this, "Vous", "Votre pseudo", JOptionPane.PLAIN_MESSAGE, null, possibleNicks, null);
				Board.getInstance().setNick(nick);
				Board.getInstance().getParser().createfilefromURL("?newGame=1&log="+log+"&vs="+logVS+"&nick="+nick);
				panPlat.repaint();
				this.repaint();
			}
		}
		/**
		 * Delete game -> log vs logVS (both required)
		 */
		if(e.getSource()==supprimerPartie){
			String log = (String)JOptionPane.showInputDialog(this, "Pseudo", "Pseudo", JOptionPane.PLAIN_MESSAGE);
			String logVS = (String)JOptionPane.showInputDialog(this, "VS", "Pseudo VS", JOptionPane.PLAIN_MESSAGE);
			if(!log.isEmpty()&&!logVS.isEmpty()){
				/**
				 * log vs logVS entered, PHP-> delete board$log-$logVS.xml, delete SQLED game
				 */
				Board.getInstance().getParser().createfilefromURL("/?delete=1&log="+log+"&vs="+logVS);
				JOptionPane.showMessageDialog(this, "Si la partie existait, elle a été supprimée");
			}
			else JOptionPane.showMessageDialog(this, "Indiquez les deux participants");
		}
		/**
		 * Load game -> log(log required)
		 */
		if(e.getSource()==chargerPartie){
			history.removeAll();
			String log = (String)JOptionPane.showInputDialog(this, "Pseudo", "Pseudo", JOptionPane.PLAIN_MESSAGE);
			String logVS = (String)JOptionPane.showInputDialog(this, "VS", "Pseudo VS", JOptionPane.PLAIN_MESSAGE);
			if(log.isEmpty()){
				JOptionPane.showMessageDialog(this, "Pas de nom, pas de pions.");
			}
			/**
			 * log vs logVS entered, PHP-> loadGame board$log-$logVS.xml
			 */
			else{
				Object[] possibles = getBoards(log, logVS).toArray();
				/**
				 * log vs logVS entered but no game..
				 */
				if(possibles.length==0)
					JOptionPane.showMessageDialog(this, "Vous n'avez pas encore joué ensemble!");
				else{
					String game = (String)JOptionPane.showInputDialog(this, "Boards", "Choosen", JOptionPane.PLAIN_MESSAGE, null, possibles,null);
					Object[] possibleNicks = {log, logVS};
					nick = (String)JOptionPane.showInputDialog(this, "Vous", "Votre pseudo", JOptionPane.PLAIN_MESSAGE, null, possibleNicks, null);
					Board.getInstance().setNick(nick);
					String logfromIn, vsfromIn;
					String[] vsfromINF;
					String[] boardfromIn;
					boardfromIn = game.split("vs");
					logfromIn = boardfromIn[0].trim();
					vsfromIn = boardfromIn[1].trim();
					vsfromINF = vsfromIn.split("->");
					Board.getInstance().getParser().createfilefromURL("/?loadGame=1&log="+logfromIn+"&vs="+vsfromINF[0]);
					nickvs= vsfromINF[0];
					gui.writeAc("Nick " +getNick());
					gui.writeAc("Nick vs "+getNickvs());
				}
					}
					
				}
			}
		
		catch(Exception er){
			System.out.println(er.getMessage());
		}
		panPlat.repaint();
		this.repaint();
		}	
	/**
	 * @param str The string to show in the "history"
	 */
	public void writeAc(String str){
		history.append(str);
	}
	/**
	 * @param log The first player
	 * @param logVS The second player
	 * @return Board associated with this game (log vs logVS)
	 */
	public ArrayList<String> getBoards(String log, String logVS){
		ArrayList<String> boards = new ArrayList<String>();
		try {
			URL url = new URL(Parser.url+"/?loadGame=1&logs="+log+"&vss="+logVS); //PHP -> loadGame
			InputStreamReader ipsr = new InputStreamReader(url.openStream());
			BufferedReader br = new BufferedReader(ipsr);
			String line = null;
			while ((line = br.readLine()) != null) {
				boards.add(line+"\n");
			}
				br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return boards;
	}
	/**
	 * @return nick The nick of the player associated with this GUI
	 */
	public String getNick(){
		return nick;
	}
	public String getNickvs(){
		return nickvs;
	}
}

