<?php

require_once('conexion.php');
require_once('clases.php');

	$nombre = $_POST["nombre"];
	$imagen = $_POST["img"];
	//$fecha = date("Y-m-dH:i:s");
	$fecha = date("d-m-Y-H:i:s");
	$random = rand();


	if ($_POST['imgCarga']=="0") {
		$url = $_POST['img'];
	}else{
	$path = "../revisador/img/ter/$nombre.$fecha.$random.jpg";
	$url = "http://sd-1488658-h00002.ferozo.net/revisador/img/ter/".$nombre.".".$fecha.".".$random.".jpg";
	file_put_contents($path,base64_decode($imagen));
	$bytesArchivo=file_get_contents($path);
	}


	plantel::recargar($_POST['tipo'],$_POST['tecnico'],$_POST['nombre'],$_POST['direccion'],$_POST['altura'],$url,$_POST['comentario'],$_POST['categoria'],$_POST['idArm'],$_POST['valor'],$_POST['distancia'],$fecha);

		echo "registra";