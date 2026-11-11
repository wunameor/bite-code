/**
 * 统一为 AJAX 请求附加 JWT。
 * 后端优先读取 Authorization: Bearer <token>，同时兼容 token / user_token。
 * 登录、注册、发码接口在后端已放行，带不带 token 都不影响。
 */
(function () {
    var token = localStorage.getItem('user_token');
    if (!token) {
        return;
    }
    $.ajaxSetup({
        headers: {
            Authorization: 'Bearer ' + token
        }
    });
})();
