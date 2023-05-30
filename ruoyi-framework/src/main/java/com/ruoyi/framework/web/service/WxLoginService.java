package com.ruoyi.framework.web.service;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.AddressUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 微信登录token验证处理
 *
 * @author ruoyi
 */
@Component
public class WxLoginService {
    // 令牌自定义标识
    private final String header = "Authorization";

    // 令牌秘钥
    private final String secret = "wxTokenSecret";

    // 令牌有效期（默认180分钟）
    private final int expireTime = 180;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    //自定义微信前缀
    private static final String WX_PREFIX = "wx";

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取微信用户身份信息
     *
     * @return wx用户信息
     */
    public WxUser getWxUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(WX_PREFIX);
                String userKey = getTokenKey(uuid);
                return redisCache.getCacheObject(userKey); //wx_token:xxxx.xxxx.xxxx
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setWxUser(WxUser user) {
        if (StringUtils.isNotNull(user) && StringUtils.isNotEmpty(user.getUuid())) {
            refreshToken(user);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delWxUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            Claims claims = parseToken(token);
            String uuid = (String) claims.get(WX_PREFIX);
            String userKey = getTokenKey(uuid);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param user 用户信息
     * @return 令牌
     */
    public String createToken(WxUser user) {
        String uuid = IdUtils.fastUUID();
        user.setUuid(uuid);
        setUserAgent(user);
        refreshToken(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put(WX_PREFIX, uuid);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param user
     * @return 令牌
     */
    public void verifyToken(WxUser user) {
        long expireTime = user.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(user);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param user 登录信息
     */
    public void refreshToken(WxUser user) {
        user.setLoginTime(System.currentTimeMillis());
        user.setExpireTime(user.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(user.getUuid());
        redisCache.setCacheObject(userKey, user, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 设置用户代理信息
     *
     * @param user 登录信息
     */
    public void setUserAgent(WxUser user) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr();
        user.setIpaddr(ip);
        user.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        user.setBrowser(userAgent.getBrowser().getName());
        user.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取请求token
     * 设置为公开
     *
     * @param request
     * @return token
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(WX_PREFIX)) {
            token = token.replace(WX_PREFIX, "");
            return token;
        } else {
            return null;
        }
    }

    private String getTokenKey(String uuid) {
        return "wx_token:" + uuid;
    }
}
