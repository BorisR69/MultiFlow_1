import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        int max = 0;
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time
        List<FutureTask<Integer>> listFuture = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (String text : texts) {
            // Создаем задачу для запуска в потоке
            Callable run = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(Thread.currentThread().getName() + " " + text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            FutureTask<Integer> task = new FutureTask<Integer>(run); // Создаем поток
            listFuture.add(task);
        }

        for (FutureTask futureTask : listFuture) {
            new Thread(futureTask).start();
        }

        for (FutureTask futureTask : listFuture) {
            int curMax = (Integer) futureTask.get();
            if (curMax > max) {
                max = curMax;
            }
        }

        System.out.println("Max = " + max);
        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

}