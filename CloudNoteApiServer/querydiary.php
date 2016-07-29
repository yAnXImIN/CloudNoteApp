<?php


/**
 * 查询某个用户的所有日记列表
 * @author OUYANG
 * 
 */

include_once './Class/Mysql.class.php';
include_once './Class/Response.class.php';

$db_provider = MysqlDB::getInstance();


if(isset($_POST['username']) && isset($_POST['password']) && isset($_POST['user_id'])){
	
	$db_user_result = $db_provider->select('user', array(), array(
			'username' => $_POST['username'],
			'password' => md5($_POST['password'])
	));
	
	if(empty($db_user_result)) {
		Response::show(-1, '登陆失败，用户名或密码错误');
		die('Edit Diary failed');
	}
	
	$db_data_result = $db_provider->select('note', array(), array(
			'user_id' => $_POST['user_id']
	));
	
	
	
	Response::show(1, '查询成功', $db_data_result);
	
	
	
} else {
	if(!isset($_POST['user_id']))
		echo '请附加上您的id' . '<br/>';
	else
		echo 'NULL[NOT BY POST]' . '<br/>';

}