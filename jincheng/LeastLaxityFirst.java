package com.suncong.os.jincheng;

import javafx.util.Pair;

import java.util.*;

/**
 * @author SunCong
 * @date 2022/11/23 0:20
 */
public class LeastLaxityFirst {

    /**
     * 最低松弛度的优先队列
     */
    public static Comparator<Pair<Integer, Integer>> laxityComparator = new Comparator<Pair<Integer, Integer>>() {
        @Override
        public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
            return o1.getKey() - o2.getKey();
        }
    };

    public static void main(String[] args) {
        int num = 5;

        // 到达时间，服务时间，优先级
        // 这个副本用来扣除已经服务过的时间，原来的serviceTime用来计算最后的一些数值
        int[] arrivalTime = {1, 2, 3, 5, 9};
        int[] serviceTime = {6, 4, 1, 1, 3};
        int[] serviceTimeCopy = {6, 4, 1, 1, 3};
        int[] deadline = {8, 9, 10, 12, 13};
        int[] laxity = {0, 0, 0, 0, 0};

        Queue<Pair<Integer, Integer>> runningProcesses = new PriorityQueue<>(7, laxityComparator);

        System.out.println("============================进程的信息 ==================================");
        // 打印各个进程排序后的编号、到达时间、服务时间、优先级
        System.out.println("进程\t\t到达时间\t\t服务时间\t\t截止日期");
        for (int i = 0; i < num; i++) {
            System.out.println((i + 1) + "\t\t" + arrivalTime[i] + "\t\t\t" + serviceTime[i] + "\t\t\t" + deadline[i]);
        }


        System.out.println("=========================================================================");

        System.out.println("============================ 执行过程 ====================================");

        // 一些进程运行时的参数
        // 以s为单位，逐渐递增的，方便统计时间
        int nowTime = 1;
        // int totTime = 0;   // 总时间 这个就不用定义了，就是当所有的进程都执行完了，最后的nowTime  从0s开始
        boolean isFinished = false;

        //运行
        while (true) {

            // 1.判断所有的进程是否结束了
            isFinished = true;
            for (int i = 0; i < num; i++) {
                if (serviceTimeCopy[i] > 0) {
                    isFinished = false;
                    break;
                }
            }
            if (isFinished) {
                break;
            }

            // 2.判断当前是否有线程进来
            //      有的话  放到优先队列里  他会自己排序 按照我们定义的cmp
            for (int i = 0; i < num; i++) {

                // 判断有没有过期，过期的话就踢掉
                if (deadline[i] < nowTime) {
                   List<Pair<Integer, Integer>> processes = new ArrayList<>();
                   while(!runningProcesses.isEmpty()){
                       processes.add(runningProcesses.poll());
                   }
                   for(Pair<Integer, Integer> pair: processes){
                       if(pair.getValue()==i){
                           System.out.println("进程" + (i + 1) + "在" + nowTime + "尚未完成，丢弃");
                           serviceTimeCopy[i]=0;
                       }else{
                           runningProcesses.add(pair);
                       }
                   }
                    // remove不带参数是返回对头元素，带参数是删掉这个参数

                }


                if (arrivalTime[i] == nowTime) {
                    // 松弛度=必须完成时间-其本身运行时间-当前时间
                    laxity[i] = deadline[i] - serviceTimeCopy[i] - nowTime;
                    //                                       松弛度     进程编号
                    Pair<Integer, Integer> pair = new Pair<>(laxity[i], i);
                    runningProcesses.add(pair);
                }

            }

            // 更新队列中的元素的松弛度
            //不要在foreach循环里进行元素的remove/add操作，remove元素请使用Iterator方式。
            // 如果松弛度小于0的话，就kick

            List<Pair<Integer, Integer>> processes = new ArrayList<>();

            // poll 获取队头元素并删除，并返回，失败时前者抛出null，再调整堆结构
            while (!runningProcesses.isEmpty()) {
                processes.add(runningProcesses.poll());
            }

            for (Pair<Integer, Integer> pair1 : processes) {

                int index = pair1.getValue();
                int value = deadline[index] - serviceTimeCopy[index] - nowTime;
                if (value >= 0) {
                    runningProcesses.add(new Pair<>(value, index));
                } else {
                    System.out.println("进程" + (index + 1) + "松弛度小于0，丢弃");
                    serviceTimeCopy[index]=0;
                }

            }


            // 3.优先队列最前面的一个进程serviceTimeCopy-1
            if (runningProcesses.size() > 0) {
                // 取出优先级最高的进程
                Pair<Integer, Integer> temp = runningProcesses.peek();
                // 取出进程编号，拿到编号即可索引全部信息
                int firstIndex = temp.getValue();
                serviceTimeCopy[firstIndex]--;
                // 打印当前时间
                System.out.println("第" + nowTime + "秒，正在执行" + (firstIndex + 1) + "号线程，剩余" + serviceTimeCopy[firstIndex] + "服务时间");

                // 3.1 该进程没有执行完

                // 3.2 该进程执行完了，计算他的周转时间和带权周转时间，踢出优先队列
                if (serviceTimeCopy[firstIndex] == 0) {
                    System.out.println("进程" + (firstIndex+1) + "在" + nowTime + "执行完成");
                    // kick
                    runningProcesses.remove();
                }
            }

            nowTime++;
        }

        System.out.println("所有进程均执行完毕，耗时" + nowTime);
    }

}
