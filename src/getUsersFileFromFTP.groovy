groovy.grape.Grape.grab(group:'commons-net', module:'commons-net', version:'3.6')
import groovy.json.JsonSlurper
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply

Properties properties = new Properties();
properties.load(new FileInputStream( System.getenv("JENKINS_HOME")+"/.groovy/"+"ftpConfig.properties"))

def directory=ENVIROMENT // имя параметра Jenkins, в котором выполняется скрипт getFTPFolderList
def sourceDirectory="/"+directory
def tmp_path= System.getenv("JENKINS_HOME")+"/"

def ftpClient = new FTPClient()
ftpClient.connect(properties.getProperty("ftp.Server"),properties.getProperty("ftp.Port").toInteger())
def reply=ftpClient.getReplyCode()

if (!FTPReply.isPositiveCompletion(reply)) {
    ftpClient.disconnect();
    return ["Connection to FTP failed."]
}
if (ftpClient.login(properties.getProperty("ftp.UserName"),properties.getProperty("ftp.UserPwd"))){
    ftpClient.enterLocalPassiveMode()
    def fileslist = ftpClient.listFiles(sourceDirectory)
    def usersFile=fileslist.toList().stream().filter{ x->x.getName().toString().contains("json") }.map{ x->x.getName().toString()}.collect().getAt(0)
    if (usersFile==null){
        ftpClient.disconnect()
        return ["There is no any *.json in directory " + directory]
    }else{
        ftpClient.changeWorkingDirectory(directory)
        ftpClient.fileType=(FTPClient.BINARY_FILE_TYPE)

        def incomingFile = new File(tmp_path+usersFile.toString())
        incomingFile.withOutputStream { ostream ->  ftpClient.retrieveFile(usersFile.toString(), ostream )}
        def text = incomingFile.getText()
        incomingFile.delete()
        ftpClient.disconnect()
        def jsonSlurper = new JsonSlurper()
        def jsonResponce = jsonSlurper.parseText(text)

        return jsonResponce.collect().stream().map{x->x.toString()}.collect()
    }

}
else return ["Couldn't connect to FTP with "+ user]