import java.util.Scanner;

public class OptimalStaticDistributedBST {
    // BST helper node data type
    public static class Node {
        private int key;            // key
        private Node left, right;   // left and right subtrees

        public Node(int key) {
            this.key = key;
        }
    }

    public Node root;
    /*
    Assuming that in the input nodeWeights[][]:
    nodeWeights[a][b]= freq(a,b) + freq(b,a) for all a<b
    i.e.
    nodeWeights[b][a]=0 if b>=a => nodeWeights is upper triangular matrix (with zero diagonal)
     */

    public void createBST(int[][] nodeWeights, Node[] allNodes) //Assuming all Nodes' keys will be integers; and all Nodes are sorted according to its key
    {
        int[][] subtrees_cost = new int[allNodes.length][allNodes.length];// subtree[start][start+diff]=Optimal cost of tree fo rmed by interval [start,start+diff]
        int[][] allRoots = new int[allNodes.length][allNodes.length];     // allRoots[start][start+diff] Stores index of most optimal Root for interval I=[start,start+diff]
        int[][] W = new int[nodeWeights.length][nodeWeights.length];      // Let I=[start,end] then,
        //W[start][end]= Σ ( Σ ( w(u,v)+w(v,u) ) for all u∈I(cap) ) for all v∈I
        //Loop to Calculate value of W
        for (int start = 0; start < allNodes.length; start++)                   //Keeping start of interval fixed, keep changing end of the interval
        {
            for (int end = start; end < allNodes.length; end++)      //I=[start,end]
            {
                if (end - 1 >= start) {
                    W[start][end] = W[start][end - 1];
                }
                for (int pointer = 0; pointer < start; pointer++) {   //pointer traverses everywhere in allNodes including I=[start,end]
                    W[start][end] = W[start][end] + nodeWeights[pointer][end];
                }
                for (int pointer = start; pointer <= end; pointer++) {
                    W[start][end] = W[start][end] - nodeWeights[pointer][end];
                }
                for (int pointer = end + 1; pointer < nodeWeights.length; pointer++) {
                    W[start][end] = W[start][end] + nodeWeights[end][pointer];
                }

            }
        }
        // W's calculated in O(n^3) time
        for (int diff = 0; diff < allNodes.length; diff++) {
            for (int start = 0; start < allNodes.length - diff; start++)   //I=[start,start+diff]
            {
                if (diff == 0) {
                    subtrees_cost[start][start] = 0;
                    allRoots[start][start] = start;
                } else if (diff == 1)        //2 Nodes
                {
                    /*
                    CaseAB:- A          CaseBA:- B
                              \                 /
                               B               A
                     */
                    int costCaseAB = 2 * nodeWeights[start][start + diff] + W[start + diff][start + diff] - nodeWeights[start][start + diff];
                    int costCaseBA = 2 * nodeWeights[start][start + diff] + W[start][start] - nodeWeights[start][start + diff];
                    if (costCaseAB <= costCaseBA) {
                        subtrees_cost[start][start + diff] = costCaseAB;
                        allRoots[start][start + diff] = start;
                    } else {
                        subtrees_cost[start][start + diff] = costCaseBA;
                        allRoots[start][start + diff] = start + diff;
                    }
                } else {
                    int min = Integer.MAX_VALUE;    //stores min cost till now
                    //I'=[start,root-1] && I"= [root+1,start+diff]
                    for (int root = start; root <= start + diff; root++)  //loop to find the best root
                    {
                        int cost = 0;
                        if (start <= root - 1) {
                            cost = cost + W[start][root - 1] + subtrees_cost[start][root - 1];
                        }
                        if (root + 1 <= start + diff) {
                            cost = cost + W[root + 1][start + diff] + subtrees_cost[root + 1][start + diff];
                        }
                        if (cost < min) {
                            min = cost;
                            allRoots[start][start + diff] = root;
                            subtrees_cost[start][start + diff] = cost;
                        }
                    }
                }
            }
        }
        //Printing the tree
        printtree(allNodes, allRoots, 0, allNodes.length - 1);
        // return subtrees_cost[0][allNodes.length-1];
    }

    //DFS printing, preorder printing
    public void printtree(Node[] allNodes, int[][] allRoots, int beg, int end) {
        if (beg > end) {
            System.out.print("null ");
            return;
        }
        System.out.print(allNodes[allRoots[beg][end]].key + " ");
        printtree(allNodes, allRoots, beg, allRoots[beg][end] - 1);
        printtree(allNodes, allRoots, allRoots[beg][end] + 1, end);
    }

    /*
    Input:-
    line 1: number of nodes in the input (say k)
    line 2: increasing order of k nodes' keys
    line 3-k:  k*k upper triangular matrix with matrix[a,b]=freq[a,b] and matrix[a,a]=0 for all 0<=a<k
    eg input:
    5
    1 2 3 4 5
    0 2 1 0 1
    0 0 2 2 3
    0 0 0 1 2
    0 0 0 0 3
    0 0 0 0 0
    expected output:
    1 null 2 null 4 3 null null 5 null null
    corresponding tree:
        1
         \
          2
           \
            4
           / \
          3   5
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Node allNodes[] = new Node[s.nextInt()];
        for (int i = 0; i < allNodes.length; i++) {
            allNodes[i] = new Node(s.nextInt());
        }
        int[][] nodeWeights = new int[allNodes.length][allNodes.length];
        for (int i = 0; i < allNodes.length; i++) {
            for (int j = 0; j < allNodes.length; j++) {
                nodeWeights[i][j] = s.nextInt();
            }
        }
        OptimalStaticDistributedBST obj = new OptimalStaticDistributedBST();
        obj.createBST(nodeWeights, allNodes);
    }
}
