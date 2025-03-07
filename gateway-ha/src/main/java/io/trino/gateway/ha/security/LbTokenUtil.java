/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.gateway.ha.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Optional;

public final class LbTokenUtil
{
    private static final Logger log = LoggerFactory.getLogger(LbTokenUtil.class);

    /**
     * Cookie key to pass the token.
     */

    private LbTokenUtil()
    {
    }

    public static boolean validateToken(String idToken, RSAPublicKey publicKey, String issuer, Optional<List<String>> audiences)
    {
        try {
            if (log.isDebugEnabled()) {
                DecodedJWT jwt = JWT.decode(idToken);
                jwt.getClaims().forEach(
                        (key, value) -> log.debug("JWT {} : {}", key, value));
            }

            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            Verification verification =
                    JWT.require(algorithm)
                        .withIssuer(issuer);

            audiences.ifPresent(auds -> verification.withAnyOfAudience(auds.toArray(new String[0])));

            verification.build().verify(idToken);
        }
        catch (Exception exc) {
            log.error("Could not validate token.", exc);
            return false;
        }
        return true;
    }
}
