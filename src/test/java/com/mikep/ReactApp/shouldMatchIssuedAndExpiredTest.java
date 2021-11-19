package com.mikep.ReactApp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Log4j2
public class shouldMatchIssuedAndExpiredTest {

    @Test
    public void TestCheckIssuedAndExpired() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("secret");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiration = issuedAt.plus(3, ChronoUnit.MINUTES);

        log.info("Issued at: {}", issuedAt);
        log.info("Expires at: {}", expiration);

        String jws = Jwts.builder()
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, "secret").compact();

        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(jws)
                .getBody();

        assertThat(claims.getIssuedAt().toInstant()).isEqualTo(issuedAt);
        assertThat(claims.getExpiration().toInstant()).isEqualTo(expiration);
    }
}