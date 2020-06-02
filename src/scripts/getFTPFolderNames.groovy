package scripts
// Получение списка папок с настройками окружения с FTP сервера
groovy.grape.Grape.grab(group:'commons-net', module:'commons-net', version:'3.6')

import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply

Properties properties = new Properties();
properties.load(new FileInputStream( System.getenv("JENKINS_HOME")+"/.groovy/"+"ftpConfig.properties"))

def ftpClient = new FTPClient()
ftpClient.connect(properties.getProperty("ftp.Server"),properties.getProperty("ftp.Port").toInteger())
def reply=ftpClient.getReplyCode()

if (!FTPReply.isPositiveCompletion(reply)) {
    ftpClient.disconnect();
    return "Connection to FTP failed."
}
if (ftpClient.login(properties.getProperty("ftp.UserName"),properties.getProperty("ftp.UserPwd"))){
    ftpClient.enterLocalPassiveMode()
    def fileslist = ftpClient.listFiles("/")
    ftpClient.disconnect()
    return fileslist.toList().stream().filter{ x->x.directory}.map{ x->x.getName().toString()}.collect().toList()
}
else return "Couldn't connect to FTP with "+ user