import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simple java tool to simulate adapter contamination in a FastQ file of your choice and annotating this for verification purposes in your data file.
 * This is simply assuming perfect adapters without sequencing errors, thus being unable to simulate much more than perfect adapters of variable length.
 * Created by peltzer on 22/03/2017.
 */
public class AdaptorContaminator {
    private int desired_read_length;
    private int adapter_length;
    private String adapter; // = "AGATCGGAAGAGCACACGTCTGAACTCCAGTCAC";
    private BufferedReader bfr;
    private FileReader fr;

    private BufferedWriter bfwr;
    private FileWriter fileWriter;

    public AdaptorContaminator(File f, int desired_read_length, String adapter) throws Exception {
        this.desired_read_length = desired_read_length;
        this.adapter_length = adapter.length();
        this.adapter = adapter;

        //init readers/writers
        fr = new FileReader(f);
        bfr = new BufferedReader(fr);

        fileWriter = new FileWriter(new File(f.getAbsolutePath()+"." + desired_read_length +".ACcont.fq"));
        bfwr = new BufferedWriter(fileWriter);
        runWorker();
    }

    /*
        This method takes a read, draws a random number between maximal library length (desired_read_length) and cuts off a couple of bases that are then filled up with adapter sequence.
         */
    private Read contaminateRead(Read rawRead){

        int randomNumber = ThreadLocalRandom.current().nextInt(desired_read_length-adapter_length, desired_read_length+1);
        int cutoff = desired_read_length-randomNumber;

        String tmpid = rawRead.getId(); //we add information about what we did to the read for evaluation later...
        String tmpseq = rawRead.getSeq();
        String tmpstrand = rawRead.getStrand();//we just keep it as well
        String tmpqual = rawRead.getQual();//just keep the quality equal to before, dont care about this for now

        if(randomNumber <= tmpseq.length()){
            tmpseq = tmpseq.substring(0,randomNumber-1);
            tmpseq = tmpseq + adapter.substring(0,cutoff);
            tmpid += "-AC-"+String.valueOf(cutoff); //add -AC-7 to let people know that this has AdaptorContamination of length 7 of used input adapter (!)
            if(tmpseq.length() > tmpqual.length()) {
                int offset = tmpseq.length() - tmpqual.length();
                tmpqual = tmpqual + tmpqual.substring(tmpqual.length()-offset, tmpqual.length());
            }
        } else {
            int elong = desired_read_length - tmpseq.length();
            tmpseq = tmpseq + adapter.substring(0, elong);
            tmpid += "-AC+"+String.valueOf(elong);
            tmpstrand = rawRead.getStrand();
            tmpqual = tmpqual + tmpqual.substring(tmpqual.length()-elong, tmpqual.length());
        }



        return new Read(tmpid, tmpseq,tmpstrand,tmpqual);
    }


    /**
     * Runs the actual processing
     *  * @RSRS-10000
     CTAACACTATGCTTAGGCGCTATCACCACTCTGTTCGCAGCAGTCTGCGCCCTTACACAAAATGACATCAAAAAA
     +
     AAAAAEEEEAEEEEEEEAEEEEEEEEEEEEAE<EAEEEEEEEEEEEAEEE//EAAAAEEAEEEEEEE/EEEEEEE
     *
     * @throws IOException
     */
    private void runWorker() throws Exception {

        String[] read;

        while((read = readFourLines(bfr)) != null){

            Read r = new Read(read[0], read[1], read[2], read[3]);

            Read cont_read = contaminateRead(r);

            bfwr.write(cont_read.getOutputString());

        }

        bfwr.flush();
        bfwr.close();



    }


    /*
     * Reads four lines at once
     *
     */

    private  String[] readFourLines(BufferedReader br) throws Exception {
        String[] fourLines = new String[4];
        for(int i = 0; i < 4; i++) {
            String line = br.readLine();
            if(line == null)
                return null;
            fourLines[i] = line;
        }
        return fourLines;
    }


    public static void main(String[] args) throws Exception {
        if(args.length != 3) {//need input.fq, adapter, desired read length
            System.out.println("Need input fastq, adapter sequence and desired read length for this!");
            System.exit(0);
        } else {
            AdaptorContaminator adaptorContaminator = new AdaptorContaminator(new File(args[0]), Integer.parseInt(args[1]), args[2]);

        }
    }

}
