package com.Daniel.CSSA;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

import com.Daniel.CSSA.GeoUtil.GaussSphere;
import com.baidu.bae.api.util.BaeEnv;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class WeChatReplyNewsMessage {

	private static Logger logger = Logger. getLogger("WeChatReplyNewsMessage");
	private String toUserName;
	private String fromUserName;
	private String createTime;
	private String msgType;
	private int articleCount;
	private List<Item> items;
	private String funcFlag;
	
	
	//--------------------------------------------

		


		
			
	
	//-----------------------------------------------
	
	public static String getWeChatReplyNewsMessageByBaiduPlace(List<BaiduPlaceResponse> placeList, double lat, double lng,String userName, int size){

		WeChatReplyNewsMessage newsMessage = new WeChatReplyNewsMessage();
		List<Item> items = new ArrayList<Item>();
		StringBuffer strBuf = new StringBuffer();
		logger.log(Level.INFO,"placeList count="+placeList.size());
		newsMessage.setItems(items);
		if(placeList.size()>size){
			newsMessage.setArticleCount(size);
		}
		else{
			newsMessage.setArticleCount(placeList.size());
		}
		logger.log(Level.INFO,"article count="+newsMessage.getArticleCount());
		newsMessage.setCreateTime(new Date().getTime()+"");
		newsMessage.setMsgType("news");
		newsMessage.setFuncFlag("0");
		newsMessage.setToUserName(userName);
		newsMessage.setFromUserName(WeChatConstant.FROMUSERNAME);
		for(int i = 0;i <newsMessage.getArticleCount();i++){
			BaiduPlaceResponse place = placeList.get(i);
			Double distance = GeoUtil.DistanceOfTwoPoints(Double.valueOf(place.getLng()), Double.valueOf(place.getLat()), lng, lat, GaussSphere.Beijing54);
			Item item = new Item();
			item.setTitle(place.getName()+"["+distance+"米]"+"\n"+place.getAddress()+"\n"+place.getTelephone());
			item.setPicUrl("");
			item.setUrl(place.getDetailUrl());
			item.setDescription("");
			items.add(item);
		}
		logger.log(Level.INFO,"newMessage="+newsMessage.toString());
		strBuf = strBuf.append(getWeChatNewsMessage(newsMessage));
		
		return strBuf.toString();
	}
	
	
	public static String getWeChatReplyNewsMessageByBaiduPlace2(List<BaiduPlaceResponse> placeList, double lat, double lng,String userName, int size){
		Statement st = null;
		ResultSet set = null;
		String host = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_SQL_IP);
		String port = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_SQL_PORT);
		String username = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_AK);
		String password = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_SK);
		String driverName = "com.mysql.jdbc.Driver";
		String dbUrl = "jdbc:mysql://";
		String serverName = host + ":" + port + "/";
		String Name = null, Start =null, End = null, Address = null, phone = null;
		String databaseName = "FhyuGuhfGBhNDORhvdGB";
		String connName = dbUrl + serverName + databaseName;
		Connection connection = null;
	try{					
		String StatusSql = "SELECT * FROM LIBRARY WHERE lib_name = 'Robarts Library'";
		Class.forName(driverName);
	                //具体的数据库操作逻辑
			connection = DriverManager.getConnection(connName, username,
					password);
			st = connection.createStatement();
			set = st.executeQuery(StatusSql);
			
			if(set.next())
			{
			Name = set.getString("lib_name");
			Start = set.getString("start_time");
			End = set.getString("end_time");
			Address = set.getString("address");
			phone = set.getString("phone");
			}
			
			
			
			
	} catch (SQLException e) {
		logger.log(Level.INFO,e.getMessage());
	} catch (ClassNotFoundException e) {
		logger.log(Level.INFO,e.getMessage());
	}
		
	WeChatReplyNewsMessage newsMessage = new WeChatReplyNewsMessage();
	List<Item> items = new ArrayList<Item>();
	StringBuffer strBuf = new StringBuffer();
	logger.log(Level.INFO,"placeList count="+placeList.size());
	newsMessage.setItems(items);
