package com.ddbs.cloudnote.util;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.ddbs.cloudnote.config.ApplicationConfig;


public class WebUtil {
	public static String getResponceByPost(String url,List<NameValuePair> params) throws Exception{
		params.add(new BasicNameValuePair("username",ApplicationConfig.username));
		params.add(new BasicNameValuePair("password", ApplicationConfig.password));
		params.add(new BasicNameValuePair("user_id",""+ ApplicationConfig.userId));
		
		HttpParams httpParams = new BasicHttpParams(); 
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000); //设置连接超时
		HttpConnectionParams.setSoTimeout(httpParams, 10000); //设置请求超时
		
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost httpPost = new HttpPost(url);
		
		UrlEncodedFormEntity entity =  new UrlEncodedFormEntity(params, "utf-8");
		httpPost.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPost);
		if(response.getStatusLine().getStatusCode()==200){
			String resStr =  EntityUtils.toString(response.getEntity(), "utf-8");
			return resStr;
		}else{
			throw new Exception();
		}
	}
}
