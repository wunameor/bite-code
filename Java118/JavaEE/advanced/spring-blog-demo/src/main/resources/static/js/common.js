$(document).ajaxError(function (event, xhr, options, exc) {
    if (xhr.status === 400) {
        alert("参数校验失败");
    } else if (xhr.status === 401) {
        // 用户未登录
        location.href = "blog_login.html"
    }
});

$(document).ajaxSend(function (e, xhr, opt) {
    var user_token = localStorage.getItem("User_Token");
    xhr.setRequestHeader("User_Token", user_token);
});



function getUserInfo(url) {
    $.ajax({
        url: url,
        type: 'get',
        success: (result) => {
            if (result != null && result.code === 2000 && result.data != null) {
                let userInfo = result.data;
                $(".container .left .card h3").text(userInfo.userName);
                $(".container .left .card a").attr("href", userInfo.githubUrl);
            }
        }
    })
}

