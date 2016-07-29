<?php
/**
 * 更新日记接口
 * @author OUYANG
 */

include_once './Class/Mysql.class.php';
include_once './Class/Response.class.php';

$db_provider = MysqlDB::getInstance();
$affected_rows = -1;

if(isset($_POST['username']) && isset($_POST['password']) && isset($_POST['note_id'])) {
	$db_user_result = $db_provider->select('user', array(), array(
			'username' => $_POST['username'],
			'password' => md5($_POST['password'])
	));
	
	if(empty($db_user_result)) {
		Response::show(-1, '登陆失败，用户名或密码错误');
		die('Edit Diary failed');
	}
	
	$title= isset($_POST['title'])?$_POST['title']:"";
	$content = isset($_POST['content'])?$_POST['content']:"";
	$time = date('Y-m-d H:i:s', time());
	
	$affected_rows = $db_provider->update('note', array(
			'title' => $title,
			'content' => $content,
			'time' => $time
	), array(
			'id' => $_POST['note_id']
	));
	
	
	$affected_result[] = $affected_rows;
	
	if($affected_rows > 0){
		Response::show(1, '更新日记成功', $affected_result);
	}else{
		if($affected_rows == 0) {
			Response::show(-1, '更新日记失败，数据库中不存在这样的数据');
		}else if($affected_rows == -1)
			Response::show(-1, '更新日记失败');
	}
	
	
	
	
	
	
}else{
	if(!isset($_POST['note_id']))
		Response::show(-1, '请附加上您要删除的note_id');
	else
		echo 'NULL[NOT BY POST]' . '<br/>';
}