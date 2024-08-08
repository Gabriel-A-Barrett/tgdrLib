// takes genotype calls and fasta to build vcf

class SnpCalls {

    // stores calls
    private String[] indvs;
    private String[] genotypes;
    private TreeMap markers; // 

    public SnpCalls() {
        
    }

    public void variantContextBuilder() {
        // loop through TreeMap
        
        /*
         * HashMap [1:[site,alternative:[indv:genotypes]]]
         * INFO: first with site,
         *      reference allele is determined via reference/manifest 
         * FORMAT: genotypes, quality
         */

    }
}