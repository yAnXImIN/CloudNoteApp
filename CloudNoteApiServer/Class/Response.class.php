<?php


/**
 * 返回响应数据[JSON数据格式]
 * @author OUYANG
 */




class Response {
	const JSON = "json";
	const XML  = "xml";
	
	public static function show($messageId, $messageInfo='', $responseData=array()){
		
		// 默认返回JSON格式的数据
		$type = isset($_POST['format']) ? $_POST['format'] : self::JSON;
		switch ($type){
			case 'xml':
				
				break;
			case 'json':
				
				self::toJson($messageId, $messageInfo, $responseData);
				break;
			default:
				// TODO
				
				break;
		
		}
		
	}
	
	
	public static function toJson($messageId, $messageInfo='', $responseData=array()){
		
		
		$result = array(
				'messageId' => $messageId,
				'messageInfo' => $messageInfo,
				'responseData' => $responseData
		);
		
		
		// 返回JSON数据
		echo json_encode($result);
		exit;
		
	}
	
}





