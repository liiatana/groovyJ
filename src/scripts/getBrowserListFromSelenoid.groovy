package scripts
//Список браузеров

import groovy.json.JsonSlurper

def selenoidURL="http://172.19.90.46:8080/status"

def get = new URL(selenoidURL).openConnection();

if(get.getResponseCode().equals(200)) {
    def response =get.getInputStream().getText()
    def jsonSlurper = new JsonSlurper()
    def jsonResponce = jsonSlurper.parseText(response.toString())
    Map browsers = jsonResponce.get("state").get ("browsers")

    def selectedValue=browsers.keySet().asList().get(0)+":selected"
    browsers.remove(browsers.keySet().getAt(0))
    browsers.put (selectedValue,"")
    return browsers.keySet().asList().sort()
}
def exception= "Connection to " + selenoidURL + " failed"
return [exception];