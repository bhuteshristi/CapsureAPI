package buzz.yun.capsure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.handlers.MapListHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@SuppressWarnings("serial")
@Controller
public class AutoTravel extends HttpServlet {


	@RequestMapping(value = "/auto_travel", method = RequestMethod.POST)
	public void doPost (HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		StringBuffer jb = new StringBuffer();
		String line = null;		
		int reqID;
		Boolean status = false;
		String carNumber,policyType,name,email,phoneno;
		String travelingLocation,startDate,endDate;

		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) { /*report an error*/ }
		try {

			JSONObject myObject = new JSONObject(jb.toString());
			reqID = myObject.getInt("reqID");

		} catch (JSONException e) {
			// crash and burn
			throw new IOException("Error parsing JSON request string");
		}
		String url;
		if (System
				.getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {
			url = System.getProperty("ae-cloudsql.cloudsql-database-url");
			try {
				// Load the class that provides the new "jdbc:google:mysql://" prefix.
				Class.forName("com.mysql.jdbc.GoogleDriver");
			} catch (ClassNotFoundException e) {
				throw new ServletException("Error loading Google JDBC Driver", e);
			}
		} else {
			// Set the url with the local MySQL database connection url when running locally
			url = System.getProperty("ae-cloudsql.local-database-url");
		}


		if(reqID==1)
		{
			JSONObject myObject = new JSONObject(jb.toString());
			carNumber = myObject.getString("carNumber");
			policyType = myObject.getString("policyType");
			name = myObject.getString("name");
			email = myObject.getString("email");
			phoneno = myObject.getString("phoneno");


			String createEntry = "INSERT INTO auto (carNumber,policyType,name,email,phoneno) VALUES (?, ?, ?, ?, ?)";
			try
			{
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement statementCreate = conn.prepareStatement(createEntry);
				statementCreate.setString(1, carNumber);
				statementCreate.setString(2, policyType);
				statementCreate.setString(3, name);
				statementCreate.setString(4, email);
				statementCreate.setString(5, phoneno);

				int i = statementCreate.executeUpdate();
				if (i > 0)
				{
					status =  true;

				}else
				{
					status = false;
				}

				conn.close();
			}catch(Exception e){
				System.err.println("Got an exception!");
				System.err.println(e.getMessage());
			}
		}


		if(reqID==2)
		{
			JSONObject myObject = new JSONObject(jb.toString());
			travelingLocation = myObject.getString("travelingLocation");
			startDate = myObject.getString("startDate");
			endDate = myObject.getString("endDate");
			name = myObject.getString("name");
			email = myObject.getString("email");
			phoneno = myObject.getString("phoneno");


			String create = "INSERT INTO travel (travelingLocation,startDate,endDate,name,email,phoneno) VALUES (?, ?, ?, ?, ?, ?)";
			try
			{
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement statementCreate = conn.prepareStatement(create);
				statementCreate.setString(1, travelingLocation);
				statementCreate.setString(2, startDate);
				statementCreate.setString(3, endDate);
				statementCreate.setString(4, name);
				statementCreate.setString(5, email);
				statementCreate.setString(6, phoneno);

				int i = statementCreate.executeUpdate();
				if (i > 0)
				{
					status =  true;

				}else
				{
					status = false;
				}

				conn.close();
			}catch(Exception e){
				System.err.println("Got an exception!");
				System.err.println(e.getMessage());
			}
		}
		

		response.setContentType("json/application");

		// Get the printwriter object from response to write the required json object to the output stream      
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
		response.addHeader("Access-Control-Allow-Headers", "GET, PUT, OPTIONS, X-XSRF-TOKEN");
		Map<String, String> result = new HashMap<String, String>();
		//result.put("reqID",String.valueOf(reqID));
		if(reqID==1||reqID==2){
			result.put("status", String.valueOf(status));

		}

		JSONObject final_result = new JSONObject(result);
		out.print(final_result);

		out.flush();
	}
}





