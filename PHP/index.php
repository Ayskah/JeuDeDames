<?php
	if(!isset($_GET['newGame'])||!isset($_GET['move'])){
		$board=initaBoard();
	}
	if(isset($_GET['newGame'])){
		$board=initParty();
	}
	if(isset($_GET['move'])){
		$board=load("board.xml");
		$move = $_GET['move'];
		list($from, $dest)= explode("-", $move);
		checkMove();
	}
	makexmlfromBoard($board);
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
*	- SET GAME	INIT STATE
*	- RETURN NEW board[10][10]
*/
	function initParty(){
	//INIT BLACKS
		for($i=1;$i<=10;$i++){
			for($j=1;$j<=10;$j++){
				if($i<5){
					if(($i%2==0&&$j%2!=0)||$i%2!=0&&$j%2==0){
						$board[$i][$j]=1;
					}
					else{$board[$i][$j]=0;}
				}
	//INIT MID	
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
*XMLIZE BOARD[10][10]
*/
	function makexmlfromBoard($board){
		$p=0;
		$doc = new DOMDocument();
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
		echo $doc->saveXML();
		$doc->save("board.xml");
	}

/*
*CHECK IF PAWN EATING
*	-> FROM, TO
*	-> RETURN NEW board[10][10]
*/
/*funtion checkMove($from, $dest){
	$pwn=$board[$from];
		if($dest==($pwn==1)? 2:1)
			echo "Mangé";
}*/
?>
		