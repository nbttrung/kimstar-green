package com.dogoo.SystemWeighingSas.service.impl;

import com.dogoo.SystemWeighingSas.config.Constants;
import com.dogoo.SystemWeighingSas.dao.IAccountDao;
import com.dogoo.SystemWeighingSas.dao.ICustomerDao;
import com.dogoo.SystemWeighingSas.dao.IRoleDao;
import com.dogoo.SystemWeighingSas.dao.IWeighingStationDao;
import com.dogoo.SystemWeighingSas.entity.Account;
import com.dogoo.SystemWeighingSas.entity.Customer;
import com.dogoo.SystemWeighingSas.entity.Role;
import com.dogoo.SystemWeighingSas.entity.WeighingStation;
import com.dogoo.SystemWeighingSas.model.RoleAddMapperModel;
import com.dogoo.SystemWeighingSas.model.RoleMapperModel;
import com.dogoo.SystemWeighingSas.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private IRoleDao iRoleDao;
    @Autowired
    private IAccountDao iAccountDao;
    @Autowired
    private IWeighingStationDao iWeighingStationDao;
    @Autowired
    private ICustomerDao iCustomerDao;

    @Override
    public void addRole(RoleMapperModel model) {
        Role role = Constants.SERIALIZER.convertValue(model, Role.class);
        iRoleDao.save(role);
    }

    @Override
    public void updateRole(RoleMapperModel model) {
        Role role = iRoleDao.findById(model.getId());
        role.setRoleCreate(model.isRoleCreate());
        role.setRoleView(model.isRoleView());
        role.setModule(model.getModule());

        iRoleDao.save(role);
    }

    @Override
    public List<RoleMapperModel> getListRole(long accountId) {
        return iRoleDao.findByAccountId(accountId).stream()
                .map(role -> Constants.SERIALIZER.convertValue(role, RoleMapperModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkRole(long accountId, String key, String path, String path2) {

        Account account = iAccountDao.findByAccountId(accountId);

        return checkPermission(account, path) || checkWeighingStation(accountId, key, path,
                URLDecoder.decode(path2, StandardCharsets.UTF_8) );

    }

    @Override
    public RoleAddMapperModel checkRoleAdd(long accountId, String key, String path) {

        RoleAddMapperModel model = new RoleAddMapperModel();

        if (path.equals(Constants.arrayPathAdmin.get(0)) || path.equals(Constants.arrayPathAdminUser.get(0)) ||
        path.equals(Constants.arrayPathAdminUser.get(3))){
            model.setCheckAdd(Boolean.FALSE);
            return model;
        }

        if (path.equals(Constants.arrayPathAdmin.get(1)) || path.equals(Constants.arrayPathAdmin.get(4)) ){
            model.setCheckAdd(Boolean.TRUE);
            return model;
        }

        Account account = iAccountDao.findByAccountId(accountId);
        Role role = iRoleDao.findByAccountIdAndModule(account.getAccountId(), path);

        if (path.equals(Constants.arrayPathAdminUser.get(1)) &&
                (account.getRoleAll() || (role!=null && role.isRoleCreate()) ) ){
            model.setCheckAdd(Boolean.TRUE);
            return model;
        }

        if (path.equals(Constants.arrayPathAdminUser.get(5))){

            Customer customer = iCustomerDao.findCustomerByKey(key);
            if (customer== null){
                model.setCheckAdd(Boolean.FALSE);
                return model;
            }

            if (Boolean.TRUE.equals(account.getRoleAll())){
                List<String> codeList = iWeighingStationDao.findWeighingStationByCustomerId(customer.getId())
                        .stream()
                        .map(WeighingStation::getWeighingStationCode)
                        .collect(Collectors.toList());

                model.setCheckAdd(!codeList.isEmpty());
                model.setCode(codeList);
                return model;
            }

            List<String> codeList = iWeighingStationDao.findWeighingStationByCustomerId(customer.getId())
                    .stream().filter(weighingStation -> {
                        Role role1 = iRoleDao.findByAccountIdAndModule(accountId, weighingStation.getWeighingStationCode());
                        return role1 != null && role1.isRoleCreate();
                    })
                    .map(WeighingStation::getWeighingStationCode)
                    .collect(Collectors.toList());

            model.setCheckAdd(!codeList.isEmpty());
            model.setCode(codeList);
            return model;
        }

        return model;
    }

    @Override
    public boolean checkRoleCreate(long accountId, String code) {

        Account account = iAccountDao.findByAccountId(accountId);

        if (Boolean.TRUE.equals(account.getRoleAll()))
            return true ;

        Role role = iRoleDao.findByAccountIdAndModule(accountId , code);

        return role != null && role.isRoleCreate();
    }

    private boolean checkPermission(Account account,
                                    String path){
        if (account.getRole().name().equals("admin") && Constants.arrayPathAdmin.contains(path) ){
            return true;
        }

        if (Boolean.TRUE.equals(account.getRoleAll()) &&
                !account.getRole().name().equals("admin") &&
                !Constants.arrayPathAdmin.contains(path))
            return true;

        Role role = iRoleDao.findByAccountIdAndModule(account.getAccountId(), path);
        boolean checkRole = role != null && role.isRoleView();
        return (!account.getRole().name().equals("admin") &&
                Constants.arrayPathAdminUser.contains(path) &&
                (checkRole || path.equals("report")));
    }

    private boolean checkWeighingStation (long accountId,
                                          String key ,
                                          String path,
                                          String path2) {

        Customer customer = iCustomerDao.findCustomerByKey(key);
        if (customer== null)
            return false;

        List<WeighingStation> list = iWeighingStationDao.findWeighingStationByCustomerId(customer.getId());

        Role role = iRoleDao.findByAccountIdAndModule(accountId, path2);
        boolean checkRole = role != null && role.isRoleView();
        return path.equals("report-weighing-station") &&
                path2 != null &&
                list.stream().anyMatch(c -> c.getWeighingStationCode().equals(path2)) &&
                checkRole;
    }
}
