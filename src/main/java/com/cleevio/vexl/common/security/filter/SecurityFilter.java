package com.cleevio.vexl.common.security.filter;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.common.exception.DigitalSignatureException;
import com.cleevio.vexl.common.security.AuthenticationHolder;
import com.cleevio.vexl.common.service.SignatureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

public class SecurityFilter extends OncePerRequestFilter {

    private final SignatureService signatureService;

    public SecurityFilter(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();

        String publicKey = request.getHeader("public-key");
        String phoneHash = request.getHeader("phone-hash");
        String signature = request.getHeader("signature");

        if (signature == null || publicKey == null || phoneHash == null || !requestURI.contains("/api/v1/offer")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (signatureService.isSignatureValid(publicKey, phoneHash, signature)) {
                AuthenticationHolder authenticationHolder;

                authenticationHolder = new AuthenticationHolder(null, null);
                authenticationHolder.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationHolder);
            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (DigitalSignatureException e) {
            SecurityContextHolder.clearContext();
            handleError(response, "Signature verification failed: " + e.getMessage(), 400);
        }

        filterChain.doFilter(request, response);
    }

    protected void handleError(ServletResponse response, String s, int code) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(code);

        ErrorResponse error = new ErrorResponse(Collections.singleton(s), "0", Collections.emptyMap());
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, error);
        out.flush();

        throw new RuntimeException();
    }
}
