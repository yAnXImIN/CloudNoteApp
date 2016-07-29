<?php


//-----------------------------------------------------------------------------------------------------------------
// 单例模式三大原则：
// 1. 构造函数需要标记为非public（防止外部使用new操作符创建对象），单例类不能再其他类中实例化，只能被其自身实例化;
// 2. 拥有一个保存类实例的静态成员变量$_instance;
// 3. 拥有一个访问这个实例的公共的静态方法;
//-----------------------------------------------------------------------------------------------------------------
/**
 * 单例模式完成对数据库的CRUD操作封装
 * 使用面向过程的mysql方式
 * @author OUYANG
 */
ini_set('display_errors', false);

// header('Refresh:0.01');

class MysqlDB {
	private static $_instance = null;
	private $_conn;
	private static $sql;
	
	private function __clone(){
		
	}
	
	
	public static function getInstance(){
		if(is_null(self::$_instance)){
			self::$_instance = new self();
		}
		return self::$_instance;
		
	}
	
	
	private function __construct(){
		global $_dbConfig;
		
		// 这里容易出BUG...
		$_dbConfig = include './Config/db_config.php';
		
		if(!$this->_conn){
			$this->_conn = mysql_connect($_dbConfig['DB_HOST'], $_dbConfig['DB_USER'], $_dbConfig['DB_PASSWORD'], $_dbConfig['DB_PORT']);
			if(!$this->_conn){
				die('[Mysql Connect Error]' . mysql_errno() . mysql_error());
			}
			if(!mysql_select_db($_dbConfig['DB_BASE'],$this->_conn)){
				die('[Mysql Select Database Error]' . mysql_errno() . mysql_error());
				
			}
			mysql_query('set names utf8',$this->_conn);
			
		}
		
	}
	
	
	/**
	 * Retrieve Manipulation
	 * return: query results in array
	 */
	public function select($table, $field=array(), $condition=array()){
		$where = '';
		$fieldStr = '*';
		$resultRows = array();
		if( empty($table)) {
			die('[Customized Error in function:select]' . 'Invalid Arguments :(');
		}
		if(!empty($condition)){
			foreach($condition as $f => $v){
				$where .= "{$f} = '{$v}' and ";
			}
			$where = 'where ' . substr($where, 0, strlen($where) - 4);
			
		}
		
		if(!empty($field)){
			$fieldStr = implode(',', $field);
		}
		
		self::$sql = "SELECT {$fieldStr} FROM {$table} {$where}";
		
		if(!($results = mysql_query(self::$sql, $this->_conn))){
			die('[Mysql Error in function:select]' . mysql_errno() . mysql_error());
		}
		
		
		
		while($row = mysql_fetch_assoc($results)){
			$resultRows[] = $row;
		}
		
		// ... ...
		
		return $resultRows;
		
	}
	
	
	
	/**
	 * Create Manipulation
	 * return: the ID generated in the last query
	 */
	public function insert($table, $data){
		if(empty($data) || empty($table)) {
			die('[Customized Error in function:insert]' . 'Invalid Arguments :(');
		}
		$field = '';
		$value = '';
		
		foreach ($data as $k => $v){
			$field .= $k . ',';
			$value .= "'$v'" . ',';
			
		}
		
		$field = rtrim($field, ',');
		$value = rtrim($value, ',');
		
		self::$sql = "INSERT INTO {$table}({$field}) VALUES ({$value})";
		
		if(!($result = mysql_query(self::$sql, $this->_conn))){
			die('[Mysql Error in function:insert]' . mysql_errno() . mysql_error());
		}
		return mysql_insert_id();
	}
	
	
	/**
	 * Update Manipulation
	 * return: the number of affected rows
	 */
	public function update($table, $data, $condition=array()){
		$where = '';
		$updateField = '';
		
		if(empty($data) || empty($table)) {
			die('[Customized Error in function:insert]' . 'Invalid Arguments :(');
		}
		
		if(!empty($condition)){
			foreach($condition as $f => $v){
				$where .= "{$f} = '{$v}' and ";
			}
			$where = 'where ' . substr($where, 0, strlen($where) - 4);
		}
		
		foreach($data as $f =>$v){
			$updateField .= "{$f}='{$v}',";
		}
		$updateField = rtrim($updateField, ',');
		
		self::$sql = "UPDATE {$table} SET {$updateField} {$where}";
		
		if(!mysql_query(self::$sql, $this->_conn)){
			die('[Mysql Error in function:update]' . mysql_errno() . mysql_error());
		}
		return mysql_affected_rows();
	}
	
	
	/**
	 * Delete Manipulation
	 * return: the number of affected rows
	 */
	public function delete($table, $condition=array()){
		$where = '';
		
		if(empty($table)) {
			die('[Customized Error in function:delete]' . 'Invalid Arguments :(');
		}
		
		if(!empty($condition)){
			foreach($condition as $f => $v){
				$where .= "{$f} = '{$v}' and ";
			}
			$where = 'where ' . substr($where, 0, strlen($where) - 4);
		}
		
		self::$sql = "DELETE FROM {$table} {$where}";
		if(!mysql_query(self::$sql, $this->_conn)){
			die('[Mysql Error in function:delete]' . mysql_errno() . mysql_error());
		}
		
		return mysql_affected_rows();
	}
	
	public static function getLastSQL(){
		return self::$sql;
	}
	
	public function debug($debug, $result_rows){
		echo '<p style="color:red;"><b>-->' . $debug . '</b></p>';
		echo '<pre><p style="font-size:20px;color:#808080;">';
		var_dump ( $result_rows );
		echo '</p></pre>';
		
	}
	
	
}




// ======================================================================================
// test data: 
// ======================================================================================

// $mysql = MysqlDB::getInstance();
// $result_rows = $mysql->select('user', array('id', 'username', 'password'));
// echo '<pre>';
// var_dump($result_rows);

// $mysql->debug(MysqlDB::getLastSQL(), $result_rows);

// $result_rows = $mysql->update('n7_users', array('user_login'=>'OUYANG','user_nicename'=>'tong'),array('user_login'=>'OUYANGTONG'));
// $mysql->debug(MysqlDB::getLastSQL(), $result_rows);

// $result_rows = $mysql->delete('n7_users',array('user_nicename'=>'wtf'));
// $mysql->debug(MysqlDB::getLastSQL(), $result_rows);



// $result_rows = $mysql->insert('user', array('username'=>'admin','password'=>md5('shine')));
// $mysql->debug(MysqlDB::getLastSQL(), $result_rows);

// echo '</pre>';
