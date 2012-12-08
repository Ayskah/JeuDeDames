import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/*GAME BOARD DRAWING
 * - PAINT USING Xs, Ys FROM PLATEAU
 * - PAINT PB, PW FROM XML BOARD INDEX
*/
/**
 * @author ayskaw
 *
 */
@SuppressWarnings("serial")
public class PanelJeu extends JPanel implements ActionListener{
	Image img;
	GUI gui;
	ImageIcon imgW, imgB, imggOon;
	String theBoard;
	int i=0, indFrom, indTo, snd;
	ArrayList<JButton> butP=new ArrayList<JButton>();
	
	/*CONSTRUCT WITHOUT DRAWING WITH BOARD.PNG*/
	/**
	 * @param img
	 */
	public PanelJeu(Image img, GUI gui) {
		this.gui = gui;
		setPreferredSize(new Dimension(500, 500));
		imgB = new ImageIcon("res/noires.png");
		imgW = new ImageIcon("res/blanches.png");
		imggOon = new ImageIcon("res/goon.png");
		this.img=img;
		for(int i=1;i<=100;i++){
			JButton p=new JButton();
			this.setLayout(new GridLayout(10,10));
			p.setLocation(Board.getInstance().getptsP().get(i));
			p.addActionListener(this);
			p.setOpaque(false);
			p.setContentAreaFilled(false);
			p.setBorderPainted(false);
			p.setName(""+i);
			butP.add(p);
			this.add(p);
		}
	}
	/*DRAWBOARD
	 * ->GRAPHICS<-BOARD+PW/PB FROM BOARD PARSER
	 */
	@Override
	public void paintComponent(Graphics g){
		g.drawImage(img, 0, 0, null);
		for(int i=1;i<100;i++){ 
			int x, y;
			//System.out.println("lol");
			x= (int)Board.getInstance().getptsP().get(i).getX();
			y= (int)Board.getInstance().getptsP().get(i).getY();
			if(Board.getInstance().getParser().getVal(i)==2){
				g.drawImage(imgW.getImage(), x, y, null);
			}
			else if(Board.getInstance().getParser().getVal(i)==1){
				g.drawImage(imgB.getImage(), x, y, null);
			}
		}
	}
	
	

	public void actionPerformed(ActionEvent e){
		int val = Board.getInstance().getParser().getVal(Integer.parseInt(((JButton)e.getSource()).getName()));
		if(val!=1){	
			if(snd==0){
				indFrom = Integer.parseInt(((JButton)e.getSource()).getName());
				gui.writeAc("- From "+indFrom+" -> ");
				snd=1;
			}
			if(snd==1 && val==0){
				indTo = Integer.parseInt(((JButton)e.getSource()).getName());
				if(Board.getInstance().getParser().browseMoves(indFrom).contains(indTo)){
					gui.writeAc("To "+indTo+"\n");
				}
				else gui.writeAc("Refusé\n");
				snd=0;
			}
		}
	}
}