<?php

require_once('conexion.php');
require_once('clases.php');

if(isset($_POST['login']))
{
  $datos = array($_POST['usuario'], $_POST['pass']);
  if(empty($datos) == false)
  {
    if(strpos($datos[0], " ") == false)
    {
      $resultados = usuarios::verificar($datos[0]);
      if(empty($resultados) == false)
      {
        if($datos[1] == $resultados[0]["pass"])
        {
          echo "loginOk";
        }else{echo "passError";}
      }else{echo "userError";}
    }
  }
}

