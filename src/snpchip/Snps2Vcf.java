//package snpchip;

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Snps2Vcf {
    public static void main(String[] args) {
        String calls_file = args[0];
        String output_filename = args[1];
        String manifest_file = args[2];
        StringBuilder headerContent = new StringBuilder();
        int row = 0;
        int samples = 0;

        // Get the current date in the format yyyyMMdd
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(dateFormatter);

        // Create a map to store the reference alleles by chrom_pos
        Map<String, Character> refAlleleMap = new HashMap<>();
        HashSet<String> contigs = new HashSet<>();
       
        boolean isFirstLine = true; // Flag to skip the header
        // Read manifest 
        try (Scanner manifestScanner = new Scanner(new java.io.File(manifest_file))) {
            while (manifestScanner.hasNextLine()) {
                String manifestLine = manifestScanner.nextLine();
                String[] manifestColumns = manifestLine.split(",");

                if (manifestColumns.length >= 16) {
                    // Skip the first line if it's the header
                    if (isFirstLine) {
                        isFirstLine = false;
                    continue;
                        }
                    String chromPos = manifestColumns[1].trim();
                    String strand = manifestColumns[2].trim();
                    String illuminaId = manifestColumns[0].trim();
                    String orientation = illuminaId.split("_")[4];
                    
                    System.out.println(orientation + " " + strand);
                    
                    char refAllele = 'A';
                    char snpAllele = 'A';
                    // Top & Bottom is used by illumina to designate strand based on variant and surrounding sequence
                    if ("TOP".equals(strand)) {
                        snpAllele = manifestColumns[3].charAt(1);
                    } else if ("BOT".equals(strand)) {
                        snpAllele = manifestColumns[3].charAt(3);
                    } else {
                        System.out.println("unrecognized strand nomenclature: " + strand);
                        break;
                    }
                    switch (snpAllele) {
                        case 'A':
                            refAllele = 'T';
                            break;
                        case 'T':
                            refAllele = 'A';
                            break;
                        case 'C':
                            refAllele = 'G';
                            break;
                        case 'G':
                            refAllele = 'C';
                            break;
                        default:
                            refAllele = 'A';
                    }
                    refAlleleMap.put(chromPos, refAllele);
                    contigs.add(chromPos.split("_")[0] + "_" + chromPos.split("_")[1]);
                }
            }
            //System.out.println(refAlleleMap);
        } catch (FileNotFoundException e) {
            System.out.println("Manifest file not found.");
            e.printStackTrace();
            return; // Exit the program if manifest file is not found
        }

        try (Scanner sc = new Scanner(new java.io.File(calls_file));
             PrintWriter writer = new PrintWriter(output_filename)) {

            // Write VCF header
            writer.println("##fileformat=VCFv4.2");
            writer.println("##fileDate=" + formattedDate);
            writer.println("##source=Snps2VcfV0");
            writer.println("##reference=Potr/v4.2");
            for (String contig : contigs) {
                writer.println("##contig=<ID=" + contig + ">");
            }
            writer.println("##INFO=<ID=NS,Number=1,Type=Integer,Description=\"Number of Samples With Data\">");
            writer.println("##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">");
            writer.println("##FORMAT=<ID=GQ,Number=2,Type=Float,Description=\"Genotype Quality\">");
            writer.write("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t");

            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] columns = line.split(",");
                // write samples into FORMAT Field from first row
                if (row == 0) {
                    for (int indv = 2; indv < columns.length; indv++) {
                        if (indv < columns.length - 1) {
                             writer.write(columns[indv] + '\t');
                        } else {
                            writer.write(columns[indv] + "\n");
                        }
                        samples++;
                    }

                // write SNP records
                } else {
                    // first column contains chrom and position
                    String[] site = columns[0].split("_");
                    String chrom = site[0] + "_" + site[1];
                    String pos = site[2];
                    String chromPos = chrom + "_" + pos;
                    char ref = refAlleleMap.getOrDefault(chromPos, 'A');
                    String alt = "C"; // should get written after genotype loop
                    String qual = ".";
                    String filter = "PASS";
                    String info = ""; // should get written after genotype loop
                    String format = "GT:GQ";
                    int ns = 0;
                    int ploidy = 2;
                    
                    // predecessing columns contain genotypes
                        // determine 0,1,2 based on manifest (TOP/BOT)
                        // calculate statistics? allele frequency, fraction genotype missing
                    
                    // Initialize mapping for allele encoding
                    List<Character> alleleList = new ArrayList<>(); // can use indexof on characters; need to check
                    List<String> site_genotypes = new ArrayList<>(); // all individual genotypes at the site
                    alleleList.add(0, ref); // update list with reference allele (0)

                    for (int genotypes = 2; genotypes < columns.length; genotypes++) {
                        StringBuilder genotypeEncoding = new StringBuilder();
                        String[] call = columns[genotypes].split("\\|");
                        char[] charArray = "#N/A".equals(call[0]) ? "ZZ".toCharArray() : call[0].toCharArray(); // Convert the string to a char array
                        String quality = ("A".equals(call[0]) || "T".equals(call[0]) || "C".equals(call[0]) || "G".equals(call[0])) ? call[1] : "."; // avoids list out of bounds error  

                        for (char allele : charArray) {
                            //System.out.println(allele);
                            // Check if allele is A, T, C, or G
                            if (allele == 'A' || allele == 'T' || allele == 'C' || allele == 'G') {
                                int alleleIndex = alleleList.indexOf(allele); // provided character prints location in list
                                if (alleleIndex != -1) { // -1 means no matches
                                    genotypeEncoding.append(alleleIndex); // add the allele encoding based on index location
                                } else {
                                    alleleList.add(alleleList.size(), allele);
                                    genotypeEncoding.append(alleleList.size() - 1);
                                }
                                ns++; // count how many samples have a valid allele. need to divide by ploidy
                            } else {
                                genotypeEncoding.append('.');
                            }
                        }
                        // update info with number of samples
                        info = "NS=" + ns / ploidy;               

                        // this joins a list with | 
                        String joinedString = genotypeEncoding.toString()
                                .chars()                                        // Convert to IntStream
                                .mapToObj(c -> String.valueOf((char) c))        // Convert each character to a string
                                .collect(Collectors.joining("|"));             // Join strings with |
                        
                        String indvFormat = joinedString + ':' + quality;

                        site_genotypes.add(indvFormat);
                    }
                    alleleList.remove(0);
                    alt = alleleList.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                    
                    String genotypes = site_genotypes.stream()
                        .collect(Collectors.joining("\t"));
                                        
                    writer.println(chrom + "\t" + pos + "\t" + columns[0] + "\t" + ref + "\t" + alt + "\t" + qual + "\t" + filter + "\t" + info + "\t" + format + "\t" + genotypes);

                    // outside of individual genotypes
                }
                row++;
            }
            int snps = row - 1;
            System.out.println("Total number of samples = " + samples);
            System.out.println("Total number of records = " + snps);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } 

    }
}