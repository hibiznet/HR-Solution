$env:JAVA_HOME="D:\DEV_PROJ3\jdk-17"
$env:Path="D:\DEV_PROJ3\jdk-17\bin;$env:Path"

Write-Host "JAVA_HOME=$env:JAVA_HOME"
java -version
javac -version

.\gradlew.bat bootRun --args="--spring.profiles.active=local"
