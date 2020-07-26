<?php

require_once('conexion.php');
require_once('clases.php');

	if ($_GET['tipo']=="Arm") {
		$json = array();
		$consulta=plantel::consultar($_GET['tipo'],$_GET['central']);
		$json['plantel']=$consulta;
		echo json_encode($json);
	}

	if ($_GET['tipo']=="Rig") {
		$json = array();
		$consulta=plantel::consultar($_GET['tipo'],$_GET['central']);
		$json['plantel']=$consulta;
		echo json_encode($json);
	}

	if ($_GET['tipo']=="Ter") {
		$json = array();
		$consulta=plantel::consultarDe($_GET['tipo'],$_GET['central'],$_GET['de']);
		$json['plantel']=$consulta;
		echo json_encode($json);
	}

	if ($_GET['TerTer']=="Ter") {
		$json = array();
		$consulta=plantel::existe($_GET['TerTer'],$_GET['central'],$_GET['nombre'],$_GET['idArm']);
		$json['plantel']=$consulta;
		echo json_encode($json);
	}

	if ($_GET['consultarArm']) {
		$json = array();
		$consulta=plantel::consultarArm($_GET['consultarArm'],$_GET['nombre'],$_GET['central'],$_GET['terminal']);
		$json['plantel']=$consulta;
		echo json_encode($json);
	}

	if ($_GET['altura']) {
		$json = array();
		$altura=($_GET['altura']-150);
		$altura2=$_GET['altura']+150;
		$consulta=plantel::consultaDirecciones($_GET['central'],$_GET['calle'],$altura,$altura2);
		$json['plantel']=$consulta;
		echo json_encode($json);
	}