//	if(placeList.size()>size){
//		newsMessage.setArticleCount(size);
//	}
//	else{
//		newsMessage.setArticleCount(placeList.size());
//	}
	newsMessage.setArticleCount(1);
	logger.log(Level.INFO,"article count="+newsMessage.getArticleCount());
	newsMessage.setCreateTime(new Date().getTime()+"");
	newsMessage.setMsgType("news");
	newsMessage.setFuncFlag("0");
	newsMessage.setToUserName(userName);
	newsMessage.setFromUserName(WeChatConstant.FROMUSERNAME);
	for(int i = 0;i <newsMessage.getArticleCount();i++){
		//BaiduPlaceResponse place = placeList.get(i);
		//Double distance = GeoUtil.DistanceOfTwoPoints(Double.valueOf(place.getLng()), Double.valueOf(place.getLat()), lng, lat, GaussSphere.Beijing54);
		Item item = new Item();
		item.setTitle(Name+"\n"+Address+"\n"+"Today "+"\n"+" Open at: "+Start+"\n"+"Close at: "+End+"\n");
		item.setPicUrl("");
		//item.setUrl(place.getDetailUrl());
		item.setUrl("http://resource.library.utoronto.ca/hours/month.cfm?library_id=109");
		item.setDescription("");
		items.add(item);
	}
	logger.log(Level.INFO,"newMessage="+newsMessage.toString());
	strBuf = strBuf.append(getWeChatNewsMessage(newsMessage));
	
	return strBuf.toString();
	
	

	}
	
	public static String getWeChatReplyNewsMessageByBaiduPlace3(List<BaiduPlaceResponse> placeList,String userName, int size){
		Statement st = null;
		ResultSet set = null;
		String host = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_SQL_IP);
		String port = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_SQL_PORT);
		String username = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_AK);
		String password = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_SK);
		String driverName = "com.mysql.jdbc.Driver";
		String dbUrl = "jdbc:mysql://";
		String serverName = host + ":" + port + "/";

	     //��ƽ̨��ѯӦ��Ҫʹ�õ���ݿ���
		String databaseName = "MuhrRBNBzcVUhLATDnRx";
		String connName = dbUrl + serverName + databaseName;
		Connection connection = null;
		String choice = "fail";
		int point = 1;
//		try{		
//			
//			String StatusSql = "SELECT points FROM selection_menu where userid = 'opZ3ZjquX3c6iRwGfN2FswwRDyr4'";
//			Class.forName(driverName);
//		
//				connection = DriverManager.getConnection(connName, username,
//						password);
//				st = connection.createStatement();
//				set = st.executeQuery(StatusSql);
//				if( (set.next() ) ){		
//					
//					point = set.getInt("points");
//			
//				} 
//		
//			
//		} catch (SQLException e) {
//			logger.log(Level.INFO,e.getMessage());
//		} catch (ClassNotFoundException e) {
//			logger.log(Level.INFO,e.getMessage());
//		}
		
		
	
		
	WeChatReplyNewsMessage newsMessage = new WeChatReplyNewsMessage();
	List<Item> items = new ArrayList<Item>();
	StringBuffer strBuf = new StringBuffer();
	logger.log(Level.INFO,"placeList count="+placeList.size());
	newsMessage.setItems(items);
