<?php	
	$board = array();	
	$bdd = new PDO('sqlite:boards.db');
	$bdd->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);	
	$doc = new DOMDocument();
	date_default_timezone_set('Europe/Berlin');
	if(isset($_GET['loadGame'])&&isset($_GET['logs'])){
		$log=$_GET['logs'];
		$vs=$_GET['vss'];
		$ans = loadSQL("SELECT * FROM boards", $bdd, "lec");
		$i=0;
		while($res = $ans->fetch()){				
			$two = $res['playerTwo'];
			$one = $res['playerOne'];
			if($one==$log&&$two==$vs){
				$i++;
				echo "$one vs $two->".$res['time']." : ".$one." : ".$res['nbpOne']." - ".$two." : ".$res['nbpTwo']."\n";
			}
		}
	}
	
	if(isset($_GET['loadGame'])&&isset($_GET['log'])){
		$log=$_GET['log'];
		$vs=$_GET['vs'];
		try{
		$doc->load("board$log-$vs.xml");	
		}	
		catch (Exception $e){
			$doc->load("board$vs-$log.xml");
		}
		echoxmlBoard($doc);
	}

	
	if(!isset($_GET['newGame'])&&!isset($_GET['move'])&&!isset($_GET['loadGame'])&&!isset($_GET['delete'])&&!isset($_GET['acc'])){
		initaBoard($board);
		makeXML($doc, "");
	}
	if(isset($_GET['acc'])){
		$players = loadSQL("SELECT COUNT(*) FROM PLAYERS", $bdd, "sel");
		$histo= loadSQL("SELECT COUNT(*) FROM HISTORY", $bdd, "sel");
		$nbPlayers = $players->fetch();
		$nbHisto = $histo->fetch();
		$boards = loadSQL("SELECT COUNT(*) FROM BOARDS", $bdd, "sel");
		$nbBoards = $boards->fetch();
		$boardsToday = loadSQL("SELECT COUNT(*) FROM BOARDS WHERE time='".date('d-m-Y')."'", $bdd, "sel");
		$nbToday = $boardsToday->fetch();
		echo "<center>Jeu De Dames - Interface Web</center>";
		echo "<a href='JeuDames.jar'>Télécharger le jeu</a><br/>";
		echo "Statistiques principales : <br/><br/>";
		echo "Nombre de parties finies -> $nbHisto[0]<br/>";
		echo "Nombre de joueurs s'étant connectés -> $nbPlayers[0]<br/>";
		echo "Nombre de parties en cours -> $nbBoards[0] <br/>";
		echo "Nombre de parties débutées aujourd'hui -> $nbToday[0]<br/>";
		
				
		
	}
	if(isset($_GET['delete'])){
		try{
			$log = $_GET['log'];
			$vs = $_GET['vs'];
			$ans = loadSQL("DELETE FROM boards WHERE playerOne='$log' AND playerTwo='$vs';", $bdd, "del");
			unlink("board$log-$vs.xml");
		}
		catch(PDOException $e){
			echo "Erreur de base de données : ".$e->getMessage();
		}
	}

	if(isset($_GET['newGame'])){
		$log=$_GET['log'];
		$vs=$_GET['vs'];
		$today=date('d-m-Y');
		try{
		$players = loadSQL("SELECT * FROM PLAYERS WHERE id='$log'", $bdd, "sel");
		$ans = $players->fetch();
		if($ans[0]!=$log){
			$ans = loadSQL("INSERT INTO PLAYERS(id) VALUES('$log')", $bdd, "ins");
		}
		$players = loadSQL("SELECT * FROM PLAYERS WHERE id='$vs'", $bdd, "sel");
		$ans = $players->fetch();
		if($ans[0]!=$vs){
			$ans = loadSQL("INSERT INTO PLAYERS(id) VALUES('$vs')", $bdd, "ins");
		}
		$ans = loadSQL("INSERT INTO boards(playerOne, playerTwo, nbpOne, nbpTwo, board, white, time) VALUES ('$log', '$vs', 20, 20, 'board$log-$vs.xml', '$log', '$today')", $bdd, "ins");
		initaBoard($board);
		initParty($board);
		$turn=$_GET["log"];
		makeXML($doc, $turn);	
		}
		catch(PDOException $e){
			if(strstr($e->getMessage(), "Integrity"))
				echo "Vous ne pouvez pas creer plus d'une partie a la fois entre vous, supprimez l'ancienne..";		
		}		
	}
	if(isset($_GET['move'])){
		$isValid=
		$log=$_GET['log'];
		$vs=$_GET['vs'];
		$nick = $_GET['nick'];
		$doc->load("board$log-$vs.xml");
		$docS = simplexml_import_dom($doc);
		
		$pNode = $docS->{"players"};
		$sNode = $docS->{"square"};
		$turn = $pNode->attributes()->turn;
		if($nick==$turn){
			$col= loadSQL("SELECT white FROM BOARDS WHERE playerOne='$log' AND playerTwo='$vs' AND playerOne='$log'", $bdd, "sel");
			$res = $col->fetch();	
			$isDame = explode("*", $_GET['move']);
			$board = loadXMLfromB($doc);
			$boardS = "";
			$move = $_GET['move'];
			list($from, $to)= explode("-", $move);
			if(($res['white']==$nick&&$sNode[$from-1]==2||$sNode[$from-1]==22)||($res['white']!=$nick&&$sNode[$from-1]==1||$sNode[$from-1]==11)){
				
				pawnsAte($doc, $from-1, $to-1);	
				if($turn==$log)
					$turn=$vs;
				else $turn=$log;
				makeXML($doc);
			}	
			else echo "Vous ne jouez pas ces pions";				
		}
		else echo "Attendez votre tour..";
	}
