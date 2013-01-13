<?php
	$board = array();
	if(!isset($_GET['newGame'])&&!isset($_GET['move'])){
		$doc = new DOMDocument();
		initaBoard($board);
		makexmlfromBoard();
	}		
	if(isset($_GET['newGame'])){
		$doc = new DOMDocument();
		initaBoard($board);
		initParty($board);
		makexmlfromBoard();
	}
	if(isset($_GET['move'])){
		$isDame = explode("*", $_GET['move']);
		if($isDame[0]=="")
			$dameStat = true;
		$doc = simplexml_load_file("board.xml");
		$board = loadXML($doc);
		$move = $_GET['move'];
		list($from, $to)= explode("-", $move);
		checkMove($doc, $from-1, $to-1);
		$square=$doc->{"square"};
		makexmlfromBoard();
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
						$board[$i][$j]=22;
					}
						
				}
			}
		}
	}
/*
*LOAD XML NODES->BOARD FROM FILE
*/	
	function loadXML($doc){
		$root = $doc->{"square"};
		$p=0;
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
				$board[$i][$j] = $root[$p];
				$p++;
			}
		}
		return $board;
	}	
/*
	*XMLIZE BOARD[10][10]
	*/
	function makexmlfromBoard(){
		global $board;
		global $dameStat;
		$doc = new DOMDocument();
		$p=1;
		$doc->version='1.0';
		$doc->encoding='UTF-8';
		$eBoard=$doc->createElement('board');
		$doc->appendChild($eBoard);
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
				}
				if($val==2){
					$eMoves=$doc->createElement('posMoves');
					$ePoss=$doc->createElement('squares', $p-11);
					$eMoves->appendChild($ePoss);
					$ePoss=$doc->createElement('squares', $p-9);
					$eMoves->appendChild($ePoss);
					$esquare->appendChild($eMoves);	
				}
				
				if($val==22){
					$eMoves = calculatespecialMove($p, $doc);
					$esquare->appendChild($eMoves);
				}
				$esquare->setAttribute('id', $p);
				$eBoard->appendChild($esquare);
				$p++;
			}
		}
	$doc->formatOutput = true;
	echo $doc->saveXML();
	$doc->save("board.xml");
	}
/*
*SET NEW [I][J] VAL
*	-> BOARD, I, J, VAL
*/
	function setVal($board, $i,$j, $val){
		$board[$i][$j]=$val;
		return $board;
	}
/*
*GET I, J FROM XML NODE IND
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
*	-> SETVAL(BOARD, I, J, VAL)s
*/
	function checkMove($doc, $from, $to){
		global $board;
	
		$square = $doc->{'square'};
		$fromB=getIJ($from);
		$toB=getIJ($to);
		$pwn = $square[(int)$from];
		if($pwn==2){
			$square[(int)$from]=0;
			if($square[(int)$to]==1){	
				if($from==($to+9)){
					if($square[(int)$to-9]==0){
						$square[(int)$to]=0;
						$square[(int)$to-9]=2;
					}
				}
				else{
					if($square[(int)$to-11]==0){
						$square[(int)$to]=0;
						$square[(int)$to-11]=2;
					}
				}
			}
			else $square[(int)$to]=2;
		}
		
		if($pwn==22){
			hitdameAte($from, $to, $doc);
		}
		if($pwn==1){		
			$square[(int)$from]=0;
			if($square[(int)$to]==2){
				$square[(int)$to]=0;
				if($from==($to-9))
					$square[(int)$to+9]=1;
				else 
					$square[(int)$to+11]=1;
			}
			else $square[(int)$to]=1;
		}
	}
/*
*IF ISDAME-> CHECK DIAGS FOR PWNS TO EAT
	$k=($aimC+$aimL)-1;
	$o=$aimC-$aimL;
	$board[$i][$k]=8;
	$board[$i][$j+$o]=8;
*/	 	
	function calculatespecialMove($from, $doc){
		$fromB=getIJ($from);
		$aimL=$fromB[0];
		$aimC= $fromB[1];
		$moves=$doc->createElement('posMoves');
		$aimCTEMP=$aimC;
	
		if($aimCTEMP<10){
			for($i=$aimL-1; $i>=1; $i--){
				if($aimCTEMP<10){
					$aimCTEMP++;
					$ePoss=$doc->createElement('squares', getP($i, $aimCTEMP));
					$moves->appendChild($ePoss);
					
				}
			}
		}
		
		$aimCTEMP=$aimC;
		if($aimCTEMP>1){
			for($i=$aimL-1; $i>=1; $i--){
				if($aimCTEMP>1){
					$aimCTEMP--;
					$ePoss=$doc->createElement('squares', getP($i, $aimCTEMP));
					$moves->appendChild($ePoss);
				}				
			}
		}
		
		$aimCTEMP=$aimC;
		if($aimL<10){
			for($i=$aimL+1; $i<=10; $i++){
				if($aimCTEMP>1){
					$aimCTEMP--;
					$ePoss=$doc->createElement('squares', getP($i, $aimCTEMP));
					$moves->appendChild($ePoss);
					
				}
			}
		}
		
		$aimCTEMP=$aimC;
		if($aimCTEMP<10){
			for($i=$aimL+1; $i<=10; $i++){
				if($aimCTEMP<10){
					$aimCTEMP++;
					$ePoss=$doc->createElement('squares', getP($i, $aimCTEMP));
					$moves->appendChild($ePoss);
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
		$squares = $doc->{"square"};
		$movesFrom = $squares[$from]->{"posMoves"}->{"squares"};
		//echo "Dame: $from, -> $squares[$from], Col -> $fromC, Lig -> $fromL";
		if($squares[$to]==0){
			foreach($movesFrom as $p){
				$toEat = getIJ($p-1);
				$toEatL = $toEat[0];
				$toEatC = $toEat[1];
				if($toEatL<$fromL&&$toEatL>$toL){
					if($toC<=$fromC){
						if($toEatC>=$toC&&$toEatC<=$fromC){
							if($squares[$p-1]==1)
								$squares[$p-1]=0;
						}
					}
					else{
						if($toEatC<=$toC&&$toEatC>=$fromC){
							if($squares[$p-1]==1)
								$squares[$p-1]=0;
						}
					}
				}
				elseif($toEatL>=$fromL&&$toEatL<=$toL){
					if($toC<=$fromC){
						if($toEatC>=$toC&&$toEatC<=$fromC){
							if($squares[$p-1]==1)
								$squares[$p-1]=0;
						}
					}
					else{
						if($toEatC<=$toC&&$toEatC>=$fromC){
							if($squares[$p-1]==1)
								$squares[$p-1]=0;
						}
					}
				}
				}
			}
			$squares[$from]=0;
			$squares[$to]=22;
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
		echo "<center>";
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
				echo $board[$i][$j];
				echo "\t";
				echo "\t";
				$p++;
			}
			echo "<br/>";
		}
		echo "</center>";
	}	
	?>
