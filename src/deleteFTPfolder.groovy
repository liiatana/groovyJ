// Удалить конфигурацию с FTP сервера
//Jenkins :
// 1. установить и настроить плагин https://plugins.jenkins.io/groovy/
// 2. "положить" этот groovy-файл на машину Jenkins, например, /var/lib/jenkins/.groovy/deleteFTPfolder.groovy
// 3. в разделе "Сборка" добавить шаг "Execute Groovy Script" и указать путь до скрипта(из. шага 2)
@Grab(group='commons-net', module='commons-net', version='3.6')

import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply


def server="172.29.17.219"
def user="Jenkins"
def pass="Jenkins1"

def folderName=args[0] // ENVIROMENT - имя параметра джоба, в котором хранятся названия папок, передать как аргумент командной строки

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
    def deletedFolder = fileslist.toList().stream().filter{ x->x.directory && x.getName().toString().equals(folderName)}.collect()
    if (deletedFolder.isEmpty()){
        ftpClient.disconnect()
        println folderName + " not found"
    }
    else{
        def dir = deletedFolder.getAt(0).getProperties().get("name")
        ftpClient.changeWorkingDirectory(dir)
        def files = ftpClient.listFiles("/"+dir)
        files.toList().forEach(x->ftpClient.deleteFile("/"+dir+ "/" + x.getProperties().get("name")))
        ftpClient.changeWorkingDirectory("/")
        ftpClient.removeDirectory(dir)
        ftpClient.disconnect()
        println folderName + " was deleted"
    }
}
else return "Couldn't connect to FTP with "+ user