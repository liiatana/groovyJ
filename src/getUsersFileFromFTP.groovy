//Jenkins property=ActiveChoiceReferenceReactiveParameter/Bullet Items List
//@Grab(group='commons-net', module='commons-net', version='2.0')
import groovy.json.JsonSlurper
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply

def server="172.29.17.219"
def user="Jenkins"
def pass="Jenkins1"
def directory="default" //ENVIROMENT
def sourceDirectory="/"+directory


def ftpClient = new FTPClient()
ftpClient.connect(server)
def reply=ftpClient.getReplyCode()

if (!FTPReply.isPositiveCompletion(reply)) {
    ftpClient.disconnect();
    return ["Connection to FTP failed."]
}
if (ftpClient.login(user,pass)){
    ftpClient.enterLocalPassiveMode()
    def fileslist = ftpClient.listFiles(sourceDirectory)
    def usersFile=fileslist.toList().stream().filter{ x->x.getName().toString().contains("json") }.map{ x->x.getName().toString()}.collect().getAt(0)
    if (usersFile==null){
        ftpClient.disconnect()
        return ["There is no any *.json in directory " + directory]
    }else{
        ftpClient.changeWorkingDirectory(directory)
        ftpClient.fileType=(FTPClient.BINARY_FILE_TYPE)

        def incomingFile = new File(usersFile.toString())
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






