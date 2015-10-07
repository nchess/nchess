<?php
	function ParseParams($argv)
	{
		$params = Array(); 
		$values = Array();
		
		for($i=1; $i < count($argv); $i++)
		{
			if(substr($argv[$i], 0, 1) == '-')
			{
				$key = trim($argv[$i], "- \t\n\r\0\x0B");
				$value = $argv[$i+1];
				$params[$key] = $value;
				$i++;
			}
			else
			{
				$values[] = $argv[$i];
			}
		}
		
		return Array("params" => $params, "values" => $values); 
	}
	
	//Thanks, Gumbo!
	//http://stackoverflow.com/questions/1241177/c-sharp-string-format-equivalent-in-php
	function format() 
	{
		$args = func_get_args();
		if (count($args) == 0) {
			return;
		}
		if (count($args) == 1) {
			return $args[0];
		}
		$str = array_shift($args);
		$str = preg_replace_callback('/\\{(0|[1-9]\\d*)\\}/', 
				create_function('$match', '$args = '.var_export($args, true).'; return isset($args[$match[1]]) ? $args[$match[1]] : $match[0];'), $str);
		return $str;
	}
?>