package com.newcoder.community.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤，使用TreeNode形式实现
 */
@Component
@Slf4j
public class SensitiveFilter {

    // 敏感词
    private static final String REPLACEMENT = "***";

    // 根节点
    private TreeNode rootNode = new TreeNode();

    // 初始化方法，加载配置文件中的敏感词
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 将敏感词添加到前缀树
                addKeyword(keyword);
            }
        } catch (IOException e) {
            log.error("加载敏感词文件失败: " + e.getMessage());
        }


    }

    // 将敏感词加入前缀树
    private void addKeyword(String keyword) {
        // 将根节点设为临时节点
        TreeNode tempNode = rootNode;
        // 循环遍历创建节点
        for (int i = 0; i < keyword.length(); i++) {
            // 获取字符
            Character c = keyword.charAt(i);
            // 获取子节点
            TreeNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                // 创建子节点
                subNode = new TreeNode();
                tempNode.addSubNode(c, subNode);
            }
            // 临时节点指向子节点，进行下一次循环
            tempNode = subNode;

            // 设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    // 过滤敏感词
    public String filter(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        // 指针1
        TreeNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点,将此符号计入结果,让指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                position++;
            }
        }

        // 将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    // 前缀树
    private class TreeNode {

        // 是否是敏感词结束
        private boolean isKeywordEnd = false;

        // 当前节点下子节点集合
        private Map<Character, TreeNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TreeNode treeNode) {
            subNodes.put(c, treeNode);
        }

        // 获取子节点
        public TreeNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

}
