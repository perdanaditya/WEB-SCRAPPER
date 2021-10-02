package io.onebrick.demo.web.scrapper.service.utilities;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.onebrick.demo.web.scrapper.entity.constant.TokopediaConstant;
import io.onebrick.demo.web.scrapper.entity.dto.Product;
import io.onebrick.demo.web.scrapper.service.configuration.TokopediaProperties;
import io.vavr.control.Try;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rizky Perdana
 */
@Slf4j
public class TokopediaScrapperUtil {

  private static final String PARAM_R = "r=";
  private static final String AMP = "&";
  private static final String PROMO_URL = "https://ta.tokopedia.com/promo";
  private static final String EMPTY = "";
  private static final String DOT = ".";
  private static final String IDR_CURRENCY = "Rp";

  private TokopediaScrapperUtil() {
  }

  public static List<Product> findProducts(TokopediaProperties properties) {
    log.info("Start scrapping product from Tokopedia at {}", new Date());
    return Try.of(() -> {
      Playwright playwright = Playwright.create();
      Browser browser = playwright.chromium()
          .launch(new BrowserType.LaunchOptions().setHeadless(properties.isHeadless()));
      BrowserContext context = browser.newContext(new Browser.NewContextOptions()
          .setUserAgent(properties.getUserAgent())
          .setJavaScriptEnabled(true));
      Page page = context.newPage();
      List<Product> products = extractProducts(page, context, properties);
      log.info("Successfully extract {} products", products.size());

      page.close();
      browser.close();
      return products;
    }).recover(throwable -> {
      log.error("[findProducts.scrap]", throwable);
      return null;
    }).get();
  }

  private static List<Product> extractProducts(Page page, BrowserContext context,
      TokopediaProperties properties) {
    log.info("Extracting product from Tokopedia");
    List<Product> products = new ArrayList<>();
    return Try.of(() -> {
      int webPage = 1;
      while (products.size() < properties.getDataLimit()) {
        page.navigate(properties.getUrl() + "?page=" + webPage);
        Page.WaitForSelectorOptions selectorOptions = new Page.WaitForSelectorOptions()
            .setState(WaitForSelectorState.VISIBLE);
        loadAllData(page, selectorOptions);
        List<ElementHandle> elementHandles = page
            .querySelectorAll(TokopediaConstant.PRODUCT_ELEMENT);
        constructProductFromElementHandler(products, elementHandles, page, context,
            selectorOptions, properties.getDataLimit());
        webPage++;
      }
      return products;
    }).recover(throwable -> {
      log.error("Extract Product Error, product extracted {}", products.size(), throwable);
      return products;
    }).get();
  }

  private static void constructProductFromElementHandler(List<Product> products,
      List<ElementHandle> elementHandles, Page page, BrowserContext context,
      Page.WaitForSelectorOptions selectorOptions, int dataLimit) {
    log.info("Constructing product from Tokopedia");

    for (ElementHandle elementHandle : elementHandles) {
      ElementHandle handle = elementHandle.querySelector(TokopediaConstant.PRODUCT_CONTAINER);
      String url = handle.getAttribute("href");
      if (isUrlContainsPromo(url)) {
        url = cleanupUrl(url);
      }
      if (Objects.nonNull(url)) {
        Page productPage = context.newPage();
        productPage.navigate(url);
        productPage.waitForSelector(TokopediaConstant.WISHLIST_BUTTON, selectorOptions);
        String productName = getText(productPage, TokopediaConstant.PRODUCT_NAME);
        String description = getText(productPage, TokopediaConstant.PRODUCT_DESCRIPTION);
        String price = getText(productPage, TokopediaConstant.PRODUCT_PRICE);
        String rate = getText(productPage, TokopediaConstant.PRODUCT_RATE);
        String storeName = getText(productPage, TokopediaConstant.STORE_NAME);
        String imageUrl = page
            .getAttribute(TokopediaConstant.PRODUCT_IMAGE, TokopediaConstant.SOURCE_ATTRIBUTE);
        products.add(Product.builder()
            .productName(productName)
            .description(description)
            .imageUrl(imageUrl)
            .price(getPrice(price))
            .rate(Objects.nonNull(rate) ? Double.parseDouble(rate) : 0D)
            .storeName(storeName)
            .build());
        log.info("Products extracted: {}", products.size());
        productPage.close();
      }
      if (products.size() == dataLimit) {
        break;
      }
    }
  }

  private static void loadAllData(Page page, Page.WaitForSelectorOptions selectorOptions) {
    page.waitForSelector(TokopediaConstant.PRODUCT_LIST, selectorOptions);
    page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
    page.waitForSelector(TokopediaConstant.PAGINATION);
    page.evaluate("window.scrollTo(0, 0)");
  }

  private static String cleanupUrl(String path) {
    try {
      return URLDecoder.decode(path.substring(path.indexOf(PARAM_R) + 2).split(AMP)[0],
          StandardCharsets.UTF_8.name());
    } catch (Exception e) {
      return null;
    }
  }

  private static boolean isUrlContainsPromo(String path) {
    return path.contains(PROMO_URL);
  }

  private static String getText(Page page, String selector) {
    return Try.of(() -> page.innerText(selector))
        .recover(throwable -> {
          log.error("Get inner text error with selector {}", selector);
          return null;
        }).get();
  }

  private static BigDecimal getPrice(String priceStr) {
    if (Objects.nonNull(priceStr)) {
      return new BigDecimal(priceStr.split(IDR_CURRENCY)[1].replace(DOT, EMPTY));
    }
    return BigDecimal.ZERO;
  }
}
