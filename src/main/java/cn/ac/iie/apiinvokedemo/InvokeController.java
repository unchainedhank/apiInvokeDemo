package cn.ac.iie.apiinvokedemo;

import com.alibaba.fastjson.JSONObject;
import com.demo.util.AkskUtil;
import com.demo.util.Signature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.ac.iie.apiinvokedemo.LookAlikeHttpService.sendHttpsPost;

@RestController
@Slf4j
public class InvokeController {

    @GetMapping("/")
    public String invokeTest(@RequestParam("ak") String ak,
                             @RequestParam("sk") String sk,
                             @RequestParam("url") String url) {

        //test财务公共指标
        JSONObject requestParam = new JSONObject();
        requestParam.put("pageSize", "1500");
        requestParam.put("pageNum", "1");
        requestParam.put("stat_date", "202106");
        requestParam.put("province_code", "00");
        JSONObject response = createRequest(ak, sk, url, requestParam);

        //test公司信息
        JSONObject requestParam1 = new JSONObject();
        requestParam1.put("pageSize", "1500");
        requestParam1.put("pageNum", "1");
        requestParam1.put("stat_date", "202106");
        requestParam1.put("province_code", "00");
        JSONObject response1 = createRequest(ak, sk, url, requestParam1);
        return response.toJSONString();
    }

    private JSONObject createRequest(String ak, String sk, String url, JSONObject requestParam) {
        //生成Signature
        Signature signature = new Signature();
        signature.setAk(ak);
        signature.setSk(sk);
        signature.setUrl(url);
        signature.setRequestParamMap(requestParam);
        String sign = AkskUtil.createSign(signature);
        log.info("创建签名:" + sign);
        JSONObject response = new JSONObject();
        try {
            response = https(signature, sign);
        } catch (Exception e) {
            log.info("请求错误:" + e.getMessage());
        }
        log.info("响应:" + response.toJSONString());
        return response;
    }

    //使用https调用
    public JSONObject https(Signature signature, String sign) throws Exception {
        log.info("调用:" + signature.getUrl() + ":" + signature.getRequestParamMap().toString());
        log.info("等待响应~");
        String s = sendHttpsPost(signature.getUrl(), signature.getRequestParamMap().toString(), sign);
        JSONObject result = JSONObject.parseObject(s);
        log.info("调用结果:" + result);
        return result;
    }
}
