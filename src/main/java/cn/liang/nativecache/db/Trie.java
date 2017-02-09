package cn.liang.nativecache.db;

import java.util.*;

/**
 * Created by mc-050 on 2017/1/13 10:41.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
        root.wordEnd = false;
    }

    public void insert(Class table, String column, String word, long line) {
        TrieNode node = root;
        String key = buildKey(table, column);
        for (int i = 0; i < word.length(); i++) {
            Character c = word.charAt(i);
            if (!node.childdren.containsKey(c)) {
                node.childdren.put(c, new TrieNode());
            }
            node = node.childdren.get(c);
            if (node.tableLineSet.containsKey(key)) {
                node.tableLineSet.get(key).add(line);
            } else {
                Set<Long> set = new HashSet<>();
                set.add(line);
                node.tableLineSet.put(key, set);
            }
        }
        node.wordEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = root;
        boolean found = true;
        for (int i = 0; i < word.length(); i++) {
            Character c = new Character(word.charAt(i));
            if (!node.childdren.containsKey(c)) {
                return false;
            }
            node = node.childdren.get(c);
        }
        return found && node.wordEnd;
    }

    public boolean startsWith(String prefix) {
        TrieNode node = root;
        boolean found = true;
        for (int i = 0; i < prefix.length(); i++) {
            Character c = new Character(prefix.charAt(i));
            if (!node.childdren.containsKey(c)) {
                return false;
            }
            node = node.childdren.get(c);
        }
        return found;
    }

    /**
     * 根据条件查询满足条件的所有行数据
     *
     * @param table
     * @param column
     * @param word
     * @return 返回满足条件的所有行
     */
    public Set<Long> searchLine(Class table, String column, String word) {
        TrieNode node = root;
        String key = buildKey(table, column);
        for (int i = 0; i < word.length(); i++) {
            Character c = new Character(word.charAt(i));
            if (!node.childdren.containsKey(c)) {
                return Collections.emptySet();
            }
            node = node.childdren.get(c);
        }
        return node.tableLineSet.get(key);
    }

    private class TrieNode {
        Map<Character, TrieNode> childdren;
        boolean wordEnd;
        Map<String, Set<Long>> tableLineSet;

        public TrieNode() {
            childdren = new HashMap<Character, TrieNode>();
            wordEnd = false;
            tableLineSet = new HashMap<>();
        }

        @Override
        public String toString() {
            return "TrieNode{" +
                    "childdren=" + childdren +
                    ", wordEnd=" + wordEnd +
                    ", tableLineSet=" + tableLineSet +
                    '}';
        }
    }

    private String buildKey(Class table, String column) {
        return table.getName() + "-" + column;
    }

}

