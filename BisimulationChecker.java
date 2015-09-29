//Implemented by Jamie Zhang, UPI: mzha273
import java.io.*;
import java.util.*;

public class BisimulationChecker {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BisimulationChecker c = new BisimulationChecker();
		
		System.out.println("Please enter the file names one by one \nfirst file:");
		//input
		Scanner s = new Scanner(System.in);
		String fileP=s.next();
		System.out.println("second file:");
		String fileQ=s.next();
		
		c.readInput(fileP, fileQ);
		c.performBisimulation();
		System.out.println("Checking Finished! Please enter the name for the output txt file (WITHOUT extension), file with the same name will be overwritten:");
		String outputFile=s.next();
		c.writeOutput(outputFile);
		s.close();
		System.out.println("Output finished!");
		// below is for screen printing
		/*System.out.print("Process P\nS=");
		Iterator<Integer> sP = c.setStateP.iterator();
		while (sP.hasNext()) {
			System.out.print(sP.next() + ", ");
		}
		System.out.print("\b\b \nA=");
		Iterator<String> aP = c.setActionP.iterator();
		while (aP.hasNext()) {
			System.out.print(aP.next() + ", ");
		}
		System.out.print("\b\b \nT=");

		for (int p = 0; p < c.stateP.length; p++) {
			System.out.print("(" + c.stateP[p] + ", " + c.actionP[p] + ", " + c.statePf[p] + ")" + ", ");
		}

		System.out.print("\b\b \nProcess Q\nQ=");
		Iterator<Integer> sQ = c.setStateQ.iterator();
		while (sQ.hasNext()) {
			System.out.print(sQ.next() + ", ");
		}
		System.out.print("\b\b \nA=");
		Iterator<String> aQ = c.setActionQ.iterator();
		while (aQ.hasNext()) {
			System.out.print(aQ.next() + ", ");
		}
		System.out.print("\b\b \nT=");

		for (int p = 0; p < c.stateQ.length; p++) {
			System.out.print("(" + c.stateQ[p] + ", " + c.actionQ[p] + ", " + c.stateQf[p] + ")" + ", ");
		}
		System.out.print("\b\b \nBisimulation Results\n");
		boolean simulationResult = true;

		for (int p = 0; p < rou.length; p++) {
			for (int q = 0; q < rou[p].length; q++) {
				if (rou[p].length == 1)
					simulationResult = false;
				System.out.print(rou[p][q] + ", ");
			}
			System.out.print("\b\b \n");
		}

		System.out.println("Bisimulation Answer");
		if (simulationResult)
			System.out.println("YES");
		else
			System.out.println("NO");*/

	}

	public BisimulationChecker() {
	} // constructor

	protected int lineCountP = 0;
	protected int lineCountQ = 0;

	protected int[] stateP;
	protected int[] statePf;
	protected String[] actionP;
	
	protected int maxNumInP = 0; //avoid same state name in P&Q

	protected int[] stateQ;
	protected int[] stateQf;
	protected String[] actionQ;
	

	protected Set<Integer> setStateP = new HashSet<Integer>();
	protected Set<Integer> setStateQ = new HashSet<Integer>();
	protected Set<Integer> setState = new HashSet<Integer>();

	protected Set<String> setActionP = new HashSet<String>();
	protected Set<String> setActionQ = new HashSet<String>();
	protected Set<String> setAction = new HashSet<String>();

	static int[][] rou1; // ρ1
	static int[][] rou; // ρ
	static int[][] waiting;

	static String[] A;
	static int[] P;
	static int[] P1; // P'
	static int[][] splitP = new int[2][];
	static int[][] matchP;

	// matchP using a, remove and put the results into matchP[][];
	public void matchPRemoveFromRou(String a) {
		// System.out.println("matchP using "+a);
		int numOfMatch = 0;
		int lineAtMatchP = 0;
		int lineAtRou = 0;
		int hasMatch = 0;
		matchP = rou;
		int matchPLen = matchP.length;
		for (int i = 0; i < matchPLen; i++) {
			numOfMatch = 0;
			for (int j = 0; j < matchP[lineAtMatchP].length; j++) {
				hasMatch = 0;
				for (int k = 0; k < P1.length; k++) {
					if (hasTransition(matchP[lineAtMatchP][j], a, P1[k]))
						hasMatch = 1;
				}
				if (hasMatch == 1)
					numOfMatch++;
			}
			// remove lines that dont have match
			if ((numOfMatch == 0) || (numOfMatch == matchP[lineAtMatchP].length)) {
				chooseRemoveMatchP(lineAtMatchP--);
			} else
				chooseRemoveRou(lineAtRou--);
			lineAtMatchP++;
			lineAtRou++;
		}
	}

	// split(P,a,P1) and put the results into splitP[][]
	public void split(String a) {
		int lP = 0, lP1 = 0;
		int lPHasBeenRemoved = 0;
		Set<Integer> splitP1 = new HashSet<Integer>(); // can be split
		Set<Integer> splitP2 = new HashSet<Integer>();
		for (lP = 0; lP < P.length; lP++) {
			lPHasBeenRemoved = 0;
			for (lP1 = 0; lP1 < P1.length; lP1++) {
				if (hasTransition(P[lP], a, P1[lP1])) {
					splitP1.add(P[lP]);
					lPHasBeenRemoved = 1;
					break;
				}
			}
			if (lPHasBeenRemoved == 0)
				splitP2.add(P[lP]);
		}
		int[] splitResult1 = new int[splitP1.size()];
		int[] splitResult2 = new int[splitP2.size()];
		Iterator<Integer> toSplitP1 = splitP1.iterator();
		int i = 0;
		while (toSplitP1.hasNext()) {
			splitResult1[i++] = toSplitP1.next();
		}
		i = 0;
		Iterator<Integer> toSplitP2 = splitP2.iterator();
		while (toSplitP2.hasNext()) {
			splitResult2[i++] = toSplitP2.next();
		}
		splitP[0] = splitResult1;
		splitP[1] = splitResult2;
	}

	// add split results back to rou and waiting
	public void addSplitPToRou() {
		int[][] temp = rou;
		rou = new int[temp.length + 2][];
		int i = 0;
		for (i = 0; i < temp.length; i++) {
			rou[i] = temp[i];
		}
		rou[i++] = splitP[0];
		rou[i] = splitP[1];
	}

	public void addSplitPToWaiting() {
		// System.out.println("from addsp to waiting");
		int[][] temp = waiting;
		waiting = new int[temp.length + 2][];
		int i = 0;
		for (i = 0; i < temp.length; i++) {
			waiting[i] = temp[i];
		}
		waiting[i++] = splitP[0];
		waiting[i] = splitP[1];
	}

	//
	public void performBisimulation() {
		// initialize sets
		int[] a;
		int i = 0;
		setState.addAll(setStateP);
		setState.addAll(setStateQ);
		a = new int[setState.size()];

		Iterator<Integer> itS = setState.iterator();
		while (itS.hasNext()) {
			a[i++] = itS.next();
		}
		setAction.addAll(setActionP);
		setAction.addAll(setActionQ);
		i = 0;

		A = new String[setAction.size()];
		Iterator<String> itA = setAction.iterator();
		while (itA.hasNext()) {
			A[i++] = itA.next();
		}

		rou1 = new int[1][];
		waiting = new int[1][];
		rou = new int[1][];

		rou1[0] = a;
		rou = rou1;
		waiting[0] = a;
		// loop until waiting is empty
		while (waiting.length != 0) {
			/*
			 * System.out.println("waiting is:"); for(int
			 * p=0;p<waiting.length;p++){ for(int w=0;w<waiting[p].length;w++)
			 * System.out.print(waiting[p][w]+", "); }
			 */
			P1 = chooseRemoveWaiting(0);
			for (int b = 0; b < A.length; b++) { // for each action
				matchPRemoveFromRou(A[b]); // matchP using A[b]
				if (matchP.length != 0)
					for (int c = 0; c < matchP.length; c++) {
						P = matchP[c];
						split(A[b]);
						addSplitPToRou();
						addSplitPToWaiting();
					}
			}

		}
	}

	// choose and remove one line at "lineNum"
	public int[] chooseRemoveRou(int lineNum) {
		int[][] temp = rou;
		int[] choose;
		choose = rou[lineNum];
		// System.out.println("rou.len before remove "+rou.length );
		rou = new int[rou.length - 1][];

		for (int j = 0, k = 0; j < (rou.length); j++, k++) {
			if (k == lineNum)
				k++;
			rou[j] = temp[k];
		}
		// System.out.println("set.len after remove "+rou.length );
		return choose;
	}

	public int[] chooseRemoveWaiting(int lineNum) {

		int[][] temp = waiting;
		int[] choose;
		choose = waiting[lineNum];
		waiting = new int[waiting.length - 1][];

		for (int j = 0, k = 0; j < (waiting.length); j++, k++) {
			if (k == lineNum)
				k++;
			waiting[j] = temp[k];
		}
		return choose;
	}

	public int[] chooseRemoveMatchP(int lineNum) {
		int[][] temp = matchP;
		int[] choose;
		choose = matchP[lineNum];
		matchP = new int[matchP.length - 1][];

		for (int j = 0, k = 0; j < (matchP.length); j++, k++) {
			if (k == lineNum)
				k++;
			matchP[j] = temp[k];
		}
		return choose;
	}

	// find transition pairs
	public boolean hasTransition(int stateA, String action, int successorStateB) {
		boolean hasT = false;
		// check P
		for (int d = 0; d < lineCountP; d++) {
			if ((stateP[d] == stateA) && (statePf[d] == successorStateB) && actionP[d].equals(action)) {
				hasT = true;
				break;
			}
		}
		if (hasT == true)
			return hasT;
		// check Q
		for (int d = 0; d < lineCountQ; d++) {
			if ((stateQ[d] == stateA) && (stateQf[d] == successorStateB) && actionQ[d].equals(action)) {
				hasT = true;
				break;
			}
		}
		return hasT;
	}
	/*
	 * //check if stateA has action public boolean hasAction(int stateA, String
	 * action){ boolean hasA = false; //check P for(int d=0; d<lineCountP; d++){
	 * if((stateP[d]==stateA)&&actionP[d].equals(action)){ hasA=true; break; } }
	 * if(hasA==true)return hasA; //check Q for(int d=0; d<lineCountQ; d++){
	 * if((stateQ[d]==stateA)&&actionQ[d].equals(action)){ hasA=true; break; } }
	 * return hasA; }
	 */

	// read data
	public void readInput(String fileP, String fileQ) {
		Scanner s = new Scanner(System.in);
		while (true) {
		File srcFileP = new File(fileP);
		File srcFileQ = new File(fileQ);
		int i = 0;
			try {
				FileInputStream sourceP = new FileInputStream(srcFileP);
				FileInputStream sourceQ = new FileInputStream(srcFileQ);

				BufferedReader Pr = new BufferedReader(new InputStreamReader(sourceP));
				BufferedReader Qr = new BufferedReader(new InputStreamReader(sourceQ));

				// firstly process P
				String line = Pr.readLine();
				// scan file and get total lines
				while (!line.equals("!")) {
					lineCountP++;
					line = Pr.readLine();
				}
				Pr.close();
				// initialize 3 arrays to represent P
				stateP = new int[lineCountP];
				statePf = new int[lineCountP];
				actionP = new String[lineCountP];
				// proceed read data
				sourceP = new FileInputStream(srcFileP);
				Pr = new BufferedReader(new InputStreamReader(sourceP));
				line = Pr.readLine();
				// read states and actions
				while (!line.equals("!")) {
					// System.out.println(line);
					String temp[] = new String[3];
					temp = line.split(",|:");
					// System.out.println(temp[0]);
					stateP[i] = Integer.parseInt(temp[0]);
					statePf[i] = Integer.parseInt(temp[2]);
					actionP[i] = temp[1];
					i++;
					line = Pr.readLine();
				}
				// add to set
				for (i = 0; i < lineCountP; i++) {
					setStateP.add(stateP[i]);
					setStateP.add(statePf[i]);
					setActionP.add(actionP[i]);
				}
				Pr.close();
				
				//find the max num in P, then add this num to Q to avoid name conflict
				Iterator<Integer> findMax = setStateP.iterator();
				int maxTemp;
				while (findMax.hasNext()) {
					maxTemp=findMax.next();
					if(maxNumInP<maxTemp)
					maxNumInP = maxTemp;
				}
				//System.out.println("Max Num In P is "+maxNumInP);

				// then process Q
				line = Qr.readLine();
				// scan file and get total lines
				while (!line.equals("!")) {
					lineCountQ++;
					line = Qr.readLine();
				}
				Qr.close();
				// initialize 3 arrays to represent Q
				stateQ = new int[lineCountQ];
				stateQf = new int[lineCountQ];
				actionQ = new String[lineCountQ];
				// proceed read data
				sourceQ = new FileInputStream(srcFileQ);
				Qr = new BufferedReader(new InputStreamReader(sourceQ));
				line = Qr.readLine();
				// read states and actions
				i = 0;
				while (!line.equals("!")) {
					// System.out.println(line);
					String temp[] = new String[3];
					temp = line.split(",|:");
					// System.out.println(temp[0]);
					//avoid name conflict by adding (maxNumInP+1)
					stateQ[i] = Integer.parseInt(temp[0])+maxNumInP+1;
					stateQf[i] = Integer.parseInt(temp[2])+maxNumInP+1;
					actionQ[i] = temp[1];
					i++;
					line = Qr.readLine();
				}
				// add to set
				for (i = 0; i < lineCountQ; i++) {
					setStateQ.add(stateQ[i]);
					setStateQ.add(stateQf[i]);
					setActionQ.add(actionQ[i]);
				}
				Qr.close();
				//System.out.println("Scanner closed");
				break;
				// }
			}catch(Exception e){
				System.out.println("Invalid filenames. Please enter the correct file names one by one\nfirst file:");
				//input
				fileP=s.next();
				System.out.println("second file:");
				fileQ=s.next();
			};
		} // end of while

	}
	
	//check if the relation belongs to R
	public boolean belongR(int[] p){
		boolean flagP=false;
		boolean flagQ=false;
		
		boolean tempP=false;
		boolean tempQ=false;
		int count = 0;
		
		for(int c=0;c<p.length;c++){
			tempP=false;
			tempQ=false;
			//System.out.println("check "+"p"+c);
			//check p
			for(int c1=0;c1<stateP.length;c1++){
				if(p[c]==stateP[c1]){
					tempP=true;
					//System.out.println("p"+c+" found in stateP: "+stateP[c1] +"; c1= "+c1);
					break;
				}
			}
			if(tempP!=true){
				for(int c2=0;c2<statePf.length;c2++){
					if(p[c]==statePf[c2]){
						tempP=true;
					//System.out.println("p"+c+" found in statePf: "+statePf[c2]);
					break;
					}
				}
			}
			if(tempP==false){flagQ=true;}
			else
				flagP=true;
			}
			
		if(flagP==true&&flagQ==true)
			return true;
		else
			return false;
	}
	//
	public void writeOutput(String filename) {
		String outputFile=filename;
		String spr = System.getProperty("line.separator");
		Scanner s = new Scanner(System.in);
		int commaFlag = 0;
		FileWriter fw;
		while (true) {
			try {
				fw = new FileWriter(filename+".txt", false);
				fw.write("Process P"+spr+"S=");
				Iterator<Integer> sP = setStateP.iterator();
				
				while (sP.hasNext()) {
					if(0!=commaFlag++)
						fw.write(", ");
					fw.write(sP.next()+"");
				}
				fw.write(spr+"A=");
				Iterator<String> aP = setActionP.iterator();
				commaFlag=0;
				while (aP.hasNext()) {
					if(0!=commaFlag++)
						fw.write(", ");
					fw.write(aP.next() +"");
				}
				fw.write(spr+"T=");

				for (int p = 0; p < stateP.length; p++) {
					fw.write("(" + stateP[p] + ", " + actionP[p] + ", " + statePf[p] + ")");
					if(p!=stateP.length-1)
						fw.write(", ");
				}

				fw.write(spr+"Process Q"+spr+"Q=");
				Iterator<Integer> sQ = setStateQ.iterator();
				commaFlag=0;
				while (sQ.hasNext()) {
					if(0!=commaFlag++)
						fw.write(", ");
					fw.write(sQ.next()-maxNumInP-1 + "");
				}
				fw.write(spr+"A=");
				Iterator<String> aQ = setActionQ.iterator();
				commaFlag=0;
				while (aQ.hasNext()) {
					if(0!=commaFlag++)
						fw.write(", ");
					fw.write(aQ.next() +"");
				}
				fw.write(spr+"T=");

				for (int p = 0; p < stateQ.length; p++) {
					fw.write("(" + (stateQ[p]-maxNumInP-1) + ", " + actionQ[p] + ", " + (stateQf[p]-maxNumInP-1) + ")");
					if(p!=stateQ.length-1)fw.write(", ");
				}
				fw.write(spr+"Bisimulation Results"+spr);
				boolean simulationResult = true;

				for (int p = 0; p < rou.length; p++) {
					//check every partition in rou has elements in P and Q
					//System.out.println("check "+p+" in rou;"+"rou[p].length= "+rou[p].length);
					if(!belongR(rou[p]))simulationResult = false;
					
					for (int q = 0; q < rou[p].length; q++) {
						if(rou[p][q]>maxNumInP){fw.write((rou[p][q]-maxNumInP-1)+"");}
						else
							fw.write(rou[p][q]+"");
						if(q!=(rou[p].length-1))
							fw.write(", ");
					}
					fw.write(spr);
				}

				fw.write("Bisimulation Answer"+spr);
				if (simulationResult)
					fw.write("YES"+spr);
				else
					fw.write("NO"+spr);
				fw.close();
				//Process p = new ProcessBuilder("open", outputFile+".txt").start();
				break;
			} catch (IOException e) {
				System.out.println("Failed to creat output file, please enter again with a writable path(e.g. D:\\result)");
				outputFile=s.next();
			}
		}

	}
}
