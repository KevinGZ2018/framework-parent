package com.fushun.framework.elasticsearch.cmp;


import com.fushun.framework.util.exception.base.BaseCMP;

import javax.persistence.Transient;


public class TestCMP extends BaseCMP {
    @Transient
    private String l;

    private String m;

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }


}
