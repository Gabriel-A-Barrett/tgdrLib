package snpchip.manifest;

import java.util.HashMap;

public class Manifest {
    private String ilmnId;
    private String name;
    private String ilmnStrand;
    private String snp;
    private String addressAId;
    private String alleleAProbeSeq;
    private String addressBId;
    private String alleleBProbeSeq;
    private String chr;
    private String mapInfo;
    private String ploidy;
    private String species;
    private String customerStrand;
    private String sequence;
    private String illumicodeSeq;
    private String topGenomicSeq;

    public Manifest(HashMap<String, String> row) {
        this.ilmnId = row.get("IlmnID");
        this.name = row.get("Name");
        this.ilmnStrand = row.get("IlmnStrand");
        this.snp = row.get("SNP");
        this.addressAId = row.get("AddressA_ID");
        this.alleleAProbeSeq = row.get("AlleleA_ProbeSeq");
        this.addressBId = row.get("AddressB_ID");
        this.alleleBProbeSeq = row.get("AlleleB_ProbeSeq");
        this.chr = row.get("Chr");
        this.mapInfo = row.get("MapInfo");
        this.ploidy = row.get("Ploidy");
        this.species = row.get("Species");
        this.customerStrand = row.get("CustomerStrand");
        this.sequence = row.get("sequence");
        this.illumicodeSeq = row.get("IllumicodeSeq");
        this.topGenomicSeq = row.get("TopGenomicSeq");
    }

    public String getIlmnId() {
        return ilmnId;
    }

    public String getName() {
        return name;
    }

    public String getIlmnStrand() {
        return ilmnStrand;
    }

    public String getSnp() {
        return snp;
    }

    public String getAddressAId() {
        return addressAId;
    }

    public String getAlleleAProbeSeq() {
        return alleleAProbeSeq;
    }

    public String getAddressBId() {
        return addressBId;
    }

    public String getAlleleBProbeSeq() {
        return alleleBProbeSeq;
    }

    public String getChr() {
        return chr;
    }

    public String getMapInfo() {
        return mapInfo;
    }

    public String getPloidy() {
        return ploidy;
    }

    public String getSpecies() {
        return species;
    }

    public String getCustomerStrand() {
        return customerStrand;
    }

    public String getSequence() {
        return sequence;
    }

    public String getReferenceSequence() {
        String[] alleles = snp.substring(1, snp.length() - 1).split("/");
        int snpPosition = sequence.indexOf('[');
        if (snpPosition != -1) {
            if (ilmnStrand.equals("TOP")) {
                return sequence.substring(0, snpPosition) + alleles[0] + sequence.substring(snpPosition + 5);
            } else {
                return sequence.substring(0, snpPosition) + alleles[1] + sequence.substring(snpPosition + 5);
            }
        } else {
            System.out.println("Warning: SNP position not found in " + ilmnId + " with sequence: " + sequence);
            return null;
        }
    }

    public String getIllumicodeSeq() {
        return illumicodeSeq;
    }

    public String getTopGenomicSeq() {
        return topGenomicSeq;
    }

    @Override
    public String toString() {
        return "Manifest Entry:\n" +
                "  IlmnID: " + ilmnId + "\n" +
                "  Name: " + name + "\n" +
                "  IlmnStrand: " + ilmnStrand + "\n" +
                "  SNP: " + snp + "\n" +
                "  Sequence: " + sequence + "\n" +
                "  Chr: " + chr + "\n" +
                "  MapInfo: " + mapInfo + "\n";
    }
}