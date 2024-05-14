# Compile snpchip

1) `javac -d . snpchip/Manifest2Fasta.java snpchip/Snps2Vcf.java`
2) `jar cvf snpchip.jar snpchip/`

Usage: `java -cp snpchip.jar snpchip.Snps2Vcf ./snpchip/test/SNP_calls.csv test.vcf ./snpchip/test/manifest_chip34K.csv`