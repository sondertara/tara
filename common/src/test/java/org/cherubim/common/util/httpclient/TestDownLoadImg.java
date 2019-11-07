package org.cherubim.common.util.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.cherubim.common.util.httpclient.common.HttpConfig;
import org.cherubim.common.util.httpclient.exception.HttpProcessException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 下载demo
 *
 * @author arron
 * @version 1.0
 * @date 2016年6月7日 上午10:29:30
 */
@Slf4j
public class TestDownLoadImg {

    public static void main(String[] args) throws FileNotFoundException, HttpProcessException {
        String imgUrl = "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white_fe6da1ec.png"; //百度logo
        File file = new File("baidu.png");
        HttpClientUtil.down(HttpConfig.custom().url(imgUrl).out(new FileOutputStream(file)));
        if (file.exists()) {
            log.info("图片下载成功了！存放在：" + file.getPath());
        }

        String mp3Url = "http://win.web.rh01.sycdn.kuwo.cn/resource/n1/24/6/707126989.mp3"; //四叶草-好想你
        file = new File("好想你.mp3");
        HttpClientUtil.down(HttpConfig.custom().url(mp3Url).out(new FileOutputStream(file)));
        if (file.exists()) {
            log.info("mp3下载成功了！存放在：" + file.getPath());
        }
    }
}
