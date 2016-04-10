Payment tracker
=================

Prerequisites
-----------
* Java 8+
* Maven 3+

Installation
-----------
```
git clone https://github.com/lukaspatoraj/bsc
cd bsc
mvn clean install
```

Test
-----------
```
mvn test
```

Usage
-----------
* run
```
mvn exec:java
```
or
```
java -jar target/bsc-1.0.jar
```
* specifying input files is optional. possible to use multiple files 
```
mvn exec:java -Dexec.args="D:\input1.txt D:\input2.txt"
java -jar target/bsc-1.0.jar D:\input1.txt D:\input2.txt
```
* in runtime payments are loaded only from console after Enter is hit
* USD exchange rate is set only for currencies, that are set in file "src/main/resources/exchange_rates.properties". if currency is missed, there is not exchange rate in output
* termination :
```
quit
```
* history of payments :
```
history
```
* samples of inputs are in src/main/java/resources

Input (format)
-----------
* correct: USD1, USD-1, USD+1, USD 1, USD  +1, USD  -1
* incorrect: usd1, _USD 1, USD1.5, USDD1

Exceptions
-----------
* error message in case of incorrect payment, file processing, file not found
* valid range for amount is <-2147483648, 2147483647>