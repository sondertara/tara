package org.cherubim.common.util.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.cherubim.common.util.httpclient.builder.HttpClientBuilder;
import org.cherubim.common.util.httpclient.common.*;
import org.cherubim.common.util.httpclient.exception.HttpProcessException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用HttpClient模拟发送（http/https）请求
 *
 * @author huangxioahu
 * @version 1.0
 */
@Slf4j
public class HttpClientUtil {

    /**
     * 默认采用的http协议的HttpClient对象
     */
    private static HttpClient clientHttp;

    /**
     * 默认采用的https协议的HttpClient对象
     */
    private static HttpClient clientHttps;

    static {

        try {
            clientHttp = HttpClientBuilder.custom().build();
            clientHttps = HttpClientBuilder.custom().ssl().build();
        } catch (HttpProcessException e) {
            log.error("exception", e);
        }
    }

    /**
     * 判定是否开启连接池、及url是http还是https <br> 如果已开启连接池，则自动调用build方法，从连接池中获取client对象<br>
     * 否则，直接返回相应的默认client对象<br>
     *
     * @param config 请求参数配置
     * @throws HttpProcessException http处理异常
     */
    private static void create(HttpConfig config) throws HttpProcessException {
        //如果为空，设为默认client对象
        if (config.client() == null) {
            if (config.url().toLowerCase().startsWith("https://")) {
                config.client(clientHttps);
            } else {
                config.client(clientHttp);
            }
        }
    }


    /**
     * 以Get方式，请求资源或服务
     *
     * @param client   client对象
     * @param url      资源地址
     * @param headers  请求头信息
     * @param context  http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String get(HttpClient client, String url, Header[] headers, HttpContext context,
                             String encoding) throws HttpProcessException {
        return get(HttpConfig.custom().client(client).url(url).headers(headers).context(context)
                .encoding(encoding));
    }

    /**
     * 以Get方式，请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回结果
     * @throws HttpProcessException http处理异常
     */
    public static String get(HttpConfig config) throws HttpProcessException {
        return send(config.method(HttpMethods.GET));
    }

    /**
     * 以Post方式，请求资源或服务
     *
     * @param client   client对象
     * @param url      资源地址
     * @param headers  请求头信息
     * @param parasMap 请求参数
     * @param context  http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String post(HttpClient client, String url, Header[] headers,
                              Map<String, Object> parasMap, HttpContext context, String encoding)
            throws HttpProcessException {
        return post(HttpConfig.custom().client(client).url(url).headers(headers).map(parasMap)
                .context(context).encoding(encoding));
    }

    /**
     * 以Post方式，请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String post(HttpConfig config) throws HttpProcessException {
        return send(config.method(HttpMethods.POST));
    }

    /**
     * 以Put方式，请求资源或服务
     *
     * @param client   client对象
     * @param url      资源地址
     * @param parasMap 请求参数
     * @param headers  请求头信息
     * @param context  http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String put(HttpClient client, String url, Map<String, Object> parasMap,
                             Header[] headers, HttpContext context, String encoding) throws HttpProcessException {
        return put(HttpConfig.custom().client(client).url(url).headers(headers).map(parasMap)
                .context(context).encoding(encoding));
    }

    /**
     * 以Put方式，请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String put(HttpConfig config) throws HttpProcessException {
        return send(config.method(HttpMethods.PUT));
    }

    /**
     * 以Delete方式，请求资源或服务
     *
     * @param client   client对象
     * @param url      资源地址
     * @param headers  请求头信息
     * @param context  http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String delete(HttpClient client, String url, Header[] headers,
                                HttpContext context, String encoding) throws HttpProcessException {
        return delete(HttpConfig.custom().client(client).url(url).headers(headers).context(context)
                .encoding(encoding));
    }

    /**
     * 以Delete方式，请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String delete(HttpConfig config) throws HttpProcessException {
        return send(config.method(HttpMethods.DELETE));
    }

    /**
     * 以Patch方式，请求资源或服务
     *
     * @param client   client对象
     * @param url      资源地址
     * @param parasMap 请求参数
     * @param headers  请求头信息
     * @param context  http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String patch(HttpClient client, String url, Map<String, Object> parasMap,
                               Header[] headers, HttpContext context, String encoding) throws HttpProcessException {
        return patch(HttpConfig.custom().client(client).url(url).headers(headers).map(parasMap)
                .context(context).encoding(encoding));
    }

    /**
     * 以Patch方式，请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String patch(HttpConfig config) throws HttpProcessException {
        return send(config.method(HttpMethods.PATCH));
    }

    /**
     * 以Head方式，请求资源或服务
     *
     * @param client   client对象
     * @param url      资源地址
     * @param headers  请求头信息
     * @param context  http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String head(HttpClient client, String url, Header[] headers, HttpContext context,
                              String encoding) throws HttpProcessException {
        return head(HttpConfig.custom().client(client).url(url).headers(headers).context(context)
                .encoding(encoding));
    }

    /**
     * 以Head方式，请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String head(HttpConfig config) throws HttpProcessException {
        return send(config.method(HttpMethods.HEAD));
    }

    /**
     * 以Options方式，请求资源或服务
     *
     * @param client   client对象
     * @param url      资源地址
     * @param headers  请求头信息
     * @param context  http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String options(HttpClient client, String url, Header[] headers,
                                 HttpContext context, String encoding) throws HttpProcessException {
        return options(HttpConfig.custom().client(client).url(url).headers(headers).context(context)
                .encoding(encoding));
    }

    /**
     * 以Options方式，请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String options(HttpConfig config) throws HttpProcessException {
        return send(config.method(HttpMethods.OPTIONS));
    }

    /**
     * 以Trace方式，请求资源或服务
     *
     * @param client   client对象
     * @param url      资源地址
     * @param headers  请求头信息
     * @param context  http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String trace(HttpClient client, String url, Header[] headers, HttpContext context,
                               String encoding) throws HttpProcessException {
        return trace(HttpConfig.custom().client(client).url(url).headers(headers).context(context)
                .encoding(encoding));
    }

    /**
     * 以Trace方式，请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String trace(HttpConfig config) throws HttpProcessException {
        return send(config.method(HttpMethods.TRACE));
    }

    /**
     * 下载文件
     *
     * @param client  client对象
     * @param url     资源地址
     * @param headers 请求头信息
     * @param context http上下文，用于cookie操作
     * @param out     输出流
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static OutputStream down(HttpClient client, String url, Header[] headers,
                                    HttpContext context, OutputStream out) throws HttpProcessException {
        return down(
                HttpConfig.custom().client(client).url(url).headers(headers).context(context).out(out));
    }

    /**
     * 下载文件
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static OutputStream down(HttpConfig config) throws HttpProcessException {
        if (config.method() == null) {
            config.method(HttpMethods.GET);
        }
        return fmt2Stream(execute(config), config.out());
    }

    /**
     * 上传文件
     *
     * @param client  client对象
     * @param url     资源地址
     * @param headers 请求头信息
     * @param context http上下文，用于cookie操作
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String upload(HttpClient client, String url, Header[] headers,
                                HttpContext context) throws HttpProcessException {
        return upload(
                HttpConfig.custom().client(client).url(url).headers(headers).context(context));
    }

    /**
     * 上传文件
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String upload(HttpConfig config) throws HttpProcessException {
        if (config.method() != HttpMethods.POST && config.method() != HttpMethods.PUT) {
            config.method(HttpMethods.POST);
        }
        return send(config);
    }

    /**
     * 查看资源链接情况，返回状态码
     *
     * @param client  client对象
     * @param url     资源地址
     * @param headers 请求头信息
     * @param context http上下文，用于cookie操作
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static int status(HttpClient client, String url, Header[] headers, HttpContext context,
                             HttpMethods method) throws HttpProcessException {
        return status(HttpConfig.custom().client(client).url(url).headers(headers).context(context)
                .method(method));
    }

    /**
     * 查看资源链接情况，返回状态码
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static int status(HttpConfig config) throws HttpProcessException {
        return fmt2Int(execute(config));
    }

    /**
     * 请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    public static String send(HttpConfig config) throws HttpProcessException {
        return fmt2String(execute(config), config.encodeOut());
    }

    /**
     * 请求资源或服务，返回HttpResult对象
     *
     * @param config 请求参数配置
     * @return 返回HttpResult处理结果
     * @throws HttpProcessException http处理异常
     */
    public static HttpResult sendAndGetResp(HttpConfig config) throws HttpProcessException {
        Header[] reqHeaders = config.headers();
        //执行结果
        HttpResponse resp = execute(config);

        HttpResult result = new HttpResult(resp);
        result.setResult(fmt2String(resp, config.encodeOut()));
        result.setReqHeaders(reqHeaders);

        return result;
    }

