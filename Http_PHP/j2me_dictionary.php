<?php require("config.php"); ?>
<?php
    // j2me_dictionary - 1.0.0 - 28.11.2006
	if($_GET['word'] == "") {
		echo "0\n";
		exit;
	}

	if(IsSet($_GET['max_results'])) $max_results = $_GET['max_results'];
	else $max_results = 20;

	// class Dictionary()
	require("classes/class_dictionary.php");
	$dictionary = new Dictionary();

	//DB info
	$dictionary->dbServer = $dbServer;
	$dictionary->dbUsername = $dbUsername;
	$dictionary->dbPassword = $dbPassword;
	$dictionary->dbDatabase = $dbDatabase;
	$dictionary->dbTable = $dbTable;
	$dictionary->dbColumn1 = "word_eng";
	$dictionary->dbColumn2 = "word_cze";
	$dictionary->dbLimit = 100;

	// Output codepage
	$dictionary->outputCodepage = "utf-8";

	// Search word
	$dictionary->word = $_GET['word'];

	// Trasnlate direction
	if($_GET['direction'] == 1) $dictionary->translateDirection = 1;
	else if($_GET['direction'] == 2) $dictionary->translateDirection = 2;
	else $dictionary->translateDirection = 0;

	// Search similar words
	if($_GET['similar'] == 1) $dictionary->searchSimilarWords = true;
	else $dictionary->searchSimilarWords = false;

	// Use logic operators
	if($_GET['logical'] == 1) {
		$dictionary->logicTranslating = true;
		$dictionary->searchSimilarWords = false;
	} else {
		$dictionary->logicTranslating = false;
	}

	// Connect to DB
	$dictionary->connectDB();

	// Searching word
	if($dictionary->searchWord()) {
		if($dictionary->error != 0) { // Check for error
			echo "0\n";
			// Close DB
			$dictionary->closeDB();
			exit;
		}

		if($dictionary->translatingDirection == 1) { // eng to cze
			echo "1\n";
			$found = $dictionary->countColumn1;
		} else if($dictionary->translatingDirection == 2) { // cze to eng
			echo "2\n";
			$found = $dictionary->countColumn2;
		}

		$count = 0;
		if($found > 0) { // Some translate was found
			// Write next word
			while($dictionary->nextWord()) {
				if($count == $max_results) break;

				if((StrLen($dictionary->original) > 30) || (StrLen($dictionary->translate) > 30)) continue;
				else {
					$count++;
					echo $dictionary->original."\n".$dictionary->translate."\n";
				}
			}
		}
	} else echo "0\n"; // error

	// Close DB
	$dictionary->closeDB();
?>