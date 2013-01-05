/*
 * Name   TestAgent.java
 * Author ZhangZhenli
 * Created on 2012-9-24, 17:46:54
 *
 * Copyright (c) 2012 NanJing YiWuXian Technology Co., Ltd. All rights reserved
 *
 */
package com.taveloper.test.agent;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * JSON测试桩
 *
 * @author ZhangZhenli
 */
@Path("/Test")
public class TestAgent {

    private static Map<String, JsonNode> responseMap = Collections.synchronizedMap(new ResponseMap());
    private static Map<String, String> requestMap = Collections.synchronizedMap(new RequestMap());
    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestAgent.class);
    private static final Object requestLock = new Object();
    private static final Object responseLock = new Object();
    private ObjectMapper oMapper = new ObjectMapper();
    private Random random = new Random();
    private static final int TIME_OUT = 20; // 代理等待的超时秒数;

    @POST
    @GET
    @Path("/Agent/{testUrl:.*}")
    @Consumes(MediaType.WILDCARD)
    public Response Agent(String inputString, @PathParam("testUrl") String testUrl, @Context HttpServletRequest request) throws IOException {
        logger.info(testUrl);
        // 获取并还原原始的HTTP请求消息
        String queryString = request.getQueryString() == null ? "" : request.getQueryString();
        Enumeration<String> headerNames = request.getHeaderNames();
        headerNames.hasMoreElements();
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod()).append(" ");
        sb.append(request.getRequestURI()).append(queryString.equals("") ? "" : "?" + queryString).append(" ");
        sb.append(request.getProtocol()).append("\n");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = "";
            Enumeration<String> headers = request.getHeaders(headerName);
            while (headers != null && headers.hasMoreElements()) {
                headerValue = headerValue + (headerValue.equals("") ? "" : ";") + headers.nextElement();
            }
            sb.append(headerName).append(" : ").append(headerValue).append("\n");
        }
        sb.append("\n");
        sb.append(inputString).append("\n");
        sb.append(System.currentTimeMillis());
        synchronized (requestLock) {
            requestMap.put(getIpAddr(request) + testUrl, sb.toString());
            requestLock.notifyAll();
        }
        JsonNode get;
        synchronized (responseLock) {
            get = responseMap.remove(getIpAddr(request) + testUrl);
            for (int i = 0; i < TIME_OUT && get == null; i++) {
                try {
                    responseLock.wait(1000);
                    get = responseMap.remove(getIpAddr(request) + testUrl);
                } catch (InterruptedException ex) {
                }
            }
        }
        if (get == null) {
            get = oMapper.readTree("{\"error\":\"Waiting for monitor connection timeout\"}");
            synchronized (requestLock) {
                requestMap.remove(getIpAddr(request) + testUrl);
                requestLock.notifyAll();
            }
        }
        ResponseBuilder rb = Response.status(Response.Status.OK);
        rb.header("Content-Type", "application/json; charset=UTF-8");
        rb.entity(get.toString());
        return rb.build();
    }

    @POST
    @Path("/Monit/{testUrl:.*}")
    @Consumes(MediaType.WILDCARD)
    public Response Monit(InputStream jsonInputStream, @PathParam("testUrl") String testUrl, @Context HttpServletRequest request) throws IOException {
        logger.info(testUrl);
        JsonNode rootNode = null;
        try {
            rootNode = oMapper.readTree(jsonInputStream);
        } catch (EOFException e) {
        }
        synchronized (responseLock) {
            responseMap.put(getIpAddr(request) + testUrl, rootNode);
            responseLock.notifyAll();
        }
        String get;
        synchronized (requestLock) {
            get = requestMap.remove(getIpAddr(request) + testUrl);
            for (int i = 0; i < TIME_OUT && get == null; i++) {
                try {
                    requestLock.wait(1000);
                    get = requestMap.remove(getIpAddr(request) + testUrl);
                } catch (InterruptedException ex) {
                }
            }
        }
        if (get == null) {
            get = "No agent requests exist";
            synchronized (responseLock) {
                responseMap.remove(getIpAddr(request) + testUrl);
                responseLock.notifyAll();
            }
        } else {
            long cTime = System.currentTimeMillis();
            String substring = get.substring(get.length() - Long.toString(cTime).length());
            long parseLong = Long.parseLong(substring);
            if (Math.abs(parseLong - cTime) > (TIME_OUT * 1000)) {
                get = "Agent request has timed out";
            }
        }
        ResponseBuilder rb = Response.status(Response.Status.OK);
        rb.header("Content-Type", "text/plain; charset=UTF-8");
        rb.entity(get);
        return rb.build();
    }

    /**
     *
     * @author ZhangZhenli
     */
    public static class ResponseMap extends LinkedHashMap<String, JsonNode> {

        final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResponseMap.class);
        private static final int MAX_ENTRIES = 50;

        public ResponseMap() {
            super(MAX_ENTRIES, Float.valueOf(0.75f), true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, JsonNode> eldest) {
            logger.debug("TokenCacheMap size:" + size());
            if (false) {
                logger.debug("TokenCacheMap toString:" + this.toString());
            }
            return size() > MAX_ENTRIES;
        }
    }

    /**
     *
     * @author ZhangZhenli
     */
    public static class RequestMap extends LinkedHashMap<String, String> {

        final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResponseMap.class);
        private static final int MAX_ENTRIES = 50;

        public RequestMap() {
            super(MAX_ENTRIES, Float.valueOf(0.75f), true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            logger.debug("TokenCacheMap size:" + size());
            if (false) {
                logger.debug("TokenCacheMap toString:" + this.toString());
            }
            return size() > MAX_ENTRIES;
        }
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    @GET
    @Path("/get")
    public Response getProductInJSON() {

        ResponseBuilder rb = Response.status(Response.Status.OK);
        rb.header("Content-Type", "application/json; charset=UTF-8");
        String get = "{\"items\":[{\"id\":\"z13lwnljpxjgt5wn222hcvzimtebslkul\",\"url\":\"https://plus.google.com/116899029375914044550/posts/HYNhBAMeA7U\",\"object\":{\"content\":\"\\u003cb\\u003eWhowilltakethetitleof2011AngryBirdsCollegeChamp?\\u003c/b\\u003e\\u003cbr/\\u003e\\u003cbr/\\u003e\\u003cbr/\\u003eIt&#39;sthe2ndanniversaryofAngryBirdsthisSunday,December11,andtocelebratethisbreak-outgamewe&#39;rehavinganintercollegiateangrybirdschallengeforstudentstocompeteforthetitleof2011AngryBirdsCollegeChampion.Add\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/105912662528057048457\\\"class=\\\"proflink\\\"oid=\\\"105912662528057048457\\\"\\u003eAngryBirdsCollegeChallenge\\u003c/a\\u003e\\u003c/span\\u003etolearnmore.Goodluck,andhavefun!\",\"plusoners\":{\"totalItems\":27}}},{\"id\":\"z13rtboyqt2sit45o04cdp3jxuf5cz2a3e4\",\"url\":\"https://plus.google.com/116899029375914044550/posts/X8W8m9Hk5rE\",\"object\":{\"content\":\"CNNHeroesshinesaspotlightoneverydaypeoplechangingtheworld.Hearthetoptenheroes&#39;inspiringstoriesbytuningintotheCNNbroadcastof&quot;CNNHeroes:AnAll-StarTribute&quot;onSunday,December11,at8pmET/5pmPTwithhost\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/106168900754103197479\\\"class=\\\"proflink\\\"oid=\\\"106168900754103197479\\\"\\u003eAndersonCooper360\\u003c/a\\u003e\\u003c/span\\u003e,anddonatetotheircausesonlineinafewsimplestepswithGoogleWallet(formerlyknownasGoogleCheckout):\\u003cahref=\\\"http://www.google.com/landing/cnnheroes/2011/\\\"\\u003ehttp://www.google.com/landing/cnnheroes/2011/\\u003c/a\\u003e.\",\"plusoners\":{\"totalItems\":21}}},{\"id\":\"z13wtpwpqvihhzeys04cdp3jxuf5cz2a3e4\",\"url\":\"https://plus.google.com/116899029375914044550/posts/dBnaybdLgzU\",\"object\":{\"content\":\"TodaywehostedoneofourBigTenteventsinTheHague.\\u003cspanclass=\\\"proflinkWrapper\\\"\\u003e\\u003cspanclass=\\\"proflinkPrefix\\\"\\u003e+\\u003c/span\\u003e\\u003cahref=\\\"https://plus.google.com/104233435224873922474\\\"class=\\\"proflink\\\"oid=\\\"104233435224873922474\\\"\\u003eEricSchmidt\\u003c/a\\u003e\\u003c/span\\u003e,DutchForeignMinisterUriRosenthal,U.S.SecretaryofStateHillaryClintonandmanyotherscametogethertodiscussfreeexpressionandtheInternet.TheHagueisourthirdBigTent,aplacewherewebringtogethervariousviewpointstodiscussessentialtopicstothefutureoftheInternet.ReadmoreontheOfficialGoogleBloghere:\\u003cahref=\\\"http://goo.gl/d9cSe\\\"\\u003ehttp://goo.gl/d9cSe\\u003c/a\\u003e,andwatchthevideobelowforhighlightsfromtheday.\",\"plusoners\":{\"totalItems\":76}}}]}";
        rb.entity(get);
        return rb.build();

    }
    /* 
     @POST
     @Path("/upload")
     @Consumes(MediaType.TEXT_PLAIN)
     @Produces(MediaType.APPLICATION_JSON)
     public Object uploadContacts(List<ContactsBean> contactsBeans, @Context HttpServletRequest request) {
     Integer userId = (Integer) request.getAttribute("LoginUserId");
     ContactsBean me = new ContactsBean();
     me.setPhoneNumber(String.valueOf(userId));
     contactsBeans.add(me);
     final List<ContactsBean> cbs = contactsBeans;
     Thread t = new Thread(new Runnable() {
     @Override
     public void run() {
     try {
     com.ywx.yiyou.rmi.remote.IContacts get = GetRmiObject.get(com.ywx.yiyou.rmi.remote.IContacts.class);
     get.contactsHandler(cbs);
     } catch (RemoteException ex) {
     logger.error("Failed to invoke remote methods");
     ex.printStackTrace();
     } catch (NamingException ex) {
     logger.error("Looking for a remote object fails");
     ex.printStackTrace();
     }
     }
     });
     t.start();
     return Message.OK;
     }

     @GET
     @Path("/get")
     @Produces("application/json")
     public List<ContactsBean> getProductInJSON() {

     ContactsBean product = new ContactsBean();
     String name = "21122333";
     try {
     product.setNickName(Hex.encodeHexString(Base64.encodeBase64(name.getBytes("UTF-8"))));
     } catch (UnsupportedEncodingException ex) {
     Logger.getLogger(TestAgent.class.getName()).log(Level.SEVERE, null, ex);
     }
     product.setPhoneNumber(DigestUtils.sha256Hex("1222299562"));
     List<ContactsBean> list = new ArrayList<ContactsBean>();
     list.add(product);
     return list;

     }

     @POST
     @Path("/put")
     @Consumes("application/json")
     public Response createProductInJSON(List<ContactsBean> product) {

     String result = "Product created : " + Arrays.toString(product.toArray());
     return Response.status(201).entity(result).build();

     }

     @POST
     @Path("/post")
     @Consumes(MediaType.TEXT_PLAIN)
     public Response postJSONString(String product, @Context HttpServletRequest request) {
     System.out.println(request.getContextPath());
     System.out.println(request.getLocalAddr());
     System.out.println(request.getLocalName());
     System.out.println(request.getLocalPort());
     System.out.println(request.getMethod());
     System.out.println(request.getPathInfo());
     System.out.println(request.getPathTranslated());
     System.out.println(request.getProtocol());
     System.out.println(request.getRemoteUser());
     System.out.println(request.getRequestURI());
     System.out.println(request.getRequestURL());
     System.out.println(request.getScheme());
     System.out.println(request.getServerName());
     System.out.println(request.getServerPort());
     System.out.println(request.getServletPath());
     System.out.println(product);
     String result = "Product created : " + product;
     return Response.status(201).entity(result).build();
     }

     @POST
     @Path("/Agent/{testUrl:.*}")
     @Consumes(MediaType.WILDCARD)
     public Response Agent(InputStream jsonInputStream, @PathParam("testUrl") String testUrl, @Context HttpServletRequest request) throws IOException {
     System.out.println(testUrl);
     JsonNode rootNode = m.readTree(jsonInputStream);
     String queryString = request.getQueryString() == null ? "" : request.getQueryString();
     String queryTmp;
     String key = testUrl + (queryString.equals("") ? "" : "?" + queryString);
     while (cacheMap.containsKey(key)) {
     queryTmp = "testAgent=" + r.nextInt(Integer.MAX_VALUE);
     key = testUrl + (queryString.equals("") ? "?" : "?" + queryString + "&") + queryTmp;
     }
     System.out.println(key);
     cacheMap.put(key, rootNode);
     System.out.println(rootNode.toString());
     Enumeration<String> headerNames = request.getHeaderNames();
     headerNames.hasMoreElements();
     StringBuilder sb = new StringBuilder();
     sb.append(request.getMethod()).append(" ");
     sb.append(request.getRequestURI()).append(queryString.equals("") ? "" : "?" + queryString).append(" ");
     sb.append(request.getProtocol()).append("\n");
     while (headerNames.hasMoreElements()) {
     String headerName = headerNames.nextElement();
     String headerValue = "";
     Enumeration<String> headers = request.getHeaders(headerName);
     while (headers != null && headers.hasMoreElements()) {
     headerValue = headerValue + (headerValue.equals("") ? "" : ";") + headers.nextElement();
     }
     sb.append(headerName).append(" : ").append(headerValue).append("\n");
     }
     sb.append("\n");
     sb.append(rootNode.toString()).append("\n\n");
     ResponseBuilder rb = Response.status(Response.Status.OK);
     rb.header("Content-Type", "text/plain; charset=UTF-8");
     rb.entity(sb.toString());
     return rb.build();
     }
     */
}
