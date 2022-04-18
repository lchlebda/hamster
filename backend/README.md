###### Problem z certyfikatem ZScaler
Trzeba dodać cert do jdk - można spróbować do Intellij + systemowo:
https://nishabe.medium.com/how-to-add-ssl-cert-to-the-java-trust-store-bbd1a52940c2

sudo keytool -import -alias zscaler_cert -file ZscalerRootCA.pem -keystore /Users/Lukasz.Chlebda\@ig.com/Library/Application\ Support/JetBrains/IntelliJIdea2021.2/ssl/cacerts
sudo keytool -import -alias zscaler_cert -file ZscalerRootCA.pem -keystore /Library/Java/JavaVirtualMachines/jdk-17.0.1.jdk/Contents/Home/lib/security/cacerts

###### DOCKER
remove images: 
 - docker rmi -f hamster-mw
 - docker rmi -f hamster-fe
