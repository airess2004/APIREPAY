package apirepay

class MyRestTokenAuthenticationFilter extends RestTokenAuthenticationFilter {

    protected String getToken(request) {
        return request.getParameter('token')
    }
}