/*
*DISPLAY XML
*/	
	function echoxmlBoard($doc){
		$doc->formatOutput = true;
		echo $doc->saveXML();
	}
/*
*LOAD XML NODES->BOARD FROM FILE
*/	
	function loadXMLfromB($doc){
		$docS = simplexml_import_dom($doc);
		$square=$docS->{"square"};
		$p=0;
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
				$board[$i][$j] = $square[$p];
				$p++;
			}
		}
		return $board;
	}
/*
*XMLIZE BOARD[10][10]
*/
	function makeXML($doc){
		global $board;
		global $log;
		global $vs;
		global $turn;
		global $bdd;

		$whites=0;
		$blacks=0;
		if($doc->documentElement){
			$old = $doc->documentElement;
		}
		
		$eBoard=$doc->createElement('board');
		$doc->version='1.0';
		$doc->encoding='UTF-8';	
		$doc->appendChild($eBoard);
		$p=1;
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
				$val= $board[$i][$j];
				$esquare=$doc->createElement('square', $val);
				if($val==1){
					$eMoves=$doc->createElement('posMoves');
					$ePoss=$doc->createElement('squares', $p+11);
					$eMoves->appendChild($ePoss);
					$ePoss=$doc->createElement('squares', $p+9);
					$eMoves->appendChild($ePoss);
					$esquare->appendChild($eMoves);
					$blacks++;
				}
				if($val==2){
					$eMoves=$doc->createElement('posMoves');
					$ePoss=$doc->createElement('squares', $p-11);
					$eMoves->appendChild($ePoss);
					$ePoss=$doc->createElement('squares', $p-9);
					$eMoves->appendChild($ePoss);
					$esquare->appendChild($eMoves);	
					$whites++;
				}				
				if($val==22){
					$whites++;
					$eMoves = calculatespecialMove($p, $doc);
					$esquare->appendChild($eMoves);
				}
				if($val==11){
					$blacks++;
					$eMoves = calculatespecialMove($p, $doc);
					$esquare->appendChild($eMoves);
				}
				$esquare->setAttribute('id', $p);
				$eBoard->appendChild($esquare);
				$p++;
			}
		}
		if(isset($old))
			$doc->removeChild($old);

			$today=date('d-m-Y');
			if($whites<20&&$blacks<20)
			$ans = loadSQL("UPDATE boards SET nbpOne=$whites WHERE playerOne='$log' AND playerTwo='$vs'", 
$bdd, "ins");
			$ans = loadSQL("UPDATE boards SET nbpTwo=$blacks WHERE playerOne='$log' AND playerTwo='$vs'", 
$bdd, "ins");
			$players = $doc->createElement('players');
			$aOne=$log;
			$aTwo=$vs;
		
			$players->setAttribute("one", $aOne);
			$players->setAttribute("two", $aTwo);
			$players->setAttribute("turn", $turn);
			$eBoard->appendChild($players);
			$doc->formatOutput = true;
			$doc->save("board".$log."-".$vs.".xml");
			echoxmlBoard($doc);
		}
