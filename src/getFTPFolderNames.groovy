//Jenkins property=ActiveChoiceParameter/SingleSelect
//@Grab(group='commons-net', module='commons-net', version='2.0')

groovy.grape.Grape.grab(group:'commons-net', module:'commons-net', version:'3.6')

import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply

def server="172.29.17.219"
def user="Jenkins"
def pass="Jenkins1"

def ftpClient = new FTPClient()
ftpClient.connect(server)
def reply=ftpClient.getReplyCode()

if (!FTPReply.isPositiveCompletion(reply)) {
    ftpClient.disconnect();
    return "Connection to FTP failed."
}
if (ftpClient.login(user,pass)){
    ftpClient.enterLocalPassiveMode()
    def fileslist = ftpClient.listFiles("/")
    ftpClient.disconnect()
    return fileslist.toList().stream().filter{ x->x.directory}.map{ x->x.getName().toString()}.collect().toList()
}
else return "Couldn't connect to FTP with "+ user






