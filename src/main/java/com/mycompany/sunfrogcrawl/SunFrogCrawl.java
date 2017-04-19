/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sunfrogcrawl;

import dal.Colors;
import dal.DetailSize;
import dal.Details;
import dal.Items;
import dal.Positions;
import dal.Sizes;
import dal.Styles;
import dal.Visited;
import dao.ColorsJpaController;
import dao.DetailSizeJpaController;
import dao.DetailsJpaController;
import dao.ItemsJpaController;
import dao.PositionsJpaController;
import dao.SizesJpaController;
import dao.StylesJpaController;
import dao.VisitedJpaController;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 *
 * @author SaiBack
 */
public class SunFrogCrawl {

    public static void main(String[] args) {
//        Map<String,String> lstLink = new HashMap<>();
        try {
            Document get = Jsoup.connect("https://www.sunfrog.com/").get();
            Element ulCate = get.getElementsByClass("double_menu").get(0);
            Elements elementsByTag = ulCate.getElementsByTag("ul");
            for (Element element : elementsByTag) {
                Elements lstLis = element.getElementsByTag("li");
                for (int i=0; i<lstLis.size();i++) {
                    Element lstLi = lstLis.get(i);
                    if (!lstLi.attr("class").equals("")) {
                        continue;
                    }
                    String linkCate = lstLi.getElementsByTag("a").get(0).attr("href");
                    String cateName = lstLi.getElementsByTag("a").get(0).html();
//                    lstLink.put(cateName, linkCate);      
//                    if (lstLink.size() == 3) {
//                        CrawlThread crawlThread = new CrawlThread(lstLink);
//                        crawlThread.start();
//                        System.out.println("Star Thread 1");
//                        lstLink.clear();
//                    }
                    if ((!checkVisited(linkCate)) && (i%4==0)) {
                        getSubCate(linkCate, cateName);
                        addVisitLink(linkCate);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//        getDataItem("https://www.sunfrog.com/LifeStyle/109053603-276737819.html", "Automotive", "Bikers");
    }

    public static void getSubCate(String link, String cateName) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_SunFrogCrawl_jar_1.0-SNAPSHOTPU");
            VisitedJpaController controller = new VisitedJpaController(emf);
            System.out.println("Get Sub Cate");
            Document catePage = Jsoup.connect(link).get();
            if (catePage.getElementById("pillbox").getElementsByTag("ul").isEmpty()) {
                if (!checkVisited(link)) {
                    getDataByCate(link, cateName);
                    addVisitLink(link);
                }
                return;
            }
            Element ulElement = catePage.getElementById("pillbox").getElementsByTag("ul").get(0);
            Elements lstSubCates = ulElement.getElementsByTag("a");
            for (Element lstSubCate : lstSubCates) {
                if (lstSubCate.attr("title").equals("")) {
                    continue;
                }
                if (!checkVisited(lstSubCate.attr("href"))) {
                    getDataByCate(lstSubCate.attr("href"), cateName, lstSubCate.attr("title"));
                    addVisitLink(lstSubCate.attr("href"));
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void getDataByCate(String link, String cate) {
        MyDriver myDriver = new MyDriver();
        PhantomJSDriver driver = myDriver.getDriver();
        driver.get(link);
        System.out.println("Connect Link");
        Document subCatePage = Jsoup.parse(driver.getPageSource());
        myDriver.quitDriver();
        Elements lstShirt = subCatePage.getElementsByAttributeValueContaining("id", "shirtDivDisplay");
        for (Element element : lstShirt) {
            if (!checkVisited(element.getElementsByTag("a").get(0).attr("href"))) {
                addVisitLink(element.getElementsByTag("a").get(0).attr("href"));
                getDataItem(element.getElementsByTag("a").get(0).attr("href"), cate, "");
            }
        }
    }

    public static void getDataByCate(String link, String cate, String subCate) {

        MyDriver myDriver = new MyDriver();
        PhantomJSDriver driver = myDriver.getDriver();
        driver.get("https://www.sunfrog.com" + link);
        System.out.println("Connect Link");
        Document subCatePage = Jsoup.parse(driver.getPageSource());
        Elements lstShirt = subCatePage.getElementsByAttributeValueContaining("id", "shirtDivDisplay");
        for (Element element : lstShirt) {
            if (!checkVisited(element.getElementsByTag("a").get(0).attr("href"))) {
                addVisitLink(element.getElementsByTag("a").get(0).attr("href"));
                getDataItem(element.getElementsByTag("a").get(0).attr("href"), cate, subCate);
            }
        }
        int i = 40;
        link = link.replace("&navpill", "");
        link = link.replace("index.cfm", "paged2.cfm");
        while (i <= 1000) {
            try {
                System.out.println("https://www.sunfrog.com" + link + "&offset=" + (i + 1));
                driver.get("https://www.sunfrog.com" + link + "&offset=" + (i + 1));
                Document ajaxPage = Jsoup.parse(driver.getPageSource());
                if (ajaxPage.getElementsByAttributeValueContaining("id", "shirtDivDisplay").isEmpty()) {
                    break;
                }
                lstShirt = ajaxPage.getElementsByAttributeValueContaining("id", "shirtDivDisplay");
                for (Element element : lstShirt) {
                    if (!checkVisited(element.getElementsByTag("a").get(0).attr("href"))) {
                        addVisitLink(element.getElementsByTag("a").get(0).attr("href"));
                        getDataItem(element.getElementsByTag("a").get(0).attr("href"), cate, subCate);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                i += 40;
            }
        }
        driver.quit();
    }

    public static void getDataItem(String linkShirt, String cate, String subCate) {
        try {
            Document shirtPage = Jsoup.connect(linkShirt).get();
            if (!shirtPage.getElementsByClass("frameit").isEmpty()) {
                Elements lstShirt = shirtPage.getElementsByClass("frameit");
                for (Element element : lstShirt) {
                    if (!checkVisited(element.getElementsByTag("a").get(0).attr("href"))) {
                        addVisitLink(element.getElementsByTag("a").get(0).attr("href"));
                        getDataItem("https://www.sunfrog.com" + element.getElementsByTag("a").get(0).attr("href"), cate, subCate);
                    }

                }
            } else {
                Items items = new Items();
                items.setTitle(shirtPage.getElementById("srshow").html().trim());
                try {
                    items.setDescription(shirtPage.getElementsByAttributeValueMatching("style", "margin:11px 0 22px 0;").get(0).html().trim());
                } catch (Exception exception) {
                    items.setDescription("");
                }
                items.setKeyword(shirtPage.getElementsByAttributeValueMatching("name", "Keywords").get(0).attr("content"));
                items.setLink(linkShirt);
                items.setCate(cate);
                items.setSubcate(subCate);
                EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_SunFrogCrawl_jar_1.0-SNAPSHOTPU");
                ItemsJpaController controller = new ItemsJpaController(emf);
                controller.create(items);
                System.out.println(items.getTitle());
                getByStyle(linkShirt, items);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void getByStyle(String link, Items itemId) {
        try {
            Document detailPage = Jsoup.connect(link).get();
            Element selectStyle = detailPage.getElementById("shirtTypes");
            Elements styles = selectStyle.getElementsByTag("option");
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_SunFrogCrawl_jar_1.0-SNAPSHOTPU");
            StylesJpaController styleContr = new StylesJpaController(emf);
            for (Element style : styles) {
                String styleLink = link.substring(0, link.lastIndexOf("/") + 1) + style.attr("value");
                String styleNameAndPrice = style.html();
                Double price = Double.valueOf(styleNameAndPrice.split(" ")[styleNameAndPrice.split(" ").length - 1].replace("$", ""));
                String styleName = styleNameAndPrice.replace(styleNameAndPrice.split(" ")[styleNameAndPrice.split(" ").length - 1], "").trim();
                Styles styleCreate = styleContr.firstOrCreate(styleName);
                getByColor(styleLink, styleCreate, itemId, price);
            }
        } catch (Exception ex) {
            Logger.getLogger(SunFrogCrawl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getByColor(String link, Styles styleId, Items itemId, double price) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_SunFrogCrawl_jar_1.0-SNAPSHOTPU");
            ColorsJpaController colorContr = new ColorsJpaController(emf);
            PositionsJpaController positionsJpaContr = new PositionsJpaController(emf);
            Document detailPage = Jsoup.connect(link).get();
            Elements colorLabels;
            try {
                colorLabels = detailPage.getElementsByClass("btn-group").get(0).getElementsByTag("label");
            } catch (Exception e) {
                colorLabels = null;
            }
            if (colorLabels != null) {
                String linkGetSize = "";
                for (Element colorLabel : colorLabels) {
                    String name = colorLabel.attr("title");
                    String onClick = colorLabel.attr("onclick");
                    String[] splits = onClick.split("'");
                    for (String split : splits) {
                        if (split.contains("html")) {
//                            System.out.println(link.substring(0, link.lastIndexOf("/") + 1) + split);
                            linkGetSize = link.substring(0, link.lastIndexOf("/") + 1) + split;
                        }
                    }
                    String style = colorLabel.getElementsByTag("div").get(0).attr("style");
                    String[] styleArrs = style.split("'");
                    String linkColor = "";
                    for (String styleArr : styleArrs) {
                        if (styleArr.contains("https")) {
                            linkColor = styleArr;
                        }
                    }
                    Colors color = colorContr.firstOrCreate(name, linkColor);
                    Details details = new Details();
                    details.setLink(linkGetSize);
                    details.setItemId(itemId);
                    details.setStyleId(styleId);
                    details.setPrice(price);
                    for (Element e : detailPage.getElementsByTag("small")) {
                        if (e.html().contains("SKU")) {
                            details.setSku(e.html().split(" ")[e.html().split(" ").length - 1]);
                        }
                    }
                    Positions positions = null;
                    try {
                        positions = positionsJpaContr.firstOrCreate(detailPage.getElementById("frontorback").html());
                    } catch (Exception exception) {
                        System.out.println("Back");
                        positions = positionsJpaContr.firstOrCreate(detailPage.getElementsByAttributeValueMatching("class", "btn btn-lg btn-default disabled pull-right visible-lg").html());
                    }
                    details.setPositionId(positions);
                    details.setColorId(color);
                    getBySize(linkGetSize, details);
                }
            } else {
                Details details = new Details();
                details.setLink(link);
                details.setItemId(itemId);
                details.setStyleId(styleId);
                details.setPrice(price);
                for (Element e : detailPage.getElementsByTag("small")) {
                    if (e.html().contains("SKU")) {
                        details.setSku(e.html().split(" ")[e.html().split(" ").length - 1]);
                    }
                }
                Positions positions = null;
                try {
                    positions = positionsJpaContr.firstOrCreate(detailPage.getElementById("frontorback").html());
                } catch (Exception exception) {
                    System.out.println("Back");
                    positions = positionsJpaContr.firstOrCreate(detailPage.getElementsByAttributeValueMatching("class", "btn btn-lg btn-default disabled pull-right visible-lg").html());
                }
                details.setPositionId(positions);
                
                getBySize(link, details);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void getBySize(String link, Details details) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_SunFrogCrawl_jar_1.0-SNAPSHOTPU");
            SizesJpaController sizesContr = new SizesJpaController(emf);
            DetailSizeJpaController detailSizeJpaContr = new DetailSizeJpaController(emf);
            DetailsJpaController detailsJpaContr = new DetailsJpaController(emf);
            Document detailPage = Jsoup.connect(link).get();
            details.setImg(detailPage.getElementById("MainImgShow").attr("src"));
            detailsJpaContr.create(details);
            Element lstSizes = detailPage.getElementById("sizeChangeId");
            if (lstSizes != null) {
                for (Element e : lstSizes.getElementsByAttribute("data-se")) {
                    Sizes sizeCreate = sizesContr.firstOrCreate(e.html());
                    detailSizeJpaContr.create(new DetailSize(sizeCreate, details));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean checkVisited(String link) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_SunFrogCrawl_jar_1.0-SNAPSHOTPU");
        VisitedJpaController controller = new VisitedJpaController(emf);
        Visited findLink = controller.findLink(link);
        if (findLink == null) return false;
        else return true;
    }

    public static void addVisitLink(String link) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_SunFrogCrawl_jar_1.0-SNAPSHOTPU");
        VisitedJpaController controller = new VisitedJpaController(emf);
        Visited visited = new Visited();
        visited.setLink(link);
        controller.create(visited);
    }

}
