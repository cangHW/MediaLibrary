package com.chx.livemaker.manager.base.params;

import android.text.TextUtils;

import com.chx.livemaker.manager.base.helpr.VideoCacheHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cangHX
 * on 2018/12/24  10:42
 */
public class VideoCacheWrapper {

    private class Node {
        int ID;
        String url;
        boolean isError = false;

        Node(String url, int id) {
            this.ID = id;
            this.url = url;
        }
    }

    public class Cache extends Node {
        boolean isUse = false;
        List<String> list = new ArrayList<>();

        Cache(List<String> list, int id) {
            super(null, id);
            this.list.addAll(list);
        }

        public List<String> getList() {
            return list;
        }

        public String updataName() {
            if (list.size() <= 0) {
                return null;
            }
            File file = new File(list.get(0));
            String name = file.getName();
            int index = name.lastIndexOf(".");
            String suffix = name.substring(index);
            File newFile = new File(file.getParent(), VideoCacheHelper.CACHE + System.currentTimeMillis() + suffix);
            url = newFile.getPath();
            return url;
        }
    }

    private int mIndex = 0;
    private volatile ArrayList<Node> mNodes = new ArrayList<>();

    public synchronized String getSingleUrl() {
        Node node = mNodes.get(0);
        return node.url;
    }

    public synchronized int getSize() {
        return mNodes.size();
    }

    public synchronized void add(String s) {
        Node node = new Node(s, mIndex);
        mNodes.add(node);
        mIndex++;
    }

    public synchronized boolean hasError() {
        for (Node node : mNodes) {
            if (node.isError) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isCanUse() {
        List<Node> nodeList = new ArrayList<>();
        List<Integer> nodeIndex = new ArrayList<>();
        for (int i = 0; i < mNodes.size(); i++) {
            Node node = mNodes.get(i);
            if (node == null) {
                continue;
            }
            if (node instanceof Cache) {
                if (nodeList.size() > 1) {
                    break;
                } else {
                    nodeList.clear();
                    nodeIndex.clear();
                }
            } else {
                nodeList.add(node);
                nodeIndex.add(i);
            }
        }
        if (nodeList.size() < 2) {
            return false;
        }
        List<String> strings = new ArrayList<>();
        int id = -1;
        for (Node node : nodeList) {
            if (id == -1) {
                id = node.ID;
            }
            strings.add(node.url);
        }
        Cache cache = new Cache(strings, id);
        for (int i = nodeIndex.size() - 1; i >= 0; i--) {
            int index = nodeIndex.get(i);
            if (i == 0) {
                mNodes.set(index, cache);
            } else {
                mNodes.remove(index);
            }
        }
        return true;
    }

    public synchronized Cache getCanUse() {
        for (Node node : mNodes) {
            if (node instanceof Cache) {
                Cache cache = (Cache) node;
                if (!cache.isUse) {
                    cache.isUse = true;
                    return cache;
                }
            }
        }
        return null;
    }

    public synchronized void updataCache(Cache cache) {
        if (TextUtils.isEmpty(cache.url)) {
            return;
        }
        for (int i = 0; i < mNodes.size(); i++) {
            Node node = mNodes.get(i);
            if (node.ID == cache.ID) {
                Node n = new Node(cache.url, cache.ID);
                mNodes.set(i, n);
                for (String s : cache.list) {
                    try {
                        File file = new File(s);
                        file.delete();
                    } catch (Exception e) {
                        e.toString();
                    }
                }
                return;
            }
        }
    }

    public synchronized void updataCacheError(Cache cache) {
        if (TextUtils.isEmpty(cache.url)) {
            return;
        }
        for (int i = 0; i < mNodes.size(); i++) {
            Node node = mNodes.get(i);
            if (node.ID == cache.ID) {
                Node n = new Node(cache.url, cache.ID);
                n.isError = true;
                mNodes.set(i, n);
                for (String s : cache.list) {
                    try {
                        File file = new File(s);
                        file.delete();
                    } catch (Exception e) {
                        e.toString();
                    }
                }
                return;
            }
        }
    }

    public synchronized void clear() {
        mNodes.clear();
    }
}
