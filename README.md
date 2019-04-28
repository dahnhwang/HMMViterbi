# HMMViterbi
Use HMM/Viterbi algorithm to find out the POS tagger with the highest probability (Korean)
Last Version : 2019-04-08 (Mon)

# Input
A program given in the class proposes lots of possible combination of POS taggers(result.txt) of a sentence(input.txt).
Used both text files as input.

# Implementation
- run all the process in Main.java
- calculate HMM probability(transition & emission probability in BigramModel.java and EmissionProb.java)
- calculate the probability of each possible combination of POS taggers and choose the maximum(Viterbi.java)

# Output Example
=========================================================
26. 안녕/NNG+하/XSV+세/EC+요/JX (1.3408431095769626E-16)
=========================================================
=========================================================
 1. 너/NP+를/JKO (6.624871528766695E-6)
 2. 사랑/NNG+하/VV+어/EF+!/SF (1.9886268852557794E-12)
=========================================================
=========================================================
 1. 우리/NP+집/NNG+에/JKB (2.6366425567046166E-8)
 1. 왜/MAG (1.8208680002754855E-4)
 5. 오/VX+았/EP+니/EF+?/SF (3.5820256528122703E-9)
=========================================================
