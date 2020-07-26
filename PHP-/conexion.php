<?PHP

	function conexion(){
		try {
			$con = new PDO('mysql:host=localhost;dbname=yamilapp_revi', 'yamilapp_user', '155833265Y4m1l' );
			$con->exec('set names utf8');
			return $con;
		} catch (PDOException $e) {
			return $e->getMessage();
		}
	}
	
?>