# Compile snpchip

1) `javac -d . snpchip/Manifest2Fasta.java snpchip/Snps2Vcf.java`
2) `jar cvf snpchip.jar snpchip/`

Usage: `java -cp snpchip.jar snpchip.Snps2Vcf ./snpchip/test/SNP_calls.csv test.vcf ./snpchip/test/manifest_chip34K.csv`

# Compile tpps

`javac -d build -cp lib/json-20220320.jar src/snpchip/*.java src/tpps/*.java`
`jar cvfm build/tgdr.jar manifest.txt -C build .`
with dependency
`jar cvfm build/tgdr.jar manifest.txt -C build . -C lib json-20220320.jar`

# compile main first
javac -d build/classes/main -cp lib/htsjdk-4.1.1.jar:lib/json-20220320.jar src/main/java/snpchip/*.java src/main/java/tpps/*.java

# compile test source files
javac -d build/classes/test -cp lib/htsjdk-4.1.1.jar:lib/json-20220320.jar:build/classes/main:lib/junit-platform-console-standalone-1.8.2.jar src/test/java/snpchip/*.java src/test/java/tpps/*.java

# download JUnit Platform Console Standalone JAR 
java -jar lib/junit-platform-console-standalone-1.8.2.jar -cp build/classes/main:build/classes/test --scan-class-path

# Compile a specific class GrabEndPoint

1) `javac -d build/classes/main -cp lib/json-20220320.jar:lib/htsjdk-4.1.1.jar src/main/java/tpps/GrabEndPoint.java`
2) `javac -d build/classes/test -cp lib/json-20220320.jar:lib/htsjdk-4.1.1.jar:build/classes/main src/test/java/tpps/GrabEndPointTest.java`
3)  `jar -cvf build/tpps.jar -C build/classes/main . -C build/classes/test .`
4) `java -cp build/tpps.jar:lib/json-20220320.jar:lib/htsjdk-4.1.1.jar tpps.GrabEndPointTest <token>`