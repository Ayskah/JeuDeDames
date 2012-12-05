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
	ImageIcon imgW, imgB;
	String theBoard;
	int i=0;
	ArrayList<JButton> butP=new ArrayList<JButton>();
	
	
	/*CONSTRUCT WITHOUT DRAWING WITH BOARD.PNG*/
	/**
	 * @param img
	 */
	public PanelJeu(Image img) {
		setPreferredSize(new Dimension(500, 500));
		imgB = new ImageIcon("res/noires.png");
		imgW = new ImageIcon("res/blanches.png");
		this.img=img;
		this.setLayout(new GridLayout(10, 10));
		for(int i=1;i<=100;i++){
			JButton p=new JButton();
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
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
		for(int i=1;i<100;i++){ 
			int x, y;
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
			int ind=Integer.parseInt(((JButton)e.getSource()).getName());
			System.out.println(ind);
			if(Board.getInstance().getParser().getVal(ind)==1 || Board.getInstance().getParser().getVal(ind)==2)
				System.out.println("Pion");
	}
}