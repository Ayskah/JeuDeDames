<?php
$board = array();
	if(!isset($_GET['newGame'])&&!(isset($_GET['move']))){
		$doc = new DOMDocument();
		$board = initaBoard();
		makexmlfromBoard($board);
	}	
	if(isset($_GET['newGame'])){
		$doc = new DOMDocument();
		$board = initParty();
		makexmlfromBoard($board);
	}
	if(isset($_GET['move'])){
		$doc = simplexml_load_file("board.xml");
		$board = loadXML($doc);
		$move = $_GET['move'];
		list($from, $to)= explode("-", $move);
		checkMove($doc, $from-1, $to-1);
		makexmlfromBoard($board);
	}

/*
*INIT A BOARD:
*	- SET 0 STATE
*	- RETURN NEW board[10][10]
*/	
	function initaBoard(){
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
				$board[$i][$j]=0;
			}
		}
		return $board;
	}
/*
*INIT WHEN "newGame":
*	- SET GAMEINIT STATE
*	- RETURN NEW board[10][10]
*/
	function initParty(){
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
/*INIT BLACKS*/
				if($i<5){
					if(($i%2==0&&$j%2!=0)||$i%2!=0&&$j%2==0){
						$board[$i][$j]=1;
					}
					else{$board[$i][$j]=0;}
				}
/*INIT MID*/
				elseif($i>=5&&$i<=6){
					$board[$i][$j]=0;
				}	
/*INIT WHITES
- TAKE A SIDE, REVERSE IT, YOU'RE DONE!
*/
				elseif($i>6){
					$aim= (10-$i)+1;
					if($board[$aim][$j]==0){
						$board[$i][$j]=2;
					}
					else $board[$i][$j]=0;
				}
			}
		}
		return $board;
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
			echo "<br/>";
		}
	}	
/*
*LOAD XML NODES->BOARD FROM FILE
*/	
	function loadXML($doc){
		$root = $doc->case;
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
	function makexmlfromBoard($board){
		$doc = new DOMDocument();
		
		$p=0;
		$doc->version='1.0';
		$doc->encoding='UTF-8';
		$eBoard=$doc->createElement('board');
		$doc->appendChild($eBoard);
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
				$p++;
				$val= $board[$i][$j];
				$eCase=$doc->createElement('case', $val);
				if($val==1){
					$eMoves=$doc->createElement('posMoves');
					$ePoss=$doc->createElement('cases', $p+11);
					$eMoves->appendChild($ePoss);
					$ePoss=$doc->createElement('cases', $p+9);
					$eMoves->appendChild($ePoss);
					$eCase->appendChild($eMoves);
				}
				if($val==2){
					$eMoves=$doc->createElement('posMoves');
					$ePoss=$doc->createElement('cases', $p-11);
					$eMoves->appendChild($ePoss);
					$ePoss=$doc->createElement('cases', $p-9);
					$eMoves->appendChild($ePoss);
					$eCase->appendChild($eMoves);	
				}
				$eCase->setAttribute('id', $p);
				$eBoard->appendChild($eCase);
			}
		}


	$doc->formatOutput = true;
	$doc->save("board.xml");
	}	
/*
*CHECK IF PAWN EATING
*	-> FROM, TO, DOC
*	-> SETVAL(BOARD, I, J, VAL)
*/
	function checkMove($doc, $from, $to){
	global $board;
		$root = $doc->{'case'};
		$fromB=getIJ($from);
		$toB=getIJ($to);
		$pwn = $root[(int)$from];
		if($pwn==2){
			if($root[(int)$to]==1){
				$board=setVal($board, $fromB[0], $fromB[1], 0);
				$board=setVal($board, $toB[0], $toB[1], $pwn);
			}
			else if($root[(int)$to]==0){
				echo $board[$toB[0]][$toB[1]];
				$board=setVal($board, $fromB[0], $fromB[1], 0);
				$board=setVal($board, $toB[0], $toB[1], $pwn);
			}	
		}
	}
/*
*SET NEW [I][J] VAL
*	-> BOARD, I, J, VAL
*/
	function setVal(&$board, $i,$j, $val){
		$board[$i][$j]=$val;
		return $board;
	}
/*
*GET I, J FROM XML NODE IND
*	-> RETURN (i,j)
*/	
	function getIJ($ind){
	global $board;
		$p=0;
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){			
				if($p==$ind){
					return array($i, $j);
				}
				$p++;
			}
		}
	}
?>