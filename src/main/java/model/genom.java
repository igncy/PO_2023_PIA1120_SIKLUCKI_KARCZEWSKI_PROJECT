package model;

import java.security.SecureRandom;

public class genom {
    private int[] arr;
    private int len;

    public genom(int[] arr) {
        this.arr = arr;
        this.len = arr.length;
    }

    public void mutation(boolean mutationType) {
        SecureRandom rand = new SecureRandom();
        int n = this.len;
        int no = rand.nextInt(n + 1);
        boolean[] vis = new boolean[n];

        for (int i = 0; i < no; i++) {
            int x = rand.nextInt(n);
            while (vis[x]) {
                x = rand.nextInt();
            }
            vis[x] = true;
        }

        for (int i = 0; i < n; i++) {
            if (vis[i]) {
                if (!mutationType) {
                    int k = rand.nextInt(8);
                    while (k == arr[i]) {
                        k = rand.nextInt(8);
                    }
                    this.arr[i] = k;
                } 
                else {
                    boolean k = rand.nextBoolean();
                    this.arr[i] = k ? (this.arr[i] + 1) % 8 : (this.arr[i] - 1) % 8;
                }
            }
        }
    }
}



