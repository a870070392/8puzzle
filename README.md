# 8 puzzle problem
first please refer to [http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html] for problem specifications and [http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html] for checklist  

## basic idea:  
**How can I reduce the amount of memory a Board uses?** Recall that an n-by-n int[][] array in Java uses about 24 + 32n + 4n^2 bytes; when n equals 3, this is 156 bytes. To save memory, consider using an n-by-n char[][] array or a length n^2 char[] array. You could use a more elaborate representation: since each board is a permutation of length n^2, in principle, you need only about lg ((n^2)!) bits to represent it; when n equals 3, this is only 19 bits.  
From the above inspiration, I managed to use a few bits to represent each n-by-n array, the key point lies in how to design a 1-to-1 mapping function from each array to a sequence of bits of smallest length possible without extra memory usage.  
My retrieve approach is  
1.converting sequence of bits to decimal number i  
2.suppose we treat every board as a base-n^2 permutation of length n^2, in other word, flatten into a 1-d array, we can find the ith smallest permutation by progressively figuring out each digit in the number starting from the left. And that's the permutation corresponding to i.  
3.transform the permutation back to an n-by-n array  
The compression approach roughly follows the same idea, we can calculate the rank of a base-n^2 number of length n^2 among all permutation possibilities, and then convert it to sequence of bits.  

**see board.java for detailed implementation**