/*
*INIT WHEN "newGame":
*	- SET GAMEINIT STATE
*	- RETURN NEW board[10][10]
*/
	function initParty(&$board){
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
/*INIT BLACKS*/
					if($i<5){
					if(($i%2==0&&$j%2!=0)||$i%2!=0&&$j%2==0){
						$board[$i][$j]=1;
					}
				}
/*INIT WHITES
- TAKE A SIDE, REVERSE IT, YOU'RE DONE!
*/
				elseif($i>6){
					$aim= (10-$i)+1;
					if($board[$aim][$j]==0){
						$board[$i][$j]=2;
					}					
				}
			}
		}
	}
/*
*INIT A BOARD:
*	- SET 0 STATE
*	- RETURN NEW board[10][10]
*/	
	function initaBoard(&$board){
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
				$board[$i][$j]=0;
			}
		}
	}
/*
*/	
	function loadSQL($req, $bdd, $ins){
		try{
			if($ins!="ins"){			
			$result = $bdd->query($req);
			return $result;
			}
		else{
			$bdd->exec($req);
		}
		}
		catch(PDOException $err){
			echo $err;
		}		
	}	
/*
*GET LINE, COL FROM XML NODE IND
*	-> RETURN (i,j)
*/
	function getIJ($ind){
		$p=1;
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
				if($p==$ind){
					return array($i, $j);
				}
				$p++;
			}
		}
	}	
/*
*CHECK IF PAWN EATING
*	-> FROM, TO, DOC
*/
	function pawnsAte($doc, $from, $to){
		global $bdd;

		$docS = simplexml_import_dom($doc);
		$board = $doc->documentElement;
		$square = $docS->{'square'};
		$fromB=getIJ($from);
		$toB=getIJ($to);
		$pwn = $square[(int)$from];
		$aim = $square[(int)$to];
		$log=$_GET['log'];
		$vs=$_GET['vs'];
		$event = $doc->createElement('event');
		if($pwn==2){
			if($aim==0&&($to==$from-9||$to==$from-11)){
				$square[$to]=2;
				$square[$from]=0;
					if($to<10){
						$square[$to]=22;
					}
			}
			else{
				if(($aim==1||$aim==11)&&$to==$from-9&&$to>10){
					if($square[(int)$to-9]==0){
						$square[(int)$to-9]=2;
						$square[(int)$to]=(($aim==11)?1:0);
						$square[(int)$from]=0;
						if($to-9<10){
							$square[$to-9]=22;						
						}
					}
				}
				else if(($aim==1||$aim==11)&&$to==$from-11&&$to>10){
					if($square[(int)$to-11]==0){
						$square[(int)$to-11]=2;
						$square[(int)$to]=(($aim==11)?1:0);
						$square[(int)$from]=0;
						if($to-11<10){
							$square[$to-11]=22;					
						}
					}
				}
			}
		}	
		if($pwn==22){
			hitdameAte($from, $to, $doc);
		}
		if($pwn==11){
			hitdameAte($from, $to, $doc);
		}
		if($pwn==1){
			if($aim==0&&($to==$from+9||$to==$from+11)){
				$square[$to]=1;
				$square[$from]=0;
				if($to>=90){
					$square[$to]=11;			
				}
			}
			else{
				if(($aim==2||$aim==22)&&$to==$from+9){
					if($square[(int)$to+9]==0){
						$square[(int)$to+9]=1;
						$square[(int)$to]=(($aim==22)?2:0);
						$square[(int)$from]=0;
						if($to+9>=90){
							$square[$to+9]=11;			
						}
					}
				}
				else if(($aim==2||$aim==22)&&$to==$from+11){
					if($square[(int)$to+11]==0){
						$square[(int)$to+11]=1;
						$square[(int)$to]=(($aim==22)?2:0);
						$square[(int)$from]=0;
						if($to+11>=90){
							$square[$to+11]=11;
						}
					}
				}
			}
		}
			$boardS="";
			for($i=0;$i<100;$i++){
				$boardS.= $square[$i]; 
			}
			exec("./game '".$boardS."'", $result);
			if($result[0]=="whites 0"||$result[1]=="blacks 0"){
				if($result[0]=="whites 0")
				echo "Partie finie, les blancs perdent..";
				else if($result[0]=="blacks 0")
					echo "Partie finie, les noirs perdent..";
				$ans = loadSQL("SELECT * FROM boards WHERE playerOne='$log' AND playerTwo='$vs'", $bdd, "lec");
				$nbP = $ans->fetch();
				$today = date('d-m-Y');
				loadSQL("INSERT INTO history(playerOne, playerTwo, nbpOne, nbpTwo, time) VALUES ('$log', '$vs', ".$nbP['nbpOne'].", ".$nbP['nbpTwo'].", '$today')", $bdd, "ins");	
				loadSQL("DELETE FROM boards WHERE playerOne='$log' AND playerTwo='$vs';", $bdd, "del");
				echo "Partie finie.. ".(strpos($result[0], "whites 0")?"les noirs perdent!":"les blancs perdent!");		
				unlink("board$log-$vs.xml");
			}
	}
