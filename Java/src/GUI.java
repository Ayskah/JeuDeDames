import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener{
	/*LOOK*/
	static GUI gui;
	Dimension dimBaseWindow = new Dimension(500, 800);
	LineBorder lineBorder = new LineBorder(Color.BLACK);
	TitledBorder titledBorderHisto = new TitledBorder("Historique de la partie");
	/*PANS+COMP*/
		JMenuBar menuBar;
		JPanel panGlobal;
		JButton jb;
		PanelJeu panPlat;
		JScrollPane panHisto;
		JTextArea historique;
	/*MENU ITEMS*/
		JMenu menu1 = new JMenu("Partie");
		JMenuItem nouvellePartie = new JMenuItem("Nouvelle Partie");
		JMenuItem chargerPartie = new JMenuItem("Charger Partie");
		JMenuItem quitterPartie = new JMenuItem("Quitter Partie");
		JMenuItem aide = new JMenuItem("Aide");
		
	/*NEW GUI*/
	/**
	 * GUI
	 */
	public static GUI getInstance(){
		return gui;
	}
	
	public GUI() {
		makeMe();
		makeMenu();
		makePans();		
		getContentPane().add(panGlobal);
		nouvellePartie.addActionListener(this);
		pack();
		setVisible(true);
	}	
	/*MAKE GLOBAL PAN
 * ->NORTH: BOARD
 * ->SOUTH: HISTO
 * ->ADD THIS.
 */
	private void makePans(){
		/*PANGLOB*/
		panGlobal = new JPanel(new BorderLayout());
		panGlobal.setBorder(new LineBorder(Color.BLACK));
		panGlobal.setBackground(Color.WHITE);
			/*CONTENU*/
				/*NORTH*/
					/*PLATEAU*/
					Image imagePlateau = new ImageIcon("res/plat.png").getImage();
					panPlat = new PanelJeu(imagePlateau, this);
			/*SOUTH*/
				/*HISTORIQUE*/
				historique = new JTextArea(10, 30);
				historique.setBorder(titledBorderHisto);
				historique.setEditable(false);
				historique.setPreferredSize(new Dimension(100, 400));
				panHisto = new JScrollPane(historique);
				panHisto.setBorder(null);
				historique.setBackground(Color.GRAY);
			
		/*CONTENU->PAN*/
		panGlobal.add(panPlat, BorderLayout.NORTH);
		panGlobal.add(panHisto);	
	}	
	private void makeMenu(){
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(menu1);
		menu1.add(nouvellePartie);
		menu1.add(chargerPartie);
	}	
	/*LAF+TITLE+SIZE*/	
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
	/*LISTENS TO:
	 * ->nouvellePartie MENU LABEL
	 */
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==nouvellePartie)
			Board.getInstance().getParser().createfilefromURL("http://localhost/JeuDames/?newGame=1");
			Board.getInstance().getParser().browseNodes();
			Board.getInstance().setVals();
			this.repaint();
			panPlat.repaint();
	}
	/**
	 * @param str
	 */
	public void writeAc(String str){
		historique.append(str);
	}
}

