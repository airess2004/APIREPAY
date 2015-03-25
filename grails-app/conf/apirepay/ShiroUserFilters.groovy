package apirepay
import org.apache.shiro.SecurityUtils
import apirepay.ShiroUser

class ShiroUserFilters {

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                //load shiro subject
                def username = SecurityUtils.subject.principal

                //load domain user
                request.currentUser = (username) ? ShiroUser.findByUsername(username) : null

                //continue processing
                return true
            }
        }
    }

}