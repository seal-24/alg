import javax.sound.midi.Soundbank;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

public class Test {
    static Integer a = 100;
    static Random r = new Random();
    static ConcurrentHashMap<Integer,Integer> writingQ = new ConcurrentHashMap();
    static ConcurrentLinkedQueue<Integer> waitingQ = new ConcurrentLinkedQueue<Integer>();
    static AtomicLong qCounter = new AtomicLong();

    static Integer total = 100000;
    static AtomicLong globalCounter = new AtomicLong(total);
    static AtomicLong rowMaxCounter = new AtomicLong(total);

    static Integer randomMax = 100000;
    static Integer sleepNs = 100;
    static AtomicLong lockCount =  new AtomicLong(0);


    public static synchronized void globalAdd(ConcurrentHashMap<Integer,Integer> Map,Integer key) throws Exception{

        if(Map.containsKey(key)){
            int v = Map.get(key);
            int newV = v+1;
            Map.put(key,newV);
        }else{
            Map.put(key,1);
        }
        Thread.sleep(0,sleepNs);

    }

    public static  void rowAdd(ConcurrentHashMap<Integer,Integer> Map,Integer key) throws Exception{

        if(writingQ.containsKey(key)){
            lockCount.getAndIncrement();
//            r(Integer k:Map.keySet()){
//                System.out.println(  "map kv "+k+ ":"+Map.get(k));
//            }
            System.out.println( "lockCnt " + lockCount.get());
            waitingQ.add(key);
        }else {

          //  System.out.println(  "writing key:"+key+ ":threadid "+Thread.currentThread().getId());
            synchronized (a) {
                if (!writingQ.contains(key)) {
                    writingQ.put(key, key);
                }
            }
//            synchronized (a) {
//                if (writingQ.contains(key)) {
//                    writingQ.put(key, 1);
//                    return;
//                }
//            }

            if(Map.containsKey(key)){
                int v = Map.get(key);
                int newV = v+1;
                Map.put(key,newV);
            }else{
                Map.put(key,1);
            }
            //System.out.println(key + ":"+Map.get(key));
            Thread.sleep(0,sleepNs);
            writingQ.remove(key);
           // System.out.println(  "remove key done:"+key+ ":threadid "+Thread.currentThread().getId());

        }


    }

    public static void pollQueue(ConcurrentHashMap<Integer,Integer> Map) throws  Exception{
        while (true) {
            while (!waitingQ.isEmpty()) {
                Integer k = waitingQ.poll();
             //   System.out.println(  "poll key:"+k+ ":threadid "+Thread.currentThread().getId());
                rowAdd(Map, k);
            }
            if(rowMaxCounter.get() <= 0 ){
                System.out.println(  "poll exit:"+Thread.currentThread().getId());
                return;
            }
            Thread.sleep(2);
        }
    }


    static void testGlobal(Integer threads) throws Exception {
        final ConcurrentHashMap<Integer, Integer> Map = new ConcurrentHashMap<Integer, Integer>();
        globalCounter = new AtomicLong(total);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Long start = System.currentTimeMillis();
        List<Future<Integer>> lists = new LinkedList<Future<Integer>>();
        for (int i = 0; i < threads; i++) {
            Future<Integer> f = executorService.submit(new Callable<Integer>() {
                public Integer call() {
                    while (globalCounter.getAndDecrement() > 0) {
                        Integer k = r.nextInt(randomMax);
                        try {
//                            synchronized (this){
//                                globalAdd(Map, k);
//                            }
                            globalAdd(Map, k);
                        } catch (Exception e) {

                        }

                    }
                    return 0;
                }
            });
            ((LinkedList<Future<Integer>>) lists).addLast(f);

        }

        for (Future<Integer> f : lists) {

            Integer r = f.get();
        }
        Long time = System.currentTimeMillis() - start;
        System.out.println("global cost:"+time + " qps:"+(total*1.0*1000)/time);
        System.out.println("shut down");
        executorService.shutdown();
        System.out.print("main");
        System.out.println("shut down");
        executorService.shutdown();
        System.out.print("main");
    }



    static void testRow(Integer threads) throws Exception {
        final ConcurrentHashMap<Integer, Integer> Map = new ConcurrentHashMap<Integer, Integer>();

        Long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        rowMaxCounter =  new AtomicLong(total);
        List<Future<Integer>> lists = new LinkedList<Future<Integer>>();
        for (int i = 0; i < threads; i++) {
            Future<Integer> f = executorService.submit(new Callable<Integer>() {
                public Integer call() {
                    while (rowMaxCounter.getAndDecrement() > 0){
                        Integer k = r.nextInt(randomMax);
                        //System.out.println("random key:"+k + ":"+Thread.currentThread().getId());
                        try {
//                            synchronized (this){
//                                globalAdd(Map, k);
//                            }
                            Long c = rowMaxCounter.get();
                            if( c%1000 == 0){
                                System.out.println("couner:"+c);

                            }
                            rowAdd(Map, k);
                        } catch (Exception e) {

                        }

                    }
                    System.out.println("exit: " +Thread.currentThread().getId());
                    return 0;
                }
            });
            ((LinkedList<Future<Integer>>) lists).addLast(f);

        }
        Future<Integer> fpoll = executorService.submit(new Callable<Integer>() {
            public Integer call() {
                try {
                    pollQueue(Map);
                }catch (Exception e){

                }
                return 0;
            }
        });
        ((LinkedList<Future<Integer>>) lists).addLast(fpoll);


        for (Future<Integer> f : lists) {
            Integer r = f.get();
        }
        Long time = System.currentTimeMillis() - start;
        System.out.println("row cost:"+time + " qps:"+(total*1.0*1000)/time);
        System.out.println("shut down");
        executorService.shutdown();
        System.out.print("main");
    }
    public static void main(String[] args) throws  Exception{

        System.out.println(5/2.0);
//        int [] threads= {7};
//////        for(int n:threads) {
//////            testGlobal(n);
//////        }
////        for(int n:threads) {
////            testRow(n);
////        }

    }





}
