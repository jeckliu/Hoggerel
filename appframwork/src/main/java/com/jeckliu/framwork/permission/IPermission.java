package com.jeckliu.framwork.permission;

import java.util.List;

/***
 * Created by Jeck.Liu on 2017/1/22 0022.
 */
public interface IPermission {

    void done();

    void unPermission(List<String> denyPermissions);
}
