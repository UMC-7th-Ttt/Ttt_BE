package com.umc.ttt.domain.place.service.impl;

import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.place.repository.PlaceRepository;
import com.umc.ttt.domain.place.service.PlaceImageCrawlingService;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceImageCrawlingServiceImpl implements PlaceImageCrawlingService {

    private WebDriver driver;
    private WebDriverWait wait;
    private final PlaceRepository placeRepository;


    @Override
    public void crawlAndSaveImages() {
        try {
            WebDriverManager.chromedriver().setup();
            this.driver = new ChromeDriver();
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            List<Place> places = placeRepository.findAll();
//            List<Place> places = placeRepository.findAllByIdGreaterThanEqual(630L);

            for (Place place : places) {
                crawlPlaceInfo(place);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    public void crawlPlaceInfo(Place place) {
        try {
            // 네이버 지도 페이지 접속
            driver.get("https://map.naver.com");

            // 검색어 입력 및 검색 실행
            WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input.input_search")
            ));
            searchBox.sendKeys(place.getTitle());
            searchBox.sendKeys(Keys.ENTER);

            // iframe 로드 대기 및 전환
            switchToSearchIframe();

            // div.place_bluelink 요소가 있는지 확인
            List<WebElement> placeLinks = driver.findElements(By.cssSelector("div.place_bluelink"));

            // div.place_bluelink가 없다면 바로 다음 장소로 넘어가기
            if (placeLinks.isEmpty()) {
                System.out.println("검색 결과가 없습니다. 다음 장소를 검색합니다.");
                return;
            }

            // 페이지 로드 대기
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("div.place_bluelink > span.YwYLL")
            ));

            // 가게 목록 가져오기
            List<WebElement> shopLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("div.place_bluelink > span.YwYLL")
            ));

            processShop(shopLinks.get(0), place);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processShop(WebElement shop, Place place) throws InterruptedException {
        // 가게 상세 페이지로 이동
        clickElement(shop);

        while (!driver.getCurrentUrl().contains("/place/")) {
            clickElement(shop);
            Thread.sleep(1000);
        }

        // iframe 전환
        switchToEntryIframe();

        // 매장 정보 수집 후 저장
        savePlaceDetails(place);
    }

    private void switchToSearchIframe() {
        WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("iframe#searchIframe")));
        driver.switchTo().frame(iframe);
    }

    private void switchToEntryIframe() throws InterruptedException {
        while (true) {
            try {
                driver.switchTo().defaultContent();
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                        By.cssSelector("iframe#entryIframe")
                ));
                break;
            } catch (NoSuchFrameException e) {
                Thread.sleep(500);
            }
        }
    }

    private void clickElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void savePlaceDetails(Place place) {
        try {
            WebElement name = driver.findElement(By.cssSelector("span.GHAhO"));
            String nameText = name.getText();

            String mainImg = null;
            try {
                mainImg = driver.findElement(By.cssSelector(".fNygA > a > img")).getDomAttribute("src");
            } catch (NoSuchElementException e) {
                mainImg = null;
            }

            if(mainImg != null) {
                place.updateImage(mainImg);
            }
            placeRepository.save(place);

            System.out.println("1. 매장 이름: " + nameText);
            System.out.println("2. 이미지 URL: " + mainImg);
        } catch (NoSuchElementException e) {
            System.out.println("매장 정보를 가져오는 데 실패했습니다.");
        }
    }

}