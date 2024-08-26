package com.dogoo.SystemWeighingSas.service.impl;

import com.dogoo.SystemWeighingSas.common.MailUtil;
import com.dogoo.SystemWeighingSas.common.generalPassword.PwdGenerator;
import com.dogoo.SystemWeighingSas.config.Constants;
import com.dogoo.SystemWeighingSas.dao.IAccountDao;
import com.dogoo.SystemWeighingSas.dao.ICustomerDao;
import com.dogoo.SystemWeighingSas.entity.Account;
import com.dogoo.SystemWeighingSas.entity.Customer;
import com.dogoo.SystemWeighingSas.enumEntity.RoleEnum;
import com.dogoo.SystemWeighingSas.enumEntity.StatusEnum;
import com.dogoo.SystemWeighingSas.model.AccountMapperModel;
import com.dogoo.SystemWeighingSas.model.RoleMapperModel;
import com.dogoo.SystemWeighingSas.model.UserTokenModel;
import com.dogoo.SystemWeighingSas.service.AccountService;
import com.dogoo.SystemWeighingSas.service.RoleService;
import com.dogoo.SystemWeighingSas.unitity.response.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

@Service
@Transactional
public class AccountServiceImpl implements AccountService, UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IAccountDao iAccountDao;
    private final PasswordEncoder passwordEncoder;
    private final PwdGenerator pwdGenerator;
    private final MailUtil mailUtil;
    private final RoleService roleService;
    private final ICustomerDao iCustomerDao;
    private static final String SUBJECT = "[Dogoo Can] - THƯ THÔNG TIN TÀI KHOẢN TRÊN DOGOO CAN";

    public AccountServiceImpl(IAccountDao iAccountDao,
                              PasswordEncoder passwordEncoder,
                              PwdGenerator pwdGenerator,
                              MailUtil mailUtil,
                              RoleService roleService,
                              ICustomerDao iCustomerDao) {
        this.iAccountDao = iAccountDao;
        this.passwordEncoder = passwordEncoder;
        this.pwdGenerator = pwdGenerator;
        this.mailUtil = mailUtil;
        this.roleService = roleService;
        this.iCustomerDao = iCustomerDao;
    }

    @Override
    public UserDetails loadUserByUsername(String screenName) throws UsernameNotFoundException {
        Account account = iAccountDao.findAccountByScreenName(screenName);
        if (account == null) {
            logger.error("account not found in the database");
            throw new UsernameNotFoundException("account not found in the database");
        } else {
            logger.info("account found in the database: {}", screenName);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(account.getRole().name()));

        return new org.springframework.security.core.userdetails.User(account.getScreenName(), account.getPassword(), authorities);
    }

    @Override
    public void createAccount(UserTokenModel tokenModel, AccountMapperModel accountMapperModel) {
        Account account = Constants.SERIALIZER.convertValue(accountMapperModel, Account.class);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setChangePassword(Boolean.FALSE);
        account.setCreateDate(new Timestamp(System.currentTimeMillis()));
        account.setKey(tokenModel.getKey());
        account = iAccountDao.save(account);

        addListRole(account.getAccountId(), accountMapperModel.getRoleList());
    }

    @Override
    public Account getAccountByScreenName(String screenName) {
        return iAccountDao.findAccountByScreenName(screenName);
    }

    @Override
    public Account getAccountByMail(String email) {
        return iAccountDao.findAccountByEmail(email);
    }

    @Override
    public Account getAccountByKey(String key) {
        return iAccountDao.findAccountByKey(key);
    }

    @Override
    public AccountMapperModel getAccountByAccountId(long accountId) {
        Account account = iAccountDao.fetchAccountByAccountId(accountId);
        AccountMapperModel model = Constants.SERIALIZER.convertValue(account, AccountMapperModel.class);
        model.setRoleList(roleService.getListRole(accountId));

        return model;
    }

    @Override
    public ResultResponse<AccountMapperModel> getAccounts(UserTokenModel model,
                                                          Integer limit,
                                                          Integer page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(page, 30, sort);
        Page<Account> accounts = iAccountDao.findAll(pageable);
        long total = iAccountDao.count();
        if (!model.getKey().equals("")) {
            accounts = iAccountDao.findAllCustomer(model.getKey(), pageable);
            total = iAccountDao.countByKey(model.getKey());
        }
        List<AccountMapperModel> list = new ArrayList<>();
        accounts.getContent().forEach(account -> {
            AccountMapperModel model1 = Constants.SERIALIZER.convertValue(account, AccountMapperModel.class);
            model1.setTextRole(listRole(account));
            list.add(model1);
        });
        ResultResponse<AccountMapperModel> resultResponse = new ResultResponse<>();
        resultResponse.setData(list);
        resultResponse.setLimit(limit);
        resultResponse.setPage(page);
        resultResponse.setTotal(total);
        resultResponse.setTotalPage((int) (total / limit));
        return resultResponse;
    }

    @Override
    public List<Account> getAccounts() {
        return iAccountDao.findAll();
    }

    @Override
    public void changePasswordAccount(long accountId, AccountMapperModel model) {
        Account account = iAccountDao.findByAccountId(accountId);
        account.setPassword(passwordEncoder.encode(model.getNewPassword().trim()));
        iAccountDao.save(account);
    }

    @Override
    public void activeAccount(long accountId, AccountMapperModel model) {
        Account account = iAccountDao.findByAccountId(accountId);
        account.setStatus(StatusEnum.valueOf(model.getStatus()));
        iAccountDao.save(account);
    }

    @Override
    public void createAccountCustomer(Customer customer) throws MessagingException {

        String password = pwdGenerator.getPassword();
        Account account = new Account();
        account.setName(customer.getCustomerName());
        account.setCreateDate(new Timestamp(System.currentTimeMillis()));
        account.setScreenName(pwdGenerator.getScreeName(customer.getCustomerName(), null));
        account.setRole(RoleEnum.adminUser);
        account.setPhoneNumber(customer.getPhoneNumber());
        account.setStatus(StatusEnum.active);
        account.setEmail(customer.getEmail());
        account.setPassword(passwordEncoder.encode(password));
        account.setChangePassword(Boolean.FALSE);
        account.setKey(customer.getKey());
        account = iAccountDao.save(account);

        Map<String, Object> model = new HashMap<>();
        model.put("name", customer.getCustomerName());
        model.put("screenName", account.getScreenName());
        model.put("password", password);
        mailUtil.sendSimpleMail(customer.getEmail(),
                SUBJECT, model);

    }

    @Override
    public void updateAccountCustomer(Customer customerHistory, String mail) throws MessagingException {
        if (!customerHistory.getEmail().equals(mail)){
            String password = pwdGenerator.getPassword();
            Account account = iAccountDao.findAccountByEmail(customerHistory.getEmail());
            account.setEmail(mail);
            account.setPassword(passwordEncoder.encode(password));
            account.setChangePassword(Boolean.FALSE);
            account = iAccountDao.save(account);

            Map<String, Object> model = new HashMap<>();
            model.put("name", customerHistory.getCustomerName());
            model.put("screenName", account.getScreenName());
            model.put("password", password);
            mailUtil.sendSimpleMail(mail,
                    SUBJECT, model);
        }
    }

    @Override
    public void updateAccount(long accountId, AccountMapperModel model) {
        Account account = iAccountDao.fetchAccountByAccountId(accountId);
        account.setName(model.getName());
        account.setScreenName(model.getScreenName());

        if (model.getNewPassword() != null) {
            account.setPassword(passwordEncoder.encode(model.getNewPassword()));
        }

        if (model.getStatus() != null) {
            account.setStatus(StatusEnum.valueOf(model.getStatus()));
        }

        account.setRoleAll(Boolean.FALSE);

        iAccountDao.save(account);
        updateListRole(accountId, model.getRoleList());
    }

    @Override
    public void changeRole(long accountId, AccountMapperModel model) {
        Account account = iAccountDao.fetchAccountByAccountId(accountId);
        account.setRoleAll(model.getRoleAll());
        iAccountDao.save(account);
        updateListRole(accountId, model.getRoleList());
    }

    @Override
    public String getScreenName(UserTokenModel model, String name) {
        Customer customer = iCustomerDao.findCustomerByKey(model.getKey());
        return pwdGenerator.getScreeName(customer.getCustomerName(), name);
    }

    @Override
    public void forgetPasswordAccount(String email) throws MessagingException {
        String password = pwdGenerator.getPassword();
        Account account = iAccountDao.findAccountByEmail(email);
        account.setPassword(passwordEncoder.encode(password));
        iAccountDao.save(account);
        Map<String, Object> model = new HashMap<>();
        model.put("name", account.getName());
        model.put("screenName", account.getScreenName());
        model.put("password", password);
        mailUtil.sendSimpleMail(email,
                SUBJECT, model);
    }

    private void addListRole(long accountId, List<RoleMapperModel> list) {
        if (list != null) {
            list.forEach(roleMapperModel -> {
                roleMapperModel.setAccountId(accountId);
                roleService.addRole(roleMapperModel);
            });
        }
    }

    private void updateListRole(long accountId, List<RoleMapperModel> list) {
        if (list != null) {
            list.forEach(roleMapperModel -> {
                if (roleMapperModel.getId() == 0) {
                    roleMapperModel.setAccountId(accountId);
                    roleService.addRole(roleMapperModel);
                } else {
                    roleService.updateRole(roleMapperModel);
                }
            });
        }
    }

    private String listRole(Account account) {

        if (Boolean.FALSE.equals(account.getRoleAll())) {
            List<RoleMapperModel> list = roleService.getListRole(account.getAccountId());

            double size = list.size();
            String role = "";
            double sizeView = (int) list.stream()
                    .filter(RoleMapperModel::isRoleView).count();
            double sizeCreate = (int) list.stream()
                    .filter(RoleMapperModel::isRoleCreate).count();

            double percentView = size > 0 ? (sizeView / size) * 100 : 0;
            double percentCreate = size > 0 ? (sizeCreate / size) * 100 : 0;

            if (percentView >= 50) {
                role += "Xem ";
            }
            if (percentCreate >= 50) {
                role += "Chỉnh sửa ";
            }

            return role;
        }

        return "Xem Chỉnh sửa";

    }
}
