package com.luizalabs.clientesapi.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        /** Em memoria mas pode-se configurar para tratar no banco de dados
         * usuario: admin senha: admin
         * usuario: luizalabs senha:luizalabs
         */

        if(userName.equals("admin")){
            return new User("admin",
                    "$2a$10$TlF90.I00ii/1HBn12Cvbe4TE0H6Ufhldk.YTBcwKuHOSudwUpYR6",
                    AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN"));
        }else {
            if(userName.equals("luizalabs")) {
                return new User("luizalabs",
                        "$2a$10$FAXejpFBRc3f66ODXIJ1cOBgLWQBPdEI.TRLk2A80Ysmrx9xhoMYu",
                        AuthorityUtils.createAuthorityList("ROLE_USER"));
            } else {
                throw new UsernameNotFoundException("Usuário e senha não confere");
            }
        }
    }
}