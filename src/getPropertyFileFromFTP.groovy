
//@Grab(group='commons-net', module='commons-net', version='2.0')
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
    def propertyFile=fileslist.toList().stream().filter{ x->x.getName().toString().contains("properties") }.map{ x->x.getName().toString()}.collect().getAt(0)
    if (propertyFile==null){
        ftpClient.disconnect()
        return ["There is no any *.properties in directory " + directory]
    }else{
        ftpClient.changeWorkingDirectory(directory)
        ftpClient.fileType=(FTPClient.BINARY_FILE_TYPE)

        def incomingFile = new File(propertyFile.toString())
        incomingFile.withOutputStream { ostream ->  ftpClient.retrieveFile(propertyFile.toString(), ostream )}
        def text = incomingFile.getText()
        incomingFile.delete()
        ftpClient.disconnect()
        return text.split("\\r\\n").toList()
    }

}
else return ["Couldn't connect to FTP with "+ user]






