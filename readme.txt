This Bisimulation can accept txt file in the given format only.
Compile and run this file, it will first ask for the file names of the input file one by one. After successful checking, it will ask for the name of the output txt file.
Input and Output files should be and will be in the same folder with the current working directory.

*********Some features of the codes********
1. The set is stored in the type of int[][], hashset is introduced only to remove duplicated numbers. 

2. Apart from the required methods, several methods are added for the algorithm. They are:

-public void matchPRemoveFromRou(String a)
Used to implemented the function of matchP. Here if matchP is not empty, the results will immediately be removed from Rou. This is slightly different from what was given in the lecture.

-public void split(String a)
Used to implemented the function of split(P,a,P1) and put the results into (int)splitP[][].

-public void addSplitPToRou()
-public void addSplitPToWaiting()
Add split results in splitP[][] back to Rou and Waiting. 

-public int[] chooseRemoveRou(int lineNum)
-public int[] chooseRemoveWaiting(int lineNum)
-public int[] chooseRemoveMatchP(int lineNum)
Chooses and removes one row at "lineNum”, then returns the row.

-public boolean hasTransition(int stateA, String action, int successorStateB)
Find if there exists a transition between states A and B using certain action. 

-public boolean belongR(int[] p)
Check if there has at least 2 elements in p such that 1 of them comes from P, and another comes from Q. This method is used to check the final results in Rou to decide whether P and Q are Bisimilar. 
(After I completed this method it came to my mind that we can alternately check whether the initial states of P and Q belongs to the same partition. However I sticked with my first solution).

3. The name of the output file does not need the extension. (if you type “result” then a file called “resulted.txt” will be generated).

4. A variable called maxNumInP which represents the maximum number(states) in P, is added to Q before performing the checking, to avoid duplicated numbers insides P and Q. 

