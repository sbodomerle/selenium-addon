package org.vaadin.addons.javaee.selenium;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.javaee.selenium.input.InputMethod;
import org.vaadin.addons.javaee.selenium.input.InputMethodFactory;
import org.vaadin.addons.javaee.selenium.po.ConfirmDialogPO;

/**
 * Several methods to change values of fields in a form or to click on elements (buttons, tabs etc.).
 * 
 * @author Thomas Letsch (contact@thomas-letsch.de)
 * 
 */
public class SeleniumActions {

    private static final String TD = "]/td[";

    private static final String DIV_CONTAINS_CLASS_V_TABLE_BODY_TR = "']//div[contains(@class, 'v-table-body')]//tr[";

    private static final String DIV_ID = "//div[@id='";

    private static Logger log = LoggerFactory.getLogger(SeleniumActions.class);

    private WebDriver driver;

    private InputMethodFactory inputMethodFactory;

    public SeleniumActions(WebDriver driver) {
        this.driver = driver;
        inputMethodFactory = new InputMethodFactory(driver);
    }

    /**
     * Sets the value of the field with id "&lt;entityName&gt;.&lt;attribute&gt;" to the given text.
     */
    public void input(String entityName, String attribute, String text) {
        InputMethod inputMethod = inputMethodFactory.get(entityName, attribute);
        inputMethod.input(entityName, attribute, text);
    }

    /**
     * Clears the value of the field with id "&lt;entityName&gt;.&lt;attribute&gt;".
     */
    public void clearText(String entityName, String attribute) {
        String id = entityName + "." + attribute;
        WebElement element = driver.findElement(By.id(id));
        element.clear();
    }

    /**
     * Clicks the button with the given buttonName as id.
     */
    public void clickButton(String buttonName) {
        driver.findElement(By.id(buttonName)).click();
        WaitConditions.waitForVaadin(driver);
    }

    /**
     * Clicks the button located at the given table at the given row and column.
     */
    public void clickTableButton(String tableName, int row, int col) {
        String xpath = getCellItemXPath(tableName, row, col, "v-button");
        WebElement button = driver.findElement(By.xpath(xpath));
        button.click();
        WaitConditions.waitForVaadin(driver);
    }

    /**
     * Clicks the button located at the given table at the given row and column. It additionally expects it to be secured with a
     * ConfirmDiaĺog (https://vaadin.com/directory#addon/confirmdialog).
     */
    public void clickTableButtonWithConfirmation(String tableName, int row, int col) {
        clickTableButton(tableName, row, col);
        ConfirmDialogPO popUpWindowPO = new ConfirmDialogPO(driver);
        popUpWindowPO.clickOKButton();
        FluentWait<WebDriver> wait = new WebDriverWait(driver, WaitConditions.LONG_WAIT_SEC, WaitConditions.SHORT_SLEEP_MS)
                .ignoring(StaleElementReferenceException.class);
        wait.until(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                List<WebElement> findElements = driver.findElements(By.xpath("//div[contains(@class, 'v-window ')]"));
                return findElements.size() == 0;
            }
        });
        WaitConditions.waitForShortTime();
    }

    /**
     * Enables the tab with the given tab number.
     */
    public void clickTab(int tabNumber) {
        WebElement tab = driver.findElement(By.xpath("//div[contains(@class, 'v-tabsheet-tabcontainer')]/table/tbody/tr/td[" + tabNumber
                + "]/div/div"));
        tab.click();
        WaitConditions.waitForVaadin(driver);
    }

    /**
     * Exports the html code of the page to a file.
     */
    public void writePageSourceToFile() {
        try {
            File source = new File("target/pageSource.html");
            source.createNewFile();
            FileWriter fw = new FileWriter(source);
            fw.append(driver.getPageSource());
            fw.close();
        } catch (Exception e) {
            log.error("Could not save file", e);
        }
    }

    /**
     * Clicks the button located at the given table at the given row and column.
     * @param tableName The ID of the table element.
     * @param row The selected row index.
     * @param col The selected column index.
     * @param className CSS class of the clicked element.
     */
    public void clickTableCellItemByClassName(String tableName, int row, int col, String className) {
        final String xpath = getCellItemXPath(tableName, row, col, className);
        final WebElement button = driver.findElement(By.xpath(xpath));
        button.click();
        WaitConditions.waitForVaadin(driver);
    }

    /**
     * Clicks the button located at the given table at the given row and column.
     * @param tableName The ID of the table element.
     * @param row The selected row index.
     * @param col The selected column index.
     */
    public void clickTreeTableCell(String tableName, int row, int col) {
        final String xpath = DIV_ID + tableName + DIV_CONTAINS_CLASS_V_TABLE_BODY_TR + row + TD + col
                + "]//div[contains(@class, 'v-table-cell-wrapper')]//span[contains(@class, 'v-treetable-treespacer')]";
        final WebElement button = driver.findElement(By.xpath(xpath));
        button.click();
        WaitConditions.waitForVaadin(driver);
    }

    /**
     * Format an XPath to get a cell element.
     * @param tableName The ID of the table element.
     * @param row The selected row index.
     * @param col The selected column index.
     * @param itemClass The CSS class of the target element
     * @return Formatted XPath string
     */
    private String getCellItemXPath(String tableName, int row, int col, String itemClass) {
        return DIV_ID + tableName + DIV_CONTAINS_CLASS_V_TABLE_BODY_TR + row + TD + col
                + "]//div[contains(@class, '" + itemClass + "')]";
    }

}
