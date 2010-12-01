<?php
	header("Content-type: text/plain; charset=UTF-8");
	header("Content-encoding: UTF-8");
?>
<?php
	require "./j2me_dictionary.php?".$_SERVER['QUERY_STRING'];
?>