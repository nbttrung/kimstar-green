package com.dogoo.SystemWeighingSas.controller;

import com.dogoo.SystemWeighingSas.common.CommonToken;
import com.dogoo.SystemWeighingSas.model.AccountMapperModel;
import com.dogoo.SystemWeighingSas.model.UserTokenModel;
import com.dogoo.SystemWeighingSas.service.AccountService;
import com.dogoo.SystemWeighingSas.service.RoleService;
import com.dogoo.SystemWeighingSas.unitity.response.Response;
import com.dogoo.SystemWeighingSas.unitity.response.ResponseFactory;
import com.dogoo.SystemWeighingSas.validator.AccountValidator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/o/dogoo/account")
public class AccountController {
    private final AccountService accountService;
    private final CommonToken commonToken;
    private final AccountValidator validator;
    private final RoleService roleService;

    public AccountController(AccountService accountService,
                             CommonToken commonToken,
                             AccountValidator validator, RoleService roleService) {
        this.accountService = accountService;
        this.commonToken = commonToken;
        this.validator = validator;
        this.roleService = roleService;
    }


    @GetMapping("/get-list")
    public Response getListAccount(@RequestParam(name = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                   @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                   HttpServletRequest request) {
        try {
            UserTokenModel userTokenModel = getUserToken(request);
            return ResponseFactory.getSuccessResponse(Response.SUCCESS,
                    accountService.getAccounts(userTokenModel, pageSize, page));

        } catch (Exception exception) {
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }


    @PostMapping("/create")
    public Response createAccount(@RequestBody AccountMapperModel accountDto,
                                  HttpServletRequest request,
                                  HttpServletResponse httpServletResponse) {
        try {
            Response response = validator.validatorAdd(getUserToken(request), accountDto);
            if (response == null) {
                accountService.createAccount(getUserToken(request), accountDto);
                return ResponseFactory.getSuccessResponse(Response.SUCCESS);
            }

            httpServletResponse.setStatus(400);
            return response;
        } catch (Exception exception) {
            httpServletResponse.setStatus(400);
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public Response updateAccount(@PathVariable("id") long id,
                                  @RequestBody AccountMapperModel accountDto,
                                  HttpServletRequest request,
                                  HttpServletResponse httpServletResponse) {
        try {
            Response response = validator.validatorUpdate(id, getUserToken(request), accountDto);
            if (response == null) {
                accountService.updateAccount(id, accountDto);
                return ResponseFactory.getSuccessResponse(Response.SUCCESS);
            }

            httpServletResponse.setStatus(400);
            return response;
        } catch (Exception exception) {
            httpServletResponse.setStatus(400);
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }


    /*
    body{
        "email":"admin@gmail.com",
        "password":"admin"
    }
     */

    @PutMapping("/change-password/{id}")
    public Response changePassword(@PathVariable("id") long id,
                                   @RequestBody AccountMapperModel accountDto,
                                   HttpServletResponse httpServletResponse) {
        try {
            Response response = validator.validatorChangePassword(id, accountDto);

            if (response == null) {
                accountService.changePasswordAccount(id, accountDto);
                return ResponseFactory.getSuccessResponse(Response.SUCCESS);
            }

            httpServletResponse.setStatus(400);
            return response;
        } catch (Exception exception) {
            httpServletResponse.setStatus(400);
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }

    @PutMapping("/change-status/{id}")
    public Response changeStatus(@PathVariable("id") long id,
                                 @RequestBody AccountMapperModel accountDto) {
        try {
            accountService.activeAccount(id, accountDto);
            return ResponseFactory.getSuccessResponse(Response.SUCCESS);
        } catch (Exception exception) {
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }

    @GetMapping("/find-account/{id}")
    public Response getAccount(@PathVariable("id") long id) {
        try {
            return ResponseFactory.getSuccessResponse(Response.SUCCESS,
                    accountService.getAccountByAccountId(id));

        } catch (Exception exception) {
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }

    @PutMapping("/change-role/{id}")
    public Response changeRole(@PathVariable("id") long id,
                               @RequestBody AccountMapperModel accountDto) {
        try {
            accountService.changeRole(id, accountDto);
            return ResponseFactory.getSuccessResponse(Response.SUCCESS);
        } catch (Exception exception) {
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }

    @GetMapping("/get-auto-code")
    public Response getAutoCodeCustomer(@RequestParam(name = "name", defaultValue = "", required = false) String name,
                                        HttpServletRequest request,
                                        HttpServletResponse httpServletResponse) {
        try {

            if (name.equals(""))
                return ResponseFactory.getSuccessResponse(Response.SUCCESS, "");

            return ResponseFactory.getSuccessResponse(Response.SUCCESS,
                    accountService.getScreenName(getUserToken(request), name));

        } catch (Exception exception) {
            httpServletResponse.setStatus(400);
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }

    @GetMapping("/get-check-permission")
    public Response getCheckPermission(@RequestParam(name = "path") String path,
                                       @RequestParam(name = "path2", required = false) String path2,
                                       HttpServletRequest request,
                                       HttpServletResponse httpServletResponse) {
        try {
            return ResponseFactory.getSuccessResponse(Response.SUCCESS,
                    roleService.checkRole(getUserToken(request).getAccountId(), getUserToken(request).getKey(),
                            path, path2));

        } catch (Exception exception) {
            httpServletResponse.setStatus(400);
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }

    @GetMapping("/check-screen-name")
    public Response checkScreenName(@RequestParam(name = "screenName") String screenName) {
        try {
            Response response = validator.validatorScreenName(screenName);
            return ResponseFactory.getSuccessResponse(Response.SUCCESS, response != null);

        } catch (Exception exception) {
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }

    @GetMapping("/get-check-add")
    public Response getCheckAdd(@RequestParam(name = "path") String path,
                                HttpServletRequest request,
                                HttpServletResponse httpServletResponse) {
        try {

            return ResponseFactory.getSuccessResponse(Response.SUCCESS, roleService.checkRoleAdd(
                    getUserToken(request).getAccountId(),
                    getUserToken(request).getKey(),
                    path));

        } catch (Exception exception) {
            httpServletResponse.setStatus(400);
            return ResponseFactory.getClientErrorResponse(exception.getMessage());
        }
    }

    private UserTokenModel getUserToken(HttpServletRequest request) {
        return commonToken.getUserToken(request);
    }
}
