package com.uptimesoftware.uptime.plugin;

import java.util.*;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.PluginWrapper;
import com.uptimesoftware.uptime.plugin.api.Extension;
import com.uptimesoftware.uptime.plugin.api.Plugin;
import com.uptimesoftware.uptime.plugin.api.PluginMonitor;
import com.uptimesoftware.uptime.plugin.monitor.PluginMonitorVariable;
import com.uptimesoftware.uptime.plugin.monitor.MonitorState;
import com.uptimesoftware.uptime.plugin.monitor.Parameters;

import com.jcraft.jsch.*;

public class MonitorSSHCommand extends Plugin {

    public MonitorSSHCommand(PluginWrapper wrapper) {
        super(wrapper);
    }

	@Extension
    public static class UptimeMonitorSSHCommand extends PluginMonitor {
	
		private static final Logger logger = LoggerFactory.getLogger(MonitorSSHCommand.class);
		
		private String hostname = "";
		private String username = "";
		private String password = "";
		private Integer port = 22;
		private String sshCommand = "";
		private String outputType = "";
		private String message = "";
		


		/*
		 * Set parameters reads the input values from the XML that defines the monitor
		 * The parameters object has all the methods for fetching the values.
		 */
		@Override
		public void setParameters(Parameters params) {
		
			hostname = params.getString("hostname");
			username = params.getString("username");
			password = params.getString("password");
			port = params.getInteger("port");
			sshCommand = params.getString("sshCommand");
			outputType = params.getString("outputType");

		}


		private String slurpToEof(InputStream in) throws IOException {
                StringBuilder output = new StringBuilder();
                byte buffer[] = new byte[4096];
                int nread;
                while ((nread = in.read(buffer)) > 0) {
                        String out = new String(buffer, 0, nread);
                        output.append(out);
                }
                return output.toString();
        }
		
		private void channelDebug(ChannelExec channel) {
            logger.debug("channel connected/closed/eof: " + channel.isConnected() + "/" + channel.isClosed() + "/"
                                                + channel.isEOF());
		}

		 private void attemptConnect(Session sess) throws Exception {
                                try {
                                        sess.connect(30000);
                                } catch (JSchException e) {
                                        throw new Exception(e.getMessage(), e);
                                }
         }
		 
		 
		@Override
		public void monitor() {

			try {
				logger.debug("MonitorSSHCommand: start---- hostname:" + hostname + " username:" + username + " outputType:" + outputType);
				JSch jsch=new JSch(); 			
				Session sshSession=jsch.getSession(username, hostname, port);				
				sshSession.setConfig("StrictHostKeyChecking", "no");
				sshSession.setPassword(password);
				attemptConnect(sshSession);
				Channel channel = sshSession.openChannel("exec");
				
				logger.debug("channel connected/closed/eof: " + channel.isConnected() + "/" + channel.isClosed() + "/"
                                                + channel.isEOF());
				((ChannelExec)channel).setCommand(sshCommand);
				logger.debug("channel connected/closed/eof: " + channel.isConnected() + "/" + channel.isClosed() + "/"
                                                + channel.isEOF());
				logger.debug("sending command: " + sshCommand);
				
				InputStream in = channel.getInputStream();
				channel.connect(30000);
				logger.debug("channel connected/closed/eof: " + channel.isConnected() + "/" + channel.isClosed() + "/"
                                                + channel.isEOF());
				String output = slurpToEof(in);
				logger.debug("channel connected/closed/eof: " + channel.isConnected() + "/" + channel.isClosed() + "/"
                                                + channel.isEOF());
				
				channel.disconnect();
				sshSession.disconnect();
				
				if (outputType.equals("String")) {
					addVariable("stringOutput", output.trim());
				} else if (outputType.equals("Numeric")) {
					addVariable("numericOutput", Double.parseDouble(output));
				}
				
				message += "Monitor ran successfully - returned: " + output.trim();
				

			} catch (JSchException e) {

				message = "JSchException in monitor. " + e.getMessage();
				setState(MonitorState.CRIT);
			} catch (IOException ioe) {
				message = "IO Exception in monitor. " + ioe.getMessage();
				setState(MonitorState.CRIT);
			} catch (Exception e) {
				message = "General Exception in monitor. " + e.getMessage();
				setState(MonitorState.CRIT);
			} 
			setMessage(message);
		}
	}
	

}
