<?php

require_once('conexion.php');
require_once('clases.php');

	if ($_POST['tipo']=="Ter") {
		if(empty($consulta=plantel::existe($_POST['tipo'],$_POST['central'],$_POST['nombre'],$_POST['idArm']))){
			echo "sinDatos";
		}else{
			echo "Existe";
		}
	}


	//plantel::registrar($_GET['tipo'],$_GET['tecnico'],$_GET['nombre'],$_GET['direccion'],$_GET['altura'],$_GET['img'],$_GET['comentario'],$_GET['categoria'],$_GET['idArm'],$_GET['valor'],$_GET['central']);