/*
*IF HITDAME-> CHECK DIAGS FOR PWNS TO EAT*/	
	function calculatespecialMove($from, $doc){
		$fromB=getIJ($from);
		$docS = simplexml_import_dom($doc);
		$squares = $docS->{'square'};
		$aimL=$fromB[0];
		$aimC= $fromB[1];
		$moves=$doc->createElement('posMoves');
		$aimCTEMP=$aimC;
		$goOn = true;
		
		if($aimCTEMP<10){
			for($i=$aimL-1; $i>=1; $i--){
				if($aimCTEMP<10&&$goOn){
					$aimCTEMP++;
					if(($squares[getP($i, $aimCTEMP)-1]==22||$squares[getP($i, $aimCTEMP)-1]==2)&&$squares[$from-1]==22)
						$goOn=false;
					if(($squares[getP($i, $aimCTEMP)-1]==11||$squares[getP($i, $aimCTEMP)-1]==1)&&$squares[$from-1]==11)
						$goOn=false;
					if($goOn){
						$ePoss=$doc->createElement('squares', getP($i, $aimCTEMP));
						$moves->appendChild($ePoss);
					}
					}
				}
			}
		
		$aimCTEMP=$aimC;
		$goOn=true;
		if($aimCTEMP>1){
			for($i=$aimL-1; $i>=1; $i--){
				if($aimCTEMP>1&&$goOn){
					$aimCTEMP--;
					if(($squares[getP($i, $aimCTEMP)-1]==22||$squares[getP($i, $aimCTEMP)-1]==2)&&$squares[$from-1]==22)
						$goOn=false;
					if(($squares[getP($i, $aimCTEMP)-1]==11||$squares[getP($i, $aimCTEMP)-1]==1)&&$squares[$from-1]==11)
						$goOn=false;
					if($goOn){
						$ePoss=$doc->createElement('squares', getP($i, $aimCTEMP));
						$moves->appendChild($ePoss);
					}
				}
			}				
		}
		
		$aimCTEMP=$aimC;
		$goOn=true;
		if($aimL<10){
			for($i=$aimL+1; $i<=10; $i++){
				if($aimCTEMP>1&&$goOn){
					$aimCTEMP--;
					if(($squares[getP($i, $aimCTEMP)-1]==22||$squares[getP($i, $aimCTEMP)-1]==2)&&$squares[$from-1]==22)
						$goOn=false;
					if(($squares[getP($i, $aimCTEMP)-1]==11||$squares[getP($i, $aimCTEMP)-1]==1)&&$squares[$from-1]==11)
						$goOn=false;
					if($goOn){
						$ePoss=$doc->createElement('squares', getP($i, $aimCTEMP));
						$moves->appendChild($ePoss);
					}
				}
			}
		}
		
		$aimCTEMP=$aimC;
		$goOn=true;
		if($aimCTEMP<10){
			for($i=$aimL+1; $i<=10; $i++){
				if($aimCTEMP<10&&$goOn){
					$aimCTEMP++;
					if(($squares[getP($i, $aimCTEMP)-1]==22||$squares[getP($i, $aimCTEMP)-1]==2)&&$squares[$from-1]==22)
						$goOn=false;
					if(($squares[getP($i, $aimCTEMP)-1]==11||$squares[getP($i, $aimCTEMP)-1]==1)&&$squares[$from-1]==11)
						$goOn=false;
					if($goOn){
						$ePoss=$doc->createElement('squares', getP($i, $aimCTEMP));
						$moves->appendChild($ePoss);
					}
				}
			}
		}
		return $moves;
	}
