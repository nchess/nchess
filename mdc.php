<?php
	require('parsedown/Parsedown.php');
	
	$text = file_get_contents("php://stdin");
	$md = new Parsedown(); 
	echo $md->text($text); 
?>