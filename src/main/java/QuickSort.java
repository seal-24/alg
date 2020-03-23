import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

public class QuickSort {
    static ExecutorService pool = Executors.newFixedThreadPool(10);
    public static void main(String[] args) {
        int[] nums = {5,4,1,3,2};
        quickSortMutilThread(nums,0,4);
        System.out.println(Arrays.toString(nums));
        pool.shutdown();

    }


    static void quickSortMutilThread(final int[] nums,final int left,final int right){
        if(left < right){
            final int mid = partition(nums,left,right);
            //CountDownLatch a = new CountDownLatch(2);

            Future<Integer> retLeft = pool.submit(new Callable<Integer>() {
                    public Integer call() throws Exception {
                        quickSort(nums,left,mid-1);
                        return 0;
                    }

            });
            Future<Integer> retRight = pool.submit(new Callable<Integer>() {
                public Integer call() throws Exception {
                    quickSort(nums,mid + 1,right);
                    return 0;
                }

            });
            try {
                retLeft.get();
                retRight.get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

    }



    static void quickSort(final int[] nums,final int left,final int right){
        if(left < right){
            final int mid = partition(nums,left,right);
            //CountDownLatch a = new CountDownLatch(2);
            quickSort(nums,left,mid-1);
            quickSort(nums,mid + 1,right);


        }

    }

    static  int partition(int[] nums,int left, int right){
        int  random = new Random(System.currentTimeMillis()).nextInt(right - left + 1)+left;
        swap(nums,left,random);
        int pivot = nums[left];
        int i = left ,j = right;
        while(i < j ){
            while (i < j  && nums[j] >= pivot){
                j--;
            }
            if(i < j){
                nums[i++] = nums[j];
            }

            while (i < j  && nums[i] <= pivot){
                i++;
            }
            if(i < j){
                nums[j--] = nums[i];
            }

        }

        nums[i] = pivot;
        return  i ;


    }

    static void swap(int [] nums, int A, int B){
        int temp = nums[A];
        nums[A] = nums[B];
        nums[B] = temp;
    }
}
