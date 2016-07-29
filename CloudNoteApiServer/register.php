<?php



/**
 * Register Interface
 * @author OUYANG
 * 
 */
include_once './Class/Mysql.class.php';
include_once './Class/Response.class.php';


$db_provider = MysqlDB::getInstance();
$id = -1;
if(isset($_POST['username']) && isset($_POST['password'])){
	$db_rows = $db_provider->select('user', array(
			
	), array(
			'username' => $_POST['username'],
			'password' => md5($_POST['password'])
	));
	
	if(empty($db_rows)){
		$id = $db_provider->insert('user', array(
				'username' => $_POST['username'],
				'password' => md5($_POST['password'])
		));
		
		$db_result['id'] = $id;
		
		
		if($id != -1){
			Response::show(1, '注册成功', $db_result);
		} else{
			Response::show(-1, '注册失败');
		}
	}else {
		Response::show(-1, '该账户已经存在，请直接登陆');
	}
	
}
else{
	echo 'null[not by POST]';
}










