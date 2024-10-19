package com.farsunset.cim.component.predicate;


import com.farsunset.cim.auth.AuthPredicateInfo;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * WS 鉴权验证
 */
@Component
public class AuthPredicate implements Predicate<AuthPredicateInfo> {

    @Override
    public boolean test(AuthPredicateInfo auth) {
        /*
            可通过header或者uri传递参数
            String token = auth.getHeader("token");
            String token = auth.getParameter("token");
            User user = doAuth(token);
            auth.getCtx().attr(AttributeKey.valueOf("user_id")).set(user.getId());
         */


        return true;
    }
}
