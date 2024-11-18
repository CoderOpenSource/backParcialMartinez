package com.example.examen2.config.jwt;

import com.example.examen2.usuarios.service.UsuarioDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UsuarioDetailsServiceImpl usuarioDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        logger.info("Iniciando JwtRequestFilter para la URL: {}", request.getRequestURI());

        // Ignorar solicitudes OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.info("Solicitud OPTIONS detectada, omitiendo filtro.");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        try {
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                logger.info("JWT Token recibido: {}", jwtToken);
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                logger.info("Nombre de usuario extraído del token: {}", username);
            } else {
                logger.warn("No se encontró un token JWT válido en la cabecera o no comienza con 'Bearer '.");
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.usuarioDetailsService.loadUserByUsername(username);
                logger.info("Detalles del usuario cargados: {}", userDetails.getUsername());

                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    String role = jwtTokenUtil.getRoleFromToken(jwtToken);
                    logger.info("Rol extraído del token: {}", role);

                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );
                    logger.info("Authorities asignadas: {}", authorities);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("Autenticación configurada en el contexto de seguridad.");
                } else {
                    logger.warn("El token JWT no es válido.");
                }
            }
        } catch (ExpiredJwtException e) {
            logger.error("El token JWT ha expirado.", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expirado. Por favor, inicia sesión nuevamente.");
            response.getWriter().flush();
            return; // Detener la ejecución si el token está expirado
        } catch (Exception e) {
            logger.error("Error procesando el token JWT.", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Error en el token JWT.");
            response.getWriter().flush();
            return;
        }

        chain.doFilter(request, response);
        logger.info("Finalizando JwtRequestFilter para la URL: {}", request.getRequestURI());
    }
}
