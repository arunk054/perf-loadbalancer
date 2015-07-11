package com.vmware.perftest.loadbalancer;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.DiscoveryClient;

import java.util.List;
import com.netflix.client.ClientFactory;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

import com.netflix.client.config.DefaultClientConfigImpl;
import org.apache.commons.configuration.Configuration;
import com.netflix.config.ConfigurationManager;

/* Always looks for the acmeair-weball service*/
public class GetCachedWebapp extends HttpServlet {


	//This is better than GetWebapp because this does not invoke eureka on every request.
	public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    		
		//Code taken from Eureka Examples
	    // initialize the client - 
	    // Since we use Archaius this piece of code is not required,
	    //however archaius looks for config.properties by default and we dont have that
	    //hence this piece is needed, remove this code in future accordingly
	    //This code also makes sure initial server list is not empty, otherwise we have to wait for 30 sec to get the list of servers
        DiscoveryManager.getInstance().initComponent(
                new MyDataCenterInstanceConfig(),
                new DefaultEurekaClientConfig());


		System.out.println("Initialized GetCachedWebapp Servlet... This uses ribbon-eureka APIs to get next available Webapp from Eureka, based on a round robin load balancing algorithm");	    
  }
  
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException{
		PrintWriter out = response.getWriter();
		
		try {
			//Gets service name from the eureka-client.properties automatically using Archaius
			out.write(getNextServiceFromEureka());
		}catch(ServiceNotFoundException e){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);		
		}
		
	}
	
	public String getNextServiceFromEureka() throws ServiceNotFoundException{
	
		try {
			//Code taken from ribbon-eureka tests
			DynamicServerListLoadBalancer<Server> lb = (DynamicServerListLoadBalancer<Server>) ClientFactory.getNamedLoadBalancer("webapp-client");
			Server s = lb.chooseServer(null);
			
			//Assuming that the port is always 8080. Make sure useIpAddre property is set to true
			return s.getHost();

			
		} catch (Exception e) {
			//Any exception just throw ServiceNotFound for now
            e.printStackTrace();
			throw new ServiceNotFoundException();
		}
	}
}
