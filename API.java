package e2e;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class API {
	
    public static void main(String[] args) throws IOException {

    	// The main routine contains an embedded http server
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
 
        // rest endpoint for the GET request for url1 defined in Data.js
        server.createContext("/api/greeting", (exchange -> {
        	exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        	exchange.getResponseHeaders().add("Content-type", "text/plain");
            if ("GET".equals(exchange.getRequestMethod())) {
                String responseText = "Hello World! from our framework-less REST API\n";           	
                exchange.sendResponseHeaders(200, responseText.length());
                exchange.getResponseBody().write(responseText.getBytes());
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
                exchange.close();
                server.stop(0);
            }        
        }));
        
        // rest endpoint for the GET request for url2 defined in Data.js
        server.createContext("/api/getID", (exchange -> {
        	exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            if ("GET".equals(exchange.getRequestMethod())) {
            	int id = querydb();
                String responseText = "Orlando ID: " + id;           	
                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseText.getBytes());
                output.flush();
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
                server.stop(0);
            }
            exchange.close();
        }));

        server.setExecutor(null); // creates a default executor
        server.start();

    }
    
    // The query api which should be refactored into a separate module, but that is "extra" right now.
    static int querydb() {
    
	Connection conn = null;
    int city_id      = -1;
    
	try {
	    // db parameters for mysql database
	    String url       = "jdbc:mysql://localhost:3306/world";
	    String user      = "fskinner";
	    String password  = "dogfood";
	    
	    // create a connection to the database
	    conn = DriverManager.getConnection(url, user, password);

	    // System.out.println("Got jdbc Connection: " + conn);
	    Statement sqlstmt  = conn.createStatement();
	    String sql = "SELECT * " +
                 "FROM city " +
    		     "WHERE name = 'Orlando'";  // eventually would want to make this where clause dynamic
	    
	    ResultSet rs    = sqlstmt.executeQuery(sql);
	    
	    while (rs.next()) {
	    	   city_id = rs.getInt("id");
	    	   System.out.println(rs.getInt("id") + "\t" + 
	    	                      rs.getString("name")  + "\t" +
	    	                      rs.getString("countrycode"));
	    	                    
	    	}

	    /* Leaving in this stored database function call that works but we aren't using results */
	    String functioncall = "{? = CALL city_search(?)}";
	    CallableStatement funcstmt = conn.prepareCall(functioncall);
	    funcstmt.registerOutParameter(1, Types.INTEGER);
        funcstmt.setString(2, "Orlando");
        funcstmt.execute();
        if (funcstmt.getInt(1) == 1) {
        	System.out.print("City found!  query returned: "+funcstmt.getInt(1)); 
        }
        else {
        	System.out.print("City not found!  query returned: "+funcstmt.getInt(1)); 
        }                                         
 
	    try{
	    	   rs.close();
	    	   sqlstmt.close();
	    	   funcstmt.close();
	    	} catch(SQLException e) {
	    	   System.out.println(e.getMessage());
	    	}
	    
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try{
	           if(conn != null) {
	             conn.close();

	           }
		}catch(SQLException ex){
	           System.out.println(ex.getMessage());
		}	
	           
	}
	return city_id;
    }
}