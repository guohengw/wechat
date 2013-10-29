package com.Daniel.CSSA;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.Daniel.CSSA.Util.SignUtil;

import org.apache.commons.io.IOUtils;

import com.baidu.bae.api.util.BaeEnv;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Servlet implementation class HelloWeChat
 */
public class HelloWeChat extends HttpServlet {
	Logger logger = Logger. getLogger("HelloWeChat");
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HelloWeChat() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String signature = request.getParameter("signature");  
        
        String timestamp = request.getParameter("timestamp");  
     
        String nonce = request.getParameter("nonce");  
       
       String echostr = request.getParameter("echostr");  
        PrintWriter out = response.getWriter();  
    
       if (SignUtil.checkSignature(signature, timestamp, nonce)) {  
           out.print(echostr);  
       }  
       out.close();  
       out = null;  
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	       //（1）指定服务地址，其中dbname需要自己修改
     //String dbUrl = "jdbc:mysql://sqld.duapp.com:4050/dbname";
     //（2）直接从请求header中获取ip、端口、用户名和密码信息
	//String host = request.getHeader("BAE_ENV_ADDR_SQL_IP");
	//String port = request.getHeader("BAE_ENV_ADDR_SQL_PORT");
	//String username = request.getHeader("BAE_ENV_AK");
	//String password = request.getHeader("BAE_ENV_SK");
    //（3）从线程变量BaeEnv接口获取ip、端口、用户名和密码信息
	String host = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_SQL_IP);
	String port = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_SQL_PORT);
	String username = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_AK);
	String password = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_SK);
	String driverName = "com.mysql.jdbc.Driver";
	String dbUrl = "jdbc:mysql://";
	String serverName = host + ":" + port + "/";

     //从平台查询应用要使用的数据库名
	String databaseName = "FhyuGuhfGBhNDORhvdGB";
	String connName = dbUrl + serverName + databaseName;
	Connection connection = null;
		
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter pw = response.getWriter();
		String wxMsgXml = IOUtils.toString(request.getInputStream(),"utf-8");
		logger.log(Level.INFO, " get wechat message "+ wxMsgXml);
		String type = "text";
		if(wxMsgXml.indexOf("<MsgType><![CDATA[text]]></MsgType>")>0) {
			type = "text";
		}
		else if(wxMsgXml.indexOf("<MsgType><![CDATA[location]]></MsgType>")>0) {
			type = "location";
		}
		else if(wxMsgXml.indexOf("<MsgType><![CDATA[voice]]></MsgType>")>0) {
			type ="voice";
		}
		else{
			type = "unknown";
		}
		
		
		StringBuffer replyMsg = new StringBuffer();
		String returnXml = "";
		WeChatTextMessage textMsg = null;
		//text message
		String choice= "";
		
		boolean content = false;
		
		if(wxMsgXml.indexOf("<Content><![CDATA[1]]></Content>")>0) {
			choice = "饭店";
		}
		else if(wxMsgXml.indexOf("<Content><![CDATA[同意]]></Content>")>0) {
			choice = "b";
		}
		else if(wxMsgXml.indexOf("<Content><![CDATA[2]]></Content>")>0) {
			choice = "图书馆";
		}
		else if(wxMsgXml.indexOf("<Content><![CDATA[3]]></Content>")>0) {
			choice = "test";
		}
		else if(wxMsgXml.indexOf("<Content><![CDATA[4]]></Content>")>0) {
			choice = "point";
		}

		
		if("text".equalsIgnoreCase(type)){
			
			
			textMsg = getWeChatTextMessage(wxMsgXml);
			//检查是不是位置
			BaiduMapService baidu = new BaiduMapService();
//			BaiduGeoCodeResponse geo = null;
//			try {
//				geo = BaiduGeoCodeResponse.getBaiduGeoCode(baidu.getGeoCode(textMsg.getContent()));
//			} catch (Exception e) {
//				logger.log(Level.INFO,e.getMessage());
//			}
//			logger.log(Level.INFO,"geo="+geo.toString());
			
//			if(geo!= null&&geo.getLat()!=null&&geo.getLng()!=null&&geo.getLat().length()>0&&geo.getLng().length()>0){
//				returnXml = getLocMsg("酒店", geo.getLat(),geo.getLng(),textMsg.getFromUserName());
//			}
//			
//			else 
			if(textMsg != null)
			
		{

				Statement st = null;
				ResultSet set = null;
				
			try{		
				
				String StatusSql = "SELECT * FROM selection_menu2 WHERE userid = '"+textMsg.getFromUserName()+"'";
				Class.forName(driverName);
			                //具体的数据库操作逻辑
					connection = DriverManager.getConnection(connName, username,
							password);
					st = connection.createStatement();
					set = st.executeQuery(StatusSql);

					//这里应该有个  if（rs.next()）  
					if(set.next())
					{
				    content = true;

					}
					else if(choice != "b")
					{
						replyMsg.append("亲 你现在还不在我们的数据库里。。麻烦发个“同意”来注册一下。");
						 returnXml = getReplyTextMessage(replyMsg.toString(), textMsg.getFromUserName());
					}
				
			} catch (SQLException e) {
				logger.log(Level.INFO,e.getMessage());
			} catch (ClassNotFoundException e) {
				logger.log(Level.INFO,e.getMessage());
			}

		if(content||choice == "b")
		{

			 if(choice == "饭店"||choice == "图书馆")
			 {

//------------------------------------------Check if user location exist.

					 try {
					
						Statement stmt = null;
						ResultSet rs = null;
						String sql2 = "SELECT userlocation_x, userlocation_y  FROM selection_menu2 WHERE userid = '"+textMsg.getFromUserName()+"'";
						Class.forName(driverName);
					                //具体的数据库操作逻辑
							connection = DriverManager.getConnection(connName, username,
									password);
							stmt = connection.createStatement();
							rs = stmt.executeQuery(sql2);
						
						
							String X, Y;
							//这里应该有个  if（rs.next()）  
							if(rs.next())
							{
							X = rs.getString("userlocation_x");
							Y = rs.getString("userlocation_y");
								if(X != null && Y != null)
								{
						    	returnXml = getLocMsg(choice, X,Y,textMsg.getFromUserName());
								}
								else
								{
								replyMsg.append("亲先告诉我你在哪呗。。。");
								 returnXml = getReplyTextMessage(replyMsg.toString(), textMsg.getFromUserName());
								}
						    

							}
						    	
							// Call method 1, 2, 3. 

							} catch (SQLException e) {
								logger.log(Level.INFO,e.getMessage());
							} catch (ClassNotFoundException e) {
								logger.log(Level.INFO,e.getMessage());
							}

			}
			 else if(choice =="test")
				 
			 {
				 
	//------------------------------------------Check if user location exist.

						 try {
						
							Statement stmt = null;
							ResultSet rs = null;
							String sql2 = "SELECT userchoice  FROM selection_menu2 WHERE userid = '"+textMsg.getFromUserName()+"'";
							Class.forName(driverName);
						                //具体的数据库操作逻辑
								connection = DriverManager.getConnection(connName, username,
										password);
								stmt = connection.createStatement();
								rs = stmt.executeQuery(sql2);
							
							
								String X;
								//这里应该有个  if（rs.next()）  
								if(rs.next())
								{
								X = rs.getString("userchoice");
							
									
							    
									replyMsg.append("亲 今天的code是： ");
									replyMsg.append(X).append("\n");
									
									replyMsg.append(" 用餐愉快么么哒 ");
									 returnXml = getReplyTextMessage(replyMsg.toString(), textMsg.getFromUserName());
								
							    

								}
							    	
								// Call method 1, 2, 3. 

								} catch (SQLException e) {
									logger.log(Level.INFO,e.getMessage());
								} catch (ClassNotFoundException e) {
									logger.log(Level.INFO,e.getMessage());
								}

				 
			 }
			 else if(choice =="point")
				 
			 {
			
				 String X = null ,Y =null;
	//------------------------------------------Check if user location exist.
				 returnXml = getLocMsg(choice, X,Y,textMsg.getFromUserName());

			 }
				 else if(choice == "b"){
					 try {
							
							Statement stmt = null;
							ResultSet rs = null;	
							String StatusSql = "SELECT * FROM selection_menu2 WHERE userid = '"+textMsg.getFromUserName()+"'";
							Class.forName(driverName);
						                //具体的数据库操作逻辑
								connection = DriverManager.getConnection(connName, username,
										password);
								stmt = connection.createStatement();
								rs = stmt.executeQuery(StatusSql);

								if(rs.next())
								{

									replyMsg.append("亲你已经注册过啦");
							
								}
								else
								{	
									Statement stmt2 = null;
									ResultSet rs2 = null;
									
									String InsertSql = "INSERT INTO selection_menu2 (userid) VALUES ('"+textMsg.getFromUserName()+"')";
									Class.forName(driverName);
								                //具体的数据库操作逻辑
										connection = DriverManager.getConnection(connName, username,
												password);
										stmt2 = connection.createStatement();
										stmt.executeUpdate(InsertSql);
									
									replyMsg.append("好滴亲，你注册成功了哟！");
									replyMsg.append("我们现在的 menu option:").append("\n");
									replyMsg.append("1 - 饭店").append("\n");
									replyMsg.append("2 - 图书馆(test)").append("\n");
									replyMsg.append("3 - 索要邀请码").append("\n");
									replyMsg.append("4 - 点数查询").append("\n");
									
								}

								
								} catch (SQLException e) {
									logger.log(Level.INFO,e.getMessage());
								} catch (ClassNotFoundException e) {
									logger.log(Level.INFO,e.getMessage());
								}

					 returnXml = getReplyTextMessage(replyMsg.toString(), textMsg.getFromUserName());

				 }
				 else
				 {
						replyMsg.append("不要乱发。。。..").append("\n");
						replyMsg.append("我现在只能看懂 ：").append("\n");
						replyMsg.append("1 - 饭店").append("\n");
						replyMsg.append("2 - 图书馆(test)").append("\n");
						replyMsg.append("3 - 索要邀请码").append("\n");
						replyMsg.append("4 - 点数查询").append("\n");
						
						 returnXml = getReplyTextMessage(replyMsg.toString(), textMsg.getFromUserName());
				 }
			}
		}
	}
			
		//location message
		else if("location".equalsIgnoreCase(type)){


//------------------------------------------Check if user location exist.

			WeChatLocationMessage localMsg = WeChatLocationMessage.getWeChatLocationMessage(wxMsgXml);
			//returnXml = getLocMsg("", localMsg.getLocationx(),localMsg.getLocaltiony(),localMsg.getFromUserName());
		 try {
		
			Statement stmt = null;
			
			int flag = 0;
			String UpdateSql = "UPDATE selection_menu2 SET userlocation_x = '"+localMsg.getLocationx()+"', userlocation_y = '"+localMsg.getLocaltiony()+"' WHERE userid = '"+localMsg.getFromUserName()+"'";
			
			
			Class.forName(driverName);
		                //具体的数据库操作逻辑
				connection = DriverManager.getConnection(connName, username,
						password);
				stmt = connection.createStatement();
				stmt.executeUpdate(UpdateSql);
				flag = stmt.executeUpdate(UpdateSql);
			
				//if(flag != 0)
				//{
					//replyMsg.append(localMsg.getLocationx()+" and y:"+localMsg.getLocaltiony());
				//}
					
				replyMsg.append("okay 我知道你在哪里啦，想要找什么？");
				
				returnXml = getReplyTextMessage(replyMsg.toString(), localMsg.getFromUserName());

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					
					logger.log(Level.INFO,e.getMessage());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					logger.log(Level.INFO,e.getMessage());
				}

		 
			
		}
		
		//unknown type
		else{
			
			 replyMsg.append("我看不懂欸。。。。");
				//replyMsg.append(Name);
			
				returnXml = getReplyTextMessage(replyMsg.toString(), textMsg.getFromUserName());

			}
				
		logger.log(Level.INFO, "reply message "+ returnXml);
		pw.println(returnXml);
	
	}
	
	
	
	
	
		
	private String getLocMsg(String target, String lat,String lng,String userName){
		String returnXml = "";
		//StringBuffer replyMsg = new StringBuffer();
		BaiduMapService baidu = new BaiduMapService();
		String respXml;
		
		if(target == "图书馆")
		{
			try {
				respXml = baidu.getPalace(target, lat, lng);
				List<BaiduPlaceResponse> list = BaiduPlaceResponse.getBaiduPlace(respXml);
				returnXml =WeChatReplyNewsMessage.getWeChatReplyNewsMessageByBaiduPlace2(list,Double.valueOf(lat),Double.valueOf(lng),userName,WeChatConstant.ARTICLE_SIZE);
			} catch (Exception e) {
				logger.log(Level.INFO, e.getMessage());
			}
			
		}
		else if(target == "test")
		{
			try {
					respXml = baidu.getPalace(target, lat, lng);
					List<BaiduPlaceResponse> list = BaiduPlaceResponse.getBaiduPlace(respXml);
					returnXml =WeChatReplyNewsMessage.getWeChatReplyNewsMessageByBaiduPlace(list,Double.valueOf(lat),Double.valueOf(lng),userName,WeChatConstant.ARTICLE_SIZE);
				} catch (Exception e) {
										logger.log(Level.INFO, e.getMessage());
										}
		}
		else if(target == "point")
		{
			try {
					respXml = baidu.getPalace(target, lat, lng);
					List<BaiduPlaceResponse> list = BaiduPlaceResponse.getBaiduPlace(respXml);
					returnXml =WeChatReplyNewsMessage.getWeChatReplyNewsMessageByBaiduPlace3(list,userName,WeChatConstant.ARTICLE_SIZE);
				} catch (Exception e) {
										logger.log(Level.INFO, e.getMessage());
										}
		}
		else
		{
			try {
					respXml = baidu.getPalace(target, lat, lng);
					List<BaiduPlaceResponse> list = BaiduPlaceResponse.getBaiduPlace(respXml);
					returnXml =WeChatReplyNewsMessage.getWeChatReplyNewsMessageByBaiduPlace(list,Double.valueOf(lat),Double.valueOf(lng),userName,WeChatConstant.ARTICLE_SIZE);
				} catch (Exception e) {
										logger.log(Level.INFO, e.getMessage());
										}
		}
		
		return returnXml;
	}
	

	private WeChatTextMessage getWeChatTextMessage(String xml){
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("xml", WeChatTextMessage.class);
		xstream.aliasField("ToUserName", WeChatTextMessage.class, "toUserName");
		xstream.aliasField("FromUserName", WeChatTextMessage.class, "fromUserName");
		xstream.aliasField("CreateTime", WeChatTextMessage.class, "createTime");
		xstream.aliasField("MsgType", WeChatTextMessage.class, "messageType");
		xstream.aliasField("Content", WeChatTextMessage.class, "content");
		xstream.aliasField("MsgId", WeChatTextMessage.class, "msgId");
		WeChatTextMessage wechatTextMessage = (WeChatTextMessage)xstream.fromXML(xml); 
		return wechatTextMessage;
	}

	private String getReplyTextMessage(String content, String weChatUser){
		WeChatReplyTextMessage we = new WeChatReplyTextMessage();
		we.setMessageType("text");
		we.setFuncFlag("0");
		we.setCreateTime(new Long(new Date().getTime()).toString());
		we.setContent(content);
		we.setToUserName(weChatUser);
		we.setFromUserName(WeChatConstant.FROMUSERNAME);
		XStream xstream = new XStream(new DomDriver()); 
		xstream.alias("xml", WeChatReplyTextMessage.class);
		xstream.aliasField("ToUserName", WeChatReplyTextMessage.class, "toUserName");
		xstream.aliasField("FromUserName", WeChatReplyTextMessage.class, "fromUserName");
		xstream.aliasField("CreateTime", WeChatReplyTextMessage.class, "createTime");
		xstream.aliasField("MsgType", WeChatReplyTextMessage.class, "messageType");
		xstream.aliasField("Content", WeChatReplyTextMessage.class, "content");
		xstream.aliasField("FuncFlag", WeChatReplyTextMessage.class, "funcFlag");
		String xml =xstream.toXML(we);
		return xml;
	}

}
