package com.fushun.framework.util.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhoup
 */
public class ValidationResult {

    private List<String> globalMessageList = new ArrayList<String>();

    private Map<String, String> messagesMap = new HashMap<String, String>();

    public boolean isValidationFail() {
        return !globalMessageList.isEmpty() || !messagesMap.isEmpty();
    }

    public void addGlobalMessage(String message) {
        globalMessageList.add(message);
    }

    public void addMessage(String fieldPath, String message) {
        messagesMap.put(fieldPath, message);
    }

    public List<String> getGlobalMessageList() {
        return globalMessageList;
    }

    public void setGlobalMessageList(List<String> globalMessageList) {
        this.globalMessageList = globalMessageList;
    }

    public Map<String, String> getMessagesMap() {
        return messagesMap;
    }

    public void setMessagesMap(Map<String, String> messagesMap) {
        this.messagesMap = messagesMap;
    }
}
