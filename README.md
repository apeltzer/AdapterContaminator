# AdapterContaminator

Version 0.1

Small tool to simulate AdapterContamination of NGS reads.

Usage:

`java -jar AdapterContaminator.jar input.fq 75 "AGGGAAATT"`


adds randomly adapter fragments of length n = $size("AGGGAAATT") to your sequencing reads, 
replacing n bases with adapter sequence. 
Output is written to a input.fq.ACcont.fq file, which can then be processed with your adapter clipping tool. 

Current version can only simulate perfect adapters of variable size, we might include sequencing erors to adapter sequences in upcoming
versions, too. 

