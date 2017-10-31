
package buzz.yun.capsure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@SuppressWarnings("serial")
@Controller
public class CapsureLogin extends HttpServlet {

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		StringBuffer jb = new StringBuffer();
		String line = null;
		String name = null, password = null;
		Boolean status = false;
		int userID = 0;
		String uname = null,upassword = null;
		String value = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) { /*report an error*/ }

		try {

			JSONObject myObject = new JSONObject(jb.toString());
			name = myObject.getString("name");
			password = myObject.getString("password");


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
		//String createEntry = "INSERT INTO userLogin (username, password) VALUES (?, ?)";
		PrintWriter out = response.getWriter();
		
		
		String checkEntry = "SELECT * FROM capsureLogin WHERE  username= ? AND password = ?";


		try
		{
			Connection conn = DriverManager.getConnection(url);
			PreparedStatement statementCreateUser = conn.prepareStatement(checkEntry);
			//statementCreateUser.setInt(1, mid);
			statementCreateUser.setString(1, name);
			statementCreateUser.setString(2, password);
			ResultSet rs = statementCreateUser.executeQuery();
			while(rs.next()){
				userID  = rs.getInt("userID");
				uname = rs.getString("username");
				upassword = rs.getString("password");
			
				response.setContentType("json/application");
			
				
				if(uname.equals(name) && upassword.equals(password)){
					 value="Username & Password match  happy!";

				}
				 else if (uname.equals(name)) {
				        value="Invalid Password!";
				        uname = rs.getString("username");
				        out.print("name =");
				        out.print(uname);
				        
				    } else if (upassword.equals(password)) {
				    	upassword = rs.getString("password");
				    	   out.print("password =");
				    	out.print(upassword);
				    } else {
				        value="Invalid Username & Password!";
				    }

						
			}
			conn.close();
		}catch(Exception e){
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
		}

		response.setContentType("json/application");

		//response.setContentType("text/plain");
		// Get the printwriter object from response to write the required json object to the output stream      
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
		response.addHeader("Access-Control-Allow-Headers", "GET, PUT, OPTIONS, X-XSRF-TOKEN");

		Map<String, String> result = new HashMap<String, String>();
		
		result.put("status", String.valueOf(value));
		//if(value=="Username & Password match shilpa happy!"){
			result.put("username", String.valueOf(uname));
			result.put("password", String.valueOf(upassword));
			result.put("userID", String.valueOf(userID));
		//}
		/*else {
			
		}*/
	
		JSONObject final_result = new JSONObject(result);
		out.print(final_result);

		out.flush();

	}
}

