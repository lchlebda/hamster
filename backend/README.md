###### Problem z certyfikatem ZScaler
Trzeba dodać cert do jdk - można spróbować do Intellij + systemowo:
https://nishabe.medium.com/how-to-add-ssl-cert-to-the-java-trust-store-bbd1a52940c2

sudo keytool -import -alias zscaler_cert -file ZscalerRootCA.pem -keystore /Users/Lukasz.Chlebda\@ig.com/Library/Application\ Support/JetBrains/IntelliJIdea2021.2/ssl/cacerts
sudo keytool -import -alias zscaler_cert -file ZscalerRootCA.pem -keystore /Library/Java/JavaVirtualMachines/jdk-17.0.1.jdk/Contents/Home/lib/security/cacerts

###### RUN WITHOUT DOCKER - SPRING BOOT MW
change proxy in package.json from http://backend:8080 to http://localhost:8080/ 

###### DOCKER
remove images: 
 - docker rmi -f hamster-mw
 - docker rmi -f hamster-fe

###### DOCKER HOSTING
https://devcenter.heroku.com/articles/container-registry-and-runtime
https://medium.com/@justkrup/deploy-a-docker-container-free-on-heroku-5c803d2fdeb1
https://docs.docker.com/docker-hub/repos/
https://www.docker.com/blog/how-to-deploy-on-remote-docker-hosts-with-docker-compose/