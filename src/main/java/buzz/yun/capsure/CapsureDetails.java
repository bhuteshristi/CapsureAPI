
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.api.services.sqladmin.model.User;
import com.google.gson.Gson;

@SuppressWarnings("serial")
@Controller
public class CapsureDetails extends HttpServlet {
	@RequestMapping(value = "/capsure_details", method = RequestMethod.POST)
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		StringBuffer jb = new StringBuffer();
		String line = null;
		int reqID;
		String name,subject,email,query;
		Boolean status = false;
		String data = null;
		

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
		PrintWriter out = response.getWriter();
		if(reqID==1){
			JSONObject myObject = new JSONObject(jb.toString());
			name = myObject.getString("name");
			subject = myObject.getString("subject");
			email = myObject.getString("email");	
			query = myObject.getString("query");
			String createEntry = "INSERT INTO capsureDetails (name,subject,email,query) VALUES (?, ?, ?, ?)";
			try
			{
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement statementUpdateDevice = conn.prepareStatement(createEntry);

				statementUpdateDevice.setString(1, name);
				statementUpdateDevice.setString(2, subject);
				statementUpdateDevice.setString(3, email);
				statementUpdateDevice.setString(4, query);

				int i = statementUpdateDevice.executeUpdate();
				if (i > 0)
				{
					status =  true;
					//out.print("A new device  inserted successfully!");
				}else{status = false;
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
		
		if(reqID==1){
			result.put("Status", String.valueOf(status));
		}
		
		
		JSONObject final_result = new JSONObject(result);
		out.print(final_result);
        //out.print(url);
		out.flush();
	}
}