package apirepay

import org.apache.shiro.authc.AccountException
import org.apache.shiro.authc.SimpleAccount
import org.apache.shiro.authc.UnknownAccountException
import apirepay.ShiroUser

class RestRealm {

    def restService

    static authTokenClass = apirepay.RestToken

    def authenticate(authToken) {
		println  "Attempting to authenticate ${authToken.token} in REST realm..."
        log.info "Attempting to authenticate ${authToken.token} in REST realm..."
        def token = authToken.token

        // Null username is invalid
        if (token == null) {
            throw new AccountException("Null token are not allowed by this realm.")
        }

        // If we don't have user for specified token then user is not authenticated
        def username = restService.getUsernameForToken(token)
        def user = (!username) ?: ShiroUser.findByUsername(username)
        if (!user) {
			println  "No account found for token [${token}]"
            throw new UnknownAccountException("No account found for token [${token}]")
        }
		println  "Found user '${user.username}' in DB"
        log.info "Found user '${user.username}' in DB"

        //ok. Account is found, user is authenticated
        return new SimpleAccount(user.username, user.passwordHash, "RestRealm")
    }

}