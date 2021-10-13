package org.sid.catalogueservice.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTAuthorizationFilter extends OncePerRequestFilter{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        //J'autorise tous les pages qui vient de n'importe quelle domaine de m'envoyer des requêtes
        response.addHeader("Access-Control-Allow-Origin", "*");
        //J'autorise le navigateur à m'envoyer une requêtes qui contient tous ces entêtes
        response.addHeader("Access-Control-Allow-Headers", "Orgin, Accept, X-Request-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, authorization");
        //J'expose tous ces entêtes aux clients http c'est à dire avec du javascript je peux lire la valeur tous ces entêtes
        response.addHeader("Acces-Control-Expose-Headers", "Acces-Control-Allow-Origin, Access-Control-Allow-Credentials, authorization");
        //Lorsque q'une page provenant d'un domaine x tente d'envoyer une requête vers une domaine y, le navigateur envoie d'abord une requête OPTIONS pour connaitre quels sont les autorizations pour les autres domaines.
        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else{String jwtToken = request.getHeader(SecurityParams.JWT_HEADER_NAME);

        if(jwtToken == null || !jwtToken.startsWith(SecurityParams.HEADER_PREFIX)){
            //On va passer au prochain filter et forcément ce filtre va rejeter l'accés à l'application puisque l'utilisateur n'est pas authentifié
            //on a va pas verifier de token on repond avec OK avec les entêtes
            filterChain.doFilter(request, response);
            return;
        }
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecurityParams.SECRET)).build();
        String jwt = jwtToken.split(" ")[1];
        DecodedJWT decodedJWT = verifier.verify(jwt);
        System.out.println(jwt);
        String username = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaims().get("roles").asList(String.class);
        System.out.println("username="+username);
        System.out.println("roles="+roles);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(rn->{
            authorities.add(new SimpleGrantedAuthority(rn));
        });

        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(user);

        filterChain.doFilter(request, response);}
    }

}
