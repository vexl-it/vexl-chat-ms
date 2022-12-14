package com.cleevio.vexl.common.security.filter;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.common.security.AuthenticationHolder;
import com.cleevio.vexl.common.service.SignatureService;
import com.cleevio.vexl.common.service.query.CheckSignatureValidityQuery;
import com.cleevio.vexl.common.util.NumberUtils;
import com.cleevio.vexl.module.inbox.constant.Platform;
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

    public static final String HEADER_PUBLIC_KEY = "public-key";
    public static final String HEADER_HASH = "hash";
    public static final String HEADER_SIGNATURE = "signature";
    public static final String X_PLATFORM = "X-Platform";
    public static final String HEADER_CRYPTO_VERSION = "crypto-version";

    private final SignatureService signatureService;

    public SecurityFilter(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();

        final String publicKey = request.getHeader(HEADER_PUBLIC_KEY);
        final String hash = request.getHeader(HEADER_HASH);
        final String signature = request.getHeader(HEADER_SIGNATURE);
        final String platform = request.getHeader(X_PLATFORM);
        final int cryptoVersion = NumberUtils.parseIntOrFallback(request.getHeader(HEADER_CRYPTO_VERSION), 1);

        if (signature == null || publicKey == null || hash == null || platform == null || !requestURI.contains("/api/v1/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Platform.valueOf(platform.toUpperCase());

            if (signatureService.isSignatureValid(new CheckSignatureValidityQuery(publicKey, hash, signature), cryptoVersion)) {
                AuthenticationHolder authenticationHolder;

                authenticationHolder = new AuthenticationHolder(null, null);
                authenticationHolder.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationHolder);
            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            handleError(response, "Authentication failed: " + e.getMessage(), 400);
        }

        filterChain.doFilter(request, response);
    }

    protected void handleError(ServletResponse response, String s, int code) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(code);

        ErrorResponse error = new ErrorResponse(Collections.singleton(s), "0");
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, error);
        out.flush();

        throw new RuntimeException();
    }
}
