# Java Interview Questions — Coding and Problem Solving

[Back to index](index.md)

**Target:** Senior Java developer / tech lead  
**Language:** Simple English; solutions focus on clear reasoning and trade-offs.  
**Format:** Every prompt is followed by an answer. Java snippets use broadly familiar Java syntax; check the target JDK for APIs newer than Java 8.

Current coding-preparation lists repeatedly cover arrays and hashing, two pointers, sliding windows, binary search, linked lists, stacks and queues, trees, graphs, heaps, intervals, backtracking, dynamic programming, and complexity analysis. Senior Java interviews also test readable code, edge cases, API choice, and production judgment. These are original prompts and solutions, not copied problem statements.

## Problem-solving method

For each coding question, practice this sequence:

1. Clarify inputs, outputs, constraints, and edge cases.
2. State a simple correct approach before optimizing it.
3. Explain the chosen data structure and algorithm.
4. Write readable Java and test a normal case plus boundary cases.
5. State time and space complexity.
6. Discuss what changes at production scale or with concurrent access.

## Question index

1. [What does Big-O describe?](#q1-what-does-big-o-describe)
2. [What is the difference between O(1), O(log n), O(n), O(n log n), and O(n²)?](#q2-what-is-the-difference-between-o1-olog-n-on-on-log-n-and-on2)
3. [What is the difference between time complexity and space complexity?](#q3-what-is-the-difference-between-time-complexity-and-space-complexity)
4. [How do you analyze two nested loops over an array?](#q4-how-do-you-analyze-two-nested-loops-over-an-array)
5. [What is amortized complexity?](#q5-what-is-amortized-complexity)
6. [How do you choose between a simple solution and a more complex optimization?](#q6-how-do-you-choose-between-a-simple-solution-and-a-more-complex-optimization)
7. [Given an integer array and a target, find two distinct indices whose values sum to the target.](#q7-given-an-integer-array-and-a-target-find-two-distinct-indices-whose-values-sum-to-the-target)
8. [How do you find whether an array contains a duplicate?](#q8-how-do-you-find-whether-an-array-contains-a-duplicate)
9. [How do you find the first non-repeating character in a string?](#q9-how-do-you-find-the-first-non-repeating-character-in-a-string)
10. [How do you check whether two strings are anagrams?](#q10-how-do-you-check-whether-two-strings-are-anagrams)
11. [How do you reverse a string in Java?](#q11-how-do-you-reverse-a-string-in-java)
12. [How do you find the longest common prefix in a list of strings?](#q12-how-do-you-find-the-longest-common-prefix-in-a-list-of-strings)
13. [How do you move all zeroes to the end while preserving the order of other values?](#q13-how-do-you-move-all-zeroes-to-the-end-while-preserving-the-order-of-other-values)
14. [How do you rotate an array right by k positions in-place?](#q14-how-do-you-rotate-an-array-right-by-k-positions-in-place)
15. [How do you find the missing number from values 0 through n when one is absent?](#q15-how-do-you-find-the-missing-number-from-values-0-through-n-when-one-is-absent)
16. [How do you merge two sorted arrays?](#q16-how-do-you-merge-two-sorted-arrays)
17. [How do you find the maximum subarray sum?](#q17-how-do-you-find-the-maximum-subarray-sum)
18. [How do you find the product of all array values except the value at each index without division?](#q18-how-do-you-find-the-product-of-all-array-values-except-the-value-at-each-index-without-division)
19. [How do you find the majority element that appears more than half the time?](#q19-how-do-you-find-the-majority-element-that-appears-more-than-half-the-time)
20. [How do you find the top k frequent values?](#q20-how-do-you-find-the-top-k-frequent-values)
21. [How do you group anagrams from a list of words?](#q21-how-do-you-group-anagrams-from-a-list-of-words)
22. [What is a prefix sum, and when is it useful?](#q22-what-is-a-prefix-sum-and-when-is-it-useful)
23. [How do you count subarrays whose sum equals k when values can be negative?](#q23-how-do-you-count-subarrays-whose-sum-equals-k-when-values-can-be-negative)
24. [How do you find the longest consecutive integer sequence in an unsorted array?](#q24-how-do-you-find-the-longest-consecutive-integer-sequence-in-an-unsorted-array)
25. [When is the two-pointer pattern useful?](#q25-when-is-the-two-pointer-pattern-useful)
26. [In a sorted array, how do you find whether two values sum to a target?](#q26-in-a-sorted-array-how-do-you-find-whether-two-values-sum-to-a-target)
27. [How do you check whether a string is a palindrome while ignoring punctuation and case?](#q27-how-do-you-check-whether-a-string-is-a-palindrome-while-ignoring-punctuation-and-case)
28. [How do you find the maximum sum of a contiguous subarray of fixed length k?](#q28-how-do-you-find-the-maximum-sum-of-a-contiguous-subarray-of-fixed-length-k)
29. [How do you find the longest substring with no repeated characters?](#q29-how-do-you-find-the-longest-substring-with-no-repeated-characters)
30. [How do you find the smallest substring containing all characters from a target string?](#q30-how-do-you-find-the-smallest-substring-containing-all-characters-from-a-target-string)
31. [When can a sliding window be used for a subarray sum problem?](#q31-when-can-a-sliding-window-be-used-for-a-subarray-sum-problem)
32. [What condition makes binary search valid?](#q32-what-condition-makes-binary-search-valid)
33. [How do you find the first occurrence of a value in a sorted array with duplicates?](#q33-how-do-you-find-the-first-occurrence-of-a-value-in-a-sorted-array-with-duplicates)
34. [How do you find a value in a rotated sorted array?](#q34-how-do-you-find-a-value-in-a-rotated-sorted-array)
35. [How do you find the minimum value in a rotated sorted array?](#q35-how-do-you-find-the-minimum-value-in-a-rotated-sorted-array)
36. [What is a lower bound?](#q36-what-is-a-lower-bound)
37. [How do you find the k-th largest value?](#q37-how-do-you-find-the-k-th-largest-value)
38. [How do you merge overlapping intervals?](#q38-how-do-you-merge-overlapping-intervals)
39. [How do you schedule the maximum number of non-overlapping meetings?](#q39-how-do-you-schedule-the-maximum-number-of-non-overlapping-meetings)
40. [How do you reverse a singly linked list iteratively?](#q40-how-do-you-reverse-a-singly-linked-list-iteratively)
41. [How do you find the middle node of a linked list?](#q41-how-do-you-find-the-middle-node-of-a-linked-list)
42. [How do you detect a cycle in a linked list?](#q42-how-do-you-detect-a-cycle-in-a-linked-list)
43. [How do you find the start of a linked-list cycle?](#q43-how-do-you-find-the-start-of-a-linked-list-cycle)
44. [How do you merge two sorted linked lists?](#q44-how-do-you-merge-two-sorted-linked-lists)
45. [How do you remove the n-th node from the end in one pass?](#q45-how-do-you-remove-the-n-th-node-from-the-end-in-one-pass)
46. [How do you validate balanced brackets?](#q46-how-do-you-validate-balanced-brackets)
47. [What is a monotonic stack?](#q47-what-is-a-monotonic-stack)
48. [How do you find the next greater element for every array item?](#q48-how-do-you-find-the-next-greater-element-for-every-array-item)
49. [How do you implement a queue using two stacks?](#q49-how-do-you-implement-a-queue-using-two-stacks)
50. [How do you design an LRU cache?](#q50-how-do-you-design-an-lru-cache)
51. [How do you find the largest rectangle in a histogram?](#q51-how-do-you-find-the-largest-rectangle-in-a-histogram)
52. [How do depth-first search and breadth-first search differ?](#q52-how-do-depth-first-search-and-breadth-first-search-differ)
53. [How do you traverse a binary tree in level order?](#q53-how-do-you-traverse-a-binary-tree-in-level-order)
54. [How do you find the maximum depth of a binary tree?](#q54-how-do-you-find-the-maximum-depth-of-a-binary-tree)
55. [How do you determine whether two binary trees are identical?](#q55-how-do-you-determine-whether-two-binary-trees-are-identical)
56. [How do you validate a binary search tree?](#q56-how-do-you-validate-a-binary-search-tree)
57. [How do you find the lowest common ancestor in a binary tree?](#q57-how-do-you-find-the-lowest-common-ancestor-in-a-binary-tree)
58. [How do you serialize and deserialize a binary tree?](#q58-how-do-you-serialize-and-deserialize-a-binary-tree)
59. [How do you find the k-th smallest value in a BST?](#q59-how-do-you-find-the-k-th-smallest-value-in-a-bst)
60. [How do you represent a graph in Java?](#q60-how-do-you-represent-a-graph-in-java)
61. [How do you detect a cycle in a directed graph?](#q61-how-do-you-detect-a-cycle-in-a-directed-graph)
62. [How do you find the shortest path in an unweighted graph?](#q62-how-do-you-find-the-shortest-path-in-an-unweighted-graph)
63. [When should you use Dijkstra's algorithm?](#q63-when-should-you-use-dijkstras-algorithm)
64. [What is topological sorting used for?](#q64-what-is-topological-sorting-used-for)
65. [How do you count connected components in an undirected graph?](#q65-how-do-you-count-connected-components-in-an-undirected-graph)
66. [What is backtracking?](#q66-what-is-backtracking)
67. [How do you generate all subsets of a set?](#q67-how-do-you-generate-all-subsets-of-a-set)
68. [What is dynamic programming?](#q68-what-is-dynamic-programming)
69. [How do you decide whether a problem fits dynamic programming?](#q69-how-do-you-decide-whether-a-problem-fits-dynamic-programming)
70. [How do you solve the climbing-stairs count problem?](#q70-how-do-you-solve-the-climbing-stairs-count-problem)
71. [How do you solve the coin-change minimum-number problem?](#q71-how-do-you-solve-the-coin-change-minimum-number-problem)
72. [What is the difference between 0/1 knapsack and unbounded knapsack?](#q72-what-is-the-difference-between-01-knapsack-and-unbounded-knapsack)
73. [How do you find the length of the longest increasing subsequence?](#q73-how-do-you-find-the-length-of-the-longest-increasing-subsequence)
74. [How do you find the longest common subsequence of two strings?](#q74-how-do-you-find-the-longest-common-subsequence-of-two-strings)
75. [Why should you use `StringBuilder` in a loop that constructs a large string?](#q75-why-should-you-use-stringbuilder-in-a-loop-that-constructs-a-large-string)
76. [How do you remove duplicates while preserving insertion order?](#q76-how-do-you-remove-duplicates-while-preserving-insertion-order)
77. [Why must `equals()` and `hashCode()` agree for hash-based collections?](#q77-why-must-equals-and-hashcode-agree-for-hash-based-collections)
78. [Why is a mutable object dangerous as a `HashMap` key?](#q78-why-is-a-mutable-object-dangerous-as-a-hashmap-key)
79. [What is the difference between `ArrayList` and `LinkedList` for indexed access?](#q79-what-is-the-difference-between-arraylist-and-linkedlist-for-indexed-access)
80. [When is `ArrayDeque` a better choice than `Stack`?](#q80-when-is-arraydeque-a-better-choice-than-stack)
81. [What is the difference between `Comparable` and `Comparator`?](#q81-what-is-the-difference-between-comparable-and-comparator)
82. [How can integer overflow break an algorithm?](#q82-how-can-integer-overflow-break-an-algorithm)
83. [What should a Java coding solution do with null input?](#q83-what-should-a-java-coding-solution-do-with-null-input)
84. [How should you test a coding solution beyond the sample?](#q84-how-should-you-test-a-coding-solution-beyond-the-sample)
85. [Design a rate limiter for a single Java process.](#q85-design-a-rate-limiter-for-a-single-java-process)
86. [Design an in-memory LRU cache with a maximum entry count.](#q86-design-an-in-memory-lru-cache-with-a-maximum-entry-count)
87. [Design a bounded producer-consumer work queue.](#q87-design-a-bounded-producer-consumer-work-queue)
88. [A batch job processes millions of records and uses too much memory. How would you change the design?](#q88-a-batch-job-processes-millions-of-records-and-uses-too-much-memory-how-would-you-change-the-design)
89. [An endpoint calculates totals from many independent sources. How would you reduce latency safely?](#q89-an-endpoint-calculates-totals-from-many-independent-sources-how-would-you-reduce-latency-safely)
90. [How would you implement a retry around a failing remote call?](#q90-how-would-you-implement-a-retry-around-a-failing-remote-call)
91. [A code exercise asks for a thread-safe check-then-act cache. What do you clarify?](#q91-a-code-exercise-asks-for-a-thread-safe-check-then-act-cache-what-do-you-clarify)
92. [How would you make a function easier to test?](#q92-how-would-you-make-a-function-easier-to-test)

## 1. Complexity and basic problem solving

### Q1. What does Big-O describe?

**Answer:** Big-O describes how an algorithm's resource use grows as input size grows, ignoring constant factors and lower-order terms. It commonly describes time or extra space.

### Q2. What is the difference between O(1), O(log n), O(n), O(n log n), and O(n²)?

**Answer:** O(1) stays roughly constant; O(log n) often halves the remaining search space; O(n) visits each input item; O(n log n) is common for efficient comparison sorting; O(n²) often compares every pair. These are growth classes, not exact runtime predictions.

### Q3. What is the difference between time complexity and space complexity?

**Answer:** Time complexity measures how the number of operations grows. Space complexity measures additional memory used as input grows. State whether the analysis includes the input itself or only auxiliary space.

### Q4. How do you analyze two nested loops over an array?

**Answer:** If both loops can each run n times independently, the time is O(n²). If the inner loop shrinks or advances monotonically across the whole algorithm, the total can instead be O(n); explain the actual number of iterations rather than multiplying blindly.

### Q5. What is amortized complexity?

**Answer:** It is the average cost per operation over a sequence of operations, even when some individual operations are expensive. For example, appending to a dynamic array is amortized O(1), though resizing one append can take O(n).

### Q6. How do you choose between a simple solution and a more complex optimization?

**Answer:** Start with correctness and constraints. Prefer the simpler approach when it meets time and memory limits. Optimize the measured bottleneck, and explain the extra complexity and maintenance cost.

## 2. Arrays, strings, and hashing

### Q7. Given an integer array and a target, find two distinct indices whose values sum to the target.

**Answer:** Scan once, storing each value's index in a `HashMap`. For each value `x`, check whether `target - x` was seen earlier. This is O(n) time and O(n) space. Check that the found index differs from the current index.

### Q8. How do you find whether an array contains a duplicate?

**Answer:** Insert each value into a `HashSet`; if insertion reports the value was already present, return true. This is O(n) expected time and O(n) space.

### Q9. How do you find the first non-repeating character in a string?

**Answer:** Count characters in one pass, then scan again and return the first character with count one. This is O(n) time. Space depends on the character set: fixed-size for ASCII, or O(k) for k distinct code points.

### Q10. How do you check whether two strings are anagrams?

**Answer:** If the problem defines a small alphabet, count each character in one string and subtract counts for the other. Otherwise use a frequency map or sort both strings. Clarify case, spaces, Unicode code points, and normalization.

### Q11. How do you reverse a string in Java?

**Answer:** For ordinary UTF-16 code-unit reversal, use `StringBuilder.reverse()`. If “character” means Unicode code point or user-perceived grapheme, simple reversal can break surrogate pairs or combining sequences; clarify the requirement first.

### Q12. How do you find the longest common prefix in a list of strings?

**Answer:** Start with the first string as the candidate prefix and shorten it until each next string starts with it. Worst-case time is O(total characters compared), with O(1) extra space apart from the result.

### Q13. How do you move all zeroes to the end while preserving the order of other values?

**Answer:** Use a write index. Copy each non-zero value forward in order, then fill the remaining positions with zero. This is O(n) time and O(1) extra space.

### Q14. How do you rotate an array right by k positions in-place?

**Answer:** Normalize `k` with `k % n`, then reverse the whole array, reverse the first k values, and reverse the rest. This is O(n) time and O(1) extra space. Handle an empty array before taking modulo.

### Q15. How do you find the missing number from values 0 through n when one is absent?

**Answer:** Use the expected sum `n * (n + 1) / 2` minus the actual sum, using a wide integer to avoid overflow. Alternatively use XOR. Both are O(n) time and O(1) extra space.

### Q16. How do you merge two sorted arrays?

**Answer:** Use two pointers, repeatedly append the smaller current value, then append the remaining tail. The time is O(m+n); output space is O(m+n) unless the prompt allows modifying one array with extra capacity.

### Q17. How do you find the maximum subarray sum?

**Answer:** Use Kadane's algorithm: at each index, decide whether to extend the current subarray or start a new one. Track the best sum seen. This is O(n) time and O(1) extra space; initialize from the first value so all-negative arrays work.

### Q18. How do you find the product of all array values except the value at each index without division?

**Answer:** Store prefix products in the output, then multiply by a running suffix product from right to left. This is O(n) time and O(1) extra space beyond the output. Clarify overflow and zero behavior.

### Q19. How do you find the majority element that appears more than half the time?

**Answer:** If the existence guarantee is given, use the Boyer–Moore voting algorithm in O(n) time and O(1) space, then optionally verify the candidate. Without the guarantee, the verification step is required.

### Q20. How do you find the top k frequent values?

**Answer:** Count frequencies with a map, then use a size-k min-heap for O(n log k) after counting, or bucket values by frequency for O(n) expected time. Choose based on input size, k, and tie-breaking requirements.

### Q21. How do you group anagrams from a list of words?

**Answer:** Use a canonical key per word—such as sorted characters or a character-frequency signature—and group words by that key in a map. Sorting costs O(m log m) per word of length m; a fixed-alphabet signature can be O(m).

### Q22. What is a prefix sum, and when is it useful?

**Answer:** A prefix sum stores cumulative totals so a range sum can be calculated by subtracting two prefix values. It turns repeated range-sum queries into O(1) each after O(n) preprocessing.

### Q23. How do you count subarrays whose sum equals k when values can be negative?

**Answer:** Maintain a running prefix sum and a map from prefix sums to their counts. At each position, add the count of `currentSum - k`. This is O(n) expected time and O(n) space. A sliding window is not generally valid with negative values.

### Q24. How do you find the longest consecutive integer sequence in an unsorted array?

**Answer:** Put values in a set. Start counting only from values with no predecessor, then walk forward through each sequence. This is O(n) expected time and O(n) space.

## 3. Two pointers and sliding window

### Q25. When is the two-pointer pattern useful?

**Answer:** It is useful when two positions can move through an ordered structure to reduce repeated work—for example, a sorted pair-sum problem or in-place partition. Explain why pointer movement does not skip a valid answer.

### Q26. In a sorted array, how do you find whether two values sum to a target?

**Answer:** Start at both ends. If the sum is too small, move the left pointer up; if too large, move the right pointer down. This is O(n) time and O(1) space.

### Q27. How do you check whether a string is a palindrome while ignoring punctuation and case?

**Answer:** Move two pointers inward, skipping characters outside the defined set, and compare normalized characters. Clarify Unicode and locale requirements; for ASCII interview constraints, this is O(n) time and O(1) space.

### Q28. How do you find the maximum sum of a contiguous subarray of fixed length k?

**Answer:** Sum the first k values, then slide the window by subtracting the outgoing value and adding the incoming value. This is O(n) time and O(1) extra space. Validate `k` is within the allowed range.

### Q29. How do you find the longest substring with no repeated characters?

**Answer:** Maintain a sliding window and the last-seen index of each character. Move the left boundary past a repeated character only when that occurrence is inside the current window. This is O(n) expected time and O(k) space.

### Q30. How do you find the smallest substring containing all characters from a target string?

**Answer:** Use a sliding window with required character counts. Expand the right side until all requirements are met, then shrink the left side while the window remains valid. This is O(n+m) expected time with maps or arrays for character counts.

### Q31. When can a sliding window be used for a subarray sum problem?

**Answer:** It commonly works when values are non-negative, because expanding the window cannot reduce the sum and shrinking cannot increase it. With negative values, those monotonic properties fail; prefix sums and hashing may be needed.

## 4. Binary search and sorting

### Q32. What condition makes binary search valid?

**Answer:** The search space must be ordered or the decision predicate must be monotonic, so one comparison can safely eliminate half the remaining candidates.

### Q33. How do you find the first occurrence of a value in a sorted array with duplicates?

**Answer:** Use lower-bound binary search: when the middle value is at least the target, continue left; otherwise continue right. Verify the resulting index is in range and equal to the target. Time is O(log n).

### Q34. How do you find a value in a rotated sorted array?

**Answer:** At each step, determine which half is sorted, then decide whether the target lies within that half. Duplicates can make the sorted half ambiguous and may degrade the worst-case time to O(n).

### Q35. How do you find the minimum value in a rotated sorted array?

**Answer:** Compare the middle value with the rightmost value to determine which half contains the rotation point. This is O(log n) without duplicates; duplicates may require shrinking the boundary one step at a time.

### Q36. What is a lower bound?

**Answer:** It is the first position whose value is greater than or equal to a target in a sorted sequence. It is useful for insertion points, range counts, and first-occurrence searches.

### Q37. How do you find the k-th largest value?

**Answer:** Use a size-k min-heap for O(n log k), or quickselect for O(n) average time and O(n²) worst case. Sorting is simpler at O(n log n). State whether mutation is allowed and whether duplicates count.

### Q38. How do you merge overlapping intervals?

**Answer:** Sort by start, then scan. If the next interval starts before or at the current end, extend the current end; otherwise emit the current interval. Sorting dominates at O(n log n).

### Q39. How do you schedule the maximum number of non-overlapping meetings?

**Answer:** Sort meetings by end time and repeatedly choose the next meeting that begins after the last chosen one ends. This greedy choice is optimal for the unweighted case and takes O(n log n).

## 5. Linked lists

### Q40. How do you reverse a singly linked list iteratively?

**Answer:** Keep `previous`, `current`, and `next`. For each node, save `next`, point the current node to `previous`, then advance. This is O(n) time and O(1) extra space.

### Q41. How do you find the middle node of a linked list?

**Answer:** Use slow and fast pointers; slow moves one step and fast moves two. When fast reaches the end, slow is at the middle. Clarify which middle is required for even-length lists.

### Q42. How do you detect a cycle in a linked list?

**Answer:** Use Floyd's slow/fast pointer algorithm. If the pointers meet, there is a cycle; if the fast pointer reaches null, there is none. Time is O(n) and space is O(1).

### Q43. How do you find the start of a linked-list cycle?

**Answer:** After slow and fast meet, move one pointer to the head; advance both one step at a time. Their next meeting point is the cycle entry. This follows from the distances traveled modulo the cycle length.

### Q44. How do you merge two sorted linked lists?

**Answer:** Use a dummy head and append the smaller node at each step, then attach the remaining list. This is O(m+n) time and O(1) extra nodes if existing nodes can be relinked.

### Q45. How do you remove the n-th node from the end in one pass?

**Answer:** Advance a fast pointer n steps, then move fast and slow together until fast reaches the end. Remove the node after slow using a dummy head to handle removing the first node. Validate n.

## 6. Stacks, queues, and intervals

### Q46. How do you validate balanced brackets?

**Answer:** Push opening brackets on a stack. For each closing bracket, check that the stack is non-empty and its top matches; at the end the stack must be empty. This is O(n) time and O(n) space.

### Q47. What is a monotonic stack?

**Answer:** It is a stack kept in increasing or decreasing order. It helps find next greater/smaller elements, spans, and histogram boundaries in linear time because each item is pushed and popped at most once.

### Q48. How do you find the next greater element for every array item?

**Answer:** Scan from left to right with a stack of unresolved indices. When the current value is greater than the value at the stack top, it is the next greater value for that index. Overall time is O(n).

### Q49. How do you implement a queue using two stacks?

**Answer:** Push new items onto an input stack. When removing or peeking and the output stack is empty, move all input items to output. Each item moves at most once, so operations are amortized O(1).

### Q50. How do you design an LRU cache?

**Answer:** Combine a hash map for O(1) key lookup with a doubly linked list for O(1) recency updates and eviction. Java's access-order `LinkedHashMap` can implement a simple version; a production cache may also need concurrency, expiry, size/weight limits, and metrics.

### Q51. How do you find the largest rectangle in a histogram?

**Answer:** Use a monotonic increasing stack of indices. When a lower bar arrives, pop taller bars and calculate the maximum rectangle for each popped height. This is O(n) time and O(n) space.

## 7. Trees and binary search trees

### Q52. How do depth-first search and breadth-first search differ?

**Answer:** DFS explores a path deeply before backtracking, often using recursion or a stack. BFS visits level by level using a queue. BFS is useful for shortest paths in unweighted graphs; DFS is useful for structure and path exploration.

### Q53. How do you traverse a binary tree in level order?

**Answer:** Put the root in a queue. Repeatedly remove a node, process it, and enqueue its children. The time is O(n); queue space is O(w), where w is the maximum tree width.

### Q54. How do you find the maximum depth of a binary tree?

**Answer:** Recursively return zero for null and one plus the larger child depth. This is O(n) time and O(h) call-stack space, where h is tree height; an iterative traversal avoids recursive stack growth.

### Q55. How do you determine whether two binary trees are identical?

**Answer:** Compare corresponding nodes recursively: both null is equal, one null is not, values must match, and both left and right subtrees must match. Time is O(n).

### Q56. How do you validate a binary search tree?

**Answer:** Pass an allowed lower and upper bound down the tree; every node must fall strictly within its bounds according to the duplicate policy. Checking only each node against its immediate children is insufficient.

### Q57. How do you find the lowest common ancestor in a binary tree?

**Answer:** In a general binary tree, recurse into both children; if both sides find a target, the current node is the ancestor. In a BST, use the ordering to move left or right until the node lies between the targets. Clarify whether both targets are guaranteed to exist.

### Q58. How do you serialize and deserialize a binary tree?

**Answer:** Use a traversal such as preorder and include explicit null markers so structure is unambiguous. Deserialize by consuming values in the same order. State the format, value escaping, and malformed-input handling.

### Q59. How do you find the k-th smallest value in a BST?

**Answer:** In-order traversal visits values in sorted order. Stop after k nodes. It is O(h+k) with early stopping and O(h) stack space; augmenting nodes with subtree sizes can support repeated queries more efficiently.

## 8. Graphs and search

### Q60. How do you represent a graph in Java?

**Answer:** An adjacency list is usually efficient for sparse graphs: map each vertex to its neighbors. An adjacency matrix gives O(1) edge lookup but uses O(V²) space. Choose based on density and operations.

### Q61. How do you detect a cycle in a directed graph?

**Answer:** Use DFS with three states—unvisited, visiting, done—or use topological sorting. An edge to a currently visiting node indicates a cycle. Time is O(V+E).

### Q62. How do you find the shortest path in an unweighted graph?

**Answer:** Use BFS from the source, recording the first distance and predecessor for each vertex. It runs in O(V+E) with an adjacency list.

### Q63. When should you use Dijkstra's algorithm?

**Answer:** Use it for shortest paths with non-negative edge weights. A priority queue gives a typical complexity of O((V+E) log V). Negative edges require a different algorithm.

### Q64. What is topological sorting used for?

**Answer:** It orders vertices in a directed acyclic graph so every dependency appears before the item that depends on it. It is useful for build tasks, scheduling, and dependency resolution. A cycle means no valid topological order exists.

### Q65. How do you count connected components in an undirected graph?

**Answer:** Visit every vertex. Each unvisited vertex starts a DFS or BFS that marks one component. The overall time is O(V+E).

## 9. Backtracking and dynamic programming

### Q66. What is backtracking?

**Answer:** Backtracking builds a candidate solution step by step, explores choices, and undoes a choice when that path cannot produce a valid answer. It is useful for permutations, combinations, and constraint problems; worst-case time is often exponential.

### Q67. How do you generate all subsets of a set?

**Answer:** For each item, choose to include or exclude it, recursively exploring both branches. There are 2^n subsets, so output size alone is exponential.

### Q68. What is dynamic programming?

**Answer:** It solves overlapping subproblems and stores their results so the same work is not repeated. The two common forms are memoization (top-down) and tabulation (bottom-up).

### Q69. How do you decide whether a problem fits dynamic programming?

**Answer:** Look for a problem that can be divided into smaller states, where choices have overlapping subproblems and an optimal answer can be built from optimal subproblem answers. Define the state and transition before writing code.

### Q70. How do you solve the climbing-stairs count problem?

**Answer:** If each step can be one or two stairs, `ways(n) = ways(n-1) + ways(n-2)`. Keep only the previous two values for O(n) time and O(1) space. Define base cases from the exact prompt.

### Q71. How do you solve the coin-change minimum-number problem?

**Answer:** Let `dp[x]` be the fewest coins needed to make amount x. Initialize `dp[0]=0`; for each amount, try every coin and take the minimum reachable previous state. Time is O(amount × number of denominations), with O(amount) space.

### Q72. What is the difference between 0/1 knapsack and unbounded knapsack?

**Answer:** In 0/1 knapsack each item may be chosen at most once; iterate capacities downward in a one-dimensional DP. In unbounded knapsack an item may be reused; iterate capacities upward for each item. Loop direction changes the meaning.

### Q73. How do you find the length of the longest increasing subsequence?

**Answer:** A simple DP is O(n²). A faster method maintains the smallest tail value for each subsequence length and uses binary search, giving O(n log n). The tail array does not necessarily itself represent the final subsequence.

### Q74. How do you find the longest common subsequence of two strings?

**Answer:** Use a two-dimensional DP over prefixes. Matching characters extend the diagonal; otherwise take the maximum of dropping one character from either string. Time is O(mn); space can be reduced to O(min(m,n)) if only the length is needed.

## 10. Java-specific coding judgment

### Q75. Why should you use `StringBuilder` in a loop that constructs a large string?

**Answer:** Repeated immutable string concatenation can create many intermediate strings. `StringBuilder` mutates a reusable buffer and is usually more efficient for iterative construction. Modern compilers can optimize simple concatenation expressions, so reason about the actual loop and profile when performance matters.

### Q76. How do you remove duplicates while preserving insertion order?

**Answer:** Use `LinkedHashSet`, then convert to the required result type. This gives expected O(n) time and preserves first-seen order.

### Q77. Why must `equals()` and `hashCode()` agree for hash-based collections?

**Answer:** Equal objects must have equal hash codes so a `HashMap` or `HashSet` searches the correct bucket. If the contract is broken, lookups may fail even when an equal key is present.

### Q78. Why is a mutable object dangerous as a `HashMap` key?

**Answer:** If fields used by `hashCode()` or `equals()` change after insertion, the key may now belong in a different bucket than the one where it was stored. Prefer immutable keys.

### Q79. What is the difference between `ArrayList` and `LinkedList` for indexed access?

**Answer:** `ArrayList` provides O(1) indexed access and amortized O(1) append, while insertion/removal in the middle shifts elements. `LinkedList` indexed access is O(n); it is not automatically faster for frequent middle updates because locating the node also costs time.

### Q80. When is `ArrayDeque` a better choice than `Stack`?

**Answer:** `ArrayDeque` is generally preferred for stack or queue operations because it is a modern deque implementation without the legacy synchronization behavior of `Stack`. It is not thread-safe.

### Q81. What is the difference between `Comparable` and `Comparator`?

**Answer:** `Comparable` defines a type's natural ordering in the type itself. `Comparator` defines an external or alternative ordering and can be supplied at the call site.

### Q82. How can integer overflow break an algorithm?

**Answer:** Arithmetic can wrap around and produce a wrong comparison, sum, or index. Use a wider type when constraints require it, and consider checked arithmetic if overflow must be reported.

### Q83. What should a Java coding solution do with null input?

**Answer:** Follow the contract. If null is invalid, reject it clearly or state the precondition; if null is valid, define the expected result. Do not silently invent behavior during an interview.

### Q84. How should you test a coding solution beyond the sample?

**Answer:** Test empty and one-element inputs, duplicates, negative values, boundary indices, already sorted/reversed data, integer limits, and the maximum constraint. Add cases that exercise each branch of the algorithm.

## 11. Scenario and case-study coding prompts

### Q85. Design a rate limiter for a single Java process.

**Answer:** Clarify whether the limit is per user, key, or service and whether it uses a fixed window, sliding window, or token bucket. For an in-memory token bucket, store state per key and update it atomically under a lock or with a carefully designed concurrent structure. Discuss expiry and memory bounds. A multi-instance service needs shared or partitioned state.

### Q86. Design an in-memory LRU cache with a maximum entry count.

**Answer:** For a simple single-threaded version, use access-order `LinkedHashMap` and evict the eldest entry. For concurrent use, define atomicity between lookup, recency update, and eviction; either lock the compound operation or use a tested cache library. Add tests for replacement, eviction order, and capacity.

### Q87. Design a bounded producer-consumer work queue.

**Answer:** Prefer `ArrayBlockingQueue` or another `BlockingQueue` instead of implementing wait/notify manually. Choose capacity, timeout, and rejection/backpressure behavior. Define shutdown and interruption behavior, then test full, empty, and cancellation cases.

### Q88. A batch job processes millions of records and uses too much memory. How would you change the design?

**Answer:** Avoid loading the entire dataset into one collection. Process pages or streams, bound queues and batches, release references promptly, and apply backpressure between stages. Measure heap and allocation behavior; preserve transaction and retry semantics.

### Q89. An endpoint calculates totals from many independent sources. How would you reduce latency safely?

**Answer:** Identify which calls can run independently, use bounded asynchronous execution or virtual threads for blocking I/O as appropriate, set an overall deadline, and cancel work that is no longer needed. Protect downstream services with concurrency limits and handle partial failures according to product requirements.

### Q90. How would you implement a retry around a failing remote call?

**Answer:** Retry only failures that may recover and operations that are safe to repeat. Use a bounded attempt count, exponential backoff with jitter, an overall deadline, and idempotency where needed. Do not retry validation errors or overload failures indefinitely.

### Q91. A code exercise asks for a thread-safe check-then-act cache. What do you clarify?

**Answer:** Clarify whether the load must run once per key, whether it can block, how failures are cached, whether null is allowed, and what cancellation means. A `ConcurrentHashMap` plus separate `get` and `put` is not atomic; consider `computeIfAbsent` or an explicit in-flight future per key.

### Q92. How would you make a function easier to test?

**Answer:** Keep it deterministic where possible, pass dependencies explicitly, separate I/O from transformation logic, and make side effects visible. Avoid adding abstractions that do not solve a real testing or design problem.

## Sources

- [LeetCode Top Interview 150](https://leetcode.com/studyplan/top-interview-150/) — broad collection of classic interview problems.
- [TechTriber — DSA Patterns in Java](https://www.techtriber.com/blog/dsa-patterns-cheat-sheet-java) — pattern-oriented preparation themes.
- [Interview Pilot — Interview Programming Questions in Java](https://www.interviewpilot.app/blog/interview-programming-questions-in-java) — Java-specific coding and problem-solving categories.
- [Java SE 25 API Documentation](https://docs.oracle.com/en/java/javase/25/) — authoritative reference for Java collections and standard APIs.

Interview problem lists are practice collections, not a promise that a particular company will ask a specific problem. Focus on reusable reasoning and explain your assumptions before coding.
