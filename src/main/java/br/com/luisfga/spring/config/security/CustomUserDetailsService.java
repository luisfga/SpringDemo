package br.com.luisfga.spring.config.security;

import br.com.luisfga.spring.business.entities.AppRole;
import br.com.luisfga.spring.business.entities.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class.getSimpleName());

    @Autowired
    public EntityManager em;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        logger.debug("loadUserByUsername");

        AppUser appUser = em.find(AppUser.class, email);
        if (appUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return buildUserForAuthentication(appUser, getUserAuthority(appUser.getRoles()));
    }

    private List<GrantedAuthority> getUserAuthority(Set<AppRole> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach((role) -> {
            roles.add(new SimpleGrantedAuthority(role.toString()));
        });

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }

    private UserDetails buildUserForAuthentication(AppUser appUser, List<GrantedAuthority> authorities) {
        return new User(appUser.getEmail(), appUser.getPassword(), authorities);
    }
}
