<?php

require_once('conexion.php');
require_once('clases.php');

	if ($_GET['tecnico']) {
		$consulta=favoritos::consultar($_GET['tecnico'],$_GET['tipo']);
		foreach ($consulta as  $value){
    	$id =  $id ."," .$value[$_GET['tipo']];
    	$id = trim($id, ",");
    	}

    	if ($_GET['tipo']=="idArm") {$tipo = "Arm";}
    	if ($_GET['tipo']=="idTer") {$tipo = "Ter";}
    	if ($_GET['tipo']=="idRig") {$tipo = "Rig";}

		$consulta2=favoritos::consultaMasiva($id,$tipo,"ARJ2");
		$json = array();
		$json['favoritos']=$consulta2;
		echo json_encode($json);


		//echo "select * from ".$tipo." where nombre in (".$id.") and central='ARJ2'";
	
	}

	if ($_POST['agregar']) {
		$consulta=favoritos::agregar($_POST['agregar'],$_POST['idarm'],$_POST['idTer'],$_POST['tipo']); 
		echo "registra";
	}
