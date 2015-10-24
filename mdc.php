<?php
	require('parsedown/Parsedown.php');
	require('common.php'); 
	
	//=========================================================================================
	//Validate parameters
	$args = ParseParams($argv);
	
	//Template ( HTML frame ) 
	if(isset($args['params']['template']))
		$templateString = file_get_contents($args['params']['template']); 
	else if(isset($args['params']['t']))
		$templateString = file_get_contents($args['params']['t']); 
	else
		$templateString = "<html>\n<head>\n</head>\n<body>\n{0}\n</body>\n</html>";
	
	//Output directory
	if(isset($args['params']['o']))
		$outputDir = $args['params']['o'];
	else if(isset($args['params']['out']))
		$outputDir = $args['params']['out'];
	else 
		$outputDir = '';

	//Gather input files 
	$inputFiles = Array(); 
	foreach($args["values"] as $f)
		$inputFiles = array_merge($inputFiles, glob($f));
		
	//=========================================================================================
	//Do work 
	
	//Create output directory if needed 
	if($outputDir != '')
	{
		if(!is_dir($outputDir))
		{
			if(!mkdir($outputDir, 0x777, true))
			{
				echo "Failed to create output directory: $outputDir"; 
				return -1;
			}
		}
		
		if(substr($outputDir, -1) != '/' && substr($outputDir, -1) != "\\")
			$outputDir .= "/";
	}
	
	$md = new Parsedown();
	foreach($inputFiles as $fname)
	{
		echo "Parsing $fname... \n";
		
		$ofname = $outputDir . basename($fname);
		$ofname = substr($ofname, 0, strrpos($ofname, '.'));
		$ofname .= ".html";
		
		$text = file_get_contents($fname); 
		$text = $md->text($text);
		$text = format($templateString, $text); 
		file_put_contents($ofname, $text); 
	}
?>