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


public class GetWebapp extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
    	super.init(config);
		System.out.println("Initialized GetWebapp Servlet...");	    
  }
  
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException{
		PrintWriter out = response.getWriter();
		
		try {
			out.write(getNextServiceFromEureka());
		}catch(ServiceNotFoundException e){
			out.write("");			
		}
	}
	
	public String getNextServiceFromEureka() throws ServiceNotFoundException{
	
		//Code taken from Eureka Examples
	    // initialize the client - 
        DiscoveryManager.getInstance().initComponent(
                new MyDataCenterInstanceConfig(),
                new DefaultEurekaClientConfig());

		//vipAddress of the Webapp service - Check  acmeair-webapp.properties file
        String vipAddress = "acmeair-webapp";

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

		return	nextServerInfo.getVIPAddress() + ":" + nextServerInfo.getPort();
	
	}
}