package com.dogoo.SystemWeighingSas.service;

import com.dogoo.SystemWeighingSas.model.RoleAddMapperModel;
import com.dogoo.SystemWeighingSas.model.RoleMapperModel;

import java.util.HashMap;
import java.util.List;

public interface RoleService {
    void addRole(RoleMapperModel model);

    void updateRole(RoleMapperModel model);

    List<RoleMapperModel> getListRole(long accountId);

    boolean checkRole(long accountId,
                      String key,
                      String path,
                      String path2);

    RoleAddMapperModel checkRoleAdd(long accountId,
                                    String key,
                                    String path);

    boolean checkRoleCreate(long accountId,
                            String code);

}
