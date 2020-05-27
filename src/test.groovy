//рабочий- Список версий браузера

import groovy.json.JsonSlurper

def browser='chrome'

def selenoidURL="http://server_IP:8080/status"

def get = new URL(selenoidURL).openConnection();

if(get.getResponseCode().equals(200)) {
    def response =get.getInputStream().getText()
    def jsonSlurper = new JsonSlurper()
    def jsonResponce = jsonSlurper.parseText(response.toString())
    Map versions = jsonResponce.get("state").get ("browsers").get(browser)

    def selectedValue=versions.keySet().sort().last() +":selected"
    versions.remove(versions.keySet().sort().last())
    versions.put (selectedValue,"")

    return versions.keySet().asList().sort()
}
def exception= "Connection to " + selenoidURL + " failed"
return [exception];