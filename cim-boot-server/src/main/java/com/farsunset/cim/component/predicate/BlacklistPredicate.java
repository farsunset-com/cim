package com.farsunset.cim.component.predicate;


import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class BlacklistPredicate implements Predicate<String> {

    /**
     * IP黑名单拦截处理
     * @param remoteAddress IP地址
     * @return true:不拦截  false:拦截
     */
    @Override
    public boolean test(String remoteAddress) {

        /*
         * 自行根据IP判断是否需要拦截
         */

        return true;
    }
}
