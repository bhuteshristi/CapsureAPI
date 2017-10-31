/*package buzz.yun.capsure;

import javax.ws.rs.Path;

import org.jsoup.helper.HttpConnection.Response;

@Path("/authentication")
public class Test {
	
	public Response authenticateUser(String username,String password){
		   try {

	            // Authenticate the user using the credentials provided
	            authenticate(username, password);

	            // Issue a token for the user
	            String token = issueToken(username);

	            // Return the token on the response
	            return Response.ok(token).build();

	        } catch (Exception e) {
	            return Response.status(Response.Status.UNAUTHORIZED).build();
	        }      
	    }
	private void authenticate(String username, String password) throws Exception {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
    }
	private String issueToken(String username){
		return issueToken(null);
		
	}
	public Response authenticateUser(Credentials credentials) {

	    String username = credentials.getUsername();
	    String password = credentials.getPassword();
		return null;

	    // Authenticate the user, issue a token and return a response
	}
		
	}


*/