//	if(placeList.size()>size){
//		newsMessage.setArticleCount(size);
//	}
//	else{
//		newsMessage.setArticleCount(placeList.size());
//	}
	newsMessage.setArticleCount(1);
	logger.log(Level.INFO,"article count="+newsMessage.getArticleCount());
	newsMessage.setCreateTime(new Date().getTime()+"");
	newsMessage.setMsgType("news");
	newsMessage.setFuncFlag("0");
	newsMessage.setToUserName(userName);
	newsMessage.setFromUserName(WeChatConstant.FROMUSERNAME);
	for(int i = 0;i <newsMessage.getArticleCount();i++){
		//BaiduPlaceResponse place = placeList.get(i);
		//Double distance = GeoUtil.DistanceOfTwoPoints(Double.valueOf(place.getLng()), Double.valueOf(place.getLat()), lng, lat, GaussSphere.Beijing54);
		Item item = new Item();
		item.setTitle("您目前一共获得了 "+point+" 枚图章 "+"\n"+"再收集 4 枚就可以获得我们"+"\n"+"的免费奶茶哦");
		item.setPicUrl("http://www.geekpics.net/images/2013/09/15/DS1r9X4k.png");
		//item.setUrl(place.getDetailUrl());
		item.setUrl("");
		item.setDescription("stamp card");
		items.add(item);
	}
	logger.log(Level.INFO,"newMessage="+newsMessage.toString());
	strBuf = strBuf.append(getWeChatNewsMessage(newsMessage));
	
	return strBuf.toString();
	
	

	}
	
	
	
	
	
	
	public static String getNewMessage( String userName,int point){

	WeChatReplyNewsMessage newsMessage = new WeChatReplyNewsMessage();
	List<Item> items = new ArrayList<Item>();
	StringBuffer strBuf = new StringBuffer();
	//logger.log(Level.INFO,"placeList count="+placeList.size());
	newsMessage.setItems(items);
//	if(placeList.size()>size){
//		newsMessage.setArticleCount(size);
//	}
//	else{
//		newsMessage.setArticleCount(placeList.size());
//	}
	newsMessage.setArticleCount(1);
	logger.log(Level.INFO,"article count="+newsMessage.getArticleCount());
	newsMessage.setCreateTime(new Date().getTime()+"");
	newsMessage.setMsgType("news");
	newsMessage.setFuncFlag("0");
	newsMessage.setToUserName(userName);
	newsMessage.setFromUserName(WeChatConstant.FROMUSERNAME);
	for(int i = 0;i <newsMessage.getArticleCount();i++){
		//BaiduPlaceResponse place = placeList.get(i);
		//Double distance = GeoUtil.DistanceOfTwoPoints(Double.valueOf(place.getLng()), Double.valueOf(place.getLat()), lng, lat, GaussSphere.Beijing54);
		Item item = new Item();
		item.setTitle("");
		item.setPicUrl("");
		//item.setUrl(place.getDetailUrl());
		item.setUrl("http://www.geekpics.net/Ffw");
		item.setDescription("");
		items.add(item);
	}
	logger.log(Level.INFO,"newMessage="+newsMessage.toString());
	strBuf = strBuf.append(getWeChatNewsMessage(newsMessage));
	
	return strBuf.toString();
	
	

	}
	
	
	public static String getWeChatNewsMessage(WeChatReplyNewsMessage newsMessage){
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("xml", WeChatReplyNewsMessage.class);
		xstream.aliasField("ToUserName", WeChatReplyNewsMessage.class, "toUserName");
		xstream.aliasField("FromUserName", WeChatReplyNewsMessage.class, "fromUserName");
		xstream.aliasField("CreateTime", WeChatReplyNewsMessage.class, "createTime");
		xstream.aliasField("MsgType", WeChatReplyNewsMessage.class, "msgType");
		xstream.aliasField("ArticleCount", WeChatReplyNewsMessage.class, "articleCount");
		xstream.aliasField("Content", WeChatReplyNewsMessage.class, "content");
		xstream.aliasField("FuncFlag", WeChatReplyNewsMessage.class, "funcFlag");
		xstream.aliasField("Articles", WeChatReplyNewsMessage.class, "items");
		
		xstream.alias("item", Item.class);
		xstream.aliasField("Title", Item.class, "title");
		xstream.aliasField("Description", Item.class, "description");
		xstream.aliasField("PicUrl", Item.class, "picUrl");
		xstream.aliasField("Url", Item.class, "url");
		
		return xstream.toXML(newsMessage);
	}
	
	
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public int getArticleCount() {
		return articleCount;
	}
	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	public String getFuncFlag() {
		return funcFlag;
	}
	public void setFuncFlag(String funcFlag) {
		this.funcFlag = funcFlag;
	}

	@Override
	public String toString() {
		return "WeChatReplyNewsMessage [toUserName=" + toUserName
				+ ", fromUserName=" + fromUserName + ", createTime="
				+ createTime + ", msgType=" + msgType + ", articleCount="
				+ articleCount + ", items=" + items + ", funcFlag=" + funcFlag
				+ "]";
	}
}

class Item{
	private String title;
	private String description;
	private String picUrl;
	private String url;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
