package com.fushun.framework.filestorage.qiniu.result;

import java.util.ArrayList;
import java.util.List;

/***
 * 七牛删除图片返回对象
 * @date: 2017-09-15 16:32:45
 * @author:wangfushun
 * @version 1.0
 */
public class DelFileResultDTO {

    /**
     * 所有失败
     * true:所有失败
     */
    private boolean allFail = false;

    /**
     * 成功删除的
     */
    private List<String> successList = new ArrayList<>();

    /**
     * 删除失败的
     */
    private List<String> failList = new ArrayList<>();


    /**
     * 添加删除成功的id
     *
     * @param id
     * @date: 2017-09-15 16:38:30
     * @author:wangfushun
     * @version 1.0
     */
    public void addSuccess(String id) {
        successList.add(id);
    }

    /**
     * 添加删除失败id
     *
     * @param id
     * @date: 2017-09-15 16:38:23
     * @author:wangfushun
     * @version 1.0
     */
    public void addFail(String id) {
        failList.add(id);
    }

    public boolean isAllFail() {
        return allFail;
    }

    public void setAllFail(boolean allFail) {
        this.allFail = allFail;
    }

    public List<String> getSuccessList() {
        return successList;
    }

    public List<String> getFailList() {
        return failList;
    }


}
