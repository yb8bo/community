package com.ylhaha.community.provider;

import com.alibaba.fastjson.JSON;
import com.ylhaha.community.dto.AccessTokenDTO;
import com.ylhaha.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author yl
 */
//@Slf4j
@Component
public class GitHubProvider {


    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json;charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(requestBody)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String string = response.body().string();
            String[] split = string.split("&");
            String tokenStr = split[0];
            String token = tokenStr.split("=")[1];
            return token;
        } catch (Exception e) {
//            log.error("getAccessToken error,{}", accessTokenDTO, e);
            return null;
        }
    }

        public GithubUser getUser (String accessToken){
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.github.com/user?access_token=" + accessToken)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String string = response.body().string();
                return JSON.parseObject(string, GithubUser.class);
            } catch (Exception e) {
//            log.error("getUser error,{}", accessToken, e);
            }
            return null;
        }
    }
