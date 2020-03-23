import javax.swing.tree.TreeNode;
import java.util.*;

import static java.lang.Math.max;

class Solution {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid.length;
        int n = obstacleGrid[0].length;
        int[][] dp = new int[m][n] ;  //初始化为0
        for(int i = 0 ; i < m;i ++){
            Arrays.fill(dp[i],0);
        }


        for(int i = 0 ; i< m ; i ++){
            if(obstacleGrid[i][0]  == 1){
                break;
            }
            dp[i][0] = 1;
        }

        for(int j = 0 ; j< n ; j ++){
            if(obstacleGrid[0][j]  == 1){
                break;
            }
            dp[0][j] = 1;
        }


        for(int i = 1 ; i < m ; i++)
            for(int j = 1 ; j < n ; j ++ ){
                if(obstacleGrid[i][j] == 0 ){
                    dp[i][j] = 0 ;
                }else{
                    dp[i][j] =  dp[i-1][j] + dp[i][j-1];
                }

            }
        return dp[m-1][n-1] ;
    }


    public boolean canJump(int[] nums) {
        int n = nums.length;
        int max_t = 0;
        for(int i = 0 ; i < n ;i ++){
            max_t = max(nums[i]+i,max_t);
            if(max_t == i  && nums[i] == 0){
                return false;
            }
        }

        return max_t >= n -1;
    }

    public String minWindow(String s, String t) {
        int need[] = new int[128];
        int window[] = new int[128];
        for(int i = 0 ;i < t.length() ;i++){
            need[t.charAt(i)] += 1;
        }
        int l = 0 ;
        int r = 0 ;
        int c = 0 ;
        int minLen = Integer.MAX_VALUE;
        String ret = null;
        while(r < s.length()){
            char temp = s.charAt(r);
            window[temp] += 1;
            if(need[temp] > 0  && window[temp] <= need[temp]) {
                c += 1;
            }
            while(c == t.length()){
                int tempLen = r - l + 1;
                if(tempLen <= minLen){
                    minLen = tempLen;
                    ret = s.substring(l,r-l+1);
                }
                char leftPopTemp = s.charAt(l);
                if(need[leftPopTemp] > 0 && window[leftPopTemp] <= need[leftPopTemp]){
                    c --;
                }
                window[leftPopTemp] -= 1;
                l++;
            }

            r ++ ;

        }

        return ret;

      }


    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    public ListNode reverseKGroup(ListNode head, int k) {
        if(head == null || head.next == null){
            return head;
        }

        ListNode cur = head;
        int c = k - 1;
        while(c > 0 && cur.next != null){
            cur = cur.next;
            c --;
        }
        if(c>0){ //不足k个
            return head;
        }
        else{
            ListNode nextHead = cur.next;
            cur.next = null;
            ListNode newHead = reverse(head);
            head.next = reverseKGroup(nextHead,k);
            return newHead;
        }

    }

    ListNode reverse(ListNode head){
        if(head == null || head.next == null){
            return head;
        }
        ListNode p1 = head;
        ListNode p2 = head.next;
        while(p2 != null ){
            ListNode p3 = p2.next;
            p2.next = p1;
            p1 = p2;
            p2 = p3;
        }

        ListNode ret = p1;
        return ret;

    }





    public static void main(String[] args) {

        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        for(int i = 0 ; i < 100;i++) {
            int randomNum = rand.nextInt(3);
            System.out.println(randomNum);

        }

        Solution solution = new Solution();
        int[][] nums = new int[][]{{0,0,0},{0,1,0},{0,0,0}};
        int []ns = {3,2,1,0,4};
        String S = "ADOBECODEBANC";
        String T = "ABC";
        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(3);
        ListNode node4 = new ListNode(4);
        ListNode node5 = new ListNode(5);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        ListNode node = solution.reverseKGroup(node1,3);

        //System.out.println(ret);
        //System.out.println(ret);
    }
}