    /**
     * 请求资源或服务
     *
     * @param config 请求参数配置
     * @return 返回HttpResponse对象
     * @throws HttpProcessException http处理异常
     */
    private static HttpResponse execute(HttpConfig config) throws HttpProcessException {
        //获取链接
        create(config);
        HttpResponse resp = null;

        try {
            //创建请求对象
            HttpRequestBase request = getRequest(config);

            //设置超时
            request.setConfig(config.requestConfig());

            //设置header信息
            request.setHeaders(config.headers());

            //判断是否支持设置entity(仅HttpPost、HttpPut、HttpPatch支持)
            if (HttpEntityEnclosingRequestBase.class.isAssignableFrom(request.getClass())) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();

                //装填参数
                HttpEntity entity = Utils.map2HttpEntity(nvps, config.map(), config.encodeIn());

                //设置参数到请求对象中
                ((HttpEntityEnclosingRequestBase) request).setEntity(entity);

                log.info("请求地址：" + config.url());
                if (nvps.size() > 0) {
                    log.info("请求参数：" + nvps.toString());
                }
                if (config.json() != null) {
                    log.info("请求参数：" + config.json());
                }
            } else {

                int idx = config.url().indexOf("?");
                log.info(
                        "请求地址：" + config.url().substring(0, (idx > 0 ? idx : config.url().length())));
                if (idx > 0) {
                    log.info("请求参数：" + config.url().substring(idx + 1));
                }
            }
            //执行请求操作，并拿到结果（同步阻塞）
            resp = (config.context() == null) ? config.client().execute(request)
                    : config.client().execute(request, config.context());

            if (config.isReturnRespHeaders()) {
                //获取所有response的header信息
                config.headers(resp.getAllHeaders());
            }

            //获取结果实体
            return resp;

        } catch (IOException e) {
            throw new HttpProcessException(e);
        }
    }

    /**
     * 转化为字符串
     *
     * @param resp     响应对象
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    private static String fmt2String(HttpResponse resp, String encoding)
            throws HttpProcessException {
        String body = "";
        try {
            if (resp.getEntity() != null) {
                // 按指定编码转换结果实体为String类型
                body = EntityUtils.toString(resp.getEntity(), encoding);
                log.info(body);
            } else {//有可能是head请求
                body = resp.getStatusLine().toString();
            }
            EntityUtils.consume(resp.getEntity());
        } catch (IOException e) {
            throw new HttpProcessException(e);
        } finally {
            close(resp);
        }
        return body;
    }

    /**
     * 转化为数字
     *
     * @param resp 响应对象
     * @return 返回处理结果
     * @throws HttpProcessException http处理异常
     */
    private static int fmt2Int(HttpResponse resp) throws HttpProcessException {
        int statusCode;
        try {
            statusCode = resp.getStatusLine().getStatusCode();
            EntityUtils.consume(resp.getEntity());
        } catch (IOException e) {
            throw new HttpProcessException(e);
        } finally {
            close(resp);
        }
        return statusCode;
    }

    /**
     * 转化为流
     *
     * @param resp 响应对象
     * @param out  输出流
     * @return 返回输出流
     * @throws HttpProcessException http处理异常
     */
    private static OutputStream fmt2Stream(HttpResponse resp, OutputStream out)
            throws HttpProcessException {
        try {
            resp.getEntity().writeTo(out);
            EntityUtils.consume(resp.getEntity());
        } catch (IOException e) {
            throw new HttpProcessException(e);
        } finally {
            close(resp);
        }
        return out;
    }

    /**
     * 根据请求方法名，获取request对象
     *
     * @return 返回Http处理request基类
     */
    private static HttpRequestBase getRequest(HttpConfig config) {
        HttpRequestBase request;
        final HttpMethods method = config.method();
        final String url = config.url();
        switch (method) {
            case GET:

                config.url(Utils.getUrlParams(url, config.map()));
                request = new HttpGet(config.url());

                break;
            case POST:
                request = new HttpPost(url);
                break;
            case HEAD:
                request = new HttpHead(url);
                break;
            case PUT:
                request = new HttpPut(url);
                break;
            case DELETE:
                request = new HttpDelete(url);
                break;
            case TRACE:
                request = new HttpTrace(url);
                break;
            case PATCH:
                request = new HttpPatch(url);
                break;
            case OPTIONS:
                request = new HttpOptions(url);
                break;
            default:
                request = new HttpPost(url);
                break;
        }
        return request;
    }

    /**
     * 尝试关闭response
     *
     * @param resp HttpResponse对象
     */
    private static void close(HttpResponse resp) {
        try {
            if (resp == null) {
                return;
            }
            //如果CloseableHttpResponse 是resp的父类，则支持关闭
            if (CloseableHttpResponse.class.isAssignableFrom(resp.getClass())) {
                ((CloseableHttpResponse) resp).close();
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public static void main(String[] args) {

        try {
            Header[] headers = HttpHeader.custom().contentType(ContentType.CONTENT_TYPE_FORM)
                    .build();
            String s = "龙泉驿区双林路&河南省-平顶山市-新华区-长安大道-长安大道&广东省-惠州市-惠城区-河南岸街道银岭路-水岸花都(银岭路)&广东省-深圳市-南山区-北环大道-北环大道&清河街道淮河路颍州区人民医院&河南省-郑州市-二七区龙湖镇郑州银行门口--二七区龙湖镇郑州银行门口&东水路与东大路交叉口东水路与东大路交叉口&河南省-郑州市-管城回族区-经北五路-81号水果&广东省-惠州市-惠城区-西堤路-维也纳酒店地下停车场&台屿路台屿小区3栋&广东省-汕尾市-海丰县-324国道-324国道&X312新蒲新区永乐镇卫生院&柯坦镇城池埂城池埂城池埂&陶庄路陶庄路&滨湖镇X002岗头(公交站)&龙山街道松石南路75号百吉(松石南路)&湖北省-恩施土家族苗族自治州-恩施市-呼北线-火车站商贸小区12-11号&临沂北路临沂路山海路(公交站)&解放西路解放西路&江苏省-连云港市-赣榆区-青口镇时代东路-赣榆县时代路农贸大市场&四川省-成都市-金堂县-赵镇街道下横街-成都市郫县千层鲜肉酥锅魁&广东省-中山市-中山市-坦洲镇裕胜村-裕胜村裕胜村&辽宁省-沈阳市-于洪区-迎宾路-停车场(于洪区城乡管理局西)&中兴街道南京南街顶盛国际花园物业服务中心&河南省-焦作市-武陟县-木城街道和平路-面条房&贵州省-黔东南苗族侗族自治州-黄平县-重安镇麦道-麦道麦道&桔山街道机场大道蓝天花园&广东省-深圳市-宝安区-西乡大道-西乡大道&西川乡西川线西川乡邮政局&小北三东路小北三东路&辽宁省-沈阳市-和平区-镇新街高平路十字路口-不详&广东省-深圳市-福田交通枢纽公交站--福田交通枢纽公交站&运河东一路与新桥路交叉口运河东一路与新桥路交叉口&笃工街道景星北街万达广场C(景星北街)&四川省-绵阳市-涪城区-宇虹北街-鸿源足浴(宇虹北街)&黄河路山东高青县城&河南省-商丘市-梁园区-中州路-农乡地锅城&上海市-松江区-加海路-加海路&四川省-巴中市-巴州区-江北街道江北大道-巴人广场综合管理室&道义街道蒲昌路沈阳市蒲昌路&石油路金银湾CNG加气站&泉山街道泉山路化肥厂(公交站)&广东省-深圳市-宝安区-光明街道光明大街-深圳市光明新区光明爱乐唱艺术培训中心&转塘街道达公园达公园达公园&广东省-惠州市-惠东县平山镇老县中红绿灯-惠东县平山镇老县中红绿灯-惠东县平山镇老县中红绿灯&木耳镇空港大道空港乐园&颐苑路邓川牛奶&涂山镇腾龙大道国际社区&京沪高速京沪高速&文化路文化路&胜利路街道胜利路一环路青峰路口(公交站)&广东省-汕头市-潮南区-324国道-324国道&佐坝乡X069宿松县佐坝乡王岭村民委员会&四川省-内江市-资中县乐双路--资中县乐双路&河南省-郑州市-金水区-金水路与中州大道交叉口-金水路与中州大道交叉口&广东省-汕尾市-海丰县-赤坑镇S241-赤坑客运站&广东省-深圳市-宝安区-沙井镇-不详&郭店街道S102珍食斋甏肉干饭&北京市-市辖区-朝阳区广台路-朝阳区广台路-朝阳区广台路&河南省-安阳市-林州市-茶店镇茶店西沟-茶店西沟茶店西沟&风雨坛街道风雨坛街保利大都会装修&四川省-成都市-温江区-金马镇金马镇-金马镇&河南省-新乡市-卫滨区-平原镇唐庄村-唐庄村唐庄村&湖北省-荆州市-开发区唐桥镇--开发区唐桥镇&海尔大道海尔图文&河南省-开封市-鼓楼区-福利院门口-福利院门口&虞山镇香山北路太保人寿理赔中心&东城街道盈峰广场a座&官桥镇福官南路278号南安市官桥镇代售点&石台路新华・学府春天(1号门)&326国道326国道&河南省-开封市-祥符区-科教大道-飞扬艺术中心&沪陕高速沪陕高速&云陵镇江云路北市菜市场&琅琊西路建设大厦(西)(公交站)&新华街道文化路金融大厦&解放路颐华苑小区1号楼&四川省-南充市-嘉陵区-桥龙乡小沟-小沟小沟&锦湖街道虹桥北路瑞安收费站(G15沈海高速出口)&四川省-成都市-简阳市-养马镇花园干道-简阳市养马镇中心幼儿园&广东省-佛山市-南海区-里水镇盛平西路-佛山市穗洲通讯器材有限公司&河南省-开封市-尉氏县-蔡庄镇高庄村-高庄村高庄村&赤水河谷旅游公路美酒河服务站&不详&猴场苗族布依族乡水黄公路乡里乡亲(猴场乡服务站)&四川省-成都市-武侯区-双楠街道大石南路-石南超市&城关街道新市中路三元税务(公交站)&江苏省-常州市-新北区-龙江路高架-龙江路高架&明光路街道明光路明光小区&河北省-石家庄市-长安区胜利北街--长安区胜利北街&四川省-成都市-郫都区-郫县立交桥-郫县收费站&青龙潭路方兴园34栋&星海湾街道高尔基路小天鹅干洗(高尔基路)&江西省-宜春市-铜鼓县-温泉镇城北路-379号天线宝宝幼儿园&四川省-成都市-金堂县-赵镇街道金泉路-徐三姐茶馆&四川省-成都市-青白江区-清泉镇清泉镇-清泉镇&河北省-衡水市-景县-景德公路-不详&湖北路利港银河广场&屯光镇S103篁墩村群众娱乐活动中心&广东省-惠州市-博罗县-湖镇镇S244-章记批发部&郑山街道朱果后村朱果后村朱果后村&洪山街道洪山南路一巷毕节第十二小学&广东省-惠州市-惠东县-稔山镇石化大道东-惠东碧桂园十里银滩浪漫假期酒店&广东省-东莞市-东莞市-东部快速干线-寮城中路出口&浙江省-温州市-鹿城区-藤桥镇溪江路-105号欧丽亚贸易&四川省-成都市-青羊区-成温邛辅道-成温邛辅道&河南省-鹤壁市-淇滨区-金山大道-金山大道&河滨南路三安首作售楼处&广东省-中山市-中山市-中山市东升镇镇南路-8号&土主镇王家坪王家坪王家坪&广东省-深圳市-龙岗区-龙岗街道龙岗大道-赏艺(长盛街)&广东省-佛山市-禅城区-东风路-2号禅城区石湾镇&连霍高速三桥收费站&河南省-焦作市-马村区解放东路新天市场门口-马村区解放东路新天市场门口-马村区解放东路新天市场门口&东城街道迎春街重庆市巴川中学(北门)&广东省-深圳市-龙岗区-永香路-永香路&广东省-深圳市-宝安区-光明街道茶林路-一嗨租车(光明城高铁站便捷点)&河南省-平顶山市-湛河区-南环路街道光明路-建井一处家属院(西1门)&江苏省-连云港市-海州区-云台街道云善路-中共云台乡委员会&新添大道北段幸福里2栋&龙岗区龙岗大道高途能源加油站&皋城路万达广场(公交站)&农林街道竹丝岗二马路5号广深高速公路有限公司&绍庆街道大路坪大路坪大路坪&福建省-漳州市-南靖县-厦蓉高速-厦蓉高速&广东省-惠州市-惠城区-惠州大道-(小金口段)114号&江苏省-扬州市-江苏省扬州市邗江区四季园小区-江苏省扬州市邗江区四季园小区-江苏省扬州市邗江区四季园小区&广东省-惠州市-惠城区-数码园立交-数码园立交&河北省-秦皇岛市-海港区102国道和东港路交叉口-海港区102国道和东港路交叉口-海港区102国道和东港路交叉口&广南线圣堂人民法院&四川省-成都市-金牛区-交大路-118号赫本花道&四川省-成都市-武侯区广福路-武侯区广福路-武侯区广福路&昌化路与人民一路交叉口昌化路与人民一路交叉口&拱辰街道学园北路福建莆田新二中&广东省-阳江市-江城区-平冈镇S277-平岗法院&四川省-成都市-金牛区-五里墩路-五里墩路&福建省-泉州市-洛江区-洛阳桥-洛阳桥&河南省-郑州市-郑东新区东岸上景 -郑东新区东岸上景 -郑东新区东岸上景&金竹立交桥金竹立交桥&广东省-惠州市-惠城区-三环南路-三环南路&石油路万科锦程(东北门)&河北省-石家庄市-桥西区友谊大街正港路口--桥西区友谊大街正港路口&蕉南街道南漷路131号南际公园(公交站)&东埔街道文昌路儿童公园(公交站)&Y005平胜小学&江苏省-连云港市-东海县-青年路-青年路&上海市-闵行区-新骏北路-新骏北路&广东省-阳江市-广东省阳江市江城区永斌-广东省阳江市江城区永斌-广东省阳江市江城区永斌&河北省-保定市-锦绣区朝阳大街贵友超市门口--锦绣区朝阳大街贵友超市门口&河南省-周口市-太康县-城关回族镇谢安中路-建北卫生室(谢安中路)&河北省-唐山市-丰润区-唐山机场立交桥-唐山机场收费站&马鼻镇浮曦村浮曦村浮曦村&渝南大道渝南汽车市场C5&虎头岩隧道虎头岩隧道&四川省-成都市-温江区-柳平二街-柳平二街&格沙屯停车区格沙屯停车区&麻园街道清毕路毕节金海湖新区政务服务中心&河南省-驻马店市-西平县-西平大道东段-西平大道东段&江苏省-常州市-钟楼区-广成路-清潭新村11栋&湖和大道谐音具体不详&西门街道北京西路凯里市中博广场管理办&广东省-惠州市-博罗县-园洲镇服装一路-日之泉圆州农贸市场送水部&山大北路知袜郎(山大北路)&广东省-深圳市-南山区-白石洲道-白石洲道&金阳南路金阳野鸭塘后勤基地&车墩镇&文峰街道解放路鸿基国际大酒店(解放路店)&广东省-惠州市-惠城区-潼侨镇新华路-惠州市楚明昌电子有限公司&四川省-成都市-成华区-建设路街道建和路-星期5&湖北省-襄阳市-樊城区-航宇路-航宇路&江苏省-南通市-海安县-李堡镇S226-宝聚饭店&马家湾互通马家湾互通&天津市-滨海新区-津沽公路-津沽公路&奈古山路与昆明路交叉口奈古山路与昆明路交叉口&内环快速路内环快速路&捷南路捷南路&天津市-县-宁河县江碗口村-宁河县江碗口村-宁河县江碗口村&湖北省-武汉市-硚口区-江汉桥-江汉桥&北京市-朝阳区-上清立交桥-上清收费站&广东省-汕尾市-陆丰市-碣石镇北园路-玄武山-三台石&河南省-开封市-鼓楼区-黄河路南段-48号&油榨街贵阳银行(油炸社区支行)&重庆市-南岸区-长生桥镇通江大道-茶园新区&真南路704号&河南省-郑州市-金水区-北三环-郑州市郑东新区龙湖区隧道管理中心&仙女河北路与南阳湖街交叉口仙女河北路与南阳湖街交叉口&济北街道华阳路济北开发区派出所&新兴镇红草沟村红草沟村红草沟村&青年大街鸿利河畔花园(东南3门)&广东省-惠州市-惠阳区淡水-惠阳区淡水-惠阳区淡水&湖北省-十堰市-竹山县-麻安高速-麻安高速&湖南省-益阳市-市辖区G5513高速-市辖区G5513高速-市辖区G5513高速&万里路&城东街泰禾广场停车场&广东省-惠州市-惠东县-大岭镇小岭路-惠东县大岭镇群发鞋厂&四川省-成都市-武侯区-肖家河街道肖家河街-元亨堂化妆品公司&德林街二段与九三路交叉口德林街二段与九三路交叉口&龙山街道巢湖路红灯笼酒店&北京路北京路&广东省-汕尾市-城区海汕公路-城区海汕公路-城区海汕公路&乐城街道人民南路乐昌体育馆&湖南省-长沙市-浏阳市-车站西路-大卫美术&206国道206国道&河北省-沧州市-任丘市-新华路街道新华路-东大门&灵源街道长安路灵源曾林社区居委会&浙江省-杭州市-滨江区-滨盛路-滨盛路&广东省-深圳市-龙岗区-爱联二路-爱联二路&义井路窦小桥小区23栋&河北省-廊坊市-广阳区-廊坊经济技术开发区李四光道-旭辉十九城邦(南门)&四川省-成都市-双流区-天府大道南段辅路-成都市天府新区政务服务中心停车场&河北省-石家庄市-长安区-体育北大街与丰收路交叉口-体育北大街与丰收路交叉口&温泉镇温泉镇温泉镇&四川省-成都市-温江区-花都大道-花都大道&柏苑路柏苑路&江苏省-宿迁市-沭阳县-沈阳路-千寻美饰美甲(千寻润玛店)&彩霞街彩霞街&洪山镇X010洪山镇人民政府&105国道105国道&河南省-郑州市-惠济区-古荥镇田坡-田坡田坡&广东省-深圳市-龙华区-大浪北路-大浪北路&湖北省-荆州市-沙市区-文化坊-文化坊&广东省-深圳市-龙华区-南坪快速路-南坪快速路&河南省-开封市-尉氏县-高速口往北100米-高速口往北100米&河北省-唐山市-路北区-河西路-河西路与龙华路交叉口&河南省-南阳市-桐柏县-城关镇淮源大道-三和大酒店&雪山镇X778雪山镇卫生院&河北省-衡水市-故城县京都国际酒店门口-故城县京都国际酒店门口-故城县京都国际酒店门口&广东省-惠州市-惠阳区-秋长街道发湖村-发湖村发湖村&三墩收费站&滁水镇行农业银行&南门东小街一江种业(陆良县财政局东)&河南省-许昌市-河南省许昌市禹州市吟唱路-河南省许昌市禹州市吟唱路-河南省许昌市禹州市吟唱路&四川省-成都市-武侯区-武侯大道三河段-自强汽修厂&河南省-安阳市-文峰区-光明路-光明路&广东省-惠州市-惠城区河南岸-惠城区河南岸-惠城区河南岸&河南省-郑州市-荥阳市-京城路与繁荣街交叉口-京城路与繁荣街交叉口&启东市&十八里铺镇曹老庄村曹老庄村曹老庄村&碑廓镇碑廓中路中国邮政储蓄银行(日照市碑廓营业所)&佟二堡镇皮草东大街灯塔佟二堡皮革城商务酒店&长江大道朗月煤机装备制造有限责任公司&湖南省-邵阳市-新邵县-小塘镇张家-张家张家&悦来街道悦来大道公共厕所&四川省-成都市-四川省成都市金牛区动物园立交硚口-四川省成都市金牛区动物园立交硚口-四川省成都市金牛区动物园立交硚口&浙江省-温州市-乐清市-建设中路-香格里拉香衣格&河北省-邯郸市-广平县-东张孟乡刘屯村-刘屯村刘屯村&湖北省-随州市-曾都区-金河新苑小区-金河新苑小区&四川省-成都市-金牛区-九里堤街道九里堤北路-醉爱酒吧(九里堤北路)&北乡镇Y621乐昌市第一水泥厂&东风乡S207郸城县东风乡敬老院&东升大道保元城市广场&石杨镇S206石杨中学&河南省-新乡市-红旗区-西街街道西街-my？manicure美甲(新丹尼斯店)&江苏省-盐城市-大丰区-S332-大丰市童乐玩具厂&博铺街道梅化路吴川市博铺街道财政结算中心&四川省-成都市-金牛区-驷马桥街道马鞍东路-恒大曹家巷广场停车场&解放北路解放北路&104国道104国道&达道湾街道千山西路凯兴启明农贸市场(公交站)&四川省-成都市-金牛区-人民北路-人民北路&河南省-南阳市-镇平县-建设大道-镇平县人民医院&银湖中路保兴湾1栋&北湖滨路华尔道夫世鸿大酒店停车场&南津街街道希尔安大道希尔安房产(希尔安大道)&湖北省-武汉市-洪山区-高新二路-高新二路&花山路与澳柯玛大道交叉口花山路与澳柯玛大道交叉口&江苏省-南京市-栖霞区华港祈福东路-栖霞区华港祈福东路-栖霞区华港祈福东路&广东省-深圳市-宝安区-松岗街道松岗大道-湖北谷城十堰&广东省-佛山市-南海区-大沥镇-广东省佛山市南海区大沥镇&顶云街道沪瑞线岭秀国际书香苑&樟木头镇樟木头镇樟木头镇&延安路延安路派出所爵士蓝岛警务室&河南省-郑州市-二七区-大学路街道大学北路-大学路中原路(公交站)&汝湖服务区汝湖服务区&广西壮族自治区-贺州市-八步区-八步街道八达西路-停车场(公交大厦东北)&刺桐大桥刺桐大桥&广东省-惠州市-惠阳区淡水镇-惠阳区淡水镇-惠阳区淡水镇&广东省-惠州市-惠阳区具体位置不详--惠阳区具体位置不详&上街镇上街上街镇政府(公交站)&广东省-中山市-西区龙关水悦西区--西区龙关水悦西区&广东省-汕尾市-海丰县-沈海高速-沈海高速&中山中路花果园U区2栋&昭阳路2号&广东省-惠州市-惠阳区-淡水街道土湖村-土湖村土湖村&湖北省-随州市-曾都区-三座桥巷-三座桥巷&广东省-惠州市-惠阳区-淡水街道泗水路-维也纳酒店停车场&威宁彝族回族苗族自治县兴隆乡&玉屏街道沿江路古镇徐家饭庄&上南路上南路&河北省-沧州市-孟村回族自治县-孟村镇民族街-38号中共孟村回族自治县委员会&渔安立交渔安立交&广东省-广州市-黄埔区-黄埔街道黄埔东路-东升图文广告(黄埔店)&振兴南路振兴南路&四川省-成都市-新都区-高速路-高速路&贵阳市公安局人民警察训练部与黔灵山路交叉口&民富路紫薇星(云岭小区分店)&河北省-保定市-河北省保定市易县南环路金色年华门口-河北省保定市易县南环路金色年华门口-河北省保定市易县南环路金色年华门口&锦绣路锦绣路&东浦路东浦养生馆&遵义市汇川区人民路9号&四川省-成都市-崇州市-文化街-文化街&广东省-佛山市-南海区-二广高速-二广高速&湖北省-黄冈市-武穴市高速-武穴市高速-武穴市高速&寨乐镇胡家坝胡家坝胡家坝&河南省-许昌市-长葛市-市辖区任庄村-任庄村任庄村&重庆市-丰都县-沪渝南线高速-沪渝南线高速&广东省-深圳市-光明新区龙大高速-光明新区龙大高速-光明新区龙大高速&河北省-唐山市-玉田县-繁荣路-玉田县卫生局&广东省-惠州市-惠阳区数码人-惠阳区数码人-惠阳区数码人&广东省-阳江市-阳东县广雅路-阳东县广雅路-阳东县广雅路&下山镇X646公共厕所&河南省-信阳市-浉河区西宫山大道-浉河区西宫山大道-浉河区西宫山大道&广东省-惠州市-博罗县-龙华镇龙华镇-龙华镇&沙头镇小溪村小溪村小溪村&四川省-成都市-都江堰市-尚阳大道-勤俭路尚阳大道路口(公交站)&河南省-南阳市-邓州市-古城街道三贤路-中原银行(邓州支行)&河北省-保定市-市辖区-白沟新城-不详&长汀服务区停车场(长汀服务区)&市府大路市府大路(地铁站)&斗篷山路京都旅馆&广东省-深圳市-宝安区-宝安大道海鲜市场-不详&天津路凤凰城10号楼&天门镇朱村朱村朱村&广东省-汕尾市-海丰县324国道--海丰县324国道&南城街道南大街南大街石油公司&内蒙古自治区-兴安盟-乌兰浩特市-和平街道乌兰东街-齐丰游戏&凤山路与淮南路交叉口凤山路与淮南路交叉口&四川省-雅安市-四川省雅安市雨城区碧峰峡-四川省雅安市雨城区碧峰峡-四川省雅安市雨城区碧峰峡&龙坪镇新场村新场村新场村&遵南大道停车场(渝能佳苑东北)&广东省-肇庆市-德庆县高速服务区-德庆县高速服务区-德庆县高速服务区&河北省-廊坊市-广阳区-爱民东道-廊坊市高速公路建设指挥部&湖北省-潜江市-潜江市-浩口镇苏港村-苏港村苏港村&宁谷镇S209宁谷汉墓群&河南省-商丘市-睢县-振兴路-睢县人民医院-多普勒&安普城市大道安普城市大道&鹤上镇S203鹤上(地铁站)&西洋镇洋后坪洋后坪洋后坪&江西省-南昌市-青山湖区-塘山镇南京东路-咏春拳馆&河北省-廊坊市-固安县-固安镇新源西街-太阳公元售楼处&广东省-惠州市-博罗县-人民路-人民路&辽宁省-沈阳市-苏家屯区-迎春街-沈阳市苏家屯区清真寺(东门)&浙江省-台州市-天台县大线道-天台县大线道-天台县大线道&杭瑞高速杭瑞高速&白岩路Cici&板桥镇果义果义果义&河南省-郑州市-管城回族区-证件路城东路往南一百米-不详&北京市-昌平区-大东流镇大东流镇-大东流镇&明光路街道长江东路瑶海区长江东路(明光路至滁州路路段)&河北省-承德市-宽城满族自治县固山子村-宽城满族自治县固山子村-宽城满族自治县固山子村&重庆市-县-铜梁县人民医院地下停车库-铜梁县人民医院地下停车库-铜梁县人民医院地下停车库&河南省-郑州市-荥阳市-京城路与繁荣街交叉口-京城路与繁荣街交叉口&广西壮族自治区-钦州市-灵山县-人民路-人民路&河南省-许昌市-长葛市-秦公路五巷-2号&河南省-新乡市-封丘县-潘店镇巨岗村-巨岗村巨岗村&纬三十六路纬三十六路&龙凤桥街道龙凤路龙庭花园停车场&钟山大道浙江大酒店(钟山大道店)&建设西路三都水族自治县档案馆&广东省-河源市-紫金县-九和镇九和街-紫金县九和卫生院&广西壮族自治区-梧州市-岑溪市-筋竹镇王同坑-王同坑王同坑&康王乡白石岭南路岳阳市钢材大市场&江苏省-苏州市-吴中区太湖房车园工地-吴中区太湖房车园工地-吴中区太湖房车园工地&三合乡芹菜村芹菜村芹菜村&观城路2号&江苏省-苏州市-昆山市-周市镇盛帆路-大德广告&广东省-惠州市-惠城区-横沥镇潭头村-潭头村潭头村&四川省-巴中市-巴州区-江北街道江北大道中段-巴河廊桥咨询中心&广东省-深圳市-南山区-107国道-107国道&文汇街道西二路118号银座佳驿精致酒店&钟山路钟山路&西洛街道金沙立交桥金沙收费站(G56杭瑞高速入口)&东海街道院前路金帝花园&播州大道林达・阳光城停车场&河南省-南阳市-镇平县-健康路-健康路&马厂镇茂良村茂良村茂良村&河南省-许昌市-襄城县-山头店镇许广高速-襄城县三亮养殖公司&陡沟街道济南绕城高速殷家林幼儿园&石桥镇大沙大沙大沙&秀河线城北警务服务站&广东省-东莞市-东莞市-长安镇长安镇-长安镇&新七路薛家收费站&四川省-南充市-南部县-成巴高速-成巴高速&四川省-成都市-金牛区-九里堤北路-40号1栋&湖南省-永州市-双牌县-二广高速-二广高速&泽头镇泽头立交威海南海收费站(S24威青高速出口)&广东省-云浮市-新兴县-东成镇广兴大道东-中国海油(新兴加油站)&王浩屯镇王浩屯镇王浩屯镇&上海市-闵行区-春潮路-春潮路&广东省-韶关市-市辖区422武深高速--市辖区422武深高速&河南省-洛阳市-孟津县孟扣路--孟津县孟扣路&龙岩大道龙岩大道&河南省-洛阳市-偃师市-207国道-207国道&广东省-汕尾市-陆丰市-揭石镇-揭石镇&广东省-河源市-源城区-205国道-205国道&广东省-汕尾市-广东省汕尾市陆丰市南塘镇-广东省汕尾市陆丰市南塘镇-广东省汕尾市陆丰市南塘镇&广东省-肇庆市-广东省肇庆市高要市咸宁港附近高速上-广东省肇庆市高要市咸宁港附近高速上-广东省肇庆市高要市咸宁港附近高速上&良村镇山王殿山王殿山王殿&广东省-东莞市-东莞市-凤岗镇龙平西路中国石油加油站-凤岗镇龙平西路中国石油加油站&四川省-成都市-郫都区-郫县立交桥-郫县收费站&宝山北路贵州师范大学5教学楼&两路口街道中山二路星鑫公寓(重庆两路口店)&嘉陵江路顺河人家&联合西路联合西路&陈店镇陈贵路南兴宾馆(陈贵路)&铁西街道振兴路第一城&广东省-深圳市-宝安区-沙井街道沙井街-深圳市宝安区沙井街道司法所&河南省-平顶山市-卫东区-新华路与青云路交叉口-新华路与青云路交叉口&四川省-宜宾市-珙县-巡场镇芙蓉大道-芙蓉矿务局水泥厂&河南省-平顶山市-湛河区-光明路-湛河区叶刘村王艳艳卫生室&四川省-资阳市-乐至县-天池镇迎宾大道-通牧卫浴&河南省-信阳市-平桥区新六大街--平桥区新六大街&辽宁省-丹东市-宽甸满族自治县中医医院院内-宽甸满族自治县中医医院院内-宽甸满族自治县中医医院院内&河南省-郑州市-金水区-东平路与慕平路交叉口-东平路与慕平路交叉口&双墩镇双墩路双凤里排档&官扎营街道济泺路汽车南站(制革街)(公交站)&华山路16号宏大汽修&河北省-衡水市-桃城区-桃城街-47号&凤城镇文明西路郭婆包店&幸福街道幸福南路合成革小区(公交站)&河南省-许昌市-魏都区-文峰街道文峰中路-三鼎华悦大酒店-洗手间&西藏自治区-拉萨市-城关区-吉崩岗街道北京中路-山东人家东岳倨酒楼&海南省-儋州市-儋州市-南丰镇中路-50号&孚玉镇北门街向阳针织百货批发部&鼎山街道鼎山大道建宇・雍山郡(东南门)&河北省-唐山市-路南区唐山矿--路南区唐山矿&工业北路德银超市(工业北路)&西航街道S102多彩万象城(公交站)&安徽省-铜陵市-铜官区-沪渝高速-铜陵收费站(G50沪渝高速入口)&江苏省-连云港市-赣榆区-青口镇黄海路-江苏省赣榆县外国语学校(北门)&广东省-中山市-中山市-西部沿海高速-坦洲收费站&钟山大道恒远・帝都新城&浙江省-湖州市-长兴县-雉洲大道-长兴南收费站(G25长深高速出口)&双河口街道龙宝大街万州区龙宝自来水厂收费处&四川省-成都市-武侯区-桂溪街道天府大道中段辅路-蛮蛮小火锅&南村镇市新路永丰陶瓷(旧市新路)&复兴镇S213复兴国土资源管理分局&江苏省-泰州市-海陵区-老街-29号&海沧街道海沧大道SEC泰地海西中心停车场(出入口)&四川省-德阳市-广汉市-北外乡大同路二段-金城电脑&环峰镇望梅路望梅理发店&汇龙镇江海北路阳光电脑&炉桥镇S311炉桥加油站&迎宾大道开发区管委会(公交站)&河南省-郑州市-中原区-西四环-企业公园10栋&海子街海子街农贸市场&四川省-成都市-金牛区-茶店子街道一品天下大街-379号一品蓝钻量贩KTV&海南省-三亚市-天涯区-天涯区解放路-海南省三亚市工商行政管理局&江苏省-扬州市-广陵区-湾头镇运河东路-航港海鲜楼&湖北省-咸宁市-崇阳县-路口镇路宁街-路口照相馆&桂林街道站前大道楼兰家居&江苏省-连云港市-赣榆区-金海西路-赣榆县新新幼儿园&四川省-达州市-宣汉县-南坝镇田坝街-角度八美业&四川省-成都市-新都区-大丰街道大天路辅路-大天路中站(公交站)&湖北省-荆门市-钟祥市铁路桥--钟祥市铁路桥&不详&四川省-成都市-四川省成都市锦江区华宇广场-四川省成都市锦江区华宇广场-四川省成都市锦江区华宇广场&旅游路居住区4栋&北京市-昌平区-小汤山镇北六环路-德林坊超市&广东省-茂名市-电白区-沙琅镇水东路-茂名市电白县小天使幼儿园(电白县沙琅镇信访办北)&湖北省-黄冈市-黄梅县民政局对面-黄梅县民政局对面-黄梅县民政局对面&河北省-石家庄市-长安区-青园街道广安大街-建华通讯&广东省-深圳市-龙岗区南联-龙岗区南联-龙岗区南联&广东省-广州市-增城区-永宁街道永联路-增城市新塘镇永润毛织厂&广东省-惠州市-惠阳区-淡水街道白云六路-新桥村(公交站)&河南省-安阳市-文峰区文峰大道--文峰区文峰大道&四川省-成都市-双流县老双中路--双流县老双中路&广东省-深圳市-宝安区-西乡街道西乡大道-宝安区西乡大道&湖北省-黄冈市-团风县-乌林路-黄冈市团风县移民新村&河北省-石家庄市-裕华区-体育大街-178号&沈北街道杭州路气动院沈北新区&广东省-佛山市-南海区-西樵镇翠云街-西桥银旺托运部&天柱山路天柱花园3栋&石马河街道松石大道宝妈时光(松石大道)&广东省-深圳市-龙岗区-坂田街道坂雪岗大道-中企莱公司&番中路中山港大桥收费站&观音桥街道建北六支路江田广场停车场(出口)&营上镇兴营路香满楼(曲靖公路管理总段营上管理所西)&214国道&四川省-成都市-双流区-双华路三段-停车场(双华路三段)&河北省-邯郸市-临漳县-马义乡-马义乡&顶效镇老街中路兴义市地税局顶效分局综合服务办税厅&寿泉街道天山路贝斯特幼儿园(天山路)&舜耕镇朝阳西路中环国际・仕府&广东省-惠州市-惠阳区-永湖镇永新街-惠州市惠阳区永湖镇畜牧兽医站&北京市-朝阳区-建国路-通惠河畔&苏山街道西三环路德赛地板&湖北省-黄石市-阳新县杨家湾路-阳新县杨家湾路-阳新县杨家湾路&河南省-濮阳市-市辖区-长济高速-河南省濮阳市市辖区长济高速&金珠西路西藏自治区商务厅&河南省-洛阳市-洛龙区-通济街-通济街开元大道口南(通济街开元大道口北)(公交站)&天津市-宁河区-一纬路-天津宁河县营业部&广东省-深圳市-龙岗区-平底镇-不详&江苏省-南通市-启东市-南阳镇人民路-启东市南阳镇纪律检查委员会&四川省-成都市-郫都区府第村-郫都区府第村-郫都区府第村&贵阳市云岩区松坡路2号&河南省-平顶山市-叶县-叶邓路-004乡道加油站&金山街道翔安隧道鑫鸿家具&广益街道登峰路登峰牛肉城(登峰路)&四川省-绵阳市-北川羌族自治县新倩路-北川羌族自治县新倩路-北川羌族自治县新倩路&四川省-成都市-郫县安静镇--郫县安静镇&南坪街道辅仁路福红五金建材&河南省-郑州市-荥阳市-万山路-郑州市烟草公司(荥阳市分公司)&云南省 保山市 隆阳区永昌街道惠通路13栋&辽宁省-沈阳市-浑南区-五三街道金卡路-四季蛋糕店&中山市小榄镇繁荣街4号&广东省-惠州市-惠东县-平山街道莲花路-惠东县东湖房管所&广东省-珠海市-金湾区-三灶镇琴石路-海珠老四川&石马河街道南石路中国烟草总公司重庆市公司物流分公司(东门)&金剑路金剑小学&盐津街道国酒南路时代百货&三灶镇唐人街唐人街市场(南门)&测试数据418晚&鸡场镇小王寨村小王寨村小王寨村&广东省-东莞市-东莞市-陈家坊新村一巷-1号&北京市-丰台区-丰台北路-丰台区律师事务所&广东省-韶关市-乐昌市-梅花北服务区-梅花北服务区&东关街道马家冲马家冲马家冲&广东省-东莞市-东莞市厚街白毫工业街26号卓讯工业园--东莞市厚街白毫工业街26号卓讯工业园&河南省-驻马店市-汝南县-梁祝镇刘楼-刘楼刘楼&双岗街道蒙城路多喜爱(北京华联蒙城路购物中心)&三合镇沙岗坡沙岗坡沙岗坡&广东省-广州市-从化市-第五中学-第五中学&浙江省-杭州市-江干区-九环路-九环路&河南省-信阳市-平桥区-新二十六大街-52号&广东省-广州市-天河区-兴华街道燕岭路-武警广东省总队医院停车场&河南省-平顶山市-汝州市-朝阳西路-朝阳路人民医院(公交站)&洪山街道洪山路实验小学(公交站)&福建省-龙岩市-连城县-莲峰镇北大东路-42号连城文川医院&广东省-阳江市-阳东区-合山镇合广路-阳江市阳东县合广旅业&旺草镇S207加油站&河北省-廊坊市-霸州市--霸州镇-前卜庄村大堤&四川省-达州市-通川区-凤凰大道-凤凰大道&清池街道府清街王侯嘉苑&广东省-惠州市-惠阳区-西区街道大亚湾大道-西区办事处(公交站)&河南省-商丘市-梁园区-神火大道与文化东路交叉口-神火大道与文化东路交叉口&江苏省-南京市-栖霞区-马群南路-马群南路&西岗镇新港南路宏港童装&广东省-深圳市-龙岗区-龙岗街道龙新大道-5号几何文具礼品批发行&下关镇泰安路无限极(惠丰新城和园北)&造化街道明星村明星村明星村&江苏省-苏州市-昆山市-锦溪镇X304-S224与X304交叉口&油榨街油榨街&长岭北路中天・会展城B4组团B区停车场&兴海南街停车场(兴海南路)&河南省-许昌市-襄城县马原市场--襄城县马原市场&陕西省-西安市-新城区-胡家庙街道金花北路-青岛海鲜&河南省-郑州市-管城回族区-航海路-自在火锅(航海路店)&济川东路交通银行ATM(海陵南路)&四川省-成都市-金牛区-一环路北一段-一环路北一段&东部快速干线企石镇府出口&北京市-东城区-文章胡同-文章胡同&湖南省-怀化市-鹤城区-西环路-西环路&广东省-深圳市-龙岗区-飞扬路-港兴&河北省-邯郸市-邯山区-东环路-欢博物流&河南省-三门峡市-陕县-绣岭路-神泉苑16栋&天宫殿街道昆仑大道41号渝铁西苑东区&文昌二街文昌花园75幢&解放北路解放北路&广东省-广州市-天河区-广汕路-88号&宁国南路宁国棋牌&广东省-深圳市-宝安区沙井民族大道--宝安区沙井民族大道&湖北省-潜江市-潜江市-泰丰路-泰丰路&观音桥街道北滨一路国美江天御府&湖北省-恩施土家族苗族自治州-鹤峰县-燕子乡燕子乡-燕子乡&广东省-深圳市-福田区-深南中路-上海航空售票处&广东省-东莞市-东莞市-长安镇长安镇-长安镇&华达北街华达北街&广东省-惠州市-惠城区-惠大高速-惠大高速&广东省-广州市-越秀区-东华东路-嘿客&建平镇新建街幸福蓝海&北京市-市辖区-丰台区方庄桥-丰台区方庄桥-丰台区方庄桥&四川省-成都市-锦江区-春熙路-锦江区楼宇服务中心&客天下观光路客天下观光路&广东省-惠州市-惠城区-小金口街道白石村-白石村白石村&广东省-惠州市-惠城区-水口镇-不详&五三街道朗月街金地・国际花园(西2门)&大学城中路大学城中路&青龙街道中环路重庆农村商业银行(中环路分理处)&惠州市惠阳区 维布村&黄山路黄金广场6栋&顶效镇福昆线顶效经济开发区&提署路提署路&河北省-石家庄市-行唐县-南桥镇S232-兴华加油站(中华路)&龙门浩街道龙门浩爱尚南山停车场(出入口)&大通路迪欧达(南秀路)&四川省-成都市-青白江区-云石路-云石路&新港大道南江门光阳机车有限公司&北京市-通州区-玉桥街道运河西大街-运河大街137号院&河南省-许昌市-禹州市-禹王大道-禹王大道&河北省-邯郸市-武安市-武安镇南环路-欧派电动车(桥西路)&金寨路金寨路&四川省-成都市-新都区-下南街-40号成都市新都区老干部休养所&抄乐镇S204中共湄潭县抄乐镇委员会&广东省-东莞市-东莞市-黄江镇长龙村-长龙村长龙村&天津市-滨海新区-第七大街-81号塘沽区天津塘沽&湖北省-潜江市-潜江市-滨河南路-滨河南路&玉溪镇联盟路民族乐园&广东省-东莞市-东莞市-环城东路-寮步出口&红枫湖镇贵清高速吉祥门业(后午路)&广东省-中山市-中山市-中山东区银桥二横街-10号&流长乡江都高速流长收费站(S30江都高速入口)&汀溪乡桃岭村桃岭村桃岭村&芙蓉中路与新建西路交叉口芙蓉中路与新建西路交叉口&河南省-焦作市-长冀高速-长冀高速-长冀高速&安成路与世纪大道交叉口安成路与世纪大道交叉口&广东省-深圳市-宝安区-公明街道松白路-光明新区人民医院门诊二部(东北门)&明田路明田路&湖南省-常德市-武陵区-龙港路-13号&河南省-郑州市-新密市-米村镇大红-大红大红&四川省-自贡市-贡井区-建设镇建设镇-建设镇&S103普济圩社区服务中心&四川省-巴中市-通江县-诺江镇城南路-通江城南汽车站&广东省-惠州市-惠阳区-广场东路-惠阳华翠苑4栋&江苏省-常州市-武进区-外环府路-36号&四川省-成都市-锦江区-静安路-私家厨房&河南省-郑州市-金水区-商务外环路-商务外环路&建新镇橫龙三路阳光天地・奥体SOHO&花果园街中国邮政储蓄银行(贵阳市花果园支行)&瑞陇高速瑞陇高速&广东省-深圳市-宝安区-石岩街道山城路-宝铭电商物流园&河南省-许昌市-禹州市西三茂牌坊对面--禹州市西三茂牌坊对面&广东省-惠州市-惠城区-惠澳大道立交-惠澳大道立交&内蒙古自治区 通辽市 科尔沁区河西广汽本田4S店&河南省-郑州市-管城回族区-经开第二大街与经南五路交叉口-经开第二大街与经南五路交叉口&四方台路207号&贵州省-毕节市-大方县-核桃彝族白族乡核桃彝族白族乡-核桃彝族白族乡&浦上大道与建新南路交叉口浦上大道与建新南路交叉口&长寨街道简笋村简笋村简笋村&花卉园东路花卉园东路&河南省-驻马店市-上蔡县-党店镇新田村-新田村新田村&解放路解放路&黑石礁街道黄浦路欣半岛酒店(黄浦路店)&贵州省-铜仁市-石阡县-本庄镇S305-本庄敬老院&幸福路幸福路&河南省-郑州市-荥阳市-034县道-034县道&云南省-红河哈尼族彝族自治州-蒙自市-文澜镇蒙雨线-蒙自县文澜镇十里铺村民委员会&河南省-郑州市-高新技术开发区-银屏路与银聪街交叉口-不详&小雅镇S303小雅镇政府&广东省-惠州市-惠东县-港口镇港口镇-港口镇&环市北路潮汕机场城市候机楼&广东省-深圳市-宝安区-西乡街道西乡大道-宝安区西乡大道&范岗镇山岗山岗山岗&石梁东路向来足疗&湖北省-咸宁市-咸安区-温泉街道庆展路-咸安区张公工业园&大十字街道商贸街民绣坊民族工艺精品店&广东省-深圳市-宝安区-新乐路-新乐路&和平大道云龙万达广场写字楼A座(南门)&广东省东莞市广发金融大厦Ｂ东北1米长安医院&广东省-深圳市-龙岗区-南湾街道南岭村大道-阳光雅苑(南岭村大道)&河北省-承德市-双桥区-府前路-鑫融典当行(府前路)&广西壮族自治区玉林市北流市永安路会仙公园附近&河北省-保定市-徐水区-崔庄镇徐新公路-河北省保定市徐水县阳光幼儿园(徐新公路)&四川省-成都市-武侯区-新乐北街-优客汉堡(高新店)&四川省-达州市-达川区-万达路-达县中医院&河南省-郑州市-中原区-嵩山路-花园酒店(嵩山路店)&河南省-许昌市-禹州市黄油镇-禹州市黄油镇-禹州市黄油镇&江苏省-无锡市-惠山区-前洲街道樱花南路-绝味鸭脖(前州镇店)&广东省-惠州市-惠阳区-龙山二路-龙山二路&丁山南路丁山南路&江西省九江市德安县解放路东佳路世贸商行&河北省-邢台市-南宫市-育才路-120号&振兴大街富贵园(东北门)&丹龙路丹龙路(公交站)&河南省-开封市-兰考县-车站路辅路-美克时尚宾馆&河南省-郑州市-金水区-商都路-宏康经络推拿&四川省-眉山市-仁寿县-蓉遵高速-S4/仁寿停车区(路口)&坪东街道新联路三和新城&河南省-驻马店市-正阳县-大林镇大林镇-大林镇&广东省-惠州市-惠城区-三环南路-金海马家居B区&站街镇水塘寨水塘寨水塘寨&河南省-许昌市-魏都区青芳街--魏都区青芳街&湖北省荆州市公安县新区路小北门火车站附近恒大金明都南门&陕西省西安市未央区阿房一路新安泾河新城正阳大道&广东省-佛山市-三水区西南街道--三水区西南街道&湖北省-荆州市-荆州区-207国道-207国道&广东省-广州市-荔湾区-荔湾路-华仔发廊&河南省-洛阳市-新安县-城关镇黄河中路-俊超汽车维修中心&云峰大道218号西南国际家居装饰傅览城家具馆2&河南省-周口市-太康县-马厂镇前河村-前河村前河村&广东省-惠州市-惠城区-小金口街道惠州大道-惠州火车站广场&河北省-邢台市-桥西区-中华大街与公园东街交叉口-中华大街与公园东街交叉口&八经街道八纬路三姐串店(八纬路)&天津市市辖区河北区金钟路普济河东道&平原路玉林烟酒店&广东省-惠州市-惠阳区-南湖路-南湖路&柳行立交桥柳行小区10号楼&河北省-石家庄市-正定县-正定镇恒州南街-正安花园&河北省-邢台市-沙河市-文谦大街-交警队家属院北楼1单元&光明路街道文化东路国泰花园(文化东路)&104国道104国道&广东省-深圳市-龙岗区-平湖新木新村-平湖新木新村&湖北省-恩施土家族苗族自治州-恩施市-龙凤镇龙凤镇-龙凤镇&广东省-汕头市-金平区-中山路-汕头市金平区总工会&敖江镇青龙路青龙食杂店&广东省-东莞市-东莞市-长东路-长东路&河北省-廊坊市-固安县孔雀大道孔雀城附近--固安县孔雀大道孔雀城附近&隘口乡X070宿松县隘口乡人大&淝河镇X014淝河派出所&云南省 大理白族自治州 宾川县金牛镇全球通大道&东海街道津淮街泉州市公安局丰泽分局&广东省-深圳市-龙岗区-南湾街道南岭村大道-阳光雅苑(南岭村大道)&江苏省南通市如东县天缘旅行社如东营业部东0米大豫镇&广东省东莞市广发金融大厦地下车库东0米中堂镇&硫磺沟镇S101S203与X125交叉口&河北省-石家庄市-新乐市-长寿街道新开路-桥头荞麦面&河北省-石家庄市-正定县-正定镇华安西路-小商品服装城&江苏省-无锡市-江阴市-富通路-恒生府邸&大朗镇大朗镇大朗镇&北京市-海淀区-航天桥环岛-航天桥环岛&蟠桃路106地质大队(邮政代办所)&戴南镇迎宾大道万源商贸城&湖南省长沙市天心区药王街坡子街&四光路保利温泉新城停车场&龙吟路龙吟路&广东省东莞市横江厦西门路横江厦&磻溪镇桑海村桑海村桑海村&广东省-惠州市-惠城区-江北街道花园路-惠州市惠城区仁和电子厂&麻涌立交麻涌出口&西湖景区街道黄庄社区黄庄社区黄庄社区&河北省-衡水市-安平县-汉王路与光明街交叉口-汉王路与光明街交叉口&广东省-江门市-新会区-司前镇汇湾-汇湾汇湾&北京市-密云区-西门外大街-西门外大街16号院(西1门)&广东省-深圳市-盐田区-海山街道梧桐路-盐田区人民医院急诊科&江苏省-淮安市-淮安区-北门大街-260号&辽宁省-辽阳市-太子河区-新生集-不详&大同街道大同路26号红蜻蜓(明珠广场)&广东省-惠州市-惠城区-宝安路-名流印象&河南省-郑州市-新密市-悦达路-不详&河北省-保定市-徐水县-贸山魏村-不详&河北省-秦皇岛市-海港区-红旗路-不详&东山街道新亭西路龙湖・春江郦城&四川省-成都市-都江堰市-IT大道-IT大道&城关镇镜湖西路晶宫悦澜湾&河北省石家庄市鹿泉市宾馆路果岭湾小区内&四川省-南充市-高坪区-建设路-余氏东风&大田集镇X038大田集镇敬老院&安徽省宿州市萧县大同街岱符公园&广东省东莞市东莞大道寮步镇&广东省广州市增城市东坑三横路荔新公路&上海市-浦东新区-宏涛路与港舟路交叉口-宏涛路与港舟路交叉口&西藏自治区-拉萨市-城关区-太阳岛二路-四合院停车场&广东省-深圳市-南山区-南头街道南头街-林夕梦&广东省-深圳市-宝安区-沈海高速-沈海高速&文峰镇S207文峰派出所&中枢街道黄树庄路符阳学校&广东省珠海市香洲区兰埔路文言路&河南省-郑州市-管城回族区-南三环与机场高速交叉口东南方向-不详&广东省-河源市-源城区-江北红星路党校红绿灯-江北红星路党校红绿灯&河北省-邢台市-桥东区-平安路-邢台硕德商贸有限公司&黑龙江省鹤岗市萝北县花园街凤翔镇十八尾加油站斜对面&中华北路北门(公交站)&广东省中山市吉昌路马鞍岛&广东省-惠州市-惠东县-白花镇集联村-集联村集联村&广东省江门市蓬江区水南路刺史高速路口&河南省-开封市-尉氏县-洧川镇陈庄村-陈庄村陈庄村&北环路阜南县社区服务中心&广东省-深圳市-宝安区-沙井街道沙井路-大王山村(公交站)&广东省-惠州市-惠城区-小金口街道惠州大道-惠州市惠城区小金口中心幼儿园&陕西省西安市灞桥区毛西公路席王村&华西街道黄果树大街建博国际广场&广东省深圳市宝安区龙华新区观澜高尔夫大道龙华新区龙华街道金银百货&花溪街道苦竹坝路融科・金色时代1站(公交站)&河南省-洛阳市-栾川县-栾川乡鸾州大道-樊营新型社区2期&鸭塘街道沪瑞线桂花苑&山东省济南市历下区伊鑫泉招待所东0米泉城路与省政府前街&四川省成都市武侯区高升桥东路武清东一路&广东省深圳市宝安区25区商业步行街西田公园&广东省-广州市-番禺区宗深镇-番禺区宗深镇-番禺区宗深镇&江苏省-南京市-雨花台区-板桥街道板桥街-板桥市民广场&广东省-东莞市-东莞市-大朗镇茶庄-茶庄茶庄&河北省唐山市丰润区荣宁道大润新城西面&北京市-市辖区-昌平区-天通苑西三区-不详&广东省-广州市-白云区金沙洲环洲五路-白云区金沙洲环洲五路-白云区金沙洲环洲五路&红河镇红河村红河村红河村&城关街道东风西街潍坊市脑科医院(南门)&涛源镇金江村金江村金江村&江西省-赣州市-定南县-历市镇公园路-文体中心(公交站)&重庆市市辖区大渡口区文体路恒通国际汽贸城&河南省郑州市中原区刁沟国伟烩面东0米兴郭路&百里镇东口村东口村东口村&广东省-广州市-荔湾区-陇西二巷-10号&四川省-成都市-武侯区-富华北路-保利百合花园(富华北路)&遂西高速遂西高速&天长街道炳辉中路天一阳光城&北京市-丰台区-右安门内大街-右安门内大街&河南省-商丘市-睢县-河堤乡河堤乡-河堤乡&磁灶镇延泽街澳泽烘焙(延泽街)&河南省-信阳市-浉河区-浉河北路-红星村588号&湖北省-黄冈市-蕲春县-大同镇查家里-查家里查家里&广东省-汕尾市-陆丰市-博美镇旧乡-旧乡旧乡&四川省-成都市-金牛区-茶店子街道茶店子东街-柯达&吉水路吉水路&通化路&S208中国石化鲁班加油站&金华镇金华路金华镇蔬菜早市&广东省佛山市南海区沙水工业区路桂和公路&广东省江门市蓬江区水南路棠下镇&河南省-周口市-郸城县基钟镇-郸城县基钟镇-郸城县基钟镇&南三经街南三经街&江西省上饶市余干县世纪大道世纪大道１０９&五一路嘿小面(开源农贸市场店)&炉山镇炉山镇炉山镇&河南省-郑州市-惠济区-迎宾路街道英才街-英才街英才街与花园路交叉口(东口)&肥西路与临泉路交叉口肥西路与临泉路交叉口&乐元镇乐元镇乐元镇乐元镇&广东省-佛山市-禅城区-张槎街道张槎一路-佛山市公安局交通警察支队一环公路大队&灵秀镇石龙路蚂蚁兄弟设计&朱家湾路朱家湾路&四川省-绵阳市-江油市-大康镇匡山路-平武县天然气有限责任公司(大康服务中心)&建设路八一一地质队宿舍22栋&新马路喜洋洋超市&广东省佛山市南海区Ｃ．ＤＤ（尺道）设计师事务所东北1米大力镇黄岐白沙社区&浙江省-温州市-鹿城区-车站大道-文化广场(公交站)&河北省-邯郸市-武安市-矿建路-新兴药房&河南省郑州市惠济区荣原物流南106米花园路立交北四环&广场路与上塘路交叉口广场路与上塘路交叉口&四公里街338号沙宣(五公里店)&王舍人街道凤凰路王舍人镇实验小学&沙河路红河门窗&河南省-郑州市-河南省郑州市新郑市港区张高镇-河南省郑州市新郑市港区张高镇-河南省郑州市新郑市港区张高镇&河南省-商丘市-柘城县-商周高速-商周高速&文星楼翡翠文化步行街文星楼翡翠文化步行街&广东省-惠州市-惠城区-水口街道湖西二路一街-明记原生态黑猪&广东省-惠州市-惠城区-口岸路-口岸路&西湖路西湖路&厚街镇下塘下塘下塘&湖北省襄阳市襄州区育红路老光彩云酒店&河南省平顶山市叶县叶县法院东0米叶鲁路北庙下坡&康园路与204国道交叉口康园路与204国道交叉口&广东省佛山市三水区荣康灵芝山庄北1米绿湖温泉度假村&襄河镇新华路153号全椒奥康商业步行街&山西省-太原市-小店区-太榆路-太榆路&罗三路江泉国际6栋&湖南省-永州市-宁远县-中和镇麻芝冲-麻芝冲麻芝冲&齐落山路齐落山路&汤岗子街道凤翔路温泉站(公交站)&河南省-南阳市-宛城区-长江中路-南阳理工学院(公交站)&青阳街道泉安中路格力(兴隆路)&湖北省-咸宁市-通山县-厦铺镇藕塘村-藕塘村藕塘村&四川省成都市武侯区华莱士炸鸡汉堡罗马假日店东0米第三人民医院&尧龙山镇包南线桐梓县天坪乡食用菌生产协会&浙江省-台州市-临海市-下桥路-下桥路&东湖街道东湖街经济小炒(少林路)&河南省洛阳市宜阳县锦屏商务酒店东0米白杨镇&河北省-保定市-高碑店市-北城街道北大街-大虎汽车电器风机电机&凉亭镇京珠线通政花苑&江苏省-南京市-浦口区-张山路-张山路&河北省-石家庄市-新华区友谊北大街509号--新华区友谊北大街509号&黄石西路马务(公交站)&河北省-沧州市-任丘市铅山道--任丘市铅山道&重庆市-渝北区-东环立交-东环立交&广东省-广州市-白云区-三元里街道机场路-机场路岗贝路口站(公交站)&浙江省-杭州市-富阳区-银湖街道九龙大道-富阳市公安局高桥派出所杭州野生动物世界警务室&广东省-惠州市-惠城区-陈江街道仲恺六路-深圳市坪山新区宏鑫五金贸易商行&四川省-成都市-武侯区南山环路-武侯区南山环路-武侯区南山环路&将军路荣盛锦绣澜湾7号楼&服装城大道化家湾(公交站)&杜鹃大道杜鹃大道&城关镇光明大道旭日尚城(东南门)&广东省-广州市-番禺区-石壁街道谢石公路-石壁山庄&莲城镇莲城镇莲城镇&河北省-张家口市-河北省张家口市辖区高新区新城铭座小区-河北省张家口市辖区高新区新城铭座小区-河北省张家口市辖区高新区新城铭座小区&湖北省-襄阳市-南漳县-城关镇中间屋-中间屋中间屋&黑龙江省-牡丹江市-阳明区-阳明街道阳明街-牡丹江市爱民区阳明区隆锋汽车养护商行&渝州支路渝州支路&湖北省-黄石市-黄石港区-黄石大道-黄石大道&芒市镇木康木康木康&石新路巴山国际陶瓷中心&广东省-深圳市-宝安区-新安街道文汇一街-深圳市宝安妇幼保健院口腔科&方庙街道临泉路天使足道养生馆(安徽大市场店)&广东省惠州市惠阳区泰兴国际停车区东1米巢莞高速&辽宁省-沈阳市-沈北新区-新城子街道贵州路-沈阳电大沈北新区分校&建新西路Wewe(建新西路)&云站路中环国际(公交站)&广东省-广州市-黄埔区-广深沿江高速-开发大道收费站(S3广深沿江高速出口萝岗方向)&大寺街与一人巷交叉口大寺街与一人巷交叉口&河北省-保定市-河北省保定市涿州市淮阳路和寨沟路交叉口-河北省保定市涿州市淮阳路和寨沟路交叉口-河北省保定市涿州市淮阳路和寨沟路交叉口&四川省-成都市-金牛区-蜀汉路-蜀汉10幢&广东省东莞市广发金融大厦地下车库东1米莞长路&西安路西安路&西陂街道人民西路永和豆浆(动车站店)&浙江省-金华市-义乌市-葛仙路-葛仙路&四川省-成都市-龙泉驿区-驿都大道-驿都大道&湖北省-荆州市-监利县-交通路-交通路&江苏省苏州市相城区苏嘉杭高速公路苏嘉杭高速公路旁&上海市闵行区闵浦二桥&广东省惠州市惠阳区泰兴国际停车区北1米叶挺大道";
            String[] split = s.split("&");
            Map<String, Object> map = new HashMap<>();
            map.put("address", split[0]);

            get(HttpConfig.custom().url(
                    "http://amap.coffee.proxy.dasouche.com//v1/map/geocode?caller=insurance_sales&password=rfd5h78j")
                    .map(map).client(HttpClientBuilder.custom().build()));

        } catch (HttpProcessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}