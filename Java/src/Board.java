import java.awt.Point;
import java.util.ArrayList;
/*SINGLETON GAME BOARD
 * SET Xs, Ys.
 * RETURNS ArrayList<Xs, Ys>.
 * RETURN THIS GB PARSER
 */
/**
 * @author ayskaw
 *
 */
public class Board{
	private ArrayList<Point> ptsP;
	static Board plat;
	private Parser theParser;
	private ArrayList<Integer> lstPwns;
	private Integer[][] tabPwns;
	/**
	 * @return
	 */
	public static Board getInstance() {
		if(plat == null) {
			plat = new Board();		
		}
		return plat;
	}	
	public Board(){
		System.out.println("Board");
		ptsP = new ArrayList<Point>();
		setptsP();
		theParser=new Parser();
		theParser.createfilefromURL("http://localhost/JeuDames/");
		theParser.browseNodes();
		tabPwns = new Integer[10][10];
	}	
	/**
	 * @return
	 */
	public ArrayList<Point> getptsP(){
		return ptsP;
	}
	public void setptsP(){
		ptsP.add(new Point());
		ptsP.add(new Point(4,10));//1
		ptsP.add(new Point(60,8));//2
		ptsP.add(new Point(107,10));//3
		ptsP.add(new Point(161,9));//4
		ptsP.add(new Point(213,12));//5
		ptsP.add(new Point(261,9));//6
		ptsP.add(new Point(308,7));//7
		ptsP.add(new Point(359,12));//8
		ptsP.add(new Point(408,11));//9
		ptsP.add(new Point(460,9));//10
		ptsP.add(new Point(7,58));//11
		ptsP.add(new Point(58,60));//12
		ptsP.add(new Point(110,58));//13
		ptsP.add(new Point(161,60));//14
		ptsP.add(new Point(209,62));//15
		ptsP.add(new Point(263,62));//16
		ptsP.add(new Point(313,58));//17
		ptsP.add(new Point(364,64));//18
		ptsP.add(new Point(408,63));//19
		ptsP.add(new Point(466,63));//20
		ptsP.add(new Point(3,110));//21
		ptsP.add(new Point(58,114));//22
		ptsP.add(new Point(111,114));//23
		ptsP.add(new Point(157,109));//24
		ptsP.add(new Point(205,109));//25
		ptsP.add(new Point(260,111));//26
		ptsP.add(new Point(306,110));//27
		ptsP.add(new Point(358,112));//28
		ptsP.add(new Point(406,111));//29
		ptsP.add(new Point(457,112));//30
		ptsP.add(new Point(7,162));//31
		ptsP.add(new Point(60,162));//32
		ptsP.add(new Point(110,165));//33
		ptsP.add(new Point(158,164));//34
		ptsP.add(new Point(209,162));//35
		ptsP.add(new Point(262,163));//36
		ptsP.add(new Point(309,163));//37
		ptsP.add(new Point(362,162));//38
		ptsP.add(new Point(405,166));//39
		ptsP.add(new Point(460,160));//40
		ptsP.add(new Point(7,212));//41
		ptsP.add(new Point(60,215));//42
		ptsP.add(new Point(108,212));//43
		ptsP.add(new Point(160,214));//44
		ptsP.add(new Point(209,214));//45
		ptsP.add(new Point(260,214));//46
		ptsP.add(new Point(309,213));//47
		ptsP.add(new Point(360,212));//48
		ptsP.add(new Point(409,215));//49
		ptsP.add(new Point(464,215));//50
		ptsP.add(new Point(5,260));//51
		ptsP.add(new Point(60,262));//52
		ptsP.add(new Point(107,268));//53
		ptsP.add(new Point(162,264));//54
		ptsP.add(new Point(209,264));//55
		ptsP.add(new Point(257,262));//56
		ptsP.add(new Point(309,263));//57
		ptsP.add(new Point(358,264));//58
		ptsP.add(new Point(411,267));//59
		ptsP.add(new Point(459,261));//60
		ptsP.add(new Point(3,309));
		ptsP.add(new Point(60,313));
		ptsP.add(new Point(105,311));
		ptsP.add(new Point(161,310));
		ptsP.add(new Point(207,310));
		ptsP.add(new Point(260,312));
		ptsP.add(new Point(308,312));
		ptsP.add(new Point(357,311));
		ptsP.add(new Point(405,310));
		ptsP.add(new Point(461,310));
		ptsP.add(new Point(4,361));//71
		ptsP.add(new Point(59,362));//72
		ptsP.add(new Point(107,363));//73
		ptsP.add(new Point(163,365));//74
		ptsP.add(new Point(209,361));//75
		ptsP.add(new Point(263,362));//76
		ptsP.add(new Point(307,361));//77
		ptsP.add(new Point(361,362));//78
		ptsP.add(new Point(411,363));//79
		ptsP.add(new Point(460,361));//80
		ptsP.add(new Point(3,413));//81
		ptsP.add(new Point(59,415));//82
		ptsP.add(new Point(109,414));//83
		ptsP.add(new Point(159,411));//84
		ptsP.add(new Point(211,412));//85
		ptsP.add(new Point(260,415));//86
		ptsP.add(new Point(308,412));//87
		ptsP.add(new Point(357,411));//88
		ptsP.add(new Point(408,411));//89
		ptsP.add(new Point(459,412));//90
		ptsP.add(new Point(6,463));//91
		ptsP.add(new Point(61,463));//92
		ptsP.add(new Point(109,462));//93
		ptsP.add(new Point(158,463));//94
		ptsP.add(new Point(206,462));//95
		ptsP.add(new Point(256,459));//96
		ptsP.add(new Point(306,461));//97
		ptsP.add(new Point(359,464));//98
		ptsP.add(new Point(405,463));//99
		ptsP.add(new Point(457,465));//100
	}
	/**
	 * @return
	 */
	public Parser getParser(){
		return theParser;
	}
	
	/**
	 * @return Integer[10][10] Tab GB
	 */
	public Integer[][] gettabPwns(){
		return tabPwns;
	}
	public void setVals(){
		int k=0;
		for(int i=1; i<=10;i++){
			k++;
			for(int j=1;j<=10;j++){
				k++;
				tabPwns[i][j]=theParser.getVal(k);
			}
		}
	}
}

