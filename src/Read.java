/**
 * Created by peltzer on 22/03/2017.

 */
public class Read {
    private String id;
    private String seq;
    private String strand;
    private String qual;

    public Read(String id, String seq, String strand, String qual) {
        this.id = id;
        this.seq = seq;
        this.strand = strand;
        this.qual = qual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getQual() {
        return qual;
    }

    public void setQual(String qual) {
        this.qual = qual;
    }

    public String getOutputString(){
        return id + "\n" + seq + "\n" + strand + "\n" + qual + "\n";
    }
}