/*
*HITDAME ATE?
*
*/
	function hitdameAte($from, $to, $doc){
		$posFrom=getIJ($from+1);
		$posTo = getIJ($to+1);
		$fromL = $posFrom[0];
		$fromC = $posFrom[1];
		$toL = $posTo[0];
		$toC = $posTo[1];
		$docS = simplexml_import_dom($doc);
		$squares = $docS->{"square"};
		$movesFrom = $squares[$from]->{"posMoves"}->{"squares"};
		$goOn = true;
		$pwn = $squares[$from];
		if($pwn==22){
			$toEat1=11;
			$toEat2=1;
		}
		else{
			$toEat1=22;
			$toEat2=2;
		}
			
		if($squares[$to]==0){
			foreach($movesFrom as $p){
					$toEat = getIJ($p-1);
					$toEatL = $toEat[0];
					$toEatC = $toEat[1];
					if($toEatL<$fromL&&$toEatL>$toL){
						if($toC<$fromC){
							if($toEatC>=$toC&&$toEatC<$fromC){
								if((($squares[$p-1]==1||$squares[$p-1]==11)&&$squares[$from]==22)||($squares[$p-1]==2||$squares[$p-1]==22)&&$squares[$from]==11)
									$squares[$p-1]=0;
							}
						}
						else{
							if($toEatC<=$toC&&$toEatC>=$fromC){
								if((($squares[$p-1]==1||$squares[$p-1]==11)&&$squares[$from]==22)||($squares[$p-1]==2||$squares[$p-1]==22)&&$squares[$from]==11)
									$squares[$p-1]=0;
							}
						}
					}
					elseif($toEatL>$fromL&&$toEatL<$toL){
						if($toC<$fromC){
							if($toEatC>=$toC&&$toEatC<$fromC){
								if((($squares[$p-1]==1||$squares[$p-1]==11)&&$squares[$from]==22)||($squares[$p-1]==2||$squares[$p-1]==22)&&$squares[$from]==11)
									$squares[$p-1]=0;
							}
						}
						else{
							if($toEatC<=$toC&&$toEatC>=$fromC){
								if((($squares[$p-1]==1||$squares[$p-1]==11)&&$squares[$from]==22)||($squares[$p-1]==2||$squares[$p-1]==22)&&$squares[$from]==11)
									$squares[$p-1]=0;
							}
						}
					}
					if($squares[$from]==11){
						$squares[$to]=11;
					}
					elseif($squares[$from]==22){
						$squares[$to]=22;
					}
			}
			$squares[$to]=$squares[$from];
			$squares[$from]=0;
		}
	}
/*
*GET P FROM I, J
*	-> RETURN P;
*/
	function getP($i, $j){
		$p=1;
		if($j==1)
			$p=($i*10)-9;
		else if($i>=1&&$j>=1)
			$p=($i-1)*10+$j;
		else if($j==10)
			$p=$i*10;
		return $p;
	}	
/*
*PRINT BOARD
*/
	function dspBoard($board){
		$p=0;
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
				echo $board[$i][$j];
				$p++;
			}
		}
	}	
	?>
