package game;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import app.GUI;
/** Draw GameBoard
 * @author ayskaw
 *
 */
@SuppressWarnings("serial")
public class PanelJeu extends JPanel implements ActionListener{
	Image img;
	GUI gui;
	ImageIcon imgW, imgB, imgdW, imgdB, imggOon;
	String theBoard;
	int i=0, indFrom, indTo, snd;
	/**
	 * Make a new game panel: board image, pawns images
	 * @param img The image of the board
	 * @param gui The GUI associated with this board
	 */
	public PanelJeu(Image img, GUI gui){
		this.gui = gui;
		setPreferredSize(new Dimension(550, 500));
		imgB = new ImageIcon(getClass().getResource("/res/noires.png"));
		imgW = new ImageIcon(getClass().getResource("/res/blanches.png"));
		imgdB = new ImageIcon(getClass().getResource("/res/dNoires.png"));
		imgdW = new ImageIcon(getClass().getResource("/res/dBlanches.png"));
		imggOon = new ImageIcon(getClass().getResource("/res/goon.png"));
		this.img=img;
		for(int i=1;i<=100;i++){ //For each indexes of the board
			JButton p=new JButton(); //Make a button
			this.setLayout(new GridLayout(10,10)); 
			p.setLocation(Board.getInstance().getptsP().get(i)); //Put it in the 10*10 grid
			p.addActionListener(this); //Make it active
			p.setOpaque(false); //But not visible
			p.setContentAreaFilled(false); //.. less visible
			p.setBorderPainted(false); // .. not at all
			p.setName(""+i); //Find it on the board
			this.add(p); //Add it to the board
		}
		Board.getInstance().getParser().createfilefromURL("");
		Board.getInstance().getParser().browseNodes();
	}
	/**
	 * Paints everything that's need to be : board, pawns
	 */
	public void paintComponent(Graphics g){
		Board.getInstance().getParser().createfilefromURL("/?loadGame=1&log="+Board.getInstance().getParser().getPlayers(1)+"&vs="+Board.getInstance().getParser().getPlayers(2));
		g.drawImage(img, 0, 0, null);
		for(int i=1;i<100;i++){ 
			int x, y;
			int val=Board.getInstance().getParser().getVal(i);
			x= (int)Board.getInstance().getptsP().get(i).getX();
			y= (int)Board.getInstance().getptsP().get(i).getY();
			if(val==2){ //Whitey
				g.drawImage(imgW.getImage(), x, y, null);
			}
			else if(val==22){ //White 
				g.drawImage(imgdW.getImage(), x, y, null);
			}
			else if(val==1){ //Blackey
				g.drawImage(imgB.getImage(), x, y, null);
			}
			else if(val==11){ //Black
				g.drawImage(imgdB.getImage(), x, y, null);
			}
		}
		this.repaint();
	}
	/**
	 * @param e The pawn clicked
	 */
	public void actionPerformed(ActionEvent e){
		Integer valindFrom=-1;
		if(snd==0){ //If first time clicked
			indFrom = Integer.parseInt(((JButton)e.getSource()).getName()); //What was clicked?
			valindFrom = Board.getInstance().getParser().getVal(indFrom);
			if(valindFrom!=0){ //If this is a pawn 
				snd=1; //Let's move it!
				gui.writeAc("Case: "+indFrom);
			}
		}
		else if(snd==1){ //If second time clicked
			indTo = Integer.parseInt(((JButton)e.getSource()).getName());
			if(Board.getInstance().getParser().browseMoves(indFrom).contains(indTo)){ //Is the move correct?
				String one = Board.getInstance().getParser().getPlayers(1);
				String vs = Board.getInstance().getParser().getPlayers(2);
				gui.writeAc(" - > vers Case: "+indTo+"\n");
				Board.getInstance().getParser().createfilefromURL("/?move="+indFrom+"-"+indTo+"&log="+one+"&vs="+vs+"&nick="+Board.getInstance().getNick()); //The move is correct : PHP->move
				snd=0;
				gui.writeAc("Au tour de "+Board.getInstance().getParser().getTurn()+"\n");
			}
			else {gui.writeAc("Impossible..");snd=0;}
		}
		}
	}
