
//@Grab(group='commons-net', module='commons-net', version='2.0')
import org.apache.commons.net.ftp.FTPClient

def server="server_IP"
def user="ftp_user"
def pass="ftp_user_password"

def ftpClient = new FTPClient()
ftpClient.connect(server)
//println(ftpClient.replyString)
ftpClient.login(user,pass)
ftpClient.enterLocalPassiveMode()

def fileslist = ftpClient.listFiles("/");

ftpClient.disconnect()

return fileslist.toList().stream().filter(x->x.directory).map(x->x.name).toArray();
