package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import configurations.ConfigurationManager;
import configurations.ProjectSpecificInterface;
import report.JIRAReport;

@SuppressWarnings("unused")
public class ConnectWinscpServer {
	
	public ProjectSpecificInterface ipp = new IPPMethods();
	public static Logger logger = LogManager.getLogger(ConnectWinscpServer.class);
	public Properties prop = new Properties();
	public String winscpUserName = null;
	public String winscpPassword = null;	
	public String destinationFilepath = null;
	public String winscpHostname = null;
	public String winscpFileProtocol = null;
	public String winscpIPAddress = null;
	public int winscpport;
	public String privateKeyPath = null;
	static JSch jsch = null;
	
	/*
	 * public void setWinscpConfig(String fileType,String localPain113filepath)
	 * throws JSchException, IOException, SftpException {
	 * 
	 * winscpUserName =
	 * ConfigurationManager.getInstance().getConfigReader().getWinscpUsername();
	 * winscpPassword =
	 * ConfigurationManager.getInstance().getConfigReader().getWinscpPassword();
	 * winscpport = 22; winscpHostname =
	 * ConfigurationManager.getInstance().getConfigReader().getWinscpHostname();
	 * destinationFilepath =
	 * ConfigurationManager.getInstance().getConfigReader().getDestinationFilepath(
	 * fileType); privateKeyPath =
	 * ConfigurationManager.getInstance().getConfigReader().getprivateKeyPath();
	 * 
	 * JSch jsch = new JSch(); Session session = null;
	 * 
	 * if (winscpPassword == null || winscpPassword.equalsIgnoreCase("")) {
	 * jsch.addIdentity(privateKeyPath); session = jsch.getSession(winscpUserName,
	 * winscpHostname, winscpport); // Establishing connection
	 * session.setConfig("PreferredAuthentications",
	 * "publickey,keyboard-interactive,password"); // set publickey as password
	 * 
	 * } else {
	 * 
	 * session = jsch.getSession(winscpUserName, winscpHostname, winscpport); //
	 * Establishing connection session.setPassword(winscpPassword); }
	 * 
	 * session.setConfig("StrictHostKeyChecking", "no"); session.connect();
	 * 
	 * boolean ptimestamp = true; // exec 'scp -t rfile' remotely String command =
	 * "scp " + (ptimestamp ? "-p" : "") + " -t " + destinationFilepath; Channel
	 * channel = session.openChannel("exec"); ((ChannelExec)
	 * channel).setCommand(command);
	 * 
	 * // get I/O streams for remote scp OutputStream out =
	 * channel.getOutputStream(); InputStream in = channel.getInputStream();
	 * 
	 * channel.connect();
	 * 
	 * 
	 * File _lfile = new File(localPain113filepath);
	 * 
	 * if (ptimestamp) { command = "T" + (_lfile.lastModified() / 1000) + " 0"; //
	 * The access time should be sent here, // but it is not accessible with JavaAPI
	 * ;-< command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
	 * out.write(command.getBytes()); // out.flush(); if(checkAck(in)!=0) {
	 * System.exit(0); }
	 * 
	 * }
	 * 
	 * // send "C0644 filesize filename", where filename should not include '/'
	 * 
	 * long filesize = _lfile.length(); command = "C0644 " + filesize + " "; if
	 * (localPain113filepath.lastIndexOf('/') > 0) { command +=
	 * localPain113filepath.substring(localPain113filepath.lastIndexOf('/') + 1);
	 * 
	 * } else if (localPain113filepath.lastIndexOf('\\') > 0) { command +=
	 * localPain113filepath.substring(localPain113filepath.lastIndexOf('\\') + 1);
	 * 
	 * } else { command += localPain113filepath; } command += "\n";
	 * out.write(command.getBytes());
	 * 
	 * 
	 * 
	 * // send a content of lfile FileInputStream fis = new
	 * FileInputStream(localPain113filepath); byte[] buf = new byte[1024]; while
	 * (true) { int len = fis.read(buf, 0, buf.length); if (len <= 0) break;
	 * out.write(buf, 0, len); // out.flush(); } ipp.hardSleep(2); fis.close(); //
	 * fis = null; // send '\0' buf[0] = 0; out.write(buf, 0, 1); out.flush();
	 * out.close(); ipp.hardSleep(2); channel.disconnect(); session.disconnect();
	 * 
	 * System.out.println("Pain 113 file placed in WinSCP");
	 * 
	 * 
	 * 
	 * }
	 */
}
