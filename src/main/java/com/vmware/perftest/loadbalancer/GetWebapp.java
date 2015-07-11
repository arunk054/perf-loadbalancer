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

/* Always looks for the acmeair-weball service*/
public class GetWebapp extends HttpServlet {


	public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    		
		//Code taken from Eureka Examples
	    // initialize the client - 
        DiscoveryManager.getInstance().initComponent(
                new MyDataCenterInstanceConfig(),
                new DefaultEurekaClientConfig());

		System.out.println("Initialized GetWebapp Servlet...");	    
  }
  
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException{
		PrintWriter out = response.getWriter();
		
		try {
			out.write(getNextServiceFromEureka("acmeair-webapp"));
		}catch(ServiceNotFoundException e){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);		
		}
		
	}
	
	public String getNextServiceFromEureka(String serviceName) throws ServiceNotFoundException{

		//vipAddress of the Webapp service - Check  acmeair-webapp.properties file
        String vipAddress = serviceName;

        InstanceInfo nextServerInfo = null;
        try {
            nextServerInfo = DiscoveryManager.getInstance()
                    .getDiscoveryClient()
                    .getNextServerFromEureka(vipAddress, false);
        } catch (Exception e) {
            System.out.println("Cannot get an instance of " + vipAddress+"to talk to from eureka : ");
            e.printStackTrace();
            throw new ServiceNotFoundException();
        }

		//return	nextServerInfo.getIPAddr() + ":" + nextServerInfo.getPort();
		//Assume default port
		return	nextServerInfo.getIPAddr();

	
	}
}
