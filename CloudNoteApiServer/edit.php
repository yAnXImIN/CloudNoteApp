<?php


/**
 * Edit Interface
 * @author OUYANG
 * 
 */



include_once './Class/Mysql.class.php';
include_once './Class/Response.class.php';

$db_provider = MysqlDB::getInstance();
$id = -1;
if(isset($_POST['username']) && $_POST['password'] && $_POST['user_id']){
	$db_result = $db_provider->select('user', array(), array(
			'username' => $_POST['username'],
			'password' => md5($_POST['password'])
	));
	
	if(empty($db_result)) {
		Response::show(-1, '登陆失败，用户名或密码错误');
		die('Edit Diary failed');
	}
	
	$user_id = $_POST['user_id'];
	$title = isset($_POST['title'])? $_POST['title'] : '';
	$content = isset($_POST['content'])?$_POST['content'] : '';
	$timestamp = date('Y-m-d H:i:s', time());
	
	$id = $db_provider->insert('note', array(
			'user_id' => $user_id,
			'title' =>$title,
			'content' => $content,
			'time' => $timestamp
	));
	
	$db_result_id['id'] = $id;
	
	if($id != -1){
		Response::show(1, "提交日记成功", $db_result_id);
	}else {
		Response::show(-1, "提交日记失败");
	}
	
	
	
}else {
	if(!isset($_POST['user_id']))
		echo '请附加上您的id' . '<br/>';
	else
		echo 'NULL[NOT BY POST]' . '<br/>';

}




