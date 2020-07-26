<?php


#USUARIOS#

class usuarios{

	function verificar($legajo){
		$con = conexion();
		$consulta = $con->prepare("select * from User where legajo = :legajo");
		$consulta->execute(array(':legajo' => $legajo));
		$resultado = $consulta->fetchAll();
		return $resultado;
	}

}

#FAVORITOS#

class favoritos{

	function agregar($tecnico,$idArm,$idTer,$tipo){
	$con = conexion();
	$consulta = $con->prepare("insert into Fav(tecnico,idArm,idTer,Tipo) values('$tecnico','$idArm','$idTer','$tipo')");
	$consulta->execute();
	}

	function consultar($tecnico,$tipo){
	$con = conexion();
	$consulta = $con->prepare("select * from Fav where tecnico='$tecnico' and tipo='$tipo' order by $tipo asc");
	$consulta->execute();
	$resultado=$consulta->fetchAll();
	return $resultado;
	}

	function consultaMasiva($id,$tipo,$central){
	$con = conexion();
	$consulta = $con->prepare("select * from $tipo where nombre in ($id) and central='$central' order by nombre asc");
	$consulta->execute();
	$resultado = $consulta->fetchAll();
	return $resultado;
	}


}

#PLANTEL#

class plantel{

	function existe($tipo,$central,$nombre,$idArm){
	$con = conexion();
	$consulta = $con->prepare("select * from $tipo where central='$central' and nombre='$nombre' and idArm='$idArm'");
	$consulta->execute();
	$resultado=$consulta->fetchAll();
	return $resultado;
	}

	function registrar($tipo,$tecnico,$nombreTer,$direccion,$altura,$img,$comentario,$categoria,$idArm,$valor,$central,$distancia,$fecha){
	$con = conexion();
	$consulta = $con->prepare("insert into $tipo(tecnico,nombre,direccion,altura,img,imgdos,comentario,categoria,idArm,valor,central,distancia,fecha) values('$tecnico','$nombreTer','$direccion','$altura','$img',null,'$comentario','$categoria','$idArm','$valor','$central','$distancia','$fecha')");
	$consulta->execute();
	}

	function recargar($tipo,$tecnico,$nombreTer,$direccion,$altura,$img,$comentario,$categoria,$idArm,$valor,$distancia,$fecha){
	$con = conexion();
	$consulta = $con->prepare("update $tipo set `tecnico`='$tecnico',`direccion`='$direccion',`altura`='$altura',`img`='$img',`comentario`='$comentario',`categoria`='$categoria',`valor`='$valor',`distancia`='$distancia',`fecha`='$fecha' where nombre='$nombreTer' and idArm='$idArm'");
	$consulta->execute();
	}

	function consultar($tipo,$central){
	$con = conexion();
	$consulta = $con->prepare("select * from $tipo where central='$central' order by nombre asc");
	$consulta->execute();
	$resultado=$consulta->fetchAll();
	return $resultado;
	}

	function consultarArm($tipo,$nombre,$central,$idArm){
	$con = conexion();
	$consulta = $con->prepare("select * from $tipo where nombre='$nombre' and central='$central' and idArm='$idArm' order by nombre asc");
	$consulta->execute();
	$resultado=$consulta->fetchAll();
	return $resultado;
	}

	function consultarDe($tipo,$central,$de){
	$con = conexion();
	$consulta = $con->prepare("select * from $tipo where central='$central' and idArm='$de' order by nombre asc");
	$consulta->execute();
	$resultado=$consulta->fetchAll();
	return $resultado;
	}

	function consultaDirecciones($central,$calle,$altura,$altura2){
	$con = conexion();
	$consulta = $con->prepare("select * from Ter where central='$central' and direccion like '%$calle%' and altura between '$altura' and '$altura2'");
	$consulta->execute();
	$resultado=$consulta->fetchAll();
	return $resultado;
	}

}

?>