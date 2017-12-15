<?php

if($_SERVER["REQUEST_METHOD"]=="POST")
{
	require 'connection.php';
	createProduct();
}

function createProduct()
{
	global $connect;
	
	$nome = $_POST["nome"];
	$valor = $_POST["valor"];
	$latitude = $_POST["latitude"];
	$longitude = $_POST["longitude"];
	
	$query = " Insert into Products(nome, valor, latitude, longitude) values ('$nome', '$valor', '$latitude','$longitude');";
	
	mysqli_query($connect, $query) or die (mysqli_error($connect));
	mysqli_close($connect);
	
}


?>
