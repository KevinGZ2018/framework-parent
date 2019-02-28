package com.fushun.framework.zookeeper.listener;

import org.apache.zookeeper.Watcher.Event.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * 监听器管理类
 *
 * @author zhoup
 * @date 2016年6月17日
 */
public class ListenerManager {
    //监听器
    private Listener listener;
    private List<String> childNode = new ArrayList<String>();
    //节点数据
    private byte[] data;
    //事件类型
    private EventType eventType;
    //是否监听孩子节点的数据
    private boolean childData;

    public ListenerManager(Listener listener) {
        this.listener = listener;
    }

    public ListenerManager(Listener listener, boolean childData) {
        this.listener = listener;
        this.childData = childData;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public List<String> getChildNode() {
        return childNode;
    }

    public void setChildNode(List<String> childNode) {
        this.childNode = childNode;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public boolean isChildData() {
        return childData;
    }

    public void setChildData(boolean childData) {
        this.childData = childData;
    }
}
