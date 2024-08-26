package com.dogoo.SystemWeighingSas;

import com.dogoo.SystemWeighingSas.dao.IAccountDao;
import com.dogoo.SystemWeighingSas.entity.Account;
import com.dogoo.SystemWeighingSas.enumEntity.RoleEnum;
import com.dogoo.SystemWeighingSas.enumEntity.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;

@SpringBootApplication
@EnableAsync
@Slf4j
public class SystemWeighingSasApplication implements CommandLineRunner {

	private final IAccountDao iAccountDao;
    private final PasswordEncoder passwordEncoder;
    @Value("${dogoo.host.name}")
    private String hostName;

    public SystemWeighingSasApplication(IAccountDao iAccountDao, PasswordEncoder passwordEncoder) {
        this.iAccountDao = iAccountDao;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
		SpringApplication.run(SystemWeighingSasApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

        if (iAccountDao.findAccountByScreenName("admin_dogoo") == null) {
            Account account = new Account();
            account.setName("admin dogoo");
            account.setCreateDate(new Timestamp(System.currentTimeMillis()));
            account.setScreenName("admin_dogoo");
            account.setRole(RoleEnum.admin);
            account.setPhoneNumber("");
            account.setStatus(StatusEnum.active);
            account.setEmail("trungnb@dogoo.com.vn");
            account.setPassword(passwordEncoder.encode("dogoo"));
            iAccountDao.save(account);
        }
	}
}
