import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by peltzer on 23/03/2017.
 */
public class AdapterClippingEvaluator {
    private BufferedReader bfr;
    private FileReader fr;

    private int overtrimmed = 0; //For cases where there was much more trimmed than expected
    private int correctlyTrimmed = 0; //exactly correct trimming (OL - specified adapter length = current length of the read)
    private int undertrimmed = 0; // for cases where the adapter was not successfully removed unfortunately

    private HashMap<Integer, Integer> overtrimmed_data = new HashMap<>();
    private HashMap<Integer, Integer> undertrimmed_data = new HashMap<>();


    private boolean debug = false; // to use System out for statistics

    private AdapterClippingEvaluator(File inputFastq) throws Exception {
        fr = new FileReader(inputFastq);
        bfr = new BufferedReader(fr);
        String[] fourLines;
        while((fourLines = readFourLines(bfr)) != null) {
            Read r = new Read(fourLines[0], fourLines[1], fourLines[2], fourLines[3]);
            String id = r.getId();
            //"-OL-" + String.valueOf(originalReadLength) + "-AC-"+String.valueOf(cutoff)
            int originalLength = Integer.parseInt(id.split("-OL-")[1].split("-AC[-+]")[0]);
            int adapterCont = Integer.parseInt(id.split("-AC[-+]")[1]);

            int currSeqLength = r.getSeq().length();

            if(currSeqLength == originalLength-adapterCont){
                correctlyTrimmed++;
            }

            if(currSeqLength > originalLength-adapterCont){
                undertrimmed++;
                System.out.println(r.getId() + " Undertrimmed");
                if(undertrimmed_data.containsKey(adapterCont)){
                    undertrimmed_data.put(adapterCont, undertrimmed_data.get(adapterCont)+1);//Log distribution of problematic reads
                } else {
                    undertrimmed_data.put(adapterCont, 1);
                }
            }

            if(currSeqLength < originalLength-adapterCont){
                overtrimmed++;
                System.out.println(r.getId() + " Overtrimmed");
                if(overtrimmed_data.containsKey(adapterCont)){
                    overtrimmed_data.put(adapterCont, overtrimmed_data.get(adapterCont)+1);//Log distribution of problematic reads
                } else {
                    overtrimmed_data.put(adapterCont, 1);
                }
            }
        }

        printStats();


    }


    private void printStats(){
        System.out.println("Final statistics for input dataset:\n");
        System.out.println("Total correctly trimmed: " + correctlyTrimmed + "\n");
        System.out.printf("Overtrimmed: " + overtrimmed+ "\n");
        System.out.println("Undertrimmed: " + undertrimmed+ "\n");


        System.out.println("Overtrimmed Data Length Distribution: \n");
        System.out.println("Size\tNumber\n");
        SortedSet<Integer> keys = new TreeSet<Integer>(overtrimmed_data.keySet());

        for(Integer key : keys){
            System.out.println(key+"\t" +overtrimmed_data.get(key));
        }

        System.out.println("Undertrimmed Data Length Distribution: \n");
        System.out.println("Size\tNumber\n");
        SortedSet<Integer> keysut = new TreeSet<Integer>(undertrimmed_data.keySet());

        for(Integer key : keysut){
            System.out.println(key+"\t" +undertrimmed_data.get(key));
        }




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
        if(args.length != 1) {//need contaminated fastq as input
            System.out.println("Need previously contaminated and clipped FastQ as entry");
            System.exit(0);
        } else {
            AdapterClippingEvaluator adapterClippingEvaluator = new AdapterClippingEvaluator(new File(args[0]));

        }
    }




}
