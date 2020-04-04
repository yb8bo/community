package com.ylhaha.community.controller;

import com.ylhaha.community.dto.AccessTokenDTO;
import com.ylhaha.community.dto.GithubUser;
import com.ylhaha.community.model.User;
import com.ylhaha.community.provider.GitHubProvider;
import com.ylhaha.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * github授权用户登录处理器
 * @author yl
 */
@Controller
public class AuthorizeController {
    @Autowired
    private GitHubProvider gitHubProvider;

    @Value("${github.redirect.uri}")
    private String redirectUri;
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;

    @Autowired
    UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam("code")String code,
                           @RequestParam("state")String state,
                           HttpServletResponse response){
        //对access_token请求的请求参数设置
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        //获取access_token
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);
        //得到认证用户
        GithubUser githubUser = gitHubProvider.getUser(accessToken);
        if(githubUser!=null){
            //认证成功，登录，把数据存入数据库
            User user = new User();

            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setBio(githubUser.getBio());
            user.setAvatarUrl(githubUser.getAvatar_url());
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            userService.createOrUpdate(user);
            //手动创建一个cookie用于用户登录验证
            response.addCookie(new Cookie("token",token));
            return "redirect:index";
        }else {
            //认证失败，携带失败信息
            return "index";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/index";
    